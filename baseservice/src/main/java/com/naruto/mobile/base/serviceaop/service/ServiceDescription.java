package com.naruto.mobile.base.serviceaop.service;


import com.naruto.mobile.base.serviceaop.MicroDescription;

/**
 * Created by xinming.xxm on 2016/5/15.
 */
public class ServiceDescription extends MicroDescription {
    /**
     * ����ӿ�
     */
    private String mInterfaceClassName;

    /**
     * �Ƿ��ӳټ���
     *
     */
    private boolean isLazy = true;


    public boolean isLazy() {
        return isLazy;
    }

    public void setLazy(boolean isLazy) {
        this.isLazy = isLazy;
    }

    public String getInterfaceClass() {
        return mInterfaceClassName;
    }

    public void setInterfaceClass(String interfaceClassName) {
        mInterfaceClassName = interfaceClassName;
    }
}
