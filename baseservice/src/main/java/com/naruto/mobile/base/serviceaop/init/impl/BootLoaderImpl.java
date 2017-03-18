package com.naruto.mobile.base.serviceaop.init.impl;

import android.app.Application;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;
import android.webkit.CookieSyncManager;


import java.util.ArrayList;
import java.util.List;

import com.naruto.mobile.base.serviceaop.NarutoApplicationContext;
import com.naruto.mobile.base.serviceaop.init.BootLoader;
import com.naruto.mobile.base.serviceaop.service.ServicesLoader;
import com.naruto.mobile.base.serviceaop.service.ext.ExternalServiceManager;
import com.naruto.mobile.base.serviceaop.service.ext.ExternalServiceManagerImpl;

/**
 * Created by xinming.xxm on 2016/5/13.
 */
public class BootLoaderImpl implements BootLoader {
    private List<Bundle> mBundles;

    private NarutoApplicationContext mNarutoApplicaitonContext;
    private ServicesLoader mServiceLoader;

    public BootLoaderImpl(NarutoApplicationContext narutoApplicationContext){
        mNarutoApplicaitonContext = narutoApplicationContext;
        mBundles = new ArrayList<Bundle>();
    }

    @Override
    public NarutoApplicationContext getContext() {
        return mNarutoApplicaitonContext;
    }

    @Override
    public void load() {
        Application application = mNarutoApplicaitonContext.getApplicationContext();//��ȡandroid������

        //��ȡmetaData
        String agentCommonServiceLoad = null;
//        String agentEntryPkgName = null;
//        try {
//            ApplicationInfo appInfo = application.getPackageManager().getApplicationInfo(application.getPackageName(), PackageManager.GET_META_DATA);
//            agentCommonServiceLoad = appInfo.metaData.getString("agent.commonservice.load");
//            agentEntryPkgName = appInfo.metaData.getString("agent.entry.pkgname");
//        } catch (Exception e1) {
//        }

        if (TextUtils.isEmpty(agentCommonServiceLoad)) {
            agentCommonServiceLoad = "com.naruto.mobile.base.serviceaop.service.impl.CommonServiceLoadAgent";
        }

        //step1. ���ȳ�ʼ���ⲿ�������
        ExternalServiceManager externalServiceManager = new ExternalServiceManagerImpl();
        externalServiceManager.attachContext(mNarutoApplicaitonContext);//����Ŀ������
        mNarutoApplicaitonContext.registerService(ExternalServiceManager.class.getName(), externalServiceManager);

        //step2. Ȼ���ʼ��������ṩ�����л�������
        try {
            Class<?> clazz = application.getClassLoader().loadClass(agentCommonServiceLoad);
            mServiceLoader = (ServicesLoader) clazz.newInstance();
            mServiceLoader.load();
        } catch (ClassNotFoundException e) {
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        }

        if (Runtime.getRuntime().availableProcessors() > 2) {//ϵͳ�Ƕദ����ʱ

            final HandlerThread loadServiceThread = new HandlerThread("name");
            loadServiceThread.start();
            Handler handler = new Handler(loadServiceThread.getLooper());

            handler.post(new Runnable() {
                public void run() {
                    try {
                        CookieSyncManager.createInstance(mNarutoApplicaitonContext.getApplicationContext());
                    } catch (Throwable e) {
                    }
                    if (mServiceLoader != null) {
                        mServiceLoader.afterBootLoad();
                    }
                }
            });

//            //��ʼ��������ṩ�����л�������,�������������
//            new CommonServiceLoadHelper(this).loadServices();

            new BundleLoadHelper(this).loadBundleDefinitions();//load����bundle(Module)�еķ���
        }
    }
}
