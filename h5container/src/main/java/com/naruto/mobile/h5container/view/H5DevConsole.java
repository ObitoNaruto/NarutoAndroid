
package com.naruto.mobile.h5container.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import com.naruto.mobile.h5container.R;
import com.naruto.mobile.h5container.util.H5Log;
import com.naruto.mobile.h5container.util.H5Log.LogListener;
import com.naruto.mobile.h5container.util.H5Utils;

/**
 */
public class H5DevConsole implements OnClickListener, LogListener {
    public static final String TAG = "H5DevConsole";
    PopupWindow popupWindow = null;
    TextView consoleLogText = null;
    Button hideConsoleBtn = null;
    Button cleanConsoleBtn = null;
    Button launchBrowserBtn = null;
    ScrollView logScrollView = null;
    String url = null;
    Button preloadButton = null;
    private ViewGroup contentView = null;
    private Context context;

    public H5DevConsole(final Context context, final String url) {
        super();
        this.context = context;
        this.url = url;
    }

    public void show() {
        if (popupWindow == null) {
            contentView = (ViewGroup) LayoutInflater.from(context).inflate(
                    R.layout.h5_console_layout, null, false);
            popupWindow = new PopupWindow(contentView,
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, false);
            popupWindow.setFocusable(false);
            popupWindow.setTouchable(true);
            popupWindow.setOutsideTouchable(true);
            consoleLogText = (TextView) contentView
                    .findViewById(R.id.h5_log_text);
            hideConsoleBtn = (Button) contentView.findViewById(R.id.h5_dw_hide);
            launchBrowserBtn = (Button) contentView
                    .findViewById(R.id.h5_dw_browser);
            cleanConsoleBtn = (Button) contentView
                    .findViewById(R.id.h5_dw_clean);
            preloadButton = (Button) contentView
                    .findViewById(R.id.h5_dw_preload);

            logScrollView = (ScrollView) contentView
                    .findViewById(R.id.h5_log_scroll);

            hideConsoleBtn.setOnClickListener(this);
            cleanConsoleBtn.setOnClickListener(this);
            launchBrowserBtn.setOnClickListener(this);
            preloadButton.setOnClickListener(this);
        }

        if (!popupWindow.isShowing()) {
            popupWindow.showAtLocation(contentView, Gravity.TOP, 0, 0);
            H5Log.setListener(this);
        }
    }

    public void showLog(final String tag, final String log) {
        H5Utils.runOnMain(new Runnable() {

            public void run() {
                showLogText(tag, log);
            }
        });
    }

    private void showLogText(String tag, String log) {
        if (consoleLogText == null) {
            return;
        }
        final String overallLog = tag + ": " + log + "\n"
                + consoleLogText.getText() + "\n";
        consoleLogText.setText(overallLog);
    }

    @Override
    public void onClick(View view) {

        if (view.equals(hideConsoleBtn)) {
            popupWindow.dismiss();
            H5Log.setListener(null);
        } else if (view.equals(launchBrowserBtn)) {

            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            try {
                Uri uri = Uri.parse(url);
                intent.setData(uri);
                view.getContext().startActivity(intent);
            } catch (Exception e) {

            }

        } else if (view.equals(cleanConsoleBtn)) {
            consoleLogText.setText("");
        } else if (view.equals(preloadButton)) {

        }
    }

    @Override
    public void onLog(String tag, String log) {
        showLog(tag, log);
    }

}
