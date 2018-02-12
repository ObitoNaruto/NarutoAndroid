package com.naruto.mobile.base.serviceaop;

import android.app.Application;
import android.util.Log;

/**
 * Created by xinming.xxm on 2016/5/13.
 */
public class NarutoApplication extends Application {
    /**
     * android上下文
     */
    private static NarutoApplication mInstance;

    /**
     * app上下文(自定义)
     */
    private NarutoApplicationContext mNarutoApplicationContext;

    @Override
    public void onCreate() {
        super.onCreate();
        //android上下文初始化
        mInstance = this;
        try {
            //这么做是为了满足单一原则，NarutoApplicationContext维护了扩展的android服务管理
            //反射形式生成对象目的：规避库循环引用的问题
            //NarutoApplicationContextImpl初始化
            mNarutoApplicationContext = (NarutoApplicationContext) Class.forName("com.naruto.mobile.base.serviceaop.NarutoApplicationContextImpl").newInstance();
            mNarutoApplicationContext.attachContext(this);//NarutoApplicationContext关联上Application上下文
        } catch (Exception e) {
            Log.e("xxm", "NarutoApplicationContextImpl newInstance failed!", e);
            e.printStackTrace();
        }
    }

    public NarutoApplicationContext getNarutoApplicationContext() {
        return mNarutoApplicationContext;
    }

    public static NarutoApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onTerminate() {
        mNarutoApplicationContext.clearState();
        super.onTerminate();
    }
}
