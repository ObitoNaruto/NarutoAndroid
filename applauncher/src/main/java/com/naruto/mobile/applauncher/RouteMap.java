package com.naruto.mobile.applauncher;

import java.util.HashMap;
import java.util.Map;

/**
 */

public class RouteMap {

    private static Map<String, Class> mRouteMap;

    static {
        mRouteMap = new HashMap<>();
        mRouteMap.put("main", MainActivity.class);
    }

    public static Class getTargetClass(String key) {
        if(!mRouteMap.containsKey(key)){
            return null;
        }

        return mRouteMap.get(key);
    }
}
