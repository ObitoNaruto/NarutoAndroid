package com.naruto.mobile.framework.rpc.myhttp.transport;

import java.util.concurrent.Future;

/**
 * 传输，管理请求，c/s通讯：http,socket，p2p通讯：声波等
 */
public interface Transport {
    /**
     * 执行请求
     * 
     * @param request 请求
     */
    public Future<Response> execute(Request request);
}
