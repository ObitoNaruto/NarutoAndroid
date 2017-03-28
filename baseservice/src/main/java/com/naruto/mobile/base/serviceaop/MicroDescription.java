package com.naruto.mobile.base.serviceaop;

/**
 * Created by xinming.xxm on 2016/5/15.
 */
public abstract class MicroDescription {

    /**
     * 名字
     */
    private String mName;
    /**
     * 类名
     */
    private String mClassName;

    public String getName() {
        return mName;
    }

    public MicroDescription setName(String name) {
        mName = name;
        return this;
    }

    public String getClassName() {
        return mClassName;
    }

    public MicroDescription setClassName(String className) {
        mClassName = className;
        return this;
    }
}
