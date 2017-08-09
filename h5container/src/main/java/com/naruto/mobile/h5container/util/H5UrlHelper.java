
package com.naruto.mobile.h5container.util;

import android.net.Uri;
import android.text.TextUtils;

public class H5UrlHelper {
    public static final String TAG = "UrlHelper";

    private final static String TAOBAO_DOMAIN = "taobao.com";
    private final static String TMALL_DOMAIN = "tmall.com";
    private final static String TMALL_HK_DOMAIN = "tmall.hk";
    private final static String ETAO_DOMAIN = "etao.com";
    private final static String HITAO_DOMAIN = "hitao.com";
    private final static String LAIWANG_DOMAIN = "laiwang.com";

    private final static String ALIPAY_DOMAIN = "alipay.com";
    private final static String ALIPAY_NET_DOMAIN = "alipay.net";

    public static Uri parseUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        Uri uri = null;
        try {
            uri = Uri.parse(url);
        } catch (Exception e) {
            H5Log.e(TAG, "parse url exception.", e);
        }
        return uri;
    }

    public static final String getHost(String url) {
        Uri uri = parseUrl(url);
        String host = null;
        if (uri != null) {
            host = uri.getHost();
        }
        return host;
    }

    public static final String getPath(String url) {
        Uri uri = parseUrl(url);
        String path = null;
        if (uri != null) {
            path = uri.getPath();
        }
        return path;
    }

    public static String getParam(Uri uri, String key, String defaultValue) {
        if (uri == null) {
            return defaultValue;
        }
        String value = null;
        try {
            value = uri.getQueryParameter(key);
        } catch (Exception e) {

        }

        if (TextUtils.isEmpty(value)) {
            value = defaultValue;
        }
        return value;
    }

    public static boolean isAlipay(Uri uri) {
        if (uri == null) {
            return false;
        }

        String host = uri.getHost();
        if (TextUtils.isEmpty(host)) {
            return false;
        }
        if (host.endsWith(ALIPAY_DOMAIN) || host.endsWith(ALIPAY_NET_DOMAIN)) {
            return true;
        }
        return false;
    }

    public static boolean isAli(Uri uri) {
        if (uri == null) {
            return false;
        }

        String host = uri.getHost();
        if (TextUtils.isEmpty(host)) {
            return false;
        }
        if (host.endsWith(ALIPAY_DOMAIN) || host.endsWith(ALIPAY_NET_DOMAIN)
                || host.endsWith(TAOBAO_DOMAIN) || host.endsWith(TMALL_DOMAIN)
                || host.endsWith(TMALL_HK_DOMAIN) || host.endsWith(ETAO_DOMAIN)
                || host.endsWith(HITAO_DOMAIN) || host.endsWith(LAIWANG_DOMAIN)) {
            return true;
        }
        return false;
    }
}
