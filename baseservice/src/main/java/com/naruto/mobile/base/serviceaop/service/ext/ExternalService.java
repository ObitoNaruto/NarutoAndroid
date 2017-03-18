package com.naruto.mobile.base.serviceaop.service.ext;

import android.os.Bundle;

import com.naruto.mobile.base.serviceaop.service.MicroService;


/**
 * Created by xinming.xxm on 2016/5/15.
 */
public abstract class ExternalService extends MicroService {

    @Override
    public void create(Bundle params) {
        onCreate(params);
    }

    @Override
    public void destroy(Bundle params) {
        //���ڴ����Ƴ���ǰ����
        getNarutoApplicationContext().onDestroyContent(this);
        onDestroy(params);
    }
}
