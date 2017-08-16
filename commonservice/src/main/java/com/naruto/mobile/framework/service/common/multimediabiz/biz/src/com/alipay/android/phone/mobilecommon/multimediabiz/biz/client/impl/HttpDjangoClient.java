/*
 * Copyright 2014 Alibaba.com All right reserved.
 * This software is the confidential and proprietary information of Alibaba.com ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.impl;

import java.util.Date;

import org.apache.http.client.HttpClient;

import android.content.Context;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.util.Log;

import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.DjangoClient;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.api.ChunkApi;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.api.CodeApi;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.api.FileApi;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.api.ImageApi;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.api.PackageApi;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.api.StreamApi;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.api.TokenApi;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.api.impl.ChunkApiImpl;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.api.impl.CodeApiImpl;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.api.impl.FileApiImpl;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.api.impl.ImageApiImpl;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.api.impl.PackageApiImpl;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.api.impl.StreamApiImpl;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.api.impl.TokenApiImpl;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.config.ConnectionManager;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.config.Env;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.config.EnvSwitcher;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.BaseDownResp;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.util.DjangoUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.util.LiteStringUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Logger;


public class HttpDjangoClient extends DjangoClient implements TokenApi.OnGotServerTimeListener {
    /**
     * 注册Django客户端
     *
     * @param context
     * @param appKey
     * @param acl
     * @param uid
     * @return
     */
    public static DjangoClient regeisterClient(Context context, String appKey,
                                               String acl, String uid) {
        return regeisterClient(context, appKey, acl, uid, EnvSwitcher.getCurrentEnv());
    }

    /**
     * 注册Django客户端，并指定其在哪个环境下运行
     *
     * @param context
     * @param appKey
     * @param acl
     * @param uid
     * @param env
     * @return
     */
    public static DjangoClient regeisterClient(Context context, String appKey,
                                               String acl, String uid, Env env) {
        if (context == null
                || LiteStringUtils.isBlank(appKey) || LiteStringUtils.isBlank(acl) || LiteStringUtils.isBlank(uid)) {
            throw new IllegalArgumentException("Parameter can not be null !");
        }

        ConnectionManager<HttpClient> conMgr = new HttpConnectionManager();
        conMgr.setAppKey(appKey);
        conMgr.setAcl(acl);
        conMgr.setUid(uid);
        HttpDjangoClient client = new HttpDjangoClient(context, conMgr);
        return client;
    }

    //=======API 处理类实例==============
    private static TokenApi tokenApi;
    private FileApi fileApi;
    private ChunkApi chunkApi;
    private CodeApi codeApi;
    private ImageApi imageApi;
    private PackageApi packageApi;
    private StreamApi streamApi;
    //=======API 处理类实例 End==========

    @SuppressWarnings("unused")
    private Context context;
    private ConnectionManager<HttpClient> connectionManager;

    public HttpDjangoClient(Context context, ConnectionManager<HttpClient> conn) {
        this.context = context;
        this.connectionManager = conn;
    }

    @Override
    public ConnectionManager<?> getConnectionManager() {
        return connectionManager;
    }

    @Override
    public void release(BaseDownResp resp) {
        DjangoUtils.releaseDownloadResponse(resp);
    }

    @Override
    public TokenApi getTokenApi() {
        if (tokenApi == null) {
            synchronized (HttpDjangoClient.class) {
                if (tokenApi == null) {
                    tokenApi = new TokenApiImpl(this, connectionManager, this);
                }
            }
        }
        return tokenApi;
    }

    @Override
    public synchronized FileApi getFileApi() {
        if (fileApi == null) {
            fileApi = new FileApiImpl(this, connectionManager);
        }
        return fileApi;
    }

    @Override
    public synchronized ChunkApi getChunkApi() {
        if (chunkApi == null) {
            chunkApi = new ChunkApiImpl(this, connectionManager);
        }
        return chunkApi;
    }

    @Override
    public synchronized CodeApi getCodeApi() {
        if (codeApi == null) {
            codeApi = new CodeApiImpl(this, connectionManager);
        }
        return codeApi;
    }

    @Override
    public synchronized ImageApi getImageApi() {
        if (imageApi == null) {
            imageApi = new ImageApiImpl(this, connectionManager);
        }
        return imageApi;
    }

    @Override
    public synchronized PackageApi getPackageApi() {
        if (packageApi == null) {
            packageApi = new PackageApiImpl(this, connectionManager);
        }
        return packageApi;
    }

    @Override
    public synchronized StreamApi getStreamApi() {
        if (streamApi == null) {
            streamApi = new StreamApiImpl(this, connectionManager);
        }
        return streamApi;
    }

    @Override
    public void onGotServerTime(long serverTime) {
        if (DEBUG) {
            Logger.D(LOG_TAG,"Update correct server time: "
                    + DateFormat.format("yyyy-MM-dd hh:mm:ss", new Date(serverTime)));
        }
        this.correctServerTimeAtPoint = serverTime;
        correctLocalElapsedRealtimeAtPoint = SystemClock.elapsedRealtime();
    }

}
