package com.naruto.mobile.base.serviceaop.task;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.List;

public class PipelineManager {

    /**
     * BroadcastReceiver的列表
     */
    private List<BroadcastReceiver> mReceivers;

    private static LocalBroadcastManager mLocalBroadcastManager;

    private List<TaskContext> mTaskContexts;

    private static PipelineManager mInstance;

    private PipelineManager(Context context) {
        mReceivers = new ArrayList<BroadcastReceiver>();
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(context);
    }

    public static synchronized PipelineManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new PipelineManager(context);
        }
        return mInstance;
    }

//    public void register

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
