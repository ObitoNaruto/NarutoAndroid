
package com.naruto.mobile.h5container.config;

import android.os.Bundle;

public class H5ConfigImpl implements H5Config {

    private Bundle config;

    public H5ConfigImpl() {
        config = new Bundle();
    }

    @Override
    public void setBoolean(String name, boolean value) {
        config.putBoolean(name, value);
    }

    @Override
    public boolean getBoolean(String name, boolean defaultValue) {
        return config.getBoolean(name, defaultValue);
    }

    @Override
    public void setString(String name, String value) {
        config.putString(name, value);
    }

    @Override
    public String getString(String name, String defaultValue) {
        return config.getString(name, defaultValue);
    }

    @Override
    public void setInt(String name, int value) {
        config.putInt(name, value);
    }

    @Override
    public int getInt(String name, int defaultValue) {
        return config.getInt(name, defaultValue);
    }

}
