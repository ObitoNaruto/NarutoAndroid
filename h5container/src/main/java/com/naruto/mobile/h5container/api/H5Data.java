
package com.naruto.mobile.h5container.api;

public interface H5Data {

    public void set(String name, String value);

    public String get(String name);

    public String remove(String name);

    public boolean has(String name);

}
