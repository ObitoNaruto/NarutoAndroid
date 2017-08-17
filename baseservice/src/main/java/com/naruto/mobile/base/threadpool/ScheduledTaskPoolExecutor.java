package com.naruto.mobile.base.threadpool;

import java.util.List;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

public class ScheduledTaskPoolExecutor extends ScheduledThreadPoolExecutor {

    public ScheduledTaskPoolExecutor(int corePoolSize,
            ThreadFactory threadFactory, RejectedExecutionHandler handler) {

        super(corePoolSize, threadFactory, handler);
    }

    @Override
    public void shutdown() {
        // super.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return null; // super.shutdownNow();
    }

}
