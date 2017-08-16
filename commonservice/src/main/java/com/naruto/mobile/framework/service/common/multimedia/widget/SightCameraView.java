package com.naruto.mobile.framework.service.common.multimedia.widget;


import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.naruto.mobile.framework.service.common.multimedia.video.data.APVideoRecordRsp;


public class SightCameraView extends FrameLayout
{
	public static final int MODE_VIDEO = 0;
	public static final int MODE_PHOTO = 1;
    public SightCameraView(Context context)
    {
        super(context);
    }

    public void startRecord()
    {
    	//mCameraView.startRecord();
    }
    public void stopRecord()
    {
    	//mCameraView.stopRecord();
    }
    
    public void cancelRecord()
    {
    	//mCameraView.stopRecord();
    }
    public Camera switchCamera()
    {
    	return null;
    }
    public Camera reopenCamera(int mode)
    {
    	return null;
    }
    public void setOnRecordListener(OnRecordListener listener)
    {
    	Log.d("ylf", "shell setOnRecordListener");
    }
    public void setOnScrollListener(OnScrollListener listener)
    {
    	Log.d("ylf", "shell setOnScrollListener");
    }
    public static interface OnRecordListener
    {
    	public void onStart();
    	public void onError(APVideoRecordRsp rsp);
    	public void onFinish(APVideoRecordRsp rsp);
    	public void onCancel();
    	public void onPrepared();
    }
    public static interface OnScrollListener
    {
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                float distanceX, float distanceY);
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                float velocityY);
    }
}
