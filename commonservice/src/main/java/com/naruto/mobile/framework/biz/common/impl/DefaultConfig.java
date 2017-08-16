package com.naruto.mobile.framework.biz.common.impl;

import com.naruto.mobile.framework.rpc.myhttp.transport.Transport;
import com.naruto.mobile.framework.rpc.myhttp.transport.http.HttpUrlHeader;
import com.naruto.mobile.base.serviceaop.NarutoApplication;
import com.naruto.mobile.framework.biz.common.HttpTransportSevice;
import com.naruto.mobile.framework.biz.common.RpcDefaultConfig;

public class DefaultConfig extends RpcDefaultConfig{

    @Override
    public String getUrl() {
        return null;
    }

    @Override
    public Transport getTransport() {
        return NarutoApplication.getInstance().getNarutoApplicationContext().findServiceByInterface(HttpTransportSevice.class.getName());
    }

    @Override
    public String getAppKey() {
        return null;
    }

    @Override
    public void giveResponseHeader(String operationType, HttpUrlHeader header) {
    }
}
