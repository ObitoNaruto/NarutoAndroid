package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.download;

import android.os.Bundle;

import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.ImageLoadReq;

/**
 * 下载接口
 * Created by jinmin on 15/7/15.
 */
public interface ImageDownloader<Resp> {
    Resp download(ImageLoadReq req, Bundle extra);
    void cancel();
}
