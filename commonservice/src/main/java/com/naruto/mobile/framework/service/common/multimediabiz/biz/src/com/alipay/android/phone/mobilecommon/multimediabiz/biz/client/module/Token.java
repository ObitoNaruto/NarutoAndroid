/*
 * Copyright 2014 Alibaba.com All right reserved.
 * This software is the confidential and proprietary information of Alibaba.com ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module;

import com.alibaba.fastjson.annotation.JSONField;

public class Token {
	private String token;
	private long expireTime;
	private long createTime;
	@JSONField(name = "currentTime")
	private long serverTime;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public long getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(long expireTime) {
		this.expireTime = expireTime;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	/**
	 * @return the serverTime
	 */
	public long getServerTime() {
		return serverTime;
	}

	/**
	 * @param serverTime
	 *            the serverTime to set
	 */
	public void setServerTime(long serverTime) {
		this.serverTime = serverTime;
	}

    @Override
    public String toString() {
        return "Token{" +
                "token='" + token + '\'' +
                ", expireTime=" + expireTime +
                ", createTime=" + createTime +
                ", serverTime=" + serverTime +
                '}';
    }
}
