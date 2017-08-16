package com.naruto.mobile.framework.service.common.multimedia.graphics;

import android.graphics.drawable.Drawable;
import android.view.View;

public interface APDisplayer {

    /**
     *
     * @param view loadImage传入的view
     * @param drawable 加载到的图片
     * @param sourcePath loadImage传入的路径或者id
     */
    void display(View view, Drawable drawable, String sourcePath);
}
