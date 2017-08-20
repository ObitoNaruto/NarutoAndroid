package com.naruto.mobile.applauncher;

import android.util.Log;

import com.naruto.mobile.base.framework.info.AppInfo;
import com.naruto.mobile.base.serviceaop.NarutoApplication;

/**
 */

public class LauncherApplication extends NarutoApplication {

    private static final String TAG = LauncherApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        initAppInfo();
        super.onCreate();
    }

    private void initAppInfo() {
        Log.i(TAG, "Initializing AppInfo...");
        AppInfo.createInstance(getApplicationContext());
//        AppInfo.getInstance().setReleaseType("");
//        AppInfo.getInstance().setProductVersion(getVersionName());
//        AppInfo.getInstance().setProductID(RPCEnvironment.instance().getProductId(this.mContext));
//        AppInfo.getInstance().setChannels(AppUtil.getChannel(getApplicationContext()));
//        LogUtils.i(TAG, "Product version = " + AppInfo.getInstance().getmProductVersion());
//        LogUtils.i(TAG, "Product ID = " + AppInfo.getInstance().getProductID());
//        LogUtils.i(TAG, "Product channel = " + AppInfo.getInstance().getmChannels());
//        LogUtils.i(TAG, "Initializing AppInfo...done");
    }
}
