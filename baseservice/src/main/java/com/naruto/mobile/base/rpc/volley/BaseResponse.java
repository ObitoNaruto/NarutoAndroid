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
 "id":3,
 "name":"北京一卡通充值优惠",
 "issueDiscount":1000,
 "rechargeDiscount":2000,
 "totalDiscount":3000,
 "issuePayFee":2000,
 "rechargePayFee":2000,
 "totalPayFee":4000,
 "valid":true,
 "desc":"优惠不可叠加"
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
