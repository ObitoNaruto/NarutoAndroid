package com.naruto.mobile.applauncher;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.naruto.mobile.base.serviceaop.NarutoApplication;
import com.naruto.mobile.base.serviceaop.NarutoApplicationContext;
import com.naruto.mobile.base.serviceaop.app.ActivityApplication;

/**
 * Launcher启动的ActivityApplication
 */

public class LauncherActivityApplication extends ActivityApplication {
    private static final String TAG = "Launcher";
    private static final java.lang.String KEY_TARGET = "target";
    private Bundle bundle;


    @Override
    public String getEntryClassName() {
        return MainActivity.class.getName();//入口类名字
    }

    @Override
    protected void onCreate(Bundle params) {
        this.bundle = bundle;
    }

    @Override
    protected void onStart() {
        route();
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
        String target = bundle == null ? null : bundle.getString(KEY_TARGET);
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

    private void suiltParams(Bundle bundle) {
//        String target = bundle.getString(KEY_TARGET);
//        if ("searchExt".equals(target)||"search".equals(target) || "classify".equals(target) || "searchHome".equals(target)) {
//            String query = bundle.getString("query");
//            if (!TextUtils.isEmpty(query)) {
//                bundle.putString(Constants.EXTRA_QUERY, query);
//            }
//            String adCode = bundle.getString("cityCode");
//            if (!TextUtils.isEmpty(query)) {
//                bundle.putString(Constants.EXTRA_ADCADE, adCode);
//            }
//            String searchSrc = bundle.getString("searchSrc");
//            if (!TextUtils.isEmpty(searchSrc)) {
//                bundle.putString(Constants.EXTRA_SEARCH_SRC, searchSrc);
//            }
//            String showSwitch = bundle.getString("showSwitch");
//            if (!TextUtils.isEmpty(showSwitch)) {
//                bundle.putString(Constants.EXTRA_SEARCH_HAS_SWITCH, showSwitch);
//            } else if("search".equals(target)){
//                bundle.putString(Constants.EXTRA_SEARCH_HAS_SWITCH, "NO");
//            }
//        }
//
//        if ("merchantChoose".equals(target)){
//            bundle.putString(Constants.ISFROM_SHARE, "true");
//            String showSwitch = bundle.getString("showSwitch");
//            if (!TextUtils.isEmpty(showSwitch)) {
//                bundle.putString(Constants.EXTRA_SEARCH_HAS_SWITCH, showSwitch);
//            } else {
//                bundle.putString(Constants.EXTRA_SEARCH_HAS_SWITCH, "YES");
//            }
//        }
    }
}
