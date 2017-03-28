package com.naruto.mobile.framework.common.threadpool;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 事务：使用TransactionExecutor去执行
 */
public abstract class Transaction implements Runnable{
    private static final AtomicInteger sCount = new AtomicInteger(0);

    final String id = "Transaction_" + sCount.getAndIncrement();

    public final String getId() {
        return id;
    }
}
