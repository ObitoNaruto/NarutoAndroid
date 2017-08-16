package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils;

/**
 * 转换工具
 * Created by jinmin on 15/6/13.
 */
public class ConvertUtils {
    public static Integer parseInt(Object object, int defaultVal) {
        try {
            return Integer.parseInt(String.valueOf(object));
        } catch (Exception e) {
            return defaultVal;
        }
    }
}
