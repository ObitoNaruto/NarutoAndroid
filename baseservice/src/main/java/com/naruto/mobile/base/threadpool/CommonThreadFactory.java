package com.naruto.mobile.base.threadpool;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class CommonThreadFactory implements ThreadFactory {

    private final AtomicInteger mCount = new AtomicInteger(1);
    private final String threadNamePrefix;
    private final int priority;

    public CommonThreadFactory(String threadNamePrefix, final int priority) {
        this.threadNamePrefix = threadNamePrefix;
        this.priority = priority;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r, threadNamePrefix
                + mCount.getAndIncrement());
        thread.setPriority(this.priority);
        return thread;
    }

}
