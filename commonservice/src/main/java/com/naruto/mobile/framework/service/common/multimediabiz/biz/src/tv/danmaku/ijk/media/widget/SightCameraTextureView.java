package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.tv.danmaku.ijk.media.widget;

import java.io.IOException;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;

import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.tv.danmaku.ijk.media.encode.FFmpegCameraEncoder;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.tv.danmaku.ijk.media.encode.FFmpegMicEncoder;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.tv.danmaku.ijk.media.encode.FFmpegSessionConfig;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SightCameraTextureView extends CameraView
{
    private FFmpegCameraEncoder mCameraEncoder;
    private FFmpegMicEncoder mMicEncoder;
    private FFmpegSessionConfig mSessionConfig;
    private SurfaceTexture mSurfaceTexture;
	public SightCameraTextureView(Context context)
	{
		super(context);
	}
	public SightCameraTextureView(Context context, AttributeSet set)
	{
		super(context, set);
	}
	public SightCameraTextureView(Context context, AttributeSet set, int defaultStyle)
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
		mCamera.startPreview();
    	try {
        	mCamera.setPreviewTexture(mSurfaceTexture);
		} catch (IOException e) {
		}
    	mSessionConfig = FFmpegSessionConfig.create();
    	mCameraEncoder = new FFmpegCameraEncoder(mCamera, mSessionConfig);
		try {
			mMicEncoder = new FFmpegMicEncoder(mSessionConfig);
		} catch (Exception e) {
			notifyMicError();
			return;
		}

    	notifyPrepared();
    }
    public Camera switchCamera()
    {
    	releaseCamera();
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
        mCamera.startPreview();
    	try {
        	mCamera.setPreviewTexture(mSurfaceTexture);
		} catch (IOException e) {
		}
    	mCameraEncoder.switchCamera(mCamera, mCameraFacing);
    	return mCamera;
    }
    public Camera reopenCamera(int mode)
    {
    	mMode = mode;
    	releaseCamera();
		try {
			initCamera();
		} catch (Exception e) {
			notifyCameraError();
			return null;
		}
    	mCameraEncoder.switchCamera(mCamera, mCameraFacing);
        mCamera.startPreview();
    	try {
        	mCamera.setPreviewTexture(mSurfaceTexture);
		} catch (IOException e) {
		}
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
		if (!isRecording())
		{
			mCameraEncoder.start();
			mMicEncoder.startRecording();
		}
    }
	public boolean isRecording()
	{
		return mCameraEncoder.isRecording();
	}

    public void stopRecord(boolean release)
    {
    	mMicEncoder.stop();
		mCameraEncoder.stop();
		if (release)
		{
			releaseCamera();
		}
    }
    public void setup()
    {
		try {
			mMicEncoder = new FFmpegMicEncoder(mSessionConfig);
		} catch (Exception e) {
			notifyMicError();
			return;
		}
		//camera encoder needn't setup again.
    }
    @Override
    public String getOutputPath()
    {
    	return mSessionConfig.vPublishUrl;
    }
}
