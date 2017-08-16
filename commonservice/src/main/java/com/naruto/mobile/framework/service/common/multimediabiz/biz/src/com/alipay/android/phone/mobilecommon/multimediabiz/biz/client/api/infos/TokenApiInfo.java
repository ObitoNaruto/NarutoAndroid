/*
 * Copyright 2014 Alibaba.com All right reserved.
 * This software is the confidential and proprietary information of Alibaba.com ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */

package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.api.infos;


import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.config.ServerAddress;

/**
 * 与Token相关的API集合
 *
 * @author jinzhaoyu
 */
public class TokenApiInfo extends BaseApiInfo{
    /**
     * 获取Token<br/>
     * http://api.django.t.taobao.com/rest/1.1/token
     */
    public static TokenApiInfo GET_TOKEN = new TokenApiInfo(ServerAddress.ServerType.API, "rest/1.1/token", HttpMethod.GET);

    public TokenApiInfo(ServerAddress.ServerType serverType, String apiPath, HttpMethod httpMethod) {
        super(serverType, apiPath, httpMethod);
    }
}
