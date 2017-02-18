package com.naruto.mobile.LogReport.PowerLogReport.libray.save;


import com.naruto.mobile.LogReport.PowerLogReport.libray.encryption.IEncryption;

/**
 * 保存日志与崩溃信息的接口
 * Created by wenmingvs on 2016/7/7.
 */
public interface ISave {

    void writeLog(String tag, String content);

    void writeCrash(Thread thread, Throwable ex, String tag, String content);

    void setEncodeType(IEncryption encodeType);

}
