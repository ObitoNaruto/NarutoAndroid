package com.naruto.mobile.BaseRecyclerViewAdapterHelper.sample.entity;

/**
 */
public class HomeItem {
    private String title;
    private Class<?> activity;
    private String colorStr;

    public String getColorStr() {
        return colorStr;
    }

    public void setColorStr(String colorStr) {
        this.colorStr = colorStr;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Class<?> getActivity() {
        return activity;
    }

    public void setActivity(Class<?> activity) {
        this.activity = activity;
    }
}