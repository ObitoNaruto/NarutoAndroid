package com.naruto.mobile.base.Router.andRouter.interceptor;

import android.content.Context;

/**
 */

public interface Interceptor {

    /**
     * @param url
     * @return if intercept the request
     */
    boolean intercept(Context context, String url);

}
