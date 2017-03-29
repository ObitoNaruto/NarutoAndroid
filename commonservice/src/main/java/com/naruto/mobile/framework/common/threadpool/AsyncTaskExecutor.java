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
        SCHEDULED_EXECUTOR.setKeepAliveTime(60L, TimeUnit.SECONDS);//当线程数大于核心时，此为终止前多余的空闲线程等待新任务的最长时间
        SCHEDULED_EXECUTOR.allowCoreThreadTimeOut(true);//允许线程数低于corePoolSize时，线程也因为空闲而终止
        SCHEDULED_EXECUTOR.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());//?找时间仔细研究一下并发线程池的技术

        PARALLEL_EXECUTOR.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());//?
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

    //schedule和scheduleAtFixedRate的区别在于：如果指定开始执行的时间在当前系统运行时间之前，
    // scheduleAtFixedRate会把已经过去的时间也作为周期执行，而schedule不会把过去的时间算上
    //举个例子，你有一个任务如果运行完需要8秒，而你设置30秒执行一次的话schedule和scheduleAtFixedRate的效果是一样的都是30秒执行一次。
    //如果你设置4秒执行一次，那么schedule执行第二次要等第一次执行完成，也就是说实际是8秒执行一次。
    // 但是要是用scheduleAtFixedRate 的话第二次执行的时候第一次还没有执行完成，因此是严格的4秒执行一次。
    // （注意这里的“执行”其实是另外开辟线程，因此之前的有没有运行完并不影响下一次的运行）
    public ScheduledFuture<?> schedule(Runnable task, String threadName, long delay, TimeUnit unit) {
        Log.v(TAG, "AsyncTaskExecutor.schedule(Runnable, threadName=" + threadName + ")");
        return SCHEDULED_EXECUTOR.schedule(NamedRunnable.TASK_POOL.obtain(task, threadName), delay, unit);
    }

    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long initialDelay, long period, TimeUnit unit) {
        Log.v(TAG, "AsyncTaskExecutor.scheduleAtFixedRate(Runnable)");
        return SCHEDULED_EXECUTOR.scheduleAtFixedRate(task, initialDelay, period, unit);
    }

    /**
     *
     scheduleWithFixedDelay从字面意义上可以理解为就是以固定延迟（时间）来执行线程任务，它实际上是不管线程任务的执行时间的，每次都要把任务执行完成后再延迟固定时间后再执行下一次。
     而scheduleFixedRate呢，是以固定频率来执行线程任务，固定频率的含义就是可能设定的固定时间不足以完成线程任务，但是它不管，达到设定的延迟时间了就要执行下一次了。
     */
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
