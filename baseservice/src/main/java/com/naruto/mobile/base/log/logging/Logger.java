package com.naruto.mobile.base.log.logging;

/**
 * 客户端日志
 */
public class Logger {

    /**
     * Tag
     */
    private String tag;

    private String getTag() {
        return tag;
    }

    protected Logger(String tag) {
        this.tag = tag;
    }

    private String getTreadId() {
        return "[" + Thread.currentThread().getId() + "]";
    }

    /**
     * 用来代替Throwable.printStackTrace ， 未来会增加的处理
     * 
     * @param e 异常
     * @return
     */
    public void printStackTraceAndMore(Throwable e) {
        e.printStackTrace();
    }

    /**
     * @param msg 消息
     */
    public void i(String msg) {
        info(msg);
    }
    
    /**
     * @param msg 消息
     */
    public void info(Object msg) {
        LogCatLog.i(getTag(), getTreadId() + msg);
    }
    

    /**
     * @param msg 消息
     */
    public void e(String msg) {
        error(msg);
    }
    
    /**
     * @param msg 消息
     */
    public void error(Object msg) {
        LogCatLog.e(getTag(), getTreadId() + msg);
    }
    

    /**
     * 错误级别日志
     * 
     * @param msg
     * @param tr
     */
    public void e(Object msg, Throwable tr) {
        error(msg, tr);
    }
    
    /**
     * 错误级别日志
     * 
     * @param msg
     * @param tr
     */
    public void error(Object msg, Throwable tr) {
        LogCatLog.e(getTag(), getTreadId() + msg, tr);
    }
    

    /**
     * @param msg 消息
     */
    public void d(String msg) {
        debug(msg);
    }
    
    /**
     * 打印debug级别日志
     * 
     * @param msg
     */
    public void debug(String msg) {
        LogCatLog.d(getTag(), getTreadId() + msg);
    }

    /**
     * @param msg 消息
     */
    public void v(String msg) {
        LogCatLog.v(getTag(), getTreadId() + msg);
    }

    /**
     * @param msg 消息
     */
    public void w(String msg) {
        warn(msg);
    }
    
    /**
     * @param msg 消息
     */
    public void warn(Object msg) {
        LogCatLog.w(getTag(), getTreadId() + msg);
    }
    

}
