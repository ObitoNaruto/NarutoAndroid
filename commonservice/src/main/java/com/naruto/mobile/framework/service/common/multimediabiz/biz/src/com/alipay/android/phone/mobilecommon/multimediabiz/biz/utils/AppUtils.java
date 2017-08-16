package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import com.naruto.mobile.base.serviceaop.NarutoApplication;
import com.naruto.mobile.framework.service.common.multimedia.api.MultimediaFileService;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Random;

/**
 * 应用工具
 * Created by jinmin on 15/5/22.
 */
public class AppUtils {

    private static final String TAG = "AppUtils";

    //TODO: 下个版本更新这里要修改版本号!!!
    private static int mainVersion = 9;
    private static int minorVersion = 1;

    public static Context getApplicationContext() {
        return NarutoApplication.getInstance().getApplicationContext();
    }

//    public static MicroApplicationContext getMicroApplicationContext() {
//        return NarutoApplication.getInstance().getMicroApplicationContext();
//    }

    public static boolean isDebug(Context context) {
        try {
            ApplicationInfo e = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
            return (e.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception var2) {
            return false;
        }
    }

    public static Resources getResources() {
        Resources resources = null;
        Context context = getApplicationContext();
        if (context != null) {
            resources = context.getResources();
        }
        return resources;
    }

    public static MultimediaFileService getFileService() {
        return NarutoApplication.getInstance().getNarutoApplicationContext().getExtServiceByInterface(MultimediaFileService.class.getName());
    }

    private static Random random = new Random();

    public static void randomSleep(int max) {
        int sleepTime = (int) Math.min(random.nextDouble() * 500, max);
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
        }
    }

    public static void randomSleep() {
        randomSleep(500);
    }

    public static void sleep(long sleepTime) {
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
        }
    }

    public static int getMainVersion(Context context) {
        if (mainVersion >= 0) {
            //Logger.I(TAG, "getMainVersion: " + mainVersion);
            return mainVersion;
        }
        int ver = 0;
        try {
            String version = getVersion(context);
            if (version.indexOf('.') > 0) {
                version = version.substring(0, version.indexOf('.'));
            }
            ver = Integer.parseInt(version);
        } catch (Exception e) {

        }
        mainVersion = ver;
        Logger.I(TAG, "getMainVersion2: " + mainVersion);
        return mainVersion;
    }

    public static int getMinorVersion(Context context) {
        if (minorVersion >= 0) {
            //Logger.I(TAG, "getMinorVersion: " + minorVersion);
            return minorVersion;
        }
        int ver = 0;
        try {
            String version = getVersion(context);
            if (version.indexOf('.') > 0) {
                version = version.substring(version.indexOf('.') + 1);
                if (version.indexOf('.') > 0) {
                    version = version.substring(0, version.indexOf('.'));
                }
                ver = Integer.parseInt(version);
            }
        } catch (Exception e) {

        }

        minorVersion = ver;
        Logger.I(TAG, "getMinorVersion2: " + minorVersion);
        return minorVersion;
    }

    public static String getVersion(Context context) {
        String version = null;
        try {
            String tpackageName = context.getPackageName();
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(tpackageName, 0);
            version = packageInfo.versionName;
        } catch (Exception e) {

        }
        //Logger.I(TAG, "getVersion: " + version);
        //version = "11.2.3";
        //version = AppInfo.getInstance().getmProductVersion();
        return version;
    }

    public static boolean isAppOnBackground() {
        // Returns a list of application processes that are running on the device
        Context context = getApplicationContext();
        ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        if (null == activityManager) {
            return false;
        }
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())
                    && appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }

    public static void causeGC() {
        System.gc();
        // 2.3以后GC是异步的,这里加入yield帮助gc回收掉无人引用的内存
        Thread.yield();
        System.gc();
        Logger.I(TAG, "causeGC free: " + Runtime.getRuntime().freeMemory());
    }


    public static int getHeapGrowthLimit() {
        int size = 96;
        try {
            Class systemProperties = ReflectUtils.getClass("android.os.SystemProperties");
            Method get = ReflectUtils.getMethod(systemProperties, "get", String.class, String.class);
            String vmHeapSize = ReflectUtils.invoke(systemProperties, get, "dalvik.vm.heapgrowthlimit", "96m");
            size = Integer.parseInt(vmHeapSize.substring(0, vmHeapSize.length()-1));
        } catch (Exception e) {
            //ignore
        }
        return size * 1024 * 1024 ;
    }

}
