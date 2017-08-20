package com.naruto.mobile.applauncher.core;

import android.app.Activity;
import android.view.View;

import com.naruto.mobile.base.serviceaop.NarutoApplicationContext;
import com.naruto.mobile.base.serviceaop.app.ActivityApplication;

/**
 * Interface definition for a widget to be invoked in a widgetGroup
 */
public interface IWidget {
    void setContext(NarutoApplicationContext narutoApplicationContext);

    void setContext(Activity activity);

    void setActivityApplication(ActivityApplication app);

    // 获得widget对应的view
    View getView();

    // 刷新事件的回调
    void onRefresh();
}
