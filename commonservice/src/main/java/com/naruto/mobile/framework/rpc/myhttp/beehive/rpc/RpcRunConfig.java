package com.naruto.mobile.framework.rpc.myhttp.beehive.rpc;


/**
 * rpc执行配置
 */
public class RpcRunConfig {

    /**
     * rpc任务的缓存模式, 默认先加载缓存再请求rpc(需要提供cacheKey才能真正生效)
     */
    public CacheMode cacheMode = CacheMode.CACHE_AND_RPC;

    /**
     * rpc任务的loading模式
     */
    public LoadingMode loadingMode = LoadingMode.CANCELABLE_LOADING;

    /**
     * 网络错误时是否显示标准无网络界面
     */
    public boolean showNetError = false;

    /**
     * 异常时是否显示提醒盒子界面
     */
    public boolean showWarn = false;

    /**
     * rpc任务的缓存key, 默认为空，如果不设置，则缓存不生效
     */
    public String cacheKey = "";

    /**
     * 如果使用先加载缓存的模式，并且加载到缓存后，标记是否需要自动修改菊花模式, 即自动将各种菊花模式改为标题栏模式 默认为true
     */
    public boolean autoModifyLoadingOnCache = true;

    /**
     * 显示FlowTipView所需的占位view id, 默认为空 需要定制FlowTipView位置时使用，如fragment，界面内部定制 如果不设置，则是标准处理方式：作为activity content view的单独一层
     * 另外flowTipHolderViewId如果设置，在FlowTipViewMode.A
     */
    public int flowTipHolderViewId;
//    /**
//     * 从Bundle中获取参数
//     */
//    public static RpcRunConfig fromBundle(Bundle b) {
//        if (b == null) {
//            return null;
//        }
//        RpcRunConfig config = new RpcRunConfig();
//        config.cacheMode = CacheMode.fromString(b.getString(RpcConstant.CONFIG_CACHE_MODE));
//        config.loadingMode = LoadingMode.fromString(b.getString(RpcConstant.CONFIG_LOADING_MODE));
//        config.showNetError = b.getBoolean(RpcConstant.CONFIG_SHOW_NET_ERROR, false);
//        config.showWarn = b.getBoolean(RpcConstant.CONFIG_SHOW_WARN, false);
//        return config;
//    }

    @Override
    public String toString() {
        try {
            return String.format("cacheMode(%s),loadingMode(%s),showNetError(%s),showWarn(%s),"
                            + "cacheKey(%s), flowTipHolderViewId(%d),"
                            + "autoModifyLoadingOnCache(%s)",
                    cacheMode, loadingMode, showNetError, showWarn, cacheKey,
                    flowTipHolderViewId, autoModifyLoadingOnCache);
        } catch (Exception ex) {
//            LoggerFactory.getTraceLogger().warn(RpcConstant.TAG, ex);
        }
        return super.toString();
    }

}
