package com.naruto.mobile.base.serviceaop.service;

/**
 * 服务加载
 * Created by xinming.xxm on 2016/5/15.
 */
public interface ServicesLoader {
    void load();

    /** 在bootfinish之后执行 */
    public void afterBootLoad();
}
