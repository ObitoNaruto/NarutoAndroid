package com.naruto.mobile.framework.rpc.myhttp.beehive.rpc;//package com.alipay.mobile.beehive.rpc;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.TypeReference;
//import com.alipay.mobile.beehive.rpc.model.ResultAction;
//import com.alipay.mobile.common.logging.api.LoggerFactory;
//
//import android.text.TextUtils;
//
//import java.lang.reflect.Field;
//
///**
// * rpc结果校验 （目前只在debug状态下使用）
// * Created by zhanqu.awb on 15/7/17.
// */
//public class RpcResultValidator {
//
//    /**
//     * 判断rpc结果是否标准rpc response模型
//     * @param v
//     */
//    public static void assertValidResult(Object v) {
//        if (!isValidFieldPrimitiveType(v, "success", boolean.class)) {
//            throw new IllegalArgumentException("rpc result must have public [boolean success] field");
//        }
//
//        Field action = RpcUtil.getFieldByReflect(v, RpcConstant.RPC_RESULT_FOLLOW_ACTION);
//        if (action == null || !RpcUtil.isFieldPublic(action) ||
//                !String.class.isAssignableFrom(action.getType())) {
//            if (!RpcSettings.supportShowType) {
//                throw new IllegalArgumentException("rpc result must have public [json followAction] field");
//            } else {
//                LoggerFactory.getTraceLogger().warn("RpcRunner",
//                        "rpc result should have public [json followAction] field");
//            }
//        } else {
//            String resultActionValue = null;
//            try {
//                resultActionValue = (String) action.get(v);
//            } catch (Exception ex) {
//                LoggerFactory.getTraceLogger().warn("RpcRunner", ex);
//            }
//            if (!TextUtils.isEmpty(resultActionValue)) {
//                assertValidResultAction(resultActionValue);
//            }
//        }
//    }
//
//    /**
//     * 校验是否能json转换成功, 转换失败将抛异常
//     * @param v
//     */
//    public static void assertValidResultAction(String v) {
//        JSON.parseObject(v, new TypeReference<ResultAction>() {
//        });
//    }
//
//    private static boolean isValidField(Object v, String fieldName, Class<?> cls) {
//        Field field = RpcUtil.getFieldByReflect(v, fieldName);
//        return field != null && RpcUtil.isFieldPublic(field) && cls.isAssignableFrom(field.getType());
//    }
//
//    private static boolean isValidFieldPrimitiveType(Object v, String fieldName, Object type) {
//        Field field = RpcUtil.getFieldByReflect(v, fieldName);
//        return field != null && RpcUtil.isFieldPublic(field) && field.getType().equals(type);
//    }
//
//}
