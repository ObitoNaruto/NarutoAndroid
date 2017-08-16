package com.naruto.mobile.framework.service.common.multimedia.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;

/**
 * 多媒体工具类
 * Created by jinmin on 15/5/29.
 */
public class MultimediaUtils {

    private static float[] DENSITYs = {1.5f, 2.0f, 2.5f, 3.0f};

    /**
     * 把Dp值转成预设像素值
     * @param context       Android上下文
     * @param dp            转换的dp值
     * @return              转换后的像素值
     */
    public static int preferZoomPx(Context context, float dp) {
        float density = getPreferDensity(context);
        return (int) (dp * density);
    }

    private static float getPreferDensity(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float density = displayMetrics.density;
        for (int i = DENSITYs.length-1; i >= 0; i--) {
            if (density >= DENSITYs[i]) {
                return DENSITYs[i];
            }
        }
        return displayMetrics.density;
    }

}
