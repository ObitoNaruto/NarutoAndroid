package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.task;


import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.assist.DefaultConfigurationFactory;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Logger;

/**
 * 网络加载
 * Created by jinmin on 15/7/13.
 */
public class ImageTaskEngine {

    private Logger logger = Logger.getLogger("ImageNetTaskEngine");

    public ConcurrentHashMap<String, ImageNetTask> taskMap = new ConcurrentHashMap<String, ImageNetTask>();

    private ConcurrentHashMap<String, Future> runningTaskMap = new ConcurrentHashMap<String, Future>();

    private ExecutorService mUrlExecutor = DefaultConfigurationFactory.createLoadExecutor("url", true);

    private ExecutorService mDjangoExecutor = DefaultConfigurationFactory.createLoadExecutor("dj", true);

    private ExecutorService mLocalExecutor = DefaultConfigurationFactory.createLoadExecutor("lo", false);

    private ExecutorService mDisplayExecutor = DefaultConfigurationFactory.commonExecutor();

    private static ImageTaskEngine sInstance = new ImageTaskEngine();

    private ImageTaskEngine() {}

    public static ImageTaskEngine get() {
        return sInstance;
    }

    public Future submit(ImageNetTask task) {
        if (task == null) {
            logger.e("submit task is null");
            return null;
        }
        //异步执行的要做合并处理
        String taskId = task.getTaskId();
        ImageNetTask netTask = taskMap.get(taskId);
        Lock lock = null;
        if (netTask == null) {//只有不存在task任务的时候才要锁，防止重复添加下载任务
            lock = getLock(taskId);
            lock.lock();//保证一个taskId的操作是原子的
            netTask = taskMap.get(taskId);//因为lock过程中，taskMap会被修改，这里应该再取一次，保证netTask是最新的信息
        }
        Future future = null;
        try {
            if (netTask == null) {
                logger.p("new task: " + task + ", taskId: " + taskId);
                task.loadReq.taskModel.setTaskId(taskId);
                taskMap.put(taskId, task);
            } else {//合并请求
                logger.p("merge to task: " + netTask + ", taskId: " + taskId);
                netTask.addImageLoadReq(task.loadReq);
            }

            future = runningTaskMap.get(taskId);
            if (future == null) {
                //允许业务自定义网络任务ExecutorService
                if (task.loadReq.options.hasNetloadExecutorService()) {
                    future = task.loadReq.options.getNetloadExecutorService().submit(task);
                } else if (task instanceof ImageUrlTask) {//url task 统一一个线程池处理
                    future = mUrlExecutor.submit(task);
                } else {    //Django task 一个线程池处理
                    future = mDjangoExecutor.submit(task);
                }
                runningTaskMap.put(taskId, future);
            }
        } finally {
            if (lock != null) {
                lockMap.remove(taskId);
                lock.unlock();
            }
        }

        if (task.loadReq.options.isSyncLoading()) {
            //同步执行的等待结果
            try {
                future.get();
            } catch (Exception e) {
//                logger.e(e, "future get exception");
            }
        }
        return future;
    }

    public Future submit(ImageLocalTask task) {
        if (task == null) return null;
        return syncOrSubmit(mLocalExecutor, task);
    }

    public Future submit(ImageDisplayTask task) {
        if (task == null) return null;
        return syncOrSubmit(mDisplayExecutor, task);
    }

    public synchronized void removeTask(String taskId) {
//        logger.p("removeTask taskId: " + taskId);
        taskMap.remove(taskId);
        runningTaskMap.remove(taskId);
    }

    public synchronized ImageNetTask cancelTask(String taskId) {
//        logger.p("cancelTask taskId: " + taskId);
        ImageNetTask task = taskMap.get(taskId);
        Future future = runningTaskMap.get(taskId);

        if (task != null) {
            task.cancel();
        }

        if (future != null) {
            future.cancel(true);
        }

        return task;
    }

    private Future syncOrSubmit(ExecutorService executor, ImageTask task) {
        Future future = null;
        if (task.loadReq.options.isSyncLoading()) {//同步
            try {
                task.call();
            } catch (Exception e) {
//                logger.e(e, "syncOrSubmit sync execute error");
            }
        } else {
            future = executor.submit(task);//在线程池中异步调用
        }
        return future;
    }

    private final ConcurrentHashMap<String, Lock> lockMap = new ConcurrentHashMap<String, Lock>();
    private Lock getLock(String taskId) {
        lockMap.putIfAbsent(taskId, new ReentrantLock());
        return lockMap.get(taskId);
    }

    public void submit(Runnable runnable) {
        if (runnable != null) {
            mDisplayExecutor.submit(runnable);
        }
    }
}
