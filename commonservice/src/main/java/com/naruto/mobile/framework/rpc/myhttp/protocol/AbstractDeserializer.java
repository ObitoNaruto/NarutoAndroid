package com.naruto.mobile.framework.rpc.myhttp.protocol;

import java.lang.reflect.Type;

/**
 * 抽象RPC反序列化
 * 
 */
public abstract class AbstractDeserializer implements Deserializer{
    /**
     * 反序列化类型
     */
    protected Type mType;
    /**
     * 数据
     */
    protected String mData;
    
    
    public AbstractDeserializer(Type type, String data) {
        mType = type;
        mData = data;
    }
}
