/*
 * Copyright 2014 Alibaba.com All right reserved.
 * This software is the confidential and proprietary information of Alibaba.com ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */

package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.api;


import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.req.GetCodesReq;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.resp.GetCodesResp;

/**
 * 与访问 django 资源Code相关的API集合
 *
 * @author jinzhaoyu
 */
public interface CodeApi {
    /**
     * 获取一次性有效 code 用于访问 django 资源，该 code 有效期 5 分钟<br>
     * 该 code 只能用于访问参数中给定的 resources 相关的数据<br>
     * http://api.django.t.taobao.com/rest/1.0/code
     *
     * @param getCodesReq
     * @return
     * @throws DjangoClientException
     */
    GetCodesResp getCodes(GetCodesReq getCodesReq);
}
