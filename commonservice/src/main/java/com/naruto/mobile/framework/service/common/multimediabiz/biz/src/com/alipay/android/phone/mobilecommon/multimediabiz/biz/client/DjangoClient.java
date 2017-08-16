/*
 * Copyright 2014 Alibaba.com All right reserved.
 * This software is the confidential and proprietary information of Alibaba.com ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client;

import android.os.SystemClock;

import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.api.ChunkApi;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.api.CodeApi;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.api.FileApi;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.api.ImageApi;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.api.PackageApi;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.api.StreamApi;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.api.TokenApi;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.config.ConnectionManager;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.exception.DjangoClientException;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.BaseDownResp;


public abstract class DjangoClient {
	/**
	 * SDK是否打印debug信息，默认不打印，使用者可以变更
	 */
	public static boolean DEBUG = true;
	public static final String LOG_TAG = "DjangoClient";
	
    /**
     * 在某一时刻（从服务器获取到Token）,得到的服务器时间
     */
    protected static long                          correctServerTimeAtPoint = 0;
    /**
     * 在获取到{@link #correctServerTimeAtPoint}的同一时刻,系统从启动到现在已经流逝的时间，用于计算服务器校准时间
     */
    protected static long                          correctLocalElapsedRealtimeAtPoint = 0;

    private ConnectionManager<?> connectionManager;

    public abstract ConnectionManager<?> getConnectionManager();

    /**
     * 获取Token相关API操作类的实例
     * @return
     * @see com.naruto.android.phone.mobilecommon.multimediabiz.biz.client.api.TokenApi
     */
    public abstract TokenApi getTokenApi();

    /**
     * 获取文件相关API操作类的实例
     * @return
     */
    public abstract FileApi getFileApi();

    /**
     * 释放相关资源<br>
     * <b>注意：在发生了下载操作后必须要调用此方法释放资源</b>
     * @param resp
     */
    public abstract void release(BaseDownResp resp);

    /**
     * 获取分块相关API操作类的实例
     * @return
     */
    public abstract ChunkApi getChunkApi();

    /**
     * 获取访问Django资源Code相关的API操作类的实例
     * @return
     */
    public abstract CodeApi getCodeApi();

    /**
     * 获取图像相关API操作接口
     * @return
     */
    public abstract ImageApi getImageApi();

    /**
     * 获取打包批量下载文件相关的API操作接口
     * @return
     */
    public abstract PackageApi getPackageApi();

    /**
     * 获取通用文件流处理相关的API操作接口
     * @return
     */
    public abstract StreamApi getStreamApi();

    /**
     * 获取服务器校准时间<br>
     * 基于{@link com.naruto.android.phone.mobilecommon.multimediabiz.biz.client.api.TokenApi#getToken(boolean)}获取Token时拿到的服务器时间<br>
     * 如果子类没有设置过服务器时间{@link #correctServerTimeAtPoint}、
     * 和系统启动后消逝的时间数{@link #correctLocalElapsedRealtimeAtPoint}则会抛出异常
     * @return
     * @throws DjangoClientException
     */
    public synchronized long getCorrectServerTime()throws DjangoClientException{
    	if(correctServerTimeAtPoint == 0 || correctLocalElapsedRealtimeAtPoint == 0){
    		throw new DjangoClientException("take it easy, this will not cause crash. Please set variable 'correctServerTimeAtPoint' and 'correctLocalElapsedRealtimeAtPoint' in TokenApi.getToken(boolean)");
    	}
    	return correctServerTimeAtPoint + (SystemClock.elapsedRealtime() - correctLocalElapsedRealtimeAtPoint);
    }

}
