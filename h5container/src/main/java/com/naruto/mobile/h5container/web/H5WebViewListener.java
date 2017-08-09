
package com.naruto.mobile.h5container.web;

public interface H5WebViewListener {

    public void onPageStarted(H5WebView view, String url);

    // called only for main frame
    public void onPageFinished(H5WebView view);

    public void onPageProgress(H5WebView view, int progress);
}
