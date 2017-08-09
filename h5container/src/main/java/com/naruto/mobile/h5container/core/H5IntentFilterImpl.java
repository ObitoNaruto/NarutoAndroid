
package com.naruto.mobile.h5container.core;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.naruto.mobile.h5container.api.H5IntentFilter;

public class H5IntentFilterImpl implements H5IntentFilter {

    private Set<String> actions;

    public H5IntentFilterImpl() {
        actions = new HashSet<String>();
    }

    public void addAction(String action) {
        actions.add(action);
    }

    public Iterator<String> actionIterator() {
        return actions.iterator();
    }
}
