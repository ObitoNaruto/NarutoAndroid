package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.memory.impl;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Log;

import java.util.Collection;

import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.memory.MemoryCache;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Logger;

/**
 * Created by jinmin on 15/5/6.
 */
public class SimpleImageLruCache implements MemoryCache<Bitmap> {

    private static final String TAG = SimpleImageLruCache.class.getSimpleName();

    private LruCache<String, Bitmap> mLruCache;

    public SimpleImageLruCache() {
        this(Runtime.getRuntime().maxMemory() / 8);
    }

    private long mMaxSize = 0;

    public SimpleImageLruCache(long maxSize) {
        mMaxSize = maxSize;
        mLruCache = new LruCache<String, Bitmap>((int)maxSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                if (value != null) {
                    int size = value.getRowBytes() * value.getHeight();
                    Logger.D(TAG, "sizeOf=" + value.getWidth() + ";" + value.getHeight() + ";" + size);
                    return size;
                }
                return super.sizeOf(key, value);
            }
        };
    }

    @Override
    public boolean put(String key, Bitmap value) {
        mLruCache.put(key, value);
        return true;
    }

    @Override
    public Bitmap get(String key) {
        return mLruCache.get(key);
    }

    @Override
    public Bitmap get(String key, Bitmap pre) {
        return mLruCache.get(key);
    }

    @Override
    public Bitmap remove(String key) {
        return mLruCache.remove(key);
    }

    @Override
    public void trimToSize(int maxSize) {
        if (maxSize < 0) {
            mLruCache.evictAll();
        } else {
            mLruCache.trimToSize(maxSize);
        }
    }

    @Override
    public long getMemoryMaxSize() {
        return mMaxSize;
    }

    @Override
    public Collection<String> keys() {
        return mLruCache.snapshot().keySet();
    }

    @Override
    public void clear() {
        mLruCache.evictAll();
    }
}
