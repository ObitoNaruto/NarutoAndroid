/**
 * 
 */

package com.naruto.mobile.h5container.view;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.naruto.mobile.h5container.R;
import com.naruto.mobile.h5container.env.H5Environment;

/**
 */
public class H5Toast {

    public static void showToast(Context context, int iconRes, String textRes,
            int duration) {
        showToast(context, iconRes, textRes, Gravity.CENTER, 0, 0, duration);
    }

    public static void showToast(Context context, int iconRes, String textRes,
            int gravity, int xOffset, int yOffset, int duration) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.h5_toast, null);
        ImageView image = (ImageView) view.findViewById(R.id.mini_toast_icon);
        if (0 != iconRes) {
            image.setImageResource(iconRes);
        } else {
            image.setVisibility(View.GONE);
        }
        TextView text = (TextView) view.findViewById(R.id.mini_toast_text);
        text.setText(textRes);
        view.getBackground().setAlpha(192);
        Toast toast = new Toast(context);
        toast.setGravity(gravity, xOffset, yOffset);
        toast.setDuration(duration);
        toast.setView(view);
        toast.show();
    }

    public static void showToast(Context context, String textRes) {
        Toast toast = Toast.makeText(context, textRes, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void showToast(Context context, int resId, int duration) {
        String text = H5Environment.getResources().getString(resId);
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
