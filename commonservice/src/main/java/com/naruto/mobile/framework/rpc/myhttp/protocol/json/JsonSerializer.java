package com.naruto.mobile.framework.rpc.myhttp.protocol.json;

import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.naruto.mobile.framework.rpc.myhttp.common.RpcException;
import com.naruto.mobile.framework.rpc.myhttp.protocol.AbstractSerializer;

/**
 * JSON序列化
 * 
 * @hide
 * @author sanping.li@alipay.com
 *
 */
public class JsonSerializer extends AbstractSerializer {
    public static final String VERSION = "1.0.0";

	private static final String TAG = "JsonSerializer";

    /**
     * 序号
     */
    private int mId;
    private Object mExtParam;

    /**
     * @param id 序号
     * @param operationType 操作码
     * @param params 参数
     */
    public JsonSerializer(int id, String operationType, Object params) {
        super(operationType, params);
        mId = id;
    }

    public void setExtParam(Object o)
    {
    	mExtParam = o;
    }
    
    @Override
    public Object packet() throws RpcException {
        ArrayList<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();

		if (null != mExtParam) {
			BasicNameValuePair extPair = new BasicNameValuePair("extParam",
					JSON.toJSONString(mExtParam));
			list.add(extPair);
		}
        BasicNameValuePair pair = new BasicNameValuePair("operationType", mOperationType);
        list.add(pair);
        BasicNameValuePair id = new BasicNameValuePair("id", mId + "");
        list.add(id);
//        LogCatLog.d(TAG, "mParams is:"+mParams);
        BasicNameValuePair params = new BasicNameValuePair("requestData",
                mParams==null?"[]":JSON.toJSONString(mParams,SerializerFeature.DisableCircularReferenceDetect));

        list.add(params);
        return list;
    }


	/**
     * 获取序号
     * 
     * @return 序号
     */
    public int getId() {
        return mId;
    }

    /**
     * 设置序号
     * 
     * @param id 序号
     */
    public void setId(int id) {
        mId = id;
    }

}
