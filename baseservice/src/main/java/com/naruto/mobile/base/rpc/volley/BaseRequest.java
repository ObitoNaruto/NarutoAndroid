package com.naruto.mobile.base.rpc.volley;

import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class BaseRequest<T> extends Request<T> implements Response.ErrorListener {

    public static final boolean STAGING = new File("/data/system/xuxm_account_preview").exists();

    protected BaseHost mHost = getHost();

    protected AtomicBoolean mIsExtraParamsAdded = new AtomicBoolean(false);

    protected final IRequestCallback mListener;

    protected final Map<String, String> mParams = new HashMap<>();

    protected final Map<String, String> mCookies = new HashMap<>();

    protected final Map<String, String> mHeaders = new HashMap<>();

    public BaseRequest(int method, String url, IRequestCallback listener) {
        super(method, url, null);
        putParams();
        mListener = listener;
    }

    protected void putParams() {
    }

    /**
     * 这里是绝对路径
     */
    @Override
    public String getUrl() {
        String absoluteUrl = getAbsoluteUrlWithParams();
        if (getMethod() == Method.GET) {
            absoluteUrl = appendParamsToAbsoluteUrl(absoluteUrl);
        }
        return super.getUrl();
    }

    public String getAbsoluteUrlWithParams() {
        return getServer() + super.getUrl();
    }

    protected String getServer() {
        if (STAGING) {
            return mHost.getStagingHost();
        }
        return mHost.getOnlineHost();
    }

    private String appendParamsToAbsoluteUrl(String originUrl) {
        if (originUrl == null) {
            throw new NullPointerException("origin is not allowed null");
        }
        StringBuilder urlBuilder = new StringBuilder(originUrl);
        try {
            Map<String, String> params = getParams();
            String paramsEncoding = getParamsEncoding();
            StringBuilder encodedParams = new StringBuilder();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (TextUtils.isEmpty(entry.getValue())) {
                    continue;
                }
                if (encodedParams.length() > 0) {
                    encodedParams.append('&');
                }
                encodedParams.append(URLEncoder.encode(entry.getKey(), paramsEncoding));
                encodedParams.append('=');
                encodedParams.append(URLEncoder.encode(entry.getValue(), paramsEncoding));
            }
            String paramPart = encodedParams.toString();
            if (!TextUtils.isEmpty(paramPart)) {
                if (originUrl.contains("?")) {
                    urlBuilder.append("&");
                } else {
                    urlBuilder.append("?");
                }
                urlBuilder.append(paramPart);
            }
        } catch (UnsupportedEncodingException uee) {
//            LogUtils.e("append Url failed", uee);
        }
        return urlBuilder.toString();
    }

    @Override
    public String getCacheKey() {
        String cachedKey = getServer() + super.getUrl();
        return cachedKey;
    }

    @Override
    public Map<String, String> getHeaders() {
        return mHeaders;
    }

    @Override
    public Map<String, String> getParams() {
        // 1. 比较AtomicBoolean和expect的值，如果一致，执行方法内的语句。其实就是一个if语句
        // 2. 把AtomicBoolean的值设成update
        // 比较最要的是这两件事是一气呵成的，这连个动作之间不会被打断，任何内部或者外部的语句都不可能在两个动作之间运行。为多线程的控制提供了解决的方案。
        if (mIsExtraParamsAdded.compareAndSet(false, true)) {
            getExtraParams(mParams);
        }
        return mParams;
    }

    protected void getExtraParams(Map<String, String> params) {
    }

    @Override
    public Response.ErrorListener getErrorListener() {
        return super.getErrorListener();
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        if (mListener != null) {
            int errorCode = -100;
            String errorMsg = "Network Error on " + super.getUrl();
            if (error != null && error.networkResponse != null) {
                errorCode = error.networkResponse.statusCode;
            }
            mListener.onFailure(errorCode, errorMsg);
        }
    }

    @Override
    public void deliverError(VolleyError error) {
        super.deliverError(error);
    }

    protected boolean isSuccess(T response) {
        return false;
    }

    public BaseRequest<T> putParams(String key, String value) {
        if (!TextUtils.isEmpty(value)) {
            mParams.put(key, value);
        }
        return this;
    }

    public BaseRequest<T> putCookie(String key, String value) {
        mCookies.put(key, value);
        return this;
    }

    public BaseRequest<T> putHeader(String key, String value) {
        mHeaders.put(key, value);
        return this;
    }

    /**
     * 获取Host 本方法可以被重写，已以便切换到别的host上
     */
    protected BaseHost getHost() {
        return new Task1Host();
    }
}
