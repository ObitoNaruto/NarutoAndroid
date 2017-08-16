package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.api.infos;

import android.text.TextUtils;

import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.config.EnvSwitcher;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.config.ServerAddress;
//import com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.config.EnvSwitcher;
//import com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.config.ServerAddress;
//import com.alipay.mobile.common.transport.httpdns.AlipayHttpDnsClient;
//import com.alipay.mobile.common.transport.httpdns.HttpDns;

/**
 * Created by xiangui.fxg on 2015/5/1.
 */
class ApiInfoUtil {
    public static String getIpInfoByHost(String hostName){
//        AlipayHttpDnsClient client = AlipayHttpDnsClient.getDnsClient();
//        HttpDns.HttpdnsIP ipInfoByHost = null;
//        if(client != null){
//            ipInfoByHost = client.queryLocalIPByHost(hostName);
//        }
//
//        if(EnvSwitcher.isEnableHost2Ip() && ipInfoByHost != null){
//            String ip = ipInfoByHost.getIp();
//            if(TextUtils.isEmpty(ip)){
//                ip = hostName;
//            }
//
//            return ip;
//        }else{
//            return hostName;
//        }
        return null;
    }

    public static String getServerAddress(String host,int port){
        return String.format(ServerAddress.SERVER_ADDR_FROMAT, host,port);
    }
}
