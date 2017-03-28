package com.naruto.mobile.framework.common.threadpool;

import android.util.Log;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 多线程任务调度执行器
 */
public class AsyncTaskExecutor {

    public static final String TAG = AsyncTaskExecutor.class.getSimpleName();

    /**
     *  -------------------线程池设置-----------------
     */
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();//处理器数量
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;//核心线程个数
    // private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 3 + 1;

    /**
     * 线程池工厂类
     */
    private static final ThreadFactory THREADFACTORY = new ThreadFactory() {
        /**
         * 线程池工厂计数器：用于作为线程名称
         */
        private final AtomicInteger mCount = new AtomicInteger(0);

        /**
         * 创建线程
         */
        @Override
        public Thread newThread(Runnable r) {
            String name = "AsyncTaskExecutor" + "_thread_" + mCount.incrementAndGet();
            Log.w(TAG, "ThreadFactory.newThread(" + name + ")");
            Thread thread = new Thread(r, name);
            thread.setPriority(Thread.MIN_PRIORITY);
            return thread;
        }
    };

    /**
     * 串行执行事务性的任务
     */
    final TransactionExecutor TRANSACTION_EXECUTOR = new TransactionExecutor();

    /**
     * 并行执行：它有自己的线程池，负责execute(..)函数执行的任务的异步逻辑。
     */
    final ThreadPoolExecutor PARALLEL_EXECUTOR =
            (ThreadPoolExecutor) Executors.newCachedThreadPool(THREADFACTORY);

    /**
     * 串行执行：它只负责executeSerially(..)函数执行的任务的串行逻辑，并没有自己的线程池，而是共享了SCHEDULED_EXECUTOR的线程池。
     */
    final SerialExecutor SERIAL_EXECUTOR = new SerialExecutor(PARALLEL_EXECUTOR);

    /**
     * Schedule执行：它有自己的线程池，负责schedule(..),scheduleAtFixedRate(..),scheduleWithFixedDelay(..)函数执行的任务的异步逻辑。
     */
    final ScheduledThreadPoolExecutor SCHEDULED_EXECUTOR =
            (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(CORE_POOL_SIZE, THREADFACTORY);

    /**
     * 单例
     */
    public static AsyncTaskExecutor INSTANCE = new AsyncTaskExecutor();

    /**
     * 构造方法：单例模式，所以访问限制为private
     */
    private AsyncTaskExecutor() {
        // SCHEDULED_EXECUTOR.setMaximumPoolSize(MAXIMUM_POOL_SIZE);
        SCHEDULED_EXECUTOR.setKeepAliveTime(60L, TimeUnit.SECONDS);
        SCHEDULED_EXECUTOR.allowCoreThreadTimeOut(true);
        SCHEDULED_EXECUTOR.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        PARALLEL_EXECUTOR.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    }

    /**
     * 单例模式获取当前对象
     *
     * @return  AsyncTaskExecutor
     */
    public static AsyncTaskExecutor getInstance() {
        return INSTANCE;
    }

    public final Executor getExecutor() {
        return PARALLEL_EXECUTOR;
    }

    /**
     * 请使用<br>
     * executeSerially(Runnable task, String threadName)
     *
     * @param task
     */
    @Deprecated
    public void executeSerially(Runnable task) {
        executeSerially(task, "");
    }

    public void executeSerially(Runnable task, String threadName) {
        Log.v(TAG, "AsyncTaskExecutor.executeSerially(Runnable, threadName=" + threadName + ")");
        SERIAL_EXECUTOR.execute(NamedRunnable.TASK_POOL.obtain(task, threadName));
    }

    /**
     * 请使用<br>
     * execute(Runnable task, String threadName)
     *
     * @param task
     */
    @Deprecated
    public void execute(Runnable task) {
        execute(task, "");
    }

    public void execute(Runnable task, String threadName) {
        Log.v(TAG, "AsyncTaskExecutor.execute(Runnable, threadName=" + threadName + ")");
        PARALLEL_EXECUTOR.execute(NamedRunnable.TASK_POOL.obtain(task, threadName));
    }

    /**
     * 请使用<br>
     * schedule(Runnable task, String threadName, long delay, TimeUnit unit)
     *
     * @param task
     */
    @Deprecated
    public ScheduledFuture<?> schedule(Runnable task, long delay, TimeUnit unit) {
        return schedule(task, "", delay, unit);
    }

    public ScheduledFuture<?> schedule(Runnable task, String threadName, long delay, TimeUnit unit) {
        Log.v(TAG, "AsyncTaskExecutor.schedule(Runnable, threadName=" + threadName + ")");
        return SCHEDULED_EXECUTOR.schedule(NamedRunnable.TASK_POOL.obtain(task, threadName), delay, unit);
    }

    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long initialDelay, long period, TimeUnit unit) {
        Log.v(TAG, "AsyncTaskExecutor.scheduleAtFixedRate(Runnable)");
        return SCHEDULED_EXECUTOR.scheduleAtFixedRate(task, initialDelay, period, unit);
    }

    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, long initialDelay, long delay, TimeUnit unit) {
        return SCHEDULED_EXECUTOR.scheduleWithFixedDelay(task, initialDelay, delay, unit);
    }

    public void shutdown() {
        TRANSACTION_EXECUTOR.shutdown();
        SERIAL_EXECUTOR.shutdown();
        PARALLEL_EXECUTOR.shutdown();
        SCHEDULED_EXECUTOR.shutdown();
    }

    public String addTransaction(Transaction tr) {
        return TRANSACTION_EXECUTOR.addTransaction(tr);
    }

    public void removeTransaction(String id) {
        TRANSACTION_EXECUTOR.removeTransaction(id);
    }
}
