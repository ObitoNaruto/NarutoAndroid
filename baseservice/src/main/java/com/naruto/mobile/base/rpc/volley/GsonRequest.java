package com.naruto.mobile.base.rpc.volley;

import android.content.Context;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.net.HttpURLConnection;

import com.google.gson.Gson;

public class GsonRequest<T extends BaseResponse> extends BaseSecureRequest<T> {

    private Gson mGson;

    private Class<T> mClazz;


    public GsonRequest(Context context, String url, Class<T> clazz, IRequestCallback listener) {
        this(context, Method.GET, url, clazz, listener);
    }

    public GsonRequest(Context context, int method, String url, Class<T> clazz, IRequestCallback listener) {
        super(context, method, url, listener);
        mGson = getGson();
        mClazz = clazz;
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response, String body) {
//        LogUtils.d(getClass().getSimpleName() + " body:" + body);
        return Response.success(mGson.fromJson(body, mClazz),
                HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(T response) {
        if (mListener != null) {
            if (isSuccess(response)) {
                mListener.onSuccess(response);
            } else {
                mListener.onFailure(response.getErrorCode(), response.getErrorDesc());
            }
        }
    }

    @Override
    protected boolean isSuccess(T response) {
        if (response != null) {
            if (response.getErrorCode() == HttpURLConnection.HTTP_OK) {
                return true;
            }
        }
        return super.isSuccess(response);
    }

    protected Gson getGson() {
        return new Gson();//默认值，可重写
    }

}
