package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.tv.danmaku.ijk.media.widget;

import java.lang.ref.WeakReference;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.naruto.mobile.framework.service.R;
import com.naruto.mobile.framework.service.common.multimedia.widget.SightPlaySurfaceView;
import com.naruto.mobile.framework.service.common.multimedia.widget.SightVideoPlayView;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.ImageCacheContext;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.memory.MemoryCache;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.video.VideoFileManager;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.tv.danmaku.ijk.media.player.IMediaPlayer;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class SightPlaySurfaceViewImpl extends SightPlaySurfaceView
					implements SurfaceHolder.Callback, IMediaPlayer.OnErrorListener, IMediaPlayer.OnCompletionListener, IMediaPlayer.OnPreparedListener
{
	private static final String TAG = "SightPlaySurfaceViewImpl";
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
    private SurfaceHolder mHolder = null;
    
    public void setOnErrorListener(SightVideoPlayView.OnPlayErrorListener listener)
    {
    	mErrorListener = listener;
    }
    public SightPlaySurfaceViewImpl(Context context)
    {
        super(context);
        init();
    }
    private void init()
    {
    	Log.d(TAG, "sightplay surfaceview init");
    	getHolder().addCallback(this);
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
    	Canvas canvas = mSurface.lockCanvas(null);
    	if (canvas != null && bitmap != null)
    	{
    		Log.d(TAG, "handleDrawBitmap and bitmap: " + bitmap);
    		canvas.drawBitmap(bitmap, null, mDestRect, mPaint);
    	}
    	mSurface.unlockCanvasAndPost(canvas);
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
    	return mMediaPlayer.isPlaying();
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
    		drawThumbnail();
    		mHandler.removeMessages(MSG_PREPARE);
	    	mHandler.sendEmptyMessage(MSG_PREPARE);
    }
    
    public void start(String id)
    {
    		setVideoId(id);
    		drawThumbnail();
    		mHandler.removeMessages(MSG_PREPARE);
	    	mHandler.sendEmptyMessage(MSG_PREPARE);
    }
    
    public void start(String id, boolean enableAudio)
    {
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
        mHandler.sendEmptyMessage(MSG_RELEASE);
        mHandler.sendEmptyMessage(MSG_QUIT);
        super.onDetachedFromWindow();
    }
    
    private void handleRelease()
    {
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
        	//Log.d(TAG, this + " prepare begin");
            mMediaPlayer.setDataSource(mPlayUrl, !mEnableAudio);
            //mMediaPlayer.setSurface(mSurface);
            mMediaPlayer.setDisplay(mHolder);
            mMediaPlayer.seekTo(mCurPlayTime);
            mMediaPlayer.prepareAsync();
            //Log.d(TAG, this + " prepare end");
        }
        catch (Exception e)
        {
        	Log.d(TAG, this + " prepare exception");
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
    	private WeakReference<SightPlaySurfaceViewImpl> mReference;
    	private Looper mLooper;
    	PlayHandler(SightPlaySurfaceViewImpl view, Looper looper)
    	{
    		super(looper);
    		mLooper = looper;
    		mReference = new WeakReference<SightPlaySurfaceViewImpl>(view);
    	}
    	private void checkSurface()
    	{
    		SightPlaySurfaceViewImpl view = mReference.get();
			if (view.mHolder == null)
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
    		SightPlaySurfaceViewImpl playTextureView = mReference.get();
    		if (playTextureView == null)
    		{
    			Log.w(TAG, "outter class is null");
    			return;
    		}
    		switch (msg.what)
    		{
			case MSG_PREPARE:
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
					Log.d(TAG, "play handler handle msg: " + msg.what);
					checkSurface();
					playTextureView.handleDrawBitmap(playTextureView.getThumbnail());
				break;
			case MSG_FLASH:
				    checkSurface();
					playTextureView.drawEndFlash();
				break;
			case MSG_BITMAP:
					Log.d(TAG, "play handler handle msg: " + msg.what);
					checkSurface();
					playTextureView.handleDrawBitmap((Bitmap)msg.obj);
					
				break;
			case MSG_QUIT:
				Log.d(TAG, "play handler handle msg: " + msg.what);
				try {
					mLooper.quit();
				} catch (Exception e) {
				}
			break;
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
    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
		//Log.i(TAG, "surfaceCreated");
		mHolder = holder;
		mSurface = mHolder.getSurface();
    	mDestRect = new Rect(0, 0, getWidth(), getHeight());
        Log.d(TAG, this + "surfaceCreated and notify######");
        synchronized (mLock) {
        	mLock.notify();
		}
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
        Log.i(TAG, "surfaceChanged");
    }

    @Override
	 public void surfaceDestroyed(SurfaceHolder holder)
    {
    	Log.i(TAG, "surfaceDestroyed");
//    	mCurPlayTime = mMediaPlayer.getCurrentPosition();
//   	mMediaPlayer.stop();
//   	mMediaPlayer.reset();
    }
}

