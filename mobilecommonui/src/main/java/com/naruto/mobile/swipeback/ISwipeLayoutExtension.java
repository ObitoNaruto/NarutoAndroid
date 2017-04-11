package com.naruto.mobile.swipeback;

/**
 */
public interface ISwipeLayoutExtension {

    /**
     * 是否支持侧滑的开关
     * @param isEnabled
     */
    void setSwipeBackEnabled(boolean isEnabled);

    /**
     * 获取侧滑组件layout
     * @return
     */
    SwipeBackLayout getSwipeBackLayout();
}
