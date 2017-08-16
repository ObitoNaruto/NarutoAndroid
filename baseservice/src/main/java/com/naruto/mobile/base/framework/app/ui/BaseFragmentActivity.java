package com.naruto.mobile.base.framework.app.ui;

import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;

import com.naruto.mobile.base.framework.app.ActivityApplication;


public abstract class BaseFragmentActivity extends FragmentActivity implements ActivityResponsable {
//    /**
//     * 所属APP
//     */
//    protected ActivityApplication mApp;
//    /**
//     * 上下文
//     */
//    protected MicroApplicationContext mMicroApplicationContext;
//    /**
//     * Activity辅助类
//     */
//    private ActivityHelper mActivityHelper;
//
//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//       mActivityHelper.dispatchOnTouchEvent(ev);
//       return super.dispatchTouchEvent(ev);
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        mActivityHelper = new ActivityHelper(this);
//        mApp = mActivityHelper.getApp();
//        mMicroApplicationContext = mActivityHelper.getMicroApplicationContext();
//
//		String deviceModel = android.os.Build.DEVICE;
//		if (TextUtils.equals(deviceModel, "M040")) {
//			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
//				// 当前view去掉硬件加速 防止mx crash
//				getWindow().getDecorView().setLayerType(
//						View.LAYER_TYPE_SOFTWARE, null);
//			}
//		}
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        if(mActivityHelper!=null){
//        mActivityHelper.onResume();
//        }
//        DefaultMesssageHandler.getInstance().onChangeEvent(EventObject.OnResume, this, this.getClass().getSimpleName());
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        if(mActivityHelper!=null){
//        mActivityHelper.onPause();
//    }
//    }
//
//    @Override
//    protected void onUserLeaveHint() {
//        super.onUserLeaveHint();
//        mActivityHelper.onUserLeaveHint();
//    }
//
//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        mActivityHelper.onWindowFocusChanged(hasFocus);
//    }
//
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        mActivityHelper.onSaveInstanceState(outState);
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        if(mActivityHelper!=null){
//        mActivityHelper.onStart();
//    }
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if(mActivityHelper!=null){
//        mActivityHelper.onDestroy();
//    }
//    }
//
//    @Override
//    public void finish() {
//        super.finish();
//        if(mActivityHelper!=null){
//        mActivityHelper.finish();
//    }
//    }
//
//    /**
//     * 显示进度对话框
//     *
//     * @param msg 消息
//     */
//    @Override
//    public void showProgressDialog(String msg) {
//        mActivityHelper.showProgressDialog(msg);
//    }
//
//    /**
//     * 显示可取消的进度对话框
//     *
//     * @param msg 消息
//     */
//    public void showProgressDialog(final String msg, final boolean cancelable,
//                                   final OnCancelListener cancelListener) {
//        mActivityHelper.showProgressDialog(msg, cancelable, cancelListener);
//    }
//
//    @Override
//    public void alert(String title, String msg, String positive, OnClickListener positiveListener,
//                      String negative, OnClickListener negativeListener) {
//        mActivityHelper.alert(title, msg, positive, positiveListener, negative, negativeListener);
//    }
//
//    @Override
//    public void alert(String title, String msg, String positive, OnClickListener positiveListener,
//                      String negative, OnClickListener negativeListener, Boolean isCanceledOnTouchOutside) {
//        mActivityHelper.alert(title, msg, positive, positiveListener, negative, negativeListener, isCanceledOnTouchOutside);
//    }
//
//    @Override
//    public void toast(String msg, int period) {
//        mActivityHelper.toast(msg, period);
//    }
//
//    @Override
//    public void dismissProgressDialog() {
//        mActivityHelper.dismissProgressDialog();
//    }

}
