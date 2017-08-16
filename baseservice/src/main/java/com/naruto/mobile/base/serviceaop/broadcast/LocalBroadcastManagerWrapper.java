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

    public void registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        mReceivers.add(receiver);
        mLocalBroadcastManager.registerReceiver(receiver, filter);
    }

    public void unregisterReceiver(BroadcastReceiver receiver) {
        mLocalBroadcastManager.unregisterReceiver(receiver);
    }

    public void close() {
        for (BroadcastReceiver receiver : mReceivers) {
            if (receiver != null) {
                mLocalBroadcastManager.unregisterReceiver(receiver);
            }
        }
        mReceivers.clear();
    }
}
