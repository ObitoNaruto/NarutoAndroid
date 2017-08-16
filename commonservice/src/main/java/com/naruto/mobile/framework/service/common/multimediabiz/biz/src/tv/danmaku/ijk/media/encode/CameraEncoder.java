package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.tv.danmaku.ijk.media.encode;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;

import javax.crypto.Mac;

import android.annotation.TargetApi;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES20;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Surface;

import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.tv.danmaku.ijk.media.gles.EglCore;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.tv.danmaku.ijk.media.gles.FullFrameRect;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.tv.danmaku.ijk.media.gles.Texture2dProgram;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.tv.danmaku.ijk.media.gles.WindowSurface;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.tv.danmaku.ijk.media.widget.CameraView;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class CameraEncoder implements SurfaceTexture.OnFrameAvailableListener
{
	private static final String TAG = "CameraEncoder";
	private HandlerThread mThread;
	private Handler mHandler;
	private CameraView mDisplayView;
    private static final int MSG_FRAME_AVAILABLE = 1;
    private static final int MSG_SET_SURFACE_TEXTURE = 2;
    private static final int MSG_RELEASE = 3;
    private EglCore mEglCore;
    private WindowSurface mDisplaySurface;
    private SurfaceTexture mCameraTexture;  // receives the output from the camera preview
    private FullFrameRect mFullFrameBlit;
    private final float[] mTmpMatrix = new float[16];
    private int mTextureId;
    
    private WindowSurface mEncoderSurface;
    private Camera mCamera;

    private volatile boolean mIsRecording;
    private volatile boolean mThumbRequest = true;

    private long mFirstTs = 0;
    private long mLastTs = 0;
    private SessionConfig mConfig;
    private VideoEncoderCore mVideoEncoder;
    private boolean mEosRequested = false;
    
    public CameraEncoder(SessionConfig config)
    {
    	mThread = new HandlerThread("CameraEncoder");
    	mThread.start();
    	mHandler = new EncoderHandler(this, mThread.getLooper());
    	mConfig = config;
    }

    public void setPreviewDisplay(CameraView display)
    {
    	mDisplayView = display;
    }

    public void setCamera(Camera camera)
    {
    	mCamera = camera;
    }
    
    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture)
    {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_FRAME_AVAILABLE));
    }
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture)
    {
    	mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_SURFACE_TEXTURE, surfaceTexture));
    }
    
    public void release()
    {
    	mHandler.sendMessage(mHandler.obtainMessage(MSG_RELEASE));
    }
    
    private void prepareEncoder(int width, int height, int bitRate, AndroidMuxer muxer) throws IOException
    {
		mVideoEncoder = new VideoEncoderCore(width, height, bitRate, muxer);
		mEncoderSurface = new WindowSurface(mEglCore, mVideoEncoder.getInputSurface(), true);
    }
    
    private void handleSetSurfaceTexture(SurfaceTexture surfaceTexture) throws IOException
    {
        mEglCore = new EglCore(null, EglCore.FLAG_RECORDABLE);
        mDisplaySurface = new WindowSurface(mEglCore, new Surface(surfaceTexture), false);
        mDisplaySurface.makeCurrent();
        mFullFrameBlit = new FullFrameRect(new Texture2dProgram(Texture2dProgram.ProgramType.TEXTURE_EXT));
        mTextureId = mFullFrameBlit.createTextureObject();
        mCameraTexture = new SurfaceTexture(mTextureId);
        mCameraTexture.setOnFrameAvailableListener(this);
        //stopPreview is needed for some devices
        if (previewRunning(mCamera))
        {
        	Log.d(TAG, "preview is running, stop it.");
        	mCamera.stopPreview();
        }
        Log.d(TAG, "starting camera preview");
        mCamera.setPreviewTexture(mCameraTexture);
        mCamera.startPreview();
        prepareEncoder(mConfig.getVideoWidth(), mConfig.getVideoHeight(), mConfig.getVideoBitrate(), mConfig.getMuxer());
    }
    private boolean previewRunning(Camera camera)
    {
    	boolean enable = true;
    	try {
    		Class clazz = camera.getClass();
    		Method method = clazz.getDeclaredMethod("previewEnabled");
    		enable = (Boolean)method.invoke(camera);
		} catch (Exception e) {
			Log.e(TAG, "previewEnabled exception:" + e.getMessage());
		}
    	return enable;
    }
    private void handleFrameAvailable()
    {
        if (mEglCore == null) {
            Log.d(TAG, "Skipping drawFrame after shutdown");
            return;
        }
        // Latch the next frame from the camera.
        mDisplaySurface.makeCurrent();
        mCameraTexture.updateTexImage();
        mCameraTexture.getTransformMatrix(mTmpMatrix);

        int viewWidth = mDisplayView.getWidth();
        int viewHeight = mDisplayView.getHeight();
        GLES20.glViewport(0, 0, viewWidth, viewHeight);
        mFullFrameBlit.drawFrame(mTextureId, mTmpMatrix);
        mDisplaySurface.swapBuffers();

        if (mIsRecording)
        {
        	long pts = mCameraTexture.getTimestamp();
        	if (mFirstTs == 0)
        	{
        		mFirstTs = pts / 1000;
        	}
        	else
        	{
        		long abs = pts / 1000 - mFirstTs;
        		if (abs - mLastTs < 52632)
        			return;
        		else
        			mLastTs += 52632;
			}
        	mVideoEncoder.drainEncoder(false);
	        mEncoderSurface.makeCurrent();
	        GLES20.glViewport(0, 0, mConfig.getVideoWidth(), mConfig.getVideoHeight());
	        //mFullFrameBlit.drawCroppedFrame(mTextureId, mTmpMatrix, mCamera.getParameters().getPreviewSize());
	        mFullFrameBlit.drawFrame(mTextureId, mTmpMatrix);
	        if (mThumbRequest)
	        {
	        	saveFrameAsImage();
	        	mThumbRequest = false;
	        }
	        mEncoderSurface.setPresentationTime(pts);
	        mEncoderSurface.swapBuffers();
            if (mEosRequested) {
                /*if (VERBOSE) */
                Log.i(TAG, "Sending last video frame. Draining encoder");
                mVideoEncoder.signalEndOfStream();
                mVideoEncoder.drainEncoder(true);
                mIsRecording = false;
                mEosRequested = false;
                release();
            }
        }
    }
    private void saveFrameAsImage() {
        try {
            String path = mConfig.getOutputFile().getAbsolutePath();
            File imageFile = new File(path.substring(0, path.indexOf('.')) + ".jpg");
            mEncoderSurface.saveFrame(imageFile);
        } catch (IOException e) {
        }
    }
    private void releaseEncoder() {
        mVideoEncoder.release();
        mConfig.getMuxer().clean();
    }
    private void handleRelease()
    {
    	//mDisplayView.releaseCamera();
    	releaseEncoder();
        if (mCameraTexture != null) {
            mCameraTexture.release();
            mCameraTexture = null;
        }
        if (mDisplaySurface != null) {
            mDisplaySurface.release();
            mDisplaySurface = null;
        }
        if (mFullFrameBlit != null) {
            mFullFrameBlit.release(false);
            mFullFrameBlit = null;
        }
        if (mEglCore != null) {
            mEglCore.release();
            mEglCore = null;
        }
        try {
        	mThread.getLooper().quit();
		} catch (Exception e) {
		}
    }
    public void startRecording()
    {
    	if (mIsRecording == true)
    		return;
    	mIsRecording = true;
    	mThumbRequest = true;
    }
    public void stopRecording()
    {
    	mEosRequested = true;
    	if (!mIsRecording)
    	{
    		release();
    	}
    	try {
    		mThread.join();
		} catch (Exception e) {
		}
    }

    private static class EncoderHandler extends Handler {
        private WeakReference<CameraEncoder> mWeakEncoder;

        public EncoderHandler(CameraEncoder encoder, Looper looper) {
            super(looper);
            mWeakEncoder = new WeakReference<CameraEncoder>(encoder);
        }

        @Override
        public void handleMessage(Message inputMessage) {
            int what = inputMessage.what;
            Object obj = inputMessage.obj;

            CameraEncoder encoder = mWeakEncoder.get();
            if (encoder == null) {
                Log.w(TAG, "EncoderHandler.handleMessage: encoder is null");
                return;
            }

            try {
                switch (what) {
                    case MSG_SET_SURFACE_TEXTURE:
                        encoder.handleSetSurfaceTexture((SurfaceTexture)obj);
                        break;
                    case MSG_FRAME_AVAILABLE:
                        encoder.handleFrameAvailable();
                        break;
                    case MSG_RELEASE:
                        encoder.handleRelease();
                        break;
                    default:
                        throw new RuntimeException("Unexpected msg what=" + what);
                }
            } catch (IOException e) {
            }
        }
    }
}
