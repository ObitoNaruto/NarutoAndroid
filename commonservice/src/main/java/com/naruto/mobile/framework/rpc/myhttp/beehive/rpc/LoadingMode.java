package com.naruto.mobile.framework.rpc.myhttp.beehive.rpc;

/**
 * rpc运行时的提示模式
 * Created by zhanqu.awb on 15/2/5.
 */
public enum LoadingMode {

    SILENT("silent"),  // 静默，不显示菊花 (无法取消) 但还是有默认的错误处理

    TITLEBAR_LOADING("titleBarLoading"), // 标题栏菊花

    CANCELABLE_LOADING("cancelableLoading"), // 显示菊花，可取消

    CANCELABLE_EXIT_LOADING("cancelableExitLoading"), // 显示菊花，取消时退出当前页面

    BLOCK_LOADING("blockLoading"), // 显示菊花，不可取消

    // 无察觉模式，即与ui毫无关系，也没有任何默认的异常处理(包括rpc网络异常抛给框架层的错误处理)
    UNAWARE("unaware");

    private String text;

    LoadingMode(String text) {
        this.text = text;
    }

    public static LoadingMode fromString(String text) {
        if (text != null) {
            for (LoadingMode b : LoadingMode.values()) {
                if (text.equals(b.text)) {
                    return b;
                }
            }
        }
        throw new IllegalArgumentException("No constant with text " + text + " found");
    }

}
