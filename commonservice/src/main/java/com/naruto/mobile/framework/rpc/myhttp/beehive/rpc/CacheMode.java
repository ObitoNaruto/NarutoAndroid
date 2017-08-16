package com.naruto.mobile.framework.rpc.myhttp.beehive.rpc;

/**
 * rpc执行时的缓存加载模式
 * 注: 当前版本加载缓存数据都无loading提示
 * Created by zhanqu.awb on 15/7/9.
 */
public enum CacheMode {

    NONE("none"),   // 不使用缓存

    CACHE_AND_RPC("cacheAndRpc"), // 先缓存，再使用rpc数据覆盖

    RPC_OR_CACHE("rpcOrCache"); // 先rpc, 如果rpc为空时再请求缓存数据

    private String text;

    CacheMode(String text) {
        this.text = text;
    }

    public static CacheMode fromString(String text) {
        if (text != null) {
            for (CacheMode b : CacheMode.values()) {
                if (text.equals(b.text)) {
                    return b;
                }
            }
        }
        throw new IllegalArgumentException("No constant with text " + text + " found");
    }

}
