package com.naruto.mobile.framework.rpc.myhttp.beehive.rpc;

import android.text.TextUtils;


import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.naruto.mobile.base.serviceaop.NarutoApplication;
import com.naruto.mobile.framework.biz.common.RpcService;
import com.naruto.mobile.framework.rpc.myhttp.common.RpcException;

/**
 * rpc工具
 * Created by zhanqu.awb on 15/2/3.
 */
public class RpcUtil {

    /**
     * 是否无网络
     * @param ex
     * @return
     */
    public static boolean isNetworkException(Exception ex) {
        if (!(ex instanceof RpcException)) {
            return false;
        }

        RpcException e = (RpcException) ex;
        if (e.getCode() == RpcException.ErrorCode.CLIENT_NETWORK_ERROR
                || e.getCode() == RpcException.ErrorCode.CLIENT_NETWORK_UNAVAILABLE_ERROR
                || isNetworkSlow(ex)) {
            // 网络连接不可用
            return true;
        } else {
            return false;
        }
    }

    /**
     * 是否网络超时
     * @param ex
     * @return
     */
    public static boolean isNetworkSlow(Exception ex) {
        if (!(ex instanceof RpcException)) {
            return false;
        }

        RpcException e = (RpcException) ex;
        if ((e.getCode() == RpcException.ErrorCode.CLIENT_NETWORK_SOCKET_ERROR
                || e.getCode() == RpcException.ErrorCode.CLIENT_NETWORK_CONNECTION_ERROR)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 依赖反射判断rpc结果是否成功
     */
    public static boolean isRpcSuccess(Object result) {
        if (result != null) {
            try {
                Field f = getFieldByReflect(result, "success");
                return f.getBoolean(result);
            } catch (Exception globalException) {
//                LoggerFactory.getTraceLogger().warn("RpcRunner", globalException);
            }
        }
        return false;
    }

    public static String getRpcResultCode(Object result) {
        Field f = getFieldByReflect(result, "resultCode");
        if (f != null) {
            try {
                return (String) f.get(result);
            } catch (Exception globalException) {
//                LoggerFactory.getTraceLogger().warn("RpcRunner", globalException);
            }
        }
        return null;
    }

//    public static String getRpcResultView(Object result) {
//        Field f = getFieldByReflect(result, "resultView");
//        if (f == null) {
//            f = getFieldByReflect(result, "resultDesc");
//        }
//        if (f != null) {
//            try {
//                f.setAccessible(true);
//                return (String) f.get(result);
//            } catch (Exception globalException) {
//                LoggerFactory.getTraceLogger().warn("RpcRunner", globalException);
//            }
//        }
//        return null;
//    }
//
//    public static int getRpcShowType(Object result) {
//        Field f = getFieldByReflect(result, "showType");
//        if (f != null) {
//            try {
//                f.setAccessible(true);
//                return (Integer) f.get(result);
//            } catch (Exception globalException) {
//                LoggerFactory.getTraceLogger().warn("RpcRunner", globalException);
//            }
//        }
//        return Integer.MIN_VALUE;
//    }

    public static Field getFieldByReflect(Object result, String fieldName) {
        if (result != null) {
            Class<?> clz = result.getClass();
            try {
                Field[] fields = clz.getFields();
                if (fields == null) {
                    return null;
                }
                for (Field f : fields) {
                    if (TextUtils.equals(fieldName, f.getName())) {
                        return f;
                    }
                }
            } catch (Exception ex) {
//                LoggerFactory.getTraceLogger().warn("RpcRunner", ex);
            }
        }
        return null;
    }

    /**
     * 工具, 拱外部使用
     * @param type
     * @param <T>
     * @return
     */
    public static <T> T getRpcProxy(Class<T> type) {
        RpcService rpcService = NarutoApplication.getInstance().getNarutoApplicationContext()
                .findServiceByInterface(RpcService.class.getName());
        return rpcService.getRpcProxy(type);
    }

    public static boolean isFieldPublic(Field f) {
        return (f.getModifiers() & Modifier.PUBLIC) > 0;
    }

}
