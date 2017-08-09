/**
 * 
 */

package com.naruto.mobile.h5container.core;

/**
 * the implement of proxy server for H5 app
 */
public class H5Proxy {
    H5ProxyStateListener listener;

    /**
     * set listener to H5Proxy, listener cleared when null is set
     * @param listener
     */
    public void setStateListenr(H5ProxyStateListener listener) {
        this.listener = listener;
    }

    public boolean start() {
        return true;
    }

    public boolean stop() {
        return true;
    }
}
