package com.naruto.mobile.framework.rpc.myhttp.beehive.rpc;

/**
 * rpc结果 处理抽象基类 如果有自己的服务端数据结构，继承该类，并实现处理业务成功及空数据
 */
public abstract class BaseRpcResultProcessor<ResultType> {

    /**
     * 根据rpc结果判断, rpc执行是否 业务意义上的成功
     */
    public abstract boolean isSuccess(ResultType result);

    /**
     * 转换服务端返回的待显示的文案, 如(toast,alert,异常界面中的文案等)
     */
    public abstract String convertResultText(ResultType result);
//    /**
//     * rpc业务失败时，在未使用followAction时判断是显示toast还是alert
//     */
//    public abstract boolean isShowToastOrAlert(ResultType result);

    /**
     * 根据rpc结果判断, rpc执行是否 业务意义上的空记录(列表类型页面常用)
     */
    public boolean isEmpty(ResultType result) {
        return false;
    }

}
