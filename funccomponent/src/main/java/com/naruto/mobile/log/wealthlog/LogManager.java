package com.naruto.mobile.log.wealthlog;

public class LogManager {

    private static BaseLog mBaseLog = new DefaultLogImpl();

    public static void setBaseLog(BaseLog newBaseLog) {
        mBaseLog = newBaseLog;
    }

    public static BaseLog getBaseLog() {
        return mBaseLog;
    }
}
