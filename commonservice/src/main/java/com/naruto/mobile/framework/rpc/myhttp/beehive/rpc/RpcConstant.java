package com.naruto.mobile.framework.rpc.myhttp.beehive.rpc;

/**
 * 常量
 * Created by zhanqu.awb on 15/2/2.
 */
public class RpcConstant {

    public static final String TAG = "RpcRunner";

    public static final String CONFIG_LOADING_MODE = "loadingMode";

    public static final String CONFIG_CACHE_MODE = "cacheMode";

    public static final String CONFIG_SHOW_NET_ERROR = "showNetError";

    //public static final String CONFIG_SHOW_STANDARD_EMPTY = "showStandardEmpty";

    public static final String CONFIG_SHOW_WARN = "showWarn";

    // rpc状态常量
    public static final String RPC_START = "rpc_start";

    // rpc执行完成后将首先发结束事件，然后根据具体状态发送后续业务成功/失败/异常等事件
    public static final String RPC_FINISH_START = "rpc_finish_start";

    // rpc执行完成后将首先发结束事件
    public static final String RPC_FINISH_END = "rpc_finish_end";

    // rpc取消事件
    public static final String RPC_CANCEL = "rpc_cancel";

    public static final String RPC_SUCCESS = "rpc_result_success";

    public static final String RPC_FAIL = "rpc_result_fail";

    public static final String RPC_EXCEPTION = "rpc_result_exception";

    public static final String RPC_RESULT_FOLLOW_ACTION = "followAction";

    // rpc缓存加载开始
    public static final String RPC_CACHE_START = "rpc_cache_start";

    // rpc缓存加载执行完成后将首先发结束事件，然后根据具体状态发送后续业务成功/失败/异常等事件
    public static final String RPC_CACHE_FINISH_START = "rpc_cache_finish_start";

    // rpc缓存加载完成后最终结束
    public static final String RPC_CACHE_FINISH_END = "rpc_cache_finish_end";

    // rpc缓存加载结果成功(注意：缓存不用处理fail事件)
    public static final String RPC_CACHE_SUCCESS = "rpc_cache_result_success";

    // rpc缓存加载结果失败(包括无结果和结果success=false情况)）
    public static final String RPC_CACHE_FAIL = "rpc_cache_fail";


}
