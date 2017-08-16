package com.naruto.mobile.framework.rpc.myhttp.protocol;


import com.naruto.mobile.framework.rpc.myhttp.common.RpcException;

/**
 * RPC序列化
 * 
 * @author sanping.li@alipay.com
 *
 */
public interface Serializer {

    /**
     * 协议封包
     * 
     * @return 协议封包对象
     */
    public Object packet() throws RpcException;
    
    /**
     * 附加参数－设备信息
     * @param o　设备信息对象
     * @throws RpcException
     */
    public void setExtParam(Object o) throws RpcException;

}
