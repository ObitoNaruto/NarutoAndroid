package com.naruto.mobile.framework.rpc.myhttp.protocol.json;

import java.lang.reflect.Type;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.naruto.mobile.framework.rpc.myhttp.common.RpcException;
import com.naruto.mobile.framework.rpc.myhttp.protocol.AbstractDeserializer;

/**
 * JSON反序列化
 * 
 * @hide
 */
public class JsonDeserializer extends AbstractDeserializer {

    public JsonDeserializer(Type type, String data) {
        super(type, data);
    }

    @Override
    public Object parser() throws RpcException {
    	
        try{        	
	        JSONObject jsonObject = JSON.parseObject(mData);
	        
	        int resultStatus = jsonObject.getIntValue("resultStatus");
	        String memo = jsonObject.getString("tips");
	        if (resultStatus != RpcException.ErrorCode.OK) {
	            throw new RpcException(resultStatus, memo);
	        }	        
	        
	        Object value = JSON.parseObject(jsonObject.getString("result"), mType);	        
	        return value;
	        
        }catch(JSONException e){
            throw new RpcException(RpcException.ErrorCode.CLIENT_DESERIALIZER_ERROR,
            		"response  =" + mData + ":" + e == null ? "": e.getMessage());
        }       
    }
}
