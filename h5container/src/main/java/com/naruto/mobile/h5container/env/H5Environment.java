
package com.naruto.mobile.h5container.env;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;

import com.naruto.mobile.h5container.api.H5Context;
import com.naruto.mobile.h5container.api.H5Param;
import com.naruto.mobile.h5container.util.H5Log;
import com.naruto.mobile.h5container.util.H5Utils;

public class H5Environment {
    public static final String TAG = "H5Environment";

    private static Context appContext;
    private static Resources resources;

    public static void setContext(Context ctx) {
        if (appContext == null) {
            appContext = ctx.getApplicationContext();
        }

        if (resources == null && appContext != null) {
            resources = appContext.getResources();
        }
    }

    public static Context getContext() {
        return appContext;
    }

    public static Resources getResources() {
        return resources;
    }

    public static String getConfig(String configName) {
        return null;
    }

    public static String getSessionId(H5Context h5Context, Bundle bundle) {
        if (bundle == null) {
            return null;
        }

        String sessionId = H5Utils.getString(bundle, H5Param.SESSION_ID);
        if (!TextUtils.isEmpty(sessionId)) {
            return sessionId;
        }

        sessionId = "h5session_default";
        bundle.putString(H5Param.SESSION_ID, sessionId);
        return sessionId;
    }

    public static void startActivity(H5Context h5Context, Intent intent) {
    	if (h5Context == null) h5Context = new H5Context(appContext);
        if (h5Context == null || intent == null) {
            H5Log.w(TAG, "invalid start activity parameters!");
            return;
        }

        Context context = h5Context.getContext();
        if (!(context instanceof Activity)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        try {
            context.startActivity(intent);
        } catch (Exception e) {
            H5Log.e(TAG, "startActivity exception", e);
        }
    }

    public static void startActivityForResult(H5Context h5Context,
            Intent intent, int requestCode) {
    	if (h5Context == null) h5Context = new H5Context(appContext);
        if (h5Context == null || intent == null) {
            H5Log.w(TAG, "invalid startActivityForResult parameters!");
            return;
        }

        Context context = h5Context.getContext();
        if (!(context instanceof Activity)) {
            return;
        }

        Activity activity = (Activity) context;
        try {
            activity.startActivityForResult(intent, requestCode);
        } catch (Exception e) {
            H5Log.e(TAG, "startActivityForResult exception", e);
        }
    }
}
