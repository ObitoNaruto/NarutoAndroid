/*
 * Copyright 2014 Alibaba.com All right reserved.
 * This software is the confidential and proprietary information of Alibaba.com ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */

package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.api.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.DjangoClient;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.api.ImageApi;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.api.TokenApi;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.api.infos.ImageApiInfo;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.config.ConnectionManager;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.exception.DjangoClientException;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.req.ThumbnailMarkDownReq;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.req.ThumbnailsDownReq;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.resp.ThumbnailsDownResp;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.util.DjangoConstant;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.util.DjangoUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.util.HttpClientUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.util.LiteStringUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.http.util.TextUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.CommonUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Logger;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.message.BasicNameValuePair;

import android.util.Log;

/**
 * @author jinzhaoyu
 */
public class ImageApiImpl extends AbstractApiImpl implements ImageApi {

    private TokenApi tokenApi;

    public ImageApiImpl(DjangoClient djangoClient, ConnectionManager<HttpClient> connectionManager) {
        super(djangoClient, connectionManager);
        this.tokenApi = djangoClient.getTokenApi();
    }

    @Override
    public ThumbnailsDownResp downloadThumbnails(ThumbnailsDownReq thumbnailsDownReq) {
        ThumbnailsDownResp response = new ThumbnailsDownResp();
        HttpGet method = null;
        HttpResponse httpResponse = null;
        String traceId = null;
        try {
            if (LiteStringUtils.isBlank(thumbnailsDownReq.getFileIds())) {
                throw new DjangoClientException("field[fileIds] can not be null");
            } else if (LiteStringUtils.isBlank(thumbnailsDownReq.getZoom())) {
                throw new DjangoClientException("field[zoom] can not be null");
            }

            String timestampStr = String.valueOf(System.currentTimeMillis());
            String acl = genAclString(thumbnailsDownReq.getFileIds(), timestampStr);

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("token", tokenApi.getTokenString()));
            params.add(new BasicNameValuePair("fileIds", thumbnailsDownReq.getFileIds()));
            params.add(new BasicNameValuePair("timestamp", timestampStr));
            params.add(new BasicNameValuePair("acl", acl));
            params.add(new BasicNameValuePair("zoom", thumbnailsDownReq.getZoom()));
            if (LiteStringUtils.isNotBlank(thumbnailsDownReq.getSource())) {
                params.add(new BasicNameValuePair("source", thumbnailsDownReq.getSource()));
            }
            traceId = getTraceId();
            if (LiteStringUtils.isNotBlank(traceId)) {
                params.add(new BasicNameValuePair(DjangoConstant.TRACE_ID, traceId));
            }

            ImageApiInfo imageApiInfo = ImageApiInfo.DOWNLOAD_THUMBNAILS;

            if (thumbnailsDownReq instanceof ThumbnailMarkDownReq) {
                imageApiInfo = ImageApiInfo.DOWNLOAD_THUMBNAILS_WARTERMARK;
                ThumbnailMarkDownReq req = (ThumbnailMarkDownReq)thumbnailsDownReq;
                params.add(new BasicNameValuePair("sourceId", req.getMarkId()));
                params.add(new BasicNameValuePair("position", String.valueOf(req.getPosition())));
                params.add(new BasicNameValuePair("transparency", String.valueOf(req.getTransparency())));
                params.add(new BasicNameValuePair("width", String.valueOf(req.getMarkWidth())));
                params.add(new BasicNameValuePair("height", String.valueOf(req.getMarkHeight())));
                params.add(new BasicNameValuePair("x", String.valueOf(req.getPaddingX())));
                params.add(new BasicNameValuePair("y", String.valueOf(req.getPaddingY())));
                if (req.getPercent() != null) {
                    params.add(new BasicNameValuePair("P", String.valueOf(req.getPercent())));
                }
            }

//            URI uri = new URIBuilder(ImageApiInfo.DOWNLOAD_THUMBNAILS.getApi()+ "?" + thumbnailsDownReq.getUrlParameter()).setParameters(params).build();
            String api = imageApiInfo.getApi();
            if (CommonUtils.isWapNetWork()) {
                api = imageApiInfo.getUrlApi();
                connectionManager.setProxy(imageApiInfo.getIp(), imageApiInfo.getHost());
            }
            method = new HttpGet(HttpClientUtils.urlAppendParams(api, params));
            method.addHeader("Host",imageApiInfo.getHost());
            method.addHeader("Cookie", getCookieString());
            if(thumbnailsDownReq.getRange() > 0){
                method.setHeader("Range", String.format("bytes=%d-", thumbnailsDownReq.getRange()));
            }

            if (DjangoClient.DEBUG) {
                Logger.D(DjangoClient.LOG_TAG, JSON.toJSONString(thumbnailsDownReq));
                //Logger.D(DjangoClient.LOG_TAG, "cookie:" + getCookieString());
                Logger.D(DjangoClient.LOG_TAG, Arrays.toString(method.getAllHeaders()));

            }
            httpResponse = connectionManager.getConnection().execute(method);

            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK || httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_PARTIAL_CONTENT) {
                response.setResp(httpResponse);
                response.setCode(DjangoConstant.DJANGO_OK);
            } else {
                response.setCode(httpResponse.getStatusLine().getStatusCode());
                response.setMsg("Http invoker error :" + response.getCode());
            }
            response.setMethod(method);
        } catch (Exception e) {
            Logger.E(DjangoClient.LOG_TAG,e, e.getMessage());
            response.setCode(DjangoConstant.DJANGO_400);
            String msg = e.getMessage();
            if(TextUtils.isEmpty(msg)){
                msg = e.getClass().getSimpleName();
            }
            response.setMsg(msg);
            DjangoUtils.releaseConnection(method, httpResponse);
        } finally {
            if (response != null && !android.text.TextUtils.isEmpty(traceId)) {
                response.setTraceId(traceId);
            }
        }

        return response;
    }

    @Override
    public ThumbnailsDownResp downloadNormal(String url) {
        ThumbnailsDownResp response = new ThumbnailsDownResp();
        HttpGet method = null;
        HttpResponse httpResponse = null;
        try {
            if (LiteStringUtils.isBlank(url)) {
                throw new DjangoClientException("url can not be null");
            }


            method = new HttpGet(url);
            httpResponse = connectionManager.getConnection().execute(method);

            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                response.setResp(httpResponse);
                response.setCode(DjangoConstant.DJANGO_OK);
            } else {
                response.setCode(httpResponse.getStatusLine().getStatusCode());
                response.setMsg("Http invoker error :" + response.getCode());
            }
            response.setMethod(method);
        } catch (Exception e) {
            Logger.E(DjangoClient.LOG_TAG, e, e.getMessage());
            response.setCode(DjangoConstant.DJANGO_400);
            String msg = e.getMessage();
            if(TextUtils.isEmpty(msg)){
                msg = e.getClass().getSimpleName();
            }

            response.setMsg(msg);
            DjangoUtils.releaseConnection(method, httpResponse);
        }
        return response;
    }

    @Override
    public String getImageLoadUrl(ThumbnailsDownReq thumbnailsDownReq) {
        try {
            String timestampStr = String.valueOf(System.currentTimeMillis());
            String acl = genAclString(thumbnailsDownReq.getFileIds(), timestampStr);

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("token", tokenApi.getTokenString()));
            params.add(new BasicNameValuePair("fileIds", thumbnailsDownReq.getFileIds()));
            params.add(new BasicNameValuePair("timestamp", timestampStr));
            params.add(new BasicNameValuePair("acl", acl));
            params.add(new BasicNameValuePair("zoom", thumbnailsDownReq.getZoom()));
            if (LiteStringUtils.isNotBlank(thumbnailsDownReq.getSource())) {
                params.add(new BasicNameValuePair("source", thumbnailsDownReq.getSource()));
            }
            String traceId = getTraceId();
            if (LiteStringUtils.isNotBlank(traceId)) {
                params.add(new BasicNameValuePair(DjangoConstant.TRACE_ID, traceId));
            }

            return HttpClientUtils.urlAppendParams(ImageApiInfo.DOWNLOAD_THUMBNAILS.getApi(), params);
        } catch (Exception e) {
            Logger.E(DjangoClient.LOG_TAG, e, e.getMessage());
        }
        return null;
    }

    @Override
    public String getImageLoadCookie() {
        return getCookieString();
    }
}
