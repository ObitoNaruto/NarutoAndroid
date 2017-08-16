package com.naruto.mobile.base.framework.app.ui;

import android.app.TabActivity;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

import java.util.EventObject;

import com.naruto.mobile.base.framework.app.ActivityApplication;
import com.naruto.mobile.base.serviceaop.NarutoApplication;
import com.naruto.mobile.base.serviceaop.NarutoApplicationContext;
//import com.alipay.mobile.framework.MicroApplicationContext;
//import com.alipay.mobile.framework.app.ActivityApplication;
//import com.alipay.test.ui.core.EventObject;
//import com.alipay.trobot.external.DefaultMesssageHandler;

@SuppressWarnings("deprecation")
public abstract class BaseTabActivity extends TabActivity implements ActivityResponsable {
    /**
     * 所属APP
     */
    protected ActivityApplication mApp;
    /**
     * 上下文
     */
    protected NarutoApplicationContext mMicroApplicationContext;
    /**
     * Activity辅助类
     */
    private ActivityHelper mActivityHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityHelper = new ActivityHelper(this);
        mApp = mActivityHelper.getApp();
        mMicroApplicationContext = NarutoApplication.getInstance().getNarutoApplicationContext();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mActivityHelper.onResume();
//        DefaultMesssageHandler.getInstance().onChangeEvent(EventObject.OnResume, this, this.getClass().getSimpleName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        mActivityHelper.onPause();
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        mActivityHelper.onUserLeaveHint();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        mActivityHelper.onWindowFocusChanged(hasFocus);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mActivityHelper.onSaveInstanceState(outState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mActivityHelper.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mActivityHelper.onDestroy();
    }
    
    @Override
    public void finish() {
        super.finish();
        mActivityHelper.finish();
    }

    /**
     * 显示进度对话框
     * 
     * @param msg 消息
     */
    @Override
    public void showProgressDialog(String msg) {
        mActivityHelper.showProgressDialog(msg);
    }

    /**
     * 显示可取消的进度对话框
     * 
     * @param msg 消息
     */
    public void showProgressDialog(final String msg, final boolean cancelable,
                                   final OnCancelListener cancelListener) {
        mActivityHelper.showProgressDialog(msg, cancelable, cancelListener);
    }

    @Override
    public void alert(String title, String msg, String positive, OnClickListener positiveListener,
                      String negative, OnClickListener negativeListener) {
        mActivityHelper.alert(title, msg, positive, positiveListener, negative, negativeListener);
    }

    @Override
    public void alert(String title, String msg, String positive, OnClickListener positiveListener,
                      String negative, OnClickListener negativeListener,
                      Boolean isCanceledOnTouchOutside) {
        
        mActivityHelper.alert(title, msg, positive, positiveListener, negative, negativeListener, isCanceledOnTouchOutside);
    }

    @Override
    public void toast(String msg, int period) {
        mActivityHelper.toast(msg, period);
    }

    @Override
    public void dismissProgressDialog() {
        mActivityHelper.dismissProgressDialog();
    }

}
