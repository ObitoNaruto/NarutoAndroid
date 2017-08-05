package com.naruto.mobile.framework.biz.ext.shortCut;

import android.graphics.Bitmap;

import java.util.Map;

import com.naruto.mobile.base.serviceaop.service.ext.ExternalService;

public abstract class ShortCutService extends ExternalService{

    /**
     * 创建指定内容的桌面Scheme快捷方式，需主线程调用
     * @param appName
     * @param iconBitmap
     * @param params
     * @return 是否创建成功
     */
    public abstract void installAppSchemeShortCut(String appName, Bitmap iconBitmap, Map<String, String> params, String targetClazz) throws Exception;

    /**
     * 删除指定名称的快捷方式，需主线程调用
     * @param appName
     * @param iconBitmap
     * @param params
     * @return 是否删除成功
     */
    public abstract void uninstallAppSchemeShortCut(String appName, Bitmap iconBitmap, Map<String, String> params, Map<String, String> sceneParams, String targetClazz) throws Exception;

    /**
     * 判断本机器是否支持安装桌面快捷方式
     * @return 是否支持
     */
    public abstract boolean isSupportInstallDesktopShortCut();

    /**
     * 判断本机器是否支持删除桌面快捷方式
     * @return 是否支持
     */
    public abstract boolean isSupportUninstallDesktopShortCut();

    /**
     * 是否已经尝试创建过桌面快捷方式
     * @param appName
     * @param iconBitmap
     * @param params
     * @return 是否已经创建桌面快捷方式
     */
    public abstract boolean isShortCutInstalledBefore(String appName, Bitmap iconBitmap, Map<String, String> params, String targetClazz);

    /**
     * 检查某个appId，在桌面是否有对应的图标
     * @param appName
     * @param iconBitmap
     * @param params
     * @return 是否已经创建桌面快捷方式
     */
    public abstract boolean isThereAShortCutOnDesktop(String appName, Bitmap iconBitmap, Map<String, String> params, String targetClazz);
}
