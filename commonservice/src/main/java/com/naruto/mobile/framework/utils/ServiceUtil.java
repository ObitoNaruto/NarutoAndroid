package com.naruto.mobile.framework.utils;


import com.naruto.mobile.base.serviceaop.NarutoApplication;

/**
 */
public class ServiceUtil {

    /**
     * 获取服务
     */
    public static <T> T getServiceByInterface(Class clazz) {
        return (T) NarutoApplication
                .getInstance().getNarutoApplicationContext().findServiceByInterface(clazz
                        .getName());
    }

}
