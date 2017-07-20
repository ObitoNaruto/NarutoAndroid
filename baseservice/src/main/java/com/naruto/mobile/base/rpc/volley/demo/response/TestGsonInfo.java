package com.naruto.mobile.base.rpc.volley.demo.response;

import com.google.gson.annotations.SerializedName;

public class TestGsonInfo {

    @SerializedName("id")
    private int mId;

    @SerializedName("text")
    private String mText;

    public int getId() {
        return mId;
    }

    public String getText() {
        return mText;
    }

    // TODO: 17-7-20  这里可添加各种针对当前对象数据的封装
}
