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
     *app上下文
     */
    private NarutoApplicationContext mNarutoApplicationContext;


    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;//android上下文初始化
        try{
            //NarutoApplicationContextImpl??NarutoApplicationContext???????
            mNarutoApplicationContext = (NarutoApplicationContext) Class.forName("com.naruto.mobile.base.serviceaop.NarutoApplicationContextImpl").newInstance();
            mNarutoApplicationContext.attachContext(this);//NarutoApplicationContext
        }
        catch (Exception e){
            Log.e("xxm", "NarutoApplicationContextImpl newInstance failed!", e);
            e.printStackTrace();
        }

    }

    public NarutoApplicationContext getNarutoApplicationContext() {
        return mNarutoApplicationContext;
    }

    public static NarutoApplication getInstance(){
        return mInstance;
    }

    @Override
    public void onTerminate() {
        mNarutoApplicationContext.clearState();
        super.onTerminate();
    }
}
