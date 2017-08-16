package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.io;

/**
 * 输入流读取回调
 * Created by jinmin on 15/4/15.
 */
public interface InputProgressListener {
    void onReadProgress(int read, int totalRead);
    void onReadFinish(int totalRead);
}
