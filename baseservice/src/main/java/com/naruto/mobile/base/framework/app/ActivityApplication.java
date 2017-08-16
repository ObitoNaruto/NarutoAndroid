package com.naruto.mobile.base.framework.app;

import java.lang.ref.WeakReference;
import java.util.Stack;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;

import com.naruto.mobile.base.log.logging.LogCatLog;


/**
 * Activity的App
 * 
 * @author sanping.li@alipay.com
 * 
 */
public abstract class ActivityApplication extends MicroApplication {
	final static String TAG =  ActivityApplication.class.getSimpleName();
	
    /**
     * 该App的Activity栈
     */
    private Stack<WeakReference<Activity>> mActivitys;

    private boolean mIsPrevent;

    public ActivityApplication() {
        mActivitys = new Stack<WeakReference<Activity>>();
        mIsPrevent = false;
    }

    public final void create(Bundle params) {
        LogCatLog.d(TAG, "microapplication: " + getAppId() + "  create.");
        onCreate(params);
    }

    public final void start() throws AppLoadException {
        // 如果子类主动返回null，我们把启动入口Activity的工作交给子类自己处理，不抛出异常。
        String className = getEntryClassName();
        if (className != null) {
            try {
//                getMicroApplicationContext().startActivity(this, className);
            } catch (ActivityNotFoundException e) {
                throw new AppLoadException(e);
            }
        }

        LogCatLog.d(TAG, "microapplication: " + getAppId() + "  start.");
        onStart();
    }

    public final void restart(Bundle params) {
        LogCatLog.d(TAG, "microapplication: " + getAppId() + "  restart.");
        onRestart(params);
    }

    public final void stop() {
        LogCatLog.d(TAG, "microapplication: " + getAppId() + "  stop.");
        onStop();
    }

    public final void destroy(Bundle params) {
        LogCatLog.d(TAG, "microapplication: " + getAppId() + "  destroy.");

        WeakReference<Activity> reference;
        Activity activity;
        while (!mActivitys.isEmpty() && (reference = mActivitys.pop()) != null) {
            activity = reference.get();
            if (activity != null && !activity.isFinishing()) {
                activity.finish();
            }
        }
        getMicroApplicationContext().onDestroyContent(this);
        super.destroy(params);
    }

    /**
     * Activity入栈
     * 
     * @param activity
     */
    public final void pushActivity(Activity activity) {
        if(!mActivitys.isEmpty()&&mActivitys.peek().get()==null){//被恢复的时候替换
            mActivitys.pop();
        }
        WeakReference<Activity> item = new WeakReference<Activity>(activity);
        mActivitys.push(item);
        LogCatLog.v(TAG, "pushActivity(): " + activity.getComponentName().getClassName());
    }

    /**
     * 移除Activity
     * 
     * @param Activity
     *            Activity
     */
    public void removeActivity(Activity activity) {
        WeakReference<Activity> dirtyItem = null;
        for (WeakReference<Activity> item : mActivitys) {
            if (item.get() == null) {
                LogCatLog.w(TAG, "activity has be finallized.");
                continue;
            }
            if (item.get() == activity) {
                dirtyItem = item;
                break;
            }
        }
        mActivitys.remove(dirtyItem);
        LogCatLog.d(TAG, "remove Activity:" + activity.getClass().getName());
        if (mActivitys.isEmpty() && !mIsPrevent) {
            destroy(null);
        }
    }

    /**
     * 通过Hashcode查找Activity
     * 
     * @param code
     *            Hashcode
     * @return
     */
    public Activity findActivityByHashcode(int code) {
        for (WeakReference<Activity> reference : mActivitys) {
            Activity activity = reference.get();
            if (activity == null)
                continue;
            if (activity.hashCode() == code) {
                return activity;
            }
        }
        return null;
    }

    /**
     * 获得焦点
     */
    public void windowFocus() {
//        getMicroApplicationContext().onWindowFocus(this);
    }

    /**
     * 获取栈顶Activity
     */
    public Activity getTopActivity() {
        if (mActivitys.isEmpty())
            return null;
        return mActivitys.peek().get();
    }

    @Override
    public void setIsPrevent(boolean isPrevent) {
        mIsPrevent = isPrevent;
    }

    @Override
    public void saveState(Editor editor) {
        editor.putInt(getAppId() + ".stack", mActivitys.size());
    }

    @Override
    public void restoreState(SharedPreferences preferences) {
        int num = preferences.getInt(getAppId() + ".stack", 0);
        for (int i = 0; i < num; i++) {
            mActivitys.push(new WeakReference<Activity>(null));
        }
    }
}
