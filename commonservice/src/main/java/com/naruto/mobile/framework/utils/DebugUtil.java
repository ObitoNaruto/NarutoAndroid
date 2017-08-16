package com.naruto.mobile.framework.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.naruto.mobile.base.serviceaop.NarutoApplication;

/**
 */
public class DebugUtil {

    public static boolean isDebug() {
        boolean isDebug;
        try {
            Context context = NarutoApplication.getInstance().getApplicationContext();
            ApplicationInfo applicationInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_CONFIGURATIONS);
            if (applicationInfo != null
                    && (applicationInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
                isDebug = true;
            } else {
                isDebug = false;
            }
        } catch (Exception e) {
            isDebug = false;
        }
        return isDebug;
    }

}