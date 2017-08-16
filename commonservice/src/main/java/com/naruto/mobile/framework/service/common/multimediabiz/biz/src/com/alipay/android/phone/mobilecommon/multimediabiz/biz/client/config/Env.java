/*
 * Copyright 2014 Alibaba.com All right reserved.
 * This software is the confidential and proprietary information of Alibaba.com ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.config;

/**
 * DjangoSDK连接的环境
 * @author jinzhaoyu
 */
public enum Env {
	ONLINE(new OnlineServerAddress()),
	PRE_RELEASE(new PreReleaseServerAddress()),
	DAILY(new DailyServerAddress()),
	;
	
	private ServerAddress addr;
	Env(ServerAddress addr){
		this.addr = addr;
	}
	
	/**
	 * 获取对应环境下,Django各个服务的地址
	 * @return the addr
	 */
	public ServerAddress getServerAddress() {
		return addr;
	}
	
	
}
