/*
 * Copyright 2014 Alibaba.com All right reserved.
 * This software is the confidential and proprietary information of Alibaba.com ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */

package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.impl;

import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.config.ConnectionManager;

import org.apache.http.client.HttpClient;


/**
 * @author zhenghui
 */
public class HttpConnectionManager extends ConnectionManager<HttpClient> {
    private HttpClientProxy mHttpClientProxy;

    public HttpConnectionManager() {
        super();
    }
	
    @Override
    public HttpClient getConnection() {
        return getConnection(true);
    }

    @Override
    public HttpClient getConnection(boolean bSpdy) {
        if (mHttpClientProxy == null) {
            mHttpClientProxy = new HttpClientProxy(bSpdy);
        }

        return mHttpClientProxy;
    }

    @Override
    public void setProxy(String ip,String host){
        setProxy(ip,host,true);
    }

    @Override
    public void setProxy(String ip,String host,boolean bSpdy){
        if(mHttpClientProxy == null){
            mHttpClientProxy = new HttpClientProxy(bSpdy);
        }

        if(mHttpClientProxy != null){
            mHttpClientProxy.setProxy(ip,host,bSpdy);
        }
    }

    @Override
    public void shutdown() {
        if(mHttpClientProxy != null){
            mHttpClientProxy.shutdown();
            mHttpClientProxy = null;
        }
    }

    @Override
    public boolean isShutdown() {
        boolean ret = true;
        if(mHttpClientProxy != null){
            ret = mHttpClientProxy.isShutdown();
        }

        return ret;
    }
}
