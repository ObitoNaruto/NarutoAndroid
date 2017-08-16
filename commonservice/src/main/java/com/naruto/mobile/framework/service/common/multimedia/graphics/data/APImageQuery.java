package com.naruto.mobile.framework.service.common.multimedia.graphics.data;

/**
 * 本地图片查询
 * Created by jinmin on 15/5/15.
 */
public abstract class APImageQuery {
    public String path;
    public CutScaleType cutScaleType;

    public APImageQuery(String path) {
        this(path, CutScaleType.KEEP_RATIO);
    }

    public APImageQuery(String path, CutScaleType cutScaleType) {
        this.path = path;
        this.cutScaleType = cutScaleType;
    }

    @Override
    public String toString() {
        return "APImageQuery{" +
                "path='" + path + '\'' +
                '}';
    }
}
