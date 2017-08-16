package com.naruto.mobile.base.log.logging;

import android.util.Log;

import com.naruto.mobile.base.framework.info.AppInfo;


/**
 * LogCat日志
 * 
 */
public class LogCatLog {
    /**
     * 是否打开LogCat输出
     */
    private static boolean mSwitch;

    /**
     * 初始化LogCat日志
     * 
     * @param context 上下文
     * @return
     */
    public static synchronized void init() {
    	//TODO: should change the switch when release.
        mSwitch = AppInfo.getInstance().isDebuggable();
    }
    
    /**
     * 用来代替Throwable.printStackTrace ， 未来会增加的处理
     * 
     * @param e 异常
     * @return
     */
    public static void printStackTraceAndMore(Throwable e) {
    	e.printStackTrace();
    }

    /**
     * @param tag tag
     * @param msg 消息
     */
    public static void i(String tag, String msg) {
        if (mSwitch && tag != null && msg != null)
            Log.i(tag, msg);
    }

    /**
     * @param tag tag
     * @param msg 消息
     */
    public static void e(String tag, String msg) {
    	if (mSwitch && tag != null && msg != null)
            Log.e(tag, msg);
    }
    
    /**
     * 记录tr 的getMessage信息
     * 
     * @param tag tag
     * @param tr 异常
     */
    public static void e(String tag,Throwable tr) {
    	e(tag, tr==null?"":tr.getMessage());
    }
    
    /**
     * 错误级别日志
     * 
     * @param tag
     * @param msg
     * @param tr
     */
    public static void e(String tag, String msg,Throwable tr) {
        if (mSwitch && tag != null && msg != null)
            Log.e(tag, msg,tr);
    }

    /**
     * @param tag tag
     * @param msg 消息
     */
    public static void d(String tag, String msg) {
        if (mSwitch && tag != null && msg != null)
            Log.d(tag, msg);
    }

    /**
     * @param tag tag
     * @param msg 消息
     */
    public static void v(String tag, String msg) {
        if (mSwitch && tag != null && msg != null)
            Log.v(tag, msg);
    }

    /**
     * 
     * @param tag tag
     * @param msg 消息
     */
    public static void w(String tag, String msg) {
        if (mSwitch && tag != null && msg != null)
            Log.w(tag, msg);
    }


    /**
     * 警告级别：记录tr 的getMessage信息
     * @param tag tag
     * @param tr 异常
     */
    public static void w(String tag, Throwable tr) {
    	w(tag, tr==null?"":tr.getMessage());
    }
    
    /**
     * @return 是否打开LogCat输出
     */
    public static boolean isSwitch() {
        return mSwitch;
    }
    
}
