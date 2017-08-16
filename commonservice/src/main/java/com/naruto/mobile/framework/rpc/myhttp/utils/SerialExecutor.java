package com.naruto.mobile.framework.rpc.myhttp.utils;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 顺序执行的任务执行器
 * 任务会先被存入<code>ArrayBlockingQueue</code>执行队列,如果超过执行队列的长度，新任务不会进队列，并且任何异常和返回值。
 * 另，新任务进队列，不是串行的，执行串行的。
 *
 */
public class SerialExecutor implements Executor {
	
	public static final String TAG = "SerialExecutor";
	
    private String mName;
    private ExecutorService mExecutorService;
    private BlockingQueue<Runnable> mTasks;
    private Runnable mActive;

    public SerialExecutor() {
        mTasks = new ArrayBlockingQueue<Runnable>(128);
        mExecutorService = Executors.newSingleThreadExecutor(THREADFACTORY);
    }

    public SerialExecutor(String name) {
        this();
        mName = name;
    }

    @Override
    public void execute(final Runnable command) {
        //offer: 将指定元素插入此队列中（如果立即可行且不会违反容量限制），成功时返回 true，如果当前没有可用的空间，则返回 false，不会抛异常：
        //http://blog.csdn.net/wei_ya_wen/article/details/19344939
        mTasks.offer(new Runnable() {
            public void run() {
                try {
                    command.run();
                } finally {
                    scheduleNext();
                }
            }
        });
        if (mActive == null) {
            scheduleNext();
        }
    }

    public void stop() {
        if (this.mExecutorService != null) {
        	this.mExecutorService.shutdown();//shutdown() 方法拒绝新任务,并在终止前允许执行以前提交的任务
//        	this.mExecutorService = null; 没有必要
        }
    }

    protected synchronized void scheduleNext() {
        //poll()则不会等待，直接返回null
        if ((mActive = mTasks.poll()) != null) {
            mExecutorService.execute(mActive);
        }
    }

    private final ThreadFactory THREADFACTORY = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(0);

        public Thread newThread(Runnable r) {
            if (mName == null)
                mName = TAG;//"SerialWorker";
            return new Thread(r, mName + "  #" + mCount.incrementAndGet());
        }
    };
}
