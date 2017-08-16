package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import java.io.ByteArrayOutputStream;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.memory.MemoryCache;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.memory.impl.SoftImageLruCache;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.io.IOUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.AppUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Bitmaps;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.ImageUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Logger;

/**
 * 图片 缓存加载器
 * Created by jinmin on 15/4/8.
 */
public class BitmapCacheLoader extends BaseCacheLoader<Bitmap> {
    private static final String TAG = "BitmapCacheLoader";
    private static final int DEFAULT_LARGE_CACHE_MEM_SIZE = 1024 * 1024;
    private static final int LARGE_CACHE_MEM_SIZE = calcLargeCacheMemorySize();
    private static final int MAX_CACHE_MEM_SIZE = 100 * 1024 * 1024;
    private final static int LARGE_MEM_CACHE_SIZE = 10;

    private MemoryCache<Bitmap> mLargeImageCache;

    private Map<String, WeakReference<Bitmap>> mDiskCacheWritingQueue = new ConcurrentHashMap<String, WeakReference<Bitmap>>();


    public BitmapCacheLoader(CacheContext cacheContext) {
        super(cacheContext);
        mLargeImageCache = new SoftImageLruCache(LARGE_MEM_CACHE_SIZE);
    }

    public BitmapCacheLoader() {
        this(ImageCacheContext.get());
    }

    @Override
    public boolean putMemCache(String key, Bitmap value) {
        if (!ImageUtils.checkBitmap(value)) {
            return false;
        }
        if (isLargeBitmap(value)) {
            return mLargeImageCache.put(key, value);
//            return super.putMemCache(key, value);
        } else if (getSize(value) < LARGE_CACHE_MEM_SIZE) {
            return super.putMemCache(key, value);
        }
        return false;
    }

    @Override
    public Bitmap getMemCache(String key) {
        Bitmap val = super.getMemCache(key);
        if (val == null) {
            val = mLargeImageCache.get(key);
        }
        return val;
    }

    @Override
    public Bitmap getMemCache(String key, Bitmap pre) {
        Bitmap val = super.getMemCache(key, pre);//内存存储
        if (val == null) {
            val = mLargeImageCache.get(key, pre);//sd卡存储
        }
        return val;
    }


    @Override
    public Bitmap removeMemCache(String key) {
        Bitmap bitmap = super.removeMemCache(key);
        if (bitmap == null) {
            bitmap = mLargeImageCache.remove(key);
        }
        return bitmap;
    }

    @Override
    public boolean putDiskCache(String key, Bitmap value) {
        boolean ret = false;
        if (!TextUtils.isEmpty(key) && value != null && !value.isRecycled()) {
            if (isInQueue(key)) {
                logger.p("putDiskCache inQueue: " + key);
                return false;
            }
//            Bitmap copy = null;
            ByteArrayOutputStream baos = null;
            //long start = System.currentTimeMillis();
            try {
//                copy = Bitmap.createBitmap(value.getWidth(), value.getHeight(), value.getConfig());
//                Bitmaps.copyBitmap(copy, value);
//                mDiskCacheWritingQueue.put(key, new WeakReference<Bitmap>(copy));
                baos = new ByteArrayOutputStream();
                if (value.hasAlpha()) {
                    value.compress(Bitmap.CompressFormat.PNG, 100, baos);
                } else {
                    value.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                }
                ret = super.putDiskCache(key, baos.toByteArray());
            } catch (Exception e) {
                logger.e(e, "putDiskCache exception");
                //AppUtils.causeGC();
            } finally {
//                if (!ret && copy != null && !copy.isRecycled()) {
//                    copy.recycle();
//                    copy = null;
//                }
                IOUtils.closeQuietly(baos);
                //logger.d("compress used: " + (System.currentTimeMillis()-start));
            }
        }
        return ret;
    }

    private int getSize(Bitmap value) {
        return value != null ? value.getRowBytes() * value.getHeight() : Integer.MAX_VALUE;
    }

    private boolean isLargeBitmap(Bitmap value) {
        int size = getSize(value);
        return size >= LARGE_CACHE_MEM_SIZE && size < MAX_CACHE_MEM_SIZE;
    }

    //这里的策略是优先释放大图，大图释放了还不够就释放小图的内存缓存
    public void releaseImageMem(long size,boolean bForce){
//        long freeMem = Runtime.getRuntime().freeMemory();
//
//        Log.d(TAG,"releaseImageMem size="+size+";freeMem="+freeMem+";bForce="+bForce);
//        //这里多预留1M
//        size += LARGE_CACHE_MEM_SIZE;
//
//        if((size <= freeMem || size < 2*LARGE_CACHE_MEM_SIZE) && (!bForce)){
//            return;
//        }

//        int dest = (int)(LARGE_MEM_CACHE_SIZE - size);
//        Log.d(TAG,"releaseImageMem size="+dest+";LARGE_MEM_CACHE_SIZE="+LARGE_MEM_CACHE_SIZE+";freeMem="+freeMem+";size="+size);
//
//        if(bForce){
//            mLargeImageCache.clear();
            //memoryCache.clear();
//            return;
//        }
//
//        if(LARGE_MEM_CACHE_SIZE <= size){
//            mLargeImageCache.clear();
//            releaseNormalMem(size - LARGE_MEM_CACHE_SIZE);
//        }else{
//            mLargeImageCache.trimToSize(dest);
//        }
        //mLargeImageCache.clear();
//        releaseNormalMem(size);
    }

    private void releaseNormalMem(long size){
        int dest = (int)(getMemoryMaxSize() - size);
        memoryCache.trimToSize(dest);
    }

    public void trimToSize(int size){
        memoryCache.trimToSize(size);
    }

    public Bitmap loadCacheBitmap(String cacheKey, boolean loadFromDiskCache) {
        //先从内存缓存，获取
        Bitmap bitmap = getMemCache(cacheKey);
        //内存没有，从DiskCache 写队列获取
        if (bitmap == null || bitmap.isRecycled()) {
            bitmap = getFromDiskWaitingQueue(cacheKey);
        }
        if (loadFromDiskCache) {
            //待写队列也没有，从文件缓存获取
            if (bitmap == null || bitmap.isRecycled()) {
                bitmap = getDiskCache(cacheKey);
            }
        }
        return bitmap;
    }

    private Bitmap getFromDiskWaitingQueue(String cacheKey) {
        WeakReference<Bitmap> weakReference = mDiskCacheWritingQueue.get(cacheKey);
        if (weakReference != null) {
            Bitmap bitmap = weakReference.get();
            if (bitmap != null && !bitmap.isRecycled()) {
                Bitmap copy = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
                try {
                    Bitmaps.copyBitmap(copy, bitmap);
                    return copy;
                } catch (Exception e) {
                    logger.e(e, "getFromDiskWaitingQueue bitmap maybe recycle!!");
                }
            } else if (bitmap != null) {
                mDiskCacheWritingQueue.remove(cacheKey);
            }
        }
        return null;
    }

    private static int calcLargeCacheMemorySize() {
        int size = DEFAULT_LARGE_CACHE_MEM_SIZE;
        Resources res = AppUtils.getResources();
        if (res != null) {
            DisplayMetrics metrics = res.getDisplayMetrics();
            if (metrics.densityDpi > DisplayMetrics.DENSITY_400) {
                size *= 1.2;
            }
        }
        Logger.P(TAG, "calcLargeCacheMemorySize size: " + size);
        return size;
    }
}
