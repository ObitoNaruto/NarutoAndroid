package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils;

import android.text.TextUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by jinmin on 15/6/29.
 */
public class ReflectUtils {
    public static Class getClass(String className) {
        try {
            return Class.forName(className);
        } catch (Exception e) {
            return null;
        }
    }

    public static Method getMethod(Class clazz, String methodName, Class<?>... args) {
        if (clazz != null) {
            try {
                return clazz.getDeclaredMethod(methodName, args);
            } catch (Exception e) {
            }
        }
        return null;
    }

    public static Method getMethod(String clazzName, String methodName, Class<?>... args) {
        if (!TextUtils.isEmpty(clazzName)) {
            Class clazz = getClass(clazzName);
            if (clazz != null) {
                return getMethod(clazz, methodName, args);
            }
        }
        return null;
    }

    public static <T> T invoke(Object obj, Method method, Object... args) {
        if (method != null) {
            try {
                return (T) method.invoke(obj, args);
            } catch (Exception e) {
            }
        }
        return null;
    }

    public static Field getField(Class clazz, String fieldName) {
        if (clazz != null) {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (Exception e) {
            }
        }
        return null;
    }

    public static boolean setField(Object obj, Field field, Object fieldValue) {
        if (obj != null && field != null) {
            try {
                field.setAccessible(true);
                field.set(obj, fieldValue);
                return true;
            } catch (Exception e) {
            }
        }
        return false;
    }
}
