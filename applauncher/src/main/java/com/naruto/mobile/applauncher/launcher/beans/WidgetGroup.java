package com.naruto.mobile.applauncher.launcher.beans;


public class WidgetGroup {

    //appId
    private String mAppId;

    //bundle名字
    private String mBundleName;

    //IWidgetGroup实现类的类名
    private String mClassName;

    //当前默认显示的tab
    private String mDefaultWidgetGroup;


    public WidgetGroup(String appId, String bundleName, String className, String defaultWidgetGroup) {
        this.mAppId = appId;
        this.mBundleName = bundleName;
        this.mClassName = className;
        this.mDefaultWidgetGroup = defaultWidgetGroup;
    }

    public String getAppId() {
        return mAppId;
    }

    public void setAppId(String mAppId) {
        this.mAppId = mAppId;
    }

    public String getBundleName() {
        return mBundleName;
    }

    public void setBundleName(String mBundleName) {
        this.mBundleName = mBundleName;
    }

    public String getClassName() {
        return mClassName;
    }

    public void setClassName(String mClassName) {
        this.mClassName = mClassName;
    }

    public String getDefaultWidgetGroup() {
        return mDefaultWidgetGroup;
    }

    public void setDefaultWidgetGroup(String mDefaultWidgetGroup) {
        this.mDefaultWidgetGroup = mDefaultWidgetGroup;
    }
}
