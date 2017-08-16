/*
 * Copyright 2014 Alibaba.com All right reserved.
 * This software is the confidential and proprietary information of Alibaba.com ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */

package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.api.infos;

import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.config.ServerAddress;

/**
 * 与图像相关的API集合
 *
 * @author jinzhaoyu
 */
public class ImageApiInfo extends BaseApiInfo{
    /**
     * 批量下载图像缩略图<br/>
     * http://dl.django.t.taobao.com/rest/1.0/image
     */
    public static ImageApiInfo DOWNLOAD_THUMBNAILS = new ImageApiInfo(ServerAddress.ServerType.DOWNLOAD, "rest/1.0/image", HttpMethod.GET);

    /**
     * 水印缩略图批量下载<br/>
     * http://dl.django.t.taobao.com/rest/2.2/image
     */
    public static ImageApiInfo DOWNLOAD_THUMBNAILS_WARTERMARK = new ImageApiInfo(ServerAddress.ServerType.DOWNLOAD, "rest/2.2/image", HttpMethod.GET);

    private ImageApiInfo(ServerAddress.ServerType serverType, String apiPath, HttpMethod httpMethod) {
        super(serverType, apiPath, httpMethod);
    }
}
