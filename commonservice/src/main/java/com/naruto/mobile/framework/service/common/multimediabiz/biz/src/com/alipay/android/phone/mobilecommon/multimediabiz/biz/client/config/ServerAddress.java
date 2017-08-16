/*
 * Copyright 2014 Alibaba.com All right reserved.
 * This software is the confidential and proprietary information of Alibaba.com ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.config;

/**
 * 以下几类服务器地址：上传服务器、下载服务器、API服务器的地址
 * @author jinzhaoyu
 * @see ServerType
 */
public abstract class ServerAddress {

    /**
     * 服务器的类型
     */
    public static enum ServerType{
        UPLOAD,
        DOWNLOAD,
        API;
    }

    public static final String SERVER_ADDR_FROMAT = "%1$s:%2$d";

	protected String upServerHost;
	protected int upServerPort = 80;
	
	protected String dlServerHost;
	protected int dlServerPort = 80;
	
	protected String apiServerHost;
	protected int apiServerPort = 80;

	/**
	 * @return the upServerHost
	 */
	public String getUploadServerHost() {
		return upServerHost;
	}

	/**
	 * @return the upServerPort
	 */
	public int getUploadServerPort() {
		return upServerPort;
	}

    /**
     * 上传服务器地址,格式如： "x.x.x.x:80"
     * @return the upServerHost and upServerPort
     */
    public String getUploadServerAddr() {
        return String.format(SERVER_ADDR_FROMAT, getUploadServerHost(),getUploadServerPort());
    }

	/**
	 * @return the dlServerHost
	 */
	public String getDownloadServerHost() {
		return dlServerHost;
	}

	/**
	 * @return the dlServerPort
	 */
	public int getDownloandServerPort() {
		return dlServerPort;
	}

    /**
     * 下载服务器地址,格式如： "x.x.x.x:80"
     * @return the dlServerHost
     */
    public String getDownloadServerAddr() {
        return String.format(SERVER_ADDR_FROMAT, getDownloadServerHost(),getDownloandServerPort());
    }

	/**
	 * @return the apiServerHost
	 */
	public String getApiServerHost() {
		return apiServerHost;
	}

	/**
	 * @return the apiServerPort
	 */
	public int getApiServerPort() {
		return apiServerPort;
	}

    /**
     * API服务器地址,格式如： "x.x.x.x:80"
     * @return the apiServerHost
     */
    public String getApiServerAddr(){
        return String.format(SERVER_ADDR_FROMAT, getApiServerHost(),getApiServerPort());
    }
}
