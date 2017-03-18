package com.naruto.mobile.base.serviceaop.init.impl;

/**
 * Created by xinming.xxm on 2016/5/15.
 */
public class Bundle {
    private String name;
    private boolean entry;
    private String packageName;

    public Bundle() {
    }

    public Bundle(String name, boolean entry, String packageName) {
        super();

        this.name = name;
        this.entry = entry;
        this.packageName = packageName;
    }

    public String getName() {
        return name;
    }

    public boolean isEntry() {
        return entry;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEntry(boolean entry) {
        this.entry = entry;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    @Override
    public String toString() {
        return "Bundle [name=" + name + ", entry=" + entry + ", packageName=" + packageName + "]";
    }
}
