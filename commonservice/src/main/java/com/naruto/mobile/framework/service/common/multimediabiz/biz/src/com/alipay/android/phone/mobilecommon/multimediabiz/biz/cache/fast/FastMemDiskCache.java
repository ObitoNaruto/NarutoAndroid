package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.fast;

import android.graphics.Bitmap;
import android.text.TextUtils;

import java.util.Collection;

import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.ImageCacheContext;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.memory.MemoryCache;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.ImageUtils;

/**
 * Created by jinmin on 15/6/9.
 */
public abstract class FastMemDiskCache implements MemoryCache<Bitmap> {

    private BitmapNativeCache mMappedDiskCache;

    public FastMemDiskCache(int maxSize) {
        mMappedDiskCache = BitmapNativeCache.open(maxSize);
    }

    @Override
    public boolean put(String key, Bitmap value) {
        mMappedDiskCache.putBitmap(key,value);
        return true;
    }

    @Override
    public Bitmap get(String key) {
        return get(key, null);
    }

    @Override
    public Bitmap get(String key,Bitmap pre) {
        Bitmap bitmap = mMappedDiskCache.getBitmap(key,pre);
        if (!ImageUtils.checkBitmap(bitmap)) {
            String refKey = ImageCacheContext.get().getRefCacheKey(key);
            if (!TextUtils.isEmpty(refKey)) {
                bitmap = mMappedDiskCache.getBitmap(refKey, pre);
            }
        }
        return bitmap;
    }

    @Override
    public Bitmap remove(String key) {
        return null;
    }

    @Override
    public void trimToSize(int maxSize) {
        if(mMappedDiskCache != null){
            mMappedDiskCache.trimToSize(maxSize);
        }
    }

    @Override
    public long getMemoryMaxSize() {
        return 0;
    }

    @Override
    public Collection<String> keys() {
        return null;
    }

    @Override
    public void clear() {
        mMappedDiskCache.cleanup();
    }
}
