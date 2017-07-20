package com.naruto.mobile.base.rpc.volley;

public class Task1Host extends BaseHost{

    private static final String SERVER_STAGING = "http://test.staging.com";
    private static final String SERVER_ONLINE = "https://test.online.com";

    @Override
    protected void setStagingHost() {
        setStagingHost(SERVER_STAGING);
    }

    @Override
    protected void setOnlineHost() {
        setOnlineHost(SERVER_ONLINE);
    }
}
