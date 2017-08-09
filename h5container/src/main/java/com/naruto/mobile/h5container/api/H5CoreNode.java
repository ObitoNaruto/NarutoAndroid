
package com.naruto.mobile.h5container.api;

import com.alibaba.fastjson.JSONObject;

public interface H5CoreNode extends H5DataProvider, H5IntentTarget {

    public void setParent(H5CoreNode parent);

    public H5CoreNode getParent();

    public boolean addChild(H5CoreNode child);

    public boolean removeChild(H5CoreNode child);

    public H5PluginManager getPluginManager();

    public void sendIntent(String action, JSONObject param);

}
