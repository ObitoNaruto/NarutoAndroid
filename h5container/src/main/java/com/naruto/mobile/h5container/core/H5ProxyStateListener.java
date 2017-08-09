/**
 * 
 */

package com.naruto.mobile.h5container.core;

/**
 */
public abstract class H5ProxyStateListener {
    public boolean enabled() {
        return true;
    }

    public abstract void onProxyAvailable(final String host, final String port);

    public abstract void onProxyChanged(final String host, final String port);
}
