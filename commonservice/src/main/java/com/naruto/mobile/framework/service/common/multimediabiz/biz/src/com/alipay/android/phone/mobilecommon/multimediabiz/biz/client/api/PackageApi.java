/*
 * Copyright 2014 Alibaba.com All right reserved.
 * This software is the confidential and proprietary information of Alibaba.com ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */

package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.api;


import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.BaseDownResp;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.req.PackageDownReq;

/**
 * 批量文件打包下载操作接口
 *
 * @author jinzhaoyu
 */
public interface PackageApi {
    /**
     * 批量文件打包下载,<b>下载最大字节数为2147483647</b><br>
     * http://dl.django.t.taobao.com/rest/1.2/package <br/>
     *
     * @param packageDownReq 注意：下载最大字节数为 2147483647
     * @return
     */
    BaseDownResp downloadPackage(PackageDownReq packageDownReq);

}
