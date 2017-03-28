package com.naruto.mobile.base.serviceaop.service;

import android.content.SharedPreferences;

import com.naruto.mobile.base.serviceaop.NarutoApplicationContext;


/**
 * Created by xinming.xxm on 2016/5/13.
 */
public interface ServiceManager {
    /**
     * 依附项目上下文
     * @param applicationContext
     */
    void attachContext(NarutoApplicationContext applicationContext);

    /**
     * 注册服务
     * @param className 服务接口类
     * @param service 服务
     * @param <T>
     * @return
     */
    <T> boolean registerService(String className, T service);

    /**
     *查找服务
     * @param className 服务接口类名
     * @param <T>
     * @return
     */
    <T> T findServiceByInterface(String className);

    /**
     * 销毁回调
     */
    void onDestroyService(MicroService microService);

    /**
     * 退出
     */
    void exit();

    /**
     * 保存状态
     * @param editor
     */
    void saveState(SharedPreferences.Editor editor);

    /**
     * 恢复状态
     * @param preferences
     */
    void restoreState(SharedPreferences preferences);

    /**
     * 注销服务
     * @param interfaceName
     * @param <T>
     * @return
     */
    <T> T unregisterService(String interfaceName);
}
