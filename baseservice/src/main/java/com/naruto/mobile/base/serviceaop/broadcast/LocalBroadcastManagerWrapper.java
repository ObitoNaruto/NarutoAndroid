package com.naruto.mobile.base.serviceaop.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.List;

public class LocalBroadcastManagerWrapper {

    /**
     * BroadcastReceiver的列表
     */
    private List<BroadcastReceiver> mReceivers;

    //android系统广播管理器
    private static LocalBroadcastManager mLocalBroadcastManager;

    private static LocalBroadcastManagerWrapper mInstance;

    private LocalBroadcastManagerWrapper(Context context) {
        mReceivers = new ArrayList<BroadcastReceiver>();
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(context);
    }

    public static synchronized LocalBroadcastManagerWrapper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new LocalBroadcastManagerWrapper(context);
        }
        return mInstance;
    }

    /**
     * 注册广播
     */
    public void registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        //内存缓存广播接收者
        mReceivers.add(receiver);
        //注册广播接收者
        mLocalBroadcastManager.registerReceiver(receiver, filter);
    }

    /**
     * 反注册广播接收者
     * @param receiver
     */
    public void unregisterReceiver(BroadcastReceiver receiver) {
        mLocalBroadcastManager.unregisterReceiver(receiver);
    }

    /**
     * 反注册框架中所有的广播接收者
     */
    public void close() {
        for (BroadcastReceiver receiver : mReceivers) {
            if (receiver != null) {
                mLocalBroadcastManager.unregisterReceiver(receiver);
            }
        }
        mReceivers.clear();
    }
}
