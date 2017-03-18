package com.naruto.mobile.base.serviceaop.service;

/**
 * Created by xinming.xxm on 2016/5/15.
 */

import android.content.SharedPreferences;
import android.os.Bundle;

import com.naruto.mobile.base.serviceaop.MicroContent;
import com.naruto.mobile.base.serviceaop.NarutoApplicationContext;


public abstract class MicroService implements MicroContent {

    private NarutoApplicationContext narutoApplicationContext;

    /**
     * �Ƿ��Ѿ������onCreate()�����ã�onDestroy()��û�б����ã�
     * @return
     */
    public abstract boolean isActivated();

    /**
     * ����
     *
     * @param params ����
     *
     */
    public abstract void create(Bundle params);

    /**
     *����
     *
     * @param params ����
     */
    public abstract void destroy(Bundle params);


    /**
     * �����ص�
     *
     * @param params ����
     */
    protected abstract void onCreate(Bundle params);

    /**
     * ���ٻص�
     *
     * @param params ����
     */
    protected abstract void onDestroy(Bundle params);

    public void attachContext(NarutoApplicationContext narutoApplicationContext){
        this.narutoApplicationContext = narutoApplicationContext;
    }

    public NarutoApplicationContext getNarutoApplicationContext(){
        return this.narutoApplicationContext;
    }

    @Override
    public void saveState(SharedPreferences.Editor editor) {

    }

    @Override
    public void restoreState(SharedPreferences preferences) {

    }
}
