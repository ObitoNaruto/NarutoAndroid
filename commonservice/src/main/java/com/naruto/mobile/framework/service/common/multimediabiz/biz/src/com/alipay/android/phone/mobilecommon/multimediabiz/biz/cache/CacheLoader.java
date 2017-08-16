package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache;

import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.disc.DiskCache;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.memory.MemoryCache;

/**
 * 缓存加载接口
 * Created by jinmin on 15/4/22.
 */
public interface CacheLoader<E> {
    /**
     * 获取一个缓存对象
     * @param key       Key
     * @return          对应的缓存对象
     */
    E get(String key);
    /**
     * 获取一个内存缓存对象
     * @param key       Key
     * @return          对应的缓存对象
     */
    E getMemCache(String key);
    /**
     * 获取一个磁盘缓存对象
     * @param key       Key
     * @return          对应的缓存对象
     */
    E getDiskCache(String key);

    /**
     * 获取一个磁盘缓存对象
     * @param key       Key
	 * @param pre       复用对象
     * @return          对应的缓存对象
     */
    E getMemCache(String key, E pre);

    /**
     * 更新或添加
     * @param key       Key
     * @param value     value
     * @return          操作是否成功
     */
    boolean put(String key, E value);

    boolean putMemCache(String key, E value);

    boolean putDiskCache(String key, E value);

    boolean putDiskCache(String key, byte[] value);

    /**
     * 缓存重命名
     * @param oldKey    old
     * @param newKey    new
     * @return          被rename的缓存对象
     */
    E rename(String oldKey, String newKey);

    E renameMemCache(String oldKey, String newKey);

    E renameDiskCache(String oldKey, String newKey);

    /**
     * 删除缓存
     * @param key       删除缓存
     * @return          此key对应的缓存对象
     */
    E remove(String key);

    E removeMemCache(String key);

    E removeDiskCache(String key);

    boolean copy(String srcKey, String dstKey);

    boolean copyMemCache(String srcKey, String dstKey);

    boolean copyDiskCache(String srcKey, String dstKey);
	
    void trimToSize(int maxSize);

    long getMemoryMaxSize();

    /**
     * 清空所有缓存
     */
    void clear();

    DiskCache<E> getDiskCache();

    MemoryCache<E> getMemoryCache();
}
