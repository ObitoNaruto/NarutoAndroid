
package com.naruto.mobile.h5container.core;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import com.naruto.mobile.h5container.api.H5CoreNode;
import com.naruto.mobile.h5container.api.H5Intent;
import com.naruto.mobile.h5container.api.H5IntentTarget;
import com.naruto.mobile.h5container.api.H5Message;
import com.naruto.mobile.h5container.util.H5Log;

public class H5IntentRouter implements H5MessegerRouter {

    public static final String TAG = "H5IntentRouter";

    public H5IntentRouter() {
    }

    @Override
    public boolean routeMesseger(H5Message messeger) {
        if (!(messeger instanceof H5Intent)) {
            H5Log.e(TAG, "invalid message instance!");
            return false;
        }

        H5Intent intent = (H5Intent) messeger;
        String actionName = intent.getAction();
        if (TextUtils.isEmpty(actionName)) {
            H5Log.w(TAG, "invalid action name");
            intent.sendError(H5Intent.Error.INVALID_PARAM);
            return false;
        }

        H5CoreNode target = intent.getTarget();
        List<H5IntentTarget> targetList = new ArrayList<H5IntentTarget>();
        while (target != null) {
            targetList.add(target);
            target = target.getParent();
        }
        if (targetList.isEmpty()) {
            H5Log.w(TAG, "no intent target!");
            intent.sendError(H5Intent.Error.INVALID_PARAM);
            return false;
        }

        H5Log.d(TAG, "routing intent " + actionName);
        int size = targetList.size();

        for (int index = size - 1; index >= 0; --index) {
            H5IntentTarget t = targetList.get(index);
            if (intent.isCanceled()) {
                H5Log.d(TAG, "intent been canceld on intercept!");
                return false;
            }
            if (t.interceptIntent(intent)) {
                return true;
            }
        }

        for (int index = 0; index < size; ++index) {
            if (intent.isCanceled()) {
                H5Log.d(TAG, "intent been canceled on handle!");
                return false;
            }

            H5IntentTarget t = targetList.get(index);
            if (t.handleIntent(intent)) {
                return true;
            }
        }

        H5Log.d(TAG, "[" + actionName + "] handled by nobody");
        intent.sendError(H5Intent.Error.NOT_FOUND);
        return false;
    }

}
