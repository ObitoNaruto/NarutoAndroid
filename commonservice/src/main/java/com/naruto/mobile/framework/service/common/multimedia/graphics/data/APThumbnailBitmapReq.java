package com.naruto.mobile.framework.service.common.multimedia.graphics.data;

/**
 * Created by jinmin on 15/6/20.
 */
public class APThumbnailBitmapReq extends APCacheBitmapReq {
    private APThumbnailBitmapReq(String path, int width, int height) {
        super(path, width, height);
    }

    public APThumbnailBitmapReq(String path) {
        super(path, -1, -1);
    }
}
