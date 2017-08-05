package com.naruto.mobile.framework.biz.ext.shortCut.impl;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;

import java.util.List;

public class LauncherUtil {

    private static String mBufferedValue = null;

    /**
     * default permission is "com.android.launcher.permission.READ_SETTINGS"<br/>
     * {@link #getAuthorityFromPermission(Context, String)}<br/>
     *
     * @param context context
     */
    public static String getAuthorityFromPermissionDefault(Context context) {
        if (TextUtils.isEmpty(mBufferedValue))//we get value buffered
            mBufferedValue = getAuthorityFromPermission(context, "com.android.launcher.permission.READ_SETTINGS");
        return mBufferedValue;
    }

    /**
     * be cautious to use this, it will cost about 500ms 此函数为费时函数，大概占用500ms左右的时间<br/>
     * android系统桌面的基本信息由一个launcher.db的Sqlite数据库管理，里面有三张表<br/>
     * 其中一张表就是favorites。这个db文件一般放在data/data/com.android.launcher(launcher2)文件的databases下<br/>
     * 但是对于不同的rom会放在不同的地方<br/>
     * 例如MIUI放在data/data/com.miui.home/databases下面<br/>
     * htc放在data/data/com.htc.launcher/databases下面<br/
     *
     * @param context    context
     * @param permission 读取设置的权限  READ_SETTINGS_PERMISSION
     * @return permission
     */
    public static String getAuthorityFromPermission(Context context, String permission) {
        if (TextUtils.isEmpty(permission)) {
            return "";
        }
        try {
            List<PackageInfo> packs = context.getPackageManager().getInstalledPackages(PackageManager.GET_PROVIDERS);
            if (packs == null) {
                return "";
            }
            for (PackageInfo pack : packs) {
                ProviderInfo[] providers = pack.providers;
                if (providers != null) {
                    for (ProviderInfo provider : providers) {
                        if (permission.equals(provider.readPermission) || permission.equals(provider.writePermission)) {
                            return provider.authority;
                        }
                        String authority = provider.authority;
                        if (!TextUtils.isEmpty(authority)
                                && (authority
                                .contains(".launcher.settings")
                                || authority
                                .contains(".twlauncher.settings") || authority
                                .contains(".launcher2.settings")))
                            return authority;
                    }
                }
            }
        } catch (Exception e) {
//            LoggerFactory.getTraceLogger().warn(ShortCutServiceImpl.TAG, e);
        }
        return "";
    }

    /**
     * get the current Launcher's Package Name
     */
    public static String getCurrentLauncherPackageName(Context context) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo res = context.getPackageManager().resolveActivity(intent, 0);
        if (res == null || res.activityInfo == null) {
            // should not happen. A home is always installed, isn't it?
            return "";
        }
        if (res.activityInfo.packageName.equals("android")) {
            return "";
        } else {
            return res.activityInfo.packageName;
        }
    }

}
