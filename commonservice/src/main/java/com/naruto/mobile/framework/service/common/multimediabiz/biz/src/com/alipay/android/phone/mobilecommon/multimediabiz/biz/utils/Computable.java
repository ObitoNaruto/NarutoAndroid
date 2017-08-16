package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils;


import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.req.ComputeCallBack;

/**
 * Created by zhefu.wyq 13-11-5.
 *
 * @param <A>
 * @param <V>
 */
public interface Computable<A, V> {
    public V compute(A arg, ComputeCallBack callBack) throws InterruptedException;
}
