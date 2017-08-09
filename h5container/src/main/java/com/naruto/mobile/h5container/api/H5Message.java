
package com.naruto.mobile.h5container.api;

import com.alibaba.fastjson.JSONObject;

public interface H5Message {

    public void setId(String id);

    public String getId();

    public void setTarget(H5CoreNode source);

    public H5CoreNode getTarget();

    public void cancel();

    public boolean isCanceled();

    public void setParam(JSONObject data);

    public JSONObject getParam();

}
