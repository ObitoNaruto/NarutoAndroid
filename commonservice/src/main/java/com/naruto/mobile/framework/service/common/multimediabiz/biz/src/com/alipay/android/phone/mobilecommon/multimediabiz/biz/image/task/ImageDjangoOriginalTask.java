package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.task;

import android.graphics.Bitmap;
import android.text.TextUtils;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

import com.naruto.mobile.framework.service.common.multimedia.api.data.APMultimediaTaskModel;
import com.naruto.mobile.framework.service.common.multimedia.file.APFileDownCallback;
import com.naruto.mobile.framework.service.common.multimedia.file.data.APFileDownloadRsp;
import com.naruto.mobile.framework.service.common.multimedia.file.data.APFileRsp;
import com.naruto.mobile.framework.service.common.multimedia.graphics.data.APImageRetMsg;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.util.DjangoConstant;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.util.MD5Utils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.ImageLoadReq;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.assist.FalconFacade;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.assist.ViewWrapper;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.download.DjangoOriginalDownloader;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.FileUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.ImageUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Logger;

/**
 *
 * Created by jinmin on 15/7/20.
 */
public class ImageDjangoOriginalTask extends ImageNetTask implements APFileDownCallback {

    private Logger logger = Logger.getLogger("ImageDjangoOriginalTask");

    private DjangoOriginalDownloader downloader;
    private int taskState = -1;
    private CountDownLatch waitCountDownLatch;

    private long start;

    public ImageDjangoOriginalTask(ImageLoadReq loadReq, ViewWrapper wrapper) {
        super(loadReq, wrapper);
    }

    @Override
    public Bitmap executeTask() throws Exception {
        waitCountDownLatch = new CountDownLatch(1);
        String savePath = FileUtils.getMediaDir(DjangoConstant.IMAGE_PATH) + File.separator + MD5Utils.getMD5String(loadReq.path);
        downloader = new DjangoOriginalDownloader(loadReq, savePath, this);
        start = System.currentTimeMillis();
        String imagePath = downloader.download(loadReq, null);
        waitCountDownLatch.await();
        logger.p("call taskState: " + taskState + ", imagePath: " + imagePath);
        if ((taskState == APFileRsp.CODE_SUCCESS) && !TextUtils.isEmpty(imagePath)) {
            //下载成功
            dealWithDownloadSuccess(imagePath);
        }
        return null;
    }

    private void dealWithDownloadSuccess(String imagePath) {
        int[] fitSize = getFitSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
//        fitSize[0] *= 1.1f;
//        fitSize[1] *= 1.1f;
        logger.p("dealWithDownloadSuccess fitSize: " + Arrays.toString(fitSize));

        try {
            Bitmap bitmap = FalconFacade.get().cutImageKeepRatio(new File(imagePath), fitSize[0], fitSize[1]);
            if (ImageUtils.checkBitmap(bitmap)) {
                for (ImageLoadReq req : loadReqSet) {
                    display(bitmap, req, null);
                }
            }
        } catch (Exception e) {
            logger.e(e, "dealWithDownloadSuccess but occur exception");
        }
    }

    @Override
    public void cancel() {
        super.cancel();
        if (downloader != null) {
            downloader.cancel();
        }
    }

    @Override
    public void onDownloadStart(APMultimediaTaskModel taskInfo) {
        logger.p("onDownloadStart");
    }

    @Override
    public void onDownloadProgress(APMultimediaTaskModel taskInfo, int progress, long hasDownSize, long total) {
        for (ImageLoadReq req : loadReqSet) {
            if (req.downLoadCallback != null) {
                req.downLoadCallback.onProcess(req.source, progress);
            }
        }
        logger.p("onDownloadProgress progress: " + progress + ", hasDownSize: " + hasDownSize + ", total: " + total);
    }

    @Override
    public void onDownloadBatchProgress(APMultimediaTaskModel taskInfo, int progress, int curIndex, long hasDownSize, long total) {
        logger.p("onDownloadBatchProgress");
    }

    @Override
    public void onDownloadFinished(APMultimediaTaskModel taskInfo, APFileDownloadRsp rsp) {
        taskState = APFileRsp.CODE_SUCCESS;
        waitCountDownLatch.countDown();
        logger.p("onDownloadFinished taskState: " + taskState);
    }

    @Override
    public void onDownloadError(APMultimediaTaskModel taskInfo, APFileDownloadRsp rsp) {
        waitCountDownLatch.countDown();
        taskState = rsp.getRetCode();
        Exception e = new Exception("download failed");
        for (ImageLoadReq req : loadReqSet) {
            if (req.downLoadCallback != null) {
                notifyError(req, APImageRetMsg.RETCODE.DOWNLOAD_FAILED, "download fail", e);
            }
        }
    }
}
