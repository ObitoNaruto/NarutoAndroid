
package com.naruto.mobile.h5container.bridge;

import android.webkit.WebView;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.naruto.mobile.h5container.api.H5Bridge;
import com.naruto.mobile.h5container.api.H5CallBack;
import com.naruto.mobile.h5container.api.H5Intent;
import com.naruto.mobile.h5container.api.H5Plugin;
import com.naruto.mobile.h5container.core.H5IntentImpl;
import com.naruto.mobile.h5container.env.H5Container;
import com.naruto.mobile.h5container.util.H5Log;
import com.naruto.mobile.h5container.util.H5Utils;

public class H5BridgeImpl implements H5Bridge {

    public static final String TAG = "H5Bridge";

    private static final String INVOKE_JS = "AlipayJSBridge._invokeJS(%s)";

    private WebView webView;

    private Map<String, H5CallBack> callPool;

    private BridgePolicy policy;

    private boolean released;

    public H5BridgeImpl(WebView webView) {
        this.webView = webView;
        this.released = false;
        this.callPool = new HashMap<String, H5CallBack>();
    }

    public void onRelease() {
        released = true;
        webView = null;
        callPool.clear();
        callPool = null;
        policy = null;
    }

    @Override
    public void sendToNative(H5Intent intent) {
        if (intent == null || released) {
            return;
        }

        postNative(intent);
    }

    private void postNative(final H5Intent intent) {
        H5Utils.runOnMain(new Runnable() {

            @Override
            public void run() {
                executeNative(intent);
            }

        });
    }

    private void executeNative(H5Intent intent) {
        String intentId = intent.getId();
        boolean inPool = callPool.containsKey(intentId);
        JSONObject callParam = intent.getParam();

        if (inPool) {
            H5CallBack callback = callPool.remove(intentId);
            callback.onCallBack(callParam);
            return;
        }

        final String action = intent.getAction();
        if (policy != null && policy.shouldBan(action)) {
            H5Log.w(TAG, "JSAPI " + action + " is banned!");
            JSONObject result = new JSONObject();
            result.put("error", 4);
            result.put("errorMessage", "接口不存在");
            intent.sendBack(result);
            return;
        }

        String paramStr = null;
        if (callParam != null) {
            paramStr = callParam.toJSONString();
        }

        H5Log.d("h5_jsapi_call name={" + action + "} params={" + paramStr + "}");
        H5Container.getMesseger().sendIntent(intent);

        H5Intent.Error error = intent.getError();
        if (error == H5Intent.Error.NONE) {
            return;
        }

        H5Intent call = new H5IntentImpl(H5Plugin.H5_PAGE_JS_CALL);
        JSONObject param = new JSONObject();
        param.put("error", "" + error.ordinal());
        param.put("funcName", "" + action);
        call.setParam(param);
        H5Container.getMesseger().sendIntent(call);
        H5Log.e("error | h5_jsapi_error name={" + action + "} error={" + error
                + "}");
    }

    @Override
    public void sendToWeb(H5Intent intent) {
        if (intent == null || released) {
            return;
        }

        postWeb(intent);
    }

    private void postWeb(final H5Intent intent) {
        H5Utils.runOnMain(new Runnable() {

            @Override
            public void run() {
                executeWeb(intent);
            }

        });
    }

    private void executeWeb(H5Intent intent) {
        if (intent == null || webView == null) {
            return;
        }

        String intentId = intent.getId();
        String action = intent.getAction();
        JSONObject param = intent.getParam();
        String type = intent.getType();
        boolean keep = false;
        if (intent instanceof H5IntentImpl) {
            keep = ((H5IntentImpl) intent).isKeep();
        }

        JSONObject jo = new JSONObject();
        jo.put(H5Container.CLIENT_ID, intentId);
        jo.put(H5Container.FUNC, action);
        jo.put(H5Container.PARAM, param);
        jo.put(H5Container.MSG_TYPE, type);
        jo.put(H5Container.KEEP_CALLBACK, keep);

        String message = jo.toJSONString();
        String joMsg = JSON.toJSONString(message);

        String javascript = String.format(INVOKE_JS, joMsg);
        try {
            H5Log.d(TAG, "executeJavascript " + javascript);
            webView.loadUrl("javascript:" + javascript);
        } catch (Exception e) {
            H5Log.e(TAG, "executeJavascript exception", e);
        }
    }

    @Override
    public void sendToWeb(String action, JSONObject param, H5CallBack callback) {
        if (released) {
            return;
        }
        H5IntentImpl intent = new H5IntentImpl();
        intent.setAction(action);
        intent.setParam(param);
        intent.setType(H5Container.CALL);
        if (callback != null) {
            String clientId = intent.getId();
            callPool.put(clientId, callback);
        }

        sendToWeb(intent);
    }

    @Override
    public void setBridgePolicy(BridgePolicy policy) {
        this.policy = policy;
    }
}
