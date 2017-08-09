
package com.naruto.mobile.h5container.core;

import com.naruto.mobile.h5container.api.H5Data;
import com.naruto.mobile.h5container.api.H5Scenario;
import com.naruto.mobile.h5container.data.H5PrefData;

public class H5ScenarioImpl implements H5Scenario {

    public static final String TAG = "H5Scenario";

    private String name;
    private H5Data data;

    public H5ScenarioImpl(String name) {
        setName(name);
    }

    @Override
    public H5Data getData() {
        return data;
    }

    @Override
    public void setData(H5Data data) {
        this.data = data;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        data = new H5PrefData(name);
    }

}
