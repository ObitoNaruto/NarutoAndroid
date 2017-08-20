package com.naruto.mobile.base.serviceaop.app.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;

public interface ActivityResponsable {
    /**
     * 弹对话框
     *
     * @param title            标题
     * @param msg              消息
     * @param positive         确定
     * @param positiveListener 确定回调
     * @param negative         否定
     * @param negativeListener 否定回调
     */
    void alert(final String title, final String msg, final String positive,
                      final DialogInterface.OnClickListener positiveListener,
                      final String negative, final DialogInterface.OnClickListener negativeListener);

    /**
     * 弹对话框
     *
     * @param title                    标题
     * @param msg                      消息
     * @param positive                 确定
     * @param positiveListener         确定回调
     * @param negative                 否定
     * @param negativeListener         否定回调
     * @param isCanceledOnTouchOutside 是否外部点击取消
     */
    void alert(final String title, final String msg, final String positive,
                      final DialogInterface.OnClickListener positiveListener,
                      final String negative, final DialogInterface.OnClickListener negativeListener, boolean isCanceledOnTouchOutside);

    /**
     * TOAST
     *
     * @param msg    消息
     * @param period 时长
     */
    void toast(final String msg, final int period);

    /**
     * 显示进度对话框
     *
     * @param msg 消息
     */
    void showProgressDialog(final String msg);

    /**
     * 显示可取消的进度对话框
     *
     * @param msg 消息
     */
    void showProgressDialog(final String msg, final boolean cancelable,
                                   final OnCancelListener cancelListener);

    /**
     * 取消进度对话框
     */
    void dismissProgressDialog();
}
