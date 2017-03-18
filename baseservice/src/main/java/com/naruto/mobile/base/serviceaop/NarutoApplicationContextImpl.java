package com.naruto.mobile.base.serviceaop;

import android.util.Log;

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
    /**
     * android������
     */
    private NarutoApplication mApplication;
    /**
     * �������
     */
    private ServiceManager mServiceManager;

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
     * ��ʼ��
     */
    private void init(){
        mServiceManager = new ServiceManagerImpl();
        mServiceManager.attachContext(this);//Ϊ�������������Ŀ�����Ļ���

        new BootLoaderImpl(NarutoApplicationContextImpl.this).load();//��ʼ�������м���load
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
            //ExternalServiceManager��Application�оͼ����ˣ�������BootLoaderImple
            ExternalServiceManager exm = mServiceManager
                    .findServiceByInterface(ExternalServiceManager.class.getName());
            if (null != exm) {
                Log.d("xxm", "NarutoApplicationContextImpl getExtServiceByInterface called! ExternalServiceManager:" + exm);
                return (T) exm.getExternalService(className);//�ⲿ��չ���������ȥ��÷���
            }
        }
        return null;
    }

    @Override
    public void onDestroyContent(MicroContent microContent) {
        if (microContent instanceof MicroService) {
            mServiceManager.onDestroyService((MicroService) microContent);
        }
    }
}
