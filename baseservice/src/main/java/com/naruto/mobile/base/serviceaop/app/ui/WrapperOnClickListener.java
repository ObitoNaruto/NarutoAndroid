package com.naruto.mobile.base.serviceaop.app.ui;

import android.view.View;
import android.view.View.OnClickListener;

/**
 * 装饰者模式
 */
public class WrapperOnClickListener implements OnClickListener {
    private OnClickListener onClickListener;

    public WrapperOnClickListener(OnClickListener onClickListener) {
        super();
        this.onClickListener = onClickListener;
    }

    @Override
    public void onClick(View view) {
        if (view.getContext() instanceof BaseActivity) {
//            BaseActivity baseActivity = (BaseActivity) view.getContext();
//            baseActivity.mApp
//                .getMicroApplicationContext()
//                .getMsgCenter()
//                .publish(baseActivity.mApp.getAppId(), MsgCodeEnum.VIEW_CLICK.getCode(),
//                    new WeakReference<View>(view));
        }
        onClickListener.onClick(view);
    }

}
