package com.naruto.mobile.framework.service.common;

import android.os.Bundle;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.naruto.mobile.base.serviceaop.service.CommonService;
import com.naruto.mobile.framework.common.threadpool.OrderedExecutor;

/**
 * 任务调度服务
 */
public abstract class TaskScheduleService extends CommonService {

    public static abstract class Transaction extends com.naruto.mobile.framework.common.threadpool.Transaction {
    }

    /**
     * 串行执行
     *
     * 请使用<br>
     * serialExecute(Runnable task, String threadName)
     *
     * @param command Runnable
     */
    @Deprecated
    public abstract void serialExecute(Runnable command);

    /**
     * 并行执行
     *
     * 请使用<br>
     * parallelExecute(Runnable task, String threadName)
     *
     * @param command Runnable
     */
    @Deprecated
    public abstract void parallelExecute(Runnable command);

    /**
     * 串行执行
     *
     * @param command Runnable
     * @param threadName ThreadName
     */
    public abstract void serialExecute(Runnable command, String threadName);

    /**
     * 并行执行
     *
     * @param command Runnable
     * @param threadName ThreadName
     */
    public abstract void parallelExecute(Runnable command, String threadName);

    public abstract ScheduledFuture<?> schedule(Runnable task, String threadName, long delay, TimeUnit unit);

    public abstract ScheduledFuture<?> scheduleAtFixedRate(Runnable task, String threadName, long initialDelay, long period, TimeUnit unit);

    public abstract ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, String threadName, long initialDelay, long delay, TimeUnit unit);

    public abstract String addTransaction(Transaction transaction);

    public abstract void removeTransaction(String id);

    /**
     * 根据调度特点及典型的使用场景划分的executor类型
     * @see TaskScheduleService#acquireExecutor(ScheduleType)
     *
     * 以下调度类型以其最适合被使用的场景来命名，
     * 但调用者需要客观评估怎么合适地使用各种线程池，比如登陆RPC虽然是网络特定的后台任务，
     * 但优先级非常高，一般建议放在URGENT类型的线程池中
     */
    public static enum ScheduleType {
        URGENT, /* 为前台UI所依赖，优先级最高，不能容忍排队 */
        NORMAL, /* 普通不太紧急，可以容忍排队的后台任务 */
        IO, /* 文件IO类操作，持久化任务，耗时可以预计，要么不久成功，要么发生异常 */
        RPC, /* 网络相关的后台任务，耗时视条件波动较大，典型使用场景为发起RPC请求 */
        SYNC, /* 用于处理syncservice同步至客户端的数据的线程池类型 */
        MMS_HTTP, /* 用于MultimediaService执行拉取图片等任务的线程池类型（后台为HTTP），任务队列为LIFO */
        MMS_DJANGO, /* 用于MultimediaService执行拉取图片等任务的线程池类型（后台为DJANGO），任务队列为LIFO */
    }

    /**
     * 获取期望的Executor实例(线程安全)
     * @return
     */
    public abstract ThreadPoolExecutor acquireExecutor(ScheduleType type);

    /**
     * 获取支持调度的Executor实例
     *
     * Note: 在所有需要Timer的地方都应该迁移至使用本接口
     *
     * @return
     */
    public abstract ScheduledThreadPoolExecutor acquireScheduledExecutor();

    /**
     * 获取OrderedExecutor实例
     *
     * 提交给OrderedExecutor的拥有相同KEY的Task会保证有序串行执行（但不一定全在同一线程），不同的KEY对应的Task之间会并发
     *
     * @return
     */
    public abstract OrderedExecutor acquireOrderedExecutor();

    /**
     * 获取OrderedExecutor实际的执行线程池
     *
     * 供调用方获取内部线程池的执行状态
     *
     * @return
     */
    public abstract ThreadPoolExecutor getOrderedExecutorCore();

    /**
     * 添加Task到Idle线程池中，app启动后整个实例存留期间第一次相对空闲时运行该Task
     *
     * @param task 任务
     * @return
     * @deprecated 请使用重载方法，传入线程名方便排查问题
     */
    @Deprecated
    public abstract boolean addIdleTask(Runnable task);

    /**
     * 添加Task到Idle线程池中，app启动后整个实例存留期间第一次相对空闲时运行该Task，可设置权重
     *
     * @param task 任务
     * @param threadName 执行时的线程名
     * @param taskWeight 权重
     * @return
     */
    public abstract boolean addIdleTask(Runnable task, String threadName, int taskWeight);

    /**
     * 供PipeLine的实现方调用来通知TaskScheduleService某一Pipeline已经执行完毕
     *
     * @param type
     */
    public abstract void onPipelineFinished(final String type);

    /**
     * invoke to collect the runtime statistics of TaskScheduleService
     * Note: expect to be used for profiling purpose
     *
     * @return
     */
    public abstract Bundle dump();

}
