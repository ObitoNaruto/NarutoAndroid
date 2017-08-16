package com.naruto.mobile.base.log.logging;

import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicBoolean;

import com.naruto.mobile.base.framework.SerialExecutor;
//import com.naruto.mobile.framework.rpc.myhttp.utils.SerialExecutor;

/**
 * 日志记录
 * 
 */
public abstract class BaseLogger {
    /**
     * 输出
     */
    protected PrintWriter mPrintWriter;
    /**
     * 格式化format
     */
    protected String mSpecifiers;
    /**
     * 顺序执行器
     */
    private SerialExecutor mSerialExecutor;
    /**
     * 缓冲区是否写入到文件中
     */
    private AtomicBoolean mFlushing = new AtomicBoolean();


    public BaseLogger() {
        mSerialExecutor = new SerialExecutor("BaseLogger");
        mFlushing.set(false);
    }

    /**
     * 记录一条日志
     *
     * @param params 日志参数
     */
    protected void doLog(Object... params) {
        if (mPrintWriter != null) {
            mPrintWriter.format(mSpecifiers, params);
            mPrintWriter.println();
            flush();
        }
    }

    private void flush() {
        if (mFlushing.get())
            return;
        mFlushing.set(true);//调度写缓冲
        mSerialExecutor.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    LogCatLog.e("BaseLogger", e+"");
                }
                //写缓冲
                synchronized (mPrintWriter) {
                    mPrintWriter.flush();
                }
                mFlushing.set(false);//写缓冲调度执行完毕
            }
        });
    }

    protected abstract void init();
}
