
package com.naruto.mobile.h5container.api;

import java.util.Iterator;

public interface H5IntentFilter {

    public void addAction(String action);

    public Iterator<String> actionIterator();
}
