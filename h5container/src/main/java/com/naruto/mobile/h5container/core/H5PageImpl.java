
package com.naruto.mobile.h5container.core;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Set;

import com.alibaba.fastjson.JSONObject;
import com.naruto.mobile.h5container.api.H5Bridge;
import com.naruto.mobile.h5container.api.H5Context;
import com.naruto.mobile.h5container.api.H5Data;
import com.naruto.mobile.h5container.api.H5Page;
import com.naruto.mobile.h5container.api.H5Param;
import com.naruto.mobile.h5container.api.H5Plugin;
import com.naruto.mobile.h5container.api.H5PluginManager;
import com.naruto.mobile.h5container.api.H5Scenario;
import com.naruto.mobile.h5container.api.H5Service;
import com.naruto.mobile.h5container.api.H5Session;
import com.naruto.mobile.h5container.bridge.H5BridgeImpl;
import com.naruto.mobile.h5container.data.H5MemData;
import com.naruto.mobile.h5container.env.H5Container;
import com.naruto.mobile.h5container.env.H5Environment;
import com.naruto.mobile.h5container.plugin.H5ActionSheetPlugin;
import com.naruto.mobile.h5container.plugin.H5AlertPlugin;
import com.naruto.mobile.h5container.plugin.H5InjectPlugin;
import com.naruto.mobile.h5container.plugin.H5LoadingPlugin;
import com.naruto.mobile.h5container.plugin.H5LongClickPlugin;
import com.naruto.mobile.h5container.plugin.H5NotifyPlugin;
import com.naruto.mobile.h5container.plugin.H5PagePlugin;
import com.naruto.mobile.h5container.plugin.H5ShakePlugin;
import com.naruto.mobile.h5container.ui.H5Activity;
import com.naruto.mobile.h5container.util.FileUtil;
import com.naruto.mobile.h5container.util.H5Log;
import com.naruto.mobile.h5container.util.H5UrlHelper;
import com.naruto.mobile.h5container.util.H5Utils;
import com.naruto.mobile.h5container.web.H5WebChromeClient;
import com.naruto.mobile.h5container.web.H5WebView;
import com.naruto.mobile.h5container.web.H5WebViewClient;

public class H5PageImpl extends H5CoreTarget implements H5Page {

    public static final String TAG = "H5PageImpl";

    private Activity activity;
    private H5SessionImpl h5Session;
    private Bundle startParams;
    private H5WebView h5WebView;
    private H5BridgeImpl h5Bridge;
    private H5PageHandler h5PageHandler;
    private H5Context h5Context;

    private boolean exited;

    private H5WebChromeClient h5ChromeClient;
    private H5WebViewClient h5ViewClient;

    public H5PageImpl(Activity activity, Bundle params) {
        H5Environment.setContext(activity);
        this.h5Context = new H5Context(activity);
        this.activity = activity;
        this.exited = false;

        String hostName = H5Utils.getClassName(activity);
        H5Log.d(TAG, "h5 page host in activity " + hostName);

        this.startParams = params;
        // if parameter not set, use activity default
        if (startParams == null) {
            startParams = activity.getIntent().getExtras();
        }

        // may user parameter not set
        if (startParams == null) {
            startParams = new Bundle();
        }

        // parse magic options and unify parameters
        parseMagicOptions(startParams);
        H5ParamParser parser = new H5ParamParser();
        startParams = parser.parse(startParams, true);

        this.h5Data = new H5MemData();

        h5WebView = new H5WebView(activity);
        H5Log.d("h5_create_webview appId={" + "} params={" + "}");
        boolean enableFile = ifEnableFileAccess();
        H5Log.d(TAG, "enable webview file access " + enableFile);
        h5WebView.init(enableFile);

        h5Bridge = new H5BridgeImpl(h5WebView);

        h5ChromeClient = new H5WebChromeClient(this);
        h5WebView.setWebChromeClient(h5ChromeClient);

        h5ViewClient = new H5WebViewClient(this);
        h5WebView.setWebViewClient(h5ViewClient);

        initPlugins();

        initSession();
        h5ViewClient.setWebProvider(h5Session);

        if (!(activity instanceof H5Activity)) {
            applyParams();
        }// else apply later in h5activity
    }

    private boolean ifEnableFileAccess() {
        String urlStr = H5Utils.getString(startParams, H5Param.LONG_URL);
        Uri uri = H5UrlHelper.parseUrl(urlStr);
        if (uri == null) {
            return false;
        }

        String scheme = uri.getScheme();
        if (!"file".equals(scheme)) {
            return false;
        }

        String filePath = uri.getPath();
        String rootPath = H5Utils.getApplicaitonDir() + "/files/apps";
        boolean underRoot = FileUtil.childOf(filePath, rootPath);
        String installPath = H5Utils.getString(startParams, H5Container.INSTALL_PATH);
        boolean underStall = FileUtil.childOf(filePath, installPath);
        if (underStall && underRoot) {
            return true;
        }
        H5Log.d(TAG, "NOT ALLOWED to load file scheme " + urlStr);
        return false;
    }

    private void parseMagicOptions(Bundle params) {
        if (params == null) {
            H5Log.w(TAG, "invalid magic parameter!");
            return;
        }

        String urlStr = H5Utils.getString(params, H5Param.URL);
        if (TextUtils.isEmpty(urlStr)) {
            urlStr = H5Utils.getString(params, H5Param.LONG_URL);
        }

        if (TextUtils.isEmpty(urlStr)) {
            H5Log.e(TAG, "no url found in magic parameter");
            return;
        }

        String decodedOptions = null;
        Uri uri = H5UrlHelper.parseUrl(urlStr);
        String optionsStr = H5UrlHelper.getParam(uri, "__webview_options__", null);
        if (TextUtils.isEmpty(optionsStr)) {
            H5Log.w(TAG, "no magic options found");
            return;
        }

        H5Log.d(TAG, "found magic options " + optionsStr);

        try {
            decodedOptions = URLDecoder.decode(optionsStr, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
        }
        if (TextUtils.isEmpty(decodedOptions)) {
            H5Log.e(TAG, "faild to decode magic options");
            return;
        }

        H5ParamParser parser = new H5ParamParser();
        try {
            String[] pairs = decodedOptions.split("&");
            for (String pair : pairs) {
                String[] values = pair.split("=");
                if (values.length < 2) {
                    continue;
                }
                String key = URLDecoder.decode(values[0], "UTF-8");
                String value = URLDecoder.decode(values[1], "UTF-8");

                // clean old(long name & short name) parameters
                parser.remove(params, key);
                // put new parameter to bundle.
                params.putString(key, (String) value);
                H5Log.d(TAG, "decode magic option [key] " + key + " [value] "
                        + value);
            }
        } catch (Exception e) {
            H5Log.e(TAG, "failed to decode magic option.", e);
            return;
        }
    }

    @Override
    public void onRelease() {
        h5ViewClient.onRelease();
        h5ViewClient = null;
        h5ChromeClient.onRelease();
        h5ChromeClient = null;
        h5Bridge.onRelease();
        h5Bridge = null;
        startParams = null;
        activity = null;
        h5Session = null;
        h5WebView.onRelease();
        h5WebView = null;
        h5Context = null;
        h5PageHandler = null;
        super.onRelease();
    }

    @Override
    public H5Context getContext() {
        return this.h5Context;
    }

    @Override
    public H5Session getSession() {
        return this.h5Session;
    }

    @Override
    public String getUrl() {
        return h5ViewClient.getPageUrl();
    }

    @Override
    public Bundle getParams() {
        return this.startParams;
    }

    @Override
    public String getTitle() {
        return h5WebView.getTitle();
    }

    @Override
    public H5Bridge getBridge() {
        return h5Bridge;
    }

    public H5WebView getWebView() {
        return h5WebView;
    }

    public H5WebViewClient getViewClient() {
        return h5ViewClient;
    }

    @Override
    public boolean exitPage() {
        if (exited) {
            H5Log.e(TAG, "page already exited!");
            return false;
        }

        if (h5WebView != null) {
            // remove TTS api added in AccessibilityInjector so as to unbind the service connection
            // from TTS service to avoid ServiceConnectionLeaked
            h5WebView.getSettings().setJavaScriptEnabled(false);
        }

        this.exited = true;

        if (h5PageHandler != null && !h5PageHandler.shouldExit()) {
            H5Log.w(TAG, "page exit intercepted!");
            return false;
        }

        if (activity != null) {
            activity.finish();
        }

        return h5Session.removePage(this);
    }

    private void initPlugins() {
        final H5PluginManager pm = getPluginManager();
        pm.register(new H5AlertPlugin(this));
        pm.register(new H5LoadingPlugin(this));
        pm.register(new H5NotifyPlugin(this));
        pm.register(new H5ActionSheetPlugin(this));
        pm.register(new H5ShakePlugin());
        pm.register(new H5InjectPlugin(this));
        pm.register(new H5LongClickPlugin(this));
        pm.register(new H5PagePlugin(this));
    }

    private void initSession() {
        String sessionId = H5Utils.getString(startParams, H5Param.SESSION_ID);
        H5Service service = H5Container.getService();
        h5Session = (H5SessionImpl) service.getSession(sessionId);

        H5Scenario h5Scenario = h5Session.getScenario();
        String scenarioName = H5Utils.getString(startParams,
                H5Param.LONG_BIZ_SCENARIO);
        if (!TextUtils.isEmpty(scenarioName) && h5Scenario == null) {
            H5Log.d(TAG, "set session scenario " + scenarioName);
            h5Scenario = new H5ScenarioImpl(scenarioName);
            h5Session.setScenario(h5Scenario);
        }
    }

    public void applyParams() {
        h5Session.addPage(this);


        Set<String> keys = startParams.keySet();
        for (String key : keys) {
            String intentName = null;
            JSONObject param = new JSONObject();
            if (H5Param.LONG_URL.equals(key)) {
                String url = H5Utils.getString(startParams, key);
                if (!TextUtils.isEmpty(url)) {
                    intentName = H5Plugin.H5_PAGE_LOAD_URL;
                    param.put(H5Param.LONG_URL, url);
                    String publicId = H5Utils.getString(startParams,
                            H5Param.PUBLIC_ID, "");
                    param.put(H5Param.PUBLIC_ID, publicId);
                }
            } else if (H5Param.LONG_SHOW_LOADING.equals(key)) {
                boolean value = H5Utils.getBoolean(startParams, key, false);
                if (value == true) {
                    intentName = H5Plugin.SHOW_LOADING;
                }
            } else if (H5Param.LONG_BACK_BEHAVIOR.equals(key)) {
                String behavior = H5Utils.getString(startParams, key);
                intentName = H5Plugin.H5_PAGE_BACK_BEHAVIOR;
                param.put(H5Param.LONG_BACK_BEHAVIOR, behavior);
            } else if (H5Param.LONG_CCB_PLUGIN.equals(key)) {
                boolean enable = H5Utils.getBoolean(startParams, key, false);
                if (enable) {
                    intentName = key;
                    param.put(key, true);
                }
            } else if (H5Param.LONG_BACKGROUND_COLOR.equals(key)) {
                int color = H5Utils.getInt(startParams, key);
                param.put(key, color);
                intentName = H5Plugin.H5_PAGE_BACKGROUND;
            } /*
               * proxy is not supported now else if (H5Param.LONG_ENABLE_PROXY.equals(key)) {
               * boolean enable = H5Utils.getBoolean(startParams, H5Param.LONG_ENABLE_PROXY, false);
               * param.put(H5Param.LONG_ENABLE_PROXY, enable); intentName = H5Plugin.SET_PROXY; }
               */

            if (!TextUtils.isEmpty(intentName)) {
                sendIntent(intentName, param);
            }
        }

        initTextSize();
    }

    private void initTextSize() {
        H5Scenario h5Scenario = h5Session.getScenario();
        if (h5Scenario == null) {
            return;
        }
        H5Data scenarioData = h5Scenario.getData();
        String sizeStr = scenarioData.get(H5Container.FONT_SIZE);
        if (TextUtils.isEmpty(sizeStr)) {
            return;
        }
        try {
            int size = Integer.parseInt(sizeStr);
            setTextSize(size);
        } catch (Exception e) {
            H5Log.e("failed to parse scenario font size.", e);
        }
    }

    @Override
    public void setHandler(H5PageHandler handler) {
        this.h5PageHandler = handler;
    }

    @Override
    public View getContentView() {
        return h5WebView;
    }

    @Override
    public void loadDataWithBaseURL(String baseUrl, String data,
            String mimeType, String encoding, String historyUrl) {
        JSONObject param = new JSONObject();
        param.put("baseUrl", baseUrl);
        param.put("data", data);
        param.put("mimeType", mimeType);
        param.put("encoding", encoding);
        param.put("historyUrl", historyUrl);
        sendIntent(H5Plugin.H5_PAGE_SHOULD_LOAD_DATA, param);
    }

    @Override
    public void loadUrl(String url) {
        JSONObject param = new JSONObject();
        param.put(H5Param.LONG_URL, url);
        sendIntent(H5Plugin.H5_PAGE_LOAD_URL, param);
    }

    @Override
    public void setTextSize(int textSize) {
        h5WebView.setTextSize(textSize);
    }

}
