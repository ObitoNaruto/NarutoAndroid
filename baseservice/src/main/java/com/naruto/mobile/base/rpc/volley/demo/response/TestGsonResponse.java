package com.naruto.mobile.base.rpc.volley.demo.response;

import com.google.gson.annotations.SerializedName;
import com.naruto.mobile.base.rpc.volley.BaseResponse;

public class TestGsonResponse extends BaseResponse {

    @SerializedName("data")
    private TestGsonInfo mTestGsonInfo;

    public TestGsonInfo getTestGsonInfo() {
        return mTestGsonInfo;
    }
}
