package com.naruto.mobile.framework.rpc.myhttp.utils;


import org.apache.http.HttpHost;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 网络工具
 */
public class NetworkUtils {

    /**
     * 获取网络代理
     * 
     * @return 网络代理
     */
    @SuppressWarnings("deprecation")
	public static HttpHost getProxy(Context context) {
		HttpHost proxy = null;
		NetworkInfo ni = getActiveNetworkInfo(context);
		if (ni != null && ni.isAvailable() && ni.getType() == ConnectivityManager.TYPE_MOBILE) {
			String proxyHost = android.net.Proxy.getDefaultHost();
			int port = android.net.Proxy.getDefaultPort();
			if (proxyHost != null)
				proxy = new HttpHost(proxyHost, port);
		}

		return proxy;
	}

	public static NetworkInfo getActiveNetworkInfo(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		return connectivityManager.getActiveNetworkInfo();
	}
	
    /**
     * 网络类型
     */
    public static int getNetType(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
            .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = connectivityManager.getActiveNetworkInfo();
        if (ni == null)
            return -1;
        return ni.getType();
    }

    /**
     * 检测网络是否可用
     * @param context
     * @return
     */
	public static boolean isNetworkAvailable(Context context) {
		boolean isNetworkAvailable = false;
		ConnectivityManager connectivity = (ConnectivityManager) (context.getSystemService(Context.CONNECTIVITY_SERVICE));
		NetworkInfo[] networkInfos = connectivity.getAllNetworkInfo();
		if (networkInfos == null)
			return false;
		
		for (NetworkInfo itemInfo : networkInfos) {
			if (itemInfo != null) {
				if (itemInfo.getState() == NetworkInfo.State.CONNECTED
						|| itemInfo.getState() == NetworkInfo.State.CONNECTING) {
					isNetworkAvailable = true;
					break;
				}
			}
		}

		return isNetworkAvailable;
	}
}
