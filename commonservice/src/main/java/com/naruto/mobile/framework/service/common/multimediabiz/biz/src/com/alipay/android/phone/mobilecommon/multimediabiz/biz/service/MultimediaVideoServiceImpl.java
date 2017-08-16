package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

//import tv.danmaku.ijk.media.ffmpeg.FFmpegApi;
//import tv.danmaku.ijk.media.ffmpeg.FFmpegVideoCompressConfig;
//import tv.danmaku.ijk.media.ffmpeg.FFmpegVideoInfo;
//import tv.danmaku.ijk.media.player.IjkLibLoader;
//import tv.danmaku.ijk.media.player.IjkMediaPlayer;
//import tv.danmaku.ijk.media.widget.SightCameraViewImpl;
//import tv.danmaku.ijk.media.widget.SightPlaySurfaceViewImpl;
//import tv.danmaku.ijk.media.widget.SightPlayViewImpl;
//import tv.danmaku.ijk.media.widget.VideoSurfaceViewImpl;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;

import com.naruto.mobile.base.serviceaop.NarutoApplication;
import com.naruto.mobile.framework.service.common.multimedia.api.MultimediaFileService;
import com.naruto.mobile.framework.service.common.multimedia.api.MultimediaImageService;
import com.naruto.mobile.framework.service.common.multimedia.api.MultimediaVideoService;
import com.naruto.mobile.framework.service.common.multimedia.api.data.APMultimediaTaskModel;
import com.naruto.mobile.framework.service.common.multimedia.file.APFileDownCallback;
import com.naruto.mobile.framework.service.common.multimedia.file.APFileUploadCallback;
import com.naruto.mobile.framework.service.common.multimedia.file.data.APFileDownloadRsp;
import com.naruto.mobile.framework.service.common.multimedia.file.data.APFileReq;
import com.naruto.mobile.framework.service.common.multimedia.file.data.APFileRsp;
import com.naruto.mobile.framework.service.common.multimedia.file.data.APFileUploadRsp;
import com.naruto.mobile.framework.service.common.multimedia.graphics.APDisplayer;
import com.naruto.mobile.framework.service.common.multimedia.graphics.APImageDownLoadCallback;
import com.naruto.mobile.framework.service.common.multimedia.graphics.data.APImageLoadRequest;
import com.naruto.mobile.framework.service.common.multimedia.video.APVideoDownloadCallback;
import com.naruto.mobile.framework.service.common.multimedia.video.APVideoUploadCallback;
import com.naruto.mobile.framework.service.common.multimedia.video.data.APAlbumVideoInfo;
import com.naruto.mobile.framework.service.common.multimedia.video.data.APVideoDownloadRsp;
import com.naruto.mobile.framework.service.common.multimedia.video.data.APVideoLoadStatus;
import com.naruto.mobile.framework.service.common.multimedia.video.data.APVideoUploadRsp;
import com.naruto.mobile.framework.service.common.multimedia.video.data.CameraParams;
import com.naruto.mobile.framework.service.common.multimedia.widget.SightCameraView;
import com.naruto.mobile.framework.service.common.multimedia.widget.SightPlaySurfaceView;
import com.naruto.mobile.framework.service.common.multimedia.widget.SightPlayView;
import com.naruto.mobile.framework.service.common.multimedia.widget.SightVideoPlayView;
import com.naruto.mobile.framework.service.common.multimedia.widget.VideoPlayView;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.ImageCacheContext;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.disc.DiskCache;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.memory.MemoryCache;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.io.IOUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.assist.ViewAssistant;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.assist.ViewWrapper;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.FileUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.PathUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.video.VideoCacheModel;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.video.VideoFileManager;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.tv.danmaku.ijk.media.ffmpeg.FFmpegApi;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.tv.danmaku.ijk.media.ffmpeg.FFmpegVideoCompressConfig;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.tv.danmaku.ijk.media.ffmpeg.FFmpegVideoInfo;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.tv.danmaku.ijk.media.player.IjkLibLoader;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.tv.danmaku.ijk.media.player.IjkMediaPlayer;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.tv.danmaku.ijk.media.widget.SightCameraViewImpl;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.tv.danmaku.ijk.media.widget.SightPlaySurfaceViewImpl;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.tv.danmaku.ijk.media.widget.SightPlayViewImpl;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.tv.danmaku.ijk.media.widget.VideoSurfaceViewImpl;


public class MultimediaVideoServiceImpl extends MultimediaVideoService {
    private Context mContext;

    private MultimediaFileService mFileService;
    private MultimediaImageService mImageService;
    public static final String TAG = "MultimediaVideoServiceImpl";
    private ConcurrentHashMap<String, APVideoLoadStatus> mStatus = new ConcurrentHashMap<String, APVideoLoadStatus>();

    private final int BITRATE = 750 * 1024;
    private static IjkLibLoader sLocalLibLoader = new IjkLibLoader() {
        @Override
        public void loadLibrary(String libName) throws UnsatisfiedLinkError, SecurityException {
            System.loadLibrary(libName);
        }
    };

    private static volatile boolean mIsLibLoaded = false;
    public static void loadLibrariesOnce(IjkLibLoader libLoader) {
        synchronized (IjkMediaPlayer.class) {
            if (!mIsLibLoaded) {
                libLoader.loadLibrary("ijkffmpeg");
                libLoader.loadLibrary("ijkutil");
                libLoader.loadLibrary("ijksdl");
                libLoader.loadLibrary("ijkmuxing");
                libLoader.loadLibrary("ijkplayer");
                mIsLibLoaded = true;
            }
        }
    }
    
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mContext = NarutoApplication.getInstance().getApplicationContext();

        if (mContext != null) {
            mFileService = NarutoApplication.getInstance().getNarutoApplicationContext().getExtServiceByInterface(
                    MultimediaFileService.class.getName());
            mImageService = NarutoApplication.getInstance().getNarutoApplicationContext().getExtServiceByInterface(
                    MultimediaImageService.class.getName());
        }
        loadLibrariesOnce(sLocalLibLoader);
    }
    public SightCameraView createCameraView(Context context, CameraParams params)
    {
    	return new SightCameraViewImpl(context);
    }
    public SightCameraView createCameraView(Context context)
    {
    	return new SightCameraViewImpl(context);
    }

    public SightPlayView createPlayView(Context context, CameraParams params)
    {
    	return new SightPlayViewImpl(context);
    }
    public SightPlayView createPlayView(Context context)
    {
    	return new SightPlayViewImpl(context);
    }
    
    public SightPlaySurfaceView createPlaySurfaceView(Context context, CameraParams params)
    {
    	return new SightPlaySurfaceViewImpl(context);
    }
    public SightPlaySurfaceView createPlaySurfaceView(Context context)
    {
    	return new SightPlaySurfaceViewImpl(context);
    }
    
    public VideoSurfaceViewImpl createVideoPlayView(Context context, CameraParams params)
    {
    	return new VideoSurfaceViewImpl(context);
    }
    public VideoSurfaceViewImpl createVideoPlayView(Context context)
    {
    	return new VideoSurfaceViewImpl(context);
    }
    
    //upload thumbnail at first then upload video
    public void uploadAlbumVideo(final String id, final APVideoUploadCallback callback)
    {
    	String path = id;
    	if (PathUtils.extractFile(path) == null)
    	{
    		path = VideoFileManager.getInstance().getVideoPath1(path);
    	}
    	else {
			path = PathUtils.extractFile(path).getAbsolutePath();
		}

    	String jpath = getVideoThumbnail(path);
    	
    	mFileService.upLoad(jpath, new APFileUploadCallback() {
			@Override
			public void onUploadStart(APMultimediaTaskModel arg0) {
				if (callback != null)
					callback.onUploadStart(arg0);
			}
			
			@Override
			public void onUploadProgress(APMultimediaTaskModel arg0, int progress,
					long arg2, long arg3) {
				if (callback != null)
					callback.onUploadProgress(arg0, progress / 20);
			}
			
			@Override
			public void onUploadFinished(APMultimediaTaskModel arg0,
					APFileUploadRsp arg1) {
				APVideoUploadRsp rsp = new APVideoUploadRsp();
				//rsp.setRsp(arg1);
				rsp.mThumbId = arg0.getCloudId();
				
				uploadVideoInternal(id, callback, rsp);
			}
			
			@Override
			public void onUploadError(APMultimediaTaskModel arg0, APFileUploadRsp arg1) {
				APVideoUploadRsp rsp = new APVideoUploadRsp();
				rsp.setRsp(arg1);
				if (callback != null)
				callback.onUploadError(rsp);
			}
		});
    }
    
    public APVideoUploadRsp uploadAlbumVideoSync(final String id, final APVideoUploadCallback callback)
    {
    	String path = id;
    	if (PathUtils.extractFile(path) == null)
    	{
    		path = VideoFileManager.getInstance().getVideoPath1(path);
    	}
    	else {
			path = PathUtils.extractFile(path).getAbsolutePath();
		}

    	String jpath = getVideoThumbnail(path);

    	final APVideoUploadRsp rsp = new APVideoUploadRsp();
    	
    	APFileReq req = new APFileReq();
    	req.setSavePath(jpath);
    	mFileService.upLoadSync(req, new APFileUploadCallback() {
			@Override
			public void onUploadStart(APMultimediaTaskModel arg0) {
				if (callback != null)
					callback.onUploadStart(arg0);
			}

			@Override
			public void onUploadProgress(APMultimediaTaskModel arg0, int progress,
										 long arg2, long arg3) {
				if (callback != null)
					callback.onUploadProgress(arg0, progress / 20);
			}

			@Override
			public void onUploadFinished(APMultimediaTaskModel arg0,
										 APFileUploadRsp arg1) {
				
				rsp.mThumbId = arg0.getCloudId();

				uploadVideoInternalSync(id, callback, rsp);
			}

			@Override
			public void onUploadError(APMultimediaTaskModel arg0, APFileUploadRsp arg1) {
				APVideoUploadRsp rsp = new APVideoUploadRsp();
				rsp.setRsp(arg1);
				if (callback != null)
					callback.onUploadError(rsp);
			}
		});
    	return rsp;
    }
    
    //upload thumbnail at first then upload video
//    public void uploadShortVideo(final String id, SightPlayView playView, final APVideoUploadCallback callback)
//    {	
//    	Log.d(TAG, "#######uploadVideo:" + id);
//    	final SightPlayViewImpl view = playView;
//    	if (view.isPlaying())
//    	{
//    		view.stop();
//    	}
//    	view.setVideoId(id);
//    	view.drawThumbnail();
//    	
//    	String path = VideoFileManager.getInstance().getThumbPath(id);
//    	APFileReq req = new APFileReq();
//    	req.setSavePath(path);
//    	mFileService.upLoad(req, new APFileUploadCallback() {
//			@Override
//			public void onUploadStart(APMultimediaTaskModel arg0) {
//				if (callback != null)
//					callback.onUploadStart(arg0);
//			}
//			
//			@Override
//			public void onUploadProgress(APMultimediaTaskModel arg0, int arg1,
//					long arg2, long arg3) {
//				if (callback != null)
//					callback.onUploadProgress(arg0, arg1 / 20);
//			}
//			
//			@Override
//			public void onUploadFinished(APMultimediaTaskModel arg0,
//					APFileUploadRsp arg1) {
//				APVideoUploadRsp rsp = new APVideoUploadRsp();
//				//rsp.setRsp(arg1);
//				rsp.mThumbId = arg0.getCloudId();
//				uploadVideoInternal(id, callback, rsp);
//			}
//			
//			@Override
//			public void onUploadError(APMultimediaTaskModel arg0, APFileUploadRsp arg1) {
//				APVideoUploadRsp rsp = new APVideoUploadRsp();
//				rsp.setRsp(arg1);
//				if (callback != null)
//					callback.onUploadError(rsp);
//			}
//		});
//    }

    public APVideoUploadRsp uploadShortVideoSync(final String id, final APVideoUploadCallback callback)
    {
    	Log.d(TAG, "#######uploadVideo sync:" + id);
    	String path = id;
    	if (PathUtils.extractFile(path) == null)
    	{
    		path = VideoFileManager.getInstance().getThumbPath1(path);
    	}
    	else {
			path = PathUtils.extractFile(path).getAbsolutePath();
		}
    	final APVideoUploadRsp uploadRsp_cb = new APVideoUploadRsp();
    	
    	APFileReq req = new APFileReq();
    	req.setSavePath(path);
    	
    	
    	mFileService.upLoadSync(req, new APFileUploadCallback() {
			@Override
			public void onUploadStart(APMultimediaTaskModel arg0) {
				if (callback != null)
					callback.onUploadStart(arg0);
			}
			
			@Override
			public void onUploadProgress(APMultimediaTaskModel arg0, int arg1,
					long arg2, long arg3) {
				if (callback != null)
					callback.onUploadProgress(arg0, arg1 / 20);
			}
			
			@Override
			public void onUploadFinished(APMultimediaTaskModel arg0,
					APFileUploadRsp arg1) {
				//APVideoUploadRsp rsp = new APVideoUploadRsp();
				//rsp.setRsp(arg1);
				if (!TextUtils.isEmpty(uploadRsp_cb.mThumbId))
					return;
				uploadRsp_cb.mThumbId = arg0.getCloudId();
				//uploadRsp_ret.mThumbId = arg0.getCloudId();
				uploadVideoInternalSync(id, callback, uploadRsp_cb);
			}
			
			@Override
			public void onUploadError(APMultimediaTaskModel arg0, APFileUploadRsp arg1) {
				APVideoUploadRsp rsp = new APVideoUploadRsp();
				rsp.setRsp(arg1);
				if (callback != null)
					callback.onUploadError(rsp);
			}
		});
    	return uploadRsp_cb;
    }
    
    //used internal
    private void uploadVideoInternal(final String id, 
						    		final APVideoUploadCallback callback, 
						    		final APVideoUploadRsp rsp)
    {
    	
    	String path = id;
    	if (PathUtils.extractFile(path) == null)
    	{
    		path = VideoFileManager.getInstance().getVideoPath(path);
    	}
    	else {
			path = PathUtils.extractFile(path).getAbsolutePath();
		}
		APFileReq req = new APFileReq();
		req.setSavePath(path);
		mFileService.upLoad(req, new APFileUploadCallback() {
			@Override
			public void onUploadStart(APMultimediaTaskModel arg0) {
			}
			
			@Override
			public void onUploadProgress(APMultimediaTaskModel arg0, int arg1,
					long arg2, long arg3) {
				if (callback != null)
				{
					callback.onUploadProgress(arg0, arg1 * 95 / 100 + 5);
				}
			}
			
			@Override
			public void onUploadFinished(APMultimediaTaskModel arg0,
					APFileUploadRsp arg1) {
				rsp.setRsp(arg1);
				rsp.mVideoId = arg0.getCloudId();
				rsp.mId = rsp.mVideoId + "|" + rsp.mThumbId;

		    	VideoFileManager.getInstance().setCloudId(rsp.mVideoId, id, VideoFileManager.VIDEO);
		    	VideoFileManager.getInstance().setCloudId(rsp.mThumbId, id, VideoFileManager.JPG);
		    	
		    	
				//rename video file and thumbnail
//				String vorigin = arg1.getFileReq().getSavePath();
//				String vdest = vorigin.substring(0, vorigin.lastIndexOf('/')) + File.separator + rsp.mVideoId + ".mp4";
//				new File(vorigin).renameTo(new File(vdest));
//				
//				
//				String jorigin = vorigin.substring(0, vorigin.lastIndexOf('.')) + ".jpg";
//				String jdest = jorigin.substring(0, jorigin.lastIndexOf('/')) + File.separator + rsp.mThumbId + ".jpg";
//				new File(jorigin).renameTo(new File(jdest));

				if (callback != null)
				{
					callback.onUploadFinished(rsp);
				}
			}
			@Override
			public void onUploadError(APMultimediaTaskModel arg0, APFileUploadRsp arg1) {
				APVideoUploadRsp rsp = new APVideoUploadRsp();
				rsp.setRsp(arg1);
				if (callback != null)
				{
					callback.onUploadError(rsp);
				}
			}
		});
    }
    
    
    //used internal
    private void uploadVideoInternalSync(final String id, 
						    		final APVideoUploadCallback callback, 
						    		final APVideoUploadRsp rsp)
    {
    	String path = id;
    	if (PathUtils.extractFile(path) == null)
    	{
    		path = VideoFileManager.getInstance().getVideoPath1(path);
    	}
    	else {
			path = PathUtils.extractFile(path).getAbsolutePath();
		}
		APFileReq req = new APFileReq();
		req.setSavePath(path);
		mFileService.upLoadSync(req, new APFileUploadCallback() {
			@Override
			public void onUploadStart(APMultimediaTaskModel arg0) {
			}
			
			@Override
			public void onUploadProgress(APMultimediaTaskModel arg0, int arg1,
					long arg2, long arg3) {
				if (callback != null)
				{
					callback.onUploadProgress(arg0, arg1 * 95 / 100 + 5);
				}
			}
			
			@Override
			public void onUploadFinished(APMultimediaTaskModel arg0,
					APFileUploadRsp arg1) {
				Log.d(TAG, "album video upload finished!");
				rsp.setRsp(arg1);
				rsp.mVideoId = arg0.getCloudId();
				rsp.mId = rsp.mVideoId + "|" + rsp.mThumbId;

		    	VideoFileManager.getInstance().setCloudId(rsp.mVideoId, id, VideoFileManager.VIDEO);
		    	VideoFileManager.getInstance().setCloudId(rsp.mThumbId, id, VideoFileManager.JPG);
		    	
				if (callback != null)
				{
					callback.onUploadFinished(rsp);
				}
			}
			@Override
			public void onUploadError(APMultimediaTaskModel arg0, APFileUploadRsp arg1) {
				APVideoUploadRsp rsp = new APVideoUploadRsp();
				rsp.setRsp(arg1);
				if (callback != null)
				{
					callback.onUploadError(rsp);
				}
			}
		});
    }
    
    //判断视频是否已经下载
    public boolean isVideoAvailable(String id)
    {
    	String 	path = id;
    	if (PathUtils.extractFile(path) == null)
    	{
    		path = VideoFileManager.getInstance().getVideoPath1(path);
    	}
    	else {
			path = PathUtils.extractFile(path).getAbsolutePath();
		}
    	
    	return FileUtils.checkFile(path);
    }
    //下载视频文件，小视频和相册视频通用
    public void downloadVideo(final String id, final APVideoDownloadCallback callback)
    {
    	if (!id.contains("|"))
    	{
    		//throw new RuntimeException("download video id must be cloudid!");
//    		Log.e(TAG, "download video id must be cloudid!");
    		return;
    	}
    	String vid = id.substring(0, id.indexOf('|'));
    	String vpath = VideoFileManager.getInstance().getVideoPath(id);
//    	if (FileUtils.checkFile(vpath))
//    	{
//    		callback.onDownloadFinished(null);
//    		return;
//    	}
    	APFileReq req = new APFileReq();
    	req.setCloudId(vid);
    	req.setSavePath(vpath);
    	
		APVideoLoadStatus status = mStatus.get(id);
		if (status != null)
		{
			status.mProgress = 0;
			status.mStatus = APVideoLoadStatus.VIDEOLOADING;
			mStatus.put(id, status);
		}
    	
    	
    	mFileService.downLoad(req, new APFileDownCallback() {
			@Override
			public void onDownloadStart(APMultimediaTaskModel arg0) {
				if (callback != null)
				{
					callback.onDownloadStart(arg0);
				}
			}
			@Override
			public void onDownloadProgress(APMultimediaTaskModel arg0, int arg1,
					long arg2, long arg3) {
				APVideoLoadStatus status = mStatus.get(id);
				if (status != null)
				{
					status.mProgress = arg1;
					status.mStatus = APVideoLoadStatus.VIDEOLOADING;
					mStatus.put(id, status);
				}
				if (callback != null)
				{
					callback.onDownloadProgress(arg0, arg1);
				}
			}
			@Override
			public void onDownloadFinished(APMultimediaTaskModel arg0,
					APFileDownloadRsp arg1) {
				
//				APVideoLoadStatus status = mStatus.get(id);
//				status.mProgress = 100;
//				status.mStatus = APVideoLoadStatus.VIDEODONE;
//		    	mStatus.put(id, status);
				mStatus.remove(id);
		    	//this downlaod may load shortvideo, I still insert it to db as album video, because it doesn't matter
				VideoFileManager.getInstance().insertRecord(arg1.getFileReq().getSavePath(), id.substring(0, id.indexOf('|')), "", VideoFileManager.TYPE_OTHERS);
				if (callback != null)
				{
					APVideoDownloadRsp rsp = new APVideoDownloadRsp();
					rsp.setRsp(arg1);
					callback.onDownloadFinished(rsp);
				}
			}
			@Override
			public void onDownloadError(APMultimediaTaskModel arg0,
					APFileDownloadRsp arg1) {
				mStatus.remove(id);
				if (callback != null)
				{
					APVideoDownloadRsp rsp = new APVideoDownloadRsp();
					rsp.setRsp(arg1);
					callback.onDownloadError(rsp);
				}
			}
			@Override
			public void onDownloadBatchProgress(APMultimediaTaskModel arg0, int arg1,
					int arg2, long arg3, long arg4) {
			}
		});
    }
    private void downloadVideoSync(final String id, final APVideoDownloadCallback callback, final APVideoDownloadRsp rsp)
    {
    	if (!id.contains("|"))
    	{
    		//throw new RuntimeException("download video id must be cloudid!");
    		Log.e(TAG, "download video id must be cloudid!");
    		return;
    	}
    	String vid = id.substring(0, id.indexOf('|'));
    	String vpath = VideoFileManager.getInstance().getVideoPath(id);

    	APFileReq req = new APFileReq();
    	req.setCloudId(vid);
    	req.setSavePath(vpath);
    	
    	mFileService.downLoadSync(req, new APFileDownCallback() {
			@Override
			public void onDownloadStart(APMultimediaTaskModel arg0) {
				if (callback != null)
				{
					callback.onDownloadStart(arg0);
				}
			}
			@Override
			public void onDownloadProgress(APMultimediaTaskModel arg0, int arg1,
					long arg2, long arg3) {
				if (callback != null)
				{
					callback.onDownloadProgress(arg0, arg1);
				}
			}
			@Override
			public void onDownloadFinished(APMultimediaTaskModel arg0,
					APFileDownloadRsp arg1) {
		    	//this downlaod may load shortvideo, I still insert it to db as album video, because it doesn't matter
				VideoFileManager.getInstance().insertRecord(arg1.getFileReq().getSavePath(), id.substring(0, id.indexOf('|')), "", VideoFileManager.TYPE_OTHERS);
				if (callback != null)
				{
					rsp.setRsp(arg1);
					callback.onDownloadFinished(rsp);
				}
			}
			@Override
			public void onDownloadError(APMultimediaTaskModel arg0,
					APFileDownloadRsp arg1) {
				if (callback != null)
				{
					rsp.setRsp(arg1);
					callback.onDownloadError(rsp);
				}
			}
			@Override
			public void onDownloadBatchProgress(APMultimediaTaskModel arg0, int arg1,
					int arg2, long arg3, long arg4) {
			}
		});
    }
    public void startPlay(String id, SightVideoPlayView playView)
    {
    	if (playView.isPlaying())
    	{
    		playView.stop();
    	}
    	playView.setVideoId(id);
    	playView.start();
    }

    //加载小视频，首先判断视频是否存在，如果存在，直接播放，否则首先下载缩略图，设置到playView上，之后再判断forceVideo，若为true就下载视频
    public void loadShortVideo(final String id,
					    		final SightVideoPlayView playView,
					    		Drawable defDrawable,
					    		final APVideoDownloadCallback callback,
					    		final boolean forceVideo)
    {
//    	Log.d(TAG, "loadShortVideo, id:" + id);
        String reuseTag = id;// todo: need use cache key, not id???
		ViewAssistant.getInstance().setViewTag(playView, reuseTag);
    	String tid = id;
    	if (PathUtils.extractFile(tid) != null)
    	{
    		tid = VideoFileManager.getInstance().getLocalIdByPath(tid);
    	}
    	if (isVideoAvailable(tid))
    	{
    		startPlay(tid, playView);
    		return;
    	}
    	if (!id.contains("|"))
    	{
    		//throw new RuntimeException("you have set a localid or path, but file missed!");
//    		Log.e(TAG, "you have set a localid or path, but file missed!");
    		return;
    	}
    	//video not available, draw default thumbnail at first
    	if (defDrawable instanceof BitmapDrawable)
    	{
    		BitmapDrawable drawable = (BitmapDrawable)defDrawable;
    		(playView).drawBitmap(drawable.getBitmap());
    	}

    	String jpath = VideoFileManager.getInstance().getThumbPath(id);
    	APFileReq req = new APFileReq();
    	req.setCloudId(id.substring(id.indexOf('|') + 1));
    	req.setSavePath(jpath);
    	if (mStatus.containsKey(id))
    	{
    		return;
    	}
    	
    	APVideoLoadStatus info = new APVideoLoadStatus();
    	info.mProgress = 0;
    	info.mStatus = APVideoLoadStatus.THUMBLOADING;
    	mStatus.put(id, info);

        final ViewWrapper viewWrapper = new ViewWrapper(playView, reuseTag);
    	
    	mFileService.downLoad(req, new APFileDownCallback() {
			@Override
			public void onDownloadStart(APMultimediaTaskModel arg0) {
			}
			@Override
			public void onDownloadProgress(APMultimediaTaskModel arg0, int arg1,
					long arg2, long arg3) {
			}
			
			//thumb download finished
			@Override
			public void onDownloadFinished(APMultimediaTaskModel arg0,
					APFileDownloadRsp arg1) {

				//check reuse and set ui
                boolean isReused = checkViewReused(viewWrapper);

                String path = arg1.getFileReq().getSavePath();
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                if (!isReused) {
                    (playView).drawBitmap(bitmap);
                }

				MemoryCache<Bitmap> cache = ImageCacheContext.get().getMemCache();
				cache.put(id, bitmap);
				
				VideoFileManager.getInstance().insertRecord(arg1.getFileReq().getSavePath(), id.substring(id.indexOf('|') + 1, id.length()), "", VideoFileManager.TYPE_OTHERS);

                //maybe we should add a callback onThumbDownloadFinish();
                if (isReused) {
                    arg1.setRetCode(APFileRsp.CODE_ERR_VIEW_REUSED);
                    APVideoDownloadRsp rsp = new APVideoDownloadRsp();
                    rsp.setRsp(arg1);
                    callback.onDownloadError(rsp);
                    mStatus.remove(id);
                    return;
                }

				if (callback != null)
				{
					APVideoDownloadRsp rsp = new APVideoDownloadRsp();
					rsp.setRsp(arg1);
					callback.onThumbDownloadFinished(rsp);
				}
	    		if (forceVideo)
	    			downloadVideo(id, new APVideoDownloadCallbackWrapper(callback, viewWrapper));
//	    		else {
//	    	    	APVideoLoadStatus info = mStatus.get(id);
//	    	    	info.mProgress = 100;
//	    	    	info.mStatus = APVideoLoadStatus.THUMBDONE;
//	    	    	mStatus.put(id, info);
//				}
	    		else {
	    			mStatus.remove(id);
				}
			}
			@Override
			public void onDownloadError(APMultimediaTaskModel arg0,
					APFileDownloadRsp arg1) {
				APVideoDownloadRsp rsp = new APVideoDownloadRsp();
				rsp.setRsp(arg1);
				mStatus.remove(id);
				if (callback != null)
				{
					callback.onDownloadError(rsp);
				}
			}
			@Override
			public void onDownloadBatchProgress(APMultimediaTaskModel arg0, int arg1,
					int arg2, long arg3, long arg4) {
			}
		});
    }
    public APVideoDownloadRsp loadShortVideoSync(final String id, final APVideoDownloadCallback callback)
    {
//    	Log.d(TAG, "loadShortVideoSync, id:" + id);
    	if (!id.contains("|"))
    	{
    		//throw new RuntimeException("you have set a localid or path, but file missed!");
//    		Log.e(TAG, "Illegal cloudid, do nothing!");
    		return null;
    	}
//    	if (isVideoAvailable(id))
//    	{
//    		return null;
//    	}

    	final APVideoDownloadRsp loadRsp = new APVideoDownloadRsp();
    	
    	String jpath = VideoFileManager.getInstance().getThumbPath(id);
    	APFileReq req = new APFileReq();
    	req.setCloudId(id.substring(id.indexOf('|') + 1));
    	req.setSavePath(jpath);
    	
    	mFileService.downLoadSync(req, new APFileDownCallback() {
			@Override
			public void onDownloadStart(APMultimediaTaskModel arg0) {
			}
			@Override
			public void onDownloadProgress(APMultimediaTaskModel arg0, int arg1,
					long arg2, long arg3) {
			}
			//thumb download finished
			@Override
			public void onDownloadFinished(APMultimediaTaskModel arg0,
					APFileDownloadRsp arg1) {
				if (callback != null)
				{
					APVideoDownloadRsp rsp = new APVideoDownloadRsp();
					rsp.setRsp(arg1);
					callback.onThumbDownloadFinished(rsp);
				}
	    		downloadVideoSync(id, callback, loadRsp);
			}
			@Override
			public void onDownloadError(APMultimediaTaskModel arg0,
					APFileDownloadRsp arg1) {
				APVideoDownloadRsp rsp = new APVideoDownloadRsp();
				rsp.setRsp(arg1);
				if (callback != null)
				{
					callback.onDownloadError(rsp);
				}
			}
			@Override
			public void onDownloadBatchProgress(APMultimediaTaskModel arg0, int arg1,
					int arg2, long arg3, long arg4) {
			}
		});
    	return loadRsp;
    }
    class APVideoDownloadCallbackWrapper implements APVideoDownloadCallback {

        private APVideoDownloadCallback callback;
        private ViewWrapper viewWrapper;
        public APVideoDownloadCallbackWrapper(APVideoDownloadCallback callback, ViewWrapper viewWrapper){
            this.callback = callback;
            this.viewWrapper = viewWrapper;
        }

        @Override
        public void onDownloadStart(APMultimediaTaskModel taskinfo) {
            if(this.callback != null) {
                this.callback.onDownloadStart(taskinfo);
            }
        }

        @Override
        public void onDownloadFinished(APVideoDownloadRsp rsp) {
            if(this.callback != null) {
                if(checkViewReused(this.viewWrapper)) {
                    rsp.setRetCode(APFileRsp.CODE_ERR_VIEW_REUSED);
                    this.callback.onDownloadError(rsp);
                } else {
                    this.callback.onDownloadFinished(rsp);
                }
            }
        }

        @Override
        public void onDownloadError(APVideoDownloadRsp rsp) {
            if(this.callback != null) {
                this.callback.onDownloadError(rsp);
            }
        }

        @Override
        public void onDownloadProgress(APMultimediaTaskModel taskInfo, int progress) {
            if(this.callback != null) {
                this.callback.onDownloadProgress(taskInfo, progress);
            }
        }

        @Override
        public void onThumbDownloadFinished(APVideoDownloadRsp rsp) {
            if(this.callback != null) {
                this.callback.onThumbDownloadFinished(rsp);
            }
        }
    }

    private boolean checkViewReused(ViewWrapper viewWrapper) {
        return ViewAssistant.getInstance().checkViewReused(viewWrapper);
    }

    //加载相册视频，只下载缩略图，之后当用户点击后，再调用downloadVideo下载视频
    public void loadAlbumVideo(String id, ImageView view, Drawable defDrawable, APImageDownLoadCallback callback)
    {
    	if (view == null)
    	{
    		return;
    	}
    	String tid = id;
		if (PathUtils.extractFile(tid) == null)
		{
			if (tid.contains("|"))
			{
				tid = tid.substring(tid.indexOf('|') + 1);
			}
			else {
				tid = VideoFileManager.getInstance().getThumbPath1(tid);
			}
		}
		else {
			tid = PathUtils.extractFile(tid).getAbsolutePath();
			if (tid.endsWith("mp4"))
			{
				String jpg = tid.substring(0, tid.indexOf('.')) + ".jpg";
				if (!new File(jpg).exists())
				{
					tid = getVideoThumbnail(tid);
				}
				else
				{
					tid = jpg;
				}
			}
		}
		Log.d(TAG, "loadAlbumVideo: " + tid);
    	mImageService.loadImage(tid, view, defDrawable, callback, view.getWidth(), view.getHeight(), null);
    }

    //**********************************************************************************************************************//

    //压缩视频，同步接口
    public APAlbumVideoInfo compressVideo(String path)
    {	
    	path = PathUtils.extractFile(path).getAbsolutePath();
    	FFmpegVideoInfo info = getVideoInfo(path);
    	APAlbumVideoInfo albuminfo = new APAlbumVideoInfo();
    	
        File rootDir = new File(VideoFileManager.mBaseDir);
        File outputFile = new File(rootDir, String.format("%d.mp4", System.currentTimeMillis()));
        if (!rootDir.mkdirs())
        {
        	Log.e("file", "mkdirs failure!");
        }

    	String destPath = outputFile.getAbsolutePath();
    	
		albuminfo.mDuration = info.duration;
		albuminfo.mPath = destPath;
		albuminfo.mId = destPath.substring(destPath.lastIndexOf('/') + 1, destPath.lastIndexOf('.'));
		
//		String jpath = getVideoThumbnail(path);
//		copyFile(jpath, destPath.substring(0, destPath.indexOf('.')) + ".jpg");
		
    	if (!needCompress(info))
    	{
    		long ts = System.currentTimeMillis();
    		copyFile(path, destPath);
//    		Log.d(TAG, "needCompress false, took " + (System.currentTimeMillis() - ts) + "ms" );
    	}
    	else
    	{
    		int destWidth = info.width;
    		int destHeigh = info.height;
    		final int threshold = 640 * 360;
    		boolean resize = info.width * info.height > threshold;
    		if (resize)
    		{
    			destWidth = (int)Math.sqrt(threshold * info.width / info.height);
    			destHeigh = destWidth * info.height / info.width;
    		}
    		FFmpegVideoCompressConfig config = new FFmpegVideoCompressConfig();
    		config.bitrate = BITRATE;
    		config.height = destHeigh;
    		config.width = destWidth;
    		config.inputPath = path;
    		config.outputPath = destPath;
    		long ts = System.currentTimeMillis();
    		FFmpegApi.videoCompress(config);
//    		Log.d(TAG, "videocompress took " + (System.currentTimeMillis() - ts) + "ms" );
    	}

		albuminfo.mSize = new File(destPath).length();
		albuminfo.mSuccess = true;
		
		//copy thumbnail
		String jpath = destPath.substring(0, destPath.lastIndexOf('.')) + ".jpg";
		copyFile(getVideoThumbnail(path), jpath);
		VideoFileManager.getInstance().insertRecord(destPath, "", albuminfo.mId, VideoFileManager.TYPE_OTHERS);
		VideoFileManager.getInstance().insertRecord(jpath, "", albuminfo.mId, VideoFileManager.TYPE_OTHERS);
		return albuminfo;
    }

    //检查视频是否可发送
    public boolean isVideoTransmissible(String path)
    {
//    	Log.d(TAG, "isVideoTransmissible: " + path);
    	FFmpegVideoInfo info = getVideoInfo(path);
    	if (!(info.audioEncodeId == info.AAC && info.videoEncodeId == info.H264))
    	{
    		return false;
    	}
    	float factor = info.videoBitrate / (BITRATE * 1.0f);
    	long size = new File(path).length();
    	return size / factor < 20 * 1024 * 1024 * 1.0f;
    }

    private FFmpegVideoInfo getVideoInfo(String path)
    {
    	return FFmpegApi.getVideoInfo(path);
    }
    private boolean needCompress(FFmpegVideoInfo info)
    {
    	return info.videoBitrate > BITRATE;
    }
    //优化滑动
    public void optimizeView(AbsListView listView, AbsListView.OnScrollListener listener)
    {
    	
    }

    public ArrayList<String> getRecentVideo(int day)
    {
		ArrayList<String> videoList = new ArrayList<String>();
		List<VideoCacheModel> models = VideoFileManager.getInstance().queryRecentVideo(day * 84600000L);
		if (models != null && !models.isEmpty()) {
			for(VideoCacheModel model : models) {
				if (model.path.endsWith("mp4") && !TextUtils.isEmpty(model.localId))
					videoList.add(model.localId);
			}
		}
    	return videoList;
    }
//    public void loadShortVideo(final String id, final SightPlayView playView, Drawable defDrawable)
//    {
//    	if (isVideoAvailable(id))
//    	{
//    		startPlay(id, playView);
//    		return;
//    	}
//    	APImageLoadRequest request = new APImageLoadRequest();
//    	request.defaultDrawable = defDrawable;
//    	request.displayer = new APDisplayer() {
//			@Override
//			public void display(View arg0, Drawable arg1, String arg2) {
//				Log.d(TAG, "display view: " + arg0);
//				//must take care of reuse
//				Bitmap bitmap = null;
//				if (arg1 instanceof BitmapDrawable)
//				{
//					bitmap = ((BitmapDrawable)arg1).getBitmap();
//				}
//				(playView).drawBitmap(bitmap);
//				
//				
//				downloadVideo(id, new APVideoDownloadCallback() {
//					@Override
//					public void onThumbDownloadFinished(APVideoDownloadRsp arg0) {
//					}
//					@Override
//					public void onDownloadStart(APMultimediaTaskModel arg0) {
//					}
//					@Override
//					public void onDownloadProgress(APMultimediaTaskModel arg0, int arg1) {
//					}
//					@Override
//					public void onDownloadFinished(APVideoDownloadRsp arg0) {
//						startPlay(id, playView);
//					}
//					@Override
//					public void onDownloadError(APVideoDownloadRsp arg0) {
//					}
//				});
//			}
//		};
//    	mImageService.loadImage(request);
//    }
    public APVideoLoadStatus getVideoLoadStatus(String id)
    {
    	return mStatus.get(id);
    }
    public String getVideoThumbnail(String path)
    {
    	if (PathUtils.extractFile(path) == null)
    	{
    		path = VideoFileManager.getInstance().getVideoPath1(path);
    	}
    	else {
			path = PathUtils.extractFile(path).getAbsolutePath();
		}
    	String result = "";
//    	MemoryCache<Bitmap> cache = ImageCacheContext.get().getMemCache();
    	Bitmap bitmap = null;
    	DiskCache<Bitmap> cache = ImageCacheContext.get().getDiskCache();
    	File cacheFile = cache.getFile(path);
    	if (cacheFile == null)
    	{
    		bitmap = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.MINI_KIND);
    		if (bitmap != null)
    		{
    			try {
    				cache.save(path, bitmap);
    				result = cache.getFile(path).getAbsolutePath();
				} catch (Exception e) {
//					Log.e(TAG, "cache.save error, key: " + path);
//					Log.e(TAG, "cache.save error:" + e.getMessage());
				}
    		}
    	}
    	else {
			result = cacheFile.getAbsolutePath();
		}
    	return result;
    }
    
    
    private boolean copyFile(String srcPath, String destPath) {
        //logger.d("copyFile from " + srcPath + " to " + destPath);
        if (TextUtils.isEmpty(srcPath) || TextUtils.isEmpty(destPath) || srcPath.equals(destPath)) {
            return false;
        }

        File src = new File(srcPath);
        File dst = new File(destPath);
        if ((src.exists() && src.isFile()) && (!dst.exists() || !dst.isFile())) {
            InputStream input = null;
            OutputStream out = null;
            try {
                dst.getParentFile().mkdirs();
                input = new FileInputStream(src);
                out = new FileOutputStream(dst);
                IOUtils.copy(input, out);
                return true;
            } catch (IOException e) {
//                Log.d(TAG, "copy file exception: " + e.getMessage());
//                Log.d(TAG, "copy file exception, srcPath:" + srcPath + ",destPath:" + destPath);
            } finally {
                IOUtils.closeQuietly(input);
                IOUtils.closeQuietly(out);
            }
        }
        return false;
    }
    public void burnFile(String id)
    {
    	VideoFileManager.getInstance().removeRecordById(id);
    	new File(VideoFileManager.getInstance().getThumbPath1(id)).delete();
    	new File(VideoFileManager.getInstance().getVideoPath1(id)).delete();
    }
    public void deleteShortVideo(String localid)
    {
    	VideoFileManager.getInstance().deleteByLocalId(localid);
    }
    public String getVideoPathById(String id)
    {
    	if (PathUtils.extractFile(id) != null)
    	{
    		return PathUtils.extractFile(id).getAbsolutePath();
    	}
    	return VideoFileManager.getInstance().getVideoPath1(id);
    }
    public void loadVideoThumb(String id, final View playView, Drawable defDrawable, APImageDownLoadCallback callback)
    {
    	String tid = id;
		if (PathUtils.extractFile(tid) == null)
		{
			if (tid.contains("|"))
			{
				tid = tid.substring(tid.indexOf('|') + 1);
			}
			else {
				tid = VideoFileManager.getInstance().getThumbPath1(tid);
			}
		}
		else {
			tid = PathUtils.extractFile(tid).getAbsolutePath();
			if (tid.endsWith("mp4"))
			{
				String jpg = tid.substring(0, tid.indexOf('.')) + ".jpg";
				if (!new File(jpg).exists())
				{
					tid = getVideoThumbnail(tid);
				}
				else
				{
					tid = jpg;
				}
			}
		}
//		Log.d(TAG, "loadVideoThumb thumbnail path:" + tid);

        String reuseTag = id;// todo: need use cache key, not id???

        ViewAssistant.getInstance().setViewTag(playView, reuseTag);

        final ViewWrapper viewWrapper = new ViewWrapper(playView, reuseTag);

		APImageLoadRequest request = new APImageLoadRequest();
		request.callback = callback;
    	//if (defDrawable == null || defDrawable instanceof BitmapDrawable)
    	{
    		///BitmapDrawable drawable = (BitmapDrawable)defDrawable;
    		request.defaultDrawable = defDrawable;
    		request.path = tid;
    		request.width = 0;
    		request.height = 0;
    		request.displayer = new APDisplayer() {
				@Override
				public void display(View view, Drawable drawable, String arg2) {
					Log.d(TAG, "display called###");
                    boolean isReused = checkViewReused(viewWrapper);
                    if (isReused) {
                        return;
                    }

					if (drawable instanceof BitmapDrawable)
					{
						Log.d(TAG, "drawBitmap");
						//should check reuse
						if (playView instanceof SightVideoPlayView)
						{
							((SightVideoPlayView)playView).drawBitmap(((BitmapDrawable) drawable).getBitmap());
						}
						else if (playView instanceof VideoPlayView)
						{
							((VideoPlayView)playView).drawBitmap(((BitmapDrawable) drawable).getBitmap());
						}
					}
				}
			};
    	}
    	mImageService.loadImage(request);
    }
}
