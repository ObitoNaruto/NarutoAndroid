package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.text.TextUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.disc.DiskCache;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.disc.impl.BitmapDiskLruCache;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.fast.FastBitmapMemDiskCache;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.memory.MemoryCache;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.AppUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Logger;

/**
 * ImageCacheContext
 * Created by jinmin on 15/5/25.
 */
public class ImageCacheContext implements CacheContext {
    private static final String TAG = "ImageCacheContext";

    private Logger logger = Logger.getLogger(TAG);

    private static final int DEFAULT_MAX_MEM_SIZE = 96 * 1024 * 1024;
    private static final int DEFAULT_MIN_MEM_SIZE = 48 * 1024 * 1024;

    private static ImageCacheContext sInstance;
    private DiskCache<Bitmap> mImageDiskCache;
    private MemoryCache<Bitmap> mImageMemCache;
    private Map<String, Set<String>> mLocalLoadTempCache = new ConcurrentHashMap<String, Set<String>>();

    public static ImageCacheContext get() {
        if (sInstance == null) {
            synchronized (ImageCacheContext.class) {
                if (sInstance == null) {
                    sInstance = new ImageCacheContext();
                }
            }
        }
        return sInstance;
    }

    public static ImageCacheContext getWithoutInit() {
        return sInstance;
    }

    private ImageCacheContext() {
        initImageMemCache();
        initImageDiskCache();
    }

    private void initImageMemCache() {
        if (mImageMemCache == null) {
            //mImageMemCache = new SimpleImageLruCache();
            mImageMemCache = new FastBitmapMemDiskCache(getNativeMemCacheSize());
        }
    }

    private void initImageDiskCache() {
        if (mImageDiskCache == null) {
//            mImageDiskCache = new BitmapSimpleDiskCache(CacheConfig.getImageCacheDir(), new BitmapDiskCacheHandler());
            try {
                mImageDiskCache = new BitmapDiskLruCache(CacheConfig.getImageCacheDir());
            } catch (IOException e) {
                logger.e(e, "initImageDiskCache");
            }
        }
    }

    @Override
    public MemoryCache<Bitmap> getMemCache() {
        if (mImageMemCache == null) {
            synchronized (this) {
                if (mImageMemCache == null) {
                    initImageMemCache();
                }
            }
        }
        return mImageMemCache;
    }

    @Override
    public DiskCache<Bitmap> getDiskCache() {
        if (mImageDiskCache == null) {
            synchronized (this) {
                if (mImageDiskCache == null) {
                    initImageDiskCache();
                }
            }
        }
        return mImageDiskCache;
    }

    @Override
    public void clear() {
        if (mImageMemCache != null) {
            mImageMemCache.clear();
        }
    }

    @Override
    public synchronized void destroy() {
        clear();
        if (mImageMemCache != null) {
            mImageMemCache = null;
        }
        if (mImageDiskCache != null) {
            mImageDiskCache = null;
        }
        sInstance = null;
    }


    private int getNativeMemCacheSize() {
//        Logger.P(TAG, "getNativeMemCacheSize getVmHeapSize: " + AppUtils.getVmHeapSize());
//        Logger.P(TAG, "getNativeMemCacheSize getHeapGrowthLimit: " + AppUtils.getHeapGrowthLimit());
//        Logger.P(TAG, "getNativeMemCacheSize freeMemory: " + Runtime.getRuntime().freeMemory());
//        Logger.P(TAG, "getNativeMemCacheSize totalMemory: " + Runtime.getRuntime().totalMemory());
//        Logger.P(TAG, "getNativeMemCacheSize maxMemory: " + Runtime.getRuntime().maxMemory());
//        ActivityManager am = (ActivityManager) AppUtils.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
//        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
//        am.getMemoryInfo(memoryInfo);
//        Logger.P(TAG, "getNativeMemCacheSize memoryInfo.availMem: " + memoryInfo.availMem + ", " + memoryInfo.totalMem);
        int max = Math.min(AppUtils.getHeapGrowthLimit()*3/8, DEFAULT_MAX_MEM_SIZE);
        int size = Math.max(DEFAULT_MIN_MEM_SIZE, max);
        Logger.P(TAG, "getNativeMemCacheSize size: " + size);
        return size;
    }



    public Set<String> popTempCache(String path) {
        return mLocalLoadTempCache.remove(path);
    }

    /**
     * 获取Cache Key关联的CacheKey
     * @param sourceCacheKey
     * @return      存在则返回关联的CacheKey，否则返回null
     */
    public String getRefCacheKey(String sourceCacheKey) {
        if (getDiskCache() != null) {
            return getDiskCache().getRefCacheKey(sourceCacheKey);
        }
        return null;
    }

    public String getRefPath(String sourcePath) {
        if (getDiskCache() != null) {
            return getDiskCache().getRefPath(sourcePath);
        }
        return null;
    }

    public synchronized void resetDiskCache() {
        resetDiskCache(true);
    }

    public synchronized void resetDiskCache(boolean reInit) {
        if (mImageDiskCache != null) {
            mImageDiskCache.close();
            mImageDiskCache = null;
            if (reInit) initImageDiskCache();
        }
    }
}
