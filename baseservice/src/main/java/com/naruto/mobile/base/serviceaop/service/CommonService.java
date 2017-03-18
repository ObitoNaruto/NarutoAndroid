package com.naruto.mobile.base.serviceaop.service;

import android.os.Bundle;

/**
 * Created by xinming.xxm on 2016/5/15.
 */
public abstract class CommonService extends MicroService{

    private boolean mIsActivated = false;

    public final boolean isActivated() {
        return mIsActivated;
    }

    @Override
    public final void create(Bundle params) {
        onCreate(params);
        mIsActivated = true;
    }

    @Override
    public final void destroy(Bundle params) {
        getNarutoApplicationContext().onDestroyContent(this);
        onDestroy(params);
        mIsActivated = false;
    }
}
