package com.naruto.mobile.framework.rpc.myhttp.protocol;


/**
 * 抽象RPC序列化
 * 
 */
public abstract class AbstractSerializer implements Serializer{
    /**
     * 操作码
     */
    protected String mOperationType;
    /**
     * 参数
     */
    protected Object mParams;

    /**
     * @param operationType 操作码
     * @param params 参数
     */
    public AbstractSerializer(String operationType , Object params) {
        mOperationType = operationType;
        mParams = params;
    }

}
