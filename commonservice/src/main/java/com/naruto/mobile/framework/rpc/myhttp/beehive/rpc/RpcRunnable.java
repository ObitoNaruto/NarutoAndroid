package com.naruto.mobile.framework.rpc.myhttp.beehive.rpc;

/**
 * rpc执行体
 */
public interface RpcRunnable<ResultType> {

    ResultType execute(Object... params);

}
