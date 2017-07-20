package com.naruto.mobile.base.rpc.volley;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

public class GsonRequest2<T> extends Request<T> implements Response.ErrorListener{



    private final Gson gson = new Gson();

    private final Class<T> clazz;

    private final Map<String, String> headers;

    private final Map<String, String> params;

    private final Response.Listener<T> listener;

    /**
     * Make a GET request and return a parsed object from JSON.
     *
     * @param url     URL of the request to make
     * @param clazz   Relevant class object, for Gson's reflection
     * @param headers Map of request headers
     */
    public GsonRequest2(String url, Class<T> clazz, Map<String, String> headers,
            Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        this.clazz = clazz;
        this.headers = headers;
        this.params = null;
        this.listener = listener;
    }

    /**
     * Make a request and return a parsed object from JSON.
     *
     * @param url URL of the request to make
     * @param clazz Relevant class object, for Gson's reflection
     * @param headers Map of request headers
     */
    public GsonRequest2(int type, String url, Class<T> clazz, Map<String, String> headers,
            Map<String, String> params,
            Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(type, url, errorListener);
        this.clazz = clazz;
        this.headers = headers;
        this.params = params;
        this.listener = listener;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }

    @Override
    public Map<String, String> getParams() throws AuthFailureError{
        return params != null ? params : super.getParams();
    }

    @Override
    protected void deliverResponse(T response) {
        if(listener != null){
            listener.onResponse(response);
        }
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            return Response.success(gson.fromJson(json, clazz), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
    }

    public final Class<T> getClazz(){
        return clazz;
    }
}