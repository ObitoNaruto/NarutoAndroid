
package com.naruto.mobile.h5container.util;

import android.text.TextUtils;
import android.util.Log;

public class H5Log {
    public static final String TAG = "H5Log";

    private static LogListener logListener;

    public interface LogListener {
        public void onLog(String tag, String log);
    }

    public static void setListener(LogListener listener) {
        synchronized (LogListener.class) {
            logListener = listener;
        }
    }

    public static void d(String log) {
        d(TAG, log);
    }

    public static void d(String tag, String log) {
        if (TextUtils.isEmpty(log)) {
            return;
        }

        sendLog(tag, log);
        Log.d(tag, log);
    }

    public static void w(String log) {
        w(TAG, log);
    }

    public static void w(String tag, String log) {
        if (TextUtils.isEmpty(log)) {
            return;
        }

        sendLog(tag, log);
        Log.w(tag, log);
    }

    public static void e(String log) {
        e(TAG, log, null);
    }

    public static void e(String tag, String log) {
        e(tag, log, null);
    }

    public static void e(String log, Exception e) {
        e(TAG, log, e);
    }

    public static void e(String tag, String log, Exception e) {
        sendLog(tag, log);
        Log.e(tag, log, e);
    }

    private static void sendLog(String tag, String log) {
        if (!H5Utils.isDebugable()) {
            return;
        }

        synchronized (LogListener.class) {
            if (logListener != null) {
                logListener.onLog(tag, log);
            }
        }
    }
}
