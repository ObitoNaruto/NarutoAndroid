package com.naruto.mobile.framework.rpc.myhttp.beehive.rpc;

/**
 * 将rpc运行封装为task概念，包含外部请求参数，rpc实际执行接口，rpc返回回调接口
 */
public class RpcTask<ResultType> {

    /**
     * rpc执行过程配置
     */
    private RpcRunConfig runConfig;

    /**
     * rpc实际执行类
     */
    private RpcRunnable<ResultType> runnable;

    /**
     * rpc结果处理回调
     */
    private RpcSubscriber<ResultType> subscriber;

    /**
     * rpc结果 (默认为空，采用内置的标准处理方式)
     */
    private BaseRpcResultProcessor rpcResultProcessor;

    /**
     * 外部调用的入参 （注意，不是rpc接口调用的请求参数）
     */
    private Object[] params;

    public RpcTask(RpcRunConfig config,
            RpcRunnable<ResultType> r,
            RpcSubscriber<ResultType> subscriber,
            BaseRpcResultProcessor processor) {
        this.runConfig = config;
        this.runnable = r;
        this.subscriber = subscriber;
        this.rpcResultProcessor = processor;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

    public RpcRunConfig getRunConfig() {
        return runConfig;
    }

    public void setRpcResultProcessor(BaseRpcResultProcessor rpcResultProcessor) {
        this.rpcResultProcessor = rpcResultProcessor;
    }

    public RpcRunnable<ResultType> getRunnable() {
        return runnable;
    }

    public RpcSubscriber<ResultType> getSubscriber() {
        return subscriber;
    }

    public BaseRpcResultProcessor getRpcResultProcessor() {
        return rpcResultProcessor;
    }

    public void setRunConfig(RpcRunConfig runConfig) {
        this.runConfig = runConfig;
    }

    public void setSubscriber(RpcSubscriber<ResultType> subscriber) {
        this.subscriber = subscriber;
    }

}
