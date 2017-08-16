package com.naruto.mobile.framework.service.common.multimedia.graphics.data;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import java.io.InputStream;

/**
 * 可重用的Bitmap的包装
 */
public class ReusableBitmapDrawable extends BitmapDrawable {
    public ReusableBitmapDrawable() {
    }

    public ReusableBitmapDrawable(Resources res) {
        super(res);
    }

    public ReusableBitmapDrawable(Bitmap bitmap) {
        super(bitmap);
    }

    public ReusableBitmapDrawable(Resources res, Bitmap bitmap) {
        super(res, bitmap);
    }

    public ReusableBitmapDrawable(String filepath) {
        super(filepath);
    }

    public ReusableBitmapDrawable(Resources res, String filepath) {
        super(res, filepath);
    }

    public ReusableBitmapDrawable(InputStream is) {
        super(is);
    }

    public ReusableBitmapDrawable(Resources res, InputStream is) {
        super(res, is);
    }
}
