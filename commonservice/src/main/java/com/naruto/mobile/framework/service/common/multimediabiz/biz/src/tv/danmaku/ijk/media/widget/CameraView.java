package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.tv.danmaku.ijk.media.widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Size;
import android.os.Build;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;

import com.naruto.mobile.framework.service.common.multimedia.video.data.APVideoRecordRsp;
import com.naruto.mobile.framework.service.common.multimedia.widget.SightCameraView;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Logger;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public abstract class CameraView extends TextureView
					implements TextureView.SurfaceTextureListener, Camera.AutoFocusCallback
{
	public final String TAG = "CameraView";
    private Logger logger = Logger.getLogger(TAG);
	private String mOutputPath;
	protected Camera mCamera;
	protected int mCameraFacing = CameraInfo.CAMERA_FACING_BACK;
	protected int mMode = SightCameraView.MODE_VIDEO;
	protected Size mPreviewSize;
	private Context mContext;
	private static final String VIDEO_PATH =  Environment.getExternalStorageDirectory().getAbsolutePath() + "/ShortVideo/";
	private boolean mFocusAreaSupported;
	private boolean mMeteringAreaSupported;
	protected SightCameraView.OnRecordListener mListener;
	public void setOnRecordListener(SightCameraView.OnRecordListener listener)
	{
		mListener = listener;
	}
	
	public CameraView(Context context)
	{
		super(context);
		mContext = context;
		setSurfaceTextureListener(this);
		logger.d("CameraView construct!");
	}
	public CameraView(Context context, AttributeSet set)
	{
		super(context, set);
		mContext = context;
		setSurfaceTextureListener(this);
	}
	public CameraView(Context context, AttributeSet set, int defaultStyle)
	{
		super(context, set, defaultStyle);
		mContext = context;
		setSurfaceTextureListener(this);
	}

	protected abstract String getOutputPath();
	protected abstract Camera switchCamera();
	protected abstract Camera reopenCamera(int mode);
    protected abstract void startRecord();
    protected abstract void stopRecord(boolean release);
    protected abstract void setup();
	protected void initCamera()
	{
		long ts = System.currentTimeMillis();
		logger.d("initCamera begin");
		CameraInfo cameraInfo = new CameraInfo();
		int numcameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numcameras; i++)
        {
            Camera.getCameraInfo(i, cameraInfo);
            Log.i(TAG, "try camera: " + i + " " + cameraInfo.facing);
            if (cameraInfo.facing == mCameraFacing)
            {
                mCamera = Camera.open(i);
                if (mCamera == null) {
                    throw new RuntimeException("open camera error");
                }
                mCameraFacing = cameraInfo.facing;
                break;
            }
        }
        
        Camera.Parameters parameters = mCamera.getParameters();

        List<String> supportedFlashModes = parameters.getSupportedFlashModes();
        if (supportedFlashModes != null && supportedFlashModes.contains(Camera.Parameters.FLASH_MODE_AUTO)) {
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
        }
 
        if (parameters.getMaxNumFocusAreas() > 0) {
            mFocusAreaSupported = true;
        }
 
        if (parameters.getMaxNumMeteringAreas() > 0) {
            mMeteringAreaSupported = true;
        }
        
        if (mMode == SightCameraView.MODE_VIDEO)
        {
        	chooseVideoPreviewSize(parameters);
        }
        else
        {
        	choosePhotoPreviewSize(parameters);
		}
		if (mPreviewSize == null)
		{
			mPreviewSize = parameters.getPreferredPreviewSizeForVideo();
		}
		parameters.setPreviewFormat(ImageFormat.NV21);
		
        List<String> focusMode = parameters.getSupportedFocusModes();
        for (int i = 0; i < focusMode.size(); i++)
        {
        	logger.d("camera foucus mode: " + focusMode.get(i));
        }

        if (focusMode.contains("continuous-video") && mMode == SightCameraView.MODE_VIDEO) {
        	parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        } else if (focusMode.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
        	parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }
        
        List<int[]> fpsRange = parameters.getSupportedPreviewFpsRange();
        int minfps = 0;
        for (int i = 0; i < fpsRange.size(); i++) {
            int[] r = fpsRange.get(i);
            logger.d("camera fpsRange " + " range0(min):" + r[0] + " range1(max):" + r[1]);
            if (minfps == 0 || r[0] < minfps)
            {
                minfps = r[0];
                parameters.setPreviewFpsRange(r[0], r[1]);
            }
        }

        logger.d("camera current scene mode : " + parameters.getSceneMode());
        List<String> sceneModes = parameters.getSupportedSceneModes();
        if (sceneModes != null)
        {
            for (int i = 0; i < sceneModes.size(); i++)
            {
                logger.d("camera scene mode : " + sceneModes.get(i));
            }
        }
        //parameters.setRecordingHint(true); //this api is a bug
        setCameraDisplayOrientation((Activity)mContext, cameraInfo.facing);
        mCamera.setParameters(parameters);
        if (getParent() instanceof SightCameraViewImpl)
        	reLayout();
        logger.d("init camera took " + (System.currentTimeMillis() - ts) + "ms");
	}
	
    private void setCameraDisplayOrientation(Activity activity, int cameraId) {
        CameraInfo info = new CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation)
        {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }
    
        int result;
        if (info.facing == CameraInfo.CAMERA_FACING_FRONT)
        {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } 
        else
        {
            result = (info.orientation - degrees + 360) % 360;
        }
        mCamera.setDisplayOrientation(result);
        logger.d("current camera orientation:" + info.orientation);
    }
    public void releaseCamera()
    {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.setPreviewCallbackWithBuffer(null);
            mCamera.release();
            mCamera = null;
            logger.d("releaseCamera -- done");
        }
    }
    private void reLayout()
    {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
//        ViewGroup.LayoutParams layoutParams = new FrameLayout.LayoutParams(displayMetrics.widthPixels, 
//        		displayMetrics.widthPixels * mPreviewSize.width / mPreviewSize.height);
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.width = displayMetrics.widthPixels;
        layoutParams.height = displayMetrics.widthPixels * mPreviewSize.width / mPreviewSize.height;
        setLayoutParams(layoutParams);
        requestLayout();
    }

    protected void focusOnTouch(MotionEvent event)
    {
        if (mCamera != null) {
            //cancel previous actions
            mCamera.cancelAutoFocus();
            Rect focusRect = calculateTapArea(event.getX(), event.getY(), 1f);
            Rect meteringRect = calculateTapArea(event.getX(), event.getY(), 1.5f);
 
            Camera.Parameters parameters = null;
            try {
                parameters = mCamera.getParameters();
            } catch (Exception e) {
            	Log.e(TAG, e.getMessage());
            }
 
            // check if parameters are set (handle RuntimeException: getParameters failed (empty parameters))
            if (parameters != null)
            {
            	if (mFocusAreaSupported)
            	{
	                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
	                ArrayList list = new ArrayList(1);
	                list.add(new Camera.Area(focusRect, 1000));
	                parameters.setFocusAreas(list);
            	}
                if (mMeteringAreaSupported)
                {
                	ArrayList list = new ArrayList(1);
                	list.add(new Camera.Area(meteringRect, 1000));
                    parameters.setMeteringAreas(list);
                }
 
                try {
                    mCamera.setParameters(parameters);
                    mCamera.autoFocus(this);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        }
    }

    private Rect calculateTapArea(float x, float y, float coefficient) {
		final float scale = getContext().getResources().getDisplayMetrics().density;
		int focusAreaSize =  (int)(72 * scale + 0.5f);
        int areaSize = Float.valueOf(focusAreaSize * coefficient).intValue();
        
        int centerX = (int) (x / getWidth() * 2000 - 1000);  
        int centerY = (int) (y / getHeight() * 2000 - 1000);

        int left = clamp((int) centerX - areaSize / 2, -1000, 1000);
        int top = clamp((int) centerY - areaSize / 2, -1000, 1000);
        int right = clamp((int) centerX + areaSize / 2, -1000, 1000);
        int bottom = clamp((int) centerY + areaSize / 2, -1000, 1000);
        //RectF rectF = new RectF(left, top, left + areaSize, top + areaSize);
        //mMatrix.mapRect(rectF);
 
        //return new Rect(Math.round(rectF.left), Math.round(rectF.top), Math.round(rectF.right), Math.round(rectF.bottom));
        return new Rect(left, top, right, bottom);
    }
 
    private int clamp(int x, int min, int max) {
        if (x > max) {
            return max;
        }
        if (x < min) {
            return min;
        }
        return x;
    }
    public void onAutoFocus(boolean success, Camera camera)
    {
    	logger.d("onAutoFocus result: " + success);
    }
    protected void zoom()
    {
    	if (mCamera != null)
    	{
            Camera.Parameters parameters = null;
            try {
                parameters = mCamera.getParameters();
            } catch (Exception e) {
            	Log.e(TAG, e.getMessage());
            }
            boolean zoom = parameters.isZoomSupported();
            if (zoom)
            {
            	int max = parameters.getMaxZoom();
            	int curr = parameters.getZoom();
            	logger.d("curr: " + curr);
            	if (curr == 0)
            	{
            		parameters.setZoom(max / 2);
            	}
            	else
            	{
            		parameters.setZoom(0);
				}
            	mCamera.setParameters(parameters);
            }
    	}
    }
    private void chooseVideoPreviewSize(Camera.Parameters parameters)
    {
        List<Size> previewSizes = mCamera.getParameters().getSupportedPreviewSizes();
        Collections.sort(previewSizes, new Comparator<Size>() {
					    				@Override
					    				public int compare(Size a, Size b) {
					    					return a.height - b.height;
					    				}
					    			});
		for (int i = 0; i < previewSizes.size(); i++)
		{
			Size previewSize = previewSizes.get(i);
		    logger.d("camera preview size " + " width:" + previewSize.width + " height:" + previewSize.height);
		    if (previewSize.height >= 360 && previewSize.width * 9 / 16 == previewSize.height)
		    {
		    	parameters.setPreviewSize(previewSize.width, previewSize.height);
		    	mPreviewSize = previewSize;
		    	break;
		    }
		}
    }
    private void choosePhotoPreviewSize(Camera.Parameters parameters)
    {
        List<Size> previewSizes = mCamera.getParameters().getSupportedPreviewSizes();
        Collections.sort(previewSizes, new Comparator<Size>() {
					    				@Override
					    				public int compare(Size a, Size b) {
					    					return b.height - a.height;
					    				}
					    			});
		for (int i = 0; i < previewSizes.size(); i++)
		{
			Size previewSize = previewSizes.get(i);
		    logger.d("camera preview size " + " width:" + previewSize.width + " height:" + previewSize.height);
		    if (previewSize.width * 9 / 16 == previewSize.height)
		    {
		    	parameters.setPreviewSize(previewSize.width, previewSize.height);
		    	mPreviewSize = previewSize;
		    	break;
		    }
		}
    }

    //回调相关
    protected void notifyCameraError() {
        APVideoRecordRsp rsp = new APVideoRecordRsp();
        rsp.mRspCode = APVideoRecordRsp.CODE_ERR_CAMERA_OPEN;
        notifyError(rsp);
    }

    protected void notifyError(APVideoRecordRsp rsp) {
        logger.e("notifyError, rsp: " + rsp);
        if (mListener != null) {
            mListener.onError(rsp);
        }
    }

    protected void notifyPrepared() {
        logger.p("notifyPrepared");
        if (mListener != null) {
            mListener.onPrepared();
        }
    }

    protected void notifyMicError() {
        APVideoRecordRsp rsp = new APVideoRecordRsp();
        rsp.mRspCode = APVideoRecordRsp.CODE_ERR_MIC_PERMISSION_DENIED;
        notifyError(rsp);
    }
}
