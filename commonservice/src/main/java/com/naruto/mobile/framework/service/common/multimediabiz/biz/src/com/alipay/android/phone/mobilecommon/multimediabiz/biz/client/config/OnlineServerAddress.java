/*
 * Copyright 2014 Alibaba.com All right reserved.
 * This software is the confidential and proprietary information of Alibaba.com ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.config;

/**
 * 线上服务器地址
 * @author jinzhaoyu
 */
public class OnlineServerAddress extends ServerAddress {
	public OnlineServerAddress() {
		upServerHost = "alipay.up.django.t.taobao.com";
		dlServerHost = "alipay.dl.django.t.taobao.com";
		apiServerHost = "api.django.t.taobao.com";
//		upServerHost = "110.75.48.185";
//		dlServerHost = "110.75.48.186";
//		apiServerHost = "110.75.48.184";
	}
}
