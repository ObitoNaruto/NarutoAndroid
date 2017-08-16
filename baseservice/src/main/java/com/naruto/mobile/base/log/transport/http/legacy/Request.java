/**
 * 
 */
package com.naruto.mobile.base.log.transport.http.legacy;

import android.content.Context;

/**
 * @author sanping.li
 * 请求元素
 *
 */
public class Request {
    private String  mUrl;
    private String  mPostData;
    private boolean mRelAccount;

    public Request(String url) {
        mUrl = url;
    }

    public String getUrl(Context context) {
        //TODO:
        //        return Constant.getURL(context,mUrl);
        return mUrl;
    }

    public String getPostData() {
        return mPostData;
    }

    public boolean getRelAccount() {
        return mRelAccount;
    }

    public void setPostData(String postData) {
        mPostData = postData;
    }

    public void setRelAccount(boolean relAccount) {
        mRelAccount = relAccount;
    }

}
