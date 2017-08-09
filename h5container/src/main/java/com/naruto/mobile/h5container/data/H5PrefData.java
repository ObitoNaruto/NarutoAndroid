
package com.naruto.mobile.h5container.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.naruto.mobile.h5container.api.H5Data;
import com.naruto.mobile.h5container.env.H5Environment;

public class H5PrefData implements H5Data {

    private SharedPreferences sp;

    public H5PrefData(String name) {
        String localName = "h5_data_";
        if (!TextUtils.isEmpty(name)) {
            localName = localName + name;
        }

        sp = H5Environment.getContext().getSharedPreferences(localName,
                Context.MODE_PRIVATE);
    }

    @Override
    public void set(String name, String value) {
        sp.edit().putString(name, value).commit();
    }

    @Override
    public String get(String name) {
        return sp.getString(name, null);
    }

    @Override
    public String remove(String name) {
        String value = sp.getString(name, null);
        if (!TextUtils.isEmpty(value)) {
            sp.edit().remove(name).commit();
        }
        return value;
    }

    @Override
    public boolean has(String name) {
        return sp.contains(name);
    }

}
