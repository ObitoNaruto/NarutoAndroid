/*
 * Copyright 2014 Alibaba.com All right reserved.
 * This software is the confidential and proprietary information of Alibaba.com ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */

package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.api.impl;

import android.util.Base64;
//import com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.DjangoClient;
//import com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.config.ConnectionManager;
//import com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.config.EnvSwitcher;
//import com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.exception.DjangoClientException;
//import com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.util.ByteUtil;
//import com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.util.DjangoConstant;
//import com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.util.DjangoUtils;
//import com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.util.MD5Utils;
//import com.alipay.android.phone.mobilecommon.multimediabiz.biz.http.apache.entity.mine.HttpMultipartMode;
//import com.alipay.android.phone.mobilecommon.multimediabiz.biz.http.apache.entity.mine.MultipartEntityBuilder;
//import com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.AppUtils;
//import com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Logger;
//import com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.UuidManager;
//import com.alipay.mobile.common.transport.utils.NetworkUtils;
import org.apache.http.client.HttpClient;

import java.util.UUID;

import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.DjangoClient;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.config.ConnectionManager;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.config.EnvSwitcher;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.exception.DjangoClientException;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.util.ByteUtil;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.util.DjangoConstant;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.http.apache.entity.mine.HttpMultipartMode;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.http.apache.entity.mine.MultipartEntityBuilder;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.AppUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Logger;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.UuidManager;

/**
 * 提供通用API 实现的抽象类
 *
 * @author jinzhaoyu
 */
public abstract class AbstractApiImpl {

    protected ConnectionManager<HttpClient> connectionManager;
    protected DjangoClient djangoClient;

    public AbstractApiImpl(DjangoClient djangoClient, ConnectionManager<HttpClient> connectionManager) {
        this.djangoClient = djangoClient;
        this.connectionManager = connectionManager;
    }

    /**
     * 生成一个新的com.alipay.apache.http.entity.mime.MultipartEntityBuilder
     *
     * @return
     */
    protected MultipartEntityBuilder genMultipartEntityBuilder() {
        MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
        multipartEntityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        multipartEntityBuilder.setCharset(DjangoConstant.DEFAULT_CHARSET);
        return multipartEntityBuilder;
    }

    /**
     * 根据文件ID生成ACL字符串，ACL算法为：md5 (fileIds + timestamp + DJANGO_UID + DJANGO_ACL + appSecret)
     *
     * @param id
     * @param timestamp
     * @return
     */
    protected String genAclString(String id, String timestamp) {
        return EnvSwitcher.getAclString(id, timestamp, connectionManager);
    }

    /**
     * 获取默认Cookie的内容,
     *
     * @return
     * @see com.naruto.android.phone.mobilecommon.multimediabiz.biz.client.util.DjangoConstant#COOKIE_FORMAT
     */
    protected String getCookieString() {
        return String.format(DjangoConstant.COOKIE_FORMAT, connectionManager.getUid(), connectionManager.getAcl());
    }

    public String getTraceId() {
        return getTraceId(false);
    }

    public String getTokenTraceId() {
        return getTraceId(true);
    }

    /**
     * @return
     * @ref http://docs.alibaba-inc.com/pages/viewpage.action?spm=0.0.0.0.ibBDPy&pageId=252122975
     * base64 (version + appId + channel + timestamp + uuid)
     * len: base64(1 + 4 + 4 + 8 + 16 = 33 bytes) = 44 chars
     */
    private String getTraceId(boolean isGetToken) {
        try {
            UUID uuid = UuidManager.get().getUUID();
            byte[] uuidBytes = ByteUtil.UUIDToByteArray(uuid);  //16 bytes
            byte[] timeStampBytes = ByteUtil.longToByteArray(getServerTime(djangoClient, isGetToken)); //8 bytes
            byte[] channelBytes = getChannelBytes(); // 4 bytes
            byte[] appIdBytes = ByteUtil.intToByteArray(connectionManager.getAppId()); // 4 bytes

            int version = DjangoConstant.VERSION;
            byte[] versionBytes = new byte[1]; //1 byte
            versionBytes[0] = (byte) (version & 0xff); // 最低位

            int total = 0;
            total += uuidBytes.length;
            total += timeStampBytes.length;
            total += channelBytes.length;
            total += appIdBytes.length;
            total += versionBytes.length;

            byte[] formatBytes = new byte[total]; //33 bytes 注意和前面各个字段保持长度一致
            int destPos = 0;
            System.arraycopy(uuidBytes, 0, formatBytes, destPos, uuidBytes.length);
            destPos += uuidBytes.length;
            System.arraycopy(timeStampBytes, 0, formatBytes, destPos, timeStampBytes.length);
            destPos += timeStampBytes.length;
            System.arraycopy(channelBytes, 0, formatBytes, destPos, channelBytes.length);
            destPos += channelBytes.length;
            System.arraycopy(appIdBytes, 0, formatBytes, destPos, appIdBytes.length);
            destPos += appIdBytes.length;
            System.arraycopy(versionBytes, 0, formatBytes, destPos, versionBytes.length);
            destPos += versionBytes.length;

            // refer org.apache.commons.codec.binary.Base64.encodeBase64URLSafe.
            // 注意这里encode flag, 要放到url里面做参数的!!!!!!!!!!!!!!!!!!
            // NO_PADDING 去掉尾部的=
            // URL_SAFE 不使用对URL和文件名有特殊意义的字符
            // NO_WRAP 略去所有的换行符
            String base64String = Base64.encodeToString(formatBytes, Base64.NO_PADDING | Base64.NO_WRAP | Base64.URL_SAFE);

//            Logger.I(DjangoClient.LOG_TAG, "uuid: " + uuid);
//            Logger.I(DjangoClient.LOG_TAG, "byteArrayToUUID: " + ByteUtil.byteArrayToUUID(uuidBytes));
//            Logger.I(DjangoClient.LOG_TAG, "channel: " + ByteUtil.byteArrayToInt(channelBytes));
//            Logger.I(DjangoClient.LOG_TAG, "base64String: " + base64String);

            return base64String;
        } catch (Exception e) {
            Logger.E(DjangoClient.LOG_TAG,e,"getTraceId exception");
        }
        return "";
    }

    private long getServerTime(DjangoClient djangoClient, boolean isGetToken) {
        long serverTime = 0;
        int count = 3;
        while (count > 0) {
            try {
                serverTime = djangoClient.getCorrectServerTime();
            } catch (DjangoClientException e) {
                Logger.E(DjangoClient.LOG_TAG, e, "getCorrectServerTime exception");
            }
            if (serverTime > 0) {
                break;
            }
            // 取token时, 如未取过服务器时间, 用本地时间
            if (isGetToken) {
                Logger.P(DjangoClient.LOG_TAG, "getServerTime use local timestamp");
                serverTime = System.currentTimeMillis();
                break;
            }
            try {
                djangoClient.getTokenApi().getTokenString();
            } catch (Exception e) {
                Logger.E(DjangoClient.LOG_TAG, e, "getTokenString exception");
                break;
            }
            count--;
        }
        return serverTime;
    }

    private byte[] getChannelBytes() {
        int netType = 1/*DjangoUtils.convertNetworkType(NetworkUtils.getNetworkType(AppUtils.getApplicationContext()))*/;
        int minorVer = AppUtils.getMinorVersion(AppUtils.getApplicationContext());
        int mainVer = AppUtils.getMainVersion(AppUtils.getApplicationContext());
        int platform = DjangoConstant.PLAT_ANDROID;
        byte[] channelBytes = new byte[4];
        channelBytes[0] = (byte) netType;
        channelBytes[1] = (byte) minorVer;
        channelBytes[2] = (byte) mainVer;
        channelBytes[3] = (byte) platform;
        return channelBytes;
    }
}
