/**
 * 
 */
package com.naruto.mobile.base.framework;

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
 * @author sanping.li@alipay.com
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
//        	this.mExecutorService = null; 没有必要 by 海通
        }
    }

    protected synchronized void scheduleNext() {
        if ((mActive = mTasks.poll()) != null) {
            mExecutorService.execute(mActive);
        }
    }

    private final ThreadFactory THREADFACTORY = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(0);//by 海通

        public Thread newThread(Runnable r) {
            if (mName == null)
                mName = TAG;//"SerialWorker"; by 海通
            return new Thread(r, mName + "  #" + mCount.incrementAndGet());//by 海通
        }
    };
}
