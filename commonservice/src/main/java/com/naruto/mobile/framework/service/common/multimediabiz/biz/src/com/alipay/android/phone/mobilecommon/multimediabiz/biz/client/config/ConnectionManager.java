/*
 * Copyright 2014 Alibaba.com All right reserved.
 * This software is the confidential and proprietary information of Alibaba.com ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.config;

import android.annotation.SuppressLint;


import java.util.ArrayList;
import java.util.List;

import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.util.DjangoConstant;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.util.HttpConstants;

@SuppressLint("DefaultLocale")
public abstract class ConnectionManager<T>
{

	protected int maxConnection = 10;
	protected int maxRouteConnection = 10;
	protected int socketTimeout = HttpConstants.SOCKET_TIME_OUT;
	protected int connectionTimeout = HttpConstants.CONNECTION_TIME_OUT;
	protected int poolTimeout = HttpConstants.POOL_TIME_OUT;

	private String appKey = "aliwallet";
	// 日常环境 0846ea8b62e145c1a25bbffd490f2901
	// 线上0b82ce6ba9c6431cb64ea50c9fbd580c

	// 线上
	private int appId = DjangoConstant.APP_ID_ONLINE;
	// 日常
	// private int appId = DjangoConstant.APP_ID_DAILY;


	private String acl = "acl";
	private String uid = "uid";

	public ConnectionManager() {

	}

	/**
	 * @return
	 */
	public abstract T getConnection();

    /**
     * @return
     */
    public abstract T getConnection(boolean bSpdy);

	public abstract void shutdown();

	public abstract boolean isShutdown();

	public int getAppId() {
		return appId;
	}

	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public String getAcl() {
		return acl;
	}

	public void setAcl(String acl) {
		this.acl = acl;
	}

    public abstract void setProxy(String ip,String host);

    public abstract void setProxy(String ip,String host,boolean bSpdy);

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}
}
