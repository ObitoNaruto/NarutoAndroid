package com.naruto.mobile.framework.service.common.multimedia.api;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;

import com.naruto.mobile.base.serviceaop.service.ext.ExternalService;
import com.naruto.mobile.framework.service.common.multimedia.graphics.APImageDownLoadCallback;
import com.naruto.mobile.framework.service.common.multimedia.video.APVideoDownloadCallback;
import com.naruto.mobile.framework.service.common.multimedia.video.APVideoUploadCallback;
import com.naruto.mobile.framework.service.common.multimedia.video.data.APAlbumVideoInfo;
import com.naruto.mobile.framework.service.common.multimedia.video.data.APVideoDownloadRsp;
import com.naruto.mobile.framework.service.common.multimedia.video.data.APVideoUploadRsp;
import com.naruto.mobile.framework.service.common.multimedia.video.data.CameraParams;
import com.naruto.mobile.framework.service.common.multimedia.widget.SightCameraView;
import com.naruto.mobile.framework.service.common.multimedia.widget.SightPlaySurfaceView;
import com.naruto.mobile.framework.service.common.multimedia.widget.SightPlayView;
import com.naruto.mobile.framework.service.common.multimedia.widget.SightVideoPlayView;
import com.naruto.mobile.framework.service.common.multimedia.widget.VideoPlayView;


/**
 *
 * 寰呭疄鐜版帴鍙ｏ細瑙嗛鐨勪笂浼犱笅杞� * Created by xiangui.fxg on 2015/4/15.
 */
public abstract class MultimediaVideoService extends ExternalService {

    @Override
    protected void onCreate(Bundle bundle) {

    }

    @Override
    protected void onDestroy(Bundle bundle) {

    }
    
    public abstract SightCameraView createCameraView(Context context, CameraParams params);
    public abstract SightCameraView createCameraView(Context context);
    
    public abstract SightPlayView createPlayView(Context context, CameraParams params);
    public abstract SightPlayView createPlayView(Context context);
    
    public abstract SightPlaySurfaceView createPlaySurfaceView(Context context, CameraParams params);
    public abstract SightPlaySurfaceView createPlaySurfaceView(Context context);

    public abstract VideoPlayView.VideoSurfaceView createVideoPlayView(Context context, CameraParams params);
    public abstract VideoPlayView.VideoSurfaceView createVideoPlayView(Context context);
    
    //**********************************************************************************************************************//
    //上传小视频，先上传缩略图，再上传视频
    public abstract APVideoUploadRsp uploadShortVideoSync(String id, APVideoUploadCallback callback);
    
    //public abstract void uploadShortVideo(final String id, final VideoPlayView view, final APVideoUploadCallback callback);
    
    //上传相册视频，先上传缩略图，再上传视频
    public abstract void uploadAlbumVideo(String id, APVideoUploadCallback callback);
    public abstract APVideoUploadRsp uploadAlbumVideoSync(String id, APVideoUploadCallback callback);
    //**********************************************************************************************************************//
    //判断视频是否已经下载
    public abstract boolean isVideoAvailable(String id);
    //下载视频文件，小视频和相册视频通用
    public abstract void downloadVideo(String id, APVideoDownloadCallback callback);
    
    //加载小视频，首先下载缩略图，设置到playView上，之后再判断forceVideo，若为true就下载视频
    public abstract void loadShortVideo(String id, SightVideoPlayView playView, Drawable defDrawable, APVideoDownloadCallback callback, boolean forceVideo);
    public abstract APVideoDownloadRsp loadShortVideoSync(String id, APVideoDownloadCallback callback);
    //public abstract VideoLoadInfo getVideoLoadStatus(String id);
    
    //public abstract void loadShortVideo(String id, VideoPlayView playView, Drawable defDrawable);
    //小视频存在，直接播放
    public abstract void startPlay(String id, SightVideoPlayView playView);
    
    //加载相册视频，只下载缩略图，之后当用户点击后，再调用downloadVideo下载视频
    public abstract void loadAlbumVideo(String id, ImageView view, Drawable defDrawable, APImageDownLoadCallback callback);
    
    //**********************************************************************************************************************//
    
    //得到相册中视频的大小、长度、尺寸等信息
    //public abstract APAlbumVideoInfo getAlbumVideoInfo(String path);
    public abstract String getVideoThumbnail(String id);
    //压缩视频
    public abstract APAlbumVideoInfo compressVideo(String path);
    //检查视频是否可发送
    public abstract boolean isVideoTransmissible(String path);
    //优化滑动
    public abstract void optimizeView(AbsListView listView, AbsListView.OnScrollListener listener);
    
    //获取本地录制的小视频
    public abstract ArrayList<String> getRecentVideo(int day);
    
    public abstract void burnFile(String path);
    
    public abstract String getVideoPathById(String id);
    
    public abstract void deleteShortVideo(String localid);
    
    public abstract void loadVideoThumb(String id, View view, Drawable defDrawable, APImageDownLoadCallback callback);
    //public abstract void loadShortVideoThumb(String id, SightVideoPlayView view, Drawable defDrawable, APImageDownLoadCallback callback);
}
