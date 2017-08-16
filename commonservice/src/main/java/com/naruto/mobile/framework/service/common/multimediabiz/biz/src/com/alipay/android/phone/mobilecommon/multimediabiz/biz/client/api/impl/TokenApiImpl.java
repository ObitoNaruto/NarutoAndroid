/*
 * Copyright 2014 Alibaba.com All right reserved.
 * This software is the confidential and proprietary information of Alibaba.com ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */

package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.api.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.alibaba.fastjson.JSON;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.DjangoClient;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.api.TokenApi;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.api.infos.TokenApiInfo;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.config.ConnectionManager;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.config.EnvSwitcher;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.exception.DjangoClientException;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.Token;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.resp.TokenResp;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.util.DjangoConstant;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.util.HttpClientUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.http.util.TextUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.CommonUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;



/**
 * Token相关操作的具体实现类
 *
 * @author jinzhaoyu
 */
public class TokenApiImpl extends AbstractApiImpl implements TokenApi {
    private Logger logger = Logger.getLogger("TokenApiImpl");
    /**
     * Token过期时间,23小时
     */
    public static final long TOKEN_EXPIRE_PERIOD_MILLS = 23 * 60 * 60000;
    //过期保护时间
    public static final long TOKEN_EXPIRE_PROTECT_INTERVAL = 3000;
    /**
     * Token刷新时间,5小时
     */
    public static final long TOKEN_REFRESH_PERIOD_MILLS = 5 * 60 * 60000;

    private Token token;
    private boolean taskRunning;
    private Timer getTokenTimer;
    private OnGotServerTimeListener onGotServerTimeListener;

    public TokenApiImpl(DjangoClient djangoClient, ConnectionManager<HttpClient> connectionManager,OnGotServerTimeListener onGotServerTimeListener) {
        super(djangoClient, connectionManager);
        this.onGotServerTimeListener = onGotServerTimeListener;
    }

    /**
     * @param refresh Indicate is force refresh token
     * @return
     */
    @Override
    public synchronized TokenResp getToken(boolean refresh) {
        TokenResp resp = new TokenResp();
        resp.setCode(DjangoConstant.DJANGO_OK);
        resp.setToken(token);
        if (refresh) {
            token = null;
        }

        try {
            if (token != null && token.getExpireTime() > 0
                    && (token.getExpireTime() - djangoClient.getCorrectServerTime()) < TOKEN_EXPIRE_PROTECT_INTERVAL) {
                token = null;
            }

        } catch (DjangoClientException e) {
            token = null;
        }

        if (token == null) {
            synchronized (TokenApiImpl.class) {
                if (token == null) {
                    resp = doGetToken();
                    if (!taskRunning) {
                        if (getTokenTimer != null) {
                            getTokenTimer.cancel();
                        }
                        getTokenTimer = new Timer();
                        getTokenTimer.schedule(new TokenTask(), TOKEN_REFRESH_PERIOD_MILLS, TOKEN_REFRESH_PERIOD_MILLS);
                        taskRunning = true;
                    }
                }
            }
        }

        return resp;
    }

    /**
     * 获取Token，如果本地已经有Token了并且没有过期，则直接返回，否则就请求重新获取
     *
     * @return
     * @throws DjangoClientException
     */
    @Override
    public synchronized String getTokenString() throws DjangoClientException {
        if (token != null && token.getExpireTime() > 0 && (token.getExpireTime() - djangoClient.getCorrectServerTime() >= TOKEN_EXPIRE_PROTECT_INTERVAL)) {
            return token.getToken();
        }

        TokenResp resp = getToken(true);
        if (resp.isSuccess()) {
            return resp.getToken().getToken();
        } else {
            throw new DjangoClientException(String.format("code:%s,msg:%s,ti:%s",
                    resp.getCode(), resp.getMsg(), resp.getTraceId()));
        }
    }

    /**
     * @return
     */
    private synchronized TokenResp doGetToken() {
        HttpGet method = null;
        TokenResp tokenResp = null;
        String traceId = "";
        try {
            long timestamp = System.currentTimeMillis();
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("timestamp", String.valueOf(timestamp)));
            params.add(new BasicNameValuePair("appKey", connectionManager.getAppKey()));
            String sign = EnvSwitcher.getSignature(timestamp);
            if(TextUtils.isEmpty(sign)){
                throw new DjangoClientException("get token error, sign is empty");
            }else{
                params.add(new BasicNameValuePair("signature",sign));
            }
            traceId = getTokenTraceId();
            params.add(new BasicNameValuePair(DjangoConstant.TRACE_ID, traceId));

            String api = TokenApiInfo.GET_TOKEN.getApi();
            if(CommonUtils.isWapNetWork()){
                api = TokenApiInfo.GET_TOKEN.getUrlApi();
				//由于api.django.t.taobao.com不支持spdy
                connectionManager.setProxy(TokenApiInfo.GET_TOKEN.getIp(),TokenApiInfo.GET_TOKEN.getHost(),false);
            }
            method = new HttpGet(HttpClientUtils.urlAppendParams(api, params));
            method.addHeader("Host", TokenApiInfo.GET_TOKEN.getHost());

            //由于api.django.t.taobao.com不支持spdy
            HttpResponse response = connectionManager.getConnection(false).execute(method);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();

                String resp = EntityUtils.toString(entity, DjangoConstant.DEFAULT_CHARSET_NAME);
                if (DjangoClient.DEBUG) {
                    //logger.d("GetTokenResp:" + resp);
                }
                tokenResp = JSON.parseObject(resp, TokenResp.class);
                if (!tokenResp.isSuccess()) {
                    throw new DjangoClientException("get token error, http response:" + resp);
                }
            } else {
                String exp = "get token error, http code:"
                        + response.getStatusLine().getStatusCode()+";uri="+method.getURI()+";host="+TokenApiInfo.GET_TOKEN.getHost();
                logger.d(exp);
                throw new DjangoClientException(exp);
            }

            this.token = tokenResp.getToken();
            //获取服务器时间
            if(onGotServerTimeListener != null ){
                onGotServerTimeListener.onGotServerTime(token.getServerTime());
            }
            tokenResp.setTraceId(traceId);
        } catch (Throwable e) {
            Logger.E(DjangoClient.LOG_TAG, e, e.getMessage());
            tokenResp = new TokenResp();
            tokenResp.setCode(DjangoConstant.DJANGO_400);
            tokenResp.setMsg(e.getMessage());
            tokenResp.setTraceId(traceId);
        } finally {
            if (method != null) {
                method.abort();
            }
        }
        return tokenResp;
    }


    //==============Inner classes============

    /**
     *
     */
    private class TokenTask extends TimerTask {
        @Override
        public void run() {
            doGetToken();
        }
    }
}
