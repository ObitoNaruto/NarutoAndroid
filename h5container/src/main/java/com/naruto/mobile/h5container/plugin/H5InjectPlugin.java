
package com.naruto.mobile.h5container.plugin;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.naruto.mobile.h5container.api.H5Intent;
import com.naruto.mobile.h5container.api.H5IntentFilter;
import com.naruto.mobile.h5container.api.H5Plugin;
import com.naruto.mobile.h5container.core.H5PageImpl;
import com.naruto.mobile.h5container.util.H5Utils;
import com.naruto.mobile.h5container.web.H5JSInjector;

public class H5InjectPlugin implements H5Plugin {

    public static final String TAG = "H5InjectPlugin";

    private H5JSInjector injector;
    private H5PageImpl h5Page;

    public H5InjectPlugin(H5PageImpl page) {
        this.h5Page = page;
        injector = new H5JSInjector(h5Page);
    }

    @Override
    public void onRelease() {
        injector = null;
        h5Page = null;
    }

    @Override
    public void getFilter(H5IntentFilter filter) {
        filter.addAction(H5_PAGE_STARTED);
        filter.addAction(H5_PAGE_FINISHED);
        filter.addAction(H5_PAGE_RECEIVED_TITLE);
        filter.addAction(H5_PAGE_JS_PARAM);
    }

    @Override
    public boolean interceptIntent(H5Intent intent) {
        String action = intent.getAction();
        if (H5_PAGE_RECEIVED_TITLE.equals(action)) {
            injector.inject(false);
        } else if (H5_PAGE_FINISHED.equals(action)) {
            boolean updated = H5Utils.getBoolean(intent.getParam(), "pageUpdated", false);
            if (updated) {
                injector.inject(true);
            }
        } else if (H5_PAGE_STARTED.equals(action)) {
            injector.reset();
        }
        return false;
    }

    @Override
    public boolean handleIntent(H5Intent intent) {
        String action = intent.getAction();
        if (H5_PAGE_JS_PARAM.equals(action)) {
            JSONObject param = intent.getParam();
            for (String k : param.keySet()) {
                String v = H5Utils.getString(param, k);
                if (!TextUtils.isEmpty(k) && !TextUtils.isEmpty(v)) {
                    injector.addParam(k, v);
                }
            }
        } else {
            return false;
        }
        return true;
    }
}
