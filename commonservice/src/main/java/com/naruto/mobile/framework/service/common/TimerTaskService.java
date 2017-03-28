package com.naruto.mobile.framework.service.common;

import android.os.Handler;

import com.naruto.mobile.base.serviceaop.service.CommonService;

/**
 * 周期性调度服务，只能以基准周期(10分钟)的倍数提供调度，在app程序退出后停止
 *
 * @author xuxinming
 *
 */
public abstract class TimerTaskService extends CommonService {

    /**
     * 注册onTickListener, onTickListener的回调方法会在Handler的线程消息队列里被调用 ，调用周期为TimeUnit*基准周期
     *
     * @param listener 被周期回调的listener
     * @param TimeUnit 基准周期倍数
     * @param handler  回调函数执行的线程handler
     * @return 注册是否成功，true注册成功，false表示listener已经被注册
     * @throws NullPointerException     如果listener为null
     * @throws IllegalArgumentException 如果基准周期倍数为0或者负数
     */
    public abstract boolean registerListener(OnTickListener listener, int TimeUnit, Handler handler)
            throws NullPointerException, IllegalArgumentException;

    /**
     * 注册onTickListener, onTickListener的回调方法会在非UI线程上被调用 ，调用周期为TimeUnit*基准周期
     *
     * @param listener 被周期回调的listener
     * @param TimeUnit 基准周期倍数
     * @return 注册是否成功，true注册成功，false表示listener已经被注册
     * @throws NullPointerException     如果listener为null
     * @throws IllegalArgumentException 如果基准周期倍数为0或者负数
     */
    public abstract boolean registerListener(OnTickListener listener, int TimeUnit)
            throws NullPointerException, IllegalArgumentException;


    /**
     * 反注册onTickListener
     *
     * @param listener 反注册的listener， 如果为null没有任何效果
     * @return 反注册结果
     */
    public abstract boolean unregisterListener(OnTickListener listener);


    /**
     * 周期性回调方法的接口
     */
    public interface OnTickListener {

        void onTick();

        void onException(Exception e);
    }
}