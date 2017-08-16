package com.naruto.mobile.framework.service.common.multimedia.graphics.data;

/**
 * 本地图片缓存查询结果
 * Created by jinmin on 15/5/22.
 */
public class APImageQueryResult<T extends APImageQuery> {
    /**
     * 是否查询到对应缓存
     */
    public boolean success;
    /**
     * 输入的查询参数
     */
    public T query;
    /**
     * 查询到的文件路径
     */
    public String path;
    /**
     * 记录的宽
     */
    public int width;
    /**
     * 记录的高
     */
    public int height;

    @Override
    public String toString() {
        return "APImageQueryResult{" +
                "success=" + success +
                ", query=" + query +
                ", path='" + path + '\'' +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
