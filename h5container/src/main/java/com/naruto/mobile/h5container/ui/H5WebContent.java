
package com.naruto.mobile.h5container.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.naruto.mobile.h5container.R;
import com.naruto.mobile.h5container.api.H5Intent;
import com.naruto.mobile.h5container.api.H5IntentFilter;
import com.naruto.mobile.h5container.api.H5Page;
import com.naruto.mobile.h5container.api.H5Param;
import com.naruto.mobile.h5container.api.H5Plugin;
import com.naruto.mobile.h5container.core.H5PageImpl;
import com.naruto.mobile.h5container.env.H5Container;
import com.naruto.mobile.h5container.env.H5Environment;
import com.naruto.mobile.h5container.refresh.H5PullAdapter;
import com.naruto.mobile.h5container.refresh.H5PullContainer;
import com.naruto.mobile.h5container.refresh.H5PullHeader;
import com.naruto.mobile.h5container.util.H5UrlHelper;
import com.naruto.mobile.h5container.util.H5Utils;
import com.naruto.mobile.h5container.view.H5Progress;

public class H5WebContent implements H5Plugin {

    public static final String TAG = "H5WebContent";

    private View content;
    private View webContent;

    private H5PullContainer pullContainer;
    private TextView tvProvider;
    private H5Progress h5Progress;

    private boolean showProgress;
    private boolean canPullDown;
    private boolean canRefresh;

    private H5PageImpl h5Page;

    public H5WebContent(H5PageImpl h5Page) {
        this.h5Page = h5Page;
        this.canPullDown = true;
        this.canRefresh = false;
        this.showProgress = false;

        initComponent();
    }

    @Override
    public void onRelease() {
        h5Page = null;
    }

    public H5Page getPage() {
        return this.h5Page;
    }

    public View getContent() {
        return content;
    }

    @SuppressLint("InflateParams")
    private void initComponent() {
        Context context = h5Page.getContext().getContext();
        content = LayoutInflater.from(context).inflate(R.layout.h5_web_content,
                null);
        webContent = content.findViewById(R.id.h5_web_content);
        tvProvider = (TextView) content.findViewById(R.id.tv_h5_provider);
        h5Progress = (H5Progress) content.findViewById(R.id.pb_h5_progress);
        pullContainer = (H5PullContainer) content
                .findViewById(R.id.pc_h5_container);

        pullContainer.setContentView(h5Page.getWebView());
        pullContainer.setPullAdapter(pullAdapter);
        updateProvider();
    }

    private void updateProvider() {
        if (canRefresh) {
            tvProvider.setVisibility(View.GONE);
        } else {
            tvProvider.setVisibility(View.VISIBLE);
        }
    }

    private H5PullAdapter pullAdapter = new H5PullAdapter() {

        private H5PullHeader pullHeader;

        @Override
        public void onLoading() {
            if (pullHeader != null) {
                pullHeader.showLoading();
                h5Page.sendIntent(H5Plugin.H5_PAGE_RELOAD, null);
            }
        }

        @Override
        public boolean canRefresh() {
            return canRefresh;
        }

        @Override
        public boolean canPull() {
            return canPullDown;
        }

        @Override
        public View getHeaderView() {
            if (pullHeader == null) {
                Context context = h5Page.getContext().getContext();
                pullHeader = (H5PullHeader) LayoutInflater.from(context)
                        .inflate(R.layout.h5_pull_header, pullContainer, false);
            }
            return pullHeader;
        }

        @Override
        public void onOpen() {
            if (pullHeader != null) {
                pullHeader.showOpen();
            }
        }

        @Override
        public void onOver() {
            if (pullHeader != null) {
                pullHeader.showOver();
            }
        }

        @Override
        public void onFinish() {
            if (pullHeader != null) {
                pullHeader.showFinish();
            }
        }
    };

    @Override
    public void getFilter(H5IntentFilter filter) {
        filter.addAction(H5_PAGE_STARTED);
        filter.addAction(H5_PAGE_PROGRESS);
        filter.addAction(H5_PAGE_FINISHED);
        filter.addAction(H5_PAGE_PHYSICAL_BACK);

        filter.addAction(H5_TOOLBAR_BACK);
        filter.addAction(H5_TOOLBAR_CLOSE);
        filter.addAction(H5_TOOLBAR_RELOAD);
        filter.addAction(H5_TITLEBAR_OPTIONS);
        filter.addAction(H5_TITLEBAR_TITLE);
        filter.addAction(H5_TITLEBAR_SUBTITLE);
        filter.addAction(CLOSE_WEBVIEW);
        filter.addAction(PULL_REFRESH);
        filter.addAction(CAN_PULL_DOWN);
        filter.addAction(SHOW_PROGRESS_BAR);
        filter.addAction(H5_PAGE_BACKGROUND);
    }

    @Override
    public boolean interceptIntent(H5Intent intent) {
        final String action = intent.getAction();
        JSONObject param = intent.getParam();
        if (H5_PAGE_PHYSICAL_BACK.equals(action)
                || H5_TOOLBAR_BACK.equals(action)) {

        } else if (H5_PAGE_STARTED.equals(action)) {
            if (showProgress) {
                h5Progress.setVisibility(View.VISIBLE);
            }
        } else if (H5_PAGE_FINISHED.equals(action)) {
            onPageFinished(param);
        } else if (H5_PAGE_PROGRESS.equals(action)) {
            int progress = H5Utils.getInt(param, H5Container.KEY_PROGRESS);
            h5Progress.updateProgress(progress);
        } else if (H5_PAGE_BACKGROUND.equals(action)) {
            int color = H5Utils.getInt(param, H5Param.LONG_BACKGROUND_COLOR);
            webContent.setBackgroundColor(color);
        }
        return false;
    }

    @Override
    public boolean handleIntent(H5Intent intent) {
        String action = intent.getAction();
        JSONObject param = intent.getParam();
        if (H5_TITLEBAR_TITLE.equals(action)
                || H5_TITLEBAR_OPTIONS.equals(action)
                || H5_TITLEBAR_SUBTITLE.equals(action)) {
            h5Page.getBridge().sendToWeb(action, null, null);
        } else if (PULL_REFRESH.equals(action)) {
            canRefresh = H5Utils.getBoolean(param, H5Param.LONG_PULL_REFRESH,
                    false);
            updateProvider();
            pullContainer.notifyViewChanged();
        } else if (CAN_PULL_DOWN.equals(action)) {
            canPullDown = H5Utils.getBoolean(param, H5Param.LONG_CAN_PULL_DOWN,
                    true);
        } else if (CLOSE_WEBVIEW.equals(action)) {
            h5Page.sendIntent(H5Plugin.H5_PAGE_CLOSE, null);
        } else if (H5_PAGE_PHYSICAL_BACK.equals(action)
                || H5_TOOLBAR_BACK.equals(action)) {
            h5Page.sendIntent(H5_PAGE_BACK, null);
        } else if (H5_TOOLBAR_CLOSE.equals(action)) {
            h5Page.sendIntent(H5_PAGE_CLOSE, null);
        } else if (H5_TOOLBAR_RELOAD.equals(action)) {
            h5Page.sendIntent(H5_PAGE_RELOAD, null);
        } else if (SHOW_PROGRESS_BAR.equals(action)) {
            showProgress = H5Utils.getBoolean(param, H5Param.LONG_SHOW_PROGRESS, false);
            if (!showProgress) {
                h5Progress.setVisibility(View.GONE);
            }
        } else {
            return false;
        }
        return true;
    }

    private void onPageFinished(JSONObject param) {
        h5Progress.setVisibility(View.GONE);
        pullContainer.fitContent();

        final String host = H5UrlHelper.getHost(h5Page.getUrl());
        webContent.setBackgroundColor(H5Environment.getResources().getColor(
                R.color.h5_provider));
        if (!TextUtils.isEmpty(host)) {
            String provider = H5Environment.getResources().getString(
                    R.string.h5_provider, host);
            tvProvider.setText(provider);
        } else {
            tvProvider.setText("");
        }
    }
}
