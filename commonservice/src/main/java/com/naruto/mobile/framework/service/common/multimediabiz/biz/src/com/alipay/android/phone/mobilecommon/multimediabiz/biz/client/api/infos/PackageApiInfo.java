/*
 * Copyright 2014 Alibaba.com All right reserved.
 * This software is the confidential and proprietary information of Alibaba.com ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */

package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.api.infos;

import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.config.ServerAddress;

/**
 * 与批量文件打包相关的API集合
 *
 * @author jinzhaoyu
 */
public class PackageApiInfo extends BaseApiInfo{
    /**
     * 批量文件打包下载,下载最大字节数为2147483647 <br/>
     * http://dl.django.t.taobao.com/rest/1.2/package
     */
    public static PackageApiInfo DOWNLOAD_PACKAGE = new PackageApiInfo(ServerAddress.ServerType.DOWNLOAD, "rest/1.2/package", HttpMethod.POST);

    private PackageApiInfo(ServerAddress.ServerType serverType, String apiPath, HttpMethod httpMethod) {
        super(serverType, apiPath, httpMethod);
    }
}
