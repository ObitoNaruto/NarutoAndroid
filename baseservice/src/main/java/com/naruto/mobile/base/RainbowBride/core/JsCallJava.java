package com.naruto.mobile.base.RainbowBride.core;

import android.net.Uri;
import android.text.TextUtils;
import android.webkit.WebView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.json.JSONException;
import org.json.JSONObject;

/**
 */
public class JsCallJava {
    private static final String JS_BRIDGE_PROTOCOL_SCHEMA = "rainbow";
    private String mClassName;
    private String mMethodName;
    private String mPort;
    private JSONObject mParams;

    private JsCallJava() {
    }

    public static JsCallJava newInstance() {
        return new JsCallJava();
    }

    /**
     * @param webView WebView
     * @param message rainbow://class:port/method?params
     */
    public void call(WebView webView, String message) {
        if (webView == null || TextUtils.isEmpty(message))
            return;
        parseMessage(message);
        invokeNativeMethod(webView);
    }

    /**
     * 解析message
     * @param message
     */
    private void parseMessage(String message) {
        if (!message.startsWith(JS_BRIDGE_PROTOCOL_SCHEMA))
            return;
        Uri uri = Uri.parse(message);
        mClassName = uri.getHost();//class
        String path = uri.getPath();//method
        if (!TextUtils.isEmpty(path)) {
            mMethodName = path.replace("/", "");
        } else {
            mMethodName = "";
        }
        mPort = String.valueOf(uri.getPort());//port
        try {
            mParams = new JSONObject(uri.getQuery());//params
        } catch (JSONException e) {
            e.printStackTrace();
            mParams = new JSONObject();
        }
    }

    /**
     * js调用native调用
     * @param webView
     */
    private void invokeNativeMethod(WebView webView) {
        Method method = NativeMethodInjectHelper.getInstance().findMethod(mClassName, mMethodName);//找到method
        String statusMsg;
        JsCallback jsCallback = JsCallback.newInstance(webView, mPort);//初始化回调
        if (method == null) {
            statusMsg = "Method (" + mMethodName + ") in this class (" + mClassName + ") not found!";
            JsCallback.invokeJsCallback(jsCallback, false, null, statusMsg);
            return;
        }
        //参数类型固定
        Object[] objects = new Object[3];
        objects[0] = webView;
        objects[1] = mParams;
        objects[2] = jsCallback;
        try {
            method.invoke(null, objects);//反射方法调用
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
