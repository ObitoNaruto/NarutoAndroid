package com.naruto.mobile.base.RainbowBride.core;


import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.webkit.WebView;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

/**
 * 符合注入的方法的格式:
 * public static void ***(WebView webView, JSONObject data, JsCallback callback){
 * //...
 * }
 */
public class NativeMethodInjectHelper {
    private volatile static NativeMethodInjectHelper sInstance;
    private ArrayMap<String, ArrayMap<String, Method>> mArrayMap;
    private List<Class<?>> mInjectClasses;

    public static NativeMethodInjectHelper getInstance() {
        NativeMethodInjectHelper instance = sInstance;
        if (instance == null) {
            synchronized (NativeMethodInjectHelper.class) {
                instance = sInstance;
                if (instance == null) {
                    instance = new NativeMethodInjectHelper();
                    sInstance = instance;
                }
            }
        }
        return instance;
    }

    private NativeMethodInjectHelper() {
        mArrayMap = new ArrayMap<>();
        mInjectClasses = new ArrayList<>();
    }

    public NativeMethodInjectHelper clazz(Class<?> clazz) {
        if (clazz == null)
            throw new NullPointerException("NativeMethodInjectHelper:The clazz can not be null!");
        mInjectClasses.add(clazz);
        return this;
    }

    public void inject() {
        int size = mInjectClasses.size();
        if (size != 0) {
            mArrayMap.clear();
            for (int i = 0; i < size; i++) {
                putMethod(mInjectClasses.get(i));
            }
            mInjectClasses.clear();
        }
    }

    public Method findMethod(String className, String methodName) {
        if (TextUtils.isEmpty(className) || TextUtils.isEmpty(methodName))
            return null;
        if (mArrayMap.containsKey(className)) {
            ArrayMap<String, Method> arrayMap = mArrayMap.get(className);
            if (arrayMap == null)
                return null;
            if (arrayMap.containsKey(methodName)) {
                return arrayMap.get(methodName);
            }
        }
        return null;
    }

    /**
     * 安全性验证，通过自定义一套规则，还有符合这些规则的Js才能调用
     * 规则：
     * 1、Native方法包含public static void 这些修饰符（当然还可能有其它的，如：synchronized）
     2、Native方法的参数数量和类型只能有这三个：WebView、JSONObject、JsCallback。为什么要传入这三个参数呢？
     2.1、第一个参数是为了提供一个WebView对象，以便获取对应Context和执行WebView的一些方法
     2.2、第二个参数就是Js中传入过来的参数，这个肯定要的
     2.3、第三个参数就是当Native方法执行完毕后，把执行后的结果回调给Js对应的方法中
     * @param clazz
     */
    private void putMethod(Class<?> clazz) {
        if (clazz == null)
            return;
        ArrayMap<String, Method> arrayMap = new ArrayMap<>();
        Method method;
        Method[] methods = clazz.getDeclaredMethods();
        int length = methods.length;
        for (int i = 0; i < length; i++) {
            method = methods[i];
            int methodModifiers = method.getModifiers();//获取修饰符
            //修饰符为：public static void
            if ((methodModifiers & Modifier.PUBLIC) != 0 && (methodModifiers & Modifier.STATIC) != 0 && method.getReturnType() == void.class) {
                Class<?>[] parameterTypes = method.getParameterTypes();//获取method的参数类型数组
                if (parameterTypes != null && parameterTypes.length == 3) {
                    if (WebView.class == parameterTypes[0] && JSONObject.class == parameterTypes[1] && JsCallback.class == parameterTypes[2]) {
                        arrayMap.put(method.getName(), method);
                    }
                }
            }
        }
        mArrayMap.put(clazz.getSimpleName(), arrayMap);
    }
}
