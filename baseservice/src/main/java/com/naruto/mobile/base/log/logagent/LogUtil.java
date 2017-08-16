package com.naruto.mobile.base.log.logagent;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

/**
 * 
 * @author bean.yangb
 * @version $Id: LogUtil.java, v 0.1 2013-4-20 下午2:35:53 bean.yangb Exp $
 */
public class LogUtil {
    private static final boolean isLog = false;

    //    public static void init(Context context) {
    //    }

    /**
     * output the log only in debuggable mode
     * @param tag Tag for the log
     * @param info log info
     */
    public static void logOnlyDebuggable(String tag, String info) {
        if (isLog) {
            Log.d("Alipay_" + tag, info);
        }
    }

    /**
     * output the log only in debuggable mode
     * @param tag Tag for the log
     * @param info log info
     */
    public static void logContainerDebuggable(String tag, String info) {
        if (isLog) {
            Log.e("Alipay_" + tag, info);
        }
    }

    /**
     * output the log in any mode (debug and normal)
     * @param tag Tag for the log
     * @param info log info
     */
    public static void logAnyTime(String tag, String info) {
        Log.v("Alipay_" + tag, info);
    }

    /**
     * log the error
     * @param tag
     * @param info
     */
    public static void logAnyTime(String tag, String info, Exception ex) {
        Log.e("Alipay_" + tag, info, ex);
    }

    /**
     * log the info according to log level
     * @param level log level (i,v,d,w,e)
     * @param tag 
     * @param info
     */
    public static void logMsg(int level, String tag, String info) {
        if (Constants.LOG_LEVEL >= level) {
            switch (level) {
                case 1:
                    logAnyTime(tag, info);
                    break;
                case 2:
                    logAnyTime(tag, info);
                    break;
                case 3:
                    logOnlyDebuggable(tag, info);
                    break;
                case 4:
                    logOnlyDebuggable(tag, info);
                    break;
                case 5:
                    logOnlyDebuggable(tag, info);
                    break;
            }
        }
    }

    /**
     * 是否是开发状态
     */
    private static boolean isDebug(Context context) {
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(
                context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
            if ((applicationInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
                // development mode
                return true;
            } else {
                //release mode
                return false;
            }
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * 从支付宝设置程序获取值
     */
    public static String getValue(Context context, String uri, String defaultVal) {
        Cursor cursor = context.getContentResolver().query(Uri.parse(uri), null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            String ret = cursor.getString(0);
            cursor.close();
            return ret;
        }
        return defaultVal;
    }
    
    /**
     * 获取统计埋点地址
     */
    public final static String getStatisticsUrl(Context context) {
        if (isDebug(context)) {
            return getValue(context, "content://com.alipay.setting/StatisticsServerUrl",
                "http://mdap.alipay.com/loggw/log.do");
        } else {
            return "http://mdap.alipay.com/loggw/log.do";
        }
    }
}
