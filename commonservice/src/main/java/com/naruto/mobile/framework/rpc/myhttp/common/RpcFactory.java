package com.naruto.mobile.framework.rpc.myhttp.common;

import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.FutureTask;

/**
 * Rpc工厂
 */
public class RpcFactory {

    /**
     * 配置
     */
    private Config mConfig;

    /**
     * RPC调用器
     */
    private RpcInvoker mRpcInvoker;

    /**
     * 拦截器
     */
    private Map<Class<? extends Annotation>, RpcInterceptor> mInterceptors;

    public RpcFactory(Config config) {
        mConfig = config;
        mRpcInvoker = new RpcInvoker(this);
        mInterceptors = new HashMap<Class<? extends Annotation>, RpcInterceptor>();
    }

    @SuppressWarnings("unchecked")
    public <T> T getRpcProxy(Class<T> clazz) {
        //        typeChecker.isValidInterface(clazz);
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz},
                new RpcInvocationHandler(mConfig, clazz, mRpcInvoker));
    }

    /**
     * 批量调用开始
     */
    public void batchBegin() {
        mRpcInvoker.batchBegin();
    }

    /**
     * 批量调用提交
     */
    public FutureTask<?> batchCommit() {
        return mRpcInvoker.batchCommit();
    }

    /**
     * 添加协议参数
     */
    public void addProtocolArgs(String key, Object value) {
        RpcInvoker.addProtocolArgs(key, value);
    }

    /**
     * 添加拦截器
     *
     * @param clazz          对应的注解类
     * @param rpcInterceptor 拦截器
     */
    public void addRpcInterceptor(Class<? extends Annotation> clazz, RpcInterceptor rpcInterceptor) {
        mInterceptors.put(clazz, rpcInterceptor);
    }

    /**
     * 通过注解查找拦截器
     *
     * @param clazz 注解类型
     * @return 拦截器
     */
    public RpcInterceptor findRpcInterceptor(Class<? extends Annotation> clazz) {
        return mInterceptors.get(clazz);
    }

}
