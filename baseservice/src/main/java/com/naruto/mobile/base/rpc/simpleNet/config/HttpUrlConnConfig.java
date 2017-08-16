package com.naruto.mobile.base.rpc.simpleNet.config;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

/**
 * 这是针对于使用HttpUrlStack执行请求时为https请求设置的SSLSocketFactory和HostnameVerifier的配置类,参考
 * http://blog.csdn.net/xyz_lmn/article/details/8027334,http://www.cnblogs.com/
 * vus520/archive/2012/09/07/2674725.html,
 * 
 * @author mrsimple
 */
public class HttpUrlConnConfig extends HttpConfig {

    private static HttpUrlConnConfig sConfig = new HttpUrlConnConfig();

    private SSLSocketFactory mSslSocketFactory = null;
    private HostnameVerifier mHostnameVerifier = null;

    private HttpUrlConnConfig() {
    }

    public static HttpUrlConnConfig getConfig() {
        return sConfig;
    }

    /**
     * 配置https请求的SSLSocketFactory与HostnameVerifier
     * 
     * @param sslSocketFactory
     * @param hostnameVerifier
     */
    public void setHttpsConfig(SSLSocketFactory sslSocketFactory,
            HostnameVerifier hostnameVerifier) {
        mSslSocketFactory = sslSocketFactory;
        mHostnameVerifier = hostnameVerifier;
    }

    public HostnameVerifier getHostnameVerifier() {
        return mHostnameVerifier;
    }

    public SSLSocketFactory getSslSocketFactory() {
        return mSslSocketFactory;
    }

}
