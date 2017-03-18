package com.naruto.mobile.base.serviceaop.demo.service.impl;

import android.os.Bundle;
import android.util.Log;

import com.naruto.mobile.base.serviceaop.demo.service.ExtTextService;


/**
 * Created by xinming.xxm on 2016/5/15.
 */
public class ExtTextServiceImpl extends ExtTextService {

    @Override
    protected void onCreate(Bundle params) {
        Log.d("xxm", "ExtTextServiceImpl onCreate called!");
    }

    @Override
    protected void onDestroy(Bundle params) {
        Log.d("xxm", "ExtTextServiceImpl onDestroy called!");
    }

    @Override
    public boolean isActivated() {
        return false;
    }

    @Override
    public int add(int a, int b) {
        return a + b;
    }
}
