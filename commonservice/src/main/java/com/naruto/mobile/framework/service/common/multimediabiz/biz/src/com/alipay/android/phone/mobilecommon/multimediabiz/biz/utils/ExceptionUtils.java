package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils;


import java.io.IOException;

import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.ImageCacheContext;

/**
 * 异常处理工具类
 * Created by jinmin on 15/8/14.
 */
public class ExceptionUtils {

    private static final String CLASS_ANDROID_ERRNO_EXCEPTION = "android.system.ErrnoException";
    private static final String CLASS_LIBCORE_ERRNO_EXCEPTION = "libcore.io.ErrnoException";

    private static final int MIN_RESET_TIME_INTERVAL = 1000;

    private static long lastResetTime = -1;

    private static boolean isErrnoException(Exception e) {
        return e != null &&
                (CompareUtils.in(e.getClass().getName(), CLASS_ANDROID_ERRNO_EXCEPTION, CLASS_LIBCORE_ERRNO_EXCEPTION) || hasErrnoMsg(e));
    }

    private static boolean hasErrnoMsg(Exception e) {
        return e instanceof IOException && (e.getMessage().contains("EROFS") || e.getMessage().contains("EBUSY"));
    }


    public static void checkAndResetDiskCache(Exception e) {
        if ((System.currentTimeMillis()-lastResetTime>MIN_RESET_TIME_INTERVAL) && isErrnoException(e)) {
            Logger.E("DiskCache", "checkAndResetDiskCache will reset");
            ImageCacheContext.get().resetDiskCache();
            lastResetTime = System.currentTimeMillis();
        }
    }
}
