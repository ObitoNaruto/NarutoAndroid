/*
 * Copyright 2014 Alibaba.com All right reserved.
 * This software is the confidential and proprietary information of Alibaba.com ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.req;

import android.os.Build;
import android.text.TextUtils;

import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.CacheUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.CommonUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.HasKey;

public class ThumbnailsDownReq extends HasKey {
	private String fileIds;
	private String zoom;
	private String source;
    private long range;
	private ComputeCallBack callback;
    public static final String DJANGO_ORIGINAL = "original";
    public static final String DJANGO_QUALITY80 = "q80";
    public static final String DJANGO_WEBP = "_.webp";
	private boolean webp = false;

//    private String urlParameter = "default";
	/**
	 * 
	 * @param fileIds
	 * @param zoom
	 *            请求图片缩略图的大小，图片缩放格式，100x100 请参考:http://baike.corp.taobao.com/index.php/CS_RD/
	 *            tfs/http_server
	 */
	public ThumbnailsDownReq(String fileIds, String zoom) {
		this.fileIds = fileIds;

        if(TextUtils.isEmpty(zoom) || DJANGO_ORIGINAL.equalsIgnoreCase(zoom)){
            //当zoom为空时下载原图
            this.zoom = DJANGO_ORIGINAL;
        }else{
            //默认使用质量为q80的方式下载，TODO 以后看是否需要可配
            this.zoom = zoom + DJANGO_QUALITY80;
        }

        //非wifi下且4.0系统以上下载才使用webp
        if(!CommonUtils.isWifiNetwork() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH){
            this.zoom += DJANGO_WEBP;
			webp = true;
        }
	}

	public boolean isWebp() {
		return webp;
	}

	public String getFileIds() {
		return fileIds;
	}

	public void setFileIds(String fileIds) {
		this.fileIds = fileIds;
	}

	public String getZoom() {
		return zoom;
	}

	public void setZoom(String zoom) {
		this.zoom = zoom;
	}

    public long getRange(){
        return range;
    }

    public void setRange(long range){
        this.range = range;
    }

	/**
	 * @return the source
	 */
	public String getSource() {
		return source;
	}

	/**
	 * @param source
	 *            the source to set
	 */
	public void setSource(String source) {
		this.source = source;
	}

//    public void setUrlParameter(String urlParameter){
//        this.urlParameter = urlParameter;
//    }
//
//    public String getUrlParameter() {
//        return urlParameter;
//    }

	@Override
	public Object getKey() {
		//todo:md5?
		return this.fileIds + CacheUtils.CACHE_KEY_SEPARATOR + this.zoom;
	}
}
