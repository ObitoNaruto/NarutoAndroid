package com.naruto.mobile.base.serviceaop.init.impl;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.util.Log;


import java.util.List;

import com.naruto.mobile.base.log.logging.LogCatLog;
import com.naruto.mobile.base.serviceaop.NarutoApplicationContext;
import com.naruto.mobile.base.serviceaop.broadcast.BroadcastReceiverDescription;
import com.naruto.mobile.base.serviceaop.broadcast.LocalBroadcastManagerWrapper;
import com.naruto.mobile.base.serviceaop.demo.task.PipeLineServiceValueManager;
import com.naruto.mobile.base.serviceaop.init.BootLoader;
import com.naruto.mobile.base.serviceaop.msg.MsgCodeConstants;
import com.naruto.mobile.base.serviceaop.service.BaseMetaInfo;
import com.naruto.mobile.base.serviceaop.service.ServiceDescription;
import com.naruto.mobile.base.serviceaop.service.ext.ExternalServiceManager;
import com.naruto.mobile.base.serviceaop.task.ValueDescription;

/**
 * Created by xinming.xxm on 2016/5/15.
 */
public class BundleLoadHelper {
    private BootLoader mBootLoader;
    private NarutoApplicationContext mNarutoApplicationContext;
    private ExternalServiceManager mExternalServiceManager;

    /**
     * 应用内广播管理器
     */
    private LocalBroadcastManagerWrapper mLocalBroadcastManagerWrapper;

    private PipeLineServiceValueManager mPipeLineServiceValueManager;

    public BundleLoadHelper(BootLoader bootLoader){
        mBootLoader = bootLoader;
        mNarutoApplicationContext = mBootLoader.getContext();
        mExternalServiceManager = mNarutoApplicationContext.findServiceByInterface(ExternalServiceManager.class.getName());
        mLocalBroadcastManagerWrapper = mNarutoApplicationContext.findServiceByInterface(LocalBroadcastManagerWrapper.class.getName());
        mPipeLineServiceValueManager = mNarutoApplicationContext.findServiceByInterface(PipeLineServiceValueManager.class.getName());
    }

    public void loadBundleDefinitions() {
        try {
            BundleDao bundleDao = new BundleDao();//所有module在BundleDao初始化时注册
            List<Bundle> bundleList = bundleDao.getBundles();
            for (Bundle bundle : bundleList) {
                loadBundle(bundle);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void loadBundle(Bundle bundle) throws ClassNotFoundException, NoSuchFieldException,IllegalArgumentException,
            IllegalAccessException {
        BaseMetaInfo baseMetaInfo = null;
        try {
            //这里是为了获取在module下的MetaInfo文件类，此类中注册了服务
            String pkg = bundle.getPackageName();
            baseMetaInfo = (BaseMetaInfo) Class.forName(pkg + ".MetaInfo").newInstance();
            Log.d("xxm", "BundleLoadHelper loadBundle called!" + (pkg + ".MetaInfo"));
        } catch (Exception e) {
            //打印出错信息
        }

        if (null == baseMetaInfo) {
            return;
        }

        // Load service
        List<ServiceDescription> services = baseMetaInfo.getServices();
        Log.d("xxm", "BundleLoadHelper loadBundle services" + services);
        if (null != services && services.size() > 0) {
            for (ServiceDescription serviceDescription : services) {
                Log.d("xxm", "BundleLoadHelper loadBundle serviceDescription" + serviceDescription.getClassName());
                if (null == serviceDescription) {
                    continue;
                }
                mExternalServiceManager.registerExternalServiceOnly(serviceDescription);//外部服务管理器,这里只注册不加载
            }
        }

        //load broadcast
        List<BroadcastReceiverDescription> broadcastReceivers = baseMetaInfo.getBroadcastReceivers();
        if (null != broadcastReceivers && broadcastReceivers.size() > 0) {
            for (BroadcastReceiverDescription broadcastReceiverDescription : broadcastReceivers) {
                if (null == broadcastReceiverDescription.getClassName()) {
                    LogCatLog.e("BundleLoadHelper",
                            "pkg:" + bundle.getPackageName()
                                    + "的MetaInfo中存在className为空的BroadcastReceiverDescription！");
                    continue;
                }
                if (null == broadcastReceiverDescription.getMsgCode()
                        || broadcastReceiverDescription.getMsgCode().length < 1) {
                    LogCatLog.e("BundleLoadHelper", broadcastReceiverDescription.getClassName()
                            + "订阅的事件为空！");
                    continue;
                }

                BroadcastReceiver broadcastReceiver = null;
                try {
                    broadcastReceiver = (BroadcastReceiver) Class.forName(broadcastReceiverDescription.getClassName()).newInstance();
                } catch (InstantiationException e) {
                    LogCatLog.printStackTraceAndMore(e);
                }

                if (null != broadcastReceiver) {
                    IntentFilter intentFilter = new IntentFilter();
                    for (String msgCode : broadcastReceiverDescription.getMsgCode()) {
                        intentFilter.addAction(msgCode);
                    }
                    mLocalBroadcastManagerWrapper.registerReceiver(broadcastReceiver, intentFilter);
                }
            }
        }

        //load PipeLine task
        List<ValueDescription> pipeLineValueDescription = baseMetaInfo.getValueDescriptions();
        if (pipeLineValueDescription != null) {
            for (ValueDescription valueDescription : pipeLineValueDescription) {
                if(TextUtils.isEmpty(valueDescription.getClassName())) {
                    LogCatLog.e("BundleLoadHelper",
                            "pkg:" + bundle.getPackageName()
                                    + "的MetaInfo中存在className为空的ValueDescription！");
                    continue;
                }

                if(TextUtils.isEmpty(valueDescription.getPipelingName())) {
                    LogCatLog.e("BundleLoadHelper",
                            "pkg:" + bundle.getPackageName()
                                    + "的MetaInfo中存在PipelingName为空的ValueDescription！");
                    continue;
                }

                Runnable task = null;
                try{
                    task = (Runnable)Class.forName(valueDescription.getClassName()).newInstance();
                }catch (InstantiationException e){
                    LogCatLog.printStackTraceAndMore(e);
                }

                if(task != null) {
                    mPipeLineServiceValueManager.addTask(valueDescription.getPipelingName(), task, valueDescription.getThreadName(), valueDescription.getWeight());
                }
            }
        }
        //在合适的位置启动相关管道
        mPipeLineServiceValueManager.start(MsgCodeConstants.PIPELINE_FRAMEWORK_INITED);

    }
}
