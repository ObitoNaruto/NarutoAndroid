package com.naruto.mobile.base.serviceaop;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.ArrayMap;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicBoolean;

import com.naruto.mobile.base.log.logging.LogCatLog;
import com.naruto.mobile.base.serviceaop.app.ActivityApplication;
import com.naruto.mobile.base.serviceaop.app.AppLoadException;
import com.naruto.mobile.base.serviceaop.app.ApplicationDescription;
import com.naruto.mobile.base.serviceaop.app.MicroApplication;
import com.naruto.mobile.base.serviceaop.app.service.ApplicationManager;
import com.naruto.mobile.base.serviceaop.app.service.impl.ApplicationManagerImpl;
import com.naruto.mobile.base.serviceaop.app.ui.ActivityResponsable;
import com.naruto.mobile.base.serviceaop.demo.task.PipeLineServiceValueManager;
import com.naruto.mobile.base.threadpool.PipeLine;

import com.naruto.mobile.base.serviceaop.broadcast.LocalBroadcastManagerWrapper;
import com.naruto.mobile.base.serviceaop.init.impl.BootLoaderImpl;
import com.naruto.mobile.base.serviceaop.service.MicroService;
import com.naruto.mobile.base.serviceaop.service.ServiceManager;
import com.naruto.mobile.base.serviceaop.service.ext.ExternalService;
import com.naruto.mobile.base.serviceaop.service.ext.ExternalServiceManager;
import com.naruto.mobile.base.serviceaop.service.impl.ServiceManagerImpl;


/**
 * Created by xinming.xxm on 2016/5/13.
 */
public class NarutoApplicationContextImpl implements NarutoApplicationContext{

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private AtomicBoolean mInited = new AtomicBoolean(false);


    /**
     * android上下文
     */
    private NarutoApplication mApplication;

    /**
     * 当前Activity
     */
    private Activity mActiveActivity;

    /**
     * 服务管理
     */
    private ServiceManager mServiceManager;

    /**
     * 应用管理
     */
    private ApplicationManager mApplicationManager;

    /**
     * 应用内广播管理器
     */
    private LocalBroadcastManagerWrapper mLocalBroadcastManagerWrapper;

    private PipeLineServiceValueManager mPipeLineServiceValueManager;


        //这个是原装
//    @Override
//    public WeakReference<Activity> getTopActivity() {
//        return new WeakReference<Activity>(mActiveActivity);
//    }

    @Override
    public WeakReference<Activity> getTopActivity() {
        try {
            Class activityThreadClass = Class.forName("android.app.ActivityThread");
            Object activityThread = activityThreadClass.getMethod("currentActivityThread")
                    .invoke(null);
            Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
            activitiesField.setAccessible(true);
            ArrayMap activities = (ArrayMap) activitiesField.get(activityThread);
            for (Object activityRecord : activities.values()) {
                Class activityRecordClass = activityRecord.getClass();
                Field pausedField = activityRecordClass.getDeclaredField("paused");
                pausedField.setAccessible(true);
                if (!pausedField.getBoolean(activityRecord)) {
                    Field activityField = activityRecordClass.getDeclaredField("activity");
                    activityField.setAccessible(true);
                    mActiveActivity = (Activity) activityField.get(activityRecord);
                    return new WeakReference<Activity>(mActiveActivity);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        throw new RuntimeException("Didn't find the running activity");

    }

    /**
     * 更新激活的Activity
     *备注：所有的activity继承了BaseActivity，其onResume中进行了调用
     * @param activity
     */
    @Override
    public void updateActivity(Activity activity) {
//        mActiveActivity = null;
        mActiveActivity = activity;
    }


    @Override
    public void attachContext(NarutoApplication application) {
        mApplication = application;
        init();
    }

    @Override
    public NarutoApplication getApplicationContext() {
        return mApplication;
    }

    /**
     * 初始化
     */
    private void init(){
//        FrameworkExceptionHandler.getInstance().init(mApplication);

        //serviceManager初始化
        mServiceManager = new ServiceManagerImpl();
        mServiceManager.attachContext(this);//为服务管理器绑定项目上下文环境


         //AppManager初始化
        ApplicationManagerImpl applicationManager = new ApplicationManagerImpl();
        applicationManager.attachContext(this);
        mApplicationManager = applicationManager;
        mServiceManager.registerService(ApplicationManager.class.getName(), mApplicationManager);

         //应用内BroadcastReceiver管理器
        mLocalBroadcastManagerWrapper = LocalBroadcastManagerWrapper.getInstance(mApplication);
        mServiceManager.registerService(LocalBroadcastManagerWrapper.class.getName(),
                mLocalBroadcastManagerWrapper);


        //管道任务初始化
        mPipeLineServiceValueManager = PipeLineServiceValueManager.getInstance();
        mServiceManager.registerService(PipeLineServiceValueManager.class.getName(), mPipeLineServiceValueManager);

        //注册服务入口
        new BootLoaderImpl(NarutoApplicationContextImpl.this).load();//初始化并进行加载load
    }

    @Override
    public <T> boolean registerService(String className, T service) {
        return mServiceManager.registerService(className, service);
    }

    @Override
    public <T> T unregisterService(String interfaceName) {
        return mServiceManager.unregisterService(interfaceName);
    }

    @Override
    public <T> T findServiceByInterface(String className) {
        if (null != mServiceManager) {
            T t = mServiceManager.findServiceByInterface(className);
            Log.d("xxm", "NarutoApplicationContextImpl findServiceByInterface called! current service=" + t);
            if (null == t) {
                t = (T) getExtServiceByInterface(className);
            }
            return t;
        }
        return null;
    }

    @Override
    public <T extends ExternalService> T getExtServiceByInterface(String className) {
        if (null != mServiceManager) {
            //ExternalServiceManager在Application中就加载了，具体在BootLoaderImple
            ExternalServiceManager exm = mServiceManager
                    .findServiceByInterface(ExternalServiceManager.class.getName());
            if (null != exm) {
                Log.d("xxm", "NarutoApplicationContextImpl getExtServiceByInterface called! ExternalServiceManager:" + exm);
                return (T) exm.getExternalService(className);//外部扩展服务管理器去获得服务
            }
        }
        return null;
    }

    @Override
    public <T extends PipeLine> T getPipelineByName(String pipeLineName, long pipeLineTimeout) {
        return null;
    }

    @Override
    public <T extends PipeLine> T getPipelineByName(String pipeLineName) {
        return (T) mPipeLineServiceValueManager.getPipelineByName(pipeLineName);
    }

    @Override
    public void startActivity(MicroApplication microApplication, String className) {
        if(null==mActiveActivity)//mActiveActivity加载入口的app时，什么时机具体在哪更新这个数据
            return;
        if (microApplication instanceof ActivityApplication) {

            Class<?> clazz = getActivityClass(className);

            Intent intent = new Intent(mActiveActivity, clazz);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
            intent.putExtra("app_id", microApplication.getAppId());
            microApplication.setIsPrevent(true);
            mActiveActivity.startActivity(intent);
        } else {
            throw new RuntimeException("Service can't start activity");
        }
    }

    @Override
    public void startActivity(MicroApplication microApplication, Intent intent) {
        if(null==mActiveActivity)
            return;

        intent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
        intent.putExtra("app_id", microApplication.getAppId());
        microApplication.setIsPrevent(true);
        mActiveActivity.startActivity(intent);
    }

    private Class<?> getActivityClass(String className) {
        Class<?> clazz;
        try {
            ClassLoader classLoad = mApplication.getBaseContext().getClassLoader();
            clazz = classLoad.loadClass(className);
            if (clazz == null)
                throw new ActivityNotFoundException("entry class must be set.");
        } catch (ClassNotFoundException e) {
            throw new ActivityNotFoundException(e == null ? "" : e.getMessage());
        }
        return clazz;
    }


    @Override
    public MicroApplication findTopRunningApp() {
        return mApplicationManager.getTopRunningApp();
    }

    @Override
    public void startApp(final String sourceAppId, final String targetAppId, final Bundle params) throws AppLoadException {
        mHandler.post(new Runnable() {//放到主线程启动
            @Override
            public void run() {
                try {
                    mApplicationManager.startApp(sourceAppId, targetAppId, params);

//                    if ( targetAppId == AppId.MAP_ASSIST ){
//	                    AlipayLogInfo logInfo = new AlipayLogInfo();
//	            		logInfo.behaviourIdEnum = BehaviourIdEnum.OPENPAGE;
//	            		logInfo.appID = AppId.MAP_ASSIST;
//	            		logInfo.viewID = "LBSIndex";
//	            		String biz = params != null ? params.getString("biz") : "";
//	            		logInfo.extendParams = new String[] { "","",biz };
//	            		AlipayLogAgent.writeLog(getApplicationContext(),logInfo);
//                    }

                } catch (AppLoadException e) {
                    LogCatLog.e("MicroApplicationContextImpl", e);
                }
            }
        });
    }

    @Override
    public void finishApp(final String sourceAppId, final String targetAppId, final Bundle params) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mApplicationManager.finishApp(sourceAppId, targetAppId, params);
            }
        });
    }

    @Override
    public MicroApplication findAppById(String appId) {
        return mApplicationManager.findAppById(appId);
    }

    @Override
    public ApplicationDescription findDescriptionById(String appId) {
        return mApplicationManager.findDescriptionById(appId);
    }

    @Override
    public void startActivityForResult(MicroApplication microApplication, String className, int requestCode) {
        if(null==mActiveActivity)
            return;
        if (microApplication instanceof ActivityApplication) {
            Class<?> clazz = getActivityClass(className);

            Intent intent = new Intent(mActiveActivity, clazz);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
            intent.putExtra("app_id", microApplication.getAppId());
            microApplication.setIsPrevent(true);
            mActiveActivity.startActivityForResult(intent, requestCode);
        } else {
            throw new RuntimeException("Service can't start activity");
        }
    }

    @Override
    public void startActivityForResult(MicroApplication microApplication, Intent intent, int requestCode) {
        if(null==mActiveActivity)
            return;

        intent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
        intent.putExtra("app_id", microApplication.getAppId());
        microApplication.setIsPrevent(true);
        mActiveActivity.startActivityForResult(intent, requestCode);
    }

    @Override
    public void startExtActivityForResult(MicroApplication microApplication, Intent intent, int requestCode) {
        if(null==mActiveActivity)
            return;

        intent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
        mActiveActivity.startActivityForResult(intent, requestCode);
    }

    @Override
    public void startExtActivity(MicroApplication microApplication, Intent intent) {
        if(null==mActiveActivity)
            return;

        intent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
        mActiveActivity.startActivity(intent);
    }

    @Override
    public void exit() {
//        AlipayLogAgent.uploadLog(mApplication);
//    	GlobalDataHall.destroyData();
        mApplicationManager.exit();
        clearState();

        //清除信息
//        AlipayLogAgent.unInitClient();

//        SchemeService schemeService = findServiceByInterface(SchemeService.class.getName());
//        schemeService.cleanTagId();

//        clearCookies();

        // Try everything to make sure this process goes away.
        // References from com.android.internal.os.RuntimeInit.java
        // public static void wtf(String tag, Throwable t)
//        Process.killProcess(Process.myPid());
        System.exit(10);
    }

    @Override
    public String getGwUrl() {
        return "https://mobilegw.alipay.com/mgw.htm";
    }

    @Override
    public void setGwUrl(String url) {

    }

    @Override
    public void Toast(String msg, int period) {
        if (mActiveActivity instanceof ActivityResponsable) {
            ((ActivityResponsable) mActiveActivity).toast(msg, period);
        } else {
            throw new IllegalAccessError("current Activity must be ActivityInterface。");
        }
    }

    @Override
    public void Alert(String title, String msg, String positive, DialogInterface.OnClickListener positiveListener, String negative, DialogInterface.OnClickListener negativeListener) {
        if (mActiveActivity instanceof ActivityResponsable) {

            //2.x系统，当前Activity没有焦点，alert无法正常弹出，应用会进入假死状态，所以在此增加一个条件判断。
//        	if( mActiveActivity.hasWindowFocus() ){
            ((ActivityResponsable) mActiveActivity).alert(title, msg, positive, positiveListener,
                    negative, negativeListener);
//        	}

        } else {
            throw new IllegalAccessError("current Activity must be ActivityInterface。");
        }
    }

    @Override
    public void showProgressDialog(String msg) {
        if (mActiveActivity instanceof ActivityResponsable) {
            ((ActivityResponsable) mActiveActivity).showProgressDialog(msg);
        } else {
            throw new IllegalAccessError("current Activity must be ActivityInterface。");
        }
    }

    @Override
    public void showProgressDialog(String msg, boolean cancelable, DialogInterface.OnCancelListener cancelListener) {
        if (mActiveActivity instanceof ActivityResponsable) {
            ((ActivityResponsable) mActiveActivity).showProgressDialog(msg, cancelable, cancelListener);
        } else {
            throw new IllegalAccessError("current Activity must be ActivityInterface。");
        }
    }

    @Override
    public void dismissProgressDialog() {
        if (mActiveActivity instanceof ActivityResponsable) {
            ((ActivityResponsable) mActiveActivity).dismissProgressDialog();
        } else {
            throw new IllegalAccessError("current Activity must be ActivityInterface。");
        }
    }

    @Override
    public void saveState() {
        SharedPreferences preferences = NarutoApplication.getInstance().getSharedPreferences(
                "_share_tmp_", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("@@", true);//标识进程是否被非法关闭
        mApplicationManager.saveState(editor);
        mServiceManager.saveState(editor);
        editor.commit();
    }

    @Override
    public void restoreState() {
        SharedPreferences preferences = NarutoApplication.getInstance().getSharedPreferences(
                "_share_tmp_", Context.MODE_PRIVATE);
        mApplicationManager.restoreState(preferences);
        mServiceManager.restoreState(preferences);
    }

    @Override
    public void clearState() {
        SharedPreferences preferences = NarutoApplication.getInstance().getSharedPreferences(
                "_share_tmp_", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear().commit();
    }

    @Override
    public boolean hasInited() {
        return mInited.get();
    }

    @Override
    public void onWindowFocus(MicroApplication application) {
        mApplicationManager.clearTop(application);
    }

    @Override
    public void onDestroyContent(MicroContent microContent) {
        if (microContent instanceof MicroService) {
            mServiceManager.onDestroyService((MicroService) microContent);
        }
    }

}
