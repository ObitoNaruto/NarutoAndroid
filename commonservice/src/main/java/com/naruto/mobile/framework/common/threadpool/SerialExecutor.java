package com.naruto.mobile.framework.common.threadpool;

import android.util.Log;

import java.util.concurrent.Executor;

//串行执行：它只负责executeSerially(..)函数执行的任务的串行逻辑，并没有自己的线程池，
//* 而是共享了{@link com.android.mobile.mywealth.asynctask.asynctaskExecutor.AsyncTaskExecutor.SCHEDULED_EXECUTOR}的线程池。
public class SerialExecutor extends StandardPipeline {

    static final String TAG = "SerialExecutor";

    public SerialExecutor(Executor executor) {
        super(executor);
    }

    /**
     * 执行函数
     *
     * @param task AsyncTask
     */
    public void execute(final NamedRunnable task) {
        Log.v(TAG, "SerialExecutor.execute()");
        addTask(task);
        start();
    }

    /**
     * 关闭执行器
     */
    public void shutdown() {
        stop();
        mTasks.clear();
        mTasks = null;
    }
}

