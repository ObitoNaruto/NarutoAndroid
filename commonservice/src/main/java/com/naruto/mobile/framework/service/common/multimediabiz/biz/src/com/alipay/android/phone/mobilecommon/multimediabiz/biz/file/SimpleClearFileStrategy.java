package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.file;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import java.io.File;

import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.manager.APFileTaskManager;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.FileUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Logger;

/**
 * 简单粗暴, 空间不够先删三个月前, 还是不够删一个月前的
 */
public class SimpleClearFileStrategy implements ClearFileStrategy {
    private static final String TAG = SimpleClearFileStrategy.class.getSimpleName();
    private Logger logger = Logger.getLogger(TAG);

    public static final long FILE_KEEP_TIME = 1000 * 60 * 60 * 24 * 30 * 3; // 3个月
    public static final long MIN_FREE_STORAGE = 10 * 1024 * 1024;

    protected APFileTaskManager fileTaskManager;

    public SimpleClearFileStrategy(Context context) {
        this.fileTaskManager = APFileTaskManager.getInstance(context);
    }

    @Override
    public boolean clear(long size) {
        long needSize = size + MIN_FREE_STORAGE;
        if (isSpaceEnough(needSize)) {
            return true;
        }
        clearOldFile(FILE_KEEP_TIME);

        if (isSpaceEnough(needSize)) {
            return true;
        }
        clearOldFile(FILE_KEEP_TIME / 3);
        return true;
    }

    private boolean isSpaceEnough(long needSize) {
//        if (FileUtils.IsCanUseSdCard()) {
//            return FileUtils.isSDcardAvailableSpace(needSize);
//        } else {
            return isDataDirAvailableSpace(needSize);
//        }
    }

    // Build.VERSION_CODES.JELLY_BEAN_MR2
    @TargetApi(18)
    private boolean isDataDirAvailableSpace(long space) {
        try {
            File root = Environment.getDataDirectory();
            StatFs sf = new StatFs(root.getPath());

            long blockSize = 0;
            long blockCount = 0;
            long availCount = 0;
            if (Build.VERSION.SDK_INT >= 18) {
                // Build.VERSION_CODES.JELLY_BEAN_MR2 = 18
                blockSize = sf.getBlockSizeLong();
                blockCount = sf.getBlockCountLong();
                availCount = sf.getAvailableBlocksLong();
            } else {
                blockSize = sf.getBlockSize();
                blockCount = sf.getBlockCount();
                availCount = sf.getAvailableBlocks();
            }

            logger.d("block size:" + blockSize + ",block num:" + blockCount + ",total:"
                    + blockSize * blockCount / 1024 + "KB");
            logger.d("free block num:" + availCount + ",free space:"
                    + availCount * blockSize / 1024 + "KB");

            long availableSpare = blockSize * availCount;
            return space < availableSpare;
        } catch (Throwable e) {
            logger.e(e, "");
        }
        return true;
    }

    private void clearOldFile(long fileKeepTime) {
        fileTaskManager.deleteOutDateFile(fileKeepTime);
        int count = fileTaskManager.deleteOutDateTask(fileKeepTime);
    }
}
