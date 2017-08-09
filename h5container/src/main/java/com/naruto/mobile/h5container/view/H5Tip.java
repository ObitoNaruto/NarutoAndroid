
package com.naruto.mobile.h5container.view;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.naruto.mobile.h5container.R;


/**
 */
public class H5Tip {
    private volatile static PopupWindow popWindow = null;
    static Handler handler = null;
    static Runnable task = null;

    public static void showTip(Context context, ViewGroup anchorView, String tip) {
        View view = null;
        final boolean anchorViewGone = anchorView.getVisibility() == View.GONE;
        synchronized (H5Tip.class) {
            if (popWindow != null && popWindow.isShowing()) {
                return;
            }
            view = LayoutInflater.from(context).inflate(R.layout.h5_tip,
                    anchorView, false);
            popWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT, false);
        }

        Button btnClose = (Button) view.findViewById(R.id.false_image);

        if (!TextUtils.isEmpty(tip)) {
            TextView h5_description = (TextView) view
                    .findViewById(R.id.h5_description);
            h5_description.setText(tip);
        }

        popWindow.setFocusable(false);
        popWindow.setTouchable(true);
        popWindow.setOutsideTouchable(true);
        if (!anchorViewGone) {
            popWindow.showAsDropDown(anchorView, 0, 0);
        } else {
            popWindow.showAtLocation(anchorView, Gravity.TOP, 0, 0);
        }
        btnClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissTip();
            }
        });

        task = new Runnable() {
            @Override
            public void run() {
                dismissTip();
            }
        };

        handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(task, 3000);
    }

    public static void dismissTip() {
        try {
            if (popWindow != null && popWindow.isShowing()) {
                popWindow.dismiss();
                handler.removeCallbacks(task);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } finally {
            popWindow = null;
        }
    }
}
