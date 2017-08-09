
package com.naruto.mobile.h5container.util;

import android.content.Context;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.naruto.mobile.h5container.env.H5Environment;

public class H5CookieHelper {
    public static final String TAG = "H5CookieHelper";

    public static final String getCookie(String url) {
        CookieManager cookieManager = CookieManager.getInstance();
        return cookieManager.getCookie(url);
    }

    public void setCookie(String url, String value) {
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setCookie(url, value);
        Context context = H5Environment.getContext();
        CookieSyncManager.createInstance(context);
        CookieSyncManager.getInstance().sync();
    }

    public static final void clearThirdPartySessionCookies() {
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
        Context context = H5Environment.getContext();
        CookieSyncManager.createInstance(context);
        CookieSyncManager.getInstance().sync();
    }
}
