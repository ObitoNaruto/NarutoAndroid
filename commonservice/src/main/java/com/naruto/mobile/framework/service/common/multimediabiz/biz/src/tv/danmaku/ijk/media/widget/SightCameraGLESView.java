package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.tv.danmaku.ijk.media.widget;

import java.io.IOException;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.util.AttributeSet;
import android.util.Log;

import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.tv.danmaku.ijk.media.encode.AVRecorder;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.tv.danmaku.ijk.media.encode.SessionConfig;

public class SightCameraGLESView extends CameraView
{
	private AVRecorder mRecorder;
    private SessionConfig mSessionConfig;
    private SurfaceTexture mSurfaceTexture;
	public SightCameraGLESView(Context context)
	{
		super(context);
	}
	public SightCameraGLESView(Context context, AttributeSet set)
	{
		super(context, set);
	}
	public SightCameraGLESView(Context context, AttributeSet set, int defaultStyle)
	{
		super(context, set, defaultStyle);
	}
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int w, int h)
    {
    	mSurfaceTexture = surfaceTexture;
		try {
			initCamera();
		} catch (Exception e) {
			notifyCameraError();
			return;
		}

    	mSessionConfig = new SessionConfig();
    	try {
    		mRecorder = new AVRecorder(mSessionConfig);
			mRecorder.setRecordListener(mListener);
		} catch (Exception e) {
			Log.e(TAG, "onSurfaceTextureAvailable:" + e.getMessage());
			notifyMicError();
			return;
		}
    	mRecorder.setPreviewDisplay(this, surfaceTexture);
    	mRecorder.setCamera(mCamera);
		notifyPrepared();
	}

	@Override
    public void onWindowFocusChanged(boolean hasWindowFocus)
    {
    	super.onWindowFocusChanged(hasWindowFocus);
    	Log.d(TAG, "onWindowFocusChanged hasWindowFocus: " + hasWindowFocus);
    	if (!hasWindowFocus)
    	{
    		//stopRecord();
    	}
    }

    @Override
	protected void onDetachedFromWindow()
    {
		super.onDetachedFromWindow();
		stopRecord(true);
	}
	public void startRecord()
    {
		if (mRecorder != null && !mRecorder.isRecording())
			mRecorder.startRecording();
    }
    public void stopRecord(boolean release)
    {
    	if (mRecorder != null)
    		mRecorder.stopRecording();
    	if (release)
    	{
    		releaseCamera();
    	}
    }
    public void setup()
    {
    	mSessionConfig = new SessionConfig();
    	try {
    		mRecorder = new AVRecorder(mSessionConfig);
    		mRecorder.setRecordListener(mListener);
		} catch (Exception e) {
			notifyMicError();
			return ;
		}
		mRecorder.setPreviewDisplay(this, mSurfaceTexture);
		mRecorder.setCamera(mCamera);
    }
//    public SessionConfig getConfig()
//    {
//    	return mSessionConfig;
//    }
    @Override
    public String getOutputPath()
    {
    	return mSessionConfig.getOutputFile().getAbsolutePath();
    }
    public Camera switchCamera()
    {
    	stopRecord(true);
        if (mCameraFacing == CameraInfo.CAMERA_FACING_BACK)
        {
        	mCameraFacing = CameraInfo.CAMERA_FACING_FRONT;
        }
        else
        {
        	mCameraFacing = CameraInfo.CAMERA_FACING_BACK;
		}
		try {
			initCamera();
		} catch (Exception e) {
			notifyCameraError();
			return null;
		}
    	mSessionConfig = new SessionConfig();
    	try {
    		mRecorder = new AVRecorder(mSessionConfig);
    		mRecorder.setRecordListener(mListener);
		} catch (Exception e) {
			notifyMicError();
			return null;
		}
		mRecorder.setPreviewDisplay(this, mSurfaceTexture);
		mRecorder.setCamera(mCamera);
        return mCamera;
    }
    public Camera reopenCamera(int mode)
    {
    	Log.d(TAG, "reopenCamera: " + mode);
    	mMode = mode;

    	stopRecord(true);
    	//releaseCamera();
		try {
			initCamera();
		} catch (Exception e) {
			notifyCameraError();
			return null;
		}

    	mSessionConfig = new SessionConfig();
    	try {
    		mRecorder = new AVRecorder(mSessionConfig);
    		mRecorder.setRecordListener(mListener);
		} catch (Exception e) {
			notifyMicError();
			return null;
		}
		mRecorder.setPreviewDisplay(this, mSurfaceTexture);
		mRecorder.setCamera(mCamera);
    	return mCamera;
    }
	    @Override
	    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int w, int h)
	    {
	    	Log.d(TAG, this + "###onSurfaceTextureSizeChanged");
	    }
	
	    @Override
	    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture)
	    {
	    	Log.d(TAG, this + "###onSurfaceTextureDestroyed");
	    	return true;
	    }
	
	    @Override
	    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture)
	    {
	    	//Log.d(TAG, "###onSurfaceTextureUpdated");
	    }
}
