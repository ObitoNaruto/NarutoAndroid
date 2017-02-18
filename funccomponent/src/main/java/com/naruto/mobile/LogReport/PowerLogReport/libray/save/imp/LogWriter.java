package com.naruto.mobile.LogReport.PowerLogReport.libray.save.imp;


import com.naruto.mobile.LogReport.PowerLogReport.libray.LogReport;
import com.naruto.mobile.LogReport.PowerLogReport.libray.save.ISave;
import com.naruto.mobile.LogReport.PowerLogReport.libray.util.LogUtil;

/**
 * 用于写入Log到本地
 * Created by wenmingvs on 2016/7/9.
 */
public class LogWriter {
    private static LogWriter mLogWriter;
    private static ISave mSave;

    private LogWriter() {
    }


    public static LogWriter getInstance() {
        if (mLogWriter == null) {
            synchronized (LogReport.class) {
                if (mLogWriter == null) {
                    mLogWriter = new LogWriter();
                }
            }
        }
        return mLogWriter;
    }


    public LogWriter init(ISave save) {
        mSave = save;
        return this;
    }

    public static void writeLog(String tag, String content) {
        LogUtil.d(tag, content);
        mSave.writeLog(tag, content);
    }
}