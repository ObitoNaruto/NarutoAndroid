package com.naruto.mobile.swipeback;

import android.view.View;

/**
 */
public interface SwipeLayoutListener {
    //回调滑动的百分比
    void onViewFlingPercent(float flingPercent);

    //触摸边缘并没有拖动时回调
    void onEdgeTouched();

    //开始拖动时回调
    void onEdgeDragStarted();

    //抬起时回调
    void onViewReleased(View releasedView);

    //达到finish状态时回调
    void onViewFlingOver();
}