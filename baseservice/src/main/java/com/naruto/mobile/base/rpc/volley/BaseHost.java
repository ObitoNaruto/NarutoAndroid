package com.naruto.mobile.base.rpc.volley;

public abstract class BaseHost {

    private String mStagingHost;

    protected String mOnlineHost;

    public BaseHost() {
        setStagingHost();
        setOnlineHost();
    }

    public void setStagingHost(String stagingHost) {
        mStagingHost = stagingHost;
    }

    public void setOnlineHost(String onlineHost) {
        mOnlineHost = onlineHost;
    }

    public String getStagingHost() {
        return mStagingHost;
    }

    public String getOnlineHost() {
        return mOnlineHost;
    }

    protected abstract void setStagingHost();

    protected abstract void setOnlineHost();
}
