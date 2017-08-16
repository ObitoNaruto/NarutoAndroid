package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.exception;

/**
 * 下载异常
 * Created by jinmin on 15/7/24.
 */
public class DownloadException extends Exception {
    public DownloadException() {
    }

    public DownloadException(String detailMessage) {
        super(detailMessage);
    }

    public DownloadException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public DownloadException(Throwable throwable) {
        super(throwable);
    }
}
