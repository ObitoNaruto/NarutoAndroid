/*
 * Copyright 2014 Alibaba.com All right reserved.
 * This software is the confidential and proprietary information of Alibaba.com ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */

package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.api.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import android.util.Log;

import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.DjangoClient;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.api.PackageApi;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.api.TokenApi;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.api.infos.PackageApiInfo;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.config.ConnectionManager;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.exception.DjangoClientException;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.BaseDownResp;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.req.PackageDownReq;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.util.DjangoConstant;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.util.HttpClientUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.util.LiteStringUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.CommonUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Logger;


/**
 * 打包批量下载文件的接口实现类
 *
 * @author jinzhaoyu
 */
public class PackageApiImpl extends AbstractApiImpl implements PackageApi {

    private TokenApi tokenApi;

    public PackageApiImpl(DjangoClient djangoClient, ConnectionManager<HttpClient> connectionManager) {
        super(djangoClient, connectionManager);
        this.tokenApi = djangoClient.getTokenApi();
    }

    @Override
    public BaseDownResp downloadPackage(PackageDownReq packageDownReq) {
        BaseDownResp response = new BaseDownResp();
        try {
            if (LiteStringUtils.isBlank(packageDownReq.getFileIds())) {
                throw new DjangoClientException("field[fileIds] can not be null");
            } else if (LiteStringUtils.isBlank(packageDownReq.getPaths())) {
                throw new DjangoClientException("field[paths] can not be null");
            }

            String timestampStr = String.valueOf(System.currentTimeMillis());
            String acl = genAclString(packageDownReq.getFileIds(), timestampStr);

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("token", tokenApi.getTokenString()));
            params.add(new BasicNameValuePair("fileIds", packageDownReq.getFileIds()));
            params.add(new BasicNameValuePair("paths", packageDownReq.getPaths()));
            params.add(new BasicNameValuePair("timestamp", timestampStr));
            params.add(new BasicNameValuePair("acl", acl));
            if (LiteStringUtils.isNotBlank(packageDownReq.getName())) {
                params.add(new BasicNameValuePair("name", packageDownReq.getName()));
            }
            List<NameValuePair> urlParams = new ArrayList<NameValuePair>();
            String traceId = getTraceId();
            if (LiteStringUtils.isNotBlank(traceId)) {
                urlParams.add(new BasicNameValuePair(DjangoConstant.TRACE_ID, traceId));
            }

			String api = PackageApiInfo.DOWNLOAD_PACKAGE.getApi();
            if(CommonUtils.isWapNetWork()){
                api = PackageApiInfo.DOWNLOAD_PACKAGE.getUrlApi();
                connectionManager.setProxy(PackageApiInfo.DOWNLOAD_PACKAGE.getIp(),PackageApiInfo.DOWNLOAD_PACKAGE.getHost());
            }

            HttpPost post = new HttpPost(HttpClientUtils.urlAppendParams(api, urlParams));
            UrlEncodedFormEntity urlEncodedFormEntityHC4 = new UrlEncodedFormEntity(params, DjangoConstant.DEFAULT_CHARSET_NAME);
            post.setEntity(urlEncodedFormEntityHC4);
            post.addHeader("Host", PackageApiInfo.DOWNLOAD_PACKAGE.getHost());
            post.addHeader("Cookie", getCookieString());

            HttpResponse httpResponse = connectionManager.getConnection().execute(post);

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
