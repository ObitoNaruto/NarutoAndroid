package com.naruto.mobile.framework.rpc.myhttp.common;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import com.naruto.mobile.framework.rpc.myhttp.common.transport.RpcCaller;
import com.naruto.mobile.framework.rpc.myhttp.common.transport.http.HttpCaller;
import com.naruto.mobile.framework.rpc.myhttp.protocol.Deserializer;
import com.naruto.mobile.framework.rpc.myhttp.protocol.Serializer;
import com.naruto.mobile.framework.rpc.myhttp.protocol.json.JsonDeserializer;
import com.naruto.mobile.framework.rpc.myhttp.protocol.json.JsonSerializer;

/**
 * RpcInvocationHandler调用处理：调用RpcInvoker.invoke ， 还能获得对应的JsonSerializer 和 JsonDeserializer
 * TODO 要重构，这个类职责不明
 * 
 */
public class RpcInvocationHandler implements InvocationHandler {
    /**
     * 配置
     */
    private Config mConfig;
    
    /**
     * 接口类
     */
    private Class<?> mClazz;
    /**
     * RpcInvoker
     */
    private RpcInvoker mRpcInvoker;

    /**
     * @param clazz 类
     * @param rpcInvoker RpcInvoker
     */
    public RpcInvocationHandler(Config config,Class<?> clazz, RpcInvoker rpcInvoker) {
        mConfig = config;
        mClazz = clazz;
        mRpcInvoker = rpcInvoker;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws RpcException {
        return mRpcInvoker.invoke(this, proxy, mClazz, method, args);
    }

    /**
     * 获取传输类型
     * 
     * @param method 方法
     * @param reqData 请求数据
     * @return 传输类型
     */
    public RpcCaller getTransport(Method method, Object reqData) {
        return new HttpCaller(mConfig,method, reqData);
    }

    /**
     * 获取序列化
     * 
     * @param id 序列号
     * @param operationType 操作码
     * @param params 参数
     * @return 序列化
     */
    public Serializer getSerializer(int id, String operationType, Object params) {
        return new JsonSerializer(id, operationType, params);
    }

    /**
     * 获取反序列化
     * 
     * @param type 类型
     * @param data 数据 
     * @return 反序列化
     */
    public Deserializer getDeserializer(Type type, String data) {
        return new JsonDeserializer(type, data);
    }

}
