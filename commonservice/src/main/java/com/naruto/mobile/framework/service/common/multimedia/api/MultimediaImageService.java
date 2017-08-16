package com.naruto.mobile.framework.service.common.multimedia.api;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.AbsListView;
import android.widget.ImageView;

import java.util.List;

import com.naruto.mobile.base.serviceaop.service.ext.ExternalService;
import com.naruto.mobile.framework.service.common.multimedia.api.data.APMultimediaTaskModel;
import com.naruto.mobile.framework.service.common.multimedia.graphics.APImageDownLoadCallback;
import com.naruto.mobile.framework.service.common.multimedia.graphics.APImageUploadCallback;
import com.naruto.mobile.framework.service.common.multimedia.graphics.APImageUploadOption;
import com.naruto.mobile.framework.service.common.multimedia.graphics.ImageWorkerPlugin;
import com.naruto.mobile.framework.service.common.multimedia.graphics.data.APCacheBitmapReq;
import com.naruto.mobile.framework.service.common.multimedia.graphics.data.APImageLoadRequest;
import com.naruto.mobile.framework.service.common.multimedia.graphics.data.APImageOfflineDownloadReq;
import com.naruto.mobile.framework.service.common.multimedia.graphics.data.APImageOfflineDownloadRsp;
import com.naruto.mobile.framework.service.common.multimedia.graphics.data.APImageQuery;
import com.naruto.mobile.framework.service.common.multimedia.graphics.data.APImageQueryResult;
import com.naruto.mobile.framework.service.common.multimedia.graphics.data.APImageUpRequest;
import com.naruto.mobile.framework.service.common.multimedia.graphics.data.Size;
import com.naruto.mobile.framework.service.common.multimedia.graphics.load.DisplayImageOptions;

/**
 * 待实现接口：原图的上传下载、图片的裁剪、本地路径、url的图片load
 */
public abstract class MultimediaImageService extends ExternalService {

    @Override
    protected void onCreate(Bundle bundle) {
    }

    @Override
    protected void onDestroy(Bundle bundle) {
    }

    public abstract void cancelLoad(String taskId);

    public abstract void cancelUp(String taskId);

    public abstract APMultimediaTaskModel getLoadTaskStatus(String taskId);

    public abstract APMultimediaTaskModel getUpTaskStatus(String taskId);

    /**
     * 根据任务id注册下载回调
     */
    public abstract void registeLoadCallBack(String taskId, APImageDownLoadCallback callBack);

    /**
     * 根据任务id注销指定的下载回调 注：如果callback不为空则注销单个callback,否则注销此taskid下所有callback
     */
    public abstract void unregisteLoadCallBack(String taskId, APImageDownLoadCallback callBack);

    /**
     * 根据任务id注册上传回调
     */
    public abstract void registeUpCallBack(String taskId, APImageUploadCallback callBack);

    /**
     * 根据任务id注销指定的上传回调 注：如果callback不为空则注销单个callback,否则注销此taskid下所有callback
     */
    public abstract void unregisteUpCallBack(String taskId, APImageUploadCallback callBack);

    /**
     * 批量上传
     */
    public abstract List<APMultimediaTaskModel> batchUpLoad(List requestList, APImageUploadCallback callBack);

    /**
     * 批量下载
     */
    public abstract List<APMultimediaTaskModel> batchDownLoad(List requestList, APImageDownLoadCallback callBack);
    /**
     * 加载本地图片,仅从本地缓存取，不走网络下载
     * 注：
     * 1.此接口先从本地缓存中查找如果存在则直接返回；
     * 2.如果不存在，查看当前文件若存在，直接decode返回bitmap并加入缓存中
     * 3.业务使用此接口时需要自己开线程
     *
     * @param path
     * @param width
     * @param height
     * @return
     */
//    public abstract Bitmap loadLocalImage(String path,int width,int height);

    /**
     * path为本地路径或者http/https开头的url
     */
    public abstract APMultimediaTaskModel loadImage(String path, ImageView imageView, Drawable defaultImage);

    /**
     * path为本地路径或者http/https开头的url
     */
    public abstract APMultimediaTaskModel loadImage(String path, ImageView imageView, Drawable defaultImage,
            ImageWorkerPlugin plugin);

    /**
     * path为本地路径或者http/https开头的url
     */
    public abstract APMultimediaTaskModel loadImage(String path, APImageDownLoadCallback callback);

    /**
     * path为本地路径或者http/https开头的url
     */
    public abstract APMultimediaTaskModel loadImage(String path, APImageDownLoadCallback callback,
            ImageWorkerPlugin plugin);

    /**
     * path为本地路径或者http/https开头的url
     */
    public abstract APMultimediaTaskModel loadImage(String path, ImageView imageView, int width, int height);

    /**
     * path为本地路径或者http/https开头的url
     */
    public abstract APMultimediaTaskModel loadImage(String path, ImageView imageView, Drawable defaultImage,
            int width, int height);

    /**
     * path为本地路径或者http/https开头的url
     */
    public abstract APMultimediaTaskModel loadImage(String path, ImageView imageView, int defaultResId,
            int width, int height);

    /**
     * path为本地路径或者http/https开头的url
     *
     * @param isIdle 列表是否处于静止空闲中
     */
    public abstract APMultimediaTaskModel loadImage(String path, ImageView imageView, Drawable defaultImage,
            boolean isIdle, int width, int height);

    /**
     * 加载图片,指定高度宽度
     */
    public abstract APMultimediaTaskModel loadImage(String path, ImageView imageView, Drawable defaultImage,
            int width, int height, ImageWorkerPlugin plugin);

    /**
     * 加载本地的图片
     */
    public abstract APMultimediaTaskModel loadImage(String path, APImageDownLoadCallback callback,
            int width, int height);

    /**
     * 加载本地的图片
     */
    public abstract APMultimediaTaskModel loadImage(String path, APImageDownLoadCallback callback,
            int width, int height, ImageWorkerPlugin plugin);

    /**
     * 加载本地的图片
     */
    public abstract APMultimediaTaskModel loadImage(String path, ImageView imageView, Drawable defaultImage,
            APImageDownLoadCallback callback,
            int width, int height, ImageWorkerPlugin plugin);
//    /**
//     * 加载图片
//     * @param path
//     * @param imageView
//     * @param defaultImage
//     * @param callback
//     * @param width
//     * @param height
//     * @param plugin
//     * @param type
//     * @return
//     */
//    public abstract APMultimediaTaskModel loadImage(String path, ImageView imageView,Drawable defaultImage,
//                                                    APImageDownLoadCallback callback,
//                                                    int width, int height,ImageWorkerPlugin plugin, CutScaleType type);

    /**
     * 加载图片
     *
     * @param size 原图的尺寸，用于计算是否需要裁切
     */
    public abstract APMultimediaTaskModel loadImage(String path, ImageView imageView, Drawable defaultImage,
            APImageDownLoadCallback callback,
            int width, int height, ImageWorkerPlugin plugin, Size size);

    /**
     * 加载/下载图片，参数最全的接口，如果有特殊化需求请使用此方法
     *
     * @param req 请求的打包参数
     */
    public abstract APMultimediaTaskModel loadImage(APImageLoadRequest req);

    /**
     * 二进制数据获取图片
     */
    public abstract APMultimediaTaskModel loadImage(byte[] data, ImageView imageView, Drawable defaultImage,
            int width, int height, APImageDownLoadCallback callback,
            ImageWorkerPlugin plugin);

    /**
     * 下载原图, 注意：按照原图尺寸显示，一般进用于表情等小图片显示用
     */
    public abstract APMultimediaTaskModel loadOriginalImage(String path, ImageView imageView,
            Drawable defaultImage,
            APImageDownLoadCallback callback);

    /**
     * 下载原图，宽高均为0的话按原图尺寸显示
     */
    public abstract APMultimediaTaskModel loadOriginalImage(String path, ImageView imageView,
            Drawable defaultImage,
            int width, int height,
            APImageDownLoadCallback callback);
    /**
     * 将指定宽高的图片从path load到savePath存储
     * @param from
     * @param dest
     * @param width
     * @param height
     * @param callback
     * @return
     */
//    public abstract APMultimediaTaskModel saveImage(String from, String dest,
//            int width,int height, APImageDownLoadCallback callback);

    /**
     * 图片加载
     *
     * @param path      本地path、cloud id、url
     * @param imageView 待显示的ImageView
     * @param options   加载选项
     * @param callback  加载回调
     */
    public abstract APMultimediaTaskModel loadImage(String path, ImageView imageView, DisplayImageOptions options,
            APImageDownLoadCallback callback);

    /**
     * 加载带水印的图
     */
    public abstract APMultimediaTaskModel loadImageWithMark(String path, ImageView imageView,
            DisplayImageOptions options, APImageDownLoadCallback callback);
    /**
     * 另存原图
     * @param from
     * @param dest
     * @param width
     * @param height
     * @param callback
     * @return
     */
//    public abstract APMultimediaTaskModel saveOriginalImage(String from, String dest,
//            int width,int height, APImageDownLoadCallback callback);
    /**********************************以下为图片辅助相关的接口**********************************/

    /**
     * 计算目标裁切图宽高
     *
     * @param width  图片宽
     * @param height 图片高
     * @param maxLen 图片
     * @param path   图片本地路径
     */
    public abstract int[] calculateCutImageRect(int width, int height, int maxLen, String path);

    /**
     * 获取宽高
     *
     * @param path 图片本地路径
     */
    public abstract int[] calculateDesWidthHeight(String path);

    /**
     * 对View进行优化，目前只优化AbsListView
     *
     * @param view     待优化的AbsListView
     * @param listener 业务滚动监听器
     */
    public abstract void optimizeView(AbsListView view, AbsListView.OnScrollListener listener);

    /**
     * 对View进行优化，目前只优化AbsListView
     *
     * @param view          待优化的AbsListView
     * @param pauseOnScroll 列表滑动时是否暂停加载
     * @param pauseOnFling  列表fling时，是否暂停加载
     * @param listener      业务滚动监听器
     */
    public abstract void optimizeView(AbsListView view, boolean pauseOnScroll, boolean pauseOnFling,
            AbsListView.OnScrollListener listener);

    /**
     * 对ViewPager进行优化
     *
     * @param view          待优化的ViewPager
     * @param pauseOnScroll 滑动时是否暂停加载
     * @param listener      业务滚动监听器
     */
    public abstract void optimizeView(ViewPager view, boolean pauseOnScroll, ViewPager.OnPageChangeListener listener);

    /**
     * 图片缓存查询接口
     *
     * @param query 对应的查询条件，如：APImageOriginalQuery[原图]、APImageCacheQuery[缓存查询]、 APImageClearCacheQuery[缓存尺寸最大]
     * @return 查询结果
     */
    public abstract <T extends APImageQuery> APImageQueryResult<?> queryImageFor(T query);

    public abstract Bitmap zoomBitmap(Bitmap src, int targetWidth, int targetHeight);

    public abstract Bitmap loadCacheBitmap(APCacheBitmapReq req);
    /**********************************以下为图片上传相关的接口**********************************/

    /**
     * 提交离线下载任务到Django服务器
     */
    public abstract APImageOfflineDownloadRsp offlineDownload(APImageOfflineDownloadReq req);

    /**
     * 上传一张图片，不关心回调
     */
    public abstract APMultimediaTaskModel uploadImage(String filePath);

    /**
     * 上传一张图片，不关心回调
     */
    public abstract APMultimediaTaskModel uploadImage(String filePath, boolean isSync);

    /**
     * 上传图片，需要关心回调处理
     */
    public abstract APMultimediaTaskModel uploadImage(String filePath, APImageUploadCallback cb);

    /**
     * 上传图片，需要关心回调处理,并可以设置可选项
     */
    public abstract APMultimediaTaskModel uploadImage(String filePath, APImageUploadCallback cb,
            APImageUploadOption option);

    /**
     * 上传图片，不关心回调，可设置自己的可选项
     */
    public abstract APMultimediaTaskModel uploadImage(String filePath, APImageUploadOption option);

    /**
     * 上传二进制图片，不关心回调
     */
    public abstract APMultimediaTaskModel uploadImage(byte[] fileData);

    /**
     * 上传二进制图片，可以设置回调
     */
    public abstract APMultimediaTaskModel uploadImage(byte[] fileData, APImageUploadCallback cb);


    /**
     * 上传文件原图，不关心回调
     */
    public abstract APMultimediaTaskModel uploadOriginalImage(String filePath);

    /**
     * 同步上传文件原图，不关心回调
     */
    public abstract APMultimediaTaskModel uploadOriginalImage(String filePath, boolean isSync,
            APImageUploadCallback cb);

    /**
     * 上传文件原图，关心回调
     */
    public abstract APMultimediaTaskModel uploadOriginalImage(String filePath, APImageUploadCallback cb);

    /**
     * 上传图片，参数最全的接口，如果有特殊化需求请使用此方法
     */
    public abstract APMultimediaTaskModel uploadImage(APImageUpRequest req);

    /**
     * 根据原图下载地址获取本地对应的文件存储路径
     */
    public abstract String getOriginalImagePath(String url);

    /**
     * 根据源地址检查是否当前在网络任务中
     */
    @Deprecated
    public abstract boolean checkInNetTask(String key);

    public abstract Size getDjangoNearestImageSize(Size size);

    /**
     * 删除缓存
     *
     * @param path 图片的id or 路径 or url
     * @return 删除的数量
     */
    public abstract int deleteCache(String path);


}
