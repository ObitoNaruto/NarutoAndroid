package com.naruto.mobile.base.serviceaop.demo.task;

import android.util.Log;

/**
 */

public class TestDemoTask3 implements Runnable {
    @Override
    public void run() {
        Log.d("pipeLineTasks", "框架内TestDemoTask3 called!");
    }
}
