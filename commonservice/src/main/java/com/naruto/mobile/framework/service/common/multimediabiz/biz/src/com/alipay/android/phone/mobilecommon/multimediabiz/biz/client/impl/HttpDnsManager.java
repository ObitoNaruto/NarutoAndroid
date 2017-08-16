package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.impl;

import android.text.TextUtils;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by xiangui.fxg on 2015/7/3.
 */
public class HttpDnsManager {
    private static HttpDnsManager mInstall = new HttpDnsManager();
    //失效时间5分钟
    private static int IP_TIME_OUT_INTERVAL = 300000;//5* 60 * 1000;
    private ConcurrentHashMap<String,Long> mIpMap = new ConcurrentHashMap<String,Long>();

    private HttpDnsManager(){

    }

    public static HttpDnsManager getInstance(){
        return mInstall;
    }

    public long getValue(String key){
        if(!TextUtils.isEmpty(key) && mIpMap.containsKey(key)){
            return mIpMap.get(key);
        }

        return 0;
    }

    public void putValue(String key,long val){
        if(!TextUtils.isEmpty(key)){
            mIpMap.put(key,val);
        }
    }

    public boolean isIpTimeOut(String key){
        return Math.abs(System.currentTimeMillis() - getValue(key)) >= IP_TIME_OUT_INTERVAL;
    }
}
