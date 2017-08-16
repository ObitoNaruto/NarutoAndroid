package com.naruto.mobile.framework.service.common.multimedia.widget;

//import com.alipay.android.phone.mobilecommon.multimedia.api.MultimediaVideoService;
//import com.alipay.mobile.framework.LauncherApplicationAgent;
//import com.alipay.mobile.framework.app.ui.BaseActivity;
//import com.alipay.mobile.framework.app.ui.BaseFragmentActivity;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.naruto.mobile.base.serviceaop.NarutoApplication;
import com.naruto.mobile.framework.service.common.multimedia.api.MultimediaVideoService;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class SightVideoPlayView extends FrameLayout
{
	private static final String TAG = "VideoPlayView";
	private MultimediaVideoService mVideoService = null;
	private SightPlayView mPlayView = null;
	private SightPlaySurfaceView mPlaySurfaceView = null;
	private Context mContext;
	
    public SightVideoPlayView(Context context)
    {
        super(context);
        init(context);
    }

    public SightVideoPlayView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }

    public SightVideoPlayView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init(context);
    }
    private void init(Context context)
    {
    	mContext = context;

    	mVideoService = NarutoApplication.getInstance().getNarutoApplicationContext().getExtServiceByInterface(MultimediaVideoService.class.getName());
        ViewGroup.LayoutParams layoutParams = 
				new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
		//if (Build.VERSION.SDK_INT >= 100)
		{
			mPlayView = mVideoService.createPlayView(mContext);
			addView(mPlayView, layoutParams);
		}
		else
		{
			mPlaySurfaceView = mVideoService.createPlaySurfaceView(mContext);
			addView(mPlaySurfaceView, layoutParams);
		}
    }
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		Log.d(TAG, "onFinishInflate");
	}
	@Override
	protected void onAttachedToWindow()
	{
		super.onAttachedToWindow();
		Log.d(TAG, "onAttachedToWindow");
		if (mVideoService == null)
		{
			throw new RuntimeException("video service not set!");
		}
	}

    public void start(String id)
    {	
    	if (mPlayView != null)
    		mPlayView.start(id);
    	else if (mPlaySurfaceView != null)
    		mPlaySurfaceView.start(id);
    }
    
    public void start(String id, boolean enableAudio)
    {
    	if (mPlayView != null)
    		mPlayView.start(id, enableAudio);
    	else if (mPlaySurfaceView != null)
    		mPlaySurfaceView.start(id, enableAudio);
    }
    
    public void setLooping(boolean loop)
    {
    	//checkView();
    	if (mPlayView != null)
    		mPlayView.setLooping(loop);
    	else if (mPlaySurfaceView != null)
    		mPlaySurfaceView.setLooping(loop);
    }
    
    public void start()
    {
    	if (mPlayView != null)
    		mPlayView.start();
    	else if (mPlaySurfaceView != null)
    		mPlaySurfaceView.start();
    }
    public void stop()
    {
    	if (mPlayView != null)
    		mPlayView.stop();
    	else if (mPlaySurfaceView != null)
    		mPlaySurfaceView.stop();
    }
    
    public void setVideoId(String id)
    {
    	if (mPlayView != null)
    		mPlayView.setVideoId(id);
    	else if (mPlaySurfaceView != null)
    		mPlaySurfaceView.setVideoId(id);
    }
    public String getVideoId()
    {
    	//checkView();
    	if (mPlayView != null)
    		return mPlayView.getVideoId();
    	else if (mPlaySurfaceView != null)
    		return mPlaySurfaceView.getVideoId();
    	else {
			return "";
		}
    }

    public boolean isPlaying()
    {
    	//checkView();
    	if (mPlayView != null)
    		return mPlayView.isPlaying();
    	else if (mPlaySurfaceView != null)
    		return mPlaySurfaceView.isPlaying();
    	else {
			return false;
		}
    }
    public void drawThumbnail()
    {
    	//checkView();
    	if (mPlayView != null)
    		mPlayView.drawThumbnail();
    	else if (mPlaySurfaceView != null)
    		mPlaySurfaceView.drawThumbnail();
    }
    public void drawBitmap(Bitmap bitmap)
    {
    	//checkView();
    	if (mPlayView != null)
    		mPlayView.drawBitmap(bitmap);
    	else if (mPlaySurfaceView != null)
    		mPlaySurfaceView.drawBitmap(bitmap);
    }
    
    public void setOnErrorListener(OnPlayErrorListener listener)
    {
    	//checkView();
    	if (mPlayView != null)
    		mPlayView.setOnErrorListener(listener);
    	else if (mPlaySurfaceView != null)
    		mPlaySurfaceView.setOnErrorListener(listener);
    }
    public static interface OnPlayErrorListener
    {
    	public void onError(int what, String id);
    }
}
