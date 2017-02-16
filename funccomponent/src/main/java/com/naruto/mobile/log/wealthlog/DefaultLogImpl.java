package com.naruto.mobile.log.wealthlog;

import android.util.Log;

public class DefaultLogImpl implements BaseLog {

    // need to run "adb shell setprop log.tag.TSMClient DEBUG"
    public final static String TAG = "projectName";

    @Override
    public int v(String msg) {
        return Log.v(TAG, msg);
    }

    @Override
    public int v(String tag, String msg) {
        return Log.v(TAG + "." + tag, msg);
    }

    @Override
    public int v(String tag, String msg, Throwable tr) {
        return Log.v(TAG + "." + tag, msg, tr);
    }



    @Override
    public int d(String msg) {
        return Log.d(TAG, msg);
    }

    @Override
    public int d(String tag, String msg) {
        return Log.d(TAG + "." + tag, msg);
    }

    @Override
    public int d(String tag, String msg, Throwable tr) {
        return Log.d(TAG + "." + tag, msg, tr);
    }


    @Override
    public int i(String msg) {
        return Log.i(TAG, msg);
    }

    @Override
    public int i(String tag, String msg) {
        return Log.i(TAG + "." + tag, msg);
    }

    @Override
    public int i(String tag, String msg, Throwable tr) {
        return Log.i(TAG + "." + tag, msg, tr);
    }


    @Override
    public int w(String msg) {
        return Log.w(TAG, msg);
    }

    @Override
    public int w(String tag, String msg) {
        return Log.w(TAG + "." + tag, msg);
    }

    @Override
    public int w(String tag, String msg, Throwable tr) {
        return Log.w(TAG + "." + tag, msg, tr);
    }


    @Override
    public int e(String msg) {
        return Log.e(TAG, msg);
    }

    @Override
    public int e(String tag, String msg) {
        return Log.e(TAG + "." + tag, msg);
    }

    @Override
    public int e(String tag, String msg, Throwable tr) {
        return Log.e(TAG + "." + tag, msg, tr);
    }
}
