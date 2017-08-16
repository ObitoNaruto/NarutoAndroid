package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.download;

import android.os.Bundle;
import android.text.TextUtils;

import com.naruto.mobile.framework.service.common.multimedia.api.MultimediaFileService;
import com.naruto.mobile.framework.service.common.multimedia.api.data.APMultimediaTaskModel;
import com.naruto.mobile.framework.service.common.multimedia.file.APFileDownCallback;
import com.naruto.mobile.framework.service.common.multimedia.file.data.APFileDownloadRsp;
import com.naruto.mobile.framework.service.common.multimedia.file.data.APFileReq;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.ImageLoadReq;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.AppUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Logger;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.UCLogUtil;

/**
 * Django原始图片下载器
 * Created by jinmin on 15/7/20.
 */
public class DjangoOriginalDownloader implements ImageDownloader<String>, APFileDownCallback {

    private Logger logger = Logger.getLogger("DjangoOriginalDownloader");

    private MultimediaFileService mService;
    private ImageLoadReq loadReq;
    private String savePath;
    private APMultimediaTaskModel mTask;
    private APFileDownCallback mCallback;
    private long start;

    public DjangoOriginalDownloader(ImageLoadReq loadReq, String savePath, APFileDownCallback cb) {
        this.loadReq = loadReq;
        this.savePath = savePath;
        this.mCallback = cb;
        this.mService = AppUtils.getFileService();
    }

    @Override
    public String download(ImageLoadReq req, Bundle extra) {
        start = System.currentTimeMillis();
        APFileReq fileReq = new APFileReq();
        fileReq.setCloudId(req.path);
        fileReq.setSavePath(savePath);
        mTask = mService.downLoad(fileReq, this);
        return savePath;
    }

    @Override
    public void cancel() {
        if (mTask != null && !TextUtils.isEmpty(mTask.getTaskId())) {
            mService.cancelLoad(mTask.getTaskId());
        }
    }

    @Override
    public void onDownloadStart(APMultimediaTaskModel taskInfo) {
        if (mCallback != null) {
            mCallback.onDownloadStart(taskInfo);
        }
    }

    @Override
    public void onDownloadProgress(APMultimediaTaskModel taskInfo, int progress, long hasDownSize, long total) {
        if (mCallback != null) {
            mCallback.onDownloadProgress(taskInfo, progress, hasDownSize, total);
        }
    }

    @Override
    public void onDownloadBatchProgress(APMultimediaTaskModel taskInfo, int progress, int curIndex, long hasDownSize, long total) {
        if (mCallback != null) {
            mCallback.onDownloadBatchProgress(taskInfo, progress, curIndex, hasDownSize, total);
        }
    }

    @Override
    public void onDownloadFinished(APMultimediaTaskModel taskInfo, APFileDownloadRsp rsp) {
        if (mCallback != null) {
            mCallback.onDownloadFinished(taskInfo, rsp);
        }
        logger.d("saveFile downloadOriginal success, path: " + rsp.getFileReq().getCloudId() + ", saveFile: " + rsp.getFileReq().getSavePath());
        UCLogUtil.UC_MM_C04("0", 0, (int) (System.currentTimeMillis() - start), "",
                2, false, rsp.getMsg(), "");
        Logger.TIME("DjangoOriginalDownloader onDownloadFinished costTime: " + (System.currentTimeMillis() - start));
    }

    @Override
    public void onDownloadError(APMultimediaTaskModel taskInfo, APFileDownloadRsp rsp) {
        logger.e("onDownloadError rsp: " + rsp);
        if (mCallback != null) {
            mCallback.onDownloadError(taskInfo, rsp);
        }
        UCLogUtil.UC_MM_C04(String.valueOf(rsp.getRetCode()), 0, (int) (System.currentTimeMillis() - start), "",
                2, false, rsp.getMsg(), "");
        Logger.TIME("DjangoOriginalDownloader onDownloadError costTime: " + (System.currentTimeMillis() - start));
    }
}
