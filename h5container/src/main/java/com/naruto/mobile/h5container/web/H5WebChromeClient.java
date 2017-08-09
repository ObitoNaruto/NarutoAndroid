
package com.naruto.mobile.h5container.web;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.alibaba.fastjson.JSONObject;
import com.naruto.mobile.h5container.api.H5Bridge;
import com.naruto.mobile.h5container.api.H5Page;
import com.naruto.mobile.h5container.api.H5Plugin;
import com.naruto.mobile.h5container.core.H5IntentImpl;
import com.naruto.mobile.h5container.env.H5Container;
import com.naruto.mobile.h5container.util.H5Log;
import com.naruto.mobile.h5container.util.H5Utils;

public class H5WebChromeClient extends WebChromeClient {

    public static final String TAG = "H5WebChromeClient";

    private static final String BRIDGE_MSG_HEADER = "h5container.message: ";

    private H5Page h5Page;

    public H5WebChromeClient(H5Page page) {
        this.h5Page = page;
    }

    @Override
    public boolean onCreateWindow(WebView view, boolean isDialog,
            boolean isUserGesture, Message resultMsg) {
        return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
    }

    @Override
    public void onCloseWindow(WebView window) {
        super.onCloseWindow(window);
    }

    public void getVisitedHistory(ValueCallback<String[]> callback) {
        super.getVisitedHistory(callback);
    }

    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        H5Log.d(TAG, "onConsoleMessage " + consoleMessage.message());
        return super.onConsoleMessage(consoleMessage);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onConsoleMessage(String message, int lineNumber, String sourceID) {
        super.onConsoleMessage(message, lineNumber, sourceID);
        H5Log.d(TAG, "onConsoleMessage [message] " + message + " [lineNumber] "
                + lineNumber + " [sourceID] " + sourceID);

        if (TextUtils.isEmpty(message) || h5Page == null) {
            return;
        }

        String msgText = null;
        if (message.startsWith(BRIDGE_MSG_HEADER)) {
            msgText = message.replaceFirst(BRIDGE_MSG_HEADER, "");
        }

        if (TextUtils.isEmpty(msgText)) {
            return;
        }

        JSONObject joMessage = H5Utils.parseObject(msgText);
        if (joMessage == null || joMessage.isEmpty()) {
            return;
        }

        String clientId = H5Utils.getString(joMessage, H5Container.CLIENT_ID);
        String name = H5Utils.getString(joMessage, H5Container.FUNC);
        String msgType = H5Utils.getString(joMessage, H5Container.MSG_TYPE);
        boolean keep = H5Utils.getBoolean(joMessage, H5Container.KEEP_CALLBACK, false);

        if (TextUtils.isEmpty(clientId)) {
            H5Log.w(TAG, "invalid client id!");
            return;
        }

        H5Log.d(TAG, "[name] " + name + " [msgType] " + msgType
                + " [clientId] " + clientId);

        JSONObject joParam = H5Utils.getJSONObject(joMessage,
                H5Container.PARAM, null);

        H5Bridge bridge = h5Page.getBridge();
        H5IntentImpl intent = new H5IntentImpl();
        intent.setAction(name);
        intent.setBridge(bridge);
        intent.setParam(joParam);
        intent.setTarget(h5Page);
        intent.setType(msgType);
        intent.setId(clientId);
        intent.setKeep(keep);

        bridge.sendToNative(intent);
    }

    @Override
    public boolean onJsPrompt(WebView view, String url, String message,
            String defaultValue, JsPromptResult result) {
        H5Log.d(TAG, "onJsPrompt [message] " + message + " [url] " + url);
        return super.onJsPrompt(view, url, message, defaultValue, result);
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
        H5Log.d(TAG, "onProgressChanged [progress] " + newProgress);
        if (h5Page != null) {
            JSONObject param = new JSONObject();
            param.put(H5Container.KEY_PROGRESS, newProgress);
            h5Page.sendIntent(H5Plugin.H5_PAGE_PROGRESS, param);
        }
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        super.onReceivedTitle(view, title);
        H5Log.d(TAG, "onReceivedTitle [title] " + title);
        if (h5Page != null) {
            // save console
            view.loadUrl("javascript:{window.__alipayConsole__ = window.console}");
            // send intent
            JSONObject param = new JSONObject();
            param.put(H5Container.KEY_TITLE, title);
            h5Page.sendIntent(H5Plugin.H5_PAGE_RECEIVED_TITLE, param);
        }
    }

    @Override
    public void onReceivedIcon(WebView view, Bitmap icon) {
        H5Log.d(TAG, "onReceivedIcon");
        super.onReceivedIcon(view, icon);
    }

    @Override
    public void onReceivedTouchIconUrl(WebView view, String url,
            boolean precomposed) {
        H5Log.d(TAG, "onReceivedTouchIconUrl. [url] " + url + " [precomposed] "
                + precomposed);
        super.onReceivedTouchIconUrl(view, url, precomposed);
    }

    @Override
    public boolean onJsBeforeUnload(WebView view, String url, String message,
            JsResult result) {
        H5Log.d(TAG, "onJsBeforeUnload [url] " + url + " [message] " + message);
        return super.onJsBeforeUnload(view, url, message, result);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onJsTimeout() {
        H5Log.d(TAG, "onJsTimeout");
        return super.onJsTimeout();
    }

    @Override
    public View getVideoLoadingProgressView() {
        H5Log.d(TAG, "getVideoLoadingProgressView");
        return super.getVideoLoadingProgressView();
    }

    @Override
    public void onShowCustomView(View view, CustomViewCallback callback) {
        H5Log.d(TAG, "onShowCustomView [SDK Version] " + Build.VERSION.SDK_INT);

        if (Build.VERSION.SDK_INT >= 12) {
            super.onShowCustomView(view, callback);
        }
    }

    @Override
    public void onHideCustomView() {
        H5Log.d(TAG, "onShowCustomView [SDK Version] " + Build.VERSION.SDK_INT);

        if (Build.VERSION.SDK_INT >= 12) {
            super.onHideCustomView();
        }
    }

    @Override
    public void onGeolocationPermissionsShowPrompt(String origin,
                                                   GeolocationPermissions.Callback callback) {
        callback.invoke(origin, true, false);
    }

    public void onRelease() {
        h5Page = null;
    }
}
