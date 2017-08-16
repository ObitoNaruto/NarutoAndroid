/*
 * Copyright 2014 Alibaba.com All right reserved.
 * This software is the confidential and proprietary information of Alibaba.com ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.io.IOUtils;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

/**
 * 
 * @author zhenghui
 */
public class HttpClientUtils {
	 /**
     * Ensures that the entity content is fully consumed and the content stream, if exists,
     * is closed. The process is done, <i>quietly</i> , without throwing any IOException.
     *
     * @param entity the entity to consume.
     */
    public static void consumeQuietly(final HttpEntity entity) {
        try {
          consume(entity);
        } catch (final IOException ignore) {
        }
    }

    /**
     * Ensures that the entity content is fully consumed and the content stream, if exists,
     * is closed.
     *
     * @param entity the entity to consume.
     * @throws IOException if an error occurs reading the input stream
     */
    public static void consume(final HttpEntity entity) throws IOException {
        if (entity == null) {
            return;
        }
        if (entity.isStreaming()) {
            final InputStream instream = entity.getContent();
            IOUtils.closeQuietly(instream);
        }
    }
    
    /**
     * getMethod should append params to string
     * @param url
     * @param params
     * @return
     */
    public static String urlAppendParams(String url, List<NameValuePair> params){
    	String paramString = URLEncodedUtils.format(params, DjangoConstant.DEFAULT_CHARSET_NAME);
    	return url + "?" + paramString;
    }
}
