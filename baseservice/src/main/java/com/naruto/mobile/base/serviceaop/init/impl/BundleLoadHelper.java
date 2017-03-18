package com.naruto.mobile.base.serviceaop.init.impl;

import android.util.Log;


import java.util.List;

import com.naruto.mobile.base.serviceaop.NarutoApplicationContext;
import com.naruto.mobile.base.serviceaop.init.BootLoader;
import com.naruto.mobile.base.serviceaop.service.BaseMetaInfo;
import com.naruto.mobile.base.serviceaop.service.ServiceDescription;
import com.naruto.mobile.base.serviceaop.service.ext.ExternalServiceManager;

/**
 * Created by xinming.xxm on 2016/5/15.
 */
public class BundleLoadHelper {
    private BootLoader mBootLoader;
    private NarutoApplicationContext mNarutoApplicationContext;
    private ExternalServiceManager mExternalServiceManager;

    public BundleLoadHelper(BootLoader bootLoader){
        mBootLoader = bootLoader;
        mNarutoApplicationContext = mBootLoader.getContext();
        mExternalServiceManager = mNarutoApplicationContext.findServiceByInterface(ExternalServiceManager.class.getName());
    }

    public void loadBundleDefinitions() {
        try {
            BundleDao bundleDao = new BundleDao();//����module��BundleDao��ʼ��ʱע��
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
            //������Ϊ�˻�ȡ��module�µ�MetaInfo�ļ��࣬������ע���˷���
            String pkg = bundle.getPackageName();
            baseMetaInfo = (BaseMetaInfo) Class.forName(pkg + ".MetaInfo").newInstance();
            Log.d("xxm", "BundleLoadHelper loadBundle called!" + (pkg + ".MetaInfo"));
        } catch (Exception e) {
            //��ӡ������Ϣ
        }

        if (null == baseMetaInfo) {
            return;
        }

        // Load service
        List<ServiceDescription> services = baseMetaInfo.getServices();
        if (null != services && services.size() > 0) {
            for (ServiceDescription serviceDescription : services) {
                if (null == serviceDescription) {
                    continue;
                }
                mExternalServiceManager.registerExternalServiceOnly(serviceDescription);//�ⲿ���������,����ֻע�᲻����
            }
        }
    }

}
