package com.naruto.mobile.framework.rpc.myhttp.beehive.eventbus;

/**
 * Created by aowen on 15/2/2.
 */
public enum ThreadMode {

    UI("ui"),   // ui线程

    BACKGROUND("background"), // 后台线程

    CURRENT("current");   // 当前线程

    //ASYNC("async"); // 异步线程

    private String text;

    ThreadMode(String text) {
        this.text = text;
    }

    public static ThreadMode fromString(String text) {
        if (text != null) {
            for (ThreadMode b : ThreadMode.values()) {
                if (text.equals(b.text)) {
                    return b;
                }
            }
        }
        throw new IllegalArgumentException("No constant with text " + text + " found");
    }

}
