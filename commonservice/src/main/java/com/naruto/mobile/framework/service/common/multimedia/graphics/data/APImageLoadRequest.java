package com.naruto.mobile.framework.service.common.multimedia.graphics.data;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.naruto.mobile.framework.service.common.multimedia.api.data.BaseLoadReq;
import com.naruto.mobile.framework.service.common.multimedia.graphics.APDisplayer;
import com.naruto.mobile.framework.service.common.multimedia.graphics.APImageDownLoadCallback;
import com.naruto.mobile.framework.service.common.multimedia.graphics.ImageWorkerPlugin;

/**
 */
public class APImageLoadRequest extends BaseLoadReq {
    public final static int TYPE_NORMAL = 0;
    public final static int TYPE_DJANGO = 1;
    public final static int TYPE_DATA = 2;
    public final static int TYPE_ORIGINAL= 3;
    //从本地asset中加载内置图片
    public final static int TYPE_ASSET = 4;

    public final static int DEFAULT_LOAD_W = 240;
    public final static int DEFAULT_LOAD_H = 240;
    //这是个特殊值，区分是否原图
    public final static int ORIGINAL_WH = Integer.MAX_VALUE;

    public byte[] data;
    public int width = DEFAULT_LOAD_W;
    public int height = DEFAULT_LOAD_H;
    public int loadType;
    public APImageDownLoadCallback callback;
	public Bitmap defaultBitmap;
    public Drawable defaultDrawable;
    public ImageView imageView;
    public ImageWorkerPlugin plugin;
    public APDisplayer displayer;
    //是否将加载的图片用于设置背景图片
    public boolean isBackground;

    public Size imageSize;
}
