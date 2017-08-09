
package com.naruto.mobile.h5container.data;

import java.util.HashMap;
import java.util.Map;

import com.naruto.mobile.h5container.api.H5Data;

public class H5MemData implements H5Data {

    private Map<String, String> map;

    public H5MemData() {
        map = new HashMap<String, String>();
    }

    @Override
    public void set(String name, String value) {
        map.put(name, value);
    }

    @Override
    public String get(String name) {
        return map.get(name);
    }

    @Override
    public String remove(String name) {
        String value = map.remove(name);
        return value;
    }

    @Override
    public boolean has(String name) {
        return map.containsKey(name);
    }

}
