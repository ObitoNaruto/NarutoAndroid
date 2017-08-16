package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.task;

import android.graphics.Bitmap;
import android.text.TextUtils;

import java.io.File;

import com.naruto.mobile.framework.service.common.multimedia.graphics.data.APImageRetMsg;
import com.naruto.mobile.framework.service.common.multimedia.graphics.data.CutScaleType;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.BitmapCacheLoader;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.resp.ThumbnailsDownResp;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.util.DjangoConstant;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.util.MD5Utils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.ImageLoadReq;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.assist.FalconFacade;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.assist.ViewWrapper;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.download.HttpTransListener;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.download.NetDownloader;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.FileUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.ImageUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Logger;

/**
 * 普通http url图片下载
 * Created by jinmin on 15/7/14.
 */
public class ImageUrlTask extends ImageNetTask {

    private Logger logger = Logger.getLogger("ImageUrlTask");

    public ImageUrlTask(ImageLoadReq loadReq, ViewWrapper wrapper) {
        super(loadReq, wrapper);
    }

    @Override
    public Bitmap executeTask() throws Exception {
        long start = System.currentTimeMillis();
        logger.p("call image url task start");
        String baseDir = FileUtils.getMediaDir(DjangoConstant.IMAGE_PATH);
        if (TextUtils.isEmpty(baseDir)) {
            notifyError(loadReq, APImageRetMsg.RETCODE.DOWNLOAD_FAILED, "base dir is empty", new NullPointerException("base dir is empty"));
            return null;
        }
        String savePath = new File(baseDir, MD5Utils.getMD5String(loadReq.path)).getAbsolutePath();
        NetDownloader netDownloader = new NetDownloader(loadReq, savePath, new HttpTransListener(loadReqSet));
        ThumbnailsDownResp rsp = netDownloader.download(loadReq, null);
        logger.p("call ImageUrlTask download, " + loadReq.path + ", rsp: " + rsp + ", used: " + (System.currentTimeMillis() - start));
        if (rsp.isSuccess()) {
            //图片已下载，需要分别处理
            dealWithResponse(new File(savePath), rsp);
        } else {
            notifyError(null);
        }
        logger.p("call image url task finish");
        return null;
    }

    private void dealWithResponse(File saveFile, ThumbnailsDownResp rsp) {
        FalconFacade facade = FalconFacade.get();
        BitmapCacheLoader cacheLoader = getCacheLoader();
        logger.p("dealWithResponse loadReqSet.size: " + loadReqSet.size());
        for (ImageLoadReq loadReq : loadReqSet) {
            int[] fitSize = getFitSize(options.getWidth(), options.getHeight());
            try {
                Bitmap bitmap = cacheLoader.getMemCache(loadReq.cacheKey);
                if (!ImageUtils.checkBitmap(bitmap)) {
                    bitmap = facade.cutImageKeepRatio(saveFile, fitSize[0], fitSize[1]);
                    if (ImageUtils.checkBitmap(bitmap)) {
                        if (isNeedZoom(loadReq)) {
                            bitmap = ImageUtils.zoomBitmap(bitmap, fitSize[0], fitSize[1]);
                        } else if (CutScaleType.SCALE_AUTO_LIMIT.equals(options.getCutScaleType())) {
                            logger.p("SCALE_AUTO_LIMIT bitmap.w: " + bitmap.getWidth() +
                                    ", bitmap.h: " + bitmap.getHeight() +
                                    ", ow: " + options.getWidth() + ", oh: " + options.getHeight());
                            if (bitmap.getWidth() > options.getWidth() ||
                                    bitmap.getHeight() > options.getHeight()) {
                                bitmap = ImageUtils.zoomBitmap(bitmap, options.getWidth(), options.getHeight());
                            }
                        }
                        bitmap = processBitmap(options.getProcessor(), bitmap);
                        if (ImageUtils.checkBitmap(bitmap)) {
                            cacheLoader.put(loadReq.cacheKey, bitmap);
                        }
                    }
                }
                display(bitmap, loadReq, null);
            } catch (Exception e) {
                notifyError(loadReq, APImageRetMsg.RETCODE.DOWNLOAD_FAILED, getExceptionInfo(e), e);
            }
        }
    }
}
