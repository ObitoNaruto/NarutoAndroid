package com.naruto.mobile.base.serviceaop.app.service.impl;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.naruto.mobile.base.log.logging.LogCatLog;
import com.naruto.mobile.base.serviceaop.NarutoApplicationContext;
import com.naruto.mobile.base.serviceaop.app.AppLoadException;
import com.naruto.mobile.base.serviceaop.app.ApplicationDescription;
import com.naruto.mobile.base.serviceaop.app.MicroApplication;
import com.naruto.mobile.base.serviceaop.app.service.ApplicationManager;
import com.naruto.mobile.base.serviceaop.utils.ReflectUtil;
//import com.alipay.android.core_Legacy.WebAppLoader;
//import com.alipay.android.core_new.webapp.WebRunTimeApp;
//import com.alipay.mobile.apk.AndroidAppLoader;
//import com.alipay.mobile.framework.service.legacy.LegacyService;

/**
 * 应用管理
 *
 */
public class ApplicationManagerImpl implements ApplicationManager {
    final static String TAG = ApplicationManager.class.getSimpleName();

    /**
     * 应用栈
     */
    private Stack<MicroApplication> mApps;
    /**
     * 应用映射
     */
    private Map<String, MicroApplication> mAppsMap;
    /**
     * 应用描述
     */
    private List<ApplicationDescription> mAppsDes;
    /**
     * 入口应用名
     */
    private String mEntryApp;

    /**
     * MicroApplication上下文
     */
    private NarutoApplicationContext mNarutoApplicationContext;

    public ApplicationManagerImpl() {
        mApps = new Stack<>();
        mAppsMap = new HashMap<>();
        mAppsDes = new ArrayList<>();
    }

    @Override
    public synchronized void startApp(String sourceAppId, String targetAppId, Bundle params) {
        if (targetAppId == null) {
            throw new RuntimeException("targetAppId should not be null");
        }
        LogCatLog.v(TAG, "startApp() sourceAppId: " + sourceAppId + " targetAppId: " + targetAppId + " currentThread: " + Thread.currentThread().getId());

        if (!mAppsMap.containsKey(sourceAppId)) {
            LogCatLog.w(TAG, sourceAppId + " is not a App or had not start up");
        }

        if (mAppsMap.containsKey(targetAppId)) {
            doRestart(targetAppId, params);
            return;
        }

        //优先以apk方式启动
        if (startApkApp(sourceAppId, targetAppId, params)) {
            return;
        }

        //以nativeapp方式启动
        if (startNativeApp(sourceAppId, targetAppId, params)) {
            return;
        }

        //以webapp方式启动
        if (startWebApp(sourceAppId, targetAppId, params)) {
            return;
        }

//        LegacyService legacyService = mNarutoApplicationContext.getExtServiceByInterface(LegacyService.class.getName());
//        legacyService.sendMessage(sourceAppId, targetAppId, "startapp", deSearialBundle(params));        
    }

    private boolean startApkApp(String sourceAppId, String targetAppId, Bundle params) {
        Context context = mNarutoApplicationContext.getApplicationContext();
        String mPath = context.getFilesDir().getAbsolutePath() + "/apps/";

        String archiveFilePath = mPath + targetAppId + ".apk";// data/data/pkg/DemoApp.apk
//		if(new File(archiveFilePath).exists()){
//			WeakReference<Activity> topAcRef = mNarutoApplicationContext.getTopActivity();
//			if(topAcRef != null && topAcRef.get() != null){
//				AndroidAppLoader nativeAppLoader = new AndroidAppLoader(topAcRef.get());
//				return nativeAppLoader.performLaunch(archiveFilePath, params);
//			}
//		}
        return false;
    }

    private boolean startNativeApp(String sourceAppId, String targetAppId, Bundle params) {
        ApplicationDescription targetAppDescription = findDescriptionById(targetAppId);
        //非老业务
        if (targetAppDescription != null && !targetAppDescription.isLagacyApp()) {
            MicroApplication app;
            try {
                app = createNativeApp(targetAppDescription, params);
                app.setSourceId(sourceAppId);
                LogCatLog.v(TAG, "createApp() completed: " + targetAppId);

                if (!mApps.isEmpty())
                    mApps.peek().stop();//取栈顶元素不出栈
                mApps.push(app);//入栈
                mAppsMap.put(targetAppId, app);

                app.start();
            } catch (AppLoadException e) {
                e.printStackTrace();
                return false;
            }

            return true;
        }

        return false;
    }

    private boolean startWebApp(String sourceAppId, String targetAppId, Bundle params) {
        try {
            MicroApplication app = createWebApp(sourceAppId, targetAppId, params);
            app.setSourceId(sourceAppId);
            LogCatLog.v(TAG, "createApp() completed: " + targetAppId);

            if (!mApps.isEmpty())
                mApps.peek().stop();
            mApps.push(app);//入栈
            mAppsMap.put(targetAppId, app);

            app.start();
        } catch (AppLoadException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * 反序列化Bundle
     *
     * @param bundle
     * @return
     */
    private String deSearialBundle(Bundle bundle) {
        if (bundle == null)
            return null;
        StringBuilder params = new StringBuilder();
        for (String key : bundle.keySet()) {
            params.append(key + "=" + bundle.get(key) + "&");
        }

        return params.length() > 0 ? params.substring(0, params.length() - 1) : null;
    }

    private void doRestart(String targetAppId, Bundle params) {
        LogCatLog.v(TAG, "doRestart() targetAppId: " + targetAppId);
        MicroApplication app = mAppsMap.get(targetAppId);
        MicroApplication tmp = null;
        while (app != (tmp = mApps.peek())) {
            mApps.pop();
            LogCatLog.v(TAG, "doRestart() pop appId: " + tmp.getAppId());
            tmp.destroy(params);
        }
        app.restart(params);
    }

    private MicroApplication createNativeApp(ApplicationDescription targetAppDes, Bundle params)
            throws AppLoadException {
        Object object;
        String targetAppClassName = targetAppDes.getClassName();
        try {
            object = ReflectUtil.getInstance(mNarutoApplicationContext.getApplicationContext().getClassLoader(), targetAppClassName);
        } catch (ClassNotFoundException e) {
            throw new AppLoadException("App ClassNotFoundException: " + e);
        } catch (IllegalAccessException e) {
            throw new AppLoadException("App IllegalAccessException: " + e);
        } catch (InstantiationException e) {
            throw new AppLoadException("App InstantiationException: " + e);
        }
        if (!(object instanceof MicroApplication)) {
            throw new AppLoadException("App " + targetAppClassName + " is not a App");
        }
        MicroApplication app = (MicroApplication) object;
        app.setAppId(targetAppDes.getAppId());
        app.attachContext(mNarutoApplicationContext);

        app.create(params);
        return app;
    }

    private MicroApplication createWebApp(String sourceAppId, String targetAppId, Bundle params) throws AppLoadException {

        Context context = mNarutoApplicationContext.getApplicationContext();
        File appPath = new File(context.getFilesDir(), "/apps/" + targetAppId);
        if (!appPath.exists())
            throw new AppLoadException("webapp is not exist");
//    	
//    	WebAppLoader loader = new WebAppLoader(context, sourceAppId, targetAppId, appPath.getAbsolutePath() + "/");    	
//    	WebRunTimeApp webRunTimeApp = new WebRunTimeApp();
//    	webRunTimeApp.setWebRunTime(loader.load(params));
//    	
//    	webRunTimeApp.setAppId(targetAppId);
//    	webRunTimeApp.attachContext(mNarutoApplicationContext);
//
//    	webRunTimeApp.create(params);
//        
//    	return webRunTimeApp;    	
        return null;
    }


    @Override
    public void finishApp(String sourceAppId, String targetId, Bundle params) {
        if (!mAppsMap.containsKey(sourceAppId)) {
            LogCatLog.w(TAG, sourceAppId + " is not a App");
        }

        MicroApplication app = mAppsMap.get(targetId);
        if (app != null) {
            app.destroy(params);
        } else {
            LogCatLog.d(TAG, "can't find App: " + targetId);
            throw new IllegalStateException("can't find App: " + targetId);
        }
    }

    /**
     * 通过插件名查找App
     *
     * @param appId App类名
     */
    @Override
    public MicroApplication findAppById(String appId) {
        return mAppsMap.get(appId);
    }

    /**
     * 通过应用名查找应用描述
     *
     * @param appName 应用名
     * @return 应用描述
     */
    @Override
    public ApplicationDescription findDescriptionByName(String appName) {
        //修复空值异常。
        if (mAppsDes != null && !TextUtils.isEmpty(appName)) {
            for (ApplicationDescription description : mAppsDes) {
                if (description != null) {
                    if (appName.equalsIgnoreCase(description.getName())) {
                        return description;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public ApplicationDescription findDescriptionById(String appId) {
        for (ApplicationDescription description : mAppsDes) {
            if (appId.equalsIgnoreCase(description.getAppId())) {
                return description;
            }
        }
        return null;
    }

    /**
     * 添加应用描述
     *
     * @param description 应用描述
     */
    @Override
    public void addDescription(ApplicationDescription description) {
        mAppsDes.add(description);
    }

    /**
     * 添加应用描述
     *
     * @param descriptions 应用描述s
     */
    @Override
    public void addDescription(List<ApplicationDescription> descriptions) {
        mAppsDes.addAll(descriptions);
    }

    /**
     * 启动入口APP
     *
     * @throws AppLoadException
     */
    @Override
    public void startEntryApp(Bundle params) throws AppLoadException {
        ApplicationDescription description = findDescriptionByName(mEntryApp);
        String appId = description.getAppId();
        startApp(null, appId, params);
    }

    /**
     * 设置入口应用名
     *
     * @param appName 应用名
     */
    @Override
    public void setEntryAppName(String appName) {
        mEntryApp = appName;
    }

    /**
     * 依附MicroApplication上下文
     *
     * @param applicationContext 依附MicroApplication上下文
     */
    public void attachContext(NarutoApplicationContext applicationContext) {
        mNarutoApplicationContext = applicationContext;
    }

    @Override
    public void exit() {
        while (!mApps.isEmpty()) {
            MicroApplication microApplication = mApps.pop();
            LogCatLog.v(TAG, "exit() pop appId: " + microApplication.getAppId());
            microApplication.destroy(null);
        }
        mAppsMap.clear();
    }

    @Override
    public void clear() {
        mApps.clear();
        mAppsMap.clear();
    }

    @Override
    public void onDestroyApp(MicroApplication microApplication) {
        mApps.remove(microApplication);
        mAppsMap.remove(microApplication.getAppId());
        LogCatLog.v(TAG, "onDestroyApp() pop appId: " + microApplication.getAppId());
    }

    @Override
    public void clearTop(MicroApplication microApplication) {
        MicroApplication tmp;
        if(mApps == null) {
            return;
        }
        if(mApps.size() == 0) {
            return;
        }
        while (microApplication != (tmp = mApps.peek())) {
            mApps.pop();
            LogCatLog.v(TAG, "clearTop() pop appId: " + tmp.getAppId());
            mAppsMap.remove(tmp.getAppId());
            break;
        }
    }

    @Override
    public MicroApplication getTopRunningApp() {
        if (!mApps.isEmpty()) {
            return mApps.peek();//获取栈顶元素，但不出栈
        }
        return null;
    }

    @Override
    public void saveState(Editor editor) {
        List<String> appIds = new ArrayList<String>();
        for (MicroApplication application : mApps) {
            String appId = application.getAppId();
            appIds.add(appId);
            application.saveState(editor);
        }
        editor.putString("ApplicationManager", JSON.toJSONString(appIds));
        editor.putString("ApplicationManager.EntryApp", mEntryApp);
    }

    @Override
    public void restoreState(SharedPreferences preferences) {
        mEntryApp = preferences.getString("ApplicationManager.EntryApp", null);
        String string = preferences.getString("ApplicationManager", null);
        if (null != string) {
            List<String> appIds = JSON.parseArray(string, String.class);
            for (String appId : appIds) {
                try {
                    ApplicationDescription targetAppDes = findDescriptionById(appId);
                    MicroApplication application = createNativeApp(targetAppDes, null);
                    application.setSourceId(appId);
                    application.restoreState(preferences);

                    mApps.push(application);//入栈
                    LogCatLog.v(TAG, "restoreState() App pushed: " + application.getAppId());
                    mAppsMap.put(appId, application);
                } catch (AppLoadException exception) {
                    LogCatLog.e(TAG, exception);
                }
            }
        }
    }

}
