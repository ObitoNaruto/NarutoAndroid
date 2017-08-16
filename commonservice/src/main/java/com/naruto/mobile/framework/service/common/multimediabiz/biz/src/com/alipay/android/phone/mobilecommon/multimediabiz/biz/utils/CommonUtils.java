package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;

/**
 * 通用工具类
 * Created by jinmin on 15/5/25.
 */
public class CommonUtils {
    private static final Uri PREFERRED_APN_URI = Uri.parse("content://telephony/carriers/preferapn");
    // apn属性类型
    public static final String APN_PROP_APN = "apn";
    // apn属性代理
    public static final String APN_PROP_PROXY = "proxy";
    // apn属性端口
    public static final String APN_PROP_PORT = "port";
    /**
     * Determine whether the active network
     *
     * @return true yes | false no
     */
    public static boolean isActiveNetwork(Context context) {

        if (context == null) {
            return false;
        }

        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm != null){
            NetworkInfo info = cm.getActiveNetworkInfo();
            if (info != null && info.isAvailable()) {
                return true;
            }
        }

        return false;
    }

    public static String getNetStatus(Context context) {
        if (context.checkCallingOrSelfPermission("android.permission.ACCESS_NETWORK_STATE") == PackageManager.PERMISSION_DENIED) {
            return "";
        }
        ConnectivityManager connectivitymanager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkinfo = connectivitymanager.getActiveNetworkInfo();
        if (networkinfo == null) {
            return "";
        }

        if (networkinfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return "wifi";
        }

        String netInfo = networkinfo.getExtraInfo();
        if (TextUtils.isEmpty(netInfo)) {
            return "";
        }
        netInfo = netInfo.toLowerCase();
        Logger.D("NetTag", "getNetStatus netInfo="+netInfo);
        return netInfo;
    }

    public static boolean isWapNetWork() {
        boolean ret = false;
        Context context = AppUtils.getApplicationContext();

        String netInfo = getNetStatus(context);

        if(TextUtils.isEmpty(netInfo)){
            Logger.D("NetTag", "getNetStatus netInfo=null "+";ret="+ret);
            return ret;
        }

        if(netInfo.equalsIgnoreCase("cmwap") ||netInfo.equalsIgnoreCase("3gwap")
                || netInfo.equalsIgnoreCase("uniwap") || netInfo.equalsIgnoreCase("ctwap")
                || netInfo.equalsIgnoreCase("wap")){
            ret = true;
        }

        // cdma
        if(netInfo.startsWith("#777")){
            String proxy = getApnProxy(context);
            if (proxy != null && proxy.length() > 0) {
                //ctwap
                ret = true;
            } else {
                ret = false;
            }
        }
        Logger.D("NetTag", "getNetStatus netInfo=" + netInfo + ";ret=" + ret);
        return ret;
    }

    /**
     * 获取系统APN代理IP
     * @param context
     * @return
     */
    public static String getApnProxy(Context context) {
        Cursor c = null;
        String strResult = null;
        try{
            c = context.getContentResolver().query(PREFERRED_APN_URI, null, null, null, null);
            if (c == null) {
                return strResult;
            }
            c.moveToFirst();
            if (c.isAfterLast()) {
                return strResult;
            }
            strResult = c.getString(c.getColumnIndex(APN_PROP_PROXY));
        }catch (Exception e){
            //  ignore exception
        }finally {
            if(c != null){
                c.close();
            }
        }

        return strResult;
    }


    public static HttpHost getProxy() {
        Context context = AppUtils.getApplicationContext();
        HttpHost proxy = null;
        try{
            ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                NetworkInfo ni = connectivityManager.getActiveNetworkInfo();
                if (ni != null && ni.isAvailable()) {
                    String proxyHost = android.net.Proxy.getDefaultHost();
                    int port = android.net.Proxy.getDefaultPort();
                    if (proxyHost != null)
                        proxy = new HttpHost(proxyHost, port);
                }
            }
        }catch (Exception e){
            //ignore
        }

        return proxy;
    }

    /**
     * Determine whether the active wifi network
     *
     * @return true yes | false no
     */
    public static boolean isWifiNetwork() {
        Context context = AppUtils.getApplicationContext();
        if (context == null) {
            return false;
        }

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager != null){
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo != null
                    && ConnectivityManager.TYPE_WIFI == activeNetworkInfo.getType()
                    //&& activeNetworkInfo.isAvailable()
                    //&& activeNetworkInfo.isConnected()
                    /*&& !isWifiOriginFromMobileNetwork()*/) {
                return true;
            }
        }

        return false;
    }

    /**
     * 根据http的错误返回码决定是否需要重试,主要针对django请求的业务
     * @param code
     * @return
     */
    public static boolean isNeedRetry(int code){
        return code == HttpStatus.SC_FORBIDDEN || code == HttpStatus.SC_BAD_GATEWAY;
    }

}
