package com.naruto.mobile.applauncher.launcher.factory;


import com.naruto.mobile.applauncher.launcher.beans.WidgetGroup;

import java.util.ArrayList;
import java.util.List;

public class WidgetGroupDao {

    /**
     * 返回当前app的tab列表
     * @return
     */
    public List<WidgetGroup> getWidgetGroups () {
        return new ArrayList<WidgetGroup>(){{
            add(new WidgetGroup("20000001", "android-bundle-tab1-test1", "com.android.naruto.home.Tab1WidgetGroup", "true"));
            add(new WidgetGroup("20000002", "android-bundle-tab1-test2", "com.android.naruto.home.Tab2WidgetGroup", "false"));
            add(new WidgetGroup("20000003", "android-bundle-tab1-test3", "com.android.naruto.home.Tab3WidgetGroup", "false"));
            add(new WidgetGroup("20000004", "android-bundle-tab1-test4", "com.android.naruto.home.Tab4WidgetGroup", "false"));
        }};
    }
}
