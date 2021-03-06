package com.naruto.mobile.log.KLog.test;

import android.app.Application;

import com.naruto.mobile.BuildConfig;
import com.naruto.mobile.log.KLog.KLog;


/**
 * Created by zhaokaiqiang on 15/11/14.
 */
public class KLogApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //在Application初始化的时候就对KLog进行初始化，因为KLog的初始化过程非常的简单，因此不会影响App的启动速度
//        KLog.init(BuildConfig.LOG_DEBUG);
        KLog.init(BuildConfig.LOG_DEBUG, "Kai");//传入参数，是否打印日志标记和全局TAG

        //对于全局打印开关，推荐使用Gradle变量进行控制，这样当发布release版本的时候，就会自动的关闭Log的输出，防止信息的泄露和影响效率。
    }
}
