
package com.naruto.mobile.h5container.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.naruto.mobile.h5container.api.H5CoreNode;
import com.naruto.mobile.h5container.api.H5Data;
import com.naruto.mobile.h5container.api.H5Intent;
import com.naruto.mobile.h5container.api.H5PluginManager;
import com.naruto.mobile.h5container.env.H5Container;
import com.naruto.mobile.h5container.manager.H5PluginManagerImpl;
import com.naruto.mobile.h5container.util.H5Log;

public abstract class H5CoreTarget implements H5CoreNode {

    public static final String TAG = "H5CoreTarget";

    private H5PluginManager pluginManager;
    private H5CoreNode parent;
    private List<H5CoreNode> children;
    protected H5Data h5Data;

    public H5CoreTarget() {
        parent = null;
        children = new ArrayList<H5CoreNode>();
        pluginManager = new H5PluginManagerImpl();
    }

    public void setParent(H5CoreNode parent) {
        if (parent == this.parent) {
            return;
        }

        // remove old relationship
        if (this.parent != null) {
            this.parent.removeChild(this);
        }

        this.parent = parent;

        // add new relationship
        if (this.parent != null) {
            this.parent.addChild(this);
        }
    }

    public H5CoreNode getParent() {
        return this.parent;
    }

    public synchronized boolean addChild(H5CoreNode child) {
        if (child == null) {
            return false;
        }

        for (H5CoreNode target : children) {
            if (target.equals(child)) {
                return false;
            }
        }

        children.add(child);
        child.setParent(this);
        return true;
    }

    public synchronized boolean removeChild(H5CoreNode child) {
        if (child == null) {
            return false;
        }

        Iterator<H5CoreNode> iterator = children.iterator();
        while (iterator.hasNext()) {
            H5CoreNode target = iterator.next();
            if (target.equals(child)) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean interceptIntent(H5Intent intent) {
        if (pluginManager != null) {
            return pluginManager.interceptIntent(intent);
        } else {
            return false;
        }
    }

    @Override
    public boolean handleIntent(H5Intent intent) {
        if (pluginManager != null) {
            return pluginManager.handleIntent(intent);
        } else {
            return false;
        }
    }

    @Override
    public void onRelease() {
        if (pluginManager != null) {
            pluginManager.onRelease();
            pluginManager = null;
        }
        h5Data = null;
    }

    @Override
    public H5PluginManager getPluginManager() {
        return pluginManager;
    }

    @Override
    public H5Data getData() {
        return this.h5Data;
    }

    @Override
    public void setData(H5Data data) {
        this.h5Data = data;
    }

    @Override
    public void sendIntent(String action, JSONObject param) {
        H5Log.d(TAG, "sendIntent action " + action);
        H5IntentImpl intent = new H5IntentImpl();
        intent.setAction(action);
        intent.setParam(param);
        intent.setTarget(this);
        H5Container.getMesseger().sendIntent(intent);
    }
}
