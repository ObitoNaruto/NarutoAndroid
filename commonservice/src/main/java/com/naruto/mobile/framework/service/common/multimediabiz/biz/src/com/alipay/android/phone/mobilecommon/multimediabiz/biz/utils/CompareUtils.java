package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils;

/**
 * 比较工具类
 * Created by jinmin on 15/7/14.
 */
public class CompareUtils {
    public static boolean in(Object src, Object... array) {
        if (src != null && array != null && array.length > 0) {
            for (int i = 0; i < array.length; i++) {
                if (src.equals(array[i])) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean equals(Object a, Object b) {
        return (a == null && b == null) ||
                (a != null && a.equals(b)) ||
                (b != null && b.equals(a));
    }
}
