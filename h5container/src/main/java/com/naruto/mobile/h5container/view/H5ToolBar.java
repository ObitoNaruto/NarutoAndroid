
package com.naruto.mobile.h5container.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.alibaba.fastjson.JSONObject;
import com.naruto.mobile.h5container.R;
import com.naruto.mobile.h5container.api.H5Intent;
import com.naruto.mobile.h5container.api.H5IntentFilter;
import com.naruto.mobile.h5container.api.H5Page;
import com.naruto.mobile.h5container.api.H5Plugin;
import com.naruto.mobile.h5container.util.H5Log;
import com.naruto.mobile.h5container.util.H5Utils;

public class H5ToolBar implements H5Plugin, OnClickListener {

    private final static String TAG = "H5ToolBar";

    private H5Page h5Page;

    private View content;
    private View llBack;
    private View tvClose;
    private ImageView ivMenu;
    private View pbReload;
    private View ivReload;

    private H5ToolMenu h5ToolMenu;

    @SuppressLint("InflateParams")
    public H5ToolBar(H5Page page) {
        this.h5Page = page;
        Context context = h5Page.getContext().getContext();
        content = LayoutInflater.from(context).inflate(R.layout.h5_tool_bar,
                null);
        llBack = content.findViewById(R.id.h5_toolbar_back);
        tvClose = content.findViewById(R.id.h5_toolbar_close);
        ivMenu = (ImageView) content.findViewById(R.id.h5_toolbar_menu_setting);
        ivReload = content.findViewById(R.id.h5_toolbar_iv_refresh);
        pbReload = content.findViewById(R.id.h5_toolbar_pb_refresh);

        llBack.setOnClickListener(this);
        tvClose.setOnClickListener(this);
        ivMenu.setOnClickListener(this);
        ivReload.setOnClickListener(this);

        tvClose.setVisibility(View.INVISIBLE);

        h5ToolMenu = new H5ToolMenu(h5Page);
        updateMenu();
    }

    public View getContent() {
        return content;
    }

    @Override
    public void onRelease() {
        h5Page = null;
        h5ToolMenu = null;
    }

    @Override
    public boolean interceptIntent(H5Intent intent) {
        String action = intent.getAction();
        if (H5_PAGE_FINISHED.equals(action)) {
            pbReload.setVisibility(View.GONE);
            ivReload.setVisibility(View.VISIBLE);
        } else if (H5_PAGE_STARTED.equals(action)) {
            pbReload.setVisibility(View.VISIBLE);
            ivReload.setVisibility(View.GONE);
        } else if (SET_TOOL_MENU.equals(action)) {
            h5ToolMenu.setMenu(intent, false);
            updateMenu();
        } else if (H5_PAGE_SHOW_CLOSE.equals(action)) {
            JSONObject param = intent.getParam();
            boolean show = H5Utils.getBoolean(param, "show", false);
            if (show) {
                tvClose.setVisibility(View.VISIBLE);
            } else {
                tvClose.setVisibility(View.INVISIBLE);
            }
        }
        return false;
    }

    @Override
    public boolean handleIntent(H5Intent intent) {
        String action = intent.getAction();
        if (SHOW_TOOL_BAR.equals(action)) {
            showToolBar();
        } else if (HIDE_TOOL_BAR.equals(action)) {
            hideToolBar();
        } else {
            return false;
        }
        return true;
    }

    @Override
    public void getFilter(H5IntentFilter filter) {
        filter.addAction(SHOW_TOOL_BAR);
        filter.addAction(HIDE_TOOL_BAR);
        filter.addAction(SET_TOOL_MENU);
        filter.addAction(H5_PAGE_STARTED);
        filter.addAction(H5_PAGE_FINISHED);
        filter.addAction(H5_PAGE_SHOW_CLOSE);
    }

    protected void showToolBar() {
        content.setVisibility(View.VISIBLE);
    }

    protected void hideToolBar() {
        H5Log.d(TAG, "hideToolBar");
        content.setVisibility(View.GONE);
    }

    private void updateMenu() {
        if (h5ToolMenu.size() > 1) {
            ivMenu.setImageResource(R.drawable.options_selector);
        } else {
            ivMenu.setImageResource(R.drawable.font_size_selector);
        }
    }

    @Override
    public void onClick(View v) {
        if (h5Page == null || v == null) {
            H5Log.e(TAG, "FATAL ERROR, illegal parameter in onClick() h5page: "
                    + h5Page + " v: " + v);
            return;
        }

        if (v.equals(llBack)) {
            h5Page.sendIntent(H5Plugin.H5_TOOLBAR_BACK, null);
        } else if (v.equals(tvClose)) {
            h5Page.sendIntent(H5Plugin.H5_TOOLBAR_CLOSE, null);
        } else if (v.equals(ivMenu)) {
            h5Page.sendIntent(H5Plugin.H5_TOOLBAR_MENU, null);
            if (h5ToolMenu.size() <= 1) {
                h5Page.sendIntent(H5FontBar.SHOW_FONT_BAR, null);
            } else {
                h5ToolMenu.showMenu(ivMenu);
            }
        } else if (v.equals(ivReload)) {
            h5Page.sendIntent(H5Plugin.H5_TOOLBAR_RELOAD, null);
        }
    }

}
