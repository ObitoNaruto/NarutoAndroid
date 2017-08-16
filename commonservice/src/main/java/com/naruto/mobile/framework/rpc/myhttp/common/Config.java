package com.naruto.mobile.framework.rpc.myhttp.common;

import com.naruto.mobile.framework.rpc.myhttp.transport.Transport;

public interface Config {
    String getUrl();
    Transport getTransport();
}
