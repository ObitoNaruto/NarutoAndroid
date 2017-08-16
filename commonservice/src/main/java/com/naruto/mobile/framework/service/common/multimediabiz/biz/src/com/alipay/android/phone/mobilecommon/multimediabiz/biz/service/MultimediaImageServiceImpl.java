package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.widget.AbsListView;
import android.widget.ImageView;

import java.util.List;

import com.naruto.mobile.base.serviceaop.NarutoApplication;
import com.naruto.mobile.framework.service.common.multimedia.api.MultimediaImageService;
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
import com.naruto.mobile.framework.service.common.multimedia.graphics.data.APThumbnailBitmapReq;
import com.naruto.mobile.framework.service.common.multimedia.graphics.data.CutScaleType;
import com.naruto.mobile.framework.service.common.multimedia.graphics.data.Size;
import com.naruto.mobile.framework.service.common.multimedia.graphics.load.DisplayImageOptions;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.config.ConfigManager;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.ImageLoadEngine;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.service.graphics.APImageManager;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.service.graphics.APImageWorker;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.ImageUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Logger;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.PathUtils;

/**
 * 待实现接口：原图的上传下载、图片的裁剪、本地路径、url的图片load
 */
public class MultimediaImageServiceImpl extends MultimediaImageService {

    private static final String TAG = "MultimediaImageServiceImpl";

    private Context mContext;
    private APImageWorker mImageWorker;
    private int mLoadWidth = APImageLoadRequest.DEFAULT_LOAD_W;
    private int mLoadHeight = APImageLoadRequest.DEFAULT_LOAD_H;
    private final int mUpWidth = APImageUpRequest.DEFAULT_UP_W;
    private final int mUpHeight = APImageUpRequest.DEFAULT_UP_H;

    static {
//        JniFalconImg.initJni();
    }

    @Override
    protected void onCreate(Bundle bundle) {
        long start = System.currentTimeMillis();
        super.onCreate(bundle);
        this.mContext = NarutoApplication.getInstance().getApplicationContext();

        if (mContext != null) {
            DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
            mLoadWidth = Math.min(metrics.widthPixels, metrics.heightPixels) / 4;
            mLoadHeight = mLoadWidth;
        }
        //初始化配置信息
        ConfigManager.getInstance().updateConfig(true);
        Logger.P(TAG, "onCreate total cost: " + (System.currentTimeMillis() - start));
        //先加载ImageLoadHandler
    }

    /**
     * 单例模式获取Worker
     * @return
     */
    private APImageWorker getImageWorker() {
        if (mImageWorker == null) {
            synchronized (this) {
                if (mImageWorker == null) {
                    mImageWorker = APImageWorker.getInstance(mContext);
                }
            }
        }
        return mImageWorker;
    }

    @Override
    public void cancelLoad(String taskId) {
        getImageWorker().cancelLoad(taskId);
    }

    @Override
    public void cancelUp(String taskId) {

        getImageWorker().cancelUpLoad(taskId);
    }

    @Override
    public APMultimediaTaskModel getLoadTaskStatus(String taskId) {

        return getImageWorker().getTaskStatus(taskId);
    }

//    @Override
//    public APMultimediaTaskModel getLoadTaskStatusByCloudId(String cloudId) {
//        //todo:
//        return null;
//    }

    @Override
    public APMultimediaTaskModel getUpTaskStatus(String taskId) {

        return getImageWorker().getTaskStatus(taskId);
    }

//    @Override
//    public APMultimediaTaskModel getUpTaskStatusByCloudId(String cloudId) {
//        //todo:
//        return null;
//    }

    @Override
    public void registeLoadCallBack(String taskId, APImageDownLoadCallback callBack) {

        APImageManager.getInstance(mContext).registLoadCallback(taskId, callBack);
    }

    @Override
    public void unregisteLoadCallBack(String taskId, APImageDownLoadCallback callBack) {

        APImageManager.getInstance(mContext).unregistLoadCallback(taskId, callBack);
    }

    @Override
    public void registeUpCallBack(String taskId, APImageUploadCallback callBack) {

        APImageManager.getInstance(mContext).registUploadCallback(taskId, callBack);
    }

    @Override
    public void unregisteUpCallBack(String taskId, APImageUploadCallback callBack) {

        APImageManager.getInstance(mContext).unregistUploadCallback(taskId, callBack);
    }

    @Override
    public List<APMultimediaTaskModel> batchUpLoad(List requestList, APImageUploadCallback callBack) {
        return getImageWorker().batchUpImage(requestList);
    }

    @Override
    public List<APMultimediaTaskModel> batchDownLoad(List requestList, APImageDownLoadCallback callBack) {
        return getImageWorker().batchLoadImage(requestList);
    }

//    @Override
//    public Bitmap loadLocalImage(String path, int width, int height) {
//        return getImageWorker().loadLocalImage(path, width, height);
//    }

    @Override
    public APMultimediaTaskModel loadImage(String path, ImageView imageView, Drawable defaultImage) {
//        int[] wh = ImageUtils.guessPreferRect(imageView, mLoadWidth, mLoadHeight);
//        return loadImage(path,imageView,defaultImage,null,wh[0],wh[1],null);
//        return loadImage(path, imageView, defaultImage, null, mLoadWidth, mLoadWidth, null);
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .imageScaleType(CutScaleType.SCALE_AUTO_LIMIT)
                .width(mLoadWidth)
                .height(mLoadHeight)
                .showImageOnLoading(defaultImage)
                .build();
        return getImageWorker().loadImageAction(path, imageView, options, null);
    }

    @Override
    public APMultimediaTaskModel loadImage(String path, ImageView imageView, Drawable defaultImage,
            ImageWorkerPlugin plugin) {
//        int[] wh = ImageUtils.guessPreferRect(imageView, mLoadWidth, mLoadHeight);
//        return loadImage(path,imageView,defaultImage,null,wh[0],wh[1],plugin);
//        return loadImage(path, imageView, defaultImage, null, mLoadWidth, mLoadHeight, plugin);
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .imageScaleType(CutScaleType.SCALE_AUTO_LIMIT)
                .width(mLoadWidth)
                .height(mLoadHeight)
                .showImageOnLoading(defaultImage)
                .setProcessor(plugin)
                .build();
        return getImageWorker().loadImageAction(path, imageView, options, null);
    }

    @Override
    public APMultimediaTaskModel loadImage(String path, APImageDownLoadCallback callback) {
//        return loadImage(path, null, null, callback, mLoadWidth, mLoadHeight, null);
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .imageScaleType(CutScaleType.SCALE_AUTO_LIMIT)
                .width(mLoadWidth)
                .height(mLoadHeight)
                .build();
        return getImageWorker().loadImageAction(path, null, options, callback);
    }

    @Override
    public APMultimediaTaskModel loadImage(String path, APImageDownLoadCallback callback, ImageWorkerPlugin plugin) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .imageScaleType(CutScaleType.SCALE_AUTO_LIMIT)
                .width(mLoadWidth)
                .height(mLoadHeight)
                .setProcessor(plugin)
                .build();
        return getImageWorker().loadImageAction(path, null, options, callback);
    }

    @Override
    public APMultimediaTaskModel loadImage(String path, ImageView imageView, int width, int height) {
        return loadImage(path, imageView, null, null, width, height, null);
    }

    @Override
    public APMultimediaTaskModel loadImage(String path, ImageView imageView, Drawable defaultImage, int width,
            int height) {
        return loadImage(path, imageView, defaultImage, null, width, height, null);
    }

    @Override
    public APMultimediaTaskModel loadImage(String path, ImageView imageView, int defaultResId, int width, int height) {
        Drawable defaultImage = null;
        if (defaultResId > 0) {
            defaultImage = mContext.getResources().getDrawable(defaultResId);
        }
        return loadImage(path, imageView, defaultImage, null, width, height, null);
    }

    @Override
    public APMultimediaTaskModel loadImage(String path, ImageView imageView, Drawable defaultImage, boolean isIdle,
            int width, int height) {
        return getImageWorker().loadImageAction(false, ImageLoadEngine.TYPE_NORMAL,
                isIdle, path, imageView, defaultImage, width, height, null, null, null, false);
    }

    @Override
    public APMultimediaTaskModel loadImage(String path, ImageView imageView, Drawable defaultImage, int width,
            int height, ImageWorkerPlugin plugin) {
        return loadImage(path, imageView, defaultImage, null, width, height, plugin);
    }

    @Override
    public APMultimediaTaskModel loadImage(String path, APImageDownLoadCallback callback, int width, int height) {
        return loadImage(path, null, null, callback, width, height, null);
    }

    @Override
    public APMultimediaTaskModel loadImage(String path, APImageDownLoadCallback callback, int width, int height,
            ImageWorkerPlugin plugin) {
        return loadImage(path, null, null, callback, width, height, plugin);
    }

    @Override
    public APMultimediaTaskModel loadImage(String path, ImageView imageView, Drawable defaultImage,
            APImageDownLoadCallback callback, int width, int height,
            ImageWorkerPlugin plugin) {
        return loadImage(path, imageView, defaultImage, callback, width, height, plugin, null);
    }

    @Override
    public APMultimediaTaskModel loadImage(String path, ImageView imageView, Drawable defaultImage,
            APImageDownLoadCallback callback, int width, int height,
            ImageWorkerPlugin plugin, Size size) {
        //TODO 像isSync特定参数如果需要设置下的话请用最全的接口
        return getImageWorker().loadImageAction(false, ImageLoadEngine.TYPE_NORMAL, true, path, imageView,
                defaultImage, width, height, callback, plugin, null, false, size);
    }

//    @Override
//    public APMultimediaTaskModel loadImage(String path, ImageView imageView, Drawable defaultImage,
//                                                    APImageDownLoadCallback callback, int width, int height,
//                                           ImageWorkerPlugin plugin, Size size) {
//        return loadImage(path, imageView, defaultImage, callback, width, height, plugin, size);
//    }

    @Override
    public APMultimediaTaskModel loadImage(APImageLoadRequest req) {
        if (req == null) {
            return null;
        }

        APMultimediaTaskModel ret = null;

        //APImageLoadRequest的loadType默认值是TYPE_NORMAL
        if (req.loadType == APImageLoadRequest.TYPE_NORMAL
                || req.loadType == APImageLoadRequest.TYPE_DJANGO
                || req.loadType == APImageLoadRequest.TYPE_ASSET) {//normal，Django，asset模式的数据
            //下载/上传图片的工作单元
            ret = getImageWorker().loadImageAction(req.isSync, req.loadType, true,
                    req.path, req.imageView, req.defaultDrawable,
                    req.width, req.height, req.callback, req.plugin, req.displayer, req.isBackground);
        } else if (req.loadType == APImageLoadRequest.TYPE_DATA) {//数据流的模式
            ret = getImageWorker().loadDataImage(req.isSync,
                    req.data, req.imageView, req.defaultDrawable,
                    req.width, req.height, req.callback, req.plugin, req.displayer, req.imageSize);
        }

        return ret;
    }

    @Override
    public APMultimediaTaskModel loadImage(byte[] data, ImageView imageView, Drawable defaultImage, int width,
            int height, APImageDownLoadCallback callback, ImageWorkerPlugin plugin) {

        return mImageWorker
                .loadDataImage(false, data, imageView, defaultImage, width, height, callback, plugin, null, null);
    }

    @Override
    public APMultimediaTaskModel loadImage(String path, ImageView imageView, DisplayImageOptions options, APImageDownLoadCallback callback) {
        return getImageWorker().loadImageAction(path, imageView, options, callback);
    }

    @Override
    public APMultimediaTaskModel loadImageWithMark(String path, ImageView imageView, DisplayImageOptions options,
            APImageDownLoadCallback callback) {
        return getImageWorker().loadImageWithMark(path, imageView, options, callback);
    }

    @Override
    public APMultimediaTaskModel loadOriginalImage(String path, ImageView imageView, Drawable defaultImage,
            APImageDownLoadCallback callback) {
        //width and height of original image should be 0
        return getImageWorker().loadImageAction(false, ImageLoadEngine.TYPE_ORIGINAL, true, path,
                imageView, defaultImage, APImageLoadRequest.ORIGINAL_WH, APImageLoadRequest.ORIGINAL_WH,
                callback, null, null, false);
    }

    @Override
    public APMultimediaTaskModel loadOriginalImage(String path, ImageView imageView,
            Drawable defaultImage,
            int width, int height,
            APImageDownLoadCallback callback) {
        return getImageWorker().loadImageAction(false, ImageLoadEngine.TYPE_ORIGINAL, true, path,
                imageView, defaultImage, width, height, callback, null, null, false);
    }

//    @Override
//    public APMultimediaTaskModel saveImage(String from, String dest, int width, int height,
//            APImageDownLoadCallback callback) {
//        return APImageWorker.getInstance(mContext)
//                .saveImage(from, dest, width, height, ImageLoadEngine.TYPE_NORMAL, callback);
//    }

//    @Override
//    public APMultimediaTaskModel saveOriginalImage(String from, String dest, int width, int height,
//            APImageDownLoadCallback callback) {
//        return APImageWorker.getInstance(mContext)
//                .saveImage(from, dest, width, height, ImageLoadEngine.TYPE_ORIGINAL, callback);
//    }

    @Override
    public APMultimediaTaskModel uploadImage(String filePath) {
        return uploadImage(filePath, null, null);
    }

    @Override
    public APMultimediaTaskModel uploadImage(String filePath, boolean isSync) {
        return getImageWorker().uploadImage(isSync, filePath, mUpWidth, mUpHeight, null, null);
    }

    @Override
    public APMultimediaTaskModel uploadImage(String filePath, APImageUploadCallback cb) {
        return uploadImage(filePath, cb, null);
    }

    @Override
    public APMultimediaTaskModel uploadImage(String filePath, APImageUploadCallback cb, APImageUploadOption option) {

        //TODO 像isSync特定参数如果需要设置下的话请用最全的接口
        return getImageWorker().uploadImage(false, filePath, -1, -1, cb, option);
    }

    @Override
    public APMultimediaTaskModel uploadImage(String filePath, APImageUploadOption option) {
        return uploadImage(filePath, null, option);
    }

    @Override
    public APMultimediaTaskModel uploadImage(byte[] fileData) {
        return uploadImage(fileData, null);
    }

    @Override
    public APMultimediaTaskModel uploadImage(byte[] fileData, APImageUploadCallback cb) {

        return getImageWorker().uploadImage(false, fileData, cb);
    }

    @Override
    public APMultimediaTaskModel uploadOriginalImage(String filePath) {
        return uploadOriginalImage(filePath, null);
    }

    @Override
    public APMultimediaTaskModel uploadOriginalImage(String filePath, boolean isSync, APImageUploadCallback cb) {
        return getImageWorker().uploadOriginalImage(isSync, filePath, cb);
    }

    @Override
    public APMultimediaTaskModel uploadOriginalImage(String filePath, APImageUploadCallback cb) {
        return getImageWorker().uploadOriginalImage(false, filePath, cb);
    }

    @Override
    public APMultimediaTaskModel uploadImage(APImageUpRequest req) {
        if (req == null) {
            return null;
        }

        APMultimediaTaskModel ret = null;

        if (req.uploadType == APImageUpRequest.TYPE_ORIGINAL) {
            ret = getImageWorker().uploadOriginalImage(req.isSync,
                    req.path, req.callback);
        } else if (req.uploadType == APImageUpRequest.TYPE_HIGH
                || req.uploadType == APImageUpRequest.TYPE_MIDDLE
                || req.uploadType == APImageUpRequest.TYPE_LOW
                || req.uploadType == APImageUpRequest.TYPE_DEFAULT) {
            //上传二进制图片
            if (req.fileData != null && req.fileData.length > 0) {
                //上传二进制图片
                ret = getImageWorker().uploadImage(req.isSync,
                        req.fileData, req.callback);
            } else {
                //上传文件图片
                ret = getImageWorker().uploadImage(req.isSync,
                        req.path, req.width, req.height, req.callback, req.option);
            }
        }

        return ret;
    }

    @Override
    public int[] calculateCutImageRect(int width, int height, int maxLen, String path) {
        return APImageWorker.calculateCutImageRect(width, height, maxLen, path);
//        return APImageWorker.calculateCutImageRect(width, height, maxLen, null);
    }

    @Override
    public int[] calculateDesWidthHeight(String path) {
        return APImageWorker.calculateDesWidthHeight(path);
    }

    @Override
    public void optimizeView(AbsListView view, AbsListView.OnScrollListener listener) {
        optimizeView(view, false, true, listener);
    }

    @Override
    public void optimizeView(AbsListView view, boolean pauseOnScroll, boolean pauseOnFling,
            AbsListView.OnScrollListener listener) {
        getImageWorker().optimizeView(view, pauseOnScroll, pauseOnFling, listener);
    }

    @Override
    public void optimizeView(ViewPager view, boolean pauseOnScroll, ViewPager.OnPageChangeListener listener) {
        getImageWorker().optimizeView(view, pauseOnScroll, listener);
    }

    @Override
    public String getOriginalImagePath(String url) {
        return getImageWorker().getOriginalImagePath(url);
    }

    @Override
    public boolean checkInNetTask(String key) {
        return getImageWorker().checkInNetTask(key);
    }

    @Override
    public Size getDjangoNearestImageSize(Size size) {
        return PathUtils.getDjangoNearestImageSize(size);
    }

    @Override
    public <T extends APImageQuery> APImageQueryResult<?> queryImageFor(T query) {
        return getImageWorker().queryImageFor(query);
    }

    @Override
    public Bitmap zoomBitmap(Bitmap src, int targetWidth, int targetHeight) {
        return ImageUtils.zoomBitmap(src, targetWidth, targetHeight);
    }

    @Override
    public APImageOfflineDownloadRsp offlineDownload(APImageOfflineDownloadReq req) {
        return getImageWorker().offlineDownload(req);
    }

    @Override
    public Bitmap loadCacheBitmap(APCacheBitmapReq req) {
        long start = System.currentTimeMillis();
        Bitmap bitmap = null;
        if (req instanceof APThumbnailBitmapReq) {
            bitmap = getImageWorker().loadCacheBitmap((APThumbnailBitmapReq) req);
        } else {
            bitmap = getImageWorker().loadCacheBitmap(req);
        }
        Logger.TIME("loadCacheBitmap costTime: "
                + (System.currentTimeMillis() - start)
                + ", " + req);
        return bitmap;
    }

    @Override
    public int deleteCache(String path) {
        return getImageWorker().deleteCache(path);
    }
}
