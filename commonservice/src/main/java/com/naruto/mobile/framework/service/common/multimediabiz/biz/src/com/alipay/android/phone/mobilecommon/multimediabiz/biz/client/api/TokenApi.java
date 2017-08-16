/*
 * Copyright 2014 Alibaba.com All right reserved.
 * This software is the confidential and proprietary information of Alibaba.com ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */

package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.api;


import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.exception.DjangoClientException;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.resp.TokenResp;

/**
 * 与Token相关的API集合
 *
 * @author jinzhaoyu
 */
public interface TokenApi {
    /**
     * 获取Tonken<br>
     *
     * @param refresh Indicate is force refresh token
     * @return
     * @see com.naruto.android.phone.mobilecommon.multimediabiz.biz.client.api.infos.TokenApiInfo#GET_TOKEN
     */
    TokenResp getToken(boolean refresh);

    /**
     * 获取Token，如果本地已经有Token了并且没有过期，则直接返回，否则就请求重新获取
     * @return
     * @throws com.naruto.android.phone.mobilecommon.multimediabiz.biz.client.exception.DjangoClientException
     */
    String getTokenString() throws DjangoClientException;

    /**
     * 在获取到服务器时间后的回调接口
     * @author jinzhaoyu
     */
    public static interface OnGotServerTimeListener{
        /**
         * 获取到服务器时间后的回调
         * @param serverTime
         */
        void onGotServerTime(long serverTime);
    }
}
