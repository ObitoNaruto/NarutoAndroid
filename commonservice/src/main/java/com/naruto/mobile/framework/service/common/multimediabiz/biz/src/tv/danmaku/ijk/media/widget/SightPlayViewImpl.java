package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.tv.danmaku.ijk.media.widget;

import java.lang.ref.WeakReference;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;

import com.naruto.mobile.framework.service.R;
import com.naruto.mobile.framework.service.common.multimedia.widget.SightPlayView;
import com.naruto.mobile.framework.service.common.multimedia.widget.SightVideoPlayView;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.ImageCacheContext;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.memory.MemoryCache;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.video.VideoFileManager;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.tv.danmaku.ijk.media.player.IMediaPlayer;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.tv.danmaku.ijk.media.player.IjkMediaPlayer;


@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class SightPlayViewImpl extends SightPlayView
					implements TextureView.SurfaceTextureListener, IMediaPlayer.OnErrorListener, IMediaPlayer.OnCompletionListener,
		IMediaPlayer.OnPreparedListener
{
	private static final String TAG = "SightPlayViewImpl";
	private IjkMediaPlayer mMediaPlayer;
    private String mPlayUrl;
    
    //this id maybe localId or cloudId
    private String mVideoId;
    
    private long mCurPlayTime = 0;
    private Surface mSurface;
    private PlayHandler mHandler;
    private HandlerThread mThread;
    private static final int MSG_PREPARE = 0;
    private static final int MSG_PLAY = 1;
    //private static final int MSG_START = 2;
    private static final int MSG_RESET = 3;
    private static final int MSG_RELEASE = 4;
    private static final int MSG_THUMB = 5;
    private static final int MSG_FLASH = 6;
    private static final int MSG_BITMAP = 7;
    private static final int MSG_QUIT = 8;
    private static Bitmap mFlash;
    private volatile boolean mFocusLost;
    private SightVideoPlayView.OnPlayErrorListener mErrorListener;
    private Object mLock = new Object();
    private Rect mDestRect;
    private Paint mPaint;
    private boolean mIsLoop = true;
    private boolean mEnableAudio = false;
    private SurfaceTexture mSurfaceTexture;
    public void setOnErrorListener(SightVideoPlayView.OnPlayErrorListener listener)
    {
    	mErrorListener = listener;
    }
    public SightPlayViewImpl(Context context)
    {
        super(context);
        init();
    }
    private void init()
    {
    	Log.d(TAG, this + "\tsightplay view init");
    	setSurfaceTextureListener(this);
    	mMediaPlayer = new IjkMediaPlayer();
    	mMediaPlayer.setOnCompletionListener(this);
    	mMediaPlayer.setOnErrorListener(this);
    	mThread = new HandlerThread("sight_play");
    	mThread.start();
    	mHandler = new PlayHandler(this, mThread.getLooper());
    	if (mFlash == null)
    	{
    		mFlash = BitmapFactory.decodeResource(getResources(), R.drawable.flow_warning);
    	}
    	mPaint = new Paint();
    	mPaint.setFilterBitmap(true);
    }
    
    private void handleDrawBitmap(Bitmap bitmap)
    {
    	Canvas canvas = null;
    	try
    	{
    		canvas = mSurface.lockCanvas(null);
    		Log.d(TAG, this + "\tdrawBitmap, w = " + bitmap.getWidth() + ",h = " + bitmap.getHeight());
			Log.d(TAG, "before drawbitmap:" + "layer w: " + canvas.getWidth() + ", layer height:" + canvas.getHeight());
			mDestRect = new Rect(0, 0, canvas.getWidth(), canvas.getHeight());
    		canvas.drawBitmap(bitmap, null, mDestRect, mPaint);
		}
    	catch (Exception e) 
    	{
			Log.e(TAG, "drawBitmap exception: " + e.getMessage());
		}
    	finally
    	{
	          try {
	        	  mSurface.unlockCanvasAndPost(canvas);
	        } catch (IllegalArgumentException exception) {
	            Log.e(TAG, "unlockCanvasAndPost failed: " + exception.getMessage());
	        }
	        postInvalidate();
    	}
    }

    private Bitmap getThumbnail()
    {
    	long ts = System.currentTimeMillis();
    	MemoryCache<Bitmap> cache = ImageCacheContext.get().getMemCache();
    	String jpath = VideoFileManager.getInstance().getThumbPath1(mVideoId);
    	Bitmap bitmap = cache.get(jpath);
    	if ( bitmap == null)
    	{
        	bitmap = BitmapFactory.decodeFile(jpath);
        	
        	if (bitmap == null)
        	{
        		Log.e(TAG, "jpg not found### : " + jpath);
        		return null;
        	}
        	cache.put(jpath, bitmap);
    	}
    	Log.d(TAG, "operation getthumbnail took " + (System.currentTimeMillis() - ts) + "ms");
    	return bitmap;
    }
    private void drawEndFlash()
    {	
    	handleDrawBitmap(mFlash);
    	try {
    		Thread.sleep(300);
		} catch (Exception e) {
		}
    	
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int w, int h)
    {
    	Log.d(TAG, this + "###onSurfaceTextureAvailable, w = " + w + ", h = " + h);
        mSurface = new Surface(surfaceTexture);
        mSurfaceTexture = surfaceTexture;
        Log.d(TAG, this + "isAvailable and notify######");
        synchronized (mLock) {
        	mLock.notify();
		}
    }
    
    //should be called before view display
    public void setVideoId(String id)
    {
    	mVideoId = id;
    	mPlayUrl = VideoFileManager.getInstance().getVideoPath1(id);
    	Log.d(TAG, this + "setVideoId: " + id);
    }

    public String getVideoId()
    {
    	return mVideoId;
    }
    public boolean isPlaying()
    {
    	return mMediaPlayer != null ? mMediaPlayer.isPlaying() : false;
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
    	if (mSurfaceTexture != surfaceTexture)
    		Log.d(TAG, "###surfacetexture error###");
    }
    
    @Override
    public void onPrepared(IMediaPlayer mp)
    {
    	Log.d(TAG, this + " prepare done"+ " Url: " + mPlayUrl + "current time: " + mCurPlayTime);
    	//mHandler.sendEmptyMessage(MSG_PLAY);
    }
    
    @Override
    public void onCompletion(IMediaPlayer mp)
    {
    	//Log.d(TAG, this + " onCompletion");
    	if (!mFocusLost && mIsLoop)
    	{
	    	mHandler.sendEmptyMessage(MSG_RESET);
	    	//mHandler.sendEmptyMessage(MSG_FLASH);
	    	mHandler.sendEmptyMessage(MSG_PREPARE);
    	}
    }
    
    @Override
    public boolean onError(IMediaPlayer mp, int what, int extra)
    {	
    	Log.d(TAG, this + " onError:" + what + "," + extra + " file: " + mPlayUrl);
    	if (mErrorListener != null)
    	{
    		mErrorListener.onError(what, mVideoId);
    	}
    	return false;
    }

//    public void prepare(String url)
//    {
//	    	mPlayUrl = url;
//	    	mHandler.sendEmptyMessage(MSG_PREPARE);
//    }
    
    public void start()
    {	
    	Log.d(TAG, this + "\tstart###");
		drawThumbnail();
		mHandler.removeMessages(MSG_PREPARE);
    	mHandler.sendEmptyMessage(MSG_PREPARE);
    }
    
    public void start(String id)
    {
    	Log.d(TAG, this + "\tstart###");
		setVideoId(id);
		drawThumbnail();
		mHandler.removeMessages(MSG_PREPARE);
    	mHandler.sendEmptyMessage(MSG_PREPARE);
    }
    
    public void start(String id, boolean enableAudio)
    {
    	Log.d(TAG, this + "\tstart###");
		mEnableAudio = enableAudio;
		setVideoId(id);
		drawThumbnail();
		mHandler.removeMessages(MSG_PREPARE);
    	mHandler.sendEmptyMessage(MSG_PREPARE);
    }
    
    public void setLooping(boolean loop)
    {
    	mIsLoop = loop;
    }

    public void stop()
    {
    	Log.d(TAG, "stop###");
    	//mHandler.removeMessages(MSG_START);
		mHandler.removeMessages(MSG_RESET);
    	mHandler.sendEmptyMessage(MSG_RESET);
    	mCurPlayTime = 0;
    }
    public void drawThumbnail()
    {
    	//mPlayUrl = str;
    	mHandler.sendEmptyMessage(MSG_THUMB);
    }
    public void drawBitmap(Bitmap bitmap)
    {
    	Message msg = Message.obtain();
    	msg.obj = bitmap;
    	msg.what = MSG_BITMAP;
    	mHandler.sendMessage(msg);
    }
//    protected void onAttachedToWindow()
//    {
//        super.onAttachedToWindow();
//    }

    protected void onDetachedFromWindow()
    {
        Log.d(TAG, this + "onDetachedFromWindow");
        boolean result = mHandler.sendEmptyMessage(MSG_RELEASE);
        Log.d(TAG, "sendEmptyMessage result:" + result);
        mHandler.sendEmptyMessage(MSG_QUIT);
        super.onDetachedFromWindow();
    }
    
    private void handleRelease()
    {
    	Log.d(TAG, this + "mediaplayer release begin.");
		if (mMediaPlayer != null)
		{
			mMediaPlayer.release();
		}
		Log.d(TAG, this + "mediaplayer release done.");
    }
    
    private void handlePrepare()
    {
        try
        {
        	Log.d(TAG, this + " handlePrepare mplayurl:" + mPlayUrl);
            mMediaPlayer.setDataSource(mPlayUrl, !mEnableAudio);
            mMediaPlayer.setSurface(mSurface);
            mMediaPlayer.seekTo(mCurPlayTime);
            mMediaPlayer.prepareAsync();
            //Log.d(TAG, this + " prepare end");
        }
        catch (Exception e)
        {
        	Log.d(TAG, this + " prepare exception:" + e.getMessage());
        }
    }
    
    private void handlePlay()
    {
		mMediaPlayer.start();
		if (mCurPlayTime > 0)
		{
			mMediaPlayer.seekTo(mCurPlayTime);
		}
		//mMediaPlayer.seekTo(0);
    }
    
    private void handleReset()
    {
		mMediaPlayer.stop();
		mMediaPlayer.reset();
    }

    private static class PlayHandler extends Handler
    {
    	private WeakReference<SightPlayViewImpl> mReference;
    	private Looper mLooper;
    	
    	PlayHandler(SightPlayViewImpl view, Looper looper)
    	{
    		super(looper);
    		mLooper = looper;
    		mReference = new WeakReference<SightPlayViewImpl>(view);
    	}
    	private void checkSurface()
    	{
    		SightPlayViewImpl view = mReference.get();
			if (!view.isAvailable())
			{
				Log.d(TAG, this + "checkSurface and surface not ready");
				synchronized (view.mLock) {
					try {
						view.mLock.wait();
					} catch (InterruptedException e) {
					}
				}
			}
    	}
    	@Override
    	public void handleMessage(Message msg)
    	{	
    		SightPlayViewImpl playTextureView = mReference.get();
    		if (playTextureView == null)
    		{
    			Log.w(TAG, "outter class is null");
    			return;
    		}
    		Log.d(TAG, playTextureView + " play handler handle msg: " + msg.what);
    		switch (msg.what)
    		{
			case MSG_PREPARE:
				checkSurface();
				playTextureView.handlePrepare();
				break;
			case MSG_PLAY:
				playTextureView.handlePlay();
	    		break;

			case MSG_RESET:
				playTextureView.handleReset();
				break;
			case MSG_RELEASE:
				playTextureView.handleRelease();
				break;
			case MSG_THUMB:
					checkSurface();
					playTextureView.handleDrawBitmap(playTextureView.getThumbnail());
				break;
			case MSG_FLASH:
				    checkSurface();
					playTextureView.drawEndFlash();
				break;
			case MSG_BITMAP:
					checkSurface();
					playTextureView.handleDrawBitmap((Bitmap)msg.obj);
				break;
			case MSG_QUIT:
				try {
					mLooper.quit();
				} catch (Exception e) {
				}
			default:
				break;
			}
    	}
    }
//  public void onWindowFocusChanged(boolean hasWindowFocus)
//  {
//  	super.onWindowFocusChanged(hasWindowFocus);
//  	Log.d(TAG, "onWindowFocusChanged hasWindowFocus: " + hasWindowFocus);
//  	if (hasWindowFocus)
//  	{
//	    	if (isAvailable() && mFocusLost)
//	    	{
//	    		start(mPlayUrl);
//	    	}
//	    	mFocusLost = false;
//  	}
//  	else
//  	{
//  		mFocusLost = true;
//      	mCurPlayTime = mMediaPlayer.getCurrentPosition();
//      	mHandler.sendEmptyMessage(MSG_RESET);
//		}
//  }
}
