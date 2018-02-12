package com.naruto.mobile.applauncher.launcher.factory;


import android.app.Activity;

import com.naruto.mobile.applauncher.core.IWidgetGroup;
import com.naruto.mobile.applauncher.core.IWidgetGroupFactory;
import com.naruto.mobile.applauncher.launcher.beans.WidgetGroup;

import java.util.List;

public class WidgetGroupFactory implements IWidgetGroupFactory {

    private Activity mActivity;
    private List<ClassLoader> mClassloaders;
    private List<IWidgetGroup> mWidgetGroups;

    public WidgetGroupFactory(Activity activity, List<ClassLoader> classloaders, List<IWidgetGroup> widgetGroups) {
        this.mActivity = activity;
        this.mClassloaders = classloaders;
        this.mWidgetGroups = widgetGroups;
        loadWidgetDefinitions();
    }


    @Override
    public IWidgetGroup getWidgetGroup(String id) {
        return null;
    }

    @Override
    public List<IWidgetGroup> getAllWidgetGroups() {
        return mWidgetGroups;
    }

    @Override
    public List<ClassLoader> getClassloaders() {
        return mClassloaders;
    }

    private void loadWidgetDefinitions() {
        WidgetGroupDao widgetGroupDao = new WidgetGroupDao();
        List<WidgetGroup> widgetGroupList = widgetGroupDao.getWidgetGroups();

        //获取WidgetGroup列表中所有bundle的classloader，并添加到mClassloaders中去

        //通过反射的方式初始化IWidgetGroup对象，添加到mWidgetGroups中去


    }
}
