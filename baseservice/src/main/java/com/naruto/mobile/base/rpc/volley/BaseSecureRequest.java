package com.naruto.mobile.base.rpc.volley;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Set;

public abstract class BaseSecureRequest<T> extends BaseRequest<T> {

    protected Context mContext;

    protected AccountInfo mAccountInfo;
    //某种加密解密算法类
//    protected CryptCoder mCoder;

    public BaseSecureRequest(Context context, int method, String url, IRequestCallback listener) {
        super(method, url, listener);
        mContext = context;
    }

    @Override
    public Map<String, String> getHeaders() {
        loadAccountInfo();
        //登录检查，放在cookie里
//        putCookie("userId", mAccountInfo.getUserId()).putCookie("token", mAccountInfo.getAuthToken());
//        putHeader("Cookie", join(mCookies, "; "));
        return super.getHeaders();
    }

    /**
     * 使用分隔符连接Map的每个Entry, Entry表示为key=value
     *
     * @param delimiter 分隔符
     */
    private String join(Map<String, String> map, String delimiter) {
        if (map == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        Set<Map.Entry<String, String>> entries = map.entrySet();
        int i = 0;
        for (Map.Entry<String, String> entry : entries) {
            if (i > 0) {
                sb.append(delimiter);
            }
            final String key = entry.getKey();
            final String value = entry.getValue();
            sb.append(key).append("=").append(value);
            i++;
        }
        return sb.toString();
    }

    @Override
    public Map<String, String> getParams() {
        if (mIsExtraParamsAdded.compareAndSet(false, true)) {
            getExtraParams(mParams);
        }
        loadAccountInfo();
        //所有请求统一传递的默认值
//        putParams("userId", mAccountInfo.getUserId())
//                .putParams("rom_type", "rom_type_value")
//                .putParams("client_version_code", "code")
//                .putParams("client_version_name", "name");
        //请求加密
//        String method = getMethod() == Method.GET ? "GET" : "POST";
//        Map<String, String> params;
//        try {
//            params = SecureRequest.encryptParams(method, getUrlWithoutParams(), mParams, mAccountInfo.getSecurity(), mCoder);
//        } catch (CipherException e) {
//            throw new AuthFailureError(e.getMessage());
//        }
//        return params;
        return super.getParams();
    }

    //test用
    private void loadAccountInfo() {
        if (mAccountInfo == null) {
            mAccountInfo = new AccountInfo();
            mAccountInfo.setUserId("123456");
            mAccountInfo.setAuthToken("DJJDJFJDJJDJJDJ777");
        }
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        //返回数据解密
//        try {
//            String responseBody = new String(response.data,
//                    HttpHeaderParser.parseCharset(response.headers));
//            String decryptedBody = mCoder.decrypt(responseBody);
//            return parseNetworkResponse(decryptedBody);
//        } catch (UnsupportedEncodingException e) {
//            return Response.error(new ParseError(e));
//        } catch (CipherException e) {
//            return Response.error(new ParseError(e));
//        }
        try {
            String responseBody = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            return parseNetworkResponse(response, responseBody);
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }

    }

    protected abstract Response<T> parseNetworkResponse(NetworkResponse response, String body);

}
