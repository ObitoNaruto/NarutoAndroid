package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import com.naruto.mobile.framework.service.common.multimedia.graphics.data.CutScaleType;
import com.naruto.mobile.framework.service.common.multimedia.graphics.data.Size;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.io.IOUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.assist.FalconFacade;

/**
 * 图片辅助工具类
 * Created by jinmin on 15/5/6.
 */
public class ImageUtils {

    private static final String TAG = ImageUtils.class.getSimpleName();

    private static final Logger logger = Logger.getLogger(TAG);

    public static int[] guessPreferRect(View view, int defaultWidth, int defaultHeight) {
        Logger.D(TAG, "guessPreferRect start");
        long start = System.currentTimeMillis();
        int[] widthHeight;
        if (view != null && view.getContext() != null) {
            Context context = view.getContext();
            view.measure(0, 0);
            int width = view.getMeasuredWidth();
            int height = view.getMeasuredHeight();
            if (width == 0 || height == 0) {//全屏显示
                if (isFillParent(view)) {
                    DisplayMetrics metrics = context.getResources().getDisplayMetrics();
                    width = metrics.widthPixels;
                    height = metrics.heightPixels;
                }
            }
            widthHeight = new int[] { width, height };
        } else {
            widthHeight = new int[] { defaultWidth, defaultHeight };
        }
        Logger.D(TAG, "guessPreferRect ret: " + Arrays.toString(widthHeight) + ", usedTime: " +
                (System.currentTimeMillis() - start));
        return widthHeight;
    }

    public static int[] getScaleScreenRect(Context context, float scale) {
        int width = 1280;
        int height = 1280;
        if (context != null) {
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            scale = scale < 0 ? 1 : scale;
            width = (int) (metrics.widthPixels * scale);
            height = (int) (metrics.heightPixels * scale);
        }
        return new int[] { width, height };
    }

    public static int[] getScaleScreenRect(Context context) {
        return getScaleScreenRect(context, 1.2f);
    }

    private static boolean isFillParent(View view) {
        boolean fillParent = false;
        if (view != null) {
            ViewGroup.LayoutParams params = view.getLayoutParams();
            fillParent = params.width == ViewGroup.LayoutParams.MATCH_PARENT ||
                    params.height == ViewGroup.LayoutParams.MATCH_PARENT;
        }
        Logger.D(TAG, "isFillParent view: " + view + ", isFillParent: " + fillParent);
        return fillParent;
    }

    public static int[] calculateDesWidthHeight(String path) {
//        return FalconFacade.get().calculateDesWidthHeight(path);
        return null;
    }

    /**
     * 计算图片的缩放值
     */
    public static int calculateInSampleSize(BitmapFactory.Options options,
            int reqWidth, int reqHeight) {
        final int height = options.outHeight;// 获取图片的高
        final int width = options.outWidth;// 获取图片的框
        int inSampleSize = 4;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = (int) Math.ceil((double) height
                    / reqHeight);
            final int widthRatio = (int) Math.ceil((double) width / reqWidth);
//            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
            inSampleSize = Math.max(widthRatio, heightRatio);
        }
        Logger.I(TAG, "calculateInSampleSize outWidth: %s, outHeight: %s, sampleSize: %s",
                options.outWidth, options.outHeight, inSampleSize);
        return inSampleSize;// 求出缩放值
    }

    public static Bitmap decodeBitmap(String path, int width, int height) throws Exception {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        options = new BitmapFactory.Options();
        if (width > 0 && height > 0) {
            options.inSampleSize = calculateInSampleSize(options, width, height);
        }
//        options.inMutable = true;
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        return bitmap;
    }

    /**
     *
     * @param data
     * @return
     */
    public static Bitmap getBitmapFromByte(byte[] data) {
        if (data != null) {
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            } catch (Exception e) {
                //ignore
            }
            return bitmap;
        } else {
            return null;
        }
    }

    public static Bitmap decodeBitmap(InputStream is) {
        Bitmap bitmap = null;
        if (is != null) {
            try {
                bitmap = BitmapFactory.decodeStream(is);
            } catch (Exception e) {
                logger.e(e, "decodeBitmap inputStream failed");
            }
        }
        return bitmap;
    }

    public static Bitmap decodeBitmap(File file) {
        Bitmap bitmap = null;
        if (FileUtils.checkFile(file)) {
            try {
                bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            } catch (Exception e) {
                logger.e(e, "decodeBitmap file failed, file: " + file);
            }
        }
        return bitmap;
    }

    public static boolean compressBitmap(Bitmap bitmap, OutputStream os) {
        boolean ret = false;
        try {
            if (checkBitmap(bitmap)) {
                if (bitmap.hasAlpha()) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                } else {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, os);
                }
                ret = true;
            }
        } catch (Exception e) {
            logger.e(e, "compressBitmap error");
        } finally {
            IOUtils.closeQuietly(os);
        }
        return ret;
    }

    public static boolean checkBitmap(Bitmap bitmap) {
        return bitmap != null && !bitmap.isRecycled();
    }

    public static boolean hasAlphaFile(String path) {
        boolean hasAlpha = false;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);
            String mimeType = options.outMimeType.toLowerCase();
            hasAlpha = mimeType.endsWith("png") || mimeType.endsWith("gif");
        } catch (Exception e) {
            //ignore
        }
        return hasAlpha;
    }

    public static CutScaleType calcCutScaleType(Size size, int maxLen) {
        if (size != null && maxLen > 0 && size.getWidth() > 0 && size.getHeight() > 0) {
            double delta = 0.01;
            int[] wh = FalconFacade.get().calculateCutImageRect(size.getWidth(), size.getHeight(), maxLen, null);
            double widthRatio = (double) wh[0] / size.getWidth();
            double heightRatio = (double) wh[1] / size.getHeight();
            double srcRatio = (double) size.getWidth() / size.getHeight();
            double newRatio = (double) wh[0] / wh[1];
            if (Math.abs(widthRatio - heightRatio) >= delta || Math.abs(srcRatio - newRatio) >= delta) {
                return CutScaleType.CENTER_CROP;
            }
        }
        return CutScaleType.KEEP_RATIO;
    }

    public static Bitmap zoomBitmap(Bitmap src, int targetWidth, int targetHeight) {
        if (src != null && !src.isRecycled() && !(targetWidth == 0 && targetHeight == 0)) {
            float bitmapWidth = src.getWidth();
            float bitmapHeight = src.getHeight();

            float scale = 1.0f;
            //targetWidth & targetHeight 均不为0
            if (targetWidth > 0 && targetHeight > 0) {
                float scaleWidth = targetWidth / bitmapWidth;
                float scaleHeight = targetHeight / bitmapHeight;
                scale = Math.min(scaleWidth, scaleHeight);
            } else if (targetWidth > 0) {   //以宽为标准
                scale = targetWidth / bitmapWidth;
            } else if (targetHeight > 0) {  //以高为标准
                scale = targetHeight / bitmapHeight;
            }
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);

            Bitmap scaleBitmap = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
            Logger.D(TAG, "zoomBitmap newWidth: %s, newHeight: %s", scaleBitmap.getWidth(), scaleBitmap.getHeight());
            return scaleBitmap;
        }
        return null;
    }

    public static int dp2px(float dp) {
        Context context = AppUtils.getApplicationContext();
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
    }

    public static boolean isImage(String filePath) {
        long start = System.currentTimeMillis();
        BitmapFactory.Options option_decodeOptions = new BitmapFactory.Options();
        option_decodeOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(filePath, option_decodeOptions);
        Logger.I(TAG, "isImage outMimeType: %s, newWidth: %s, newHeight: %s", option_decodeOptions.outMimeType,
                option_decodeOptions.outWidth, option_decodeOptions.outHeight);
        if (TextUtils.isEmpty(option_decodeOptions.outMimeType) || option_decodeOptions.outWidth <= 0 ||
                option_decodeOptions.outHeight <= 0) {
            Logger.I(TAG, "isImage false cost: %s, ", (System.currentTimeMillis() - start));
            return false;
        }
        Logger.I(TAG, "isImage yes cost: %s, ", (System.currentTimeMillis() - start));
        return true;
    }

    public static int[] getPreferOriginalShowSize(int[] defaultSize) {
        final double MAX_SCALE = 3.5;
        Runtime runtime = Runtime.getRuntime();
        long maxMem = runtime.maxMemory() >> 1;
        long totalMem = runtime.totalMemory();
        long free = maxMem - totalMem;//考虑扩展内存可用，所以用max-cur
        int maxSide = Math.max(defaultSize[0], defaultSize[1]);
        double scale = Math.min(MAX_SCALE, (double) free / (maxSide * maxSide * 8));
        logger.d("getPreferOriginalShowSize defaultSize: " + Arrays.toString(defaultSize));
        if (scale > 1.5) {
            defaultSize[0] *= scale;
            defaultSize[1] *= scale;
        } else {
            defaultSize[0] *= 1.5;
            defaultSize[1] *= 1.5;
        }
        logger.d("getPreferOriginalShowSize prefer size: " + Arrays.toString(defaultSize) + ", free: " + free + ", scale: " + scale);
        return defaultSize;
    }

    //这图大，想复用falcon的oom降级处理
    public static Bitmap decodeBitmapByFalcon(File file) {
        try {
            Bitmap bitmap = FalconFacade.get().cutImageKeepRatio(file, 2000, 2000);//ImageUtils.decodeBitmap(file);
            return bitmap;
        } catch (Exception e) {
            logger.e(e, "decodeBitmapByFalcon err");
        }
        return null;
    }
}
