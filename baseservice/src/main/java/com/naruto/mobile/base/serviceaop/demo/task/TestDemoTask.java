package com.naruto.mobile.base.serviceaop.demo.task;

import android.util.Log;

public class TestDemoTask implements Runnable {

    private static final String TAG = TestDemoTask.class.getSimpleName();
    @Override
    public void run() {
        // TODO: 17-8-8 做一些异步的任务
        Log.d("pipeLineTasks", "框架内TestDemoTask called!");

    }
}
