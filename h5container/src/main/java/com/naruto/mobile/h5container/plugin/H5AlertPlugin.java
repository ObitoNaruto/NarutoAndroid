
package com.naruto.mobile.h5container.plugin;

import android.app.Activity;
import android.content.res.Resources;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.naruto.mobile.h5container.R;
import com.naruto.mobile.h5container.api.H5Intent;
import com.naruto.mobile.h5container.api.H5IntentFilter;
import com.naruto.mobile.h5container.api.H5Page;
import com.naruto.mobile.h5container.api.H5Plugin;
import com.naruto.mobile.h5container.env.H5Environment;
import com.naruto.mobile.h5container.util.H5Log;
import com.naruto.mobile.h5container.util.H5Utils;
import com.naruto.mobile.h5container.view.H5Alert;

/**
 * alert, confirm related dialog widgets
 */
public class H5AlertPlugin implements H5Plugin {

    public static final String TAG = "H5AlertPlugin";
    private H5Alert h5Alert;
    private H5Page h5Page;

    public H5AlertPlugin(H5Page page) {
        this.h5Page = page;
    }

    @Override
    public void onRelease() {
        h5Page = null;
        h5Alert = null;
    }

    @Override
    public void getFilter(H5IntentFilter filter) {
        filter.addAction(SHOW_ALERT);
        filter.addAction(ALERT);
        filter.addAction(CONFIRM);
    }

    @Override
    public boolean interceptIntent(H5Intent intent) {
        return false;
    }

    @Override
    public boolean handleIntent(H5Intent intent) {
        String action = intent.getAction();
        if (SHOW_ALERT.equals(action)) {
            showAlert(intent);
        } else if (ALERT.equals(action)) {
            alert(intent);
        } else if (CONFIRM.equals(action)) {
            confirm(intent);
        }
        return true;
    }

    private void alert(final H5Intent intent) {
        if (intent == null) {
            return;
        }
        JSONObject params = intent.getParam();
        if (params == null) {
            return;
        }

        String title = params.getString("title");
        String message = params.getString("message");
        String button = params.getString("button");

        if (TextUtils.isEmpty(button)) {
            button = H5Environment.getResources().getString(
                    R.string.default_confirm);
        }
        String[] buttons = new String[] {
                button
        };

        H5Alert.H5AlertListener listener = new H5Alert.H5AlertListener() {
            @Override
            public void onClick(H5Alert alert, int index) {
                intent.sendBack(null);
                h5Alert = null;
            }

            @Override
            public void onCancel(H5Alert alert) {
                intent.sendBack(null);
                h5Alert = null;
            }
        };

        if (h5Alert != null) {
            h5Alert.dismiss();
            h5Alert = null;
        }

        Activity activity = (Activity) h5Page.getContext().getContext();
        h5Alert = new H5Alert(activity).cancelable(false).title(title)
                .message(message).buttons(buttons).listener(listener).show();
    }

    private void confirm(final H5Intent intent) {
        if (intent == null) {
            return;
        }
        JSONObject params = intent.getParam();
        if (params == null) {
            return;
        }
        String title = params.getString("title");
        String message = params.getString("message");

        String confirm = params.getString("okButton");
        Resources resources = H5Environment.getResources();
        if (TextUtils.isEmpty(confirm)) {
            confirm = resources.getString(R.string.default_confirm);
        }
        String cancel = params.getString("cancelButton");
        if (TextUtils.isEmpty(cancel)) {
            cancel = resources.getString(R.string.default_cancel);
        }

        String[] buttons = new String[] {
                confirm, cancel
        };
        H5Alert.H5AlertListener listener = new H5Alert.H5AlertListener() {
            @Override
            public void onClick(H5Alert alert, int index) {
                boolean confirmed = (index == H5Alert.INDEX_LEFT);
                JSONObject result = new JSONObject();
                result.put("ok", confirmed);
                intent.sendBack(result);
                h5Alert = null;
            }

            @Override
            public void onCancel(H5Alert alert) {
                JSONObject result = new JSONObject();
                result.put("ok", false);
                intent.sendBack(result);
                h5Alert = null;
            }
        };

        if (h5Alert != null) {
            h5Alert.dismiss();
            h5Alert = null;
        }

        Activity activity = (Activity) h5Page.getContext().getContext();
        h5Alert = new H5Alert(activity).cancelable(false).title(title)
                .message(message).buttons(buttons).listener(listener).show();
    }

    private void showAlert(final H5Intent intent) {
        final JSONObject param = intent.getParam();
        if (param == null) {
            H5Log.e(TAG, "none params");
            return;
        }
        final String title = H5Utils.getString(param, "title", null);
        final String message = H5Utils.getString(param, "message", null);
        String[] buttonLabels = null;
        try {
            JSONArray buttons = H5Utils.getJSONArray(param, "buttons", null);
            if (buttons.size() > 0) {
                buttonLabels = new String[buttons.size()];
                for (int i = 0; i != buttons.size(); i++) {
                    buttonLabels[i] = buttons.getString(i);
                }
            }
        } catch (Exception e) {
            H5Log.e(TAG, e);
        }

        H5Alert.H5AlertListener listener = new H5Alert.H5AlertListener() {
            @Override
            public void onClick(H5Alert alert, int index) {
                alert.dismiss();
                intent.sendBack("index", index);
                h5Alert = null;
            }

            @Override
            public void onCancel(H5Alert alert) {
                intent.sendBack("index", H5Alert.INDEX_CANCEL);
                h5Alert = null;
            }
        };

        if (h5Alert != null) {
            h5Alert.dismiss();
            h5Alert = null;
        }

        Activity activity = (Activity) h5Page.getContext().getContext();
        h5Alert = new H5Alert(activity).cancelable(false).title(title)
                .message(message).buttons(buttonLabels).listener(listener)
                .show();
    }

}
