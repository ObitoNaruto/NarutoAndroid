//package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.impl;
//
//import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.util.HttpConstants;
//import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.AppUtils;
//
//import org.apache.http.params.CoreConnectionPNames;
//
///**
// * OkApacheClient对外封装类
// * Created by xiangui.fxg on 2015/7/21.
// */
//public class OkClientProxy extends OkApacheClient{
//    public OkClientProxy(){
//        super(AppUtils.getApplicationContext());
//        initParams();
//    }
//
//    private void initParams(){
//        getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, HttpConstants.SO_TIMEOUT);
//    }
//
//    //TODO spdy的网络流关闭接口
//    public void shutdown(){
//
//    }
//
//}
