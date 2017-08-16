package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache;

import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.disc.DiskCache;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.memory.MemoryCache;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.ExceptionUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Logger;

/**
 * 缓存加载器
 * Created by jinmin on 15/4/7.
 */
public class BaseCacheLoader<E> implements CacheLoader<E> {

    protected Logger logger = Logger.getLogger("BaseCacheLoader");

    protected MemoryCache<E> memoryCache;
//    protected DiskCache<E> diskCache;

    private HandlerThread mAsyncHandlerThread;
    private CacheContext mCacheContext;
    protected Handler mAsyncHandler;
    private ConcurrentHashMap<String,String>  savingMap = new ConcurrentHashMap<String, String>();

    public BaseCacheLoader(CacheContext cacheContext) {
        this.mCacheContext = cacheContext;
        this.memoryCache = (MemoryCache<E>) mCacheContext.getMemCache();
//        this.diskCache = (DiskCache<E>) cacheContext.getDiskCache();

        mAsyncHandlerThread = new HandlerThread("AsyncHandlerThread");
        mAsyncHandlerThread.setPriority(Thread.MIN_PRIORITY);
        mAsyncHandlerThread.start();
        mAsyncHandler = new Handler(mAsyncHandlerThread.getLooper());
    }

    public E get(String key) {
        E value = getMemCache(key);
        if (value == null) {
            value = getDiskCache(key);
            if (value != null) {
                putMemCache(key, value);
            }
        }
        //logger.d("get cache, [key: %s]:[value: %s]", key, value);
        return value;
    }

    public E getMemCache(String key){
        E value = null;
        if (memoryCache != null) {
            value = memoryCache.get(key);
            //logger.d("getMemCache, [key: %s]:[value: %s]", key, value);
        }

        //logger.d("getMemCache, [key: %s]:[value: %s]", key, value);
        return value;
    }


    @Override
    public E getMemCache(String key, E pre) {
	 	E value = null;
        if (memoryCache != null) {
            value = memoryCache.get(key, pre);
            //logger.d("getMemCache, [key: %s]:[value: %s]", key, value);
        }

        //logger.d("getMemCache, [key: %s]:[value: %s]", key, value);
        return value;
    }

    @Override
    public E getDiskCache(String key) {
        E value = null;
        if (getDiskCache() != null) {
            value = getDiskCache().get(key);
            //logger.d("load from disk, [key: %s]:[value: %s]", key, value);
        }
        return value;
    }

    public boolean put(String key, E value) {
        boolean ret = putMemCache(key, value);
        ret = putDiskCache(key, value) | ret;
        //logger.d("put to cache, [key: %s]:[value: %s], ret: %s", key, value, ret);
        return ret;
    }

    @Override
    public boolean putMemCache(String key, E value) {
        boolean ret = false;
        if (memoryCache != null) {
            //logger.d("set to memory, [key: %s]:[value: %s]", key, value);
            memoryCache.put(key, value);
            ret = true;
        }
        return ret;
    }

    @Override
    public boolean putDiskCache(final String key, final E value) {
        boolean ret = false;
        //final long start = System.currentTimeMillis();
        if (getDiskCache() != null) {
            //logger.d("save to disk by post key: %s, value: %s", key, value);
            if(!TextUtils.isEmpty(savingMap.putIfAbsent(key,"0"))){
                //Logger.I("BitmapDiskCacheHandler", " putDiskCache exist... cost: " + (System.currentTimeMillis() - start));
                return ret;
            }

            mAsyncHandler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        //long start = System.currentTimeMillis();
                        //logger.d("save to disk start, key: %s, value: %s", key, value);
                        boolean ret = getDiskCache().save(key, value);
                        //long end = System.currentTimeMillis() - start;
                        //logger.d("save to disk end, key: %s, value: %s", key, value);
                        if (!ret) {
                            logger.e("save to disk failed, key: %s, value: %s", key, value);
                        }
                        //logger.d("save to disk finish, (key: %s, value: %s), ret = %s, usedTime: %s", key, value, ret, end);
                    } catch (Exception e) {
                        logger.e(e, "Key: %s, value: %s, save error", key, value);
                        ExceptionUtils.checkAndResetDiskCache(e);
                    } finally {
                        savingMap.remove(key);
                        //mAsyncHandler.removeCallbacks(this);
                    }
                    //Logger.I("BitmapDiskCacheHandler", " putDiskCache cost: " + (System.currentTimeMillis() - start));
                }
            });
            ret = true;
        }
        return ret;
    }

    @Override
    public boolean putDiskCache(final String key, final byte[] value) {
        if (getDiskCache() != null) {
            boolean ret = false;
            if(!TextUtils.isEmpty(savingMap.putIfAbsent(key,"0"))){
                //Logger.I("BitmapDiskCacheHandler", " putDiskCache exist... cost: " + (System.currentTimeMillis() - start));
                return ret;
            }
            mAsyncHandler.post(new Runnable() {
                @Override
                public void run() {
                    try {
//                        long start = System.currentTimeMillis();
                        boolean ret = getDiskCache().save(key, value);
//                        long end = System.currentTimeMillis()-start;
                        if (!ret) {
                            logger.e("save to disk failed, key: %s, value: %s", key, value);
                        }
                        //logger.d("save to disk finish, (key: %s, value: %s), usedTime: %s, ret: %s", key,
                        //        value == null ? 0 : value.length, end, ret);
                    } catch (Exception e) {
                        logger.e(e, "Key: %s, value: %s, save error", key, value);
                        ExceptionUtils.checkAndResetDiskCache(e);
                    } finally {
                        savingMap.remove(key);
                    }
                }
            });
            return true;
        }
        return false;
    }

    public E rename(String oldKey, String newKey) {
        return renameMemCache(oldKey, newKey);
    }

    @Override
    public E renameMemCache(String oldKey, String newKey) {
        if (oldKey == null || newKey == null) {
            return null;
        }
        E value = null;
        if (memoryCache != null) {
            value = memoryCache.remove(oldKey);
        }
        return value;
    }

    @Override
    public E renameDiskCache(String oldKey, String newKey) {
        return null;
    }

    public E remove(String key) {
        E value = removeMemCache(key);
        removeDiskCache(key);
        return value;
    }

    @Override
    public E removeMemCache(String key) {
        if (memoryCache != null) {
            return memoryCache.remove(key);
        }
        return null;
    }

    @Override
    public E removeDiskCache(String key) {
        if (getDiskCache() != null) {
            getDiskCache().remove(key);
        }
        return null;
    }

    @Override
    public boolean copy(String srcKey, String dstKey) {
        boolean ret = false;
        ret = copyMemCache(srcKey, dstKey);
        ret = copyDiskCache(srcKey, dstKey) | ret;
        return ret;
    }

    @Override
    public boolean copyMemCache(String srcKey, String dstKey) {
        if (memoryCache != null) {
            E srcValue = memoryCache.get(srcKey);
            if (srcValue != null) {
                memoryCache.put(dstKey, srcValue);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean copyDiskCache(String srcKey, String dstKey) {
        if (getDiskCache() != null) {
            try {
                getDiskCache().copy(srcKey, dstKey);
                return true;
            } catch (IOException e) {
                logger.e(e, "copyDiskCache error, srcKey: %s, dstKey: %s", srcKey, dstKey);
            }
        }
        return false;
    }
	
 	@Override
    public void trimToSize(int maxSize) {
        if (memoryCache != null) {
            memoryCache.trimToSize(maxSize);
        }
    }

    @Override
    public long getMemoryMaxSize(){
        if (memoryCache != null) {
            return memoryCache.getMemoryMaxSize();
        }

        return 0;
    }

    public void clear() {
        if (memoryCache != null) {
            memoryCache.clear();
        }
        if (getDiskCache() != null) {
            getDiskCache().clear();
        }
    }

    @Override
    public DiskCache<E> getDiskCache() {
        return (DiskCache<E>) mCacheContext.getDiskCache();
    }

    @Override
    public MemoryCache<E> getMemoryCache() {
        return memoryCache;
    }

    protected boolean isInQueue(String key) {
        return savingMap.containsKey(key);
    }
}
