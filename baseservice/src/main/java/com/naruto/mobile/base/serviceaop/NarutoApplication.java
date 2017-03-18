package com.naruto.mobile.base.serviceaop;

import android.app.Application;
import android.util.Log;

/**
 * Created by xinming.xxm on 2016/5/13.
 */
public class NarutoApplication extends Application {
    /**
     * android������
     */
    private static NarutoApplication mInstance;

    /**
     *��Ŀ������
     */
    private NarutoApplicationContext mNarutoApplicationContext;


    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;//android�����ĳ�ʼ��
        try{
            //NarutoApplicationContextImpl��NarutoApplicationContext��ʵ����
            mNarutoApplicationContext = (NarutoApplicationContext) Class.forName("com.naruto.mobile.base.serviceaop.NarutoApplicationContextImpl").newInstance();
            mNarutoApplicationContext.attachContext(this);//NarutoApplicationContext��android�����Ļ�����Ȼ����г�ʼ��
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
}
