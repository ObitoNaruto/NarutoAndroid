package com.naruto.mobile.framework.service.common.multimedia.widget;


import android.content.Context;
import android.graphics.Bitmap;
import android.view.SurfaceView;

public class SightPlaySurfaceView extends SurfaceView{
    public SightPlaySurfaceView(Context context)
    {
        super(context);
    }
    
    public void start(String id)
    {
    }
    
    public void start(String id, boolean enableAudio)
    {
    }
    
    public void setLooping(boolean loop)
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
    public void setOnErrorListener(SightVideoPlayView.OnPlayErrorListener listener)
    {
    	
    }
}