package com.naruto.mobile.base.rpc.volley;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * 数据样例：
 * {
 "errCode":0,
 "errDesc":"SUCCESS",
 "data":[
 {
 "id":1,
 "name":"测试",
 "test":1000
 }
 ]
 }
 *
 *
 *
 */
public class BaseResponse {

    @SerializedName("errCode")
    private int mErrorCode;

    @SerializedName("errDesc")
    private String mErrorDesc;

    public int getErrorCode() {
        return mErrorCode;
    }

    public String getErrorDesc() {
        return mErrorDesc;
    }



    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
