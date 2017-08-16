package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.disc.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 图片缓存DB 模型
 * Created by jinmin on 15/5/22.
 */
@DatabaseTable(tableName = "tbl_image_cache")
public class ImageCacheModel {

    public static final int IMAGE_TYPE_ORIGINAL = 0;
    public static final int IMAGE_TYPE_BIG = 1;
    public static final int IMAGE_TYPE_THUMB = 2;

    /**
     * 自增长id
     */
    @DatabaseField(generatedId = true)
    public int id;
    /**
     * 图片类型 0:原图 1：大图 2：缩略图
     */
    @DatabaseField(columnName = "image_type", defaultValue = "2")
    public int imageType = IMAGE_TYPE_THUMB;
    /**
     * 输入的路径：本地路径、cloud id、local id
     */
    @DatabaseField(columnName = "src_path", index = true, defaultValue = "", canBeNull = false/*, encryption = true*/)
    public String srcPath;
    /**
     * 缓存的路径
     */
    @DatabaseField(columnName = "path", defaultValue = ""/*, encryption = true*/)
    public String path;
    /**
     * cache的宽
     */
    @DatabaseField(columnName = "width", defaultValue = "-1")
    public int width;
    /**
     * cache的高
     */
    @DatabaseField(columnName = "height", defaultValue = "-1")
    public int height;
    /**
     * cache的像素
     */
    @DatabaseField(columnName = "pixels", defaultValue = "-1")
    public int pixels;
    /**
     * disk cache的key值
     */
    @DatabaseField(columnName = "cache_key", index = true, unique = true, defaultValue = ""/*, encryption = true*/)
    public String cacheKey;
    /**
     * 引用的cacheKey值
     */
    @DatabaseField(columnName = "ref_cache_key", index = true, defaultValue = ""/*, encryption = true*/)
    public String refCacheKey;
    /**
     * 引用的信息id
     */
    @DatabaseField(columnName = "ref_id", canBeNull = true/*, encryption = true*/)
    public int refId;
    /**
     * 插件ID
     */
    @DatabaseField(columnName = "plugin_id", defaultValue = "")
    public String pluginId;
    /**
     * 缩放模式
     */
    @DatabaseField(columnName = "cut_scale_type", defaultValue = "0")
    public int cutScaleType;
    /**
     * 建立的时间
     */
    @DatabaseField(columnName = "create_time", defaultValue = "-1")
    public long createTime;
    /**
     * 最后修改时间
     */
    @DatabaseField(columnName = "last_modify_time", defaultValue = "-1")
    public long lastModifyTime;

    public ImageCacheModel() {

    }

    public ImageCacheModel(int imageType, String srcPath, int width, int height, int cutScaleType, String pluginId,
                            String cacheKey) {
        this.imageType = imageType;
        this.srcPath = srcPath;
        this.width = width;
        this.height = height;
        this.pluginId = pluginId;
        this.cacheKey = cacheKey;
        this.cutScaleType = cutScaleType;

        switch (imageType) {
            case IMAGE_TYPE_ORIGINAL:
                this.pixels = Integer.MAX_VALUE;
                break;
            case IMAGE_TYPE_BIG://大图默认值定为1280*1280
                this.pixels = 1280 * 1280;//APImageUpRequest.DEFAULT_UP_W * APImageUpRequest.DEFAULT_UP_H;
                break;
            case IMAGE_TYPE_THUMB://0，0是大图，大图默认值定为1280*1280
                if (width == 0 && height == 0) {
                    width = height = 1280;
                }
                this.pixels = width * height;
                break;
        }

        this.createTime = System.currentTimeMillis();
        this.lastModifyTime = this.createTime;
    }

    public ImageCacheModel(String srcPath, int width, int height, int cutScaleType, String pluginId, String cacheKey) {
        this(IMAGE_TYPE_THUMB, srcPath, width, height, cutScaleType, pluginId, cacheKey);
    }

    public ImageCacheModel(int imageType, String srcPath, String width, String height, int cutScaleType, String pluginId,
                           String cacheKey) {
        this(imageType, srcPath, getInt(width), getInt(height), cutScaleType, pluginId, cacheKey);
    }

    public ImageCacheModel(String srcPath, String width, String height, String cutScaleType, String pluginId, String cacheKey) {
        this(srcPath, getInt(width), getInt(height), getInt(cutScaleType), pluginId, cacheKey);
    }

    private static int getInt(String input, int defVal) {
        try {
            return Integer.parseInt(input);
        } catch (Exception e) {}
        return defVal;
    }

    private static int getInt(String input) {
        return getInt(input, -1);
    }

    @Override
    public String toString() {
        return "ImageCacheModel{" +
                "id=" + id +
                ", imageType=" + imageType +
                ", srcPath='" + srcPath + '\'' +
                ", path='" + path + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", pixels=" + pixels +
                ", cacheKey='" + cacheKey + '\'' +
                ", refCacheKey='" + refCacheKey + '\'' +
                ", refId=" + refId +
                ", pluginId='" + pluginId + '\'' +
                ", cutScaleType=" + cutScaleType +
                ", createTime=" + createTime +
                ", lastModifyTime=" + lastModifyTime +
                '}';
    }
}
