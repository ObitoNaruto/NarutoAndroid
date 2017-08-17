package com.naruto.mobile.framework.service.common.impl;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.naruto.mobile.base.serviceaop.NarutoApplication;
import com.naruto.mobile.base.serviceaop.NarutoApplicationContext;
import com.naruto.mobile.base.serviceaop.demo.service.TaskScheduleService;
import com.naruto.mobile.framework.service.common.TimerTaskService;

public class TimerTaskServiceImpl extends TimerTaskService {
    Map<onTickListenerWrapper, Integer> mListeners;
    Map<onTickListenerWrapper, Integer> mCountDownListeners;
    TaskScheduleService mScheduleService;
    Timer mTimer = null;
    long baseTime = 10; // 10min
    Object lock = new Object();
    boolean scheduleStarted = false;
    ScheduledFuture<?> scheduleTask = null;

    private static class onTickListenerWrapper {
        OnTickListener mListener;
        Handler mHandler;

        public onTickListenerWrapper(OnTickListener mListener, Handler mHandler) {
            super();
            this.mListener = mListener;
            this.mHandler = mHandler;
        }

        public OnTickListener getListener() {
            return mListener;
        }

        public void setListener(OnTickListener mListener) {
            this.mListener = mListener;
        }

        public Handler getHandler() {
            return mHandler;
        }

        public void setHandler(Handler mHandler) {
            this.mHandler = mHandler;
        }

    }

    public TimerTaskServiceImpl() {
        super();
        mListeners = new HashMap<onTickListenerWrapper, Integer>();
        mCountDownListeners = new HashMap<onTickListenerWrapper, Integer>();
        NarutoApplicationContext narutoApplicationContext = NarutoApplication
                .getInstance().getNarutoApplicationContext();
        mScheduleService = narutoApplicationContext
                .findServiceByInterface(TaskScheduleService.class.getName());
    }

    @Override
    public boolean registerListener(OnTickListener listener, int timeUnit,
            Handler handler) {
        if (listener == null) {
            throw new IllegalArgumentException();
        }
        if (timeUnit <= 0) {
            throw new IllegalArgumentException();
        }
        if (isListenerRegistered(listener)) {
            return false;
        }
        onTickListenerWrapper wrapper;
        if (handler == null) {
            wrapper = new onTickListenerWrapper(listener, new Handler(Looper.getMainLooper()));
        } else {
            wrapper = new onTickListenerWrapper(listener, handler);
        }
        synchronized (lock) {//原子操作
            mListeners.put(wrapper, timeUnit);
            mCountDownListeners.put(wrapper, timeUnit);
            if (!scheduleStarted) {//周期性调度服务第一次被启动
                scheduleTask = mScheduleService.scheduleWithFixedDelay(
                        new TickTimerTask(), //任务，一般是子线程
                        "TimerTaskService", //线程名称
                        0, //delay的初始时间
                        baseTime,//delay时间
                        TimeUnit.MINUTES//时间单位
                );
                scheduleStarted = true;//重置状态表示任务已开启
            }
        }
        return true;
    }

    /**
     * 该listener是否已注册过
     * @param listener
     * @return
     */
    private boolean isListenerRegistered(OnTickListener listener) {
        boolean find = false;//状态
        Iterator<onTickListenerWrapper> iterator = mListeners.keySet().iterator();//迭代器迭代
        while (iterator.hasNext()) {//遍历
            onTickListenerWrapper tickListenerWrapper = iterator.next();
            if (tickListenerWrapper.getListener() == listener) {//已经注册过
                find = true;
                break;
            }
        }
        return find;
    }

    @Override
    public boolean registerListener(OnTickListener listener, int timeUnit){
        if (listener == null) {
            throw new IllegalArgumentException();
        }
        if (timeUnit <= 0) {
            throw new IllegalArgumentException();
        }
        if (isListenerRegistered(listener)) {
            return false;
        }
        onTickListenerWrapper wrapper = new onTickListenerWrapper(listener, null);

        synchronized (lock) {
            mListeners.put(wrapper, timeUnit);
            mCountDownListeners.put(wrapper, timeUnit);
            if (!scheduleStarted) {
                scheduleTask = mScheduleService.scheduleWithFixedDelay(
                        new TickTimerTask(),
                        "TimerTaskService",
                        0,
                        baseTime,
                        TimeUnit.MINUTES
                );
                scheduleStarted = true;
            }
        }
        return true;
    }

    @Override
    public boolean unregisterListener(OnTickListener listener) {
        if (listener == null) {
            return false;
        }
        Iterator<onTickListenerWrapper> iterator = mListeners.keySet().iterator();
        while (iterator.hasNext()) {
            onTickListenerWrapper tickListenerWrapper = iterator.next();
            if (tickListenerWrapper.getListener() == listener) {
                synchronized (lock) {//原子操作
                    mListeners.remove(tickListenerWrapper);
                    mCountDownListeners.remove(tickListenerWrapper);
                }
                stopTheTimerifNoListener();//停止周期性调度监听
                return true;
            }
        }
        return false;
    }

    /**
     * 停止所有的周期性调度监听
     */
    private void stopTheTimerifNoListener() {
        if (mListeners.isEmpty()) {
            stopTheTimer();
        }
    }

    /**
     * 停止周期性调度监听
     */
    private void stopTheTimer() {
        if (scheduleTask != null) {
            scheduleTask.cancel(false);
            scheduleStarted = false;
        }
    }

    @Override
    protected void onCreate(Bundle paramBundle) {
        // TODO Auto-generated method stub

    }

    /**
     * app退出时停止所有监听
     * @param paramBundle
     */
    @Override
    protected void onDestroy(Bundle paramBundle) {
        mListeners = null;
        mCountDownListeners = null;
        stopTheTimer();

    }

    private class TickTimerTask implements Runnable {

        @Override
        public void run() {
            scheduleTickListeners();
        }

        /**
         * eg:比如传递进来的周期倍数为3，即每30分钟监听回调一次
         * ，因为任务是每十分钟调度一次,每次回调时监听器map会遍历，判断自己是不是应该回调了，如果满足条件就去回调，然后数据reset，周而复始
         */
        private void scheduleTickListeners() {
            synchronized (lock) {
                Iterator<Map.Entry<onTickListenerWrapper, Integer>> iterator = mCountDownListeners
                        .entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<onTickListenerWrapper, Integer> entry = iterator.next();
                    if (entry.getValue().equals(1)) {
                        mCountDownListeners.put(entry.getKey(), mListeners.get(entry.getKey()));//reset
                        fireOnListener(entry.getKey());//监听
                    } else {
                        mCountDownListeners.put(entry.getKey(), entry.getValue() - 1);//做处理每次-1，知道其值为1时
                    }
                }
            }
        }

        private void fireOnListener(onTickListenerWrapper wrapper) {
            if (wrapper.getListener() == null) {
                return;
            }
            if (wrapper.getHandler() == null) {
                callbackOnThreadPool(wrapper.getListener());
            } else {
                callbackOnHandler(wrapper.getListener(), wrapper.getHandler());
            }
        }

        private void callbackOnThreadPool(final OnTickListener listener) {
            //并行执行
            mScheduleService.parallelExecute(new Runnable() {

                @Override
                public void run() {
                    try {
                        listener.onTick();
                    } catch (Exception e) {
                        listener.onException(e);
                    }
                }
            }, "TimerTaskServiceImpl");

        }

        private void callbackOnHandler(final OnTickListener listener, Handler handler) {
            handler.post(new Runnable() {

                @Override
                public void run() {
                    try {
                        listener.onTick();
                    } catch (Exception e) {
                        listener.onException(e);
                    }
                }
            });
        }
    }
}
