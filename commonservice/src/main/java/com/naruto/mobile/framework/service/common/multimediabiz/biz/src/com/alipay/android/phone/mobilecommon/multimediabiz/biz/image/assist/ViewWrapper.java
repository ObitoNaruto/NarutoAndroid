package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.assist;

import android.view.View;

import java.lang.ref.WeakReference;

public class ViewWrapper<T extends View> {
    private WeakReference<T> targetView;
    private Object tag;

    public ViewWrapper(T v) {
        this(v, null);
    }

    public ViewWrapper(T v, Object tag) {
        this.targetView = new WeakReference<T>(v);
        this.tag = tag;
    }

    public T getTargetView() {
        return targetView.get();
    }

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "ViewWrapper{"
                + ((targetView.get() == null) ? null: targetView.get().hashCode())
                + ", tag=" + tag +
                '}';
    }
}
