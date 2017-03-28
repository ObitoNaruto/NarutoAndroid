package com.naruto.mobile.framework.common.threadpool;

import android.text.TextUtils;
import android.util.Log;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class NamedRunnable implements Runnable, Pool.Poolable {

    /**
     * Log TAG
     */
    static final String TAG = "AsyncTaskExecutor";

    /**
     * 对象池
     */
    public static final class NamedRunnablePool extends Pool<NamedRunnable> {

        private final AtomicInteger mIndex = new AtomicInteger(1);

        public NamedRunnablePool(int initialCapacity, int max) {
            super(initialCapacity, max);
        }

        @Deprecated
        @Override
        public NamedRunnable obtain() {
            throw new RuntimeException("call obtain(Runnable, String) method instead.");
        }

        public synchronized NamedRunnable obtain(Runnable task, String threadName) {
            return obtain(task, threadName, 0);
        }

        public synchronized NamedRunnable obtain(Runnable task, String threadName, int weight) {
            NamedRunnable namedRunnable;
            if (freeObjects.size() == 0) {
                Log.i(TAG, "NamedRunnablePool.obtain(): create a new NamedRunnable obj.");
                namedRunnable = newObject(task, threadName, weight);
            } else {
                Log.i(TAG, "NamedRunnablePool.obtain(): hit a cache NamedRunnable obj.");
                namedRunnable = freeObjects.pop();
                namedRunnable.setTask(task);
                namedRunnable.setThreadName(threadName);
                namedRunnable.setWeight(weight);
            }
            return namedRunnable;
        }

        @Override
        @Deprecated
        protected NamedRunnable newObject() {
            throw new RuntimeException("call newObject(Runnable, String) method instead.");
        }

        NamedRunnable newObject(Runnable task, String threadName, int weight) {
            if (TextUtils.isEmpty(threadName)) {
                threadName = "NamedRunable_" + mIndex.getAndIncrement();
            } else {
                threadName = "NamedRunable_" + mIndex.getAndIncrement() + "_" + threadName;
            }
            return new NamedRunnable(task, threadName, weight);
        }

        @Override
        public synchronized void free(NamedRunnable object) {
            super.free(object);
        }

        @Override
        public synchronized void freeAll(List<NamedRunnable> objects) {
            super.freeAll(objects);
        }

        @Override
        public synchronized void clear() {
            super.clear();
        }
    };

    public static final NamedRunnablePool TASK_POOL = new NamedRunnablePool(8, 16);

    String mThreadName;

    Runnable mTask;

    StandardPipeline.IScheduleNext mScheduleNext;

    int mWeight = 0;

    NamedRunnable(Runnable runnable, String threadName, int weight) {
        mTask = runnable;
        mThreadName = threadName;
        mWeight = weight;
    }

    void setThreadName(String threadName) {
        mThreadName = threadName;
    }

    void setTask(Runnable task) {
        mTask = task;
    }

    NamedRunnable setScheduleNext(StandardPipeline.IScheduleNext scheduleNext) {
        mScheduleNext = scheduleNext;
        return this;
    }

    void setWeight(int weight) {
        mWeight = weight;
    }

    @Override
    public void run() {
        String threadName = null;
        if (!TextUtils.isEmpty(mThreadName)) {
            threadName = Thread.currentThread().getName();
            Log.i(TAG, "NamedRunable.run(set ThreadName to:" + mThreadName + ")");
            Thread.currentThread().setName(threadName + "_" + mThreadName);
        }
        long start = System.currentTimeMillis();
        try {
            mTask.run();
        } finally {
            long end = System.currentTimeMillis();
            if (!TextUtils.isEmpty(mThreadName)) {
                Log.i(TAG, "NamedRunable.run(set ThreadName back to:" + threadName + ")");
                if (null != threadName) {
                    Thread.currentThread().setName(threadName);
                }
            }
            if (null != mScheduleNext) {
                Log.v(TAG, "NamedRunnable.run()->finish(finally:mScheduleNext.scheduleNext())");
                mScheduleNext.scheduleNext();
            } else {
                Log.v(TAG, "NamedRunnable.run()->finish(finally:null == mScheduleNext)");
            }
            TASK_POOL.free(this);
            Log.d(TAG, "NamedRunnable.run()->finish(TASK_POOL.free(this)): pool.size=" + TASK_POOL.freeObjects.size());
        }
    }

    @Override
    public void reset() {
        mTask = null;
        mThreadName = null;
        mScheduleNext = null;
        mWeight = 0;
    }
}
