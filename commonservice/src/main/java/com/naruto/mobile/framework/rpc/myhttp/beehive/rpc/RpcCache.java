package com.naruto.mobile.framework.rpc.myhttp.beehive.rpc;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.naruto.mobile.base.log.logging.LoggerFactory;

import java.lang.reflect.Type;

/**
 * rpc缓存功能，提供rpc缓存操作接口
 * Created by zhanqu.awb on 15/10/16.
 */
public class RpcCache {

    public static Object get(String cacheKey, Class<?> runnableClass) {
        Object result = null;
        try {
            Class[] args = new Class[1];
            args[0] = Object[].class;
            Type retType = runnableClass.getDeclaredMethod("execute", args).getGenericReturnType();
            String v = ""/*SimpleSecurityCacheUtil.getString(cacheKey)*/;
            if (retType == String.class) {
                result = v;
            } else {
                if (!TextUtils.isEmpty(v)) {
                    result = JSON.parseObject(v, retType);
                }
            }
        } catch (Exception ex) {
//            LoggerFactory.getTraceLogger().warn("RpcRunner", ex);
        }
        return result;
    }

    public static void put(Object result, String cacheKey) {
        try {
            String s = JSON.toJSONString(result);
//            SimpleSecurityCacheUtil.setString(cacheKey, s);
        } catch (Exception ex) {
//            LoggerFactory.getTraceLogger().warn("RpcRunner", ex);
        }
    }

}
