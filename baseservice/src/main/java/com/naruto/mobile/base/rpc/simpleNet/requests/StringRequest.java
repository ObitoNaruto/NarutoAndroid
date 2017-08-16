package com.naruto.mobile.base.rpc.simpleNet.requests;


import com.naruto.mobile.base.rpc.simpleNet.base.Request;
import com.naruto.mobile.base.rpc.simpleNet.base.Response;

public class StringRequest extends Request<String> {

    public StringRequest(HttpMethod method, String url, RequestListener<String> listener) {
        super(method, url, listener);
    }

    @Override
    public String parseResponse(Response response) {
        return new String(response.getRawData());
    }

}
