package com.naruto.mobile.base.serviceaop;


import android.app.Activity;

import java.lang.ref.WeakReference;

import com.naruto.mobile.base.serviceaop.service.ext.ExternalService;

/**
 * Created by xinming.xxm on 2016/5/13.
 */
public interface NarutoApplicationContext {

    /**
     * ����ջ����Activity
     *
     * @return ջ��Activity
     */
    WeakReference<Activity> getTopActivity();

    /**
     * ���µ�ǰActivity
     *
     * @param activity Activity
     */
    void updateActivity(Activity activity);

    /**
     * ����android������
     * @param application
     */
    void attachContext(NarutoApplication application);

    /**
     * ��ȡandroid������
     * @return
     */
    NarutoApplication getApplicationContext();

    /**
     *ע�����
     * @param className ����ӿ�����
     * @param service ����
     * @param <T>
     * @return
     */
    <T> boolean registerService(String className, T service);

    <T> T unregisterService(String interfaceName);

    /**
     * ���ҷ���
     * @param className ����ӿ�����
     * @param <T>
     * @return
     */
    <T> T findServiceByInterface(String className);

    /**
     * ͨ������ӿڻ�ȡ�ⲿ����
     * @param className
     * @param <T>
     * @return
     */
    <T extends ExternalService> T getExtServiceByInterface(String className);

    /**
     * ����
     * @param microContent
     */
    void onDestroyContent(MicroContent microContent);
}
