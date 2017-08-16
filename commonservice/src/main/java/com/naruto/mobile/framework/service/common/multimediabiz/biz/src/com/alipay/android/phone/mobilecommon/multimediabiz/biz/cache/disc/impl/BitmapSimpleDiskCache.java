package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.disc.impl;

import android.graphics.Bitmap;

import java.io.File;

import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.disc.DiskCacheHandler;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.disc.naming.FileNameGenerator;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.disc.naming.Md5FileNameGenerator;

/**
 * BitmapSimpleDiskCache
 * Created by jinmin on 15/5/11.
 */
public class BitmapSimpleDiskCache extends SimpleDiskCache<Bitmap> {
    public BitmapSimpleDiskCache(File baseDir, DiskCacheHandler<Bitmap> handler, FileNameGenerator nameGenerator) {
        super(baseDir, handler, nameGenerator);
    }

    public BitmapSimpleDiskCache(File baseDir, DiskCacheHandler<Bitmap> handler) {
        this(baseDir, handler, new Md5FileNameGenerator());
    }
}
