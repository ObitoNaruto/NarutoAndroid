package com.naruto.mobile.base.threadpool;

import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.Executor;

public class StandardPipeline implements PipeLine{
    /**
     * 串行执行的回调接口
     */
    public interface IScheduleNext {
        /**
         * 串行执行的回调函数
         */
        void scheduleNext();
    }

    /**
     * Log TAG
     */
    static final String TAG = "StandardPipeline";

    /**
     * 串行执行的缓存队列
     */
    protected ArrayList<NamedRunnable> mTasks = new ArrayList<NamedRunnable>();

    /**
     * 串行执行的回调接口
     */
    final IScheduleNext next = new IScheduleNext() {
        @Override
        public void scheduleNext() {
            Log.v(TAG, "StandardPipeline.scheduleNext()");
            if (StandardPipeline.this.mIsStart) {
                StandardPipeline.this.executeNext();//执行下一个AsyncTas
            }
        }
    };

    /**
     * 当前正在执行的AsyncTask任务
     */
    volatile NamedRunnable mActive;

    protected volatile boolean mIsStart = false;

    private Executor mExecutor;

    public StandardPipeline() {
        this(null);
    }
    public StandardPipeline(Executor executor) {
        mExecutor = executor;
    }

    @Override
    public void setExecutor(Executor executor) {
        mExecutor = executor;
    }

    @Override
    public void addTask(Runnable task, String threadName) {
        addTask(task, threadName, 0);
    }

    @Override
    public void addTask(Runnable task, String threadName, int weight) {
        NamedRunnable _task = NamedRunnable.TASK_POOL.obtain(task, threadName, weight);
        addTask(_task);
    }

    @Override
    public void addIdleListener(Runnable task) {
        if(task == null) {
            stop();
        }
        NamedRunnable _task = NamedRunnable.TASK_POOL.obtain(task, null);
        addTask(_task);
    }

    public void addTask(final NamedRunnable task) {
        Log.v(TAG, "StandardPipeline.addTask()");
        if (null == mTasks) {
            Log.v(TAG, "StandardPipeline.addTask(), mTasks:null");
            throw new RuntimeException("The StandardPipeline has already stopped.");
        } else {
            task.setScheduleNext(next);
            synchronized (mTasks) {
                int index = 0;
                if (!mTasks.isEmpty()) {
                    //按权重排序
                    for (index = mTasks.size() - 1; index >= 0; index--) {
                        if (task.mWeight <= mTasks.get(index).mWeight) {
                            index += 1;
                            break;
                        }
                    }
                    index = index < 0 ? 0 : index;
                }
                Log.v(TAG, "StandardPipeline.addTask(), add:" + task.toString());
                //按权重排序插入到对应的位置
                mTasks.add(index, task);
            }
        }
        if (mIsStart) {
            doStart();
        }
    }

    @Override
    public void start() {
        Log.v(TAG, "StandardPipeline.start()");
        if (null == mExecutor) {
            throw new RuntimeException("StandardPipeline start failed : The StandardPipeline's Execturo is null.");
        }
        mIsStart = true;
        doStart();
    }

    /**
     *
     */
    protected void doStart() {
        if (mActive == null) {
            executeNext();
        } else {
            Log.v(TAG, "StandardPipeline.start(a task is running, so don't call scheduleNext())");
        }
    }

    /**
     * 执行下一个AsyncTask
     */
    private void executeNext() {
        synchronized (mTasks) {
            if (!mTasks.isEmpty()) {
                mActive = mTasks.remove(0);
            } else {
                mActive = null;
                Log.v(TAG, "mTasks is empty.");
            }
        }
        if (mActive != null) {
            Log.d(TAG, "StandardPipeline.scheduleNext()");
            if (null != mExecutor) {
                mExecutor.execute(mActive);
            } else {
                throw new RuntimeException("The StandardPipeline's Executor is null.");
            }
        } else {
            Log.d(TAG, "StandardPipeline.scheduleNext(mTasks is empty)");
        }
    }

    /**
     * 关闭执行器
     */
    @Override
    public void stop() {
        mIsStart = false;
    }
}
