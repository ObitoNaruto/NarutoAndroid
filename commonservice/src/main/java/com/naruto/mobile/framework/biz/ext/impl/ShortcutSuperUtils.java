package com.naruto.mobile.framework.biz.ext.impl;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import java.util.List;

import com.naruto.mobile.base.serviceaop.NarutoApplication;

public class ShortcutSuperUtils {

    /**
     * 检查是否可以创建桌面快捷方式
     *
     * @return 是否支持添加
     */
    public static boolean checkCanAddShortcut() {
        boolean re = false;
        Intent intent = new Intent(ShortcutUtils.ACTION_ADD_SHORTCUT);
        List<ResolveInfo> queryList = NarutoApplication.getInstance()
                .getPackageManager().queryBroadcastReceivers(intent, 0);
        if (queryList != null && queryList.size() > 0) {
            re = true;
        }
        return re;
//        // FIXME 由于技术问题，无法准确判断是否支持创建快捷方式，某些机型无发获取到广播接受者，但仍能添加
//        return true;
    }


    /**
     * 检查是否可以创建桌面快捷方式
     *
     * @return 是否支持删除
     */
    public static boolean checkCanRemoveShortcut() {
        boolean re = false;
        Intent intent = new Intent(ShortcutUtils.ACTION_REMOVE_SHORTCUT);
        List<ResolveInfo> queryList = NarutoApplication.getInstance()
                .getPackageManager().queryBroadcastReceivers(intent, 0);
        if (queryList != null && queryList.size() > 0) {
            re = true;
        }
        return re;
//        // FIXME 由于技术问题，无法准确判断是否支持卸载快捷方式，某些机型无发获取到广播接受者，但仍能删除
//        return true;
    }

    /**
     * 判断快捷方式是否存在
     * <p/>
     * 不一定所有的手机都有效，因为国内大部分手机的桌面不是系统原生的<br/>
     * 桌面有两种，系统桌面(ROM自带)与第三方桌面，一般只考虑系统自带<br/>
     * 第三方桌面如果没有实现系统响应的方法是无法判断的，比如GO桌面<br/>
     *
     * @param context context
     * @param title   快捷方式名
     * @param intent  快捷方式Intent
     * @return 是否存在
     */
    public static boolean isShortCutExist(Context context, String title, Intent intent) {
        boolean result = false;
        try {
            ContentResolver cr = context.getContentResolver();
            Uri uri = getUriFromLauncher(context);
            Cursor c = cr.query(uri, new String[]{"title", "intent"}, "title=?  and intent=?",
                    new String[]{title, intent.toUri(0)}, null);
            if (c != null && c.getCount() > 0) {
                result = true;
            }
            if (c != null && !c.isClosed()) {
                c.close();
            }
        } catch (Exception ex) {
            result = false;
        }
        return result;
    }

    private static Uri getUriFromLauncher(Context context) {
        StringBuilder uriStr = new StringBuilder();
        String authority = LauncherUtil.getAuthorityFromPermissionDefault(context);
        if (authority == null || authority.trim().equals("")) {
            authority = LauncherUtil.getAuthorityFromPermission(context, LauncherUtil.getCurrentLauncherPackageName(context) + ".permission.READ_SETTINGS");
        }
        uriStr.append("content://");
        if (TextUtils.isEmpty(authority)) {
            int sdkInt = android.os.Build.VERSION.SDK_INT;
            if (sdkInt < 8) { // Android 2.1.x(API 7)以及以下的
                uriStr.append("com.android.launcher.settings");
            } else if (sdkInt < 19) {// Android 4.4以下
                uriStr.append("com.android.launcher2.settings");
            } else {// 4.4以及以上
                uriStr.append("com.android.launcher3.settings");
            }
        } else {
            uriStr.append(authority);
        }
        uriStr.append("/favorites?notify=true");
        return Uri.parse(uriStr.toString());
    }

}
