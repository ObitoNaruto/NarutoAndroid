package com.naruto.mobile.base.serviceaop.service;

/**
 * �������
 * Created by xinming.xxm on 2016/5/15.
 */
public interface ServicesLoader {
    void load();

    /** ��bootfinish֮��ִ�� */
    public void afterBootLoad();
}
