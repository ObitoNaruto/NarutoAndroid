package com.naruto.mobile.base.serviceaop.demo.service;

import android.os.Bundle;

import com.naruto.mobile.base.serviceaop.service.CommonService;

public abstract class InnerTxtService extends CommonService {

    @Override
    protected void onCreate(Bundle params) {
    }

    @Override
    protected void onDestroy(Bundle params) {
    }

    public abstract int subtract(int a, int b);
}
