package com.naruto.mobile.framework.biz.common.impl;

import java.util.concurrent.Future;

import android.os.Bundle;

import com.naruto.mobile.framework.rpc.myhttp.transport.Request;
import com.naruto.mobile.framework.rpc.myhttp.transport.Response;
import com.naruto.mobile.framework.rpc.myhttp.transport.http.HttpManager;
import com.naruto.mobile.base.serviceaop.NarutoApplication;
import com.naruto.mobile.framework.biz.common.HttpTransportSevice;

/**
 * HttpTransportServiceImpl
 * 
 */
public class HttpTransportSeviceImpl extends HttpTransportSevice {
    private HttpManager mHttpManager;

    public HttpTransportSeviceImpl() {
        mHttpManager = new HttpManager(
                NarutoApplication.getInstance().getApplicationContext());
    }

    @Override
    public Future<Response> execute(Request request) {
        return mHttpManager.execute(request);
    }

    @Override
    protected void onCreate(Bundle params) {

    }

    @Override
    protected void onDestroy(Bundle params) {
        mHttpManager.close();
    }

}
