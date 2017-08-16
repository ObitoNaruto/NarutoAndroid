package com.naruto.mobile.base.rpc.simpleNet.httpstacks;


import com.naruto.mobile.base.rpc.simpleNet.base.Request;
import com.naruto.mobile.base.rpc.simpleNet.base.Response;

/**
 * 执行网络请求的接口
 */
public interface HttpStack {
    /**
     * 执行Http请求
     * 
     * @param request 待执行的请求
     * @return
     */
    Response performRequest(Request<?> request);
}
