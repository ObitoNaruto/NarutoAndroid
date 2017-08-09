
package com.naruto.mobile.h5container.plugin;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.naruto.mobile.h5container.api.H5CoreNode;
import com.naruto.mobile.h5container.api.H5Intent;
import com.naruto.mobile.h5container.api.H5IntentFilter;
import com.naruto.mobile.h5container.api.H5Page;
import com.naruto.mobile.h5container.api.H5Plugin;
import com.naruto.mobile.h5container.api.H5PluginManager;
import com.naruto.mobile.h5container.api.H5Session;
import com.naruto.mobile.h5container.env.H5Environment;
import com.naruto.mobile.h5container.util.H5UrlHelper;
import com.naruto.mobile.h5container.util.H5Utils;

public class H5SystemPlugin implements H5Plugin {

    public static final String TAG = "H5SystemPlugin";

    @Override
    public void getFilter(H5IntentFilter filter) {
        filter.addAction(OPEN_IN_BROWSER);
        filter.addAction(SEND_SMS);
        filter.addAction(IS_INSTALLED_APP);
        filter.addAction(CHECK_JS_API);
        filter.addAction(START_PACKAGE);
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
        if (SEND_SMS.equals(action)) {
            sendSms(intent);
        } else if (IS_INSTALLED_APP.equals(action)) {
            installedApp(intent);
        } else if (CHECK_JS_API.equals(action)) {
            checkJsAPI(intent);
        } else if (OPEN_IN_BROWSER.equals(action)) {
            openInBrowser(intent);
        } else if (START_PACKAGE.equals(action)) {
            startPackage(intent);
        }
        return true;
    }

    private void openInBrowser(H5Intent intent) {
        JSONObject param = intent.getParam();
        String url = H5Utils.getString(param, "url");
        Uri uri = H5UrlHelper.parseUrl(url);
        if (uri == null) {
            JSONObject result = new JSONObject();
            result.put("error", 2);
            intent.sendBack(result);
            return;
        }

        Intent openIntent = new Intent(Intent.ACTION_VIEW, uri);
        openIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        H5Environment.startActivity(null, openIntent);
    }

    private void installedApp(H5Intent intent) {
        JSONObject param = intent.getParam();
        if (param == null || !param.containsKey("packagename")) {
            return;
        }
        String packageName = param.getString("packagename");
        Context context = H5Environment.getContext();
        PackageInfo packageInfo = H5Utils.getPackageInfo(context, packageName);
        JSONObject data = new JSONObject();
        data.put("installed", (packageInfo != null));
        intent.sendBack(data);
    }

    private void sendSms(H5Intent intent) {
        JSONObject parseObject = intent.getParam();
        String contact = parseObject.getString("mobile");
        String message = parseObject.getString("content");
        Uri smsToUri = Uri.parse("smsto:" + contact);

        Intent smsIntent = new Intent(Intent.ACTION_SENDTO, smsToUri);
        smsIntent.putExtra("sms_body", message);
        smsIntent.putExtra(Intent.EXTRA_TEXT, message);
        smsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        H5Environment.startActivity(null, smsIntent);
    }

    private void checkJsAPI(H5Intent intent) {
        JSONObject param = intent.getParam();
        String apiName = H5Utils.getString(param, "api", null);
        boolean available = false;

        H5CoreNode target = intent.getTarget();
        while (!TextUtils.isEmpty(apiName) && !available && target != null) {
            H5PluginManager pluginManager = target.getPluginManager();
            target = target.getParent();
            available = pluginManager.canHandle(apiName);
        }

        JSONObject result = new JSONObject();
        result.put("available", available);
        intent.sendBack(result);
    }

    private void startPackage(H5Intent intent) {
        JSONObject data = new JSONObject();
        JSONObject param = intent.getParam();

        String packageName = H5Utils.getString(param, "packagename");
        Context context = H5Environment.getContext();
        PackageInfo packageInfo = H5Utils.getPackageInfo(context, packageName);
        if (packageInfo != null) {
            boolean closeCurrentApp = H5Utils.getBoolean(param,
                    "closeCurrentApp", false);
            if (closeCurrentApp) {
                H5CoreNode target = intent.getTarget();
                if (target instanceof H5Page) {
                    H5Page page = (H5Page) target;
                    H5Session session = page.getSession();
                    session.exitSession();
                }
            }
            Intent launchIntent = context.getPackageManager()
                    .getLaunchIntentForPackage(packageName);
            H5Environment.startActivity(null, launchIntent);

            data.put("startPackage", "true");
        } else {
            data.put("error", "");
        }
        intent.sendBack(data);
    }
}
