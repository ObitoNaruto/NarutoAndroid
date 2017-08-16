package com.naruto.mobile.framework.rpc.myhttp.common.transport;


import com.naruto.mobile.framework.rpc.myhttp.common.RpcException;

/**
 * RPC传输调用器，管理请求，c/s通讯：http,socket，
 * 不涉及 p2p通讯：声波等
 * 
 */
public interface RpcCaller {

    /**
     * 调用
     * 
     * @return 响应数据
     * @throws RpcException
     */
    public Object call() throws RpcException;

}
