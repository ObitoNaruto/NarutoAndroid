
package com.naruto.mobile.h5container.plugin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.naruto.mobile.h5container.R;
import com.naruto.mobile.h5container.api.H5Intent;
import com.naruto.mobile.h5container.api.H5IntentFilter;
import com.naruto.mobile.h5container.api.H5Page;
import com.naruto.mobile.h5container.api.H5Plugin;
import com.naruto.mobile.h5container.util.H5Log;
import com.naruto.mobile.h5container.util.H5Utils;

public class H5LoadingPlugin implements H5Plugin {

    public static final String TAG = "H5LoadingPlugin";

    private static final int LOADING_TEXT_MAX = 20;
    private Runnable loadingTask;
    private Handler handler;
    private H5Page h5Page;
    private LoadingDialog dialog;

    public H5LoadingPlugin(H5Page page) {
        this.h5Page = page;
        handler = new Handler();
    }

    @Override
    public void onRelease() {
        handler.removeCallbacks(loadingTask);
        loadingTask = null;
        h5Page = null;
    }

    class LoadingDialog extends AlertDialog {

        private ProgressBar pbLoading;
        private TextView tvMessage;
        private String messageText;

        protected LoadingDialog(Context context) {
            this(context, R.style.h5_loading_style);
        }

        public LoadingDialog(Context context, int theme) {
            super(context, theme);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View view = inflater.inflate(R.layout.h5_loading, null);
            pbLoading = (ProgressBar) view.findViewById(R.id.h5_loading_progress);
            tvMessage = (TextView) view.findViewById(R.id.h5_loading_message);

            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.alpha = 0.99f;
            getWindow().setAttributes(lp);
            setView(view);
            pbLoading.setVisibility(View.VISIBLE);
            setCancelable(true);
            setOnCancelListener(null);
            pbLoading.setIndeterminate(false);
            setCanceledOnTouchOutside(false);
            realSetMessage();
            super.onCreate(savedInstanceState);
        }

        public void setMessage(String text) {
            messageText = text;
            if (tvMessage != null) {
                realSetMessage();
            }
        }

        private void realSetMessage() {
            tvMessage.setText(messageText);
            if (TextUtils.isEmpty(messageText)) {
                tvMessage.setVisibility(View.GONE);
            } else {
                tvMessage.setVisibility(View.VISIBLE);
            }
        }

    }

    public void showLoading(H5Intent intent) {
        JSONObject param = intent.getParam();
        String title = H5Utils.getString(param, "text");
        int delay = H5Utils.getInt(param, "delay");

        H5Log.d(TAG, "showLoading [title] " + title + " [delay] " + delay);

        final Activity activity;
        Context context = h5Page.getContext().getContext();
        if (context instanceof Activity) {
            activity = (Activity) context;
        } else {
            return;
        }

        if (dialog == null) {
            dialog = new LoadingDialog(activity);
        }

        hideLoading();

        // cut the text to limited size
        if (!TextUtils.isEmpty(title) && title.length() > LOADING_TEXT_MAX) {
            title = title.substring(0, LOADING_TEXT_MAX);
        }

        dialog.setMessage(title);

        loadingTask = new Runnable() {
            @Override
            public void run() {
                if (!activity.isFinishing()) {
                    dialog.show();
                }
            }
        };
        handler.postDelayed(loadingTask, delay);
    }

    public void hideLoading() {
        if (loadingTask != null) {
            handler.removeCallbacks(loadingTask);
            loadingTask = null;
        }

        if (dialog != null && dialog.isShowing()) {
            H5Log.d("hideLoading");
            dialog.dismiss();
        }
    }

    @Override
    public void getFilter(H5IntentFilter filter) {
        filter.addAction(SHOW_LOADING);
        filter.addAction(HIDE_LOADING);
    }

    @Override
    public boolean interceptIntent(H5Intent intent) {
        return false;
    }

    @Override
    public boolean handleIntent(H5Intent intent) {
        String action = intent.getAction();
        if (SHOW_LOADING.equals(action)) {
            showLoading(intent);
        } else if (HIDE_LOADING.equals(action)) {
            hideLoading();
        }
        return true;
    }
}
