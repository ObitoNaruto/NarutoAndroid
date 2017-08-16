package com.naruto.mobile.framework.service.common.multimedia.graphics.data;


import com.naruto.mobile.framework.service.common.multimedia.graphics.ImageWorkerPlugin;

/**
 * Created by jinmin on 15/5/15.
 */
public class APImageCacheQuery extends APImageQuery {
    public int width;
    public int height;
    public ImageWorkerPlugin plugin;

    public APImageCacheQuery(String path, int width, int height, ImageWorkerPlugin plugin) {
        super(path);
        this.width = width;
        this.height = height;
        this.plugin = plugin;
    }

    public APImageCacheQuery(String path, int width, int height) {
        this(path, width, height, null);
    }

    @Override
    public String toString() {
        return "APImageCacheQuery{" +
                "width=" + width +
                ", height=" + height +
                ", plugin=" + plugin +
                ", path=" + path +
                "}" ;
    }
}
