
package com.naruto.mobile.h5container.plugin;

import android.content.Context;
import android.text.ClipboardManager;

import com.alibaba.fastjson.JSONObject;
import com.naruto.mobile.h5container.api.H5Intent;
import com.naruto.mobile.h5container.api.H5IntentFilter;
import com.naruto.mobile.h5container.api.H5Plugin;
import com.naruto.mobile.h5container.env.H5Environment;
import com.naruto.mobile.h5container.util.H5Utils;

@SuppressWarnings("deprecation")
public class H5ClipboardPlugin implements H5Plugin {

    @Override
    public void getFilter(H5IntentFilter filter) {
        filter.addAction(SET_CLIPBOARD);
        filter.addAction(GET_CLIPBOARD);
    }

    @Override
    public void onRelease() {

    }

    @Override
    public boolean interceptIntent(H5Intent intent) {
        return false;
    }

    @Override
    public boolean handleIntent(H5Intent intent) {
        String action = intent.getAction();
        if (SET_CLIPBOARD.equals(action)) {
            setClipboard(intent);
        } else if (GET_CLIPBOARD.equals(action)) {
            getClipboard(intent);
        }
        return true;
    }

    public void setClipboard(H5Intent intent) {
        JSONObject callParam = intent.getParam();
        if (callParam == null) {
            return;
        }
        String text = H5Utils.getString(callParam, "text");

        Context context = H5Environment.getContext();
        ClipboardManager clipboard = (ClipboardManager) context
                .getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setText(text);
    }

    public void getClipboard(H5Intent intent) {
        Context context = H5Environment.getContext();
        ClipboardManager clipboard = (ClipboardManager) context
                .getSystemService(Context.CLIPBOARD_SERVICE);
        CharSequence cs = clipboard.getText();
        String text = null;
        if (cs != null) {
            text = cs.toString();
        } else {
            text = "";
        }
        JSONObject data = new JSONObject();
        data.put("text", text);
        intent.sendBack(data);
    }

}
