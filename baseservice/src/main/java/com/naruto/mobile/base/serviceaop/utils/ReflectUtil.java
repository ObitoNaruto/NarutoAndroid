package com.naruto.mobile.base.serviceaop.utils;

/**
 * 反射工具
 */
public class ReflectUtil {
    /**
     * 反射对象
     *
     * @param className 类名
     * @return 对象
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static final Object getInstance(ClassLoader classLoader, String className) throws ClassNotFoundException,
            IllegalAccessException,
            InstantiationException {
        Class<?> clazz = classLoader.loadClass(className);
        return clazz.newInstance();
    }

    /**
     * 反射对象
     *
     * @param clazz 类
     * @return 对象
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static final Object getInstance(Class<?> clazz) throws IllegalAccessException,
            InstantiationException {
        return clazz.newInstance();
    }
}
