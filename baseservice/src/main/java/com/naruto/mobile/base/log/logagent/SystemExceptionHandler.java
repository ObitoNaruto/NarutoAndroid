package com.naruto.mobile.base.log.logagent;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import android.content.Context;

/**
 * 框架层，异常处理器，，目前主要处理：
 * RuntimeException 和 RpcException
 * 
 * @author sanping.li@alipay.com
 *
 */
public class SystemExceptionHandler{
    private Context mContext;
    private static SystemExceptionHandler INSTANCE;
    private String previousException = "";//上一次的错误信息

    private SystemExceptionHandler() {
    }

    public static SystemExceptionHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SystemExceptionHandler();
        }
        return INSTANCE;
    }
    

    public void init(Context ctx) {
        mContext = ctx;
    }



    /**
     * 保存异常信息到文件方便上传
     * @param ex	异常对象
     * @param exceptionType 异常类型	
     */
    public void saveErrorInfoToFile(Throwable ex, String exceptionType) {
        String result = getExceptionMsg(ex);
        if (result != null) {
            StorageStateInfo storageStateInfo = StorageStateInfo.getInstance();
            AlipayLogAgent.onError(mContext, result,
                storageStateInfo.getValue(Constants.STORAGE_CURRENTVIEWID), exceptionType);
        }
    }

    /**
     * 保存网络信息到文件
     * @param ex
     * @param exceptionType
     */
    public void saveConnInfoToFile(Throwable ex, String exceptionType) {
        String result = getExceptionMsg(ex);
        if (result != null && result.equals(previousException)) {
            return;
        }

        previousException = result;
        if (result != null) {
            StorageStateInfo storageStateInfo = StorageStateInfo.getInstance();
            AlipayLogAgent.onError(mContext,
                "operationType=" + storageStateInfo.getValue(Constants.STORAGE_REQUESTTYPE) + "|"
                        + result, storageStateInfo.getValue(Constants.STORAGE_CURRENTVIEWID),
                exceptionType);
        }
    }

    
    
    /**
     * 解析异常信息
     * @param ex
     * @return
     */
    private String getExceptionMsg(Throwable ex) {
        Writer info = new StringWriter();
        PrintWriter printWriter = new PrintWriter(info);

        Throwable cause = ex.getCause();
        if (cause == null) {
            ex.printStackTrace(printWriter);
        }

        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        return info.toString();
    }
}