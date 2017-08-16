package com.naruto.mobile.rpc.myhttp.request;

import java.io.Serializable;

public class BaseRpcRequest implements Serializable{

    /**
     * 城市ID,整个ID需要与客户端一起约定
     */
    public String cityId;

    /**
     * LBS里面的经度
     */
    public double x;

    /**
     * LBS里面的纬度
     */
    public double y;

}
