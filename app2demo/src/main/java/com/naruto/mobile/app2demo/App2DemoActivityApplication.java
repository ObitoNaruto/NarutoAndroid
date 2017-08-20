package com.naruto.mobile.app2demo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.naruto.mobile.base.serviceaop.NarutoApplication;
import com.naruto.mobile.base.serviceaop.NarutoApplicationContext;
import com.naruto.mobile.base.serviceaop.app.ActivityApplication;

/**
 */

public class App2DemoActivityApplication extends ActivityApplication {

    private static final java.lang.String KEY_TARGET = "target";
    private Bundle bundle;

    @Override
    public String getEntryClassName() {
        return MainActivity.class.getName();
    }

    @Override
    protected void onCreate(Bundle params) {
        this.bundle = bundle;
    }

    @Override
    protected void onStart() {
        //ActivityApplication中已经启动了
//        route();
    }

    @Override
    protected void onRestart(Bundle params) {

    }

    @Override
    protected void onStop() {

    }

    @Override
    protected void onDestroy(Bundle params) {

    }

    private void route(){
        String target = bundle == null ? "main" : bundle.getString(KEY_TARGET);
        if (TextUtils.isEmpty(target)) {
            target = "default";
        }

//        LoggerFactory.getTraceLogger().debug(TAG, "The target activity is: " + target);
        Class findClass = RouteMap.getTargetClass(target);
        if (findClass == null) {
//            LoggerFactory.getTraceLogger().error("O2OApp", "The target class is null");
            return;
        }

//        LoggerFactory.getTraceLogger().debug(TAG, "The target is: " + findClass);
        NarutoApplicationContext microContext = NarutoApplication.getInstance().getNarutoApplicationContext();
        Context context = microContext.getApplicationContext();

        Intent intent = new Intent();
        intent.setClass(context, findClass);
        if (bundle != null) {
            suiltParams(bundle);
            intent.putExtras(bundle);
        }
        microContext.startActivity(this, intent);
    }

    private void suiltParams(Bundle bundle){

    }
}
