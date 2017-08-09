
package com.naruto.mobile.h5container.ui;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;

import java.util.Set;

import com.alibaba.fastjson.JSONObject;
import com.naruto.mobile.h5container.R;
import com.naruto.mobile.h5container.api.H5Page.H5PageHandler;
import com.naruto.mobile.h5container.api.H5Param;
import com.naruto.mobile.h5container.api.H5Plugin;
import com.naruto.mobile.h5container.core.H5PageImpl;
import com.naruto.mobile.h5container.env.H5BaseActivity;
import com.naruto.mobile.h5container.env.H5Environment;
import com.naruto.mobile.h5container.util.H5Log;
import com.naruto.mobile.h5container.util.H5Utils;
import com.naruto.mobile.h5container.util.KeyboardUtil;
import com.naruto.mobile.h5container.util.KeyboardUtil.KeyboardListener;
import com.naruto.mobile.h5container.view.H5FontBar;
import com.naruto.mobile.h5container.view.H5NavigationBar;
import com.naruto.mobile.h5container.view.H5TitleBar;
import com.naruto.mobile.h5container.view.H5ToolBar;

public class H5Activity extends H5BaseActivity {

    public static final String TAG = "H5Activity";

    public interface PageListener {
        public void onActivityResult(int resultCode, Intent intent);
    }

    private boolean isRunning;
    private Bundle startParams;
    private ViewGroup h5RootView;

    private H5NavigationBar h5NavBar;
    private H5TitleBar h5TitleBar;
    private View titleBarView;
    private View mBackView;
    private H5ToolBar h5ToolBar;
    private H5FontBar h5FontBar;
    private View toolBarView;
    private H5WebContent h5WebContainer;
    private View h5WebContent;
    private H5PageImpl h5Page;
    private KeyboardUtil KeyboardHelper;
    private PageListener pageListener;
    private boolean newNavStyle;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        newNavStyle = true;

        H5Environment.setContext(this);
        setContentView(R.layout.h5_activity);
        h5RootView = (ViewGroup) findViewById(R.id.h5_container_root);
        h5Page = new H5PageImpl(this, null);
        h5Page.setHandler(new H5PageHandler() {

            @Override
            public boolean shouldExit() {
                return true;
            }
        });

        if (newNavStyle) {
            initNavBar();
        } else {
            initTitleBar();
        }
        initToolBar();
        initFontBar();
        initWebContent();
        initHelpers();
        h5Page.applyParams();
        applyParams();
    }

    public void setPageListener(PageListener pl) {
        this.pageListener = pl;
    }

    private void initHelpers() {
        KeyboardHelper = new KeyboardUtil(this);
        KeyboardHelper.setListener(keyboardListener);
    }

    private KeyboardListener keyboardListener = new KeyboardListener() {

        @Override
        public void onKeyboardVisible(boolean visible) {
            H5Log.d(TAG, "onKeyboardVisible " + visible);
            if (visible) {
                String publicId = H5Utils.getString(startParams,
                        H5Param.PUBLIC_ID, "");
                String url = h5Page.getUrl();
                JSONObject param = new JSONObject();
                param.put(H5Param.PUBLIC_ID, publicId);
                param.put(H5Param.LONG_URL, url);
                h5Page.sendIntent(H5Plugin.KEY_BOARD_BECOME_VISIBLE, param);
            }
        }
    };

    private void initNavBar() {
        h5NavBar = new H5NavigationBar(h5Page);
        h5Page.getPluginManager().register(h5NavBar);

        titleBarView = h5NavBar.getContent();
        int height = (int) H5Environment.getResources().getDimension(
                R.dimen.h5_title_height);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, height);
        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        h5RootView.addView(titleBarView, 0, lp);
    }

    private void initTitleBar() {
        h5TitleBar = new H5TitleBar(h5Page);
        h5Page.getPluginManager().register(h5TitleBar);

        titleBarView = h5TitleBar.getContent();
        mBackView = h5TitleBar.getBackView();
        mBackView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				h5Page.sendIntent(H5Plugin.H5_PAGE_PHYSICAL_BACK, null);
			}
		});
        
        int height = (int) H5Environment.getResources().getDimension(
                R.dimen.h5_title_height);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, height);
        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        h5RootView.addView(titleBarView, 0, lp);
    }

    private void initToolBar() {
        h5ToolBar = new H5ToolBar(h5Page);

        h5Page.getPluginManager().register(h5ToolBar);

        toolBarView = h5ToolBar.getContent();
        int height = (int) H5Environment.getResources().getDimension(
                R.dimen.h5_bottom_height);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, height);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        h5RootView.addView(toolBarView, h5RootView.getChildCount(), lp);
    }

    private void initFontBar() {
        h5FontBar = new H5FontBar(h5Page);
        h5Page.getPluginManager().register(h5FontBar);
    }

    private void initWebContent() {
        h5WebContainer = new H5WebContent(h5Page);
        h5WebContent = h5WebContainer.getContent();
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        if (toolBarView != null) {
            lp.addRule(RelativeLayout.ABOVE, toolBarView.getId());
        }
        if (titleBarView != null) {
            lp.addRule(RelativeLayout.BELOW, titleBarView.getId());
        }
        h5RootView.addView(h5WebContent, 0, lp);

        h5Page.getPluginManager().register(h5WebContainer);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent intent) {
        boolean backEvent = (intent.getKeyCode() == KeyEvent.KEYCODE_BACK)
                && (intent.getRepeatCount() == 0);
        if (backEvent) {
            h5Page.sendIntent(H5Plugin.H5_PAGE_PHYSICAL_BACK, null);
            return true;
        } else {
            return super.onKeyDown(keyCode, intent);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onResume() {
        super.onResume();
        if (!isRunning) {
            isRunning = true;
        } else {
            h5Page.sendIntent(H5Plugin.H5_PAGE_RESUME, null);
        }
        // this operation is a must to start/stop the flash player
        if (Build.VERSION.SDK_INT >= 11 && h5Page.getWebView() != null) {
            h5Page.getWebView().onResume();
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onPause() {
        super.onPause();

        // this operation is a must to start/stop the flash player
        if (Build.VERSION.SDK_INT >= 11 && h5Page.getWebView() != null) {
            h5Page.getWebView().onPause();
        }
    }

    @Override
    protected void onDestroy() {
        if (!isRunning) {
            return;
        }
        H5Log.d(TAG, "onDestroy H5Activity");
        isRunning = false;
        h5Page.exitPage();
        h5Page.sendIntent(H5Plugin.H5_PAGE_CLOSED, null);
        h5ToolBar = null;
        h5TitleBar = null;
        h5FontBar = null;
        KeyboardHelper.setListener(null);
        KeyboardHelper = null;
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (this.pageListener != null) {
            this.pageListener.onActivityResult(resultCode, data);
            this.pageListener = null;
        }
    }

    private void applyParams() {
        startParams = h5Page.getParams();

        // force to set title bar invisible if tool bar visible.
        if (H5Utils.getBoolean(startParams, H5Param.LONG_SHOW_TOOLBAR, false)) {
            H5Log.d(TAG, "force to hide titlebar!");
            startParams.putBoolean(H5Param.LONG_SHOW_TITLEBAR, false);
        }

        StringBuilder builder = new StringBuilder();
        builder.append("H5 start params:");

        Set<String> keys = startParams.keySet();
        for (String key : keys) {
            Object value = startParams.get(key);
            String text = String.format("\n[%s ==> %s]", key, value);
            builder.append(text);
        }

        String paramsStr = builder.toString();
        H5Log.d(TAG, paramsStr);

        for (String key : keys) {
            String intentName = null;
            JSONObject param = new JSONObject();
            if (H5Param.LONG_SHOW_TITLEBAR.equals(key)) {
                boolean value = H5Utils.getBoolean(startParams, key, true);
                if (value == true) {
                    intentName = H5Plugin.SHOW_TITLE_BAR;
                } else {
                    intentName = H5Plugin.HIDE_TITLE_BAR;
                }
            } else if (H5Param.LONG_SHOW_TOOLBAR.equals(key)) {
                boolean value = H5Utils.getBoolean(startParams, key, false);
                if (value == true) {
                    intentName = H5Plugin.SHOW_TOOL_BAR;
                } else {
                    intentName = H5Plugin.HIDE_TOOL_BAR;
                }
            } else if (H5Param.LONG_DEFAULT_TITLE.equals(key)) {
                String title = H5Utils.getString(startParams, key);
                intentName = H5Plugin.SET_TITLE;
                param.put("title", title);
            } else if (H5Param.LONG_READ_TITLE.equals(key)) {
                boolean readTitle = H5Utils.getBoolean(startParams, key, true);
                intentName = H5Plugin.READ_TITLE;
                param.put(key, readTitle);
            } else if (H5Param.LONG_TOOLBAR_MENU.equals(key)) {
                String toolbarMenu = H5Utils.getString(startParams, key);
                param = H5Utils.parseObject(toolbarMenu);
                intentName = H5Plugin.SET_TOOL_MENU;
            } else if (H5Param.LONG_PULL_REFRESH.equals(key)) {
                boolean refresh = H5Utils.getBoolean(startParams, key, false);
                param.put(key, refresh);
                intentName = H5Plugin.PULL_REFRESH;
            } else if (H5Param.LONG_CAN_PULL_DOWN.equals(key)) {
                boolean down = H5Utils.getBoolean(startParams, key, true);
                param.put(key, down);
                intentName = H5Plugin.CAN_PULL_DOWN;
            } else if (H5Param.LONG_SHOW_PROGRESS.equals(key)) {
                boolean show = H5Utils.getBoolean(startParams, key, false);
                param.put(key, show);
                intentName = H5Plugin.SHOW_PROGRESS_BAR;
            }
            if (!TextUtils.isEmpty(intentName)) {
                h5Page.sendIntent(intentName, param);
            }
        }
    }
}
