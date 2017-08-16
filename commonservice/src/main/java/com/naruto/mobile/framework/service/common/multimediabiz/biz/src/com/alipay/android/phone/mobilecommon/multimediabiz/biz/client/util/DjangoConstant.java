/*
 * Copyright 2014 Alibaba.com All right reserved.
 * This software is the confidential and proprietary information of Alibaba.com ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.util;

import java.nio.charset.Charset;

/**
 * Django SDK for Android 使用的常量类
 */
public class DjangoConstant {

    /**
     * 该ID需要找django同学申请
     */
    public static final int APP_ID_ONLINE = 35;
    public static final int APP_ID_DAILY = 78;

    public static final int VERSION = 1;

    public static final int PLAT_ANDROID = 2;

    public static final String TRACE_ID = "traceId";

    public static final String API_URL_FORMAT_HTTP = "http://%1$s/%2$s";

    public static int DJANGO_OK = 0;
    public static int DJANGO_400 = 888;

    public static final String DEFAULT_CHARSET_NAME = "UTF-8";
    public static final Charset DEFAULT_CHARSET  = Charset.forName(DjangoConstant.DEFAULT_CHARSET_NAME);

    public static final String COOKIE_FORMAT = "DJANGO_UID=%s;DJANGO_ACL=%s";

    public static final String STORE_PATH = "alipay/multimedia/";

    public static final String IMAGE_PATH = "im";
    public static final String SMALL_IMAGE_PATH = "im_";

    public static final String FILE_PATH = "alipay_files";

}
