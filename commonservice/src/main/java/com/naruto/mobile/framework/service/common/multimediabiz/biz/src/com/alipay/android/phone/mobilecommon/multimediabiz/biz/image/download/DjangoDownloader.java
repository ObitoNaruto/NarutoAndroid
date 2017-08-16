package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.download;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;

import com.naruto.mobile.framework.service.common.multimedia.api.data.APMultimediaTaskModel;
import com.naruto.mobile.framework.service.common.multimedia.graphics.data.APImageLoadRequest;
import com.naruto.mobile.framework.service.common.multimedia.graphics.data.APImageMarkRequest;
import com.naruto.mobile.framework.service.common.multimedia.graphics.data.APImageRetMsg;
import com.naruto.mobile.framework.service.common.multimedia.graphics.data.APImageUpRequest;
import com.naruto.mobile.framework.service.common.multimedia.graphics.data.CutScaleType;
import com.naruto.mobile.framework.service.common.multimedia.graphics.data.Size;
import com.naruto.mobile.framework.service.common.multimedia.graphics.load.DisplayImageOptions;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.ImageCacheContext;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.disc.DiskCache;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.DjangoClient;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.config.ConnectionManager;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.exception.DjangoClientException;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.impl.HttpConnectionManager;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.impl.HttpDjangoClient;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.io.DownloadResponseHelper;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.io.IOUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.io.TransferredListener;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.req.ThumbnailMarkDownReq;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.req.ThumbnailsDownReq;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.resp.ThumbnailsDownResp;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.util.DjangoConstant;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.util.MD5Utils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.ImageLoadReq;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.io.InputProgressListener;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.io.ProgressInputStream;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.manager.APMultimediaTaskManager;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.AppUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.ExceptionUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Logger;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.UCLogUtil;

/**
 * Django图片下载器
 * Created by jinmin on 15/7/17.
 */
public class DjangoDownloader implements ImageDownloader<ThumbnailsDownResp> {

    private static final int TYPE_ORIGINAL = 2;
    private static final int TYPE_BIG = 1;
    private static final int TYPE_SMALL = 0;

    private static final String APP_KEY = "aliwallet";
    private static final int MAX_REQ_SIZE = 4000;
    private static final String IMAGE_ZOOM_FORMAT = "%dx%d";
    final AtomicBoolean bFinish = new AtomicBoolean(false);
    int mProgress = -1;
    private Logger logger = Logger.getLogger("DjangoDownloader");
    private ImageLoadReq loadReq;
    private String savePath;
    private DjangoClient djangoClient;
    private DownloadListener listener;
    private Context mContext;
    private APImageRetMsg.RETCODE mCode;
    private long start;
    private boolean cancelled = false;

    public DjangoDownloader(String savePath, ImageLoadReq req, DownloadListener listener) {
        this.savePath = savePath;
        this.loadReq = req;
        this.listener = listener;
        this.mContext = AppUtils.getApplicationContext();
    }

    @Override
    public ThumbnailsDownResp download(ImageLoadReq req, Bundle extra) {
        this.loadReq = req;
        DjangoClient djangoClient = getDjangoClient();
        ThumbnailsDownReq downReq = createDownReq(req);
        setupDownReq(downReq);
        ThumbnailsDownResp rsp = null;
        try {
            start = System.currentTimeMillis();
            rsp = djangoClient.getImageApi().downloadThumbnails(downReq);
            Logger.TIME("downloadThumbnails get response costTime: " + (System.currentTimeMillis() - start));
            handleDownloadRsp(downReq, rsp);

        } catch (Exception e) {
            if (listener != null) {
                listener.onDownloadError(this, loadReq, e);
            }
        }

        return rsp;
    }

    private ThumbnailsDownReq createDownReq(ImageLoadReq req) {
        if (req.options != null && req.options.getImageMarkRequest() != null) {
            APImageMarkRequest markRequest = req.options.getImageMarkRequest();
            ThumbnailMarkDownReq downReq = new ThumbnailMarkDownReq(req.path, getZoom(req));
            downReq.setMarkId(markRequest.getMarkId()); //daily口碑水印 NRgGgE98S6WB0E7myOanZAAAAE4AAQED
            downReq.setMarkHeight(markRequest.getMarkHeight());
            downReq.setMarkWidth(markRequest.getMarkWidth());
            downReq.setPosition(markRequest.getPosition());
            downReq.setTransparency(markRequest.getTransparency());
            downReq.setPaddingX(markRequest.getPaddingX());
            downReq.setPaddingY(markRequest.getPaddingY());
            downReq.setPercent(markRequest.getPercent());
            return downReq;
        } else {
            return new ThumbnailsDownReq(req.path, getZoom(req));
        }
    }

    protected void handleDownloadRsp(ThumbnailsDownReq req, ThumbnailsDownResp resp) {
        InputStream is = null;
        ProgressInputStream pis = null;
        final long range = req.getRange();
        long size = 0;
        int code = -1;
        String exp = "";
        boolean bServCode = false;
        String traceId = null;
        try {
            traceId = resp.getTraceId();
            if (resp.isSuccess()) {
                HttpResponse httpResponse = resp.getResp();
                is = httpResponse.getEntity().getContent();
                size = httpResponse.getEntity().getContentLength();
                final long ts = size + range;
                logger.p("getFromDjango, ts: " + ts + ", range: " + range + ", mSourcePath: " + loadReq.path);
                pis = new ProgressInputStream(is, new InputProgressListener() {
                    @Override
                    public void onReadProgress(int read, int totalRead) {
                        checkCancel(djangoClient);
                        onProcess(totalRead + range, ts);
                    }

                    @Override
                    public void onReadFinish(int size) {
                        logger.p("getFromDjango onReadFinish size=" + size + ";range=" + range + ", mSourcePath: " + loadReq.path);
                        if (size > 0 && ((size + range) == ts || ts <= 0)) {
                            bFinish.set(true);
                        }

                        logger.d("getFromDjango onReadFinish usedTime: " + (System.currentTimeMillis() - start)
                                + "; size: " + size + ";range: " + range + "; ts: " + ts
                                + ";bFinish=" + bFinish.get() + ";path=" + loadReq.path);
                    }
                });
                String path = toFile(pis, savePath, range);
                if (TextUtils.isEmpty(path)) {
                    mCode = APImageRetMsg.RETCODE.STORE_FAILED;
                    resp.setCode(mCode.ordinal());
                    resp.setMsg("to file fail");
                    logger.d("get from django toFile fail, resp: " + resp);
                } else {
                    resp.setSavePath(path);
                    copyToCache(path, req, resp);
                }
                code = resp.getCode();
            } else {
                exp = resp.getMsg();
                code = resp.getCode();
                bServCode = true;
                logger.d("get from django fail, resp: " + resp);
            }

        } catch (Exception e) {
//            Log.e(TAG, "", e);
            if (!(e instanceof RuntimeException)) {
                mCode = APImageRetMsg.RETCODE.DOWNLOAD_FAILED;
            }
            ExceptionUtils.checkAndResetDiskCache(e);
            logger.e(e, "");
            code = mCode.ordinal();
            resp.setCode(DjangoConstant.DJANGO_400);
            resp.setMsg(e.getMessage());
            exp = e.getMessage();
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(pis);
            String ret = String.valueOf(code);
            UCLogUtil.UC_MM_C04(bServCode ? ("s" + ret) : ret, size, (int) (System.currentTimeMillis() - start), req.getZoom(),
                    getImageType(loadReq.options.getWidth(), loadReq.options.getHeight()), false, exp, traceId);
        }
    }

    private void copyToCache(String path, ThumbnailsDownReq req, ThumbnailsDownResp rsp) {
        boolean ret = false;
        Bundle extra = new Bundle();
        FileInputStream fis = null;
        try {
            if (!req.isWebp() && !CutScaleType.CENTER_CROP.equals(loadReq.options.getCutScaleType())) {
                fis = new FileInputStream(path);
                DiskCache diskCache = ImageCacheContext.get().getDiskCache();
                ret = diskCache.save(loadReq.cacheKey, fis);
            }
        } catch (Exception e) {
            logger.e(e, "copyToCache error");
        } finally {
            IOUtils.closeQuietly(fis);
            extra.putBoolean("saveDisk", ret);
            rsp.setExtra(extra);
        }
    }

    private int getImageType(Integer width, Integer height) {
        if (width != null && height != null) {
            if ((width == 0 && height == 0) || (width == APImageUpRequest.DEFAULT_UP_W && height == APImageUpRequest.DEFAULT_UP_H)) {
                return TYPE_BIG;
            } else if (width == Integer.MAX_VALUE && height == Integer.MAX_VALUE) {
                return TYPE_ORIGINAL;
            } else {
                return TYPE_SMALL;
            }
        }
        return TYPE_ORIGINAL;
    }

    private void checkCancel(DjangoClient jgClient) {
        if (cancelled || loadReq.taskModel.getStatus() == APMultimediaTaskModel.STATUS_CANCEL) {
            mCode = APImageRetMsg.RETCODE.CANCEL;
            if (jgClient != null && jgClient.getConnectionManager() != null) {
                jgClient.getConnectionManager().shutdown();
            }

            logger.d("checkCancel task cancel");
            throw new RuntimeException("cancel");
        }
    }

    private int onProcess(long cSize, long tSize) {

        int progressValue = 0;
        if (tSize > 0) {
            progressValue = (int) ((float) cSize * 100.0f / tSize);
        }

        if (mProgress == progressValue) {
            return progressValue;
        }

        mProgress = progressValue;

        loadReq.taskModel.setTotalSize(tSize);
        loadReq.taskModel.setCurrentSize(cSize);
        APMultimediaTaskManager.getInstance(mContext).updateTaskRecord(loadReq.taskModel);

        if (listener != null) {
            listener.onDownloadProgress(this, loadReq, cSize, tSize, mProgress);
        }

        return progressValue;
    }

    private String toFile(InputStream in, String dir, long start) throws DjangoClientException, IOException {
        DownloadResponseHelper helper = new DownloadResponseHelper();
        long startTime = System.currentTimeMillis();
        String fileName = MD5Utils.getMD5String(loadReq.cacheKey);//直接使用sourcepath，合并url 和 Django
        final File storeFile = new File(dir, fileName + ".tmp");
        File finalFile = new File(dir, fileName);
        //OutputStream os = null;
        String path = null;
        boolean flag = false;
        try {
            if (finalFile.exists() && finalFile.length() > 0) {
                path = finalFile.getAbsolutePath();
                bFinish.set(true);
                if (storeFile.exists()) {
                    logger.d("toFile for finalFile.exists() && finalFile.length() > 0, del: " + storeFile);
                    storeFile.delete();
                }
            } else {
                if (finalFile.exists() && finalFile.length() == 0) {
                    logger.d("toFile for finalFile.exists() && finalFile.length(), del: " + storeFile);
                    finalFile.delete();
                }

                //os = new FileOutputStream(storeFile);
                long offset = start;
                // 可能在该任务有其他任务修改了这个storeFile.length
//                if (storeFile.exists() && storeFile.length() > 0) {
//                    offset = storeFile.length();
//                }
                logger.d(" toFile  offset :" + offset + ",path: " + loadReq.path + ", store path: " + storeFile.getAbsolutePath());
                helper.writeSingleFile(in, storeFile, offset, new TransferredListener() {
                    @Override
                    public void onTransferred(long transferredCount) {
                        //logger.d(" onTransferred  transferredCount :" + transferredCount + ", storeFile len: " + storeFile.length()+ ", store path: " + storeFile.getAbsolutePath());
                    }
                });
                logger.d(" onTransferred bFinish :" + bFinish + ", storeFile len: " + storeFile.length() + ", store path: " + storeFile.getAbsolutePath());
                if (bFinish.get()) {
                    //TODO: 增加长度校验  if(storeFile.length() != )
                    flag = storeFile.renameTo(finalFile);
                    logger.d("saveFile key: " + loadReq.path + loadReq.options + ", renameFrom: " + storeFile +
                            ", finalFile: " + finalFile + ", len: " + finalFile.length() + ", ret: " + flag);
                    path = finalFile.getAbsolutePath();
                }
            }

        } finally {
            //IOUtils.closeQuietly(os);

            //增加校验存储文件大小
            if (!bFinish.get() || TextUtils.isEmpty(path)) {
                if (loadReq.taskModel.getStatus() != APMultimediaTaskModel.STATUS_CANCEL) {
                    logger.d("toFile for !bFinish.get() || TextUtils.isEmpty(path), del: " + storeFile);
                    storeFile.delete();
                    logger.d("toFile delete path=" + path);
                    path = null;
                }
            }

            logger.d("toFile usedTime=" + (System.currentTimeMillis() - startTime) + ";bFinish=" + bFinish.get() +
                    ";flag=" + flag);
        }
        return path;
    }

    @Override
    public void cancel() {
        cancelled = true;
    }

    protected void setupDownReq(ThumbnailsDownReq req) {

    }

    protected synchronized DjangoClient getDjangoClient() {
        if (djangoClient == null || djangoClient.getConnectionManager().isShutdown()) {
            ConnectionManager<HttpClient> conMgr = new HttpConnectionManager();
            conMgr.setAppKey(APP_KEY);
//            conMgr.setAppSecret(APP_SECRET);
            djangoClient = new HttpDjangoClient(AppUtils.getApplicationContext(), conMgr);
        }
        return djangoClient;
    }

    public String getZoom(ImageLoadReq req) {
        DisplayImageOptions options = req.options;
        int width = options.getWidth();
        int height = options.getHeight();
        CutScaleType cutScaleType = options.getCutScaleType();
        Size size = options.getOriginalSize();
        if (width == 0 && height == 0) {
            return String.format(IMAGE_ZOOM_FORMAT, APImageUpRequest.DEFAULT_UP_W, APImageUpRequest.DEFAULT_UP_H);
        } else if ((width == -1 && height == -1) ||
                (width == APImageLoadRequest.ORIGINAL_WH && height == APImageLoadRequest.ORIGINAL_WH)) {
            return ThumbnailsDownReq.DJANGO_ORIGINAL;
        }
        //防止值过大
        if (width > MAX_REQ_SIZE || height > MAX_REQ_SIZE) {
            width = MAX_REQ_SIZE;
            height = MAX_REQ_SIZE;
        }
        String zoomVal = null;
        if (CutScaleType.CENTER_CROP.equals(cutScaleType) && size != null) {
            //调整下宽高
//            int[] newWH = FalconFacade.get()
//                    .calculateCutImageRect(size.getWidth(), size.getHeight(), Math.max(width, height), null);
            int[] newWH = size.getWidth() > size.getHeight() ? new int[]{375, 187} : new int[]{187, 375};
            zoomVal = String.format(IMAGE_ZOOM_FORMAT, newWH[0], newWH[1]) + "xz";
        } else {
            zoomVal = String.format(IMAGE_ZOOM_FORMAT, width, height);
        }
        logger.d("formatZoom width: %s, height: %s, cutType: %s, size: %s, zoomVal: %s", width, height, cutScaleType,
                size, zoomVal);
        return zoomVal;
    }

    public interface DownloadListener {
        void onDownloadStart(DjangoDownloader downloader, ImageLoadReq req);
        void onDownloadProgress(DjangoDownloader downloader, ImageLoadReq req, long read, long total, int percent);
        void onDownloadFinish(DjangoDownloader downloader, ImageLoadReq req, ThumbnailsDownResp rsp);
        void onDownloadError(DjangoDownloader downloader, ImageLoadReq req, Exception e);
    }
}
