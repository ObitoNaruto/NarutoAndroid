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
     * 是否已经被激活（onCreate()被调用，onDestroy()还没有被调用）
     * @return
     */
    public abstract boolean isActivated();

    /**
     * 创建
     *
     * @param params 参数
     *
     */
    public abstract void create(Bundle params);

    /**
     *销毁
     *
     * @param params 参数
     */
    public abstract void destroy(Bundle params);


    /**
     * 创建回调
     *
     * @param params 参数
     */
    protected abstract void onCreate(Bundle params);

    /**
     * 销毁回调
     *
     * @param params 参数
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
