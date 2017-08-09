
package com.naruto.mobile.h5container.plugin;

import com.alibaba.fastjson.JSONObject;
import com.naruto.mobile.h5container.api.H5Intent;
import com.naruto.mobile.h5container.api.H5IntentFilter;
import com.naruto.mobile.h5container.api.H5Plugin;
import com.naruto.mobile.h5container.util.H5Utils;

public class H5NetworkPlugin implements H5Plugin {

    @Override
    public void getFilter(H5IntentFilter filter) {
        filter.addAction(GET_NETWORK_TYPE);
    }

    @Override
    public boolean interceptIntent(H5Intent intent) {
        return false;
    }

    @Override
    public void onRelease() {

    }

    @Override
    public boolean handleIntent(H5Intent intent) {
        String action = intent.getAction();
        if (GET_NETWORK_TYPE.equals(action)) {
            getNetworkType(intent);
        }
        return true;
    }

    private void getNetworkType(H5Intent intent) {
        final String value = H5Utils.getNetworkType();
        final String err_msg = "network_type:" + value;
        JSONObject data = new JSONObject();
        data.put("err_msg", err_msg);
        data.put("networkType", value);
        boolean hasNetwork = !("fail".equals(value));
        data.put("networkAvailable", hasNetwork);
        intent.sendBack(data);
    }
}
