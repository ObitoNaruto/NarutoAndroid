package com.naruto.mobile.base.rpc.volley;

public class AccountInfo {
    /**
     * 初始token，accountManager获取的未解析token，用于重置token
     */
    private String mAuthToken;

    /**
     * 安全秘钥.用于参数的aes加解密.
     */
    private String mSecurity;

    /**
     * 用户id.
     */
    private String mUserId;
    /**
     * token
     */
    private String mServiceToken;

    public String getSecurity() {
        return mSecurity;
    }

    public void setSecurity(String security) {
        this.mSecurity = security;
    }

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String userId) {
        this.mUserId = userId;
    }

    public String getServiceToken() {
        return mServiceToken;
    }

    public void setServiceToken(String serviceToken) {
        this.mServiceToken = serviceToken;
    }

    public String getAuthToken() {
        return mAuthToken;
    }

    public void setAuthToken(String authToken) {
        this.mAuthToken = authToken;
    }
}
