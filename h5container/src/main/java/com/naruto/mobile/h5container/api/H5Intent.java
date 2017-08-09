
package com.naruto.mobile.h5container.api;

import com.alibaba.fastjson.JSONObject;

public interface H5Intent extends H5Message {

    public static enum Error {
        NONE, NOT_FOUND, INVALID_PARAM, UNKNOWN_ERROR, FORBIDDEN
    }

    public String getAction();

    public String getType();

    public H5CallBack getCallBack();

    public H5Bridge getBridge();

    // send back one time
    public boolean sendBack(JSONObject data);

    // send back one time
    public boolean sendBack(String k, Object o);

    // send more than one time
    public boolean keepSend(JSONObject data);

    // send more than one time
    public boolean keepSend(String k, Object o);

    public Error getError();

    public boolean sendError(Error code);

}
