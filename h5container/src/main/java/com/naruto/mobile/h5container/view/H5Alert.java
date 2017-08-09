
package com.naruto.mobile.h5container.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.os.Build;
import android.text.TextUtils;

import com.naruto.mobile.h5container.util.H5Log;

/**
 */
public class H5Alert implements OnClickListener, OnCancelListener {
    public static final String TAG = "H5Alert";

    public static interface H5AlertListener {
        public void onClick(H5Alert alert, int index);

        public void onCancel(H5Alert alert);
    }

    public static final int INDEX_LEFT = 0;
    public static final int INDEX_MIDDLE = 1;
    public static final int INDEX_RIGHT = 2;
    public static final int INDEX_CANCEL = 3;

    private Activity activity;
    private String title;
    private String message;
    private String button1Label;
    private String button2Label;
    private String button3Label;
    private boolean cancelable = true;
    private H5AlertListener listener;
    private AlertDialog dialog;

    public H5Alert(Activity activity) {
        this.activity = activity;
    }

    public H5Alert title(String title) {
        this.title = title;
        return this;
    }

    public H5Alert message(String message) {
        this.message = message;
        return this;
    }

    public H5Alert cancelable(boolean cancelable) {
        this.cancelable = cancelable;
        return this;
    }

    public H5Alert buttons(String[] labels) {
        if (labels == null || labels.length == 0) {
            H5Log.w(TAG, "no buttons set.");
            return this;
        }

        switch (labels.length) {
            case 3:
                button3Label = labels[2];
            case 2:
                button2Label = labels[1];
            case 1:
                button1Label = labels[0];
                break;
        }
        return this;
    }

    public H5Alert listener(H5AlertListener listener) {
        this.listener = listener;
        return this;
    }

    public H5Alert show() {
        if (activity == null || activity.isFinishing()) {
            H5Log.d(TAG, "activity is finishing");
            dialog = null;
            return this;
        }

        if (TextUtils.isEmpty(title) && TextUtils.isEmpty(message)) {
            H5Log.w(TAG, "empty title and message");
            return this;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }
        if (!TextUtils.isEmpty(message)) {
            builder.setMessage(message);
        }
        if (!TextUtils.isEmpty(button1Label)) {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                builder.setPositiveButton(button1Label, this);
            } else {
                builder.setNegativeButton(button1Label, this);
            }
        }
        if (!TextUtils.isEmpty(button2Label)) {
            builder.setNeutralButton(button2Label, this);
        }
        if (!TextUtils.isEmpty(button3Label)) {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                builder.setNegativeButton(button3Label, this);
            } else {
                builder.setPositiveButton(button3Label, this);
            }
        }

        builder.setCancelable(cancelable);
        builder.setOnCancelListener(this);
        dialog = builder.show();
        return this;
    }

    public void dismiss() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        int index = INDEX_RIGHT;
        if (which == DialogInterface.BUTTON_NEUTRAL) {
            index = INDEX_MIDDLE;
        } else if ((which == DialogInterface.BUTTON_POSITIVE && Build.VERSION.SDK_INT <= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
                || (which == DialogInterface.BUTTON_NEGATIVE && (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH))) {
            index = INDEX_LEFT;
        }

        dismiss();

        if (listener != null) {
            listener.onClick(this, index);
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        if (listener != null) {
            listener.onCancel(this);
        }
    }
}
