package com.naruto.mobile.base.serviceaop.service;


import com.naruto.mobile.base.serviceaop.MicroDescription;

/**
 * Created by xinming.xxm on 2016/5/15.
 */
public class ServiceDescription extends MicroDescription {
    /**
     * 服务接口名称
     */
    private String mInterfaceClassName;

    /**
     * 是否延迟加载
     *
     */
    private boolean isLazy = true;

    public boolean isLazy() {
        return isLazy;
    }

    public ServiceDescription setLazy(boolean isLazy) {
        this.isLazy = isLazy;
        return this;
    }

    public String getInterfaceClass() {
        return mInterfaceClassName;
    }

    public ServiceDescription setInterfaceClass(String interfaceClassName) {
        this.mInterfaceClassName = interfaceClassName;
        return this;
    }
}
