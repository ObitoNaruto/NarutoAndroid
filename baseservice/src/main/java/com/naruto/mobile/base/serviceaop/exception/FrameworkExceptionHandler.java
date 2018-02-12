package com.naruto.mobile.base.serviceaop.exception;


import android.content.Context;

public class FrameworkExceptionHandler implements Thread.UncaughtExceptionHandler{

    private static FrameworkExceptionHandler mInstance;

    public static FrameworkExceptionHandler getInstance() {
        if(mInstance == null) {
            mInstance = new FrameworkExceptionHandler();
        }
        return mInstance;
    }

    public void init(Context context) {

    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {

    }
}
