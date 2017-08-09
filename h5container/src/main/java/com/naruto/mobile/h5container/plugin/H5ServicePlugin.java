
package com.naruto.mobile.h5container.plugin;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.naruto.mobile.h5container.api.H5Data;
import com.naruto.mobile.h5container.api.H5Intent;
import com.naruto.mobile.h5container.api.H5IntentFilter;
import com.naruto.mobile.h5container.api.H5Plugin;
import com.naruto.mobile.h5container.env.H5Container;
import com.naruto.mobile.h5container.util.H5Utils;

public class H5ServicePlugin implements H5Plugin {

    public H5ServicePlugin() {

    }

    @Override
    public void onRelease() {

    }

    @Override
    public void getFilter(H5IntentFilter filter) {
        filter.addAction(SET_SHARE_DATA);
        filter.addAction(GET_SHARE_DATA);
        filter.addAction(REMOVE_SHARE_DATA);
    }

    @Override
    public boolean interceptIntent(H5Intent intent) {
        return false;
    }

    @Override
    public boolean handleIntent(H5Intent intent) {
        String action = intent.getAction();
        if (GET_SHARE_DATA.equals(action)) {
            getSharedData(intent);
        } else if (SET_SHARE_DATA.equals(action)) {
            setSharedData(intent);
        } else if (REMOVE_SHARE_DATA.equals(action)) {
            removeShareData(intent);
        }
        return true;
    }

    private void setSharedData(H5Intent intent) {
        JSONObject param = intent.getParam();
        JSONObject data = H5Utils.getJSONObject(param, "data", null);
        if (data == null || data.isEmpty()) {
            return;
        }

        H5Data shareData = H5Container.getService().getData();

        for (String key : data.keySet()) {
            String value = H5Utils.getString(data, key);
            if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
                shareData.set(key, value);
            }
        }
    }

    private void getSharedData(H5Intent intent) {
        JSONObject param = intent.getParam();
        JSONObject data = new JSONObject();
        JSONArray keys = H5Utils.getJSONArray(param, "keys", null);

        H5Data shareData = H5Container.getService().getData();

        if (keys != null && !keys.isEmpty()) {
            int size = keys.size();
            for (int index = 0; index < size; ++index) {
                Object obj = keys.get(index);
                if (!(obj instanceof String)) {
                    continue;
                }
                String key = (String) obj;
                String value = shareData.get(key);
                data.put(key, value);
            }
        }
        JSONObject result = new JSONObject();
        result.put("data", data);
        intent.sendBack(result);
    }

    private void removeShareData(H5Intent intent) {
        JSONObject param = intent.getParam();
        JSONArray keys = H5Utils.getJSONArray(param, "keys", null);

        H5Data shareData = H5Container.getService().getData();

        if (keys != null && !keys.isEmpty()) {
            int size = keys.size();
            for (int index = 0; index < size; ++index) {
                Object obj = keys.get(index);
                if (!(obj instanceof String)) {
                    continue;
                }
                String key = (String) obj;
                shareData.remove(key);
            }
        }
    }

}
