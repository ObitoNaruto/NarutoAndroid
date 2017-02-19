package com.naruto.mobile.toast.WealthToast;

import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.naruto.mobile.R;

public class WealthToast {
    private static final int IMG_SUCCESS    = R.drawable.toast_success;
    private static final int IMG_FAVORITE   = R.drawable.toast_favorite;


    private static WealthToast sWealthToast = new WealthToast();

    private static volatile Toast mToast;

    private View mView;

    public static int getSystemSdk() {
        return Build.VERSION.SDK_INT;
    }

    private WealthToast() {
    }

    public static WealthToast getInstance() {
        return sWealthToast;
    }

    /**
     * 成功
     */
    public void makeSuccess(Context context, String text) {
        showTips(context, text, IMG_SUCCESS);
    }

    /**
     * 警告
     */
    public void makeWarning(Context context, String text) {
        showTips(context, text, 0);
    }

    /**
     * 失败
     */
    public void makeError(Context context, String text) {
        showTips(context, text, 0);
    }

    /**
     * 喜欢
     */
    public void makeFavorite(Context context, String text) {
        showTips(context, text, IMG_FAVORITE);
    }

    private void showTips(Context context, String msg, int tipsImage) {
        if (mToast != null) {
            if (getSystemSdk() < 14) {
                mToast.cancel();
            }
        } else {
            mToast = new Toast(context.getApplicationContext());
        }
        mToast.setView(makeTipsView(context, msg, tipsImage));
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.show();
    }

    private synchronized View makeTipsView(Context context, String msg, int tipsImage) {
        if (mView == null) {
            mView = View.inflate(context, R.layout.common_wealth_toast, null);
        }
        if (mView != null) {
            ImageView tipsIv = (ImageView) mView.findViewById(R.id.tips_iv);
            TextView tipsTv = (TextView) mView.findViewById(R.id.tips_tv);
            if (tipsImage != 0) {
                tipsIv.setImageResource(tipsImage);
                tipsIv.setVisibility(View.VISIBLE);
            } else {
                tipsIv.setVisibility(View.GONE);
            }
            tipsTv.setText(msg);
        }
        return mView;
    }
}
