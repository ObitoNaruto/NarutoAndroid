package com.naruto.mobile.base.rpc.volley;

import android.content.Context;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;

import java.util.concurrent.ExecutionException;

import com.google.gson.reflect.TypeToken;

public class GsonSyncRequest<T extends BaseResponse> extends GsonRequest<T> {

    private RequestFuture<T> mListener = RequestFuture.newFuture();

    public GsonSyncRequest(Context context, int method, String url, Class<T> clazz) {
        super(context, method, url, clazz, null);
    }

    @Override
    public void deliverResponse(T response) {
//        LogUtils.d(this.getClass().getSimpleName() + " deliverResponse");
        if (mListener != null) {
            mListener.onResponse(response);
        }
    }

    @Override
    public void deliverError(VolleyError error) {
//        LogUtils.d(this.getClass().getSimpleName() + " deliverError");
        if (mListener != null) {
            mListener.onErrorResponse(error);
        }
    }

    public T getResponse() throws ExecutionException, InterruptedException {
        if (isCanceled()) {
            throw new InterruptedException("Request has been canceled");
        }
        return mListener.get();
    }

    @Override
    public Response.ErrorListener getErrorListener() {
        return mListener;
    }

    public void cancel() {
        super.cancel();
        if (mListener != null) {
            boolean result = mListener.cancel(true);
//            LogUtils.d(this.getClass().getSimpleName() + "is canceled:" + result);
        }
    }

}
