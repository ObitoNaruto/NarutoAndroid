
package com.naruto.mobile.h5container.api;

public class H5BasePlugin implements H5Plugin {

    @Override
    public void onRelease() {

    }

    @Override
    public boolean interceptIntent(H5Intent intent) {
        return false;
    }

    @Override
    public boolean handleIntent(H5Intent intent) {
        return false;
    }

    @Override
    public void getFilter(H5IntentFilter filter) {

    }

}
