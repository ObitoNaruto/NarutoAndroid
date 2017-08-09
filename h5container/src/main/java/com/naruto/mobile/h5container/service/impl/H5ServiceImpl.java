package com.naruto.mobile.h5container.service.impl;

import android.os.Bundle;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.naruto.mobile.base.serviceaop.NarutoApplication;
import com.naruto.mobile.h5container.api.H5Bundle;
import com.naruto.mobile.h5container.api.H5Context;
import com.naruto.mobile.h5container.api.H5Listener;
import com.naruto.mobile.h5container.api.H5Page;
import com.naruto.mobile.h5container.api.H5Param;
import com.naruto.mobile.h5container.api.H5Plugin;
import com.naruto.mobile.h5container.api.H5Session;
import com.naruto.mobile.h5container.env.H5Container;
import com.naruto.mobile.h5container.service.H5Service;
import com.naruto.mobile.h5container.util.H5Utils;

public class H5ServiceImpl extends H5Service {

    public static final String TAG = H5ServiceImpl.class.getSimpleName();

    @Override
    protected void onCreate(Bundle params) {
    }

    @Override
    public void startPage(Bundle params) {
        H5Bundle bundle = new H5Bundle();
        bundle.addListener(h5Listener);
        if (params != null) {
            bundle.setParams(params);
        }else{
            params = new Bundle();
            params.putString(H5Param.SHOW_TITLEBAR, "YES");
            params.putString(H5Param.SHOW_TOOLBAR, "NO");
            params.putString(H5Param.SHOW_LOADING, "YES");
            params.putString(H5Param.READ_TITLE, "YES");
            bundle.setParams(params);
        }
        H5Context h5Context = new H5Context(NarutoApplication.getInstance().getNarutoApplicationContext().getApplicationContext());
        H5Container.getService().startPage(h5Context, bundle);
    }

    @Override
    protected void onDestroy(Bundle params) {
    }


    private H5Listener h5Listener = new H5Listener() {

        @Override
        public void onSessionDestroyed(H5Session session) {
            Log.d(TAG, "onSessionDestroyed");
        }

        @Override
        public void onSessionCreated(H5Session session) {
            Log.d(TAG, "onSessionCreated");
        }

        @Override
        public void onPageDestroyed(H5Page page) {
            Log.d(TAG, "onPageDestroyed");
        }

        @Override
        public void onPageCreated(H5Page page) {
        }
    };
}
