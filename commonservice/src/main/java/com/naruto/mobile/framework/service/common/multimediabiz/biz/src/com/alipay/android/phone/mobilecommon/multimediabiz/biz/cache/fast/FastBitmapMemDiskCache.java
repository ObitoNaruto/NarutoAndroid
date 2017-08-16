package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.fast;

import android.graphics.Bitmap;

import java.io.File;
import java.nio.ByteBuffer;

import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Logger;

/**
 * 高速图片映射缓存
 * Created by jinmin on 15/6/9.
 */
public class FastBitmapMemDiskCache extends FastMemDiskCache{

    private Logger logger = Logger.getLogger("FastBitmapMemDiskCache");

    public FastBitmapMemDiskCache(int maxSize) {
        super(maxSize);
    }

    @Override
    public Bitmap get(String key) {
        //long start = System.currentTimeMillis();
        Bitmap bitmap = super.get(key);
        //logger.d("get key: %s, bitmap: %s, usedTime: %s", key, bitmap, (System.currentTimeMillis()-start));
        return bitmap;
    }

    public Bitmap get(String key, Bitmap reuse) {
        //long start = System.currentTimeMillis();
        Bitmap bitmap = super.get(key, reuse);
        //logger.d("get key: %s, bitmap: %s, pre: %s, usedTime: %s", key, bitmap, reuse, (System.currentTimeMillis()-start));
        return bitmap;
    }

    @Override
    public boolean put(String key, Bitmap value) {
        //long start = System.currentTimeMillis();
        boolean flag = super.put(key, value);
        //logger.d("put key: %s, usedTime: %s, flag: %s", key, (System.currentTimeMillis()-start), flag);
        return flag;
    }
}
