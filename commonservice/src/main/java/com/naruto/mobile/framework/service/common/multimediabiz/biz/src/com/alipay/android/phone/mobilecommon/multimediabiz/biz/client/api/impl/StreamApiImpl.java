/*
 * Copyright 2014 Alibaba.com All right reserved.
 * This software is the confidential and proprietary information of Alibaba.com ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */

package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.api.impl;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.DjangoClient;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.api.StreamApi;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.api.TokenApi;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.api.infos.StreamApiInfo;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.config.ConnectionManager;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.exception.DjangoClientException;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.BaseDownResp;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.req.StreamApiReq;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.resp.StreamCommitTaskResp;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.resp.StreamQueryTaskResp;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.util.DjangoConstant;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.util.HttpClientUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.util.LiteStringUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.CommonUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.util.Log;


/**
 * 通用文件流处理接口的具体实现类
 *
 * @author jinzhaoyu
 */
public class StreamApiImpl extends AbstractApiImpl implements StreamApi {

    private TokenApi tokenApi;

    public StreamApiImpl(DjangoClient djangoClient, ConnectionManager<HttpClient> connectionManager) {
        super(djangoClient, connectionManager);
        this.tokenApi = djangoClient.getTokenApi();
    }

    @Override
    public StreamCommitTaskResp commitTask(StreamApiReq streamApiReq) {
        StreamCommitTaskResp response = null;
        try {
            if (LiteStringUtils.isBlank(streamApiReq.getFileId())) {
                throw new DjangoClientException("field[fileId] can not be null");
            } else if (LiteStringUtils.isBlank(streamApiReq.getModule())) {
                throw new DjangoClientException("field[module] can not be null");
            }

            String timestampStr = String.valueOf(System.currentTimeMillis());
            String acl = genAclString(streamApiReq.getFileId(), timestampStr);

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("token", tokenApi.getTokenString()));
            params.add(new BasicNameValuePair("fileId", streamApiReq.getFileId()));
            params.add(new BasicNameValuePair("timestamp", timestampStr));
            params.add(new BasicNameValuePair("acl", acl));
            params.add(new BasicNameValuePair("module", streamApiReq.getModule()));

            if(LiteStringUtils.isNotBlank(streamApiReq.getSource()))
                params.add(new BasicNameValuePair("source", streamApiReq.getSource()));
            if(streamApiReq.getSize()>0)
                params.add(new BasicNameValuePair("size", String.valueOf(streamApiReq.getSize())));
            if(LiteStringUtils.isNotBlank(streamApiReq.getExt()))
                params.add(new BasicNameValuePair("ext", streamApiReq.getExt()));
            if(streamApiReq.getCreateTime()>0) {
                params.add(new BasicNameValuePair("createTime", String.valueOf(streamApiReq.getCreateTime())));
            }
            List<NameValuePair> urlParams = new ArrayList<NameValuePair>();
            String traceId = getTraceId();
            if (LiteStringUtils.isNotBlank(traceId)) {
                urlParams.add(new BasicNameValuePair(DjangoConstant.TRACE_ID, traceId));
            }

 			String api = StreamApiInfo.COMMIT_TASK.getApi();
            if(CommonUtils.isWapNetWork()){
                api = StreamApiInfo.COMMIT_TASK.getUrlApi();
                connectionManager.setProxy(StreamApiInfo.COMMIT_TASK.getIp(),StreamApiInfo.COMMIT_TASK.getHost());
            }
			
            HttpPost method = new HttpPost(HttpClientUtils.urlAppendParams(api, urlParams));
            UrlEncodedFormEntity urlEntity = new UrlEncodedFormEntity(params, DjangoConstant.DEFAULT_CHARSET_NAME);
            method.setEntity(urlEntity);
            method.addHeader("Host", StreamApiInfo.COMMIT_TASK.getHost());
            method.addHeader("Cookie", getCookieString());

            HttpResponse httpResponse = connectionManager.getConnection().execute(method);
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = httpResponse.getEntity();
                String content = EntityUtils.toString(entity, DjangoConstant.DEFAULT_CHARSET_NAME);
                if (DjangoClient.DEBUG) {
                    Logger.D(DjangoClient.LOG_TAG, "commitTask() :" + content);
                }
                response = JSON.parseObject(content, StreamCommitTaskResp.class);
            } else {
                response = new StreamCommitTaskResp();
                response.setCode(httpResponse.getStatusLine().getStatusCode());
                response.setMsg("Http invoker error: " + response.getCode());
            }
        } catch (Exception e) {
            Logger.E(DjangoClient.LOG_TAG, e, e.getMessage());
            response = new StreamCommitTaskResp();
            response.setCode(DjangoConstant.DJANGO_400);
            response.setMsg(e.getMessage());
        }

        return response;
    }

    @Override
    public StreamQueryTaskResp queryTask(StreamApiReq streamApiReq) {
        StreamQueryTaskResp response = null;
        try {
            if (LiteStringUtils.isBlank(streamApiReq.getFileId())) {
                throw new DjangoClientException("field[fileId] can not be null");
            } else if (LiteStringUtils.isBlank(streamApiReq.getModule())) {
                throw new DjangoClientException("field[module] can not be null");
            }

            String timestampStr = String.valueOf(System.currentTimeMillis());
            String acl = genAclString(streamApiReq.getFileId(), timestampStr);

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("token", tokenApi.getTokenString()));
            params.add(new BasicNameValuePair("fileId", streamApiReq.getFileId()));
            params.add(new BasicNameValuePair("timestamp", timestampStr));
            params.add(new BasicNameValuePair("acl", acl));
            params.add(new BasicNameValuePair("module", streamApiReq.getModule()));
            params.add(new BasicNameValuePair(DjangoConstant.TRACE_ID, getTraceId()));

//            URI uri = new URIBuilder(StreamApiInfo.QUERY_TASK.getApi()).setParameters(params).build();

            String api = StreamApiInfo.QUERY_TASK.getApi();
            if(CommonUtils.isWapNetWork()){
                api = StreamApiInfo.QUERY_TASK.getUrlApi();
                connectionManager.setProxy(StreamApiInfo.QUERY_TASK.getIp(),StreamApiInfo.QUERY_TASK.getHost());
            }
            HttpGet method = new HttpGet(HttpClientUtils.urlAppendParams(api, params));
            method.addHeader("Host", StreamApiInfo.QUERY_TASK.getHost());
            method.addHeader("Cookie", getCookieString());

            HttpResponse httpResponse = connectionManager.getConnection().execute(method);
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = httpResponse.getEntity();
                String content = EntityUtils.toString(entity, DjangoConstant.DEFAULT_CHARSET_NAME);
                if (DjangoClient.DEBUG) {
                    Logger.D(DjangoClient.LOG_TAG, "queryTask() :" + content);
                }
                response = JSON.parseObject(content, StreamQueryTaskResp.class);
            } else {
                response = new StreamQueryTaskResp();
                response.setCode(httpResponse.getStatusLine().getStatusCode());
                response.setMsg("Http invoker error: " + response.getCode());
            }
        } catch (Exception e) {
            Logger.E(DjangoClient.LOG_TAG, e, e.getMessage());
            response = new StreamQueryTaskResp();
            response.setCode(DjangoConstant.DJANGO_400);
            response.setMsg(e.getMessage());
        }

        return response;
    }

    @Override
    public BaseDownResp getContent(StreamApiReq streamApiReq) {
        BaseDownResp response = new BaseDownResp();
        try {
            if (LiteStringUtils.isBlank(streamApiReq.getFileId())) {
                throw new DjangoClientException("field[fileId] can not be null");
            } else if (LiteStringUtils.isBlank(streamApiReq.getModule())) {
                throw new DjangoClientException("field[module] can not be null");
            }

            String timestampStr = String.valueOf(System.currentTimeMillis());
            String acl = genAclString(streamApiReq.getFileId(), timestampStr);

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("token", tokenApi.getTokenString()));
            params.add(new BasicNameValuePair("fileId", streamApiReq.getFileId()));
            params.add(new BasicNameValuePair("timestamp", timestampStr));
            params.add(new BasicNameValuePair("acl", acl));
            params.add(new BasicNameValuePair("module", streamApiReq.getModule()));

            if(LiteStringUtils.isNotBlank(streamApiReq.getFileName()))
                 params.add(new BasicNameValuePair("fileName", streamApiReq.getFileName()));

            if(LiteStringUtils.isNotBlank(streamApiReq.getFormat())){
                params.add(new BasicNameValuePair("format", streamApiReq.getFormat()));
            }
            params.add(new BasicNameValuePair(DjangoConstant.TRACE_ID, getTraceId()));

//            URI uri = new URIBuilder(StreamApiInfo.GET_CONTENT.getApi()).setParameters(params).build();

            String api = StreamApiInfo.GET_CONTENT.getApi();
            if(CommonUtils.isWapNetWork()){
                api = StreamApiInfo.GET_CONTENT.getUrlApi();
                connectionManager.setProxy(StreamApiInfo.GET_CONTENT.getIp(),StreamApiInfo.GET_CONTENT.getHost());
            }
            HttpGet method = new HttpGet(HttpClientUtils.urlAppendParams(api, params));
            method.addHeader("Host", StreamApiInfo.GET_CONTENT.getHost());
            method.addHeader("Cookie", getCookieString());
            HttpResponse httpResponse = connectionManager.getConnection().execute(method);

            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                response.setResp(httpResponse);
                response.setCode(DjangoConstant.DJANGO_OK);
            } else {
                response.setCode(httpResponse.getStatusLine().getStatusCode());
                response.setMsg("Http invoker error :" + response.getCode());
            }
        } catch (Exception e) {
            Logger.E(DjangoClient.LOG_TAG, e, e.getMessage());
            response.setCode(DjangoConstant.DJANGO_400);
            response.setMsg(e.getMessage());
        }

        return response;
    }
}
