package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.task;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

import com.naruto.mobile.framework.service.common.multimedia.graphics.APImageDownLoadCallback;
import com.naruto.mobile.framework.service.common.multimedia.graphics.ImageWorkerPlugin;
import com.naruto.mobile.framework.service.common.multimedia.graphics.data.APImageDownloadRsp;
import com.naruto.mobile.framework.service.common.multimedia.graphics.data.APImageRetMsg;
import com.naruto.mobile.framework.service.common.multimedia.graphics.data.APImageUpRequest;
import com.naruto.mobile.framework.service.common.multimedia.graphics.data.CutScaleType;
import com.naruto.mobile.framework.service.common.multimedia.graphics.load.DisplayImageOptions;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.BitmapCacheLoader;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.disc.ImageCacheManager;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.ImageLoadEngine;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.ImageLoadReq;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.assist.ImageDisplayUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.assist.ViewAssistant;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.assist.ViewWrapper;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.exception.CancelException;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.service.graphics.APImageWorker;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.AppUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.CompareUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.ImageUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Logger;

/**
 * 图片加载任务
 */
public abstract class ImageTask<V> implements Callable<V> {
    protected static ViewAssistant viewAssistant = ViewAssistant.getInstance();
    public ImageLoadReq loadReq;
    protected Context mContext;
    protected APImageDownloadRsp downloadRsp;
    protected APImageDownLoadCallback downLoadCallback;
    protected DisplayImageOptions options;
    protected ViewWrapper<ImageView> viewWrapper;
    protected boolean cancelled = false;
    private Logger logger = Logger.getLogger("ImageTask");

    protected ImageTask() {}

    public ImageTask(ImageLoadReq loadReq, ViewWrapper<ImageView> wrapper) {
        this.loadReq = loadReq;
        this.options = loadReq.options;
        this.downLoadCallback = loadReq.downLoadCallback;
        this.downloadRsp = loadReq.downloadRsp;

        this.mContext = AppUtils.getApplicationContext();
        this.viewWrapper = (wrapper == null ? new ViewWrapper<ImageView>(loadReq.imageView, loadReq.cacheKey)
                                                : wrapper);
    }

    protected BitmapCacheLoader getCacheLoader() {
        return ImageLoadEngine.getInstance().getCacheLoader();
    }

    protected ImageCacheManager getCacheManager() {
        return APImageWorker.getInstance(AppUtils.getApplicationContext()).getImageCacheManager();
    }

    protected boolean checkImageViewReused() {
        return viewAssistant.checkViewReused(viewWrapper);
    }

    public void cancel() {
        cancelled = true;
    }

    public Bitmap processBitmap(Bitmap bitmap) {
        return processBitmap(options.getProcessor(), bitmap);
    }

    public Bitmap processBitmap(ImageWorkerPlugin processor, Bitmap bitmap) {
        if (processor != null) {
            try {
                bitmap = processor.process(loadReq.taskModel, bitmap);
            } catch (Exception e) {
                logger.e(e, "processBitmap error");
            }
        }
        return bitmap;
    }

    /**
     * 显示
     * @param bitmap
     * @param req
     * @param wrapper       传null，表示从ImageLoadReq解析
     */
    public void display(Bitmap bitmap, ImageLoadReq req, ViewWrapper wrapper) {
        if (req.skipDisplay) return;
        logger.d("display bitmap: " + bitmap + ", req: " + req + ", wrapper: " + wrapper);
        ImageDisplayTask task = new ImageDisplayTask(bitmap, req, wrapper);
        task.syncRunTask();
    }

    protected boolean isNeedZoom(ImageLoadReq req) {
        DisplayImageOptions o = req.options;
        return CutScaleType.KEEP_RATIO.equals(o.getCutScaleType()) && (o.getWidth() != 0 && o.getHeight() != 0);
    }

    protected int[] getFitSize(int width, int height) {
        int[] defaultWh = ImageUtils.getScaleScreenRect(mContext, 1.0f);
        float defaultRatio = ((float) defaultWh[0]) / (float) defaultWh[1];

        if (defaultWh[0] < defaultWh[1]) {
            if (defaultWh[1] > APImageUpRequest.DEFAULT_UP_H) {
                defaultWh[1] = APImageUpRequest.DEFAULT_UP_H;
                defaultWh[0] = (int) ((float) defaultWh[1] * defaultRatio);
            }
        } else {
            if (defaultWh[0] > APImageUpRequest.DEFAULT_UP_W) {
                defaultWh[0] = APImageUpRequest.DEFAULT_UP_W;
                defaultWh[1] = (int) ((float) defaultWh[0] / defaultRatio);
            }
        }

        if (width == 0 || height == 0) {
            return defaultWh;
        }

        if (width == Integer.MAX_VALUE || height == Integer.MAX_VALUE) {
//            defaultWh = ImageUtils.getPreferOriginalShowSize(defaultWh);
            defaultWh[0] = 2000;//APImageUpRequest.DEFAULT_UP_W * 2;
            defaultWh[1] = 2000;//APImageUpRequest.DEFAULT_UP_H * 2;
            return defaultWh;
        }

        float ratio = ((float) width) / (float) height;

        if (width > APImageUpRequest.DEFAULT_UP_W) {
            width = APImageUpRequest.DEFAULT_UP_W;
        }
        if (height > APImageUpRequest.DEFAULT_UP_H) {
            height = APImageUpRequest.DEFAULT_UP_H;
        }

        int newWidth = 0;
        int newHeight = 0;

        if (ratio > defaultRatio) {
            newWidth = width;
            newHeight = (int) ((float) newWidth / ratio);
        } else {
            newHeight = height;
            newWidth = (int) ((float) newHeight * ratio);
        }
        return new int[] { newWidth, newHeight };
    }

    protected boolean checkTask() {
        boolean ret = isCanceled();
        if (ret) {
            notifyCancel();
            return true;
        }

        ret = waitIfPaused();
        if (ret) {
            notifyReuse();
        }
        return ret;
    }

    protected boolean waitIfPaused() {
        ImageLoadEngine engine = loadReq.loadEngine;
        AtomicBoolean pause = engine.getPause();
        if (pause.get()) {
            synchronized (engine.getPauseLock()) {
                if (pause.get()) {
                    try {
                        engine.getPauseLock().wait();
                    } catch (InterruptedException e) {
                        return true;
                    }
                }
            }
        }
        return checkImageViewReused();
    }

    protected boolean isCanceled() {
        return cancelled || Thread.interrupted();
    }

    protected void notifyCancel() {
        notifyError(APImageRetMsg.RETCODE.CANCEL, "load cancel, key: " + loadReq.cacheKey, new CancelException());
    }

    protected void notifyReuse() {
        notifyError(APImageRetMsg.RETCODE.REUSE, "load reuse, key: " + loadReq.cacheKey, new CancelException());
    }

    protected void notifyError(APImageRetMsg.RETCODE code, String msg, Exception e) {
        notifyError(loadReq, code, msg, e);
    }

    protected void notifyError(final ImageLoadReq req, final APImageRetMsg.RETCODE code, final String msg, final Exception e) {
        if (ImageDisplayUtils.inMainLooper()) {
            ImageTaskEngine.get().submit(new Runnable() {
                @Override
                public void run() {
                    notifyErrorInCurrentThread(req, code, msg, e);
                }
            });
        } else {
            notifyErrorInCurrentThread(req, code, msg, e);
        }
    }

    private void notifyErrorInCurrentThread(ImageLoadReq req, APImageRetMsg.RETCODE code, String msg, Exception e) {
        if (req.downLoadCallback != null) {
            APImageDownloadRsp rsp = req.downloadRsp;
            APImageRetMsg retMsg = new APImageRetMsg();
            if (!CompareUtils.in(code, APImageRetMsg.RETCODE.CANCEL, APImageRetMsg.RETCODE.REUSE) && checkImageViewReused()) {
                retMsg.setCode(APImageRetMsg.RETCODE.REUSE);
            } else {
                retMsg.setCode(code);
            }
            retMsg.setMsg(msg);
            rsp.setRetmsg(retMsg);
            //do callback
            req.downLoadCallback.onError(rsp, e);
        }
        if (e instanceof CancelException) {
            logger.p("notifyError code: " + code + ", msg: " + msg + ", loadReq: " + req);
        } else {
            logger.e(e, "notifyError code: " + code + ", msg: " + msg + ", loadReq: " + req);
        }
        Logger.TIME("ImageTask notifyError costTime: "
                + (System.currentTimeMillis() - req.startTime) + ", " + loadReq.path);
    }

    protected void notifySuccess() {
        logger.p("notifySuccess downLoadCallback: " + downLoadCallback + ", downloadRsp: " + downloadRsp);
        if (downLoadCallback != null) {
            APImageDownloadRsp rsp = downloadRsp;
            APImageRetMsg retMsg = new APImageRetMsg();
            retMsg.setMsg("load success: " + loadReq.source);
            retMsg.setCode(APImageRetMsg.RETCODE.SUC);
            downLoadCallback.onSucc(rsp);
        }
        if (System.currentTimeMillis()-loadReq.startTime > 100) {
            Logger.TIME("ImageTask notifySuccess costTime: "
                    + (System.currentTimeMillis() - loadReq.startTime) + ", " + loadReq.path);
        } else {
            Logger.P("CostTime", "ImageTask notifySuccess costTime: "
                    + (System.currentTimeMillis() - loadReq.startTime) + ", " + loadReq.path);
        }
    }


}
