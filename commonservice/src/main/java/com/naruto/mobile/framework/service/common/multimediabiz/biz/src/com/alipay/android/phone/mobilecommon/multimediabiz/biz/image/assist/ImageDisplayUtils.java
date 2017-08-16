package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.assist;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import com.naruto.mobile.framework.service.common.multimedia.graphics.data.ReusableBitmapDrawable;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.AppUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.ImageUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Logger;

/**
 * 显示相关工具类
 * Created by jinmin on 15/7/13.
 */
public class ImageDisplayUtils {

    private static Logger logger = Logger.getLogger("ImageDisplayUtils");

    //主线程Handler
    private static final Handler uiHandler = new Handler(Looper.getMainLooper());

    //判断当前是否在处在UI线程
    public static boolean inMainLooper() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    public static boolean display(final Bitmap bitmap, final ViewWrapper viewWrapper, final boolean bitmapReusable) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //已经再用，重新使用了，直接返回
                if (checkImageViewReused(viewWrapper)) {
                    logger.p("display bitmap checkImageViewReused return !");
                    return;
                }
                setImage(bitmap, (ImageView) viewWrapper.getTargetView(), bitmapReusable);
            }
        });
        return true;
    }

    public static boolean display(final Drawable drawable, final ViewWrapper viewWrapper) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (checkImageViewReused(viewWrapper)) {
                    logger.p("display drawable checkImageViewReused return !");
                    return;
                }
                setImage(drawable, (ImageView) viewWrapper.getTargetView());
            }
        });
        return true;
    }

    private static boolean setImage(Bitmap bitmap, ImageView imageView, boolean bitmapReusable) {
        boolean ret = false;
        if (ImageUtils.checkBitmap(bitmap)) {//bitmap有效
            if (bitmapReusable) {//可再用
                //先进行可重用的Bitmap的包装，然后设置
                ret = setImage(new ReusableBitmapDrawable(AppUtils.getResources(), bitmap), imageView);
            } else if (imageView != null) {
                imageView.setImageBitmap(bitmap);
//                imageView.postInvalidate();
                ret = true;//设置成功了
            }
        }
        logger.p("setImage bitmap: " + bitmap + ", imageView: " + imageView + ", reusable: " + bitmapReusable +
                ", ret: " + ret);
        return ret;
    }

    private static boolean setImage(Drawable drawable, ImageView imageView) {
        boolean ret = false;
        if (imageView != null) {
            imageView.setImageDrawable(drawable);
//            imageView.postInvalidate();
            ret = true;
        }
        return ret;
    }

    public static Bitmap getReusableBitmap(ImageView imageView) {
        Bitmap reusableBitmap = null;
        if (imageView != null && imageView.getDrawable() instanceof ReusableBitmapDrawable) {
            reusableBitmap = ((ReusableBitmapDrawable) imageView.getDrawable()).getBitmap();
        }
        return reusableBitmap;
    }

    public static void runOnUiThread(Runnable runnable) {
        if (runnable == null) return;
        if (inMainLooper()) {
            runnable.run();
        } else {
            uiHandler.post(runnable);
        }
    }

    public static boolean checkImageViewReused(ViewWrapper viewWrapper) {
        return ViewAssistant.getInstance().checkViewReused(viewWrapper);
    }
}
