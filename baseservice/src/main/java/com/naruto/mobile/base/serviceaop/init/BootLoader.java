package com.naruto.mobile.base.serviceaop.init;


import com.naruto.mobile.base.serviceaop.NarutoApplicationContext;

/**
 * Created by xinming.xxm on 2016/5/13.
 */
public interface BootLoader {

    /**
     * ��ȡ������
     * @return
     */
    NarutoApplicationContext getContext();
    /**
     * ����
     */
    void load();

}
