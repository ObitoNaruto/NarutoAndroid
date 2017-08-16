/*
 * Copyright 2014 Alibaba.com All right reserved.
 * This software is the confidential and proprietary information of Alibaba.com ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */

package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.api;


import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.req.ThumbnailsDownReq;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.resp.ThumbnailsDownResp;

/**
 * 图像相关API操作接口
 *
 * @author jinzhaoyu
 */
public interface ImageApi {
    /**
     * 批量下载图像缩略图,<b>必须在 thumbnailsDownReq 参数中指定fileIds 和 zoom </b><br>
     * http://dl.django.t.taobao.com/rest/1.0/image <br/>
     *
     * @param thumbnailsDownReq 其中的zoom参数是指图片缩放格式，如 100x100。参见：http://baike.corp.taobao.com/index.php/CS_RD/tfs/http_server
     * @return
     */
    ThumbnailsDownResp downloadThumbnails(ThumbnailsDownReq thumbnailsDownReq);

    /**
     * 支持原生http/https请求，兼容老的通过网络地址的图片下载
     * @return
     */
    ThumbnailsDownResp downloadNormal(String url);

    String getImageLoadUrl(ThumbnailsDownReq thumbnailsDownReq);

    String getImageLoadCookie();

}
