package com.naruto.mobile.base.serviceaop;

/**
 * Created by xinming.xxm on 2016/5/15.
 */
public abstract class MicroDescription {

    /**
     * app id
     */
    private String mAppId;

    /**
     * 服务名字
     */
    private String mName;
    /**
     * 服务实现类名
     */
    private String mClassName;

    /**
     * 是否是老业务app
     */
    private boolean isLagacyApp = false;


    public String getAppId() {
        return mAppId;
    }

    public MicroDescription setAppId(String mAppId) {
        this.mAppId = mAppId;
        return this;
    }

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

    public boolean isLagacyApp() {
        return isLagacyApp;
    }

    public MicroDescription setLagacyApp(boolean lagacyApp) {
        isLagacyApp = lagacyApp;
        return this;
    }
}
