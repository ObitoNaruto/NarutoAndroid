package com.naruto.mobile.swipeback;

import android.app.Activity;
import android.app.ActivityOptions;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 */
public class SwipeBackLayoutHelper {

    private Activity mActivity;

    private SwipeBackLayout mSwipeBackLayout;

    private SwipeBackLayoutHelper(Activity activity) {
        //入参判空
        if (activity == null)
            throw new IllegalArgumentException("activity can not be null!");
        this.mActivity = activity;//当前上下文activity
        mActivity.getWindow().getDecorView().setBackgroundResource(0);//去掉背景
        mActivity.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//时间而终透明色
        mSwipeBackLayout = new SwipeBackLayout(mActivity);//初始化SwipeBackLayout
    }

    /**
     * 初始化SwipeBackLayoutHelper
     * @param activity
     * @return
     */
    public static SwipeBackLayoutHelper create(Activity activity) {
        return new SwipeBackLayoutHelper(activity);
    }

    public SwipeBackLayout getSwipeBackLayout() {
        if (mSwipeBackLayout == null) {
            mSwipeBackLayout = new SwipeBackLayout(mActivity);
        }
        return mSwipeBackLayout;
    }

    public void restoreActivityStopMethod(Activity activity) {
        if (activity == null)
            return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            convertTranslucentToOpaque(activity);
            convertOpaqueToTranslucent(activity);
        }
    }

    private void convertTranslucentToOpaque(Activity activity) {
        try {
            Method method = Activity.class.getDeclaredMethod("convertFromTranslucent");
            method.setAccessible(true);
            method.invoke(activity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void convertOpaqueToTranslucent(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            convertOpaqueToTranslucentAfterL(activity);
        } else {
            convertOpaqueToTranslucentBeforeL(activity);
        }
    }

    /**
     * Calling the convertToTranslucent method on platforms before Android 5.0
     */
    private static void convertOpaqueToTranslucentBeforeL(Activity activity) {
        try {
            Field field = Activity.class.getDeclaredField("mTranslucentCallback");
            field.setAccessible(true);
            Method method = Activity.class.getDeclaredMethod("convertToTranslucent", field.getType());
            method.setAccessible(true);
            method.invoke(activity, new Object[]{null});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Calling the convertToTranslucent method on platforms after Android 5.0
     */
    private static void convertOpaqueToTranslucentAfterL(Activity activity) {
        try {
            Field field = Activity.class.getDeclaredField("mTranslucentCallback");
            field.setAccessible(true);
            Method method = Activity.class.getDeclaredMethod("convertToTranslucent", field.getType(), ActivityOptions.class);
            method.setAccessible(true);
            Method methodGetOptions = Activity.class.getDeclaredMethod("getActivityOptions");
            methodGetOptions.setAccessible(true);
            Object options = methodGetOptions.invoke(activity);
            method.invoke(activity, null, options);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
