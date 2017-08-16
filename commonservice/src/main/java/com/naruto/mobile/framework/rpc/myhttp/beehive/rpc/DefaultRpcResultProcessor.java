package com.naruto.mobile.framework.rpc.myhttp.beehive.rpc;


import java.lang.reflect.Field;

/**
 * 默认的rpc结果处理器
 * Created by zhanqu.awb on 15/10/22.
 */
public class DefaultRpcResultProcessor extends BaseRpcResultProcessor<Object> {

    @Override
    public boolean isSuccess(Object result) {
        return RpcUtil.isRpcSuccess(result);
    }

    @Override
    public String convertResultText(Object result) {
        String v = "";
        try {
            Field f = RpcUtil.getFieldByReflect(v, ResultActionProcessor.RESULT_VIEW);
            if (f != null) {
                v = (String) f.get(result);
            } else {
//                LoggerFactory.getTraceLogger().info(RpcConstant.TAG, "resultView字段不存在");
            }
        } catch (Exception ex) {
//            LoggerFactory.getTraceLogger().warn(RpcConstant.TAG, ex);
        }
        return v;
    }

//    @Override
//    public boolean isShowToastOrAlert(Object result) {
//        return false;
//    }

}
