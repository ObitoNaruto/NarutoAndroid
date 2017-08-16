package com.naruto.mobile.base.serviceaop.demo.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.naruto.mobile.base.rpc.volley.demo.Test;
import com.naruto.mobile.base.serviceaop.NarutoApplication;

public class TestDemoBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = TestDemoBroadcastReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: 17-8-8 处理广播
        Log.d(TAG, "onReceive called!" + TAG);
        Toast.makeText(NarutoApplication.getInstance().getApplicationContext(), "框架广播 " + TAG + " called!", Toast.LENGTH_LONG).show();
    }
}
