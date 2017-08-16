package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.task;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.ByteArrayInputStream;

import com.naruto.mobile.framework.service.common.multimedia.graphics.data.ReusableBitmapDrawable;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.BitmapCacheLoader;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.io.IOUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.ImageLoadReq;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.assist.ImageDisplayUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.AppUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.ImageUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Logger;

/**
 * 图片加载任务
 * Created by jinmin on 15/7/14.
 */
public class ImageLoadTask extends ImageTask {

    private Logger logger = Logger.getLogger("ImageLoadTask");

    public ImageLoadTask(ImageLoadReq loadReq) {
        super(loadReq, null);
        //设置tag
        this.viewAssistant.setViewTag(loadReq.imageView, loadReq.cacheKey);
        displayDefaultDrawable();
    }

    @Override
    public Object call() throws Exception {
        long start = System.currentTimeMillis();
        logger.d("load start...." + loadReq);
        if (checkTask()) {
            return null;
        }
        //填充默认图
        displayDefaultDrawable();

        BitmapCacheLoader cacheLoader = getCacheLoader();
        Bitmap bitmap = null;
        if (loadReq.data != null && loadReq.data.length > 0) {//检测存入是否图片data
            ByteArrayInputStream in = new ByteArrayInputStream(loadReq.data);
            bitmap = ImageUtils.decodeBitmap(in);
            IOUtils.closeQuietly(in);
        } else {//检测本地缓存
            bitmap = cacheLoader.getDiskCache(loadReq.cacheKey);
            cacheLoader.putMemCache(loadReq.cacheKey, bitmap);
        }
        Logger.TIME("ImageLocalTask call getDiskCache costTime: "
                + (System.currentTimeMillis() - start) + ", " + loadReq.path);

        start = System.currentTimeMillis();
        logger.d("check disk cache bitmap: " + bitmap);
        if (ImageUtils.checkBitmap(bitmap)) {
            display(bitmap, loadReq, viewWrapper);
            Logger.TIME("ImageLocalTask call display costTime: "
                    + (System.currentTimeMillis() - start) + ", " + loadReq.path);
            logger.p("loadFrom diskCache " + loadReq.path + " cacheKey: " + loadReq.cacheKey);
            return null;
        }
        if (checkImageViewReused()) {
            notifyReuse();
            return null;
        }
        if (!ImageUtils.checkBitmap(bitmap)) {
            //启动本地任务
            ImageTaskEngine.get().submit(new ImageLocalTask(loadReq, viewWrapper));
        }
        Logger.TIME("ImageLocalTask call ImageLocalTask costTime: "
                + (System.currentTimeMillis() - start) + ", " + loadReq.path);
        return null;
    }

    private void displayDefaultDrawable() {
        logger.p("set default image");
//        Bitmap pre = ImageDisplayUtils.getReusableBitmap(loadReq.imageView);
//        Bitmap bitmap = null;//cacheLoader.getMemCache(loadReq.cacheKey, pre);
//        if (ImageUtils.checkBitmap(bitmap)) {
//            display(bitmap, loadReq, viewWrapper);
//        } else {
            Drawable d = options.getImageOnLoading();
            if (d instanceof ReusableBitmapDrawable) {
                d = new BitmapDrawable(AppUtils.getResources(), ((ReusableBitmapDrawable) d).getBitmap());
            }
            ImageDisplayUtils.display(d, viewWrapper);
//        }

    }
}
