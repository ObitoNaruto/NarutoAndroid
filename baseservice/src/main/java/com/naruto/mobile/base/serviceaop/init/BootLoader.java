package com.naruto.mobile.base.serviceaop.init;


import com.naruto.mobile.base.serviceaop.NarutoApplicationContext;

/**
 * Created by xinming.xxm on 2016/5/13.
 */
public interface BootLoader {

    /**
     * 获取上下文
     * @return
     */
    NarutoApplicationContext getContext();
    /**
     * 加载
     */
    void load();

}
