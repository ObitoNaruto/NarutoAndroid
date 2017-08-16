package com.naruto.mobile.base.log.logging;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;

import com.naruto.mobile.base.framework.info.AppInfo;
import com.naruto.mobile.base.framework.info.DeviceInfo;
//import com.naruto.mobile.framework.rpc.myhttp.common.info.AppInfo;
//import com.naruto.mobile.framework.rpc.myhttp.common.info.DeviceInfo;

/**
 * 性能记录器
 * 
 * <pre>
 * 记录在xxxxx/file/log或者sd卡上的xxxx/log目录下的pref.csv文件
 * 
 * 格式如下：
 * 时间，内存占用，tag串
 * </pre>
 */
public class PerformanceLog extends BaseLogger {
    private static final String LOG_FILE_NAME = "perf.csv";

    private static PerformanceLog mInstance;

    private String mPath;
    
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    private PerformanceLog() {
        super();
    }

    /**
     * 获取性能记录器实例
     * 
     * @return 性能记录器
     */
    public static synchronized PerformanceLog getInstance() {
        if (mInstance == null)
            throw new IllegalStateException(
                "PerformanceLog must be create by call createInstance(Context context)");
        return mInstance;
    }

    /**
     * 创建性能记录器
     * 
     * @return
     */
    public static synchronized PerformanceLog createInstance() {
        if (mInstance == null) {
            mInstance = new PerformanceLog();
            if (AppInfo.getInstance().isDebuggable()) {//debug状态
                mInstance.init();
            }
        }
        return mInstance;
    }

    @Override
    protected void init() {
        mPath = initPath();
        File file = new File(mPath);
        boolean exist = file.exists();
        try {
            FileWriter fileWriter = new FileWriter(file, true);
            mPrintWriter = new PrintWriter(fileWriter);
        } catch (IOException e) {
            LogCatLog.e("PerformanceLog", e+"");
            return;
        }
        if (!exist) {
            mPrintWriter.format("%s,%s,%s", "time", "mem", "tag");
            mPrintWriter.println();
        }
        //时间,内存,Tag
        mSpecifiers = "%s,%d,%s";
    }

    private String initPath() {
        String path = DeviceInfo.getInstance().getExternalStoragePath("log");
        if (path != null) {
            return path + File.separatorChar + LOG_FILE_NAME;
        }
        path = AppInfo.getInstance().getFilesDirPath() + File.separatorChar + "log";
        File file = new File(path);
        if (!file.exists() && !file.mkdir()) {
            LogCatLog.e("PerformanceLog", "fail to creat log dir:" + path);
            return path + File.separatorChar + LOG_FILE_NAME;
        } else if (!file.isDirectory()) {
            LogCatLog.e("PerformanceLog", "log dir exist,but not directory:" + path);
            throw new RuntimeException("log dir exist,but not directory:" + path);
        } else {
            return path + File.separatorChar + LOG_FILE_NAME;
        }
    }

    /**
     * 记录一条性能日志
     * 
     * @param tag tag串
     */
    public void log(String tag) {
    	if (!AppInfo.getInstance().isDebuggable() || mPrintWriter == null )
            return;
    	
        String time = this.sdf.format(System.currentTimeMillis());
        synchronized (mPrintWriter) {
            doLog(time, Runtime.getRuntime().maxMemory(), tag);
        }
    }

}
