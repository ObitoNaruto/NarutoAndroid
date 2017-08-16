package com.naruto.mobile.framework.service.common.multimedia.graphics;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

public abstract class APDefaultDisplayer implements APDisplayer {
    @Override
    public void display(View view, Drawable drawable, String sourcePath) {
        if (drawable instanceof BitmapDrawable) {
            display(view, ((BitmapDrawable) drawable).getBitmap(), sourcePath);
        } else {
            // other type
        }
    }

    public abstract void display(View view, Bitmap bitmap, String sourcePath);
}
