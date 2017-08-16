package com.naruto.mobile.framework.rpc.myhttp.beehive.rpc;

/**
 * rpc事件封装
 * Created by zhanqu.awb on 15/2/2.
 */
public class RpcEvent<ResultType> {

    // 具体rpc事件状态
    public String status;

    // 事件发送者
    public RpcRunner rpcRunner;

    public RpcTask<ResultType> rpcTask;

    // rpc请求结果
    public ResultType result;

    public Exception exception;

    private boolean isNetworkException;//NOPMD

    public RpcEvent(RpcRunner runner, RpcTask<ResultType> rpcTask,
            ResultType result, Exception exception) {
        this.rpcRunner = runner;
        this.rpcTask = rpcTask;
        this.result = result;
        this.exception = exception;
        this.isNetworkException = RpcUtil.isNetworkException(exception);
    }

    public String getStatus() {
        return status;
    }

    public RpcTask<ResultType> getRpcTask() {
        return rpcTask;
    }

    public ResultType getResult() {
        return result;
    }

}
