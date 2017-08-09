
package com.naruto.mobile.h5container.manager;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.naruto.mobile.h5container.api.H5Intent;
import com.naruto.mobile.h5container.api.H5IntentFilter;
import com.naruto.mobile.h5container.api.H5Plugin;
import com.naruto.mobile.h5container.api.H5PluginManager;
import com.naruto.mobile.h5container.core.H5IntentFilterImpl;
import com.naruto.mobile.h5container.util.H5Log;
import com.naruto.mobile.h5container.util.H5Utils;

public class H5PluginManagerImpl implements H5PluginManager {

    public static final String TAG = "H5PluginManager";

    private Set<H5Plugin> pluginSet;
    private Map<String, List<H5Plugin>> actionMap;

    public H5PluginManagerImpl() {
        pluginSet = new HashSet<H5Plugin>();
        actionMap = new HashMap<String, List<H5Plugin>>();
    }

    @Override
    public synchronized boolean register(H5Plugin plugin) {
        if (plugin == null) {
            H5Log.w(TAG, "invalid plugin parameter!");
            return false;
        }
        if (pluginSet.contains(plugin)) {
            H5Log.w(TAG, "plugin already registered!");
            return false;
        }

        H5IntentFilter filter = new H5IntentFilterImpl();
        plugin.getFilter(filter);

        Iterator<String> iterator = filter.actionIterator();
        if (!iterator.hasNext()) {
            H5Log.w(TAG, "empty filter");
            return false;
        }

        pluginSet.add(plugin);
        while (iterator.hasNext()) {
            String action = iterator.next();

            if (TextUtils.isEmpty(action)) {
                H5Log.w(TAG, "intent can't be empty!");
                continue;
            }

            List<H5Plugin> plugins;
            if (!actionMap.containsKey(action)) {
                plugins = new ArrayList<H5Plugin>();
                actionMap.put(action, plugins);
            } else {
                plugins = actionMap.get(action);
            }

            plugins.add(plugin);
        }

        String pluginName = H5Utils.getClassName(plugin);
        H5Log.d(TAG, "register plugin " + pluginName);

        return true;
    }

    @Override
    public synchronized boolean unregister(H5Plugin plugin) {
        if (plugin == null) {
            H5Log.w(TAG, "invalid plugin parameter!");
            return false;
        }

        if (!pluginSet.contains(plugin)) {
            H5Log.w(TAG, "plugin not registered!");
            return false;
        }

        // remove plugin from set
        pluginSet.remove(plugin);

        // remove plugin from action map
        Iterator<String> iterator = actionMap.keySet().iterator();
        while (iterator.hasNext()) {
            String action = iterator.next();
            List<H5Plugin> plugins = actionMap.get(action);

            Iterator<H5Plugin> i = plugins.iterator();
            while (i.hasNext()) {
                H5Plugin l = i.next();
                if (plugin.equals(l)) {
                    i.remove();
                }
            }

            if (plugins.isEmpty()) {
                actionMap.remove(action);
            }
        }

        String pluginName = H5Utils.getClassName(plugin);
        H5Log.d(TAG, "unregister plugin " + pluginName);
        return true;
    }

    @Override
    public synchronized boolean interceptIntent(H5Intent intent) {
        if (intent == null) {
            H5Log.e(TAG, "invalid intent!");
            return false;
        }

        String action = intent.getAction();
        if (TextUtils.isEmpty(action)) {
            H5Log.w(TAG, "invalid intent name");
            return false;
        }

        List<H5Plugin> plugins = actionMap.get(action);
        if (plugins == null || plugins.isEmpty()) {
            return false;
        }

        for (int index = plugins.size() - 1; index >= 0; --index) {
            H5Plugin plugin = plugins.get(index);
            boolean result = false;
            try {
                result = plugin.interceptIntent(intent);
            } catch (Exception e) {
                intent.sendError(H5Intent.Error.UNKNOWN_ERROR);
                H5Log.e(TAG, "interceptIntent exception.", e);
                return true;
            }
            if (!result) {
                continue;
            }
            String clazz = H5Utils.getClassName(plugin);
            H5Log.d(TAG, "[" + action + "] intecepted by " + clazz);
            return true;
        }

        return false;
    }

    @Override
    public synchronized boolean handleIntent(H5Intent intent) {
        if (intent == null) {
            H5Log.e(TAG, "invalid intent!");
            return false;
        }

        String action = intent.getAction();
        if (TextUtils.isEmpty(action)) {
            H5Log.w(TAG, "invalid intent name");
            return false;
        }

        List<H5Plugin> plugins = actionMap.get(action);
        if (plugins == null || plugins.isEmpty()) {
            return false;
        }

        for (int index = plugins.size() - 1; index >= 0; --index) {
            H5Plugin plugin = plugins.get(index);
            boolean result = false;
            try {
                result = plugin.handleIntent(intent);
            } catch (Exception e) {
                intent.sendError(H5Intent.Error.UNKNOWN_ERROR);
                H5Log.e(TAG, "handleIntent exception.", e);
                return true;
            }
            if (!result) {
                continue;
            }
            String clazz = H5Utils.getClassName(plugin);
            H5Log.d(TAG, "[" + action + "] handled by " + clazz);
            return true;
        }

        return false;
    }

    @Override
    public synchronized void onRelease() {
        for (H5Plugin plugin : pluginSet) {
            plugin.onRelease();
        }
        pluginSet.clear();
        actionMap.clear();
    }

    @Override
    public boolean register(List<H5Plugin> plugins) {
        if (plugins == null || plugins.isEmpty()) {
            return false;
        }

        boolean result = true;
        for (H5Plugin p : plugins) {
            result |= register(p);
        }

        return result;
    }

    @Override
    public boolean unregister(List<H5Plugin> plugins) {
        if (plugins == null || plugins.isEmpty()) {
            return false;
        }

        boolean result = true;
        for (H5Plugin p : plugins) {
            result |= unregister(p);
        }

        return result;
    }

    @Override
    public synchronized boolean canHandle(String action) {
        if (TextUtils.isEmpty(action)) {
            return false;
        }
        return actionMap.containsKey(action);
    }

}
