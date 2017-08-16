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
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.api.ChunkApi;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.api.TokenApi;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.api.infos.ChunkApiInfo;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.config.ConnectionManager;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.exception.DjangoClientException;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.req.ChunksDownReq;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.req.FileChunksInfoReq;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.req.GetChunksMetaReq;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.resp.ChunksDownResp;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.resp.FileChunksInfoResp;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.resp.GetChunksMetaResp;
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


/**
 * @author jinzhaoyu
 */
public class ChunkApiImpl extends AbstractApiImpl implements ChunkApi {

    private TokenApi tokenApi;

    public ChunkApiImpl(DjangoClient djangoClient, ConnectionManager<HttpClient> connectionManager) {
        super(djangoClient, connectionManager);
        this.tokenApi = djangoClient.getTokenApi();
    }

    @Override
    public FileChunksInfoResp getFileChunksInfo(FileChunksInfoReq fileChunksInfoReq) {
        FileChunksInfoResp response = null;

        HttpGet method = null;
        HttpResponse resp = null;

        try {
            String timestampStr = String.valueOf(System.currentTimeMillis());
            String acl = genAclString(fileChunksInfoReq.getFileId(), timestampStr);

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("token", tokenApi.getTokenString()));
            params.add(new BasicNameValuePair("fileId", fileChunksInfoReq.getFileId()));
            params.add(new BasicNameValuePair("timestamp",timestampStr));
            params.add(new BasicNameValuePair("acl",acl));
            params.add(new BasicNameValuePair(DjangoConstant.TRACE_ID, getTraceId()));

//            URI uri = new URIBuilder(ChunkApiInfo.GET_FILE_CHUNKS_INFO.getApi()).setParameters(params).build();

            String api = ChunkApiInfo.GET_FILE_CHUNKS_INFO.getApi();
            if(CommonUtils.isWapNetWork()){
                api = ChunkApiInfo.GET_FILE_CHUNKS_INFO.getUrlApi();
                connectionManager.setProxy(ChunkApiInfo.GET_FILE_CHUNKS_INFO.getIp(),ChunkApiInfo.GET_FILE_CHUNKS_INFO.getHost());
            }
            method = new HttpGet(HttpClientUtils.urlAppendParams(api, params));
            method.addHeader("Host", ChunkApiInfo.GET_FILE_CHUNKS_INFO.getHost());
            method.addHeader("Cookie", getCookieString());

            resp = connectionManager.getConnection().execute(method);
            if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = resp.getEntity();
                String content = EntityUtils.toString(entity, DjangoConstant.DEFAULT_CHARSET_NAME);
                if (DjangoClient.DEBUG) {
                    Logger.D(DjangoClient.LOG_TAG, "getFileChunksInfo() :" + content);
                }
                response = JSON.parseObject(content, FileChunksInfoResp.class);
            } else {
                response = new FileChunksInfoResp();
                response.setCode(resp.getStatusLine().getStatusCode());
                response.setMsg("http invoker error!");
            }
        } catch (Exception e) {
            Logger.E(DjangoClient.LOG_TAG, e, e.getMessage());
            response = new FileChunksInfoResp();
            response.setCode(DjangoConstant.DJANGO_400);
            response.setMsg(e.getMessage());
        } finally {
            DjangoUtils.releaseConnection(method, resp);
        }

        return response;
    }

    @Override
    public GetChunksMetaResp getChunksMeta(GetChunksMetaReq getChunksMetaReq) {
        GetChunksMetaResp response = null;

        HttpGet method = null;
        HttpResponse resp = null;
        try {
            String timestampStr = String.valueOf(System.currentTimeMillis());
            String acl = genAclString(getChunksMetaReq.getChunkIds(), timestampStr);

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("token", tokenApi.getTokenString()));
            params.add(new BasicNameValuePair("chunkIds", getChunksMetaReq.getChunkIds()));
            params.add(new BasicNameValuePair("timestamp",timestampStr));
            params.add(new BasicNameValuePair("acl",acl));
            params.add(new BasicNameValuePair(DjangoConstant.TRACE_ID, getTraceId()));

            String api = ChunkApiInfo.GET_CHUNKS_META.getApi();
            if(CommonUtils.isWapNetWork()){
                api = ChunkApiInfo.GET_CHUNKS_META.getUrlApi();
                connectionManager.setProxy(ChunkApiInfo.GET_CHUNKS_META.getIp(),ChunkApiInfo.GET_CHUNKS_META.getHost());
            }
            method = new HttpGet(HttpClientUtils.urlAppendParams(api, params));
            method.addHeader("Host", ChunkApiInfo.GET_CHUNKS_META.getHost());
            method.addHeader("Cookie",getCookieString());

            resp = connectionManager.getConnection().execute(method);

            if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = resp.getEntity();
                String content = EntityUtils.toString(entity, DjangoConstant.DEFAULT_CHARSET_NAME);
                if (DjangoClient.DEBUG) {
                    Logger.D(DjangoClient.LOG_TAG, "getChunksMeta() :" + content);
                }
                response = JSON.parseObject(content, GetChunksMetaResp.class);
            } else {
                response = new GetChunksMetaResp();
                response.setCode(resp.getStatusLine().getStatusCode());
                response.setMsg("http invoker error!");
            }
        } catch (Exception e) {
            Logger.E(DjangoClient.LOG_TAG, e, e.getMessage());
            response = new GetChunksMetaResp();
            response.setCode(DjangoConstant.DJANGO_400);
            response.setMsg(e.getMessage());
        } finally {
            DjangoUtils.releaseConnection(method, resp);
        }

        return response;
    }

    @Override
    public ChunksDownResp downloadChunks(ChunksDownReq chunksDownReq) {
        ChunksDownResp response;

        try {
            if (LiteStringUtils.isBlank(chunksDownReq.getChunkIds())) {
                throw new DjangoClientException("field[chunkIds] is null");
            }

            String timestampStr = String.valueOf(System.currentTimeMillis());
            String acl = genAclString(chunksDownReq.getChunkIds(), timestampStr);

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("token", tokenApi.getTokenString()));
            params.add(new BasicNameValuePair("chunkIds", chunksDownReq.getChunkIds()));
            params.add(new BasicNameValuePair("timestamp",timestampStr));
            params.add(new BasicNameValuePair("acl",acl));
            params.add(new BasicNameValuePair(DjangoConstant.TRACE_ID, getTraceId()));

//            URI uri = new URIBuilder(ChunkApiInfo.DOWNLOAD_CHUNKS.getApi()).setParameters(params).build();
            String api = ChunkApiInfo.DOWNLOAD_CHUNKS.getApi();
            if(CommonUtils.isWapNetWork()){
                api = ChunkApiInfo.DOWNLOAD_CHUNKS.getUrlApi();
                connectionManager.setProxy(ChunkApiInfo.DOWNLOAD_CHUNKS.getIp(),ChunkApiInfo.DOWNLOAD_CHUNKS.getHost());
            }
            HttpGet method = new HttpGet(HttpClientUtils.urlAppendParams(api, params));
            method.addHeader("Host", ChunkApiInfo.DOWNLOAD_CHUNKS.getHost());
            method.addHeader("Cookie",getCookieString());
            HttpResponse resp = connectionManager.getConnection().execute(method);

            if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String content = EntityUtils.toString(resp.getEntity(), DjangoConstant.DEFAULT_CHARSET_NAME);
                if (DjangoClient.DEBUG) {
                    Logger.D(DjangoClient.LOG_TAG, "chunksDown() :" + content);
                }
                response = JSON.parseObject(content, ChunksDownResp.class);
            } else {
                response = new ChunksDownResp();
                response.setCode(resp.getStatusLine().getStatusCode());
                response.setMsg("http invoker error!");
            }
            response.setResp(resp);
        } catch (Exception e) {
            Logger.E(DjangoClient.LOG_TAG, e, e.getMessage());
            response = new ChunksDownResp();
            response.setCode(DjangoConstant.DJANGO_400);
            response.setMsg(e.getMessage());
        }
        return response;
    }
}
