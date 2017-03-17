package com.naruto.mobile.pullrefresh.simple;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;

public final class ViewFinder {
    public static boolean DEBUG = false;
    private static SparseArray<WeakReference<View>> mViewMap = new SparseArray();
    private static WeakReference<View> mRootView;

    public ViewFinder() {
    }

    public static void initContentView(View contentView) {
        if(contentView == null) {
            throw new RuntimeException("ViewFinder init failed, mContentView == null.");
        } else {
            mRootView = new WeakReference(contentView);
            mViewMap.clear();
        }
    }

    public static void initContentView(Context context, int layoutId) {
        initContentView(context, (ViewGroup)null, layoutId);
    }

    public static void initContentView(Context context, ViewGroup parent, int layoutId) {
        if(context != null && layoutId > 0) {
            View rootView = LayoutInflater.from(context).inflate(layoutId, parent, false);
            initContentView(rootView);
        } else {
            throw new RuntimeException("initContentView invalid params, context == null || layoutId == -1.");
        }
    }

    public static View getContentView() {
        return (View)mRootView.get();
    }

    public static <T extends View> T findViewById(int viewId) {
        View targetView = null;
        WeakReference viewWrf = (WeakReference)mViewMap.get(viewId);
        if(viewWrf != null) {
            targetView = (View)viewWrf.get();
        }

        if(targetView == null && mRootView != null && mRootView.get() != null) {
            targetView = ((View)mRootView.get()).findViewById(viewId);
            mViewMap.put(viewId, new WeakReference(targetView));
        }

        Log.d("", "### find view = " + targetView);
        return targetView == null?null:(T)targetView;
    }

    public static <T extends View> T findViewById(View rootView, int viewId) {
        View targetView = null;
        if(rootView != null) {
            targetView = rootView.findViewById(viewId);
        }

        Log.d("", "### find view = " + targetView);
        return targetView == null?null:(T)targetView;
    }

    public static void clear() {
        if(mRootView != null) {
            mRootView.clear();
            mRootView = null;
        }

        if(mViewMap != null) {
            mViewMap.clear();
            mViewMap = null;
        }

    }
}
