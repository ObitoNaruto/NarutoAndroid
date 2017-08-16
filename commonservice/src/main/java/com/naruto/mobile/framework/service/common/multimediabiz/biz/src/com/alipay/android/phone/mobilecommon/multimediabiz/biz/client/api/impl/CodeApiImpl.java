/*
 * Copyright 2014 Alibaba.com All right reserved.
 * This software is the confidential and proprietary information of Alibaba.com ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */

package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.api.impl;

import com.alibaba.fastjson.JSON;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.DjangoClient;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.api.CodeApi;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.api.TokenApi;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.api.infos.CodeApiInfo;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.config.ConnectionManager;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.exception.DjangoClientException;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.req.GetCodesReq;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.resp.GetCodesResp;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.util.DjangoConstant;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.util.DjangoUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.util.HttpClientUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.util.LiteStringUtils;
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

import android.util.Log;


import java.util.ArrayList;
import java.util.List;

/**
 * @author jinzhaoyu
 */
public class CodeApiImpl extends AbstractApiImpl implements CodeApi {

    private TokenApi tokenApi;

    public CodeApiImpl(DjangoClient djangoClient, ConnectionManager<HttpClient> connectionManager) {
        super(djangoClient, connectionManager);
        tokenApi = djangoClient.getTokenApi();
    }

    @Override
    public GetCodesResp getCodes(GetCodesReq getCodesReq) {
        GetCodesResp response = null;
        HttpGet method = null;
        HttpResponse resp = null;

        try {
            if (LiteStringUtils.isBlank(getCodesReq.getResources())) {
                throw new DjangoClientException("field[resource] is null");
            }

            String timestampStr = String.valueOf(System.currentTimeMillis());
            String acl = genAclString(getCodesReq.getResources(), timestampStr);

//            method = new HttpGet(CodeApiInfo.GET_CODES.getApi());
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(DjangoConstant.TRACE_ID, getTraceId()));
			
			String api = CodeApiInfo.GET_CODES.getApi();
            if(CommonUtils.isWapNetWork()){
                api = CodeApiInfo.GET_CODES.getUrlApi();
                connectionManager.setProxy(CodeApiInfo.GET_CODES.getIp(),CodeApiInfo.GET_CODES.getHost());
            }

            method = new HttpGet(HttpClientUtils.urlAppendParams(api, params));
            method.addHeader("Host", CodeApiInfo.GET_CODES.getHost());
            method.addHeader("Cookie",getCookieString());
            method.addHeader("token", tokenApi.getTokenString());
            method.addHeader("resources", getCodesReq.getResources());
            method.addHeader("timestamp", timestampStr);
            method.addHeader("acl",acl);

            resp = connectionManager.getConnection().execute(method);

            if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = resp.getEntity();
                String content = EntityUtils.toString(entity, DjangoConstant.DEFAULT_CHARSET_NAME);
                if (DjangoClient.DEBUG) {
                    Logger.D(DjangoClient.LOG_TAG, "getCodes() :" + content);
                }
                response = JSON.parseObject(content, GetCodesResp.class);
            } else {
                response = new GetCodesResp();
                response.setCode(resp.getStatusLine().getStatusCode());
                response.setMsg("http invoker error!");
            }
        } catch (Exception e) {
            Logger.E(DjangoClient.LOG_TAG, e, e.getMessage());
            response = new GetCodesResp();
            response.setCode(DjangoConstant.DJANGO_400);
            response.setMsg(e.getMessage());
        } finally {
            DjangoUtils.releaseConnection(method, resp);
        }
        return response;
    }
}
