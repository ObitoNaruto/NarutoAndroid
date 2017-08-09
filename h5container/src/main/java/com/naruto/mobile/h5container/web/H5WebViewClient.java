
package com.naruto.mobile.h5container.web;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.net.TrafficStats;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.WebBackForwardList;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.InputStream;

import com.alibaba.fastjson.JSONObject;
import com.naruto.mobile.h5container.api.H5Param;
import com.naruto.mobile.h5container.api.H5Plugin;
import com.naruto.mobile.h5container.api.H5WebProvider;
import com.naruto.mobile.h5container.core.H5PageImpl;
import com.naruto.mobile.h5container.env.H5Container;
import com.naruto.mobile.h5container.env.H5Environment;
import com.naruto.mobile.h5container.util.FileUtil;
import com.naruto.mobile.h5container.util.H5Log;
import com.naruto.mobile.h5container.util.H5UrlHelper;
import com.naruto.mobile.h5container.util.H5Utils;

public class H5WebViewClient extends WebViewClient {

    public static final String TAG = "H5WebViewClient";

    private H5PageImpl h5Page;
    private String checkingUrl;
    private String loadingUrl;
    private String pageUrl;
    private H5WebProvider provider;
    private long totalBytes;
    private int uid;
    private long startTime;
    private int lastPageIndex;
    private boolean pageUpdated;

    public H5WebViewClient(H5PageImpl page) {
        this.h5Page = page;
        this.uid = H5Utils.getUid(H5Environment.getContext());
        this.pageUpdated = false;
        this.lastPageIndex = -1;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setCheckingUrl(String url) {
        checkingUrl = url;
    }

    public void setWebProvider(H5WebProvider provider) {
        this.provider = provider;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Log.d(TAG, "shouldOverrideUrlLoading " + url);
        if (h5Page == null || TextUtils.isEmpty(url)) {
            return true;
        }

        checkingUrl = null;
        // reset checkingUrl
        JSONObject param = new JSONObject();
        param.put(H5Param.LONG_URL, url);
        h5Page.sendIntent(H5Plugin.H5_PAGE_SHOULD_LOAD_URL, param);

        // checkingUrl should be set by H5_PAGE_SHOULD_LOAD_URL
        if (url.equals(checkingUrl)) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onReceivedError(WebView view, int errorCode,
            String description, String failingUrl) {
        String info = "onReceivedError errorCode " + errorCode
                + " description " + description + " failingUrl " + failingUrl;
        H5Log.d(TAG, info);

        if (h5Page != null) {
            H5Log.w("h5_load_url_error url={" + failingUrl + "} error={"
                    + errorCode + "}");
            JSONObject param = new JSONObject();
            param.put("errorCode", errorCode);
            param.put(H5Param.LONG_URL, failingUrl);
            h5Page.sendIntent(H5Plugin.H5_PAGE_ERROR, param);
        }
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler,
            SslError error) {
        super.onReceivedSslError(view, handler, error);
        int errorCode = error.getPrimaryError();
        H5Log.d(TAG, "onReceivedSslError " + errorCode);

        if (h5Page != null) {
            H5Log.w("h5_load_url_error url={" + loadingUrl + "} error={"
                    + errorCode + "}");
            JSONObject param = new JSONObject();
            param.put("errorCode", errorCode);
            h5Page.sendIntent(H5Plugin.H5_PAGE_ERROR, param);
        }
    }

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        Log.d(TAG, "shouldInterceptRequest " + url);
        if (!TextUtils.isEmpty(url) && url.startsWith("file://")) {
            Bundle params = h5Page.getParams();
            String installPath = H5Utils.getString(params, H5Container.INSTALL_PATH);
            String filePath = H5UrlHelper.getPath(url);
            if (!FileUtil.childOf(filePath, installPath)) {
                return new WebResourceResponse(null, null, null);
            }
        } else if (provider != null) {
            InputStream is = provider.getWebResource(url);
            if (is != null) {
                String filePath = H5UrlHelper.getPath(url);
                String fileName = FileUtil.getFileName(filePath);
                String mimeType = FileUtil.getMimeType(fileName);
                WebResourceResponse wrr = new WebResourceResponse(mimeType, "UTF-8", is);
                return wrr;
            }
        }
        return null;
    }

    @Override
    public void onLoadResource(WebView view, String url) {
        super.onLoadResource(view, url);
        Log.d(TAG, "onLoadResource " + url);
        if (h5Page != null && h5Page.getUrl() != null && h5Page.getUrl().startsWith("file://")
                && !url.startsWith("file://")) {
            H5Log.d(TAG, "trigger taobao auto login when onLoadResource");
            JSONObject param = new JSONObject();
            param.put("url", url);
            h5Page.sendIntent(H5Container.H5_PAGE_LOAD_RESOURCE, param);
        }
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        Log.d(TAG, "onPageStarted " + url);

        pageUpdated = false;
        if (h5Page != null) {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
                // some special payment url processed here
                JSONObject param = new JSONObject();
                param.put(H5Param.LONG_URL, url);
                param.put("webview", view);
                // TODO: why here??
                h5Page.sendIntent("specialCashPay", param);
            }

            loadingUrl = url;
            H5Log.d("onPageStarted url={" + url + "} ");
            int webViewIndex = 0;
            if (view instanceof H5WebView) {
                webViewIndex = ((H5WebView) view).getWebViewIndex();
            }

            totalBytes = getTotalRxBytes();
            JSONObject param = new JSONObject();
            param.put(H5Param.LONG_URL, url);
            param.put("webViewIndex", webViewIndex);
            h5Page.sendIntent(H5Plugin.H5_PAGE_STARTED, param);
            H5Log.d("h5_page_start url={" + url + "}");
            startTime = System.currentTimeMillis();
        }
    }

    @Override
    public void doUpdateVisitedHistory(WebView view, String url,
            boolean isReload) {
        super.doUpdateVisitedHistory(view, url, isReload);
        Log.d(TAG, "doUpdateVisitedHistory " + url + " isReload " + isReload);
        pageUpdated = true;
        if (h5Page != null) {
            pageUrl = url;
            JSONObject param = new JSONObject();
            param.put(H5Param.LONG_URL, url);
            h5Page.sendIntent(H5Plugin.H5_PAGE_UPDATED, param);
        }
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        Log.d(TAG, "onPageFinished " + url);
        if (h5Page != null && view != null) {
            long pageSize = getTotalRxBytes() - totalBytes;
            JSONObject param = new JSONObject();
            param.put(H5Param.LONG_URL, url);
            String title = view.getTitle();
            Uri uri = H5UrlHelper.parseUrl(url);
            if (uri != null && !TextUtils.isEmpty(title)) {
                CharSequence host = uri.getHost();
                if (!TextUtils.isEmpty(host)
                        && title.contains(host)) {
                    title = null;
                }
            }
            param.put(H5Container.KEY_TITLE, title);
            param.put("pageSize", pageSize);
            WebBackForwardList list = view.copyBackForwardList();
            int historySize = list.getSize();
            int pageIndex = list.getCurrentIndex();
            if (pageIndex != lastPageIndex || !TextUtils.equals(view.getOriginalUrl(), url)) {
                pageUpdated = true;
                lastPageIndex = pageIndex;
            }

            param.put("pageIndex", pageIndex);
            param.put(H5Container.KEY_PAGE_UPDATED, pageUpdated);
            param.put("historySize", historySize);
            h5Page.sendIntent(H5Plugin.H5_PAGE_FINISHED, param);
            long delta = System.currentTimeMillis() - startTime;
            H5Log.d("h5_page_finish url={" + url + "} cost={" + delta + "}");
        }
    }

    private long getTotalRxBytes() {
        long begUidRx = 0;
        try {
            begUidRx = TrafficStats.getUidRxBytes(uid);
        } catch (Exception e) {
        }
        return begUidRx;
    }

    public void onRelease() {
        h5Page = null;
    }
}
