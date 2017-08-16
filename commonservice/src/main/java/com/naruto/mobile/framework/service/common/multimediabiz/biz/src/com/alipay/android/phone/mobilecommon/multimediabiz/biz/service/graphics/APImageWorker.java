package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.service.graphics;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.widget.AbsListView;
import android.widget.ImageView;
//import com.googlecode.androidannotations.api.BackgroundExecutor;
import com.naruto.mobile.framework.service.common.multimedia.api.data.APMultimediaTaskModel;
import com.naruto.mobile.framework.service.common.multimedia.graphics.APDisplayer;
import com.naruto.mobile.framework.service.common.multimedia.graphics.APImageDownLoadCallback;
import com.naruto.mobile.framework.service.common.multimedia.graphics.APImageUploadCallback;
import com.naruto.mobile.framework.service.common.multimedia.graphics.APImageUploadOption;
import com.naruto.mobile.framework.service.common.multimedia.graphics.ImageWorkerPlugin;
import com.naruto.mobile.framework.service.common.multimedia.graphics.data.APCacheBitmapReq;
import com.naruto.mobile.framework.service.common.multimedia.graphics.data.APImageBigQuery;
import com.naruto.mobile.framework.service.common.multimedia.graphics.data.APImageCacheQuery;
import com.naruto.mobile.framework.service.common.multimedia.graphics.data.APImageClearCacheQuery;
import com.naruto.mobile.framework.service.common.multimedia.graphics.data.APImageDownloadRsp;
import com.naruto.mobile.framework.service.common.multimedia.graphics.data.APImageLoadRequest;
import com.naruto.mobile.framework.service.common.multimedia.graphics.data.APImageOfflineDownloadReq;
import com.naruto.mobile.framework.service.common.multimedia.graphics.data.APImageOfflineDownloadRsp;
import com.naruto.mobile.framework.service.common.multimedia.graphics.data.APImageOriginalQuery;
import com.naruto.mobile.framework.service.common.multimedia.graphics.data.APImageQuery;
import com.naruto.mobile.framework.service.common.multimedia.graphics.data.APImageQueryResult;
import com.naruto.mobile.framework.service.common.multimedia.graphics.data.APImageRetMsg;
import com.naruto.mobile.framework.service.common.multimedia.graphics.data.APImageSourceCutQuery;
import com.naruto.mobile.framework.service.common.multimedia.graphics.data.APImageThumbnailQuery;
import com.naruto.mobile.framework.service.common.multimedia.graphics.data.APImageUpRequest;
import com.naruto.mobile.framework.service.common.multimedia.graphics.data.APThumbnailBitmapReq;
import com.naruto.mobile.framework.service.common.multimedia.graphics.data.CutScaleType;
import com.naruto.mobile.framework.service.common.multimedia.graphics.data.Size;
import com.naruto.mobile.framework.service.common.multimedia.graphics.load.DisplayImageOptions;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.BitmapCacheLoader;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.disc.ImageCacheManager;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.ImageLoadEngine;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.ImageLoadReq;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.ImageOfflineDownloadHandler;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.ImageUpHandler;
//import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.assist.FalconFacade;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.assist.ImageDisplayUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.assist.ViewAssistant;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.listener.PauseOnPageChangeListener;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.listener.PauseOnScrollListener;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.task.ImageDisplayTask;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.task.ImageLoadTask;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.task.ImageNetTask;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.task.ImageTaskEngine;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.manager.APMultimediaTaskManager;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.AppUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.CacheUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.ImageUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Logger;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.MarkUtil;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.PathUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.AppUtils.*;

/**
 * 下载/上传图片的工作单元
 */
public class APImageWorker {
    private final String TAG = "APImageWorker";
    private static APImageWorker mInstance = null;
    private static String USERLEAVEHINT_ACTION = "com.alipay.mobile.framework.USERLEAVEHINT";

    private Logger logger = Logger.getLogger(TAG);

    private ImageLoadEngine mEngine;//Image加载操作类
    //这里可以用于存储全局ApplicationContext
    private Context mContext = null;

    private ImageCacheManager mImageCacheManager;

    private APImageWorker(Context context) {
//        long start = System.currentTimeMillis();
        mContext = context;
        mEngine = ImageLoadEngine.getInstance();
        //registUserLeaveHintReceiver();
//        Logger.P("MultimediaImageServiceImpl", "APImageWorker finish new cost: " + (System.currentTimeMillis() - start));
    }

    public static APImageWorker getInstance(Context context) {
//        long start = System.currentTimeMillis();
        if (mInstance == null) {
            synchronized (APImageWorker.class) {
//                Logger.P("MultimediaImageServiceImpl", "APImageWorker getInstance synchronized cost: " + (System.currentTimeMillis()-start));
//                start = System.currentTimeMillis();
                if (mInstance == null) {
                    mInstance = new APImageWorker(context);
//                    Logger.P("MultimediaImageServiceImpl", "APImageWorker synchronized new cost: " + (System.currentTimeMillis()-start));
//                    start = System.currentTimeMillis();
                }
//                Logger.P("MultimediaImageServiceImpl", "APImageWorker new cost: " + (System.currentTimeMillis()-start));
            }
        }
//        Logger.P("MultimediaImageServiceImpl", "APImageWorker getInstance cost: " + (System.currentTimeMillis()-start));

        return mInstance;
    }

    public ImageCacheManager getImageCacheManager() {
        if (mImageCacheManager == null) {
            synchronized (this) {
                if (mImageCacheManager == null) {
                    mImageCacheManager = new ImageCacheManager();
                }
            }
        }
        return mImageCacheManager;
    }

    public APMultimediaTaskModel getTaskStatus(String taskId) {
        return APMultimediaTaskManager.getInstance(mContext).getTaskRecord(taskId);
    }

    public APMultimediaTaskModel cancelLoad(String taskId) {
        if (TextUtils.isEmpty(taskId)) {
            logger.d("cancelLoad taskId is null");
            return null;
        }

        ImageNetTask task = ImageTaskEngine.get().cancelTask(taskId);
        return task == null ? null : task.loadReq.taskModel;
    }


    private Bitmap loadFromMemCache(ImageLoadReq req) {
        if (TextUtils.isEmpty(req.path) || !mEngine.hasInitCacheLoader()) return null;
        BitmapCacheLoader loader = mEngine.getCacheLoader();
        Bitmap pre = ImageDisplayUtils.getReusableBitmap(req.imageView);
        Bitmap cache = loader.getMemCache(req.cacheKey, pre);

        logger.p("loadFromMemCache pre: " + pre + ", cache: " + cache + ", cacheKey: " + req.cacheKey);
        return cache;
    }

    /**
     * 从django服务端获取图片
     *
     * @param isSync 是否同步
     * @param loadType load的图片源类型
     * @param isIdle ?
     * @param path 下载地址或路径
     * @param imageView ImageView组件
     * @param width 请求图片宽度
     * @param height 请求图片高度
     * @param callback 下载的回调接口
     * @param plugin 图片处理插件
     * @param displayer 回调
     * @param isBackground ?
     * @return
     */
    public APMultimediaTaskModel loadImageAction(boolean isSync, int loadType, boolean isIdle,
            String path, final ImageView imageView,
            final Drawable defaultImage,
            int width, int height,
            APImageDownLoadCallback callback,
            ImageWorkerPlugin plugin,
            APDisplayer displayer,
            boolean isBackground) {
        return loadImageAction(isSync, loadType, isIdle, path, imageView, defaultImage, width, height, callback, plugin,
                displayer, isBackground, null);
    }

    /**
     * 从django服务端获取图片
     *
     * @param isSync
     * @param loadType
     * @param path
     * @param imageView
     * @param width
     * @param height
     * @param callback
     * @param plugin
     * @param size  图片宽高信息
     * @return
     */
    public APMultimediaTaskModel loadImageAction(boolean isSync, int loadType, boolean isIdle,
            String path, final ImageView imageView,
            final Drawable defaultImage,
            int width, int height,
            APImageDownLoadCallback callback,
            ImageWorkerPlugin plugin,
            APDisplayer displayer,
            boolean isBackground, Size size) {
        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder()
                                            .syncLoading(isSync)
                                            .showImageOnLoading(defaultImage)
                                            .setProcessor(plugin)
                                            .displayer(displayer)
                                            .originalSize(size)
                                            .width(width)
                                            .height(height);
        //如果加载图来源是原图，设置图片不缩放模式
        if (loadType == ImageLoadEngine.TYPE_ORIGINAL) {
            builder.imageScaleType(CutScaleType.NONE);
        }

        //创建获取配置
        DisplayImageOptions options = builder.build();

        //包装成图片加载请求
        ImageLoadReq req = new ImageLoadReq(mEngine, path, imageView, callback, options);
        return loadImageAction(req);
    }

    public APMultimediaTaskModel loadImageAction(ImageLoadReq req) {
        req.startTime = System.currentTimeMillis();
        logger.p("loadImageAction req: " + req);
        //请求未带图片路径
        if (TextUtils.isEmpty(req.source)) {
            if (req.imageView != null) {
                //设置tag
                ViewAssistant.getInstance().setViewTag(req.imageView, req.cacheKey);
            }
            ImageDisplayTask displayTask = new ImageDisplayTask(req);
            displayTask.syncRunTask();
            Logger.TIME("loadImageAction empty path cost: " + (System.currentTimeMillis() - req.startTime));
            return null;
        }
        Bitmap cache = loadFromMemCache(req);
        if (ImageUtils.checkBitmap(cache)) {
            logger.d("loadImageAction from cache: " + cache + ", cacheKey: " + req.cacheKey);
            ImageDisplayTask displayTask = new ImageDisplayTask(cache, req);
            displayTask.syncRunTask();
//            ViewAssistant.getInstance().removeViewTag(req.imageView);
            Logger.TIME("loadImageAction from memCache cost: " + (System.currentTimeMillis() - req.startTime));
            return null;
        }
        APMultimediaTaskModel taskStatus = new APMultimediaTaskModel();
        req.taskModel = taskStatus;
//        APMultimediaTaskManager.getInstance(mContext).addTaskRecord(taskStatus);
        ImageLoadTask task = new ImageLoadTask(req);
        mEngine.submit(task);
        Logger.TIME("loadImageAction submit task cost: " + (System.currentTimeMillis()-req.startTime));
        return taskStatus;
    }

    public APMultimediaTaskModel loadImageAction(String path, ImageView imageView, DisplayImageOptions options,
                                                 APImageDownLoadCallback callback) {
        ImageLoadReq req = new ImageLoadReq(mEngine, path, imageView, callback, options);
        return loadImageAction(req);
    }

    public APMultimediaTaskModel loadImageWithMark(String path, ImageView imageView, DisplayImageOptions options,
            APImageDownLoadCallback callback) {
        boolean ret = MarkUtil.isValidMarkOption(options);
        if (ret) {
            return loadImageAction(path, imageView, options, callback);
        } else {
            throw new RuntimeException("loadImageWithMark options is invalid");
        }
    }

    /**
     * 二进制数据获取图片
     *
     * @param data
     * @param imageView
     * @param width
     * @param height
     * @param callback
     * @param plugin
     * @return
     */
    public APMultimediaTaskModel loadDataImage(boolean isSync, byte[] data,
            ImageView imageView,
            Drawable defaultImage,
            int width, int height,
            APImageDownLoadCallback callback,
            ImageWorkerPlugin plugin,
            APDisplayer displayer, Size size) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                                            .setProcessor(plugin)
                                            .showImageOnLoading(defaultImage)
                                            .width(width)
                                            .height(height)
                                            .originalSize(size)
                                            .syncLoading(isSync)
                                            .displayer(displayer)
                                            .build();
        ImageLoadReq req = new ImageLoadReq(mEngine, data, imageView, callback, options);
        return loadImageAction(req);
    }

    /***********************以下为图片上传部分**********************/

    /**
     * 取消任务
     *
     * @param taskId
     */
    public APMultimediaTaskModel cancelUpLoad(String taskId) {
        if (TextUtils.isEmpty(taskId)) {
            logger.d("cancelUpLoad taskId is null");
            return null;
        }

        Map<Future, Object> futures = APMultimediaTaskManager.getInstance(mContext).getTaskFutureList(taskId);
        APMultimediaTaskModel task = null;

        if (futures != null) {
            for (Future future : futures.keySet()) {
                future.cancel(true);
            }

            futures.clear();
        }

        APMultimediaTaskManager.getInstance(mContext).removeTaskFuture(taskId);

        task = APMultimediaTaskManager.getInstance(mContext).getTaskRecord(taskId);
        if (task != null) {
            task.setStatus(APMultimediaTaskModel.STATUS_CANCEL);

            //TODO 是否需要进行注销监听器？？？(暂时不需要注销，因为可能存在一个callback用在多个任务上)
            APImageManager.getInstance(mContext).unregistUploadCallback(taskId);
            //TODO 这里是否需要删除task记录？？？
            //APMultimediaTaskManager.getInstance(mContext).delTaskRecord(task.getTaskId());
            APMultimediaTaskManager.getInstance(mContext).updateTaskRecord(task);
            logger.d("cancelUpLoad taskId =" + taskId);
        }

        return task;
    }

    public APMultimediaTaskModel uploadImage(boolean isSync, String filePath,
            int width, int height, APImageUploadCallback cb,
            APImageUploadOption option) {
        if (option == null) {
            option = new APImageUploadOption();
            option.setQua(APImageUploadOption.QUALITITY.DEFAULT);

            option.setImage_x(width);
            option.setImage_y(height);
        }
        // TODO 回调开始

        APMultimediaTaskModel taskStatus = new APMultimediaTaskModel();
        taskStatus.setSourcePath(filePath);
        APMultimediaTaskManager.getInstance(mContext).addTaskRecord(taskStatus);
        //add by jinmin
        if (taskStatus != null && cb != null) {
            APImageManager.getInstance(mContext).registUploadCallback(taskStatus.getTaskId(), cb);
        }

        Future future = mEngine.submit(new ImageUpHandler(mContext, filePath, cb, option, taskStatus));
        APMultimediaTaskManager.getInstance(mContext).addTaskFuture(taskStatus.getTaskId(), future);
        if (isSync) {
            try {
                future.get();
            } catch (InterruptedException e) {
                taskStatus.setStatus(APMultimediaTaskModel.STATUS_CANCEL);
                APMultimediaTaskManager.getInstance(mContext).updateTaskRecord(taskStatus);
            } catch (ExecutionException e) {
                taskStatus.setStatus(APMultimediaTaskModel.STATUS_FAIL);
                APMultimediaTaskManager.getInstance(mContext).updateTaskRecord(taskStatus);
            } finally {
                APMultimediaTaskManager.getInstance(mContext).removeTaskFuture(taskStatus.getTaskId());
            }
        }
        return taskStatus;
    }

    /**
     * 上传原图
     *
     * @param isSync
     * @param filePath
     * @param cb
     * @return
     */
    public APMultimediaTaskModel uploadOriginalImage(boolean isSync,
            String filePath,
            APImageUploadCallback cb) {
        APMultimediaTaskModel taskStatus = new APMultimediaTaskModel();
        taskStatus.setSourcePath(filePath);
        APMultimediaTaskManager.getInstance(mContext).addTaskRecord(taskStatus);
        APImageUploadOption uploadOption = new APImageUploadOption();
        uploadOption.setQua(APImageUploadOption.QUALITITY.ORIGINAL);

        if (taskStatus != null && cb != null) {
            APImageManager.getInstance(mContext).registUploadCallback(taskStatus.getTaskId(), cb);
        }

        Future future = mEngine.submit(new ImageUpHandler(mContext, filePath, cb, uploadOption, taskStatus));
        APMultimediaTaskManager.getInstance(mContext).addTaskFuture(taskStatus.getTaskId(), future);
        if (isSync) {
            try {
                future.get();
            } catch (InterruptedException e) {
                taskStatus.setStatus(APMultimediaTaskModel.STATUS_CANCEL);
                APMultimediaTaskManager.getInstance(mContext).updateTaskRecord(taskStatus);
            } catch (ExecutionException e) {
                taskStatus.setStatus(APMultimediaTaskModel.STATUS_FAIL);
                APMultimediaTaskManager.getInstance(mContext).updateTaskRecord(taskStatus);
            } finally {
                APMultimediaTaskManager.getInstance(mContext).removeTaskFuture(taskStatus.getTaskId());
            }
        }
        return taskStatus;
    }

    /**
     * 上传二进制图片
     *
     * @param isSync
     * @param fileData
     * @param cb
     * @return
     */
    public APMultimediaTaskModel uploadImage(boolean isSync,
            byte[] fileData,
            APImageUploadCallback cb) {
        APMultimediaTaskModel taskStatus = new APMultimediaTaskModel();
        APMultimediaTaskManager.getInstance(mContext).addTaskRecord(taskStatus);
        APImageUploadOption uploadOption = new APImageUploadOption();
        uploadOption.setQua(APImageUploadOption.QUALITITY.DEFAULT);

        if (taskStatus != null && cb != null) {
            APImageManager.getInstance(mContext).registUploadCallback(taskStatus.getTaskId(), cb);
        }

        Future future = mEngine.submit(new ImageUpHandler(mContext, fileData, cb, uploadOption, taskStatus));
        APMultimediaTaskManager.getInstance(mContext).addTaskFuture(taskStatus.getTaskId(), future);
        if (isSync) {
            try {
                future.get();
            } catch (InterruptedException e) {
                taskStatus.setStatus(APMultimediaTaskModel.STATUS_CANCEL);
                APMultimediaTaskManager.getInstance(mContext).updateTaskRecord(taskStatus);
            } catch (ExecutionException e) {
                taskStatus.setStatus(APMultimediaTaskModel.STATUS_FAIL);
                APMultimediaTaskManager.getInstance(mContext).updateTaskRecord(taskStatus);
            } finally {
                APMultimediaTaskManager.getInstance(mContext).removeTaskFuture(taskStatus.getTaskId());
            }
        }

        return taskStatus;
    }

//    /**
//     * 从本地路径加载图片,同时将文件存储到cache中
//     *
//     * @param path
//     * @param width
//     * @param height
//     * @return Bitmap
//     * @throws java.io.IOException
//     */
//    private Bitmap getFromLocal(String path, int width, int height) throws IOException {
//        File imageFile = new File(path);
//        if (imageFile.exists() && imageFile.isFile()) {
//            FalconFacade falconFacade = FalconFacade.get();
//            Bitmap bmp;
//            if (width > 0 && height > 0) {//cut
//                int max = Math.max(width, height);
//                bmp = falconFacade.cutImage(new File(path), max, max / 2, 0.5f);
//            } else {
//                ByteArrayOutputStream out = null;
//                out = falconFacade.compressImage(imageFile, ImageHandler.DEFAULT_QUALITY, width, height);
//                byte[] data = out.toByteArray();
//                bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
//            }
//            String cacheKey = CacheUtils.makeImageCacheKey(null, path, width, height, CutScaleType.CENTER_CROP);
//            BitmapCacheLoader cacheLoader = mEngine.getCacheLoader();
//            if (cacheLoader != null && bmp != null && !bmp.isRecycled()) {
//                cacheLoader.put(cacheKey, bmp);
//            }
//            return bmp;
//        } else {
//            throw new IOException(path + " not exists or not file..");
//        }
//    }

    /********************批量上传下载实现*************************/

    /**
     * 批量上传图片
     *
     * @param requestList
     * @return
     */
    public List<APMultimediaTaskModel> batchUpImage(List<APImageUpRequest> requestList) {
        List<APMultimediaTaskModel> upList = new ArrayList<APMultimediaTaskModel>();
        for (APImageUpRequest req : requestList) {
            APMultimediaTaskModel model = null;
            switch (req.uploadType) {
                case APImageUpRequest.TYPE_ORIGINAL:
                    model = uploadOriginalImage(req.isSync, req.path, req.callback);
                    break;
                case APImageUpRequest.TYPE_HIGH:
                case APImageUpRequest.TYPE_MIDDLE:
                case APImageUpRequest.TYPE_LOW:
                case APImageUpRequest.TYPE_DEFAULT:
                    APImageUploadOption option = new APImageUploadOption();
                    option.setImage_x(req.width);
                    option.setImage_y(req.height);
                    model = uploadImage(req.isSync, req.path, 0, 0, req.callback, option);
                    break;

                default:
                    break;
            }

            if (model != null) {
                upList.add(model);
            }

        }
        return upList;
    }

    /**
     * 批量下载图片
     *
     * @param requestList
     * @return
     */
    public List<APMultimediaTaskModel> batchLoadImage(List<APImageLoadRequest> requestList) {
        List<APMultimediaTaskModel> loadList = new ArrayList<APMultimediaTaskModel>();
        for (APImageLoadRequest req : requestList) {
            APMultimediaTaskModel model = null;
            switch (req.loadType) {
                case APImageLoadRequest.TYPE_DJANGO:
                case APImageLoadRequest.TYPE_NORMAL:
                    model = loadImageAction(req.isSync, req.loadType, true, req.path, req.imageView,
                            req.defaultDrawable, req.width, req.height, req.callback, req.plugin, req.displayer,
                            req.isBackground, req.imageSize);
                    break;
                case APImageLoadRequest.TYPE_DATA:
                    model = loadDataImage(req.isSync, req.data, req.imageView, req.defaultDrawable, req.width,
                            req.height, req.callback, req.plugin, req.displayer, req.imageSize);
                    break;
                default:
                    break;
            }

            if (model != null) {
                loadList.add(model);
            }

        }
        return loadList;
    }

    /**
     * 根据路径来判断使用哪种方式load
     *
     * @param path
     * @return
     */
    private int getImageLoadEngineType(String path) {
        if (!TextUtils.isEmpty(path)) {
            if (path.startsWith(PathUtils.ASSET_SCHEMA)) {
                return ImageLoadEngine.TYPE_ASSET;
            }
            path = PathUtils.extractPath(path);
            File file = new File(path);
            if ((file.exists() && file.isFile()) || (path.startsWith("/"))) {
                return ImageLoadEngine.TYPE_NORMAL;
            }

            if (PathUtils.isHttp(Uri.parse(path))) {
                return ImageLoadEngine.TYPE_NORMAL;
            } else {
                return ImageLoadEngine.TYPE_DJANGO;
            }
        }
        return ImageLoadEngine.TYPE_NORMAL;
    }

    /**
     * 计算裁切图宽高
     *
     * @param width  宽
     * @param height 高
     * @param maxLen 最长值
     * @param path   文件路径
     * @return int[2], int[0]:w, int[1]:h
     */
    public static int[] calculateCutImageRect(int width, int height, int maxLen, String path) {
//        return FalconFacade.get().calculateCutImageRect(width, height, maxLen, path);
        return null;
    }

    public static int[] calculateDesWidthHeight(String path) {
        return ImageUtils.calculateDesWidthHeight(path);
    }

    private String extractPath(String path) {
        return PathUtils.extractPath(path);
    }

    /**
     * View优化
     *
     * @param listView
     * @param pauseOnScroll
     * @param pauseOnFling
     * @param listener
     */
    public void optimizeView(AbsListView listView, boolean pauseOnScroll, boolean pauseOnFling,
            AbsListView.OnScrollListener listener) {
        if (listView != null) {
            listView.setOnScrollListener(new PauseOnScrollListener(mEngine, pauseOnScroll, pauseOnFling, listener));
        }
    }

    public void optimizeView(ViewPager viewPager, boolean pauseOnScroll, ViewPager.OnPageChangeListener listener) {
        if (viewPager != null) {
            viewPager.setOnPageChangeListener(
                    new PauseOnPageChangeListener(mEngine, pauseOnScroll, listener));
        }
    }

    private void doLoadCallbackError(final String path, final String msg, final APImageDownLoadCallback callback) {
        if (callback == null) {
            return;
        }
        post(new Runnable() {
            @Override
            public void run() {
                APImageDownloadRsp rsp = new APImageDownloadRsp();
                rsp.setSourcePath(path);
                APImageRetMsg retMsg = new APImageRetMsg();
                retMsg.setCode(APImageRetMsg.RETCODE.PARAM_ERROR);
                retMsg.setMsg(msg);
                rsp.setRetmsg(retMsg);
                callback.onError(rsp, null);
            }
        });
    }

    private void doLoadCallbackSuccess(final String path, final String cacheId,
            final APImageDownLoadCallback callback) {
        if (callback == null) {
            return;
        }
        post(new Runnable() {
            @Override
            public void run() {
                APImageDownloadRsp rsp = new APImageDownloadRsp();
                rsp.setSourcePath(path);
                rsp.setCacheId(cacheId);
                APImageRetMsg retMsg = new APImageRetMsg();
                retMsg.setCode(APImageRetMsg.RETCODE.SUC);
                rsp.setRetmsg(retMsg);
                callback.onSucc(rsp);
            }
        });
    }

    private void post(Runnable runnable) {
        ImageLoadEngine.getInstance().submit(runnable);
    }

    public String getOriginalImagePath(String url) {
        String path = "";
        APImageOriginalQuery query = new APImageOriginalQuery(url);
        APImageQueryResult<APImageOriginalQuery> result = getImageCacheManager().queryImageFor(query);
        if (result.success) {
            path = result.path;
        }
        return path;
    }

    public boolean checkInNetTask(String path) {
        return APImageManager.getInstance(mContext).isUrlInNetTask(path);
    }

    public <T extends APImageQuery> APImageQueryResult<? extends APImageQuery> queryImageFor(T query) {
        ImageCacheManager manager = getImageCacheManager();
        if (query instanceof APImageSourceCutQuery) {
            return manager.queryImageFor((APImageSourceCutQuery) query);
        }
        if (query instanceof APImageBigQuery) {
            return manager.queryImageFor((APImageBigQuery) query);
        }
        if (query instanceof APImageOriginalQuery) {
            return manager.queryImageFor((APImageOriginalQuery) query);
        }
        if (query instanceof APImageClearCacheQuery) {
            return manager.queryImageFor((APImageClearCacheQuery) query);
        }
        if (query instanceof APImageCacheQuery) {
            return manager.queryImageFor((APImageCacheQuery) query);
        }
        if (query instanceof APImageThumbnailQuery) {
            return manager.queryImageFor((APImageThumbnailQuery) query);
        }
        return null;
    }

//    public APMultimediaTaskModel saveImage(String path, String dest, int width, int height,
//            APImageDownLoadCallback callback) {
//
//        if (TextUtils.isEmpty(path) || TextUtils.isEmpty(dest)) return null;
//
//        DisplayImageOptions options = new DisplayImageOptions.Builder()
//                                            .width(width)
//                                            .height(height)
//                                            .build();
//
//        ImageLoadReq req = new ImageLoadReq(mEngine, path, null, callback, options);
//
//        APMultimediaTaskModel taskModel = new APMultimediaTaskModel();
//        req.taskModel = taskModel;
//        ImageSaveTask task = new ImageSaveTask(req, dest);
//        mEngine.submit(task);
//        return taskModel;
//
////        if (width < 0 || height < 0) {
////            width = mLoadWidth;
////            height = mLoadHeight;
////        }
////
////        String newPath = extractPath(path);
////
////        logger.i(TAG, "loadImageAction, loadType: " + loadType +
////                ", path: " + path + ", newPath: " + newPath + ", width: " +
////                width + ", height: " + height + ", downCb: " + callback);
////
////        if (TextUtils.isEmpty(newPath)) {
////            doLoadCallbackError(path, "path is empty", callback);
////            return null;
////        }
////
////        APMultimediaTaskModel taskStatus = new APMultimediaTaskModel();
////        taskStatus.setSourcePath(path);
////        taskStatus.setDestPath(dest);
////
////        APMultimediaTaskManager.getInstance(mContext).addTaskRecord(taskStatus);
////
////        if (taskStatus.getTaskId() != null && callback != null) {
////            APImageManager.getInstance(mContext).registLoadCallback(taskStatus.getTaskId(), callback);
////        }
////
////        APImageDownloadOption option = new APImageDownloadOption();
////
////        option.setImage_x(width);
////        option.setImage_y(height);
////        //下载原始图片
////        int type = getImageLoadEngineType(path);
////        if ((loadType == ImageLoadEngine.TYPE_ORIGINAL && type == ImageLoadEngine.TYPE_DJANGO)
////                || loadType == ImageLoadEngine.TYPE_ASSET) {
////            option.setLoadType(loadType);
////        } else {
////            option.setLoadType(type);
////        }
////
////        ImageSaveHandler loadTask = new ImageSaveHandler(mContext, path, null, callback, option, null, taskStatus);
////        Future future = mEngine.submit(loadTask);
////        APMultimediaTaskManager.getInstance(mContext).addTaskFuture(taskStatus.getTaskId(), future);
////        return taskStatus;
//    }

    public APImageOfflineDownloadRsp offlineDownload(APImageOfflineDownloadReq req) {
        return new ImageOfflineDownloadHandler(req).call();
    }

    public Bitmap loadCacheBitmap(APCacheBitmapReq req) {
        logger.p("loadCacheBitmap APCacheBitmapReq " + req);
        Bitmap bitmap = null;
        if (req != null) {
            updateCacheBitmapReq(req);
            String cacheKey = CacheUtils.makeImageCacheKey(req.plugin, req.path, req.width, req.height,
                    req.cutScaleType, req.imageMarkRequest);
            //先从内存缓存和本地缓存获取
            bitmap = mEngine.getCacheLoader().loadCacheBitmap(cacheKey, req.loadFromDiskCache);
            //要是取不到，从DiskCache写队列获取
            if (bitmap != null && bitmap.isRecycled()) {
                bitmap = null;
            }
        }
        return bitmap;
    }

    public Bitmap loadCacheBitmap(APThumbnailBitmapReq req) {
        logger.p("loadCacheBitmap APThumbnailBitmapReq " + req);
        Bitmap bitmap = null;
        if (req != null) {
            APImageThumbnailQuery query = new APImageThumbnailQuery(req.path);
            APImageQueryResult<?> result = getImageCacheManager().queryImageFor(query);
            if (result.success) {
                APCacheBitmapReq bitmapReq = new APCacheBitmapReq(result.query.path, result.width, result.height);
                bitmapReq.loadFromDiskCache = req.loadFromDiskCache;
                //bitmapReq.cutScaleType = result.cutScaleType;
                bitmap = loadCacheBitmap(bitmapReq);
                if (req.loadFromDiskCache && (bitmap == null || bitmap.isRecycled())) {
                    bitmap = BitmapFactory.decodeFile(result.path);
                }
            }
        }
        return bitmap;
    }

    private void updateCacheBitmapReq(APCacheBitmapReq req) {
        if (req != null) {
            req.path = PathUtils.extractPath(req.path);
            if (req.srcSize != null) {
                req.cutScaleType = ImageUtils.calcCutScaleType(req.srcSize, Math.max(req.width, req.width));
            }
        }
    }

    public int deleteCache(String path) {
        return getImageCacheManager().deleteCache(path);
    }

    public class UserLeaveHintReceiver extends BroadcastReceiver {
        private final int MSG_RELEASE_MEM = 1;
//        private BitmapCacheLoader cacheLoader = null;
        private Handler mHander = null;

        @Override
        public void onReceive(Context context, Intent intent) {
            if (mHander == null) {
                mHander = new Handler(Looper.getMainLooper()){
                    @Override
                    public void handleMessage(Message msg){
                        if(isAppOnBackground()){
                            //压后台释放一部分内存
//                            BackgroundExecutor.execute(new Runnable() {
//                                @Override
//                                public void run() {
////                           if(cacheLoader != null){
////                               cacheLoader.trimToSize(16 * 1024 * 1024);
////                           }
//                                    if (mEngine != null && mEngine.getCacheLoader() != null) {
//                                        mEngine.getCacheLoader().trimToSize(16 * 1024 * 1024);
//                                    }
//                                }
//                            });
                        }
                    }
                };
            }
            //Log.d("elvis","UserLeaveHintReceiver onReceive action="+intent.getAction());
            if(USERLEAVEHINT_ACTION.equalsIgnoreCase(intent.getAction())){
                if(mHander.hasMessages(MSG_RELEASE_MEM)){
                    mHander.removeMessages(MSG_RELEASE_MEM);
                }
                //延时一分钟去释放内存
                mHander.sendEmptyMessageDelayed(MSG_RELEASE_MEM, 60 * 1000);
            }
        }

//        public void setBitmapCacheLoader(BitmapCacheLoader cacheLoader){
//            this.cacheLoader = cacheLoader;
//        }
    }

    public void registUserLeaveHintReceiver(){
        //Log.d("elvis","registUserLeaveHintReceiver");
        long start = System.currentTimeMillis();
        LocalBroadcastManager lc = LocalBroadcastManager.getInstance(getApplicationContext());
        UserLeaveHintReceiver receiver = new UserLeaveHintReceiver();
//        receiver.setBitmapCacheLoader(mEngine.getCacheLoader());
        // We are going to watch for interesting local broadcasts.
        IntentFilter filter = new IntentFilter();
        filter.addAction(USERLEAVEHINT_ACTION);

        if (lc != null) {
            lc.registerReceiver(receiver, filter);
        }
        Logger.P("MultimediaImageServiceImpl",
                "registUserLeaveHintReceiver cost: " + (System.currentTimeMillis() - start));
    }


}
