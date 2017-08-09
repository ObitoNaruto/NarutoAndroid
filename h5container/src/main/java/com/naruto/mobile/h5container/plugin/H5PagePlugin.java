
package com.naruto.mobile.h5container.plugin;

import android.content.Context;
import android.text.TextUtils;
import android.webkit.WebBackForwardList;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.naruto.mobile.h5container.R;
import com.naruto.mobile.h5container.api.H5Bridge;
import com.naruto.mobile.h5container.api.H5CallBack;
import com.naruto.mobile.h5container.api.H5Intent;
import com.naruto.mobile.h5container.api.H5IntentFilter;
import com.naruto.mobile.h5container.api.H5Param;
import com.naruto.mobile.h5container.api.H5Plugin;
import com.naruto.mobile.h5container.core.H5PageImpl;
import com.naruto.mobile.h5container.env.H5Container;
import com.naruto.mobile.h5container.env.H5Environment;
import com.naruto.mobile.h5container.util.H5Log;
import com.naruto.mobile.h5container.util.H5Utils;
import com.naruto.mobile.h5container.view.H5FontBar;
import com.naruto.mobile.h5container.web.H5WebView;

public class H5PagePlugin implements H5Plugin {

    public static final String TAG = "H5PagePlugin";

    private static enum BackBehavior {
        POP, BACK,
    }

    private static enum PageStatus {
        NONE, LOADING, READY, ERROR, FINISHED
    }

    private H5WebView h5WebView;
    private H5PageImpl h5Page;
    private H5Bridge h5Bridge;
    private PageStatus pageStatus;
    private H5BackHandler backHandler;
    private BackBehavior backBehavior;

    public H5PagePlugin(H5PageImpl h5Page) {
        this.h5Page = h5Page;
        this.h5WebView = (H5WebView) h5Page.getWebView();
        this.h5Bridge = h5Page.getBridge();
        this.pageStatus = PageStatus.NONE;
        this.backHandler = new H5BackHandler();
        this.backBehavior = BackBehavior.BACK;
    }

    class H5BackHandler implements H5CallBack {

        public boolean waiting;
        public long lastBack;

        public H5BackHandler() {
            waiting = false;
            lastBack = 0;
        }

        @Override
        public void onCallBack(JSONObject param) {
            waiting = false;
            boolean prevent = H5Utils.getBoolean(param, "prevent", false);
            H5Log.d(TAG, "back event prevent " + prevent);
            if (prevent) {
                return;
            }
            performBack();
        }
    };

    @Override
    public void onRelease() {
        h5Bridge = null;
        h5WebView = null;
        h5Page = null;
        backHandler = null;
    }

    @Override
    public void getFilter(H5IntentFilter filter) {
        filter.addAction(H5Plugin.H5_PAGE_BACK_BEHAVIOR);
        filter.addAction(H5Plugin.H5_PAGE_RECEIVED_TITLE);
        filter.addAction(H5Plugin.H5_PAGE_LOAD_URL);
        filter.addAction(H5Plugin.H5_PAGE_LOAD_DATA);
        filter.addAction(H5Plugin.H5_PAGE_RELOAD);
        filter.addAction(H5Plugin.H5_PAGE_FONT_SIZE);
        filter.addAction(H5Plugin.H5_PAGE_RESUME);
        filter.addAction(H5Plugin.H5_PAGE_ERROR);
        filter.addAction(H5Plugin.H5_PAGE_BACK);
        filter.addAction(H5Plugin.H5_PAGE_STARTED);
        filter.addAction(H5Plugin.H5_PAGE_PROGRESS);
        filter.addAction(H5Plugin.H5_PAGE_UPDATED);
        filter.addAction(H5Plugin.H5_PAGE_FINISHED);
        filter.addAction(H5Plugin.H5_PAGE_CLOSE);
        filter.addAction(H5Plugin.H5_PAGE_BACKGROUND);
        filter.addAction(H5Plugin.H5_TOOLBAR_MENU_BT);
        filter.addAction(H5Container.H5_PAGE_DO_LOAD_URL);
    }

    @Override
    public boolean interceptIntent(H5Intent intent) {
        String action = intent.getAction();
        JSONObject param = intent.getParam();
        if (H5Plugin.H5_PAGE_ERROR.equals(action)) {
            pageStatus = PageStatus.ERROR;
        } else if (H5Plugin.H5_TOOLBAR_MENU_BT.equals(action)) {
            String tag = H5Utils.getString(param, H5Container.MENU_TAG);
            boolean shoot = H5Utils.getBoolean(param, "shoot", false);
            if (!H5Container.MENU_SHARE.equals(tag) || shoot) {
                return false;
            }

            if (pageStatus != PageStatus.FINISHED) {
                H5Log.d(TAG, "page not finished yet, direct send intent");
                return false;
            }

            ShareCallback callback = new ShareCallback(intent);
            h5Page.getBridge().sendToWeb("JSPlugin_AlipayH5Share", null, callback);

            return true;
        }
        return false;
    }

    class ShareCallback implements H5CallBack {
        private H5Intent intent;

        public ShareCallback(H5Intent intent) {
            this.intent = intent;
        }

        @Override
        public void onCallBack(JSONObject result) {
            if (result == null) {
                return;
            }
            JSONObject param = intent.getParam();
            param.put("shoot", true);
            String imgUrl = H5Utils.getString(result, "imgUrl");
            if (!TextUtils.isEmpty(imgUrl)) {
                param.put("imageUrl", imgUrl);
            }
            String title = H5Utils.getString(result, "title");
            if (!TextUtils.isEmpty(title)) {
                param.put("title", title);
            }
            param.put("desc", H5Utils.getString(result, "desc"));
            h5Page.sendIntent(H5_TOOLBAR_MENU_BT, param);
        }

    }

    @Override
    public boolean handleIntent(H5Intent intent) {
        String action = intent.getAction();
        JSONObject param = intent.getParam();
        if (H5Plugin.H5_PAGE_BACK_BEHAVIOR.equals(action)) {
            String behavior = H5Utils.getString(param, "backBehavior");
            if ("pop".equals(behavior)) {
                this.backBehavior = BackBehavior.POP;
            } else if ("back".equals(behavior)) {
                this.backBehavior = BackBehavior.BACK;
            }
        } else if (H5Plugin.H5_PAGE_LOAD_URL.equals(action)) {
            String current = h5WebView.getUrl();
            if (TextUtils.isEmpty(current)) {
                param.put(H5Container.KEY_START_URL, true);
                h5Page.sendIntent(H5Plugin.H5_PAGE_SHOULD_LOAD_URL, param);
            } else {
                loadUrl(intent);
            }
        } else if (H5_PAGE_LOAD_DATA.equals(action)) {
            loadData(intent);
        } else if (H5Container.H5_PAGE_DO_LOAD_URL.equals(action)) {
            String url = H5Utils.getString(param, H5Param.LONG_URL);
            String current = h5WebView.getUrl();
            boolean force = H5Utils.getBoolean(param, H5Container.KEY_FORCE,
                    false);
            if (!TextUtils.isEmpty(current)) {
                h5Page.getViewClient().setCheckingUrl(url);
            }

            // The first URL for web view or force parameter specified!
            if (TextUtils.isEmpty(current) || force) {
                loadUrl(intent);
            }
        } else if (H5Plugin.H5_PAGE_RELOAD.equals(action)) {
            reloadContent();
        } else if (H5Plugin.H5_TOOLBAR_BACK.equals(action)) {
            if (h5WebView.canGoBack()) {
                h5WebView.goBack();
            } else {
                h5Page.sendIntent(H5Plugin.H5_PAGE_CLOSE, null);
            }
        } else if (H5Plugin.H5_PAGE_BACK.equals(action)) {
            pageBack();
        } else if (H5Plugin.H5_PAGE_RESUME.equals(action)) {
            String popParam = h5Page.getSession().getData()
                    .remove(H5Container.H5_SESSION_POP_PARAM);
            String resumeParam = h5Page.getSession().getData()
                    .remove(H5Container.H5_SESSION_RESUME_PARAM);

            JSONObject data = new JSONObject();
            if (!TextUtils.isEmpty(popParam)) {
                data.put("data", H5Utils.parseObject(popParam));
            }
            if (!TextUtils.isEmpty(resumeParam)) {
                data.put("resumeParams", H5Utils.parseObject(resumeParam));
            }
            h5Bridge.sendToWeb("resume", data, null);
        } else if (H5Plugin.H5_PAGE_FONT_SIZE.equals(action)) {
            int size = H5Utils.getInt(param, "size", -1);
            if (size != -1) {
                h5WebView.setTextSize(size);
            }
            h5Page.getSession().getScenario().getData()
                    .set(H5Container.FONT_SIZE, "" + size);
        } else if (H5Plugin.H5_PAGE_STARTED.equals(action)) {
            pageStatus = PageStatus.LOADING;
        } else if (H5Plugin.H5_PAGE_FINISHED.equals(action)) {
            boolean pageUpdated = H5Utils.getBoolean(param, H5Container.KEY_PAGE_UPDATED, false);
            if (!pageUpdated) {
                H5Log.d(TAG, "page finished but not updated for redirect");
                return true;
            }

            if (pageStatus == PageStatus.READY || pageStatus == PageStatus.LOADING) {
                pageStatus = PageStatus.FINISHED;
            }

            h5Page.sendIntent(H5Plugin.HIDE_LOADING, null);

            int historySize = H5Utils.getInt(param, "historySize");
            if (historySize > 1 && BackBehavior.BACK == backBehavior) {
                JSONObject data = new JSONObject();
                data.put("show", true);
                h5Page.sendIntent(H5_PAGE_SHOW_CLOSE, data);
            }
        } else if (H5Plugin.H5_PAGE_RECEIVED_TITLE.equals(action)) {
            onReceivedTitle();
        } else if (H5Plugin.H5_PAGE_UPDATED.equals(action)
                || H5Plugin.H5_PAGE_PROGRESS.equals(action)) {
        } else if (H5_PAGE_CLOSE.equals(action)) {
            h5Page.exitPage();
        } else if (H5_PAGE_BACKGROUND.equals(action)) {
            int color = H5Utils.getInt(param, H5Param.LONG_BACKGROUND_COLOR);
            h5WebView.setBackgroundColor(color);
        } else if (H5Plugin.H5_TOOLBAR_MENU_BT.equals(action)) {
            String tag = H5Utils.getString(param, H5Container.MENU_TAG);
            if (H5Container.MENU_FONT.equals(tag)) {
                h5Page.sendIntent(H5FontBar.SHOW_FONT_BAR, null);
            } else if (H5Container.MENU_REFRESH.equals(tag)) {
                h5Page.sendIntent(H5Plugin.H5_PAGE_RELOAD, null);
            } else if (H5Container.MENU_COPY.equals(tag)) {
                JSONObject data = new JSONObject();
                data.put("text", h5Page.getUrl());
                h5Page.sendIntent(H5Plugin.SET_CLIPBOARD, data);
                String copied = H5Environment.getResources().getString(R.string.copied);
                Context context = h5Page.getContext().getContext();
                Toast.makeText(context, copied, Toast.LENGTH_SHORT).show();
            } else {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    private void onReceivedTitle() {
        if (pageStatus == PageStatus.LOADING) {
            this.pageStatus = PageStatus.READY;
        }
    }

    private void pageBack() {
        long time = System.currentTimeMillis();

        // page ready and last back event response received
        boolean sendIntent = (pageStatus == PageStatus.FINISHED)
                && !backHandler.waiting;

        // two back events elapsed time less than 500 milliseconds
        boolean enoughElapse = (time - backHandler.lastBack) > 500;
        boolean ignoreBridge = !sendIntent || !enoughElapse;

        if (!ignoreBridge) {
            H5Log.d(TAG, "send back event to bridge!");
            backHandler.waiting = true;
            backHandler.lastBack = time;
            h5Bridge.sendToWeb("back", null, backHandler);
        } else {
            H5Log.d(TAG, "ignore bridge, perform back!");
            performBack();
        }
    }

    private void reloadContent() {
        h5WebView.reload();
    }

    private void loadUrl(H5Intent intent) {
        JSONObject param = intent.getParam();
        String url = H5Utils.getString(param, H5Param.LONG_URL);
        if (TextUtils.isEmpty(url)) {
            H5Log.w("h5_url_isnull");
            return;
        }

        h5WebView.loadUrl(url);
    }

    private void loadData(H5Intent intent) {
        JSONObject param = intent.getParam();
        final String baseUrl = H5Utils.getString(param, "baseUrl");
        final String data = H5Utils.getString(param, "data");
        final String mimeType = H5Utils.getString(param, "mimeType");
        final String encoding = H5Utils.getString(param, "encoding");
        final String historyUrl = H5Utils.getString(param, "historyUrl");
        H5Utils.runOnMain(new Runnable() {

            @Override
            public void run() {
                h5WebView.loadDataWithBaseURL(baseUrl, data, mimeType,
                        encoding, historyUrl);
            }
        });
    }

    private void performBack() {
        H5Log.d(TAG, "perform back behavior " + backBehavior);
        // history not cleared because onPageFinished not called yet!
        if (backBehavior == BackBehavior.POP) {
            h5Page.sendIntent(H5Plugin.H5_PAGE_CLOSE, null);
        } else if (backBehavior == BackBehavior.BACK) {
            if (h5WebView == null || !h5WebView.canGoBack()) {
                H5Log.d(TAG, "webview can't go back and do exit!");
                h5Page.sendIntent(H5Plugin.H5_PAGE_CLOSE, null);
                return;
            }

            WebBackForwardList list = h5WebView.copyBackForwardList();
            int currentIndex = list.getCurrentIndex();
            if (currentIndex <= 0) {
                H5Log.d(TAG, "webview with no history and do exit!");
                h5Page.sendIntent(H5Plugin.H5_PAGE_CLOSE, null);
                return;
            }

            h5WebView.goBack();
        }
    }
}
