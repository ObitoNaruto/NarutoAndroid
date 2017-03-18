package com.naruto.mobile.base.serviceaop.service;

import android.content.SharedPreferences;

import com.naruto.mobile.base.serviceaop.NarutoApplicationContext;


/**
 * Created by xinming.xxm on 2016/5/13.
 */
public interface ServiceManager {
    /**
     * ������Ŀ������
     * @param applicationContext
     */
    void attachContext(NarutoApplicationContext applicationContext);

    /**
     * ע�����
     * @param className ����ӿ���
     * @param service ����
     * @param <T>
     * @return
     */
    <T> boolean registerService(String className, T service);

    /**
     *���ҷ���
     * @param className ����ӿ�����
     * @param <T>
     * @return
     */
    <T> T findServiceByInterface(String className);

    /**
     * ���ٻص�
     */
    void onDestroyService(MicroService microService);

    /**
     * �˳�
     */
    void exit();

    /**
     * ����״̬
     * @param editor
     */
    void saveState(SharedPreferences.Editor editor);

    /**
     * �ָ�״̬
     * @param preferences
     */
    void restoreState(SharedPreferences preferences);

    /**
     * ע������
     * @param interfaceName
     * @param <T>
     * @return
     */
    <T> T unregisterService(String interfaceName);
}
