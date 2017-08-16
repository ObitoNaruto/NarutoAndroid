package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.task;

import android.graphics.Bitmap;

import java.io.File;
import java.util.Arrays;

import com.naruto.mobile.framework.service.common.multimedia.graphics.data.APImageRetMsg;
import com.naruto.mobile.framework.service.common.multimedia.graphics.load.DisplayImageOptions;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.BitmapCacheLoader;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.resp.ThumbnailsDownResp;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.ImageLoadReq;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.assist.FalconFacade;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.assist.ViewWrapper;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.download.DjangoDownloader;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.FileUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.ImageUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Logger;

/**
 * Django 图片加载任务
 * Created by jinmin on 15/7/17.
 */
public class ImageDjangoTask extends ImageNetTask {

    private Logger logger = Logger.getLogger("ImageDjangoTask");

    public ImageDjangoTask(ImageLoadReq loadReq, ViewWrapper wrapper) {
        super(loadReq, wrapper);
    }

    @Override
    public Bitmap executeTask() throws Exception {
        String saveDir = FileUtils.getMediaDir("django");
        DjangoDownloader downloader = new DjangoDownloader(saveDir, loadReq, new DjangoDownloader.DownloadListener() {
            @Override
            public void onDownloadStart(DjangoDownloader downloader, ImageLoadReq req) {

            }

            @Override
            public void onDownloadProgress(DjangoDownloader downloader, ImageLoadReq req, long read, long total,
                    int percent) {

            }

            @Override
            public void onDownloadFinish(DjangoDownloader downloader, ImageLoadReq req, ThumbnailsDownResp rsp) {

            }

            @Override
            public void onDownloadError(DjangoDownloader downloader, ImageLoadReq req, Exception e) {

            }
        });
        ThumbnailsDownResp rsp = getDownloadRsp(downloader);
        if (rsp.isSuccess()) {
            dealWithResponse(new File(rsp.getSavePath()), rsp);
        } else {
            notifyError(null);
        }
        return null;
    }

    protected ThumbnailsDownResp getDownloadRsp(DjangoDownloader downloader) {
        return downloader.download(loadReq, null);
    }

    protected void dealWithResponse(File saveFile, ThumbnailsDownResp rsp) {
        FalconFacade facade = FalconFacade.get();
        BitmapCacheLoader cacheLoader = getCacheLoader();
        for (ImageLoadReq loadReq : loadReqSet) {
            DisplayImageOptions options = loadReq.options;
            int[] fitSize = getFitSize(options.getWidth(), options.getHeight());
            logger.p("dealWithResponse fitSize: " + Arrays.toString(fitSize) + ", cacheKey: " + loadReq.cacheKey);
            try {
                Bitmap bitmap = cacheLoader.getMemCache(loadReq.cacheKey);
                if (!ImageUtils.checkBitmap(bitmap)) {
                    bitmap = facade.cutImageKeepRatio(saveFile, fitSize[0], fitSize[1]);
                    if (ImageUtils.checkBitmap(bitmap) && isNeedZoom(loadReq)) {
                        bitmap = ImageUtils.zoomBitmap(bitmap, fitSize[0], fitSize[1]);
                    }
                    if (ImageUtils.checkBitmap(bitmap)) {
                        if (rsp.getExtra() != null && rsp.getExtra().getBoolean("saveDisk", false)) {
                            logger.p("dealWithResponse saveDisk: true, cacheKey: " + loadReq.cacheKey);
                            cacheLoader.putMemCache(loadReq.cacheKey, bitmap);
                        } else {
                            logger.p("dealWithResponse saveDisk: false, cacheKey: " + loadReq.cacheKey);
                            cacheLoader.put(loadReq.cacheKey, bitmap);
                        }
                    }
                }
                if (bitmap != null)
                    logger.p("dealWithResponse bitmap[w: " + bitmap.getWidth() + ", h: " + bitmap.getHeight()+"]");
                display(bitmap, loadReq, null);
            } catch (Exception e) {
                notifyError(loadReq, APImageRetMsg.RETCODE.DOWNLOAD_FAILED, getExceptionInfo(e), e);
            }
        }

    }

    @Override
    public String getTaskId() {
        return loadReq.cacheKey;
    }

}
