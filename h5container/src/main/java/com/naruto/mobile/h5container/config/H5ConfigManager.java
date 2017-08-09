
package com.naruto.mobile.h5container.config;

import android.os.Bundle;

import com.naruto.mobile.h5container.env.H5Environment;

public class H5ConfigManager {
    public static final String TAG = "H5ConfigManager";

    private static Bundle globleConfig;
    private static boolean LOCAL_PROXY_GLOBAL_ENABLED = true;

    public static boolean proxyGlobalEnabled() {
        return LOCAL_PROXY_GLOBAL_ENABLED;
    }

    public static Bundle getGlobleConfig() {
        synchronized (H5Environment.class) {
            if (globleConfig == null) {
                globleConfig = new Bundle();
            }
        }
        return globleConfig;
    }
}
