package com.naruto.mobile.framework.biz.ext.shortCut.impl;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import com.naruto.mobile.base.serviceaop.NarutoApplication;
import com.naruto.mobile.framework.biz.ext.shortCut.ShortCutService;

public class ShortCutServiceImpl extends ShortCutService{

    private final String SP_NAME = "ShortCutService_SP";

    private SharedPreferences mSP = null;
    private String mNoteTest = null;
    @Override
    public void installAppSchemeShortCut(final String appName, final Bitmap iconBitmap, final Map<String, String> params,
            final String targetClazz) throws Exception {
        // 检查主线程
        if (!isInMainThread()) {
            throw new RuntimeException("You should call this interface in main thread");
        }
        // 检查参数
        if (TextUtils.isEmpty(appName) || (iconBitmap == null || iconBitmap.isRecycled())) {
            throw new Exception("call installAppSchemeShortCut with invalid params!");
        }
        // 检查钱包运行状态
        Activity topActivity = null;
        //待实现，目前不知道在什么时候把当前的activity设置进来
        WeakReference<Activity> topActivityRef = NarutoApplication.getInstance().getNarutoApplicationContext().getTopActivity();
        if (topActivityRef != null) {
            topActivity = topActivityRef.get();
        }
        if (topActivity == null || topActivity.isFinishing()) {
            throw new Exception("no activity can show install shortcut dialog!");
        }
        // 判断设备是否支持添加快捷方式，不支持的话，弹一个Toast出来
        if (!isSupportInstallDesktopShortCut()) {
            Toast.makeText(topActivity, "该手机设备不支持", Toast.LENGTH_SHORT).show();
            return;
        }
        // 如果已经安装过对应的快捷方式，弹一个Toast出来
        if (isShortCutInstalledBefore(appName, iconBitmap, params, targetClazz) && isThereAShortCutOnDesktop(appName, iconBitmap, params, targetClazz)) {
            Toast.makeText(topActivity, "该应用桌面快捷方式已创建", Toast.LENGTH_SHORT).show();
            return;
        }
        // 处理配置的文案
        String[] noteTexts = mNoteTest == null ? null : mNoteTest.split(";");
        String title = "标题";
        String message = "将该应用添加至手机桌面快捷方式?";
        String positive = "立即添加";
        String negative = "取消";
        if (noteTexts != null && noteTexts.length == 4) {
            title = noteTexts[0];
            message = noteTexts[1];
            positive = noteTexts[2];
            negative = noteTexts[3];
        }
        AlertDialog dialog = new AlertDialog.Builder(topActivity)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // 埋点，上报创建快捷方式的业务场景
                        Intent intent = getAppShortcutIntent(params, targetClazz);
                        ShortcutUtils.addShortcut(intent, appName, false, iconBitmap);
//                        ShortcutUtils.addShortcut(intent, appName, false, android.R.mipmap.sym_def_app_icon);
                        initSP();
                        mSP.edit().putBoolean(targetClazz, true).apply();
                    }
                })
                .setNegativeButton(negative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // 埋点，上报创建快捷方式的业务场景
                    }
                })
                .setCancelable(false)
                .create();
        dialog.show();
    }

    @Override
    public void uninstallAppSchemeShortCut(String appName, Bitmap iconBitmap, Map<String, String> params,
            Map<String, String> sceneParams, String targetClazz) throws Exception {
        // 判断主线程
        if (!isInMainThread()) {
            throw new RuntimeException("You should call this interface in main thread");
        }
        // 埋点，上报删除快捷方式的业务场景
//        behaviorLog(BEHAVIOR_UNINSTALL, appId);
        // 检查参数
        if (TextUtils.isEmpty(appName) || (iconBitmap == null || iconBitmap.isRecycled())) {
            throw new Exception("call uninstallAppSchemeShortCut with invalid params");
        }
        // 执行删除快捷方式
        Intent intent = getAppShortcutIntent(params, targetClazz);
        ShortcutUtils.removeShortcut(intent, appName);
        initSP();
        mSP.edit().remove(targetClazz).apply();
    }

    @Override
    public boolean isSupportInstallDesktopShortCut() {
        return checkCanAddShortcut();
    }

    @Override
    public boolean isSupportUninstallDesktopShortCut() {
        return checkCanRemoveShortcut();
    }

    @Override
    public boolean isShortCutInstalledBefore(String appName, Bitmap iconBitmap,
            Map<String, String> params, String targetClazz) {
        if(TextUtils.isEmpty(appName)){
            return false;
        }
        initSP();
        return mSP.getBoolean(targetClazz, false);
    }

    @Override
    public boolean isThereAShortCutOnDesktop(String appName, Bitmap iconBitmap,
            Map<String, String> params, String targetClazz) {
        return ShortcutSuperUtils.isShortCutExist(NarutoApplication.getInstance().getApplicationContext(), appName, getAppShortcutIntent(params, targetClazz));
    }

    @Override
    protected void onCreate(Bundle params) {
        mNoteTest = "对话框标题,将该应用添加至手机桌面快捷方式?,立即添加,取消";
    }

    @Override
    protected void onDestroy(Bundle params) {
        // noting to do now
    }

    /**
     * 检查是否可以创建桌面快捷方式
     *
     * @return
     */
    private boolean checkCanAddShortcut() {
        return ShortcutSuperUtils.checkCanAddShortcut();
    }

    private boolean checkCanRemoveShortcut() {
        return ShortcutSuperUtils.checkCanRemoveShortcut();
    }

    private boolean isInMainThread() {
        return "main".equals(Thread.currentThread().getName());
    }

    /**
     * 获取第三方已安装应用的快捷方式的intent
     *
     * @param params
     * @return shortCutIntent
     */
    private Intent getAppShortcutIntent(Map<String, String> params, String targetClazz) {
        if (params == null)
            params = new HashMap<String, String>();

        String packageName = NarutoApplication.getInstance().getPackageName();
        Log.d("xxm", "getAppShortcutIntent called! packageName:" + packageName + ", targetCalzz:" + targetClazz);
        ComponentName comp = new ComponentName(packageName, targetClazz);
        Intent intent = new Intent(Intent.ACTION_VIEW).setComponent(comp);
        return intent;
    }

    private void initSP() {
        if (mSP == null) {
            synchronized (this) {
                if (mSP == null) {
                    mSP = NarutoApplication.getInstance().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
                }
            }
        }
    }
}
