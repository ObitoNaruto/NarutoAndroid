package com.naruto.mobile.framework.biz.ext.shortCut.impl;

import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;

import com.naruto.mobile.base.serviceaop.NarutoApplication;

public class ShortcutUtils {
    // Action 添加Shortcut
    public static final String ACTION_ADD_SHORTCUT = "com.android.launcher.action.INSTALL_SHORTCUT";
    // Action 移除Shortcut
    public static final String ACTION_REMOVE_SHORTCUT = "com.android.launcher.action.UNINSTALL_SHORTCUT";

    /**
     * 添加快捷方式
     *
     * @param actionIntent 要启动的Intent
     * @param name         name
     * @param allowRepeat  是否允许重复
     * @param iconBitmap   快捷方式图标
     */
    public static void addShortcut(Intent actionIntent, String name,
            boolean allowRepeat, Bitmap iconBitmap) {
        Intent addShortcutIntent = new Intent(ACTION_ADD_SHORTCUT);
        // 是否允许重复创建
        addShortcutIntent.putExtra("duplicate", allowRepeat);
        // 快捷方式的标题
        addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
        // 快捷方式的图标
        addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, iconBitmap);
        // 快捷方式的动作
        addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, actionIntent);
        NarutoApplication.getInstance().getApplicationContext().sendBroadcast(addShortcutIntent);
    }

    /**
     * 添加快捷方式
     *
     * @param actionIntent 要启动的Intent
     * @param name         name
     * @param allowRepeat  是否允许重复
     * @param resId   快捷方式图标
     */
    public static void addShortcut(Intent actionIntent, String name,
            boolean allowRepeat, int resId) {
        Intent addShortcutIntent = new Intent(ACTION_ADD_SHORTCUT);
        // 是否允许重复创建
        addShortcutIntent.putExtra("duplicate", allowRepeat);
        // 快捷方式的标题
        addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
        // 快捷方式的图标
        addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(NarutoApplication.getInstance().getApplicationContext(), resId));
        // 快捷方式的动作
        addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, actionIntent);
        NarutoApplication.getInstance().sendBroadcast(addShortcutIntent);
        Log.d("xxm", "---------------addShortcut called!------------");
    }

    /**
     * 移除快捷方式
     *
     * @param actionIntent 要启动的Intent
     * @param name         name
     */
    public static void removeShortcut(Intent actionIntent, String name) {
        Intent intent = new Intent(ACTION_REMOVE_SHORTCUT);
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
        intent.putExtra("duplicate", false);
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, actionIntent);
        NarutoApplication.getInstance().getApplicationContext().sendBroadcast(intent);
    }
}
