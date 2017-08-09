
package com.naruto.mobile.h5container.web;

import android.content.Context;
import android.text.TextUtils;
import android.webkit.WebView;

import java.util.HashMap;

import com.alibaba.fastjson.JSONObject;
import com.naruto.mobile.h5container.R;
import com.naruto.mobile.h5container.api.H5Param;
import com.naruto.mobile.h5container.core.H5PageImpl;
import com.naruto.mobile.h5container.env.H5Container;
import com.naruto.mobile.h5container.env.H5Environment;
import com.naruto.mobile.h5container.util.H5Log;
import com.naruto.mobile.h5container.util.H5Utils;

public class H5JSInjector {

    public static final String TAG = "H5JSInjector";

    private Object injectorLock;
    private boolean enableScanJs;
    private HashMap<String, String> bridgeParams;
    private WebView webView;
    private boolean injected;

    public H5JSInjector(H5PageImpl page) {
        this.webView = page.getWebView();
        this.injectorLock = new Object();
        this.enableScanJs = false;
        this.bridgeParams = new HashMap<String, String>();
        this.injected = false;
        JSONObject params = H5Utils.toJSONObject(page.getParams());
        String scenario = H5Utils.getString(params, H5Param.LONG_BIZ_SCENARIO);
        if (H5Container.SCAN_APP.equals(scenario)) {
            enableScanJS(true);
        }
        addParam("startupParams", params.toJSONString());
    }

    private void enableScanJS(boolean enable) {
        this.enableScanJs = enable;
    }

    public void reset() {
        injected = false;
    }

    public boolean inject(boolean finished) {
        if (webView == null && finished) {
            H5Log.e(TAG, "invalid webview parameter!");
            return false;
        }

        synchronized (injectorLock) {
            long time = System.currentTimeMillis();
            loadBridge(webView);
            loadWeinre(webView);
            if (finished) {
                // for this script depends on document event
                // on some platform may cause event not received bug.
                loadShare(webView);
            }

            if (finished && enableScanJs) {
                loadScan(webView);
            }
            loadDebug(webView);
            long delta = System.currentTimeMillis() - time;
            H5Log.d(TAG, "inject js total elapsed " + delta);
        }
        return true;
    }

    public void addParam(String key, String value) {
        if (TextUtils.isEmpty(key) || TextUtils.isEmpty(value)) {
            H5Log.e(TAG, "invalid js parameters!");
            return;
        }
        synchronized (injectorLock) {
            bridgeParams.put(key, value);
            H5Log.d(TAG, "addParam [key] " + key + " [value] " + value);
            if (injected) {
                webView.loadUrl("javascript:if(typeof AlipayJSBridge === 'object'){AlipayJSBridge."
                        + key + "=" + value + "'}");
            }
        }
    }

    public boolean isInjected() {
        return injected;
    }

    private void loadBridge(WebView webView) {
        long enterTime = System.currentTimeMillis();
        String bridgeStr = H5Utils.readRaw(R.raw.bridge_min);

        if (TextUtils.isEmpty(bridgeStr)) {
            bridgeStr = "";
            H5Log.d(TAG, "no bridge data defined!");
            return;
        }

        String startupStr = "AlipayJSBridge.startupParams=\"{startupParams}\"";
        String paramsStr = "";
        for (String key : bridgeParams.keySet()) {
            String value = bridgeParams.get(key);
            paramsStr += ";AlipayJSBridge." + key + "=" + value + ";";
        }

        // replace startup parameters
        if (!TextUtils.isEmpty(paramsStr)) {
            bridgeStr = bridgeStr.replace(startupStr, paramsStr);
        } else {
            H5Log.d(TAG, "no params data defined!");
        }

        H5Log.d(TAG, "bridgeStr " + bridgeStr);
        webView.loadUrl("javascript:" + bridgeStr);
        H5Log.d(TAG, "bridge data injected!");

        long deltaTime = System.currentTimeMillis() - enterTime;
        H5Log.d(TAG, "load bridge delta time " + deltaTime);
    }

    private void loadWeinre(WebView webView) {
        if (!H5Utils.isDebugable()) {
            H5Log.d(TAG, "weinre only work for debug package.");
            return;
        }
        Context context = webView.getContext();
        boolean enabled = H5Utils.getConfigBoolean(context, "weinre_enable");
        if (!enabled) {
            H5Log.d(TAG, "weinre feature not enabled");
            return;
        }

        long enterTime = System.currentTimeMillis();
        String server = H5Utils.getConfigString(context, "weinre_server");
        String portStr = H5Utils.getConfigString(context, "weinre_port");
        int port = 0;
        try {
            port = Integer.valueOf(portStr);
        } catch (Exception e) {
            H5Log.e(TAG, "load weinre exception", e);
            return;
        }
        if (TextUtils.isEmpty(server) || port <= 0) {
            H5Log.w(TAG, "invalid weinre settings!");
            return;
        }
        String url = "http://" + server + ":" + port
                + "/target/target-script-min.js:clientIP";
        String data = "(function(){var js=document.createElement('script');js.src='"
                + url + "';document.body.appendChild(js);})();";
        webView.loadUrl("javascript:" + data);
        H5Log.d(TAG, "weinre data injected!");

        long deltaTime = System.currentTimeMillis() - enterTime;
        H5Log.d(TAG, "load weinre delta time " + deltaTime);
    }

    private void loadDebug(WebView webView) {
        long enterTime = System.currentTimeMillis();

        String h5_DynamicScript = H5Environment.getConfig("h5_DynamicScript");
        // h5_DynamicScript =
        // "http://ux.alipay-inc.com/ftp/h5/dawson/remote.js";
        if (TextUtils.isEmpty(h5_DynamicScript)) {
            H5Log.d(TAG, "no config found for dynamic script");
            return;
        }
        String appendjs = "var jsref=document.createElement('script'); jsref.setAttribute(\"type\",\"text/javascript\");jsref.setAttribute(\"src\", \""
                + h5_DynamicScript
                + "\");document.getElementsByTagName(\"head\")[0].appendChild(jsref)";
        webView.loadUrl("javascript:" + appendjs);

        long deltaTime = System.currentTimeMillis() - enterTime;
        H5Log.d(TAG, "load debug delta time " + deltaTime);
    }

    private void loadShare(WebView webView) {
        String shareStr = H5Utils.readRaw(R.raw.share_min);
        if (TextUtils.isEmpty(shareStr)) {
            return;
        }
        webView.loadUrl("javascript:" + shareStr);
    }

    private void loadScan(WebView webView) {
        String scanStr = H5Utils.readRaw(R.raw.scan_min);
        if (TextUtils.isEmpty(scanStr)) {
            return;
        }
        webView.loadUrl("javascript:" + scanStr);
    }
}
