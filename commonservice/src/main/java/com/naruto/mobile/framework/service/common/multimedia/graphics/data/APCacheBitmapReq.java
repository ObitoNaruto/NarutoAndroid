package com.naruto.mobile.framework.service.common.multimedia.graphics.data;


import com.naruto.mobile.framework.service.common.multimedia.graphics.ImageWorkerPlugin;

/**
 * Created by jinmin on 15/6/19.
 */
public class APCacheBitmapReq {
    public String path;
    public int width;
    public int height;
    public Size srcSize;
    public CutScaleType cutScaleType = CutScaleType.KEEP_RATIO;
    public ImageWorkerPlugin plugin;
    public boolean loadFromDiskCache = true;
    public APImageMarkRequest imageMarkRequest;

    public APCacheBitmapReq(String path, int width, int height) {
        this.path = path;
        this.width = width;
        this.height = height;
    }

    @Override
    public String toString() {
        return "[path: " + path
                + ", width: " + width
                + ", height: " + height
                + ", srcSize: " + srcSize
                + ", cutScaleType: " + cutScaleType
                + ", plugin: " + plugin
                + ", loadFromDiskCache: " + loadFromDiskCache
                + ", imageMarkRequest: " + imageMarkRequest
                + "]";
    }
}
