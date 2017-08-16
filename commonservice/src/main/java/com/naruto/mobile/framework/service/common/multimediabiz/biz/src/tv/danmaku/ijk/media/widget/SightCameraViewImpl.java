package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.tv.danmaku.ijk.media.widget;

import java.io.File;


import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.Camera;
import android.os.Build;
import android.support.v4.view.GestureDetectorCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.FrameLayout;

import com.naruto.mobile.framework.service.common.multimedia.video.data.APVideoRecordRsp;
import com.naruto.mobile.framework.service.common.multimedia.widget.SightCameraView;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.video.VideoFileManager;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class SightCameraViewImpl extends SightCameraView
{
	private static final String TAG = "SightCameraViewImpl";
	private CameraView mCameraView;
	private CameraFrontSightView mFrontSightView;
	private GestureDetectorCompat mDetector;
	private int mOffset = 0;
	private OnRecordListener mListener;
	private OnScrollListener mScrollListener;
	
    public SightCameraViewImpl(Context context)
    {
        super(context);
    }

    public void setOnRecordListener(OnRecordListener listener)
    {
    	mListener = listener;
    }
	
	private void init()
    {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
		{
			mCameraView = new SightCameraGLESView(getContext());
		}
		else
		{
			mCameraView = new SightCameraTextureView(getContext());
		}
		mCameraView.setOnRecordListener(mListener);
    	addView(mCameraView, 0);
    	getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				mOffset = (mCameraView.getHeight() - SightCameraViewImpl.this.getHeight()) / 2;
				Log.d(TAG, "offset: " + mOffset);
				if (mOffset != 0)
				{
					scrollTo(0, mOffset);
				}
			}
		});
    	GestureListener listener = new GestureListener();
    	mDetector = new GestureDetectorCompat(getContext(), listener);
    	mFrontSightView = new CameraFrontSightView(getContext());
    	addView(mFrontSightView, 1);
    	mFrontSightView.init(120, 120);
    }
    
    protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        layoutParams.width = displayMetrics.widthPixels;
        layoutParams.height = displayMetrics.widthPixels * 16 / 9;
        setLayoutParams(layoutParams);
        init();
    }
    public void startRecord()
    {
    	mCameraView.startRecord();
    	if (mListener != null)
    		mListener.onStart();
    }
    public void stopRecord()
    {
    	Log.d(TAG, "stopRecord");
    	if (mCameraView == null)
    	{
    		return;
    	}
    	mCameraView.stopRecord(true);
    	String path = getOutputPath();
    	
    	String id = path.substring(path.lastIndexOf('/') + 1, path.lastIndexOf('.'));
    	
    	//write db
    	VideoFileManager.getInstance().insertRecord(path, "", id, VideoFileManager.TYPE_RECORD);
    	VideoFileManager.getInstance().insertRecord(path.substring(0, path.lastIndexOf('.')) + ".jpg", "", id, VideoFileManager.TYPE_RECORD);
    	
    	APVideoRecordRsp rsp = new APVideoRecordRsp();
    	rsp.mId = id;
    	rsp.mRspCode = 0;
    	rsp.mWidth = 320;
    	rsp.mHeight = 568;
    	if (mListener != null)
    		mListener.onFinish(rsp);
    }
    public void cancelRecord()
    {
    	Log.d(TAG, "cancelRecord");
    	mCameraView.stopRecord(false);
    	mCameraView.setup();
    	String path = getOutputPath();
    	File v = new File(path);
    	if (v.exists())
    	{
    		v.delete();
    	}
    	File t = new File(path.substring(0, path.lastIndexOf('.')) + ".jpg");
    	if (t.exists())
    	{
    		t.delete();
    	}
    	if (mListener != null)
    		mListener.onCancel();
    }
    public String getOutputPath()
    {
    	return mCameraView.getOutputPath();
    }
    private class GestureListener extends SimpleOnGestureListener
    {
        public boolean onDown(MotionEvent e) {
        	Log.d(TAG, "onDown");
        	e.offsetLocation(0, mOffset);
        	drawFocusArea(e.getX(), e.getY());
        	
        	mCameraView.focusOnTouch(e);
            return true;
        }

        public boolean onDoubleTap(MotionEvent e) {
        	Log.d(TAG, "onDoubleTap");
        	mCameraView.zoom();
            return false;
        }
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                float distanceX, float distanceY) {
        	mScrollListener.onScroll(e1, e2, distanceX, distanceY);
            return false;
        }

        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                float velocityY) {
        	mScrollListener.onFling(e1, e2, velocityX, velocityY);
            return false;
        }

    }
    private void drawFocusArea(float x, float y)
    {
    	if (mFrontSightView == null)
    	{
    		return;
    	}
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams)mFrontSightView.getLayoutParams();
        layoutParams.leftMargin = ((int)x - mFrontSightView.mWidth / 2);
        layoutParams.topMargin = ((int)y - mFrontSightView.mHeight / 2);
        mFrontSightView.setLayoutParams(layoutParams);
        mFrontSightView.startDraw();
        mFrontSightView.requestLayout();
    }

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return mDetector.onTouchEvent(event);
	}
	public Camera switchCamera()
	{
		return mCameraView.switchCamera();
	}
	public Camera reopenCamera(int mode)
	{
		return mCameraView.reopenCamera(mode);
	}
    public void setOnScrollListener(OnScrollListener listener)
    {
    	mScrollListener = listener;
    }
}
