package com.naruto.mobile.framework.service.common.impl;

import android.app.Application;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.naruto.mobile.base.serviceaop.NarutoApplication;
import com.naruto.mobile.framework.common.threadpool.AsyncTaskExecutor;
import com.naruto.mobile.framework.common.threadpool.CommonThreadFactory;
import com.naruto.mobile.framework.common.threadpool.LifoBlockingDeque;
import com.naruto.mobile.framework.common.threadpool.OrderedExecutor;
import com.naruto.mobile.framework.common.threadpool.ProcessCpuTracker;
import com.naruto.mobile.framework.common.threadpool.ScheduledTaskPoolExecutor;
import com.naruto.mobile.framework.common.threadpool.TaskPoolExecutor;
import com.naruto.mobile.framework.service.common.TaskScheduleService;

import org.json.JSONObject;

public class TaskScheduleServiceImpl extends TaskScheduleService {

    private final static String TAG = TaskScheduleService.class.getSimpleName();
    private final static int CPU_NUMBER = Runtime.getRuntime().availableProcessors();

    private final static String PIPELINE_NAME = TaskScheduleServiceImpl.class.getName();
    private final static long PIPELINE_TIMEOUT = TimeUnit.SECONDS.toMillis(10);
    private final static int IDLE_TIMEOUT = 180; // 秒
    private final static int IDLE_CHECK_PERIOD = 10; // 秒
    private final static int CPU_IDLE_PERCENT = 60; // 百分之
    private final static int CPU_IDLE_COUNT = 2; // 次

    private PoolCfg ioPoolCfg;
    private PoolCfg urgentPoolCfg;
    private PoolCfg normalPoolCfg;
    private PoolCfg rpcPoolCfg;
    private PoolCfg mmsHttpPoolCfg;
    private PoolCfg mmsDjangoPoolCfg;
    private PoolCfg orderedThreadPoolCfg;

    private AsyncTaskExecutor mAsyncTaskExecutor;
    private ScheduledFuture<?> idleCheckTaskFuture;
    private HashMap<ScheduleType, ThreadPoolExecutor> executorsMap =
            new HashMap<ScheduleType, ThreadPoolExecutor>();
    private ScheduledThreadPoolExecutor scheduledExecutor;
    private ThreadPoolExecutor orderedExecutorCore;
    private OrderedExecutor<String> orderedExecutor;
    private int allPipelineFinished;
    private boolean isExecuteIdleTasks;

    public TaskScheduleServiceImpl() {
        mAsyncTaskExecutor = AsyncTaskExecutor.getInstance();
        initializeThreadPools();
        acquireScheduledExecutor().schedule(new Runnable() { // 这是兜底的用于执行IDLE Task的任务
            public void run() {
                if (idleCheckTaskFuture != null) { // 到了这里不判断任何条件，强行cancel
                    try {
                        idleCheckTaskFuture.cancel(true);
                    } catch (Throwable t) {
                        Log.e(TAG, "cancel check idle", t);
                    }
                }
                Log.i(TAG, "executeIdleTasks() Reason: timeout");
//                executeIdleTasks();
            }
        }, IDLE_TIMEOUT /* 超时后无条件执行 */, TimeUnit.SECONDS);
    }

    @Override
    public void serialExecute(Runnable command) {
        mAsyncTaskExecutor.executeSerially(command);
    }

    @Override
    public void parallelExecute(Runnable command) {
        mAsyncTaskExecutor.execute(command);
    }

    @Override
    public void serialExecute(Runnable command, String threadName) {
        mAsyncTaskExecutor.executeSerially(command, threadName);
    }

    @Override
    public void parallelExecute(Runnable command, String threadName) {
        mAsyncTaskExecutor.execute(command, threadName);
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable task, String threadName, long delay, TimeUnit unit) {
        return mAsyncTaskExecutor.schedule(task, threadName, delay, unit);
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, String threadName, long initialDelay, long period, TimeUnit unit) {
        return mAsyncTaskExecutor.scheduleAtFixedRate(task, initialDelay, period, unit);
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, String threadName, long initialDelay, long delay, TimeUnit unit) {
        return mAsyncTaskExecutor.scheduleWithFixedDelay(task, initialDelay, delay, unit);
    }

    @Override
    protected void onCreate(Bundle params) {
    }

    @Override
    protected void onDestroy(Bundle params) {
        if (mAsyncTaskExecutor != null) {
            try {
                mAsyncTaskExecutor.shutdown();
            } catch (Throwable t) {
                Log.w(TAG, t);
            }
        }
        if (scheduledExecutor != null) {
            try {
                scheduledExecutor.shutdown();
            } catch (Throwable t) {
                Log.w(TAG, t);
            }
        }
        if (orderedExecutorCore != null) {
            try {
                orderedExecutorCore.shutdown();
            } catch (Throwable t) {
                Log.w(TAG, t);
            }
        }
        synchronized (executorsMap) {
            for (ThreadPoolExecutor excutor : executorsMap.values()) {
                try {
                    excutor.shutdown();
                } catch (Throwable t) {
                    Log.w(TAG, t);
                }
            }
        }
    }

    @Override
    public String addTransaction(Transaction transaction) {
        return mAsyncTaskExecutor.addTransaction(transaction);
    }

    @Override
    public void removeTransaction(String id) {
        mAsyncTaskExecutor.removeTransaction(id);
    }

    /**
     * 线程池配置
     */
    public final static class PoolCfg {
        // 服务端下发的配置值，从ConfigService中获取。
        // 默认值是-1，用来判断是否下发了此字段，好赋值给相应的变量。
        public int pushed_coreSize = -1;
        public int pushed_maxSize = -1;
        public int pushed_workQueue = -1;
        public int pushed_keepAlive = -1;

        public int coreSize;
        public int maxSize;
        public int keepAlive;
        public TimeUnit timeunit = TimeUnit.SECONDS; // default
        public boolean allowCoreTimeout = true;
        public BlockingQueue<Runnable> workQueue;
        public ThreadFactory factory;
        public RejectedExecutionHandler rejectHandler;

        @Override
        public String toString() {
            return new StringBuilder("PoolCfg{").
                    append("pushed_coreSize=").append(pushed_coreSize).
                    append("pushed_maxSize=").append(pushed_maxSize).
                    append("pushed_workQueue=").append(pushed_workQueue).
                    append("pushed_keepAlive=").append(pushed_keepAlive).
                    append("coreSize=").append(coreSize).
                    append(",maxSize=").append(maxSize).
                    append(",keepAlive=").append(keepAlive).
                    append(",timeunit=").append(timeunit).
                    append(",allowCoreTimeout=").append(allowCoreTimeout).
                    append(",workQueueSize=").append(workQueue==null ? 0 : workQueue.size()).
                    append(",factory=").append(factory !=null ? factory.getClass().getName() : "null").
                    append(",rejectHandler=").append(rejectHandler !=null ? rejectHandler.getClass().getName() : "null").
                    append("}").toString();
        }
    }

    private void initializeThreadPools() {
        ioPoolCfg = new PoolCfg();
        urgentPoolCfg = new PoolCfg();
        normalPoolCfg = new PoolCfg();
        rpcPoolCfg = new PoolCfg();
        mmsHttpPoolCfg = new PoolCfg();
        mmsDjangoPoolCfg = new PoolCfg();
        orderedThreadPoolCfg = new PoolCfg();

        try {
            initializePoolCfgs();
        } catch (Throwable t) {
            Log.e(TAG, "initializeThreadPools", t);
        }

        ioPoolCfg = createIoPoolCfg(ioPoolCfg);
        urgentPoolCfg = createUrgentPoolCfg(urgentPoolCfg);
        normalPoolCfg = createNormalPoolCfg(normalPoolCfg);
        rpcPoolCfg = createRpcPoolCfg(rpcPoolCfg);
        mmsHttpPoolCfg = createMmsPoolCfg(mmsHttpPoolCfg, "HTTP");
        mmsDjangoPoolCfg = createMmsPoolCfg(mmsDjangoPoolCfg, "DJANGO");
        orderedThreadPoolCfg = createOrderedThreadPoolCfg(orderedThreadPoolCfg, "ORDERED");
    }

    private void initializePoolCfgs() throws Exception {
        Application application = NarutoApplication.getInstance();
        if (application == null) {
            Log.e(TAG, "initializePoolCfgs: application is NULL");
            return;
        }
        //// TODO: 17-3-29 获取配置的各种级别的pool的配置值json字符串
        String poolConfigJson = "";
//        String poolConfigJson = SharedSwitchUtil.getSharedSwitch(
//                application, SharedSwitchUtil.THREAD_POOL_CONFIG);
//        if (TextUtils.isEmpty(poolConfigJson)) {
//            return;
//        }

        JSONObject poolConfigObject = new JSONObject(poolConfigJson);
        setValueFromJson(ioPoolCfg, poolConfigObject, "io");
        setValueFromJson(urgentPoolCfg, poolConfigObject, "urgent");
        setValueFromJson(normalPoolCfg, poolConfigObject, "normal");
        setValueFromJson(rpcPoolCfg, poolConfigObject, "rpc");
        setValueFromJson(mmsHttpPoolCfg, poolConfigObject, "mmsHttp");
        setValueFromJson(mmsDjangoPoolCfg, poolConfigObject, "mmsDjango");
        setValueFromJson(orderedThreadPoolCfg, poolConfigObject, "ordered");
    }

    private static void setValueFromJson(PoolCfg poolCfg, JSONObject jsonObject, String key) {
        if (poolCfg == null || jsonObject == null || TextUtils.isEmpty(key) || !jsonObject.has(key)) {
            return;
        }
        try {
            JSONObject keyObject = jsonObject.getJSONObject(key);
            poolCfg.pushed_coreSize = setValueFromJson(keyObject, "coreSize", poolCfg.pushed_coreSize);
            poolCfg.pushed_maxSize = setValueFromJson(keyObject, "maxSize", poolCfg.pushed_maxSize);
            poolCfg.pushed_workQueue = setValueFromJson(keyObject, "queueSize", poolCfg.pushed_workQueue);
            poolCfg.pushed_keepAlive = setValueFromJson(keyObject, "keepAlive", poolCfg.pushed_keepAlive);
        } catch (Throwable t) {
            Log.e(TAG, "setValueFromJson: " + key, t);
        }
    }

    private static int setValueFromJson(JSONObject jsonObject, String key, int defaultValue) {
        if (jsonObject == null || TextUtils.isEmpty(key) || !jsonObject.has(key)) {
            return defaultValue;
        }
        try {
            return jsonObject.getInt(key);
        } catch (Throwable t) {
            Log.e(TAG, "setValueFromJson: " + key, t);
            return defaultValue;
        }
    }

    /**
     * 该Executor池子core很小，线程数上限小，队列大，线程优先级低，保活时间长
     * @return
     */
    private static PoolCfg createIoPoolCfg(PoolCfg cfg) {
        if (cfg.pushed_coreSize < 0) {
            if (CPU_NUMBER <= 2) {
                cfg.coreSize = CPU_NUMBER;
            } else {
                cfg.coreSize = CPU_NUMBER;
            }
        } else {
            cfg.coreSize = Math.max(CPU_NUMBER, cfg.pushed_coreSize);
        }
        if (cfg.pushed_maxSize < 0) {
            if (CPU_NUMBER <= 2) {
                cfg.maxSize = Math.max(cfg.coreSize, CPU_NUMBER + 1);
            } else {
                cfg.maxSize = Math.max(cfg.coreSize, CPU_NUMBER * 2);
            }
        } else {
            cfg.maxSize = Math.max(cfg.coreSize, cfg.pushed_maxSize);
        }
        if (cfg.pushed_keepAlive < 0) {
            cfg.keepAlive = 45;
        } else {
            cfg.keepAlive = cfg.pushed_keepAlive;
        }
        if (cfg.pushed_workQueue < 0) {
            cfg.workQueue = new LinkedBlockingQueue<Runnable>(); // 无上限队列
        } else {
            cfg.workQueue = new LinkedBlockingQueue<Runnable>(cfg.pushed_workQueue);
        }
        cfg.rejectHandler = new ThreadPoolExecutor.DiscardOldestPolicy();
        cfg.factory = new CommonThreadFactory("IO_THREAD_", Thread.MIN_PRIORITY);
        return cfg;
    }

    /**
     * 该Executor池子core相对大，无线程数上限，队列小，线程优先级正常，保活时间短
     * @return
     */
    private static PoolCfg createUrgentPoolCfg(PoolCfg cfg) {
        if (cfg.pushed_coreSize < 0) {
            if (CPU_NUMBER <= 2) {
                cfg.coreSize = CPU_NUMBER;
            } else {
                cfg.coreSize = CPU_NUMBER;
            }
        } else {
            cfg.coreSize = Math.max(CPU_NUMBER, cfg.pushed_coreSize);
        }
        if (cfg.pushed_maxSize < 0) {
            if (CPU_NUMBER <= 2) {
                cfg.maxSize = Math.max(cfg.coreSize, CPU_NUMBER + 2);
            } else {
                cfg.maxSize = Integer.MAX_VALUE;
            }
        } else {
            cfg.maxSize = Math.max(cfg.coreSize, cfg.pushed_maxSize);
        }
        if (cfg.pushed_keepAlive < 0) {
            cfg.keepAlive = 30;
        } else {
            cfg.keepAlive = cfg.pushed_keepAlive;
        }
        if (cfg.pushed_workQueue < 0) {
            cfg.workQueue = new ArrayBlockingQueue<Runnable>(32, true);
        } else {
            cfg.workQueue = new ArrayBlockingQueue<Runnable>(cfg.pushed_workQueue, true);
        }
        cfg.rejectHandler = new ThreadPoolExecutor.DiscardOldestPolicy();
        cfg.factory = new CommonThreadFactory("URGENT_THREAD_", Thread.NORM_PRIORITY);
        return cfg;
    }

    /**
     * 该Executor池子core中等，有线程数上限，队列大，线程优先级低，保活时间长
     * @return
     */
    private static PoolCfg createNormalPoolCfg(PoolCfg cfg) {
        if (cfg.pushed_coreSize < 0) {
            if (CPU_NUMBER <= 2) {
                cfg.coreSize = CPU_NUMBER;
            } else {
                cfg.coreSize = CPU_NUMBER;
            }
        } else {
            cfg.coreSize = Math.max(CPU_NUMBER, cfg.pushed_coreSize);
        }
        if (cfg.pushed_maxSize < 0) {
            if (CPU_NUMBER <= 2) {
                cfg.maxSize = Math.max(cfg.coreSize, CPU_NUMBER * 2);
            } else {
                cfg.maxSize = Math.max(cfg.coreSize, CPU_NUMBER * 4);
            }
        } else {
            cfg.maxSize = Math.max(cfg.coreSize, cfg.pushed_maxSize);
        }
        if (cfg.pushed_keepAlive < 0) {
            cfg.keepAlive = 45;
        } else {
            cfg.keepAlive = cfg.pushed_keepAlive;
        }
        if (cfg.pushed_workQueue < 0) {
            cfg.workQueue = new LinkedBlockingQueue<Runnable>(); // 无上限队列
        } else {
            cfg.workQueue = new LinkedBlockingQueue<Runnable>(cfg.pushed_workQueue);
        }
        cfg.rejectHandler = new ThreadPoolExecutor.DiscardOldestPolicy();
        cfg.factory = new CommonThreadFactory("NORMAL_THREAD_", Thread.MIN_PRIORITY);
        return cfg;
    }

    /**
     * 该Executor池子core小，线程数无上限，队列大，线程优先级正常，保活时间很长
     * @return
     */
    private static PoolCfg createRpcPoolCfg(PoolCfg cfg) {
        if (cfg.pushed_coreSize < 0) {
            if (CPU_NUMBER <= 2) {
                cfg.coreSize = CPU_NUMBER;
            } else {
                cfg.coreSize = CPU_NUMBER;
            }
        } else {
            cfg.coreSize = Math.max(CPU_NUMBER, cfg.pushed_coreSize);
        }
        if (cfg.pushed_maxSize < 0) {
            if (CPU_NUMBER <= 2) {
                cfg.maxSize = Integer.MAX_VALUE;
            } else {
                cfg.maxSize = Integer.MAX_VALUE;
            }
        } else {
            cfg.maxSize = Math.max(cfg.coreSize, cfg.pushed_maxSize);
        }
        if (cfg.pushed_keepAlive < 0) {
            cfg.keepAlive = 60;
        } else {
            cfg.keepAlive = cfg.pushed_keepAlive;
        }
        if (cfg.pushed_workQueue < 0) {
            cfg.workQueue = new LinkedBlockingQueue<Runnable>(); // 无上限队列
        } else {
            cfg.workQueue = new LinkedBlockingQueue<Runnable>(cfg.pushed_workQueue);
        }
        cfg.rejectHandler = new ThreadPoolExecutor.DiscardOldestPolicy();
        cfg.factory = new CommonThreadFactory("RPC_INVOKER_THREAD_", Thread.MIN_PRIORITY);
        return cfg;
    }

    /**
     * 该Executor池子core小，线程数上限与core一样，队列无限大，线程优先级正常，保活时间短
     *
     * 此池子专用于MultimediaService发网络请求拉取图片（区分http, django两种后台）
     *
     * @return
     */
    private static PoolCfg createMmsPoolCfg(PoolCfg cfg, String prefix) {
        if (cfg.pushed_coreSize < 0) {
            if (CPU_NUMBER <= 2) {
                cfg.coreSize = Math.min(CPU_NUMBER, 3); // 最大三个
            } else {
                cfg.coreSize = Math.min(CPU_NUMBER, 3); // 最大三个
            }
        } else {
            cfg.coreSize = Math.min(CPU_NUMBER, cfg.pushed_coreSize);
        }
        if (cfg.pushed_maxSize < 0) {
            if (CPU_NUMBER <= 2) {
                cfg.maxSize = Math.min(CPU_NUMBER, 3); // 最大三个
            } else {
                cfg.maxSize = Math.min(CPU_NUMBER, 3); // 最大三个
            }
        } else {
            cfg.maxSize = Math.min(CPU_NUMBER, cfg.pushed_maxSize);
        }
        if (cfg.pushed_keepAlive < 0) {
            cfg.keepAlive = 20; // 图片加载一般启动后任务较多，之后越用越少
        } else {
            cfg.keepAlive = cfg.pushed_keepAlive;
        }
        cfg.workQueue = new LifoBlockingDeque<Runnable>();
        cfg.rejectHandler = new ThreadPoolExecutor.DiscardOldestPolicy();
        cfg.factory = new CommonThreadFactory("MMS_" + prefix + "_THREAD_", Thread.MIN_PRIORITY);
        return cfg;
    }

    /**
     *
     * 此池子专用于实现OrderExecutor
     *
     * @return
     */
    private static PoolCfg createOrderedThreadPoolCfg(PoolCfg cfg, String prefix) {
        if (cfg.pushed_coreSize < 0) {
            if (CPU_NUMBER <= 2) {
                cfg.coreSize = Math.max(CPU_NUMBER, 4);
            } else {
                cfg.coreSize = Math.max(CPU_NUMBER, 4);
            }
        } else {
            cfg.coreSize = Math.max(CPU_NUMBER, cfg.pushed_coreSize);
        }
        if (cfg.pushed_maxSize < 0) {
            if (CPU_NUMBER <= 2) {
                cfg.maxSize = 4;
            } else {
                cfg.maxSize = CPU_NUMBER * 2;
            }
        } else {
            cfg.maxSize = Math.max(CPU_NUMBER, cfg.pushed_maxSize);
        }
        if (cfg.pushed_keepAlive < 0) {
            cfg.keepAlive = 30;
        } else {
            cfg.keepAlive = cfg.pushed_keepAlive;
        }
        if (cfg.pushed_workQueue < 0) {
            cfg.workQueue = new ArrayBlockingQueue<Runnable>(30, true);
        } else {
            cfg.workQueue = new ArrayBlockingQueue<Runnable>(cfg.pushed_workQueue, true);
        }
        cfg.rejectHandler = new ThreadPoolExecutor.DiscardOldestPolicy();
        cfg.factory = new CommonThreadFactory(prefix + "_THREAD_", Thread.MIN_PRIORITY);
        return cfg;
    }

    @Override
    public ThreadPoolExecutor acquireExecutor(ScheduleType type) {
        ThreadPoolExecutor executor = executorsMap.get(type);
        if (executor != null) {
            return executor;
        }
        synchronized (executorsMap) {
            executor = executorsMap.get(type);
            if (executor != null) {
                return executor;
            }
//            LoggerFactory.getTraceLogger().info(TAG, "acquireExecutor: " + type);
            switch (type) {//NOSONAR
                case IO:
                    executor = createIoExecutor();
                    break;
                case URGENT:
                    executor = createUrgentExecutor();
                    break;
                case NORMAL:
                    executor = createNormalExecutor();
                    break;
                case RPC:
                    executor = createRpcExecutor();
                    break;
                case SYNC:
                    throw new IllegalArgumentException("The ThreadPool of type SYNC is not supported yet, please considering another type!");
                case MMS_HTTP:
                    executor = createMmsHttpExecutor();
                    break;
                case MMS_DJANGO:
                    executor = createMmsDjangoExecutor();
                    break;
            }
            if (executor == null) {
                throw new IllegalStateException("create executor of type: " + type + " failed!");
            }
            executorsMap.put(type, executor);
        }
        return executor;
    }

    private static ThreadPoolExecutor createExecutor(final PoolCfg cfg) {
        if (cfg == null) {
            throw new IllegalArgumentException("cfg is null");
        }
//        LoggerFactory.getTraceLogger().info(TAG, "createExecutor: " + cfg);
        final ThreadPoolExecutor executor = new TaskPoolExecutor(
                cfg.coreSize,
                cfg.maxSize,
                cfg.keepAlive,
                cfg.timeunit,
                cfg.workQueue,
                cfg.factory,
                cfg.rejectHandler);
        executor.allowCoreThreadTimeOut(cfg.allowCoreTimeout);
        return executor;
    }

    private ThreadPoolExecutor createIoExecutor() {
        return createExecutor(ioPoolCfg);
    }

    private ThreadPoolExecutor createRpcExecutor() {
        return createExecutor(rpcPoolCfg);
    }

    private ThreadPoolExecutor createMmsHttpExecutor() {
        return createExecutor(mmsHttpPoolCfg);
    }

    private ThreadPoolExecutor createMmsDjangoExecutor() {
        return createExecutor(mmsDjangoPoolCfg);
    }

    private ThreadPoolExecutor createNormalExecutor() {
        return createExecutor(normalPoolCfg);
    }

    private ThreadPoolExecutor createUrgentExecutor() {
        return createExecutor(urgentPoolCfg);
    }

    @Override
    public ScheduledThreadPoolExecutor acquireScheduledExecutor() {
        if (scheduledExecutor == null) {
            synchronized (this) {
                if (scheduledExecutor == null) {
                    scheduledExecutor = new ScheduledTaskPoolExecutor(CPU_NUMBER,
                            new CommonThreadFactory("TIMER_THREAD_", Thread.NORM_PRIORITY),
                            new ThreadPoolExecutor.DiscardOldestPolicy());

                }
            }
        }
        return scheduledExecutor;
    }

    @Override
    public OrderedExecutor<String> acquireOrderedExecutor() {
        if (orderedExecutor == null) {
            synchronized (this) {
                if (orderedExecutor == null) {
                    orderedExecutorCore = createExecutor(orderedThreadPoolCfg);
                    orderedExecutor = new OrderedExecutor<String>(orderedExecutorCore);
                }
            }
        }
        return orderedExecutor;
    }

    @Override
    public ThreadPoolExecutor getOrderedExecutorCore() {
        acquireOrderedExecutor();
        return orderedExecutorCore;
    }

    private class IdleCheckTask implements Runnable {
        public ScheduledFuture<?> taskFuture;
        private ProcessCpuTracker cpuTracker = new ProcessCpuTracker().update();
        private int idleCount = 0;

        @Override
        public void run() {
            float idlePercent = cpuTracker.update().getCpuIdlePercent();
//            LoggerFactory.getTraceLogger().info(TAG, "CPU idle: " + idlePercent);
            if (0 < idlePercent && idlePercent < CPU_IDLE_PERCENT) { // 获取CPU空闲率失败算IDLE
                return;
            }
            idleCount++;
            if (idleCount < CPU_IDLE_COUNT) {
                return;
            }
            try {
//                executeIdleTasks();
//                LoggerFactory.getTraceLogger().info(TAG, "executeIdleTasks() Reason: cpu idle");
            } catch (Throwable t) {
//                LoggerFactory.getTraceLogger().error(TAG, "IdleCheckTask", t);
            } finally {
                if (taskFuture != null) {
                    try {
                        taskFuture.cancel(false /* do not interrupt in-progress task */);
                    } catch (Throwable t) {
//                        LoggerFactory.getTraceLogger().error(TAG, "IdleCheckTask", t);
                    }
                }
            }
        }
    }

    private ScheduledFuture<?> prepareIdleCheckTask() {
        IdleCheckTask idleCheckTask = new IdleCheckTask(); // 所有PipeLine执行完即开始侦测是否IDLE
        ScheduledFuture<?> idleCheckFuture = acquireScheduledExecutor().scheduleAtFixedRate(
                idleCheckTask, IDLE_CHECK_PERIOD, IDLE_CHECK_PERIOD, TimeUnit.SECONDS);
        idleCheckTask.taskFuture = idleCheckFuture;
        return idleCheckFuture;
    }

    /**
     * @deprecated 请使用重载方法，传入线程名方便排查问题
     */
    @Override
    @Deprecated
    public boolean addIdleTask(Runnable task) {
        String threadName = task == null ? "no task" : task.getClass().getName();
        return addIdleTask(task, threadName, 0);
    }

//    // FIXME 提供几档权重的常量值（类似线程优先级常量），目前道深传10，军英传-10，其它传0。
//    @Override
//    public boolean addIdleTask(Runnable task, String threadName, int taskWeight) {
//        if (task == null) {
//            throw new IllegalArgumentException("The task is null!");
//        }
//        if (TextUtils.isEmpty(threadName)) {
//            throw new IllegalArgumentException("The thread name is none!");
//        }
//        if (idleCheckTaskFuture != null) { // 只有在三个PipeLine都执行完成之后才创建IdleCheckTask，此后再添加IdleTask不接受
////            throw new IllegalArgumentException("new task won't be accepted after all Pipeline being executed!");
//        }
//
//        MicroApplicationContext microApp = LauncherApplicationAgent.getInstance().getMicroApplicationContext();
//        if (microApp == null) {
////            LoggerFactory.getTraceLogger().error(TAG,
////                    "addIdleTask: MicroApplicationContext is NULL");
//            return false;
//        }
//
//        Pipeline pipeline = microApp.getPipelineByName(PIPELINE_NAME, PIPELINE_TIMEOUT);
//        boolean pipelineAvailable = pipeline != null;
//        if (pipelineAvailable) {
//            pipeline.addTask(task, threadName, taskWeight);
//        }
//
//        String logInfo = pipelineAvailable ? "addIdleTask: " + threadName + ", " + taskWeight
//                : "类型为" + TAG + "的pipeline不存在";
////        LoggerFactory.getTraceLogger().info(TAG, logInfo);
//        return pipelineAvailable;
//    }

//    @Override
//    public synchronized void onPipelineFinished(String type) {
//        if (type == null) {
//            return;
//        }
//        // Log.d("faywong", "onPipelineFinished() in, type:" + type);
////        LoggerFactory.getTraceLogger().info(TAG, "pipeline(event: " + type + ") has finished");
//        if (type.equals(MsgCodeConstants.PIPELINE_FRAMEWORK_INITED)) {
//            allPipelineFinished |= 1;
//        } else if (type.equals(MsgCodeConstants.PIPELINE_FRAMEWORK_CLIENT_STARTED)) {
//            allPipelineFinished |= (1 << 1);
//        } else if (type.equals("com.alipay.mobile.PORTAL_TABLAUNCHER_ACTIVATED")) {
//            allPipelineFinished |= (1 << 2);
//        }
//        if (allPipelineFinished == 7 && idleCheckTaskFuture == null) {
////            LoggerFactory.getTraceLogger().info(TAG,
////                    "prepareIdleCheckTask as all pipelines have finished!");
//            idleCheckTaskFuture = prepareIdleCheckTask();
//        }
//    }

//    private void executeIdleTasks() {
//        if (isExecuteIdleTasks) {
////            LoggerFactory.getTraceLogger().info(TAG, "executeIdleTasks: already executed");
//            return;
//        }
//        isExecuteIdleTasks = true;
//        MicroApplicationContext microApp = LauncherApplicationAgent.getInstance().getMicroApplicationContext();
//        if (microApp == null) {
////            LoggerFactory.getTraceLogger().error(TAG, "executeIdleTasks: MicroApplicationContext is NULL");
//            return;
//        }
//        final Pipeline pipeline = microApp.getPipelineByName(PIPELINE_NAME, PIPELINE_TIMEOUT);
//        pipeline.addIdleListener(new Runnable() {
//            private long idleCount;
//            @Override
//            public void run() {
//                idleCount++;
////                LoggerFactory.getTraceLogger().info(TAG, "idle tasks are all terminated, count: " + idleCount);
////                pipeline.addIdleListener(null);
//            }
//        });
////        LoggerFactory.getTraceLogger().info(TAG, "idle tasks are started");
//        pipeline.start();
//    }


    @Override
    public boolean addIdleTask(Runnable task, String threadName, int taskWeight) {
        return false;
    }

    @Override
    public void onPipelineFinished(String type) {
    }

    @Override
    public Bundle dump() {
        Bundle bundle = new Bundle();
        synchronized (executorsMap) {
            for (ScheduleType type : executorsMap.keySet()) {
                bundle.putString(type.toString(), executorsMap.get(type).toString());
            }
        }
        bundle.putString("SCHEDULED_EXECUTOR",
                scheduledExecutor == null ? "NULL" : scheduledExecutor.toString());
        bundle.putString("GLOBAL_HANDLER_THREAD",
                orderedExecutor == null ? "NULL" : orderedExecutor.toString());
        return bundle;
    }

}
