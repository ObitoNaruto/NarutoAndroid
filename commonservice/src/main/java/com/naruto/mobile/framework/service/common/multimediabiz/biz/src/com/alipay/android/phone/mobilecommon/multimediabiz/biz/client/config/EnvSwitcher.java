package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.config;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import org.apache.http.client.HttpClient;

import java.security.MessageDigest;

import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.util.DjangoUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.util.Hex;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.util.MD5Utils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.AppUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Logger;

/**
 * Django环境切换工具类
 * Created by jinmin on 15/8/20.
 */
public class EnvSwitcher {

    private static final String ENV_SETTING_URI = "content://com.alipay.setting/mtdServiceUrl";

    public static final int ENV_CASE_ONLINE = 0;
    public static final int ENV_CASE_PRE = 1;
    public static final int ENV_CASE_DAILY = 2;

    public static final int ENV_CASE = getEnv(AppUtils.getApplicationContext(), ENV_CASE_ONLINE);

    private static String APP_KEY = "aliwallet";

    public static final int getEnv(Context context, int defaultVal) {
        return AppUtils.isDebug(context) ? getValue(context, ENV_SETTING_URI, defaultVal) : defaultVal;
    }

    public static int getValue(Context context, String uri, int defaultVal) {
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(Uri.parse(uri), null, null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                int ret = cursor.getInt(0);
                Logger.D("EnvSwitcher", "getValue " + uri + ": " + ret + ", defaultVal: " + defaultVal);
                return ret;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        Logger.D("EnvSwitcher", "getValue fail return " + defaultVal);
        return defaultVal;
    }

    public static boolean isEnableHost2Ip() {
        return ENV_CASE == ENV_CASE_ONLINE;
    }

    public static boolean enableSpdyDebug() {
        return isEnableHost2Ip();
    }

    public static Env getCurrentEnv() {
        //上线前注掉
        switch (ENV_CASE) {
            case ENV_CASE_ONLINE:
                return Env.ONLINE;
            case ENV_CASE_DAILY:
                return Env.DAILY;
            case ENV_CASE_PRE:
                return Env.PRE_RELEASE;
        }
        //end
        return Env.ONLINE;
    }

    public static String getSignature(long timestamp) {
        //上线前注掉
        switch (ENV_CASE) {
            case ENV_CASE_ONLINE:
            case ENV_CASE_PRE:
                return DjangoUtils.genSignature(getAppKey(), timestamp);
            case ENV_CASE_DAILY:
                return genSignature(getAppKey(), timestamp, "0846ea8b62e145c1a25bbffd490f2901");
        }
        //end
        return DjangoUtils.genSignature(getAppKey(), timestamp);
    }

    public static String getAclString(String id, String timestamp, ConnectionManager<HttpClient> connectionManager) {
        //上线前注掉
        switch (ENV_CASE) {
            case ENV_CASE_DAILY: {
                StringBuffer sb = new StringBuffer();
                sb.append(id)
                        .append(timestamp)
                        .append(connectionManager.getUid())
                        .append(connectionManager.getAcl())
                        .append("0846ea8b62e145c1a25bbffd490f2901");
                String acl = MD5Utils.getMD5String(sb.toString());
                return acl;
            }
        }
        //end
        StringBuffer sb = new StringBuffer();
        sb.append(id)
                .append(timestamp)
                .append(connectionManager.getUid())
                .append(connectionManager.getAcl());
        return DjangoUtils.genSignature(connectionManager.getAppKey(), sb.toString());
    }

    public static String getAppKey() {
        return APP_KEY;
    }

    /***********************************  以下代码只是为了调试，上线前需要注释掉   **************************************************/
    /**
     * @param appKey
     * @param timestamp
     * @param appSecret
     * @return
     */
    public static String genSignature(String appKey, Long timestamp, String appSecret) {
        MessageDigest md = MD5Utils.getMD5Digest();
        md.update(appKey.getBytes());
        md.update(String.valueOf(timestamp).getBytes());
        md.update(appSecret.getBytes());

        return new String(Hex.encodeHex(md.digest()));
    }

}
