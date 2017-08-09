
package com.naruto.mobile.h5container.api;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class H5Bundle {
    public static final String TAG = "H5Bundle";

    private List<H5Listener> listeners;

    private Bundle params;

    public H5Bundle() {
        listeners = new ArrayList<H5Listener>();
    }

    public List<H5Listener> getListeners() {
        return listeners;
    }

    public void addListener(List<H5Listener> listeners) {
        if (listeners == null || listeners.isEmpty()) {
            return;
        }
        for (H5Listener listener : listeners) {
            addListener(listener);
        }
    }

    public void addListener(H5Listener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public Bundle getParams() {
        return params;
    }

    public void setParams(Bundle params) {
        this.params = params;
    }
}
