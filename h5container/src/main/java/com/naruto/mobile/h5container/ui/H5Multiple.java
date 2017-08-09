
package com.naruto.mobile.h5container.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSONObject;
import com.naruto.mobile.h5container.R;
import com.naruto.mobile.h5container.api.H5Bundle;
import com.naruto.mobile.h5container.api.H5Context;
import com.naruto.mobile.h5container.api.H5Intent;
import com.naruto.mobile.h5container.api.H5IntentFilter;
import com.naruto.mobile.h5container.api.H5Page;
import com.naruto.mobile.h5container.api.H5Page.H5PageHandler;
import com.naruto.mobile.h5container.api.H5Param;
import com.naruto.mobile.h5container.api.H5Plugin;
import com.naruto.mobile.h5container.env.H5BaseActivity;
import com.naruto.mobile.h5container.env.H5Container;

public class H5Multiple extends H5BaseActivity {

    public static final String TAG = "H5Multiple";

    private LinearLayout llMultiple;
    private FrameLayout flContainer;

    private View view1, view2;

    public void onClick(View view) {
        if (view1.getVisibility() == View.VISIBLE) {
            view1.setVisibility(View.GONE);
            view2.setVisibility(View.VISIBLE);
        } else {
            view1.setVisibility(View.VISIBLE);
            view2.setVisibility(View.GONE);
        }
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        setContentView(R.layout.activity_multiple);
        llMultiple = (LinearLayout) findViewById(R.id.ll_multiple);
        llMultiple.setBackgroundColor(0x8033CCFF);

        flContainer = (FrameLayout) findViewById(R.id.fl_container);

        H5Context h5Context = new H5Context(this);

        Bundle param1 = new Bundle();
        String url1 = "http://d.alipay.com/bollywood/bollywoodA019.htm?sign=intro";
        // url1 = "http://ux.alipay-inc.com/ftp/h5/dawson/entry.html";
        // param1.putString(H5Param.LONG_URL, url1);
        url1 = "https://www.baidu.com/";
        param1.putString(H5Param.SESSION_ID, "multiple_h5");
        H5Bundle bundle1 = new H5Bundle();
        bundle1.setParams(param1);
        H5Page page1 = H5Container.getService().createPage(h5Context, null);
        page1.setHandler(handler);
        FrameLayout.LayoutParams lp1 = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        lp1.setMargins(20, 32, 20, 32);
        view1 = page1.getContentView();
        view1.setVisibility(View.GONE);
        flContainer.addView(view1, lp1);

        Bundle param2 = new Bundle();
        String url2 = "http://d.alipay.com/bollywood/bollywoodA019.htm";
        // url2 = "http://ux.alipay-inc.com/ftp/h5/dawson/test.html";
        // param2.putString("url", url2);
        url2 = "http://www.ifeng.com/";
        param2.putString(H5Param.SESSION_ID, "multiple_h5");
        H5Bundle bundle2 = new H5Bundle();
        bundle2.setParams(param2);
        H5Page page2 = H5Container.getService().createPage(h5Context, null);
        page2.setHandler(handler);
        page2.getPluginManager().register(plugin);

        FrameLayout.LayoutParams lp2 = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        lp2.setMargins(20, 32, 20, 32);
        view2 = page2.getContentView();
        view2.setVisibility(View.GONE);
        flContainer.addView(view2, lp2);
        // String html = "<!DOCTYPE HTML><html><body><h2>Dawson HTML Data</h2></body></html>";
        // page2.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);

        page2.loadUrl(url2);
        page1.loadUrl(url1);
    }

    private H5Plugin plugin = new H5Plugin() {

        @Override
        public void onRelease() {

        }

        @Override
        public boolean interceptIntent(H5Intent intent) {
            JSONObject param = intent.getParam();
            String action = intent.getAction();
            if (H5Plugin.H5_PAGE_PROGRESS.equals(action)) {
                int progress = param.getIntValue("progress");
                // update your progress here
            }
            return false;
        }

        @Override
        public boolean handleIntent(H5Intent intent) {
            return false;
        }

        @Override
        public void getFilter(H5IntentFilter filter) {
            filter.addAction(H5Plugin.H5_PAGE_PROGRESS);
        }
    };

    private H5PageHandler handler = new H5PageHandler() {

        @Override
        public boolean shouldExit() {
            return false;
        }
    };
}
