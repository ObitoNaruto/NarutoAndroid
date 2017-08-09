
package com.naruto.mobile.h5container;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.naruto.mobile.h5container.api.H5Bundle;
import com.naruto.mobile.h5container.api.H5Context;
import com.naruto.mobile.h5container.api.H5Intent;
import com.naruto.mobile.h5container.api.H5IntentFilter;
import com.naruto.mobile.h5container.api.H5Listener;
import com.naruto.mobile.h5container.api.H5Page;
import com.naruto.mobile.h5container.api.H5Param;
import com.naruto.mobile.h5container.api.H5Plugin;
import com.naruto.mobile.h5container.api.H5Session;
import com.naruto.mobile.h5container.env.H5Container;
import com.naruto.mobile.h5container.ui.H5Multiple;
import com.naruto.mobile.h5container.util.H5Log;
import com.naruto.mobile.h5container.util.H5Utils;

public class HomeActivity extends Activity {
    public static final String TAG = "HomeActivity";

    private H5Page h5Page;
    private String h5PageUrl = null;

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_home);
        setTheme(R.style.h5_transparent);

        TextView tvSingle = (TextView) findViewById(R.id.tv_single);
        TextView tvMultiple = (TextView) findViewById(R.id.tv_multiple);
        tvSingle.setOnClickListener(clickListener);
        tvMultiple.setOnClickListener(clickListener);
    }

    private OnClickListener clickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.tv_single) {
                launchSinglePage();
            } else if (v.getId() == R.id.tv_multiple) {
                launchMultiplePage();
            }
        }
    };

    private void launchSinglePage() {
        h5PageUrl = "http://ux.alipay-inc.com/ftp/h5/dawson/pure.html";
        h5PageUrl = "http://ux.alipay-inc.com/ftp/h5/dawson/redirect.html";
        h5PageUrl = "http://ux.alipay-inc.com/ftp/h5/dawson/test.html";
        h5PageUrl = "http://baijia.baidu.com";
        h5PageUrl = "http://ux.alipay-inc.com/ftp/h5/dawson/temp/images.html";
        h5PageUrl = "http://ux.alipay-inc.com/iclip.php";
        h5PageUrl = "file:///sdcard/readfile.html";
        h5PageUrl = "http://hlcdn.uuzz.com/H5V2/#type=url&url=buy/dlt.html?authcode=833330b3e1cc4923a1f335405e16eD79";
        h5PageUrl = "http://ux.alipay-inc.com/ftp/h5/dawson/entry.html";
        h5PageUrl = "http://www.baidu.com";
        h5PageUrl = "http://bj.meituan.com/?utm_campaign=baidu&utm_medium=organic&utm_source=baidu&utm_content=homepage&utm_term=";
//        h5PageUrl = "https://sf.pay.xiaomi.com/views/busCard-guide/";
        h5PageUrl = "http://staging.sf.pay.xiaomi.com/views/busCard-guide/";

        // set magic options
        // h5PageUrl = h5PageUrl + "?__webview_options__=defaultTitle%3dKKKK%26readTitle%3DNO";

        Bundle param = new Bundle();
        param.putString(H5Param.LONG_URL, h5PageUrl);
//        param.putBoolean(H5Param.LONG_SHOW_TITLEBAR, true);
//        param.putBoolean(H5Param.LONG_SHOW_TOOLBAR, false);
//        param.putBoolean(H5Param.LONG_SHOW_LOADING, true);
//        param.putBoolean(H5Param.LONG_PULL_REFRESH, true);
//        param.putBoolean(H5Param.LONG_CAN_PULL_DOWN, true);
//        param.putBoolean(H5Param.LONG_SHOW_PROGRESS, true);
//        param.putBoolean(H5Param.LONG_READ_TITLE, true);
//        param.putString(H5Param.LONG_BACK_BEHAVIOR, "back");
//        param.putString(H5Param.LONG_BIZ_SCENARIO, "dawson_scenario");
         param.putInt(H5Param.LONG_BACKGROUND_COLOR, /*0xfff8f8f8*/Color.parseColor("#ffffffff"));
//        param.putString(H5Param.DEFAULT_TITLE, "Default Title");
//        param.putString("installPath", "/sdcard/");
        param.putString(H5Param.SHOW_TITLEBAR, "YES");
        param.putString(H5Param.SHOW_TOOLBAR, "NO");
        param.putString(H5Param.SHOW_LOADING, "YES");
        param.putString(H5Param.READ_TITLE, "YES");

        H5Bundle bundle = new H5Bundle();
        bundle.addListener(h5Listener);
        bundle.setParams(param);
        H5Context h5Context = new H5Context(this);
        H5Container.getService().startPage(h5Context, bundle);
    }

    private void launchMultiplePage() {
        Intent intent = new Intent(this, H5Multiple.class);
        startActivity(intent);
    }

    private H5Listener h5Listener = new H5Listener() {

        @Override
        public void onSessionDestroyed(H5Session session) {
            Log.d(TAG, "onSessionDestroyed");
        }

        @Override
        public void onSessionCreated(H5Session session) {
            Log.d(TAG, "onSessionCreated");
        }

        @Override
        public void onPageDestroyed(H5Page page) {
            Log.d(TAG, "onPageDestroyed");
        }

        @Override
        public void onPageCreated(H5Page page) {
            Log.d(TAG, "onPageCreated");

            page.sendIntent(H5Plugin.SHOW_OPTION_MENU, null);

            String paramStr = "{menus:[{name:'投诉',tag:'"
                    + H5Container.MENU_COMPLAIN + "'},{name:'分享',tag:'"
                    + H5Container.MENU_SHARE + "'}]}";
            JSONObject param = H5Utils.parseObject(paramStr);

            page.sendIntent(H5Plugin.SET_TOOL_MENU, param);

            JSONObject ppp = new JSONObject();
            ppp.put("text", "支付宝宝宝宝");
            ppp.put("url", h5PageUrl);
            page.sendIntent(H5Container.H5_PAGE_SET_BACK_TEXT, ppp);

            page.getPluginManager().register(plugin);

            h5Page = page;
        }
    };

    private H5Plugin plugin = new H5Plugin() {

        private static final String CUSTOM_API = "customApi";

        @Override
        public boolean handleIntent(H5Intent intent) {
            String action = intent.getAction();
            JSONObject params = intent.getParam();
            if (CUSTOM_API.equals(action)) {
                Log.d(TAG, "custom plugin received action " + action);
                JSONObject result = new JSONObject();
                result.put("action", action);
                intent.sendBack(result);
            } else if (H5Plugin.H5_TOOLBAR_MENU_BT.equals(action)) {
                String tag = H5Utils.getString(params, "tag");
                Log.d(TAG, "on click tag " + tag);
                if (H5Container.MENU_SHARE.equals(tag)) {
                    H5Log.d(TAG, "share result " + params.toJSONString());
                } else {
                    return false;
                }
            }
            return true;
        }

        @Override
        public void getFilter(H5IntentFilter filter) {
            filter.addAction(CUSTOM_API);
            filter.addAction(H5Plugin.H5_TOOLBAR_MENU_BT);
        }

        @Override
        public boolean interceptIntent(H5Intent intent) {
            return false;
        }

        @Override
        public void onRelease() {

        }
    };

}
