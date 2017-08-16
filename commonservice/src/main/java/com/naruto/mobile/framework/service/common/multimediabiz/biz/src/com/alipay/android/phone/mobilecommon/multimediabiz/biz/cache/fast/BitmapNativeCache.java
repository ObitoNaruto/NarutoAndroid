package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.fast;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.util.LruCache;
import android.util.Log;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.DeviceWrapper;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Logger;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.ReflectUtils;

/**
 * MemoryCache第三版本，使用JNI-C层管理内存，将bitmap的pixels数据存储在c层，Java层堆中存放Key和索引内容，通过LruCache自动淘汰
 *
 * Created by zhefu.wyq@alipay.com on 6/10/15.
 */
public class BitmapNativeCache {
    static {
        System.loadLibrary("AlipayBitmapNative");
    }

    private static final String TAG = "BitmapNativeCache";
    private Logger logger = Logger.getLogger(TAG);
    private LruCache<String, BitmapInfo> lruCache;

    private Set<SoftReference<Bitmap>> reusableSet = null;

    private ReentrantLock[] locks;
    private static final int MAX_LOCKS = 64;

    /**
     * 创建一个存放在C层的Bitmap Cache
     *
     * @param byteCount Cache最大内存占用，单位byte
     */
    public static BitmapNativeCache open(int byteCount) {
        if (byteCount <= 0)
            throw new IllegalArgumentException("byteCount <= 0");

        return new BitmapNativeCache(byteCount);
    }

    public void close() throws IOException {
        cleanup();
    }

    private BitmapNativeCache(int byteCount) {
        lruCache = new LruCache<String, BitmapInfo>(byteCount) {
            @Override
            protected int sizeOf(String key, BitmapInfo value) {
                return value.dataSize;
            }

            @Override
            protected void entryRemoved(boolean evicted, String key, BitmapInfo oldValue, BitmapInfo newValue) {
                logger.p("entryRemoved, evicted: " + evicted + ", key: " + key +", oldValue: " + oldValue + ", newValue: " + newValue);
                if (newValue == null || (oldValue != null && oldValue.pointer != newValue.pointer)) {
                    BitmapNativeCache.this.remove(oldValue);
                }
            }
        };

        if (Build.VERSION.SDK_INT >= 11) { // HONEYCOMB
            reusableSet = Collections.synchronizedSet(new HashSet<SoftReference<Bitmap>>());
        }

        locks = new ReentrantLock[MAX_LOCKS];
        for (int i = 0; i < MAX_LOCKS; i++) {
            locks[i] = new ReentrantLock();
        }
    }

    public Bitmap getBitmap(String key) {
        return getBitmap(key, null);
    }

    @TargetApi(19)
    public Bitmap getBitmap(String key, Bitmap reuse) {
        if (key == null)
            return null;

        ReentrantLock lock = getLock(key);
        lock.lock();
        Bitmap bitmap = null;
        try {
            BitmapInfo info = lruCache.get(key);
            //Log.d(TAG,"getBitmap info="+info);

            if (info != null && info.valid()) {
                Bitmap bm = getReusableBitmap(reuse, info.width, info.height, info.config);
                if (bm == null) {
                    //Log.d(TAG,"getBitmap bm="+reuse + ", " + (reuse != null ? reuse.getWidth() + "," + reuse.getHeight() + ", " + reuse.getConfig() : "") + "..."+info.width + "," + info.height+ ","+info.config);
                    bm = Bitmap.createBitmap(info.width, info.height, info.config);
                }

                if (Build.VERSION.SDK_INT >= 19) {
                    bm.reconfigure(info.width, info.height, info.config);
                }
                if (bm.isMutable() && (Bitmap.Config.ARGB_8888 == info.config || Bitmap.Config.ARGB_4444 == info.config)) {
                    if (Build.VERSION.SDK_INT >= 12) {
                        bm.setHasAlpha(true);
                    }
                    bm.eraseColor(Color.TRANSPARENT);
                }

                logger.p("getBitmapData start  key: " + key + ", info.pointer: " + info.pointer);
                try {
                    if (info.pointer != 0) {
                        getBitmapData(info.pointer, bm);
                        //logger.d("getBitmapData ret: "+ ret+"  key: " + key + ", info.pointer: " + info.pointer + ", bitmap: " + bm + ", id: " + bm.getGenerationId());
                        bitmap = bm;
                    }
                } catch (Exception e) {
                    //logger.d("getBitmapData exception  key: " + key + ", info.pointer: " + info.pointer + ", e:" + e);
                    logger.e(e, "getBitmapData exception");
                }
                logger.p("getBitmapData finish key: " + key + ", info.pointer: " + info.pointer);
            }
        } finally {
            lock.unlock();
        }

        return bitmap;
    }

    public void putBitmap(String key, Bitmap bitmap) {
        if (key == null || bitmap == null)
            return;

        ReentrantLock lock = getLock(key);
        lock.lock();
        try {
            BitmapInfo info = lruCache.get(key);
            if (info != null && info.exist(bitmap)) // already in memory cache
                return;

            long pointer = 0;
            logger.p("setBitmapData start  key: " + key + ", pointer: " + pointer);
            try {
                pointer = setBitmapData(bitmap);
            } catch (Exception e) {
                logger.e(e, "setBitmapData exception");
            }

            logger.p("setBitmapData finish key: " + key + ", pointer: " + pointer);

            if (pointer != 0) {
                lruCache.put(key, new BitmapInfo(pointer, bitmap));
            }
        } finally {
            lock.unlock();
        }
    }

    public int getTotalByteCount() {
        return lruCache.size();
    }

    public void cleanup() {
        lruCache.evictAll();
    }

    public void trimToSize(int size){
        if(lruCache != null){
            //Log.d("elvis","BitmapNativeCache trimToSize size="+size);
//            lruCache.trimToSize(size);
            Method trimToSize = ReflectUtils.getMethod(LruCache.class, "trimToSize", int.class);
            ReflectUtils.invoke(lruCache, trimToSize, size);
        }
    }

    // ------------------------------------------------------------------------

    private synchronized void remove(BitmapInfo data) {
        logger.p("free pointer start : " + data.pointer);
        free(data.pointer);
        data.pointer = 0;
        logger.p("free pointer finish : " + data.pointer);
    }

    private Bitmap getReusableBitmap(Bitmap old, int width, int height, Bitmap.Config config) {
        if (!DeviceWrapper.hasBitmapReuseablity()) {
            logger.p("hasBitmapReuseablity return false");
            return null;
        }

        if (isReusableBitmap(old, width, height, config)) {
            //Log.d(TAG,"getReusableBitmap old="+old);
            return old;
        }

        Bitmap bitmap = null;
        if (reusableSet != null) {
            final Iterator<SoftReference<Bitmap>> iterator = reusableSet.iterator();
            while (iterator.hasNext()) {
                Bitmap item = iterator.next().get();

                if (isReusableBitmap(item, width, height, config)) {
                    bitmap = item;
                    iterator.remove();
                    //Log.d(TAG,"getReusableBitmap item="+item);
                    break;
                }
            }

            if (old != null && old.isMutable()) {
                reusableSet.add(new SoftReference<Bitmap>(old));
            }
        }
        //Log.d(TAG,"getReusableBitmap bitmap="+bitmap);
        return bitmap;
    }

    private boolean isReusableBitmap(Bitmap bitmap, int width, int height, Bitmap.Config config) {
        if (bitmap == null || !bitmap.isMutable())
            return false;

        if (bitmap.getWidth() == width && bitmap.getHeight() == height && bitmap.getConfig() == config) {
            return true;
        }

        if (Build.VERSION.SDK_INT >= 19) {
            return isReUsed(bitmap,width,height,config);
        }

        return false;
    }

    @TargetApi(19)
    private boolean isReUsed(Bitmap bitmap,int width, int height, Bitmap.Config config){
        int byteNeed = width * height * sizeOfPixel(config);

        if (Build.VERSION.SDK_INT >= 19) {
            //Log.d(TAG,"isReUsed bmp_w="+bitmap.getWidth()+";bmp_h="+bitmap.getHeight()+";width="+width+";height="+height);
            if (bitmap.getAllocationByteCount() >= byteNeed) {
//                if(width <= bitmap.getWidth()){
//                    bitmap.setWidth(width);
//                    bitmap.setHeight(height);
//                }else{
//                    bitmap.setHeight(height);
//                    bitmap.setWidth(width);
//                }
//
//                bitmap.setConfig(config);
                return true;
            }
        }

        return false;
    }

    private int sizeOfPixel(Bitmap.Config config) {
        if (config == Bitmap.Config.ARGB_8888) {
            return 4;
        }
        if (config == Bitmap.Config.RGB_565 || config == Bitmap.Config.ARGB_4444) {
            return 2;
        }
        if (config == Bitmap.Config.ALPHA_8) {
            return 1;
        }

        return 1;
    }

    private ReentrantLock getLock(String s) {
        //logger.d("getLock key: " + s + ", index: " + (Math.abs(s.hashCode()) % MAX_LOCKS));
        return locks[Math.abs(s.hashCode()) % MAX_LOCKS];
    }

    private native long setBitmapData(Bitmap bitmap);

    private native void getBitmapData(long pointer, Bitmap bitmap);

    private native void free(long pointer);

    @SuppressWarnings("unused")
    private native int getMemTotal();

    @SuppressWarnings("unused")
    private native int getMemFree();

    /**
     * 保存在JVM堆内存中的对象，用来保管C内存bitmap对象的地址信息
     */
    private class BitmapInfo {
        private long pointer; // pointer address of JNI-C

        private int width;
        private int height;
        private Bitmap.Config config;
        private int bitmapHash;
        private int dataSize;

        public BitmapInfo(long pointer, Bitmap bitmap) {
            this.pointer = pointer;

            this.width = bitmap.getWidth();
            this.height = bitmap.getHeight();
            this.config = bitmap.getConfig();
            this.bitmapHash = bitmap.hashCode();
            this.dataSize = bitmap.getRowBytes() * bitmap.getHeight();
        }

        public boolean exist(Bitmap bitmap) {
            return width == bitmap.getWidth() && height == bitmap.getHeight() && config == bitmap.getConfig() && bitmapHash == bitmap.hashCode();
        }

        public boolean valid() {
            return width > 0 && height > 0 && config != null && pointer != 0;
        }

        @Override
        public String toString() {
            return "BitmapInfo{" +
                    "pointer=" + pointer +
                    ", width=" + width +
                    ", height=" + height +
                    ", config=" + config +
                    ", needBytes=" + (width * height * sizeOfPixel(config)) +
                    '}';
        }
    }
}
