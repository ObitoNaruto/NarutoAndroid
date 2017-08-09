
package com.naruto.mobile.h5container.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

public class NetworkUtil {
    public static final String TAG = "NetworkUtil";

    public interface NetworkListener {
        public void onNetworkChanged(NetworkType ot, NetworkType nt);
    };

    public static enum NetworkType {
        WIFI, MOBILE_FAST, MOBILE_MIDDLE, MOBILE_SLOW, NONE,
    }

    private NetworkType type;
    private NetworkListener listener;
    private Context context;

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            updateNetwork();
        }
    };

    public NetworkUtil(Context ctx) {
        type = NetworkType.NONE;
        context = ctx.getApplicationContext();
    }

    public void register() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(receiver, filter);
    }

    public void unregister() {
        try {
            context.unregisterReceiver(receiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public final synchronized NetworkType getNetworkType() {
        updateNetwork();
        return type;
    }

    public final void setListener(NetworkListener l) {
        listener = l;
    }

    private final void updateNetwork() {
        NetworkInfo networkInfo = getNetworkInfo();
        NetworkType t = type;
        type = checkType(networkInfo);
        if (type != t && listener != null) {
            listener.onNetworkChanged(t, type);
        }
    }

    private final synchronized NetworkInfo getNetworkInfo() {
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        return manager.getActiveNetworkInfo();
    }

    private static NetworkType checkType(NetworkInfo info) {
        if (info == null || !info.isConnected()) {
            return NetworkType.NONE;
        }

        int type = info.getType();
        int subType = info.getSubtype();
        if ((type == ConnectivityManager.TYPE_WIFI)
                || (type == ConnectivityManager.TYPE_ETHERNET)) {
            return NetworkType.WIFI;
        }

        if (type == ConnectivityManager.TYPE_MOBILE) {
            switch (subType) {
                case TelephonyManager.NETWORK_TYPE_GPRS:
                case TelephonyManager.NETWORK_TYPE_EDGE:
                case TelephonyManager.NETWORK_TYPE_CDMA:
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    return NetworkType.MOBILE_SLOW; // 2G

                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    return NetworkType.MOBILE_MIDDLE;// 3G

                case TelephonyManager.NETWORK_TYPE_LTE:
                    return NetworkType.MOBILE_FAST; // 4G
            }
        }

        return NetworkType.NONE;
    }
}
