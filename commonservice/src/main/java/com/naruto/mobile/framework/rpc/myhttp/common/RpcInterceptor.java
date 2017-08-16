package com.naruto.mobile.framework.rpc.myhttp.common;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * RPC调用拦截器
 * 
 * @author sanping.li@alipay.com
 *
 * @param <T>
 */
public interface RpcInterceptor {
    /**
     * 前置处理
     * 
     * @param proxy 调用对象
     * @param retValue 返回值
     * @param clazz 类名
     * @param method 方法
     * @param args 参数
     * @param annotation 注解
     * @param extParams 扩展协议参数
     * @return 是否需要被下一步处理
     * @throws RpcException
     */
    public boolean preHandle(Object proxy, ThreadLocal<Object> retValue, Class<?> clazz, Method method, Object[] args,
            Annotation annotation, ThreadLocal<Map<String, Object>> extParams) throws RpcException;

    /**
     * 后置处理
     * 
     * @param proxy 调用对象
     * @param retValue 返回值
     * @param clazz 类名
     * @param method 方法
     * @param args 参数
     * @return 是否需要被下一步处理
     * @throws RpcException
     */
    public boolean postHandle(Object proxy, ThreadLocal<Object> retValue, Class<?> clazz, Method method, Object[] args,
            Annotation annotation)
                                                                                            throws RpcException;

    /**
     * 异常处理
     * 
     * @param proxy 调用对象
     * @param retValue 返回值
     * @param clazz 类名
     * @param method 方法
     * @param args 参数
     * @param exception 异常
     * @return 是否需要被下一步处理
     * @throws RpcException
     */
    public boolean exceptionHandle(Object proxy, ThreadLocal<Object> retValue, Class<?> clazz, Method method,
            Object[] args,
            RpcException exception, Annotation annotation) throws RpcException;
}
