package com.naruto.mobile.framework.biz.common;

import java.lang.annotation.Annotation;
import java.util.concurrent.FutureTask;

import com.naruto.mobile.framework.rpc.myhttp.common.RpcInterceptor;
import com.naruto.mobile.base.serviceaop.service.CommonService;

/**
 * Rpc服务
 * 
 */
public abstract class RpcService extends CommonService {
    /**
     * 获取前台Rpc代理对象
     * 
     * @param clazz clazz 类型
     * @return
     */
    public abstract <T> T getRpcProxy(Class<T> clazz);
    /**
     * 获取后台Rpc代理对象
     *
     * @param clazz clazz 类型
     * @return
     */
    public abstract <T> T getBgRpcProxy(Class<T> clazz);
    /**
     * 获取PBRpc代理对象
     * 
     * @param clazz clazz 类型
     * @return
     */
    public abstract <T> T getPBRpcProxy(Class<T> clazz);

    /**
     * 批量调用开始
     */
    public abstract void batchBegin();

    /**
     * 批量调用提交
     */
    public abstract FutureTask<?> batchCommit();
    
    /**
     * 添加协议参数
     * 
     * 一次添加只在当前线程的一次调用中生效
     * 
     * @param key
     * @param value
     */
    public abstract void addProtocolArgs(String key,String value);
    
    /**
     * 添加拦截器
     * 
     * @param clazz 对应的注解类
     * @param rpcInterceptor 拦截器
     */
    public abstract void addRpcInterceptor(Class<? extends Annotation> clazz,
                                           RpcInterceptor rpcInterceptor);

    /**
     * 设置对应的请求代理需要清除cookie
     * 
     * @param object 代理对象
     */
    public abstract void prepareResetCookie(Object object);
    
//    /**
//     * 设置场景，比如active
//     * @param time
//     * @param scene
//     */
//    public abstract void setScene(long time,String scene);
//

//    /**
//     * 设置场景，比如active
//     * @param time
//     * @param scene
//     */
//    public abstract String getScene();
//
//    public RpcInvokeContext getRpcInvokeContext(Object object) {
//        return null;
//    }
//
//    public void addRpcHeaderListener(RpcHeaderListener rpcHeaderListener) {
//        //Nothing..
//    }

}
