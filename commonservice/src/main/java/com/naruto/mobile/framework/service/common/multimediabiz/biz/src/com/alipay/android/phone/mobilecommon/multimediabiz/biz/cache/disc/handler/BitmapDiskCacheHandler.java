package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.disc.handler;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.disc.DiskCacheHandler;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.io.IOUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Logger;

/**
 * 图片缓存 内存与Disk 互转处理
 * Created by jinmin on 15/4/8.
 */
public class BitmapDiskCacheHandler implements DiskCacheHandler<Bitmap> {
    private static final String TAG = "BitmapDiskCacheHandler";
    @Override
    public boolean saveDisk(File dst, String key, Bitmap value) throws IOException {
        boolean ret = false;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        if (value != null && !value.isRecycled()) {
            //Logger.D(TAG, "saveDisk start");
            String tmpFileName = dst.getAbsolutePath() + ".tmp";
            File tmpFile = new File(tmpFileName);
            //拷贝一份临时的使用，避免外部recycle了
//            Bitmap copy = Bitmap.createBitmap(value.getWidth(), value.getHeight(), value.getConfig());
            try {
//                Bitmaps.copyBitmap(copy, value);
                tmpFile.getParentFile().mkdirs();
                tmpFile.createNewFile();

                fos = new FileOutputStream(tmpFile);
                bos = new BufferedOutputStream(fos);
                if (value.hasAlpha()) {
                    value.compress(Bitmap.CompressFormat.PNG, 100, bos);
                } else {
                    value.compress(Bitmap.CompressFormat.JPEG, 80, bos);
                }
                ret = tmpFile.renameTo(dst);
                Logger.E(TAG, "saveFile saveDisk rename from " + tmpFile + " to: " + dst + ", ret: " + ret);
            } catch (Exception e) { //防止有异步线程把bitmap给回收了
                Logger.E(TAG, e, "saveDisk error");
                Logger.E(TAG, "saveDisk del: " + tmpFile);
                tmpFile.delete();
            } finally {
                IOUtils.closeQuietly(bos);
                IOUtils.closeQuietly(fos);
                if (!value.isRecycled()) {
                    value.recycle();
                }
            }
        } else {
            Logger.E(TAG, "saveDisk try to compress null or recycled bitmap, for key: %s, bitmap: %s", key, value);
        }
        //Logger.D(TAG, "saveDisk end ret="+ret);
        return ret;
    }

    @Override
    public Bitmap loadDisk(File input) throws IOException {
        String path = input.getAbsolutePath();
        BitmapFactory.Options options = new BitmapFactory.Options();
//        if (ImageUtils.hasAlphaFile(path)) {
//            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//        } else {
//            options.inPreferredConfig = Bitmap.Config.RGB_565;
//        }
        return BitmapFactory.decodeFile(path, options);
    }
}
