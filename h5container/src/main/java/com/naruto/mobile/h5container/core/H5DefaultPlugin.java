
package com.naruto.mobile.h5container.core;

import com.alibaba.fastjson.JSONObject;
import com.naruto.mobile.h5container.api.H5Intent;
import com.naruto.mobile.h5container.api.H5IntentFilter;
import com.naruto.mobile.h5container.api.H5Page;
import com.naruto.mobile.h5container.api.H5Plugin;
import com.naruto.mobile.h5container.env.H5Container;

public class H5DefaultPlugin implements H5Plugin {

    @Override
    public boolean interceptIntent(H5Intent intent) {
        return false;
    }

    @Override
    public boolean handleIntent(H5Intent intent) {
        String action = intent.getAction();
        if (H5_PAGE_SHOULD_LOAD_URL.equals(action)) {
            loadUrl(intent);
        } else if (H5_PAGE_SHOULD_LOAD_DATA.equals(action)) {
            loadData(intent);
        } else if (H5_TOOLBAR_MENU_BT.equals(action)) {
            JSONObject param = intent.getParam();
            JSONObject event = new JSONObject();
            event.put("data", param);
            H5Page h5Page = (H5Page) intent.getTarget();
            h5Page.getBridge().sendToWeb("toolbarMenuClick", event, null);
        } else {
            return false;
        }
        return true;
    }

    @Override
    public void getFilter(H5IntentFilter filter) {
        filter.addAction(H5_PAGE_SHOULD_LOAD_URL);
        filter.addAction(H5_PAGE_SHOULD_LOAD_DATA);
        filter.addAction(H5_TOOLBAR_MENU_BT);
    }

    @Override
    public void onRelease() {

    }

    private void loadUrl(H5Intent intent) {
        H5IntentImpl load = new H5IntentImpl(H5Container.H5_PAGE_DO_LOAD_URL);
        JSONObject param = intent.getParam();
        load.setParam(param);
        load.setTarget(intent.getTarget());
        H5Container.getMesseger().sendIntent(load);
    }

    private void loadData(H5Intent intent) {
        H5IntentImpl load = new H5IntentImpl(H5_PAGE_LOAD_DATA);
        JSONObject param = intent.getParam();
        load.setParam(param);
        load.setTarget(intent.getTarget());
        H5Container.getMesseger().sendIntent(load);
    }

}
