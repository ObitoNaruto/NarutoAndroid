package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.memory.impl;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import java.lang.ref.SoftReference;
import java.util.Collection;

import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.memory.MemoryCache;

/**
 * 软引用BitmapLruCache，方便回收
 * Created by jinmin on 15/6/3.
 */
public class SoftImageLruCache implements MemoryCache<Bitmap> {
    private LruCache<String, SoftReference<Bitmap>> mSoftLruCache;

    /**
     * 构造函数
     * @param maxSize       最大存储张数
     */
    public SoftImageLruCache(int maxSize) {
        mSoftLruCache = new LruCache<String, SoftReference<Bitmap>>(maxSize);
    }

    @Override
    public boolean put(String key, Bitmap value) {
        if (value != null && !value.isRecycled()) {
            mSoftLruCache.put(key, create(value));
            return true;
        }
        return false;
    }

    @Override
    public Bitmap get(String key) {
        SoftReference<Bitmap> reference = mSoftLruCache.get(key);
        return reference == null ? null : reference.get();
    }

    @Override
    public Bitmap remove(String key) {
        SoftReference<Bitmap> reference = mSoftLruCache.remove(key);
        return reference == null ? null : reference.get();
    }

    @Override
    public void trimToSize(int maxSize) {
        mSoftLruCache.trimToSize(maxSize);
    }

    @Override
    public long getMemoryMaxSize() {
        return 0;
    }

    @Override
    public Collection<String> keys() {
        return mSoftLruCache.snapshot().keySet();
    }

    @Override
    public void clear() {
        mSoftLruCache.evictAll();
    }

    @Override
    public Bitmap get(String key, Bitmap pre) {
        return get(key);
    }

    private SoftReference<Bitmap> create(Bitmap bitmap) {
        return new SoftReference<Bitmap>(bitmap);
    }
}
