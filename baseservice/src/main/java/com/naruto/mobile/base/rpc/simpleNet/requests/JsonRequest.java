package com.naruto.mobile.base.rpc.simpleNet.requests;

import com.naruto.mobile.base.rpc.simpleNet.base.Request;
import com.naruto.mobile.base.rpc.simpleNet.base.Response;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 返回的数据类型为Json的请求, Json对应的对象类型为JSONObject
 * 
 * @author mrsimple
 */
public class JsonRequest extends Request<JSONObject> {

    public JsonRequest(HttpMethod method, String url, RequestListener<JSONObject> listener) {
        super(method, url, listener);
    }

    
    /**
     * 将Response的结果转换为JSONObject
     */
    @Override
    public JSONObject parseResponse(Response response) {
        String jsonString = new String(response.getRawData());
        try {
            return new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
