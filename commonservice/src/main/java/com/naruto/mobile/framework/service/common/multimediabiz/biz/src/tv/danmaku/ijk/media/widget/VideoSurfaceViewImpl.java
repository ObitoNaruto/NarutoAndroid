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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.naruto.mobile.framework.service.R;
import com.naruto.mobile.framework.service.common.multimedia.widget.VideoPlayView;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.audio.utils.AudioUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.ImageCacheContext;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.memory.MemoryCache;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.video.VideoFileManager;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.tv.danmaku.ijk.media.ffmpeg.FFmpegApi;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.tv.danmaku.ijk.media.player.AndroidMediaPlayer;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.tv.danmaku.ijk.media.player.IMediaPlayer;


public class VideoSurfaceViewImpl extends VideoPlayView.VideoSurfaceView
					implements SurfaceHolder.Callback, IMediaPlayer.OnErrorListener, IMediaPlayer.OnCompletionListener, IMediaPlayer.OnPreparedListener
{

	private static final String TAG = "VideoSurfaceViewImpl";
	private IMediaPlayer mMediaPlayer;
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
    //private OnPlayErrorListener mErrorListener;
    private Object mSurfaceLock = new Object();
    private Rect mDestRect;
    private Paint mPaint;
    private SurfaceHolder mHolder = null;
    private boolean mStarted = false;
    private boolean mIsLoop = false;
    private VideoPlayView.OnPlayCompletionListener mCompletionListener;
    private boolean mPreparing = false;
    private Object mLock = new Object();

    public VideoSurfaceViewImpl(Context context)
    {
        super(context);
        init();
    }
    private void init()
    {
    	Log.d(TAG, "full screen video surfaceview init");
    	getHolder().addCallback(this);

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
    	Log.d(TAG, this + "\tsetVideoId: " + id);
    }

    public String getVideoId()
    {
    	return mVideoId;
    }
    public boolean isPlaying()
    {
    	return mMediaPlayer == null ? false : mMediaPlayer.isPlaying();
    }

    public void setOnCompletionListener(VideoPlayView.OnPlayCompletionListener listener)
    {
    	Log.d(TAG, "setOnCompletionListener: " + listener);
    	mCompletionListener = listener;
    }
    @Override
    public void onPrepared(IMediaPlayer mp)
    {
    	Log.d(TAG, this + " prepare done"+ " Url: " + mPlayUrl + "current time: " + mCurPlayTime);

    	mHandler.sendEmptyMessage(MSG_PLAY);
    	post(new Runnable() {
			@Override
			public void run() {
				mStarted = true;
		    	ViewGroup view = (ViewGroup)getParent();
		    	ImageView imageView = (ImageView)view.findViewWithTag("thumbnail");
		    	imageView.setVisibility(View.INVISIBLE);
			}
		});
    }

    @Override
    public void onCompletion(IMediaPlayer mp)
    {
    	Log.d(TAG, this + "\tonCompletion");
    	if (mCompletionListener != null)
    	{
    		Log.d(TAG, mCompletionListener + "\tonCompletion callback");
    		mCompletionListener.onCompletion();
    	}
    	if (mHolder != null && mIsLoop)
    	{
	    	mHandler.sendEmptyMessage(MSG_RESET);
	    	//mHandler.sendEmptyMessage(MSG_FLASH);
	    	mHandler.sendEmptyMessage(MSG_PREPARE);
    	}
    	else
    	{
    		AudioUtils.resumeSystemAudio();
		}
    }
    
    @Override
    public boolean onError(IMediaPlayer mp, int what, int extra)
    {	
    	Log.d(TAG, this + " onError:" + what + "," + extra + " file: " + mPlayUrl);
    	return false;
    }

//    public void prepare(String url)
//    {
//	    	mPlayUrl = url;
//	    	mHandler.sendEmptyMessage(MSG_PREPARE);
//    }

    public void pause()
    {
    	if (mMediaPlayer != null)
    	{
    		mMediaPlayer.pause();
    	}
    }
    public void start()
    {
    	checkHandler();
		//drawThumbnail();
		Log.d(TAG, this + "\tstart###");
		startInternal();
    }
    
    public void start(String id, int msec)
    {
    	Log.d(TAG, this + "\tstart###");
		setVideoId(id);
		checkHandler();
		mCurPlayTime = msec;
		//drawThumbnail();
		startInternal();
    }
    public void start(String id)
    {
    	Log.d(TAG, this + "\tstart###");
		setVideoId(id);
		checkHandler();
		//drawThumbnail();
		startInternal();
    }
    private void startInternal()
    {
    	if (mPreparing)
    	{
    		return;
    	}
    	mPreparing = true;
		mHandler.removeMessages(MSG_PREPARE);
    	mHandler.sendEmptyMessage(MSG_PREPARE);
    }
    public int getCurrentPosition()
    {
    	Log.d(TAG, this + "\tgetCurrentPosition###");
    	if (mMediaPlayer != null)
    	{
    		return (int)mMediaPlayer.getCurrentPosition();
    	}
    	else
    	{
    		return -1;
    	}
    }
    public int getDuration()
    {
    	Log.d(TAG, "getDuration");
    	return FFmpegApi.getVideoInfo(VideoFileManager.getInstance().getVideoPath1(mVideoId)).duration;
    }
    public void setLooping(boolean loop)
    {
//    	if (mMediaPlayer != null)
//    	{
//    		mMediaPlayer.setLooping(loop);
//    	}
    	mIsLoop = loop;
    }

    public void stop()
    {
    	//mHandler.removeMessages(MSG_START);
    	Log.d(TAG, this + "\tstop");
		mHandler.removeMessages(MSG_RESET);
    	mHandler.sendEmptyMessage(MSG_RESET);
    	mCurPlayTime = 0;
    	mStarted = false;
    }
    public void drawThumbnail()
    {
    	//mPlayUrl = str;
    	mHandler.sendEmptyMessage(MSG_THUMB);
    }
    public void drawBitmap(final Bitmap bitmap)
    {
//    	Message msg = Message.obtain();
//    	msg.obj = bitmap;
//    	msg.what = MSG_BITMAP;
//    	mHandler.sendMessage(msg);
    	post(new Runnable() {
			@Override
			public void run() {
		    	if (mStarted)
		    		return;
		    	ViewGroup view = (ViewGroup)getParent();
		    	ImageView imageView = (ImageView)view.findViewWithTag("thumbnail");
		    	imageView.setScaleType(ImageView.ScaleType.FIT_XY);
		    	imageView.setImageBitmap(bitmap);
		    	imageView.setVisibility(View.VISIBLE);
			}
		});
    }
    protected void onAttachedToWindow()
    {
    	Log.d(TAG, this + "\tonAttachedToWindow");
        super.onAttachedToWindow();
    }

    protected void onDetachedFromWindow()
    {
        Log.d(TAG, this + "\tonDetachedFromWindow");
        if (mHandler != null)
        {
	        mHandler.sendEmptyMessage(MSG_RELEASE);
	        mHandler.sendEmptyMessage(MSG_QUIT);
        }
        mStarted = false;
        super.onDetachedFromWindow();
    }
    
    private void handleRelease()
    {
		if (mMediaPlayer != null)
		{
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
		AudioUtils.resumeSystemAudio();
		Log.d(TAG, this + "mediaplayer release done.");
    }
    
    private void handlePrepare()
    {	
        try
        {
        	Log.d(TAG, this + " prepare begin, path:" + mPlayUrl);
	        	//mMediaPlayer = new IjkMediaPlayer();
        	if (mMediaPlayer != null)
        	{
        		//mMediaPlayer.stop();
        		Log.d(TAG, "clear previous mediaplayer");
        		mMediaPlayer.reset();
        		mMediaPlayer.release();
        	}
    		mMediaPlayer = new AndroidMediaPlayer();
        	mMediaPlayer.setOnCompletionListener(this);
        	mMediaPlayer.setOnErrorListener(this);
        	mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setDataSource(mPlayUrl, false);
            Log.d(TAG, "after setDataSource");
            //mMediaPlayer.setSurface(mSurface);
            mMediaPlayer.setDisplay(mHolder);
            Log.d(TAG, "after setDisplay");
            //mMediaPlayer.seekTo((int)mCurPlayTime);
            mMediaPlayer.prepareAsync();
            Log.d(TAG, "after prepareAsync");
            //Log.d(TAG, this + " prepare end");
        }
        catch (Exception e)
        {
        	Log.e(TAG, this + " prepare exception:" + e.getMessage());
        	if (mMediaPlayer != null)
        		mMediaPlayer.release();
        }
    }
    
    private void handlePlay()
    {
    	if (mMediaPlayer == null)
    		return;
    	Log.d(TAG, "mMediaPlayer.start()");
		mMediaPlayer.start();
		if (mCurPlayTime > 0)
		{
			mMediaPlayer.seekTo((int)mCurPlayTime);
		}
		mPreparing = false;
		synchronized (mLock) {
			mLock.notifyAll();
		}
		if (AudioUtils.isMusicActive())
		{
			AudioUtils.pauseSystemAudio();
		}
    }
    
    private void handleReset()
    {
    	if (mMediaPlayer == null)
    		return;
		mMediaPlayer.stop();
		mMediaPlayer.reset();
		mMediaPlayer.release();
		mMediaPlayer = null;
		AudioUtils.resumeSystemAudio();
    }

    private static class PlayHandler extends Handler
    {	
    	private WeakReference<VideoSurfaceViewImpl> mReference;
    	private Looper mLooper;
    	PlayHandler(VideoSurfaceViewImpl view, Looper looper)
    	{
    		super(looper);
    		mLooper = looper;
    		mReference = new WeakReference<VideoSurfaceViewImpl>(view);
    	}
    	private void checkSurface()
    	{
    		VideoSurfaceViewImpl view = mReference.get();
			if (view.mHolder == null)
			{
				Log.d(TAG, this + "checkSurface and surface not ready");
				synchronized (view.mSurfaceLock) {
					try {
						view.mSurfaceLock.wait();
					} catch (InterruptedException e) {
					}
				}
			}
    	}
    	@Override
    	public void handleMessage(Message msg)
    	{
    		VideoSurfaceViewImpl playTextureView = mReference.get();
    		if (playTextureView == null)
    		{
    			Log.w(TAG, "outter class is null");
    			return;
    		}
    		Log.d(TAG, playTextureView + " handle msg: " + msg.what);
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
					playTextureView.mHandler = null;
					playTextureView.mThread = null;
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
        Log.d(TAG, this + " surfaceCreated and notify######");
        synchronized (mSurfaceLock) {
        	mSurfaceLock.notify();
		}
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
        Log.i(TAG, this + " surfaceChanged");
    }

    @Override
	 public void surfaceDestroyed(SurfaceHolder holder)
    {
    	Log.i(TAG, this + " surfaceDestroyed");
    	mHolder = null;
    }
	private void checkHandler()
	{
		if (mHandler == null || mThread == null || !mThread.isAlive() || mHandler.getLooper() == null)
		{
			Log.d(TAG, "thread not ready, create...");
	    	mThread = new HandlerThread("sight_play");
	    	mThread.start();
	    	mHandler = new PlayHandler(this, mThread.getLooper());
		}
	}
}
