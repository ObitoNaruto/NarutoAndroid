package com.naruto.mobile.framework.service.common.multimedia.widget;


import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.naruto.mobile.base.serviceaop.NarutoApplication;
import com.naruto.mobile.framework.service.common.multimedia.api.MultimediaVideoService;

public class VideoPlayView extends FrameLayout
{
	private static final String TAG = "VideoPlayView";
	private MultimediaVideoService mVideoService = null;
	private VideoSurfaceView mPlayView = null;
	private Context mContext;
	
    public VideoPlayView(Context context)
    {
        super(context);
        init(context);
    }

    public VideoPlayView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }

    public VideoPlayView(Context context, AttributeSet attrs, int defStyle)
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
		
		mPlayView = mVideoService.createVideoPlayView(mContext);
		addView(mPlayView, layoutParams);
		ImageView img = new ImageView(mContext);
		img.setTag("thumbnail");
		img.setVisibility(View.INVISIBLE);
		addView(img, layoutParams);
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
    }
    public void start(String id, int msec)
    {
    	if (mPlayView != null)
    		mPlayView.start(id, msec);
    }
    public int getCurrentPosition()
    {
    	return mPlayView != null ? mPlayView.getCurrentPosition() : -1;
    }
    
    public int getDuration()
    {
    	return mPlayView != null ? mPlayView.getDuration() : -1;
    }
    
    public void setLooping(boolean loop)
    {
    	if (mPlayView != null)
    		mPlayView.setLooping(loop);
    }
    public void setOnCompletionListener(OnPlayCompletionListener listener)
    {
    	if (mPlayView != null)
    		mPlayView.setOnCompletionListener(listener);
    }
    public void start()
    {
    	if (mPlayView != null)
    		mPlayView.start();
    }
    public void pause()
    {
    	if (mPlayView != null)
    		mPlayView.pause();
    }
    public void stop()
    {
    	if (mPlayView != null)
    		mPlayView.stop();
    }
    
    public void setVideoId(String id)
    {
    	if (mPlayView != null)
    		mPlayView.setVideoId(id);
    }
    public String getVideoId()
    {
    	if (mPlayView != null)
    		return mPlayView.getVideoId();
    	else {
			return "";
		}
    }

    public boolean isPlaying()
    {
    	if (mPlayView != null)
    		return mPlayView.isPlaying();
    	else {
			return false;
		}
    }
    public void drawThumbnail()
    {
    	if (mPlayView != null)
    		mPlayView.drawThumbnail();

    }
    public void drawBitmap(Bitmap bitmap)
    {
    	if (mPlayView != null)
    		mPlayView.drawBitmap(bitmap);

    }

    public static class VideoSurfaceView extends SurfaceView
    {
        public VideoSurfaceView(Context context)
        {
            super(context);
        }
        public void start(String id)
        {
        }
        
        public void start(String id, int msec)
        {
        }
        public void pause()
        {
        	
        }
        public int getCurrentPosition()
        {
        	return -1;
        }
        public int getDuration()
        {
        	return -1;
        }
        public void setLooping(boolean loop)
        {
        }
        public void setOnCompletionListener(OnPlayCompletionListener listener)
        {
        	
        }
        public void start()
        {
        }
        public void stop()
        {
        }
        
        public void setVideoId(String id)
        {
        }
        public String getVideoId()
        {
        	return "";
        }

        public boolean isPlaying()
        {
        	return false;
        }
        public void drawThumbnail()
        {
        	
        }
        public void drawBitmap(Bitmap bitmap)
        {
        	
        }
    }
    public static interface OnPlayCompletionListener
    {
    	public void onCompletion();
    }
}
