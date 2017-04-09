package com.naruto.mobile.framework.service.common;

import android.graphics.Bitmap;

import com.naruto.mobile.base.serviceaop.service.CommonService;

/**
 * 图片缓存服务
 */
public abstract class ImageMemCacheService extends CommonService {
    /**
     * @return 当前大小
     */
    public abstract long getSize();

    /**
     * @return 最大大小
     */
    public abstract long getMaxsize();

    /**
     * @param group 删除组
     */
    public abstract void removeByGroup(String group);

    /**
     * 删除
     *
     * @param key 键
     * @return
     */
    public abstract Bitmap remove(String key);

    /**
     * 获取
     *
     * @param owner 所有者
     * @param key 键
     * @return
     */
    public abstract Bitmap get(String owner, String key);

    /**
     * 存储
     *
     * @param owner 所有者
     * @param group 组
     * @param key 键
     * @param value 图片
     */
    public abstract void put(String owner, String group, String key, Bitmap value);

}
