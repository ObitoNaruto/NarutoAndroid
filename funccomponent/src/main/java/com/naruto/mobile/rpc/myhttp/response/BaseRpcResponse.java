package com.naruto.mobile.rpc.myhttp.response;

import java.io.Serializable;
import java.util.Map;

public class BaseRpcResponse implements Serializable {

    /**
     * 是否成功
     */
    public boolean success;

    /**
     * 错误码
     */
    public String resultCode;

    /**
     * 错误描述
     */
    public String desc;

    /**
     * 返回的其他业务数据
     */
    public Map<String, Object> data;
}
