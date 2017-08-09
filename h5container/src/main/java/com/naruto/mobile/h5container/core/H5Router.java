
package com.naruto.mobile.h5container.core;

import android.text.TextUtils;

import com.naruto.mobile.h5container.api.H5CoreNode;
import com.naruto.mobile.h5container.api.H5Intent;
import com.naruto.mobile.h5container.api.H5Message;
import com.naruto.mobile.h5container.api.H5Page;
import com.naruto.mobile.h5container.api.H5Service;
import com.naruto.mobile.h5container.api.H5Session;
import com.naruto.mobile.h5container.env.H5Container;
import com.naruto.mobile.h5container.util.H5Log;
import com.naruto.mobile.h5container.util.H5Utils;

public class H5Router {

    public static final String TAG = "H5NativeMessager";

    private H5IntentRouter intentRouter;

    public H5Router() {
        intentRouter = new H5IntentRouter();
    }

    public boolean sendIntent(String intentName) {
        if (TextUtils.isEmpty(intentName)) {
            return false;
        }
        H5IntentImpl intent = new H5IntentImpl();
        intent.setAction(intentName);
        return sendIntent(intent);
    }

    public boolean sendIntent(final H5Intent intent) {
        if (!checkMessage(intent)) {
            return false;
        }

        H5Utils.runOnMain(new Runnable() {

            @Override
            public void run() {
                intentRouter.routeMesseger(intent);
            }
        });
        return true;
    }

    private boolean checkMessage(H5Message message) {
        if (message == null) {
            H5Log.w(TAG, "invalid message body!");
            return false;
        }

        H5CoreNode target = message.getTarget();
        while (target == null) {
            H5Service service = H5Container.getService();
            if (service == null) {
                break;
            }
            target = service;

            H5Session session = service.getTopSession();
            if (session == null) {
                break;
            }
            target = session;

            H5Page page = session.getTopPage();
            if (page == null) {
                break;
            }
            target = page;
            break;
        }

        if (target == null) {
            H5Log.w(TAG, "invalid message target!");
            return false;
        }
        message.setTarget(target);
        return true;
    }
}
