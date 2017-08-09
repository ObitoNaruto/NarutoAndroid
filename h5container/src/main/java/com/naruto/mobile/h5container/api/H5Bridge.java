
package com.naruto.mobile.h5container.api;

import com.alibaba.fastjson.JSONObject;

public interface H5Bridge {

    public static interface BridgePolicy {
        public boolean shouldBan(String api);
    }

    public void sendToNative(H5Intent intent);

    public void sendToWeb(H5Intent intent);

    public void sendToWeb(String action, JSONObject param, H5CallBack callback);

    public void setBridgePolicy(BridgePolicy policy);

}
