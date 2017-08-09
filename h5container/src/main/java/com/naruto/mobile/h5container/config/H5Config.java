
package com.naruto.mobile.h5container.config;

public interface H5Config {

    public void setBoolean(String name, boolean value);

    public boolean getBoolean(String name, boolean defaultValue);

    public void setString(String name, String value);

    public String getString(String name, String defaultValue);

    public void setInt(String name, int value);

    public int getInt(String name, int defaultValue);
}
