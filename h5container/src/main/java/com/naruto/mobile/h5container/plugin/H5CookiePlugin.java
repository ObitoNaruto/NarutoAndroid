
package com.naruto.mobile.h5container.plugin;

import android.text.TextUtils;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import java.util.regex.Pattern;

import com.alibaba.fastjson.JSONObject;
import com.naruto.mobile.h5container.api.H5Intent;
import com.naruto.mobile.h5container.api.H5IntentFilter;
import com.naruto.mobile.h5container.api.H5Plugin;
import com.naruto.mobile.h5container.env.H5Environment;
import com.naruto.mobile.h5container.util.H5CookieHelper;

public class H5CookiePlugin implements H5Plugin {

    public static final String TAG = "H5CookiePlugin";

    @Override
    public void getFilter(H5IntentFilter filter) {
        filter.addAction(CLEAR_ALL_COOKIES);
        filter.addAction(GET_MTOP_TOKEN);
    }

    @Override
    public void onRelease() {

    }

    @Override
    public boolean interceptIntent(H5Intent intent) {
        return false;
    }

    @Override
    public boolean handleIntent(H5Intent intent) {
        String action = intent.getAction();
        if (CLEAR_ALL_COOKIES.equals(action)) {
            clearAllCookie(intent);
        } else if (GET_MTOP_TOKEN.equals(action)) {
            getMtopToken(intent);
        }

        return true;
    }

    private void clearAllCookie(H5Intent intent) {
        CookieSyncManager.createInstance(H5Environment.getContext());
        CookieSyncManager.getInstance().startSync();
        CookieManager.getInstance().removeSessionCookie();
        intent.sendBack(null);
    }

    private void getMtopToken(H5Intent intent) {
        final JSONObject param = intent.getParam();
        String url = ".taobao.com";
        if (param != null && param.containsKey("domain")
                && !TextUtils.isEmpty(param.getString("domain"))) {
            url = param.getString("domain");
        }
        String cookie = H5CookieHelper.getCookie(url);
        String token = "";
        do {
            if (TextUtils.isEmpty(cookie)) {
                break;
            }
            String[] pairs = Pattern.compile("; ").split(cookie);
            for (String pair : pairs) {
                if (pair != null && pair.startsWith("_m_h5_tk=")) {
                    token = pair.replace("_m_h5_tk=", "");
                    break;
                }
            }
        } while (false);

        JSONObject result = new JSONObject();
        result.put("token", token);
        intent.sendBack(result);
    }

}
