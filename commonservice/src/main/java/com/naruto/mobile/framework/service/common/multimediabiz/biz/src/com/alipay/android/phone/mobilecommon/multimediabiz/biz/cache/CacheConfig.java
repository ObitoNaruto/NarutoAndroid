package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache;

import android.content.Context;
import android.text.TextUtils;

import java.io.File;

import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.AppUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.FileUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Logger;

/**
 * Created by jinmin on 15/5/11.
 */
public class CacheConfig {

    private static final String TAG = "CacheConfig";

    public static File getCacheDir() {
        Context context = AppUtils.getApplicationContext();
        File cacheDir = null;
        String cachePath = FileUtils.getMediaDir("cache", false);
        Logger.D(TAG, "cachePath: " + cachePath);
        if (TextUtils.isEmpty(cachePath)) {
            cachePath = context.getCacheDir().getAbsolutePath();
        }
        Logger.D(TAG, "cachePath: " + cachePath);
        cacheDir = new File(cachePath);
        return cacheDir;
    }

    public static File getImageCacheDir() {
        File imageCacheDir = null;
        File baseDir = getCacheDir();
        if (baseDir != null) {
            imageCacheDir = new File(baseDir, "images");
            baseDir.mkdirs();
        }
        return imageCacheDir;
    }
}
