package com.naruto.mobile.base.serviceaop.app.service;


import java.util.List;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;

import com.naruto.mobile.base.serviceaop.app.AppLoadException;
import com.naruto.mobile.base.serviceaop.app.ApplicationDescription;
import com.naruto.mobile.base.serviceaop.app.MicroApplication;

/**
 * App管理
 */
public interface ApplicationManager {

    /**
     * 通过App唯一id查找App
     *
     * @param appId App唯一Id
     * @return
     */
    MicroApplication findAppById(String appId);

    /**
     * 启动App
     *
     * @param sourceAppId 源App唯一Id
     * @param targetAppId 目标App唯一Id
     * @param params      参数
     * @throws AppLoadException
     */
    void startApp(String sourceAppId, String targetAppId, Bundle params)
            throws AppLoadException;

    /**
     * 获得当前正在运行的app
     *
     * @return
     */
    MicroApplication getTopRunningApp();

    /**
     * 关闭App
     *
     * @param sourceAppId 源App唯一id
     * @param targetId    目标App唯一id
     * @param params      参数
     */
    void finishApp(String sourceAppId, String targetId, Bundle params);

    void onDestroyApp(MicroApplication microApplication);

    void startEntryApp(Bundle params) throws AppLoadException;

    void addDescription(ApplicationDescription description);

    void addDescription(List<ApplicationDescription> descriptions);

    ApplicationDescription findDescriptionByName(String appName);

    ApplicationDescription findDescriptionById(String appId);

    void setEntryAppName(String appName);

    /**
     * 销毁
     */
    void exit();

    void clear();

    /**
     * 清除栈以上的microApplication
     *
     * @param microApplication
     */
    void clearTop(MicroApplication microApplication);

    /**
     * 保存状态
     *
     * @param editor
     */
    void saveState(Editor editor);

    /**
     * 恢复状态
     *
     * @param preferences
     */
    void restoreState(SharedPreferences preferences);
}
