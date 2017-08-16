package com.naruto.mobile.framework.rpc.myhttp.common.transport;

import java.lang.reflect.Method;


/**
 * RPC传输调用抽象类
 * 
 */
public abstract class AbstractRpcCaller implements RpcCaller {
    /**
     * 调用方法
     */
    protected Method mMethod;
    /**
     * 请求数据
     */
    protected Object mReqData;

    /**
     * @param method 调用的方法
     * @param reqData 请求数据
     */
    public AbstractRpcCaller(Method method, Object reqData) {
        mMethod = method;
        mReqData = reqData;
    }

}
