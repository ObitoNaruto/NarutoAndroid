
package com.naruto.mobile.h5container.web;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebSettings.TextSize;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.Map;

import com.naruto.mobile.h5container.env.H5Container;
import com.naruto.mobile.h5container.refresh.H5PullableView;
import com.naruto.mobile.h5container.refresh.OverScrollListener;
import com.naruto.mobile.h5container.util.FileUtil;
import com.naruto.mobile.h5container.util.H5Log;
import com.naruto.mobile.h5container.util.H5Utils;
import com.naruto.mobile.h5container.util.NetworkUtil;
import com.naruto.mobile.h5container.util.NetworkUtil.NetworkType;

@TargetApi(VERSION_CODES.HONEYCOMB)
@SuppressWarnings("deprecation")
public class H5WebView extends WebView implements H5PullableView {

    public static final String TAG = "H5WebView";

    private static int WEBVIEW_INDEX = 0;

    public enum FlingDirection {
        FLING_LEFT, FLING_UP, FLING_RIGHT, FLING_DOWN,
    };

    private OverScrollListener overScrollListener;
    private int webViewIndex = 0;
    private boolean released = false;

    public H5WebView(Context context) {
        super(context);
        webViewIndex = WEBVIEW_INDEX++;
    }

    public H5WebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        webViewIndex = WEBVIEW_INDEX++;
    }

    public H5WebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        webViewIndex = WEBVIEW_INDEX++;
    }

    public int getWebViewIndex() {
        return webViewIndex;
    }

    @TargetApi(VERSION_CODES.HONEYCOMB)
    public H5WebView(Context context, AttributeSet attrs, int defStyle,
            boolean privateBrowsing) {
        super(context, attrs, defStyle, privateBrowsing);
    }

    public void init(boolean enableFile) {
        initSettings(enableFile);
        setUserAgent();
    }

    @TargetApi(VERSION_CODES.KITKAT)
    @SuppressLint("SetJavaScriptEnabled")
    private void initSettings(boolean enableFile) {
        H5Log.d(TAG, "initSettings enableFile " + enableFile);

        WebSettings settings = getSettings();

        // set default text encoding
        settings.setDefaultTextEncodingName("utf-8");

        settings.setSupportMultipleWindows(false);

        // JavaScript settings
        try {
            settings.setJavaScriptEnabled(true);
        } catch (NullPointerException e) {
            H5Log.d(TAG, "Ignore the exception in AccessibilityInjector when init");
            e.printStackTrace();
        }
        settings.setDefaultFontSize(16);
        settings.setJavaScriptCanOpenWindowsAutomatically(false);

        // avoid webview to save user password
        settings.setSavePassword(false);

        // enable plugin state
        settings.setPluginState(PluginState.ON);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(enableFile);

        String h5Folder = H5Utils.getApplicaitonDir() + "/app_h5container";
        FileUtil.mkdirs(h5Folder);

        // database
        settings.setDatabaseEnabled(true);
        if (Build.VERSION.SDK_INT < VERSION_CODES.KITKAT) {
            settings.setDatabasePath(h5Folder + "/databases");
        }

        // cache related
        settings.setAppCacheEnabled(true);
        settings.setAppCachePath(h5Folder + "/appcache");

        // web cache
        NetworkUtil nu = new NetworkUtil(getContext());
        if (nu.getNetworkType() == NetworkType.NONE) {
            settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        } else {
            settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        }

        // zoom controls
        settings.setBuiltInZoomControls(true);
        settings.setSupportZoom(true);

        // enable overview
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);

        if (Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) {
            settings.setDisplayZoomControls(false);
        }

        if (Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
            settings.setAllowFileAccessFromFileURLs(enableFile);
            settings.setAllowUniversalAccessFromFileURLs(enableFile);
        }

        // enable debug code
        if (Build.VERSION.SDK_INT >= VERSION_CODES.KITKAT
                && H5Utils.isDebugable()) {
            setWebContentsDebuggingEnabled(true);
        }

        // for security protect
        if (Build.VERSION.SDK_INT > VERSION_CODES.GINGERBREAD_MR1
                && Build.VERSION.SDK_INT < VERSION_CODES.JELLY_BEAN_MR1) {
            removeJavascriptInterface("searchBoxJavaBridge_");
            removeJavascriptInterface("accessibility");
            removeJavascriptInterface("accessibilityTraversal");
        }
    }

    @TargetApi(VERSION_CODES.ICE_CREAM_SANDWICH)
    public void setTextSize(int size) {
        WebSettings settings = getSettings();
        if (Build.VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH) {
            settings.setTextZoom(size);
        } else {
            TextSize textSize = getTextSize(size);
            settings.setTextSize(textSize);
        }
    }

    public TextSize getTextSize(int textZoom) {
        if (textZoom >= H5Container.WEBVIEW_FONT_SIZE_LARGEST) {
            return TextSize.LARGEST;
        } else if (textZoom >= H5Container.WEBVIEW_FONT_SIZE_LARGER) {
            return TextSize.LARGER;
        } else if (textZoom >= H5Container.WEBVIEW_FONT_SIZE_NORMAL) {
            return TextSize.NORMAL;
        } else if (textZoom >= H5Container.WEBVIEW_FONT_SIZE_SMALLER) {
            return TextSize.SMALLER;
        }
        return TextSize.NORMAL;
    }

    @TargetApi(VERSION_CODES.KITKAT)
    @Override
    public void loadUrl(String url) {
        Log.d(TAG, "loadUrl " + url);
        if (TextUtils.isEmpty(url)) {
            return;
        }

        boolean doEval = Build.VERSION.SDK_INT >= VERSION_CODES.KITKAT;
        try {
            if (url.startsWith("javascript") && doEval) {
                this.evaluateJavascript(url, null);
            } else {
                super.loadUrl(url);
            }
        } catch (Exception e) {
            // load URL execute javascript exception.
            H5Log.e(TAG, "loadUrl exception", e);
        }
    }

    @Override
    public void loadUrl(String url, Map<String, String> additionalHttpHeaders) {
        super.loadUrl(url, additionalHttpHeaders);
    }

    @Override
    public void loadData(String data, String mimeType, String encoding) {
        super.loadData(data, mimeType, encoding);
    }

    @Override
    public void loadDataWithBaseURL(String baseUrl, String data,
            String mimeType, String encoding, String historyUrl) {
        super.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl);
    }

    private void setUserAgent() {
        try {
            WebSettings settings = getSettings();
            String ua = settings.getUserAgentString();
            String packageName = getContext().getPackageName();
            PackageInfo packageInfo = getContext().getPackageManager()
                    .getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
			/*ua = ua + "  AliApp(JN/" + packageInfo.versionName
					+ ") JuNiu/" + packageInfo.versionName;*/
            ua = ua + " AliApp(AP/8.4.0.121501) AlipayClient/8.4.0.121501  AliApp(JN/" + packageInfo.versionName
                    + ") JuNiu/" + packageInfo.versionName;

            settings.setUserAgentString(ua);
        } catch (NameNotFoundException e) {
            H5Log.e("setUserAgent exception", e);
        }
    }

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX,
            int scrollY, int scrollRangeX, int scrollRangeY,
            int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        if (overScrollListener != null) {
            overScrollListener.onOverScrolled(deltaX, deltaY, scrollX, scrollY);
        }

        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY,
                scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY,
                isTouchEvent);
    }

    @Override
    public void setOverScrollListener(OverScrollListener listener) {
        this.overScrollListener = listener;
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX,
            boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
    }

    @TargetApi(VERSION_CODES.HONEYCOMB)
    @Override
    public void onPause() {
        H5Log.d(TAG, "onPause " + webViewIndex);
        super.onPause();
    }

    @Override
    public void onResume() {
        H5Log.d(TAG, "onResume " + webViewIndex);
        super.onResume();
    }

    @Override
    public void setWebViewClient(WebViewClient client) {
        if (client != null) {
            String clientName = H5Utils.getClassName(client);
            H5Log.d(TAG, "setWebViewClient " + clientName);
        }
        super.setWebViewClient(client);
    }

    @Override
    public void setWebChromeClient(WebChromeClient client) {
        if (client != null) {
            String clientName = H5Utils.getClassName(client);
            H5Log.d(TAG, "setWebChromeClient " + clientName);
        }
        super.setWebChromeClient(client);
    }

    @Override
    public void removeJavascriptInterface(String name) {
        H5Log.d(TAG, "removeJavascriptInterface " + name);
        if (Build.VERSION.SDK_INT >= 11) {
            super.removeJavascriptInterface(name);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        H5Log.d(TAG, "onAttachedToWindow");
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        H5Log.d(TAG, "onDetachedFromWindow");
        super.onDetachedFromWindow();
        onRelease();
    }

    public void onRelease() {
        if (released) {
            return;
        }
        released = true;
        H5Log.d(TAG, "exit webview!");

        // flush current web content
        this.loadUrl("about:blank");
        this.reload();

        H5Utils.runOnMain(new Runnable() {

            @Override
            public void run() {
                destroyWebView();
            }
        }, 1000);
    }

    private void destroyWebView() {
        try {
            // remove from parent to stop drawing it
            ViewParent parent = this.getParent();
            if (parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeView(this);
            }

            this.setVisibility(View.GONE);
            this.clearFocus();
            this.clearAnimation();
            this.setDownloadListener(null);
            this.setWebViewClient(null);
            this.setWebChromeClient(null);
            this.stopLoading();
            this.removeAllViews();
            this.removeAllViewsInLayout();
            this.clearHistory();
            this.clearSslPreferences();
            this.destroyDrawingCache();
            this.freeMemory();

            // MUST removed from parent, or native crash
            this.destroy();
        } catch (Exception e) {
            // on some devices, destroy webview may crash
            H5Log.e(TAG, "destroy webview exception.", e);
        }
    }

}
