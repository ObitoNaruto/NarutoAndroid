
package com.naruto.mobile.h5container.api;

public interface H5IntentTarget {

    public void onRelease();

    public boolean interceptIntent(H5Intent intent);

    public boolean handleIntent(H5Intent intent);

}
