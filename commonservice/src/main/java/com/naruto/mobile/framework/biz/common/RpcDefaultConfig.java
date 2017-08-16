package com.naruto.mobile.framework.biz.common;

import com.naruto.mobile.framework.rpc.myhttp.common.Config;
import com.naruto.mobile.framework.rpc.myhttp.transport.http.HttpUrlHeader;

public abstract class RpcDefaultConfig implements Config{

    public abstract String getAppKey();

    public abstract void giveResponseHeader(String operationType, HttpUrlHeader header);
}
