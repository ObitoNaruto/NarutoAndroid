package com.naruto.mobile.framework.rpc.myhttp.protocol;


import com.naruto.mobile.framework.rpc.myhttp.common.RpcException;

/**
 * RPC反序列化
 * 
 */
public interface Deserializer {

    /**
     * 协议解包
     * 
     */
    public abstract Object parser() throws RpcException;
}
