package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.memory;

import java.util.Collection;

/**
 * Interface for memory cache
 * Created by jinmin on 15/4/7.
 */
public interface MemoryCache<E> {

    boolean put(String key, E value);

    /** Returns value by key. If there is no value for key then null will be returned. */
    E get(String key);

    /** Removes item by key */
    E remove(String key);

    E get(String key, E pre);

    /** Remove the eldest entries until the total of remaining entries is at or below the requested size. */
    void trimToSize(int maxSize);

    long getMemoryMaxSize();

    /** Returns all keys of cache */
    Collection<String> keys();

    /** Remove all items from cache */
    void clear();
}
