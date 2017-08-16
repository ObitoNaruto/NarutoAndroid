package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.assist;

import android.graphics.Bitmap;
//import com.alipay.android.phone.falcon.img.CutCallBack;
//import com.alipay.android.phone.falcon.img.FalconImgCut;
//import com.alipay.android.phone.falcon.img.FalconImgICompress;
//import com.alipay.android.phone.falcon.img.JniFalconImg;

import java.io.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

import com.naruto.mobile.framework.service.common.falcon.api.src.com.alipay.android.phone.falcon.img.CutCallBack;
import com.naruto.mobile.framework.service.common.falcon.api.src.com.alipay.android.phone.falcon.img.FalconImgCut;
import com.naruto.mobile.framework.service.common.falcon.api.src.com.alipay.android.phone.falcon.img.FalconImgICompress;
import com.naruto.mobile.framework.service.common.multimedia.graphics.data.APImageLoadRequest;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.BitmapCacheLoader;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.io.IOUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.ImageLoadEngine;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.listener.OOMListener;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Logger;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.PathUtils;

/**
 * FalconProxy
 * Created by jinmin on 15/5/25.
 */
public class FalconFacade {
    private static final String TAG = "FalconFacade";
    public static final int QUA_LOW = 0;
    public static final int QUA_MIDDLE = 1;
    public static final int QUA_HIGH = 2;

    static {
//        JniFalconImg.initJni();
    }

    private static FalconFacade sFacade = new FalconFacade();

    private Logger logger = Logger.getLogger(TAG);
    private FalconImgCut mFalconImgCut;
    private FalconImgICompress mFalconCompress;
    private BitmapCacheLoader mCacheLoader;

    public static FalconFacade get() {
        return sFacade;
    }

    private FalconFacade() {
        mFalconImgCut = new FalconImgCut();
//        mFalconImgCut = Proxy.
        mFalconCompress = new FalconImgICompress();
        mCacheLoader = ImageLoadEngine.getInstance().getCacheLoader();
    }

    ////////////////////////////////////////////////////////////
    public class FalconMemoryHandler implements CutCallBack {
        @Override
        public void onCalcMemSize(long releaseSize,boolean isForceRelease) {

            if(isForceRelease){
                causeGC(null,releaseSize);
            }else{
                mCacheLoader.releaseImageMem(releaseSize,false);
            }
        }

        private boolean causeGC(OutOfMemoryError e,long decSize){
            OOMListener listener = new OOMListener();
            return listener.OnCutImageOOm(e, null, decSize, mCacheLoader);
        }
    }
    ////////////////////////////////////////////////////////////

    /*************************************************  Falcon辅助    **************************************************/

    /**
     * 计算裁切图宽高
     *
     * @param width  宽
     * @param height 高
     * @param maxLen 最长值
     * @param path   文件路径
     * @return int[2], int[0]:w, int[1]:h
     */
    public int[] calculateCutImageRect(int width, int height, int maxLen, String path) {
        int[] wh = new int[2];
        try {
            mFalconImgCut.calcultDesWidthHeight_new(PathUtils.extractFile(path), width, height, maxLen, wh);
            logger.d("calculateCutImageRect, width: " + width + " height: " + height +
                    ", maxLen: " + maxLen + ", out: " + Arrays.toString(wh));
        } catch (Exception e) {
            wh[0] = APImageLoadRequest.DEFAULT_LOAD_W;
            wh[1] = APImageLoadRequest.DEFAULT_LOAD_H;
        }

        return wh;
    }

    /**
     * 计算目标图片宽高，角度纠正后的宽高
     * @param path
     * @return
     */
    public int[] calculateDesWidthHeight(String path) {
        int[] wh = new int[2];
        try {
            boolean success = mFalconImgCut.calcultDesWidthHeight_new(PathUtils.extractFile(path), wh);
            if (!success) {
                wh = null;
            }
        } catch (Exception e) {
            wh = null;
        }
        return wh;
    }


    /*************************************************  Falcon 压缩   **************************************************/

    /**
     * @param imageFile
     * @param quality   压缩质量，0-低质量，1-中质量，>=2-高质量
     * @param width
     * @param height
     * @return
     * @throws IOException
     */
    public ByteArrayOutputStream compressImage(File imageFile, int quality, int width, int height) throws IOException {
        FalconInterface falconInterface = getFalconCut();
        return falconInterface.compressImage(imageFile,quality,width,height);


    }

    /**
     * @param imageData
     * @param quality   压缩质量，0-低质量，1-中质量，>=2-高质量
     * @param width
     * @param height
     * @return
     * @throws IOException
     */
    public ByteArrayOutputStream compressImage(byte[] imageData, int quality, int width, int height)
            throws IOException {
        FalconInterface falconInterface = getFalconCut();
        return falconInterface.compressImage(imageData,quality,width,height);
    }

    public ByteArrayOutputStream compressImage(InputStream in, int quality, int width, int height) throws IOException {
        FalconInterface falconInterface = getFalconCut();
        return falconInterface.compressImage(in,quality,width,height);
    }

    /*************************************************  Falcon 裁切   **************************************************/
    private FalconInterface getFalconCut() {
        FalconInterface cut = new FalconInterfaceImpl();
        FalconImageCutProxy proxy = new FalconImageCutProxy(cut);
        return (FalconInterface) Proxy.newProxyInstance(cut.getClass().getClassLoader(),
                                        cut.getClass().getInterfaces(),
                                        proxy);
    }
    public Bitmap cutImage(File file, int width, int height, float scale) throws IOException {
        FalconInterface falconInterface = getFalconCut();
        return falconInterface.cutImage_new(file, width, height, scale);
    }

    public Bitmap cutImage(InputStream in, int width, int height, float scale) throws IOException {
        FalconInterface falconInterface = getFalconCut();
        return falconInterface.cutImage_new(in, width, height, scale);
    }

    public Bitmap cutImage(byte[] data, int width, int height, float scale) throws IOException {
        if (data != null && data.length > 0) {
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            Bitmap bmp = cutImage(in, width, height, scale);
            IOUtils.closeQuietly(in);
            return bmp;
        }
        return null;
    }

    public Bitmap cutImageBackground(File file, int newWidth, int newHeight) throws Exception {
        FalconInterface cut = getFalconCut();
        return cut.cutImage_backgroud(file, newWidth, newHeight);
    }

    public Bitmap cutImageBackground(InputStream in, int newWidth, int newHeight) throws Exception {
        FalconInterface cut = getFalconCut();
        return cut.cutImage_backgroud(in, newWidth, newHeight);
    }

    public Bitmap cutImageKeepRatio(File file, int newWidth, int newHeight) throws Exception {
        FalconInterface cut = getFalconCut();
        return cut.cutImage_keepRatio(file, newWidth, newHeight);
    }

    public Bitmap cutImageKeepRatio(InputStream in, int newWidth, int newHeight) throws IOException {
        FalconInterface cut = getFalconCut();
        return cut.cutImage_keepRatio(in, newWidth, newHeight);
    }

    public Bitmap cutImageKeepRatio(byte[] data, int newWidth, int newHeight) throws IOException {
        if (data != null && data.length > 0) {
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            Bitmap bitmap = cutImageKeepRatio(in, newWidth, newHeight);
            IOUtils.closeQuietly(in);
            return bitmap;
        }
        return null;
    }




    /***********************  通过动态代理统一处理OOM问题  *******************************/
    interface FalconInterface {
        FalconImgCut getFalconImgCut();
        Bitmap cutImage_new(File file, int maxLen, int minLen, float scale) throws IOException;
        Bitmap cutImage_new(InputStream in, int maxLen, int minLen, float scale) throws IOException;
        Bitmap cutImage_backgroud(File file, int newWidth, int newHeight) throws Exception;
        Bitmap cutImage_backgroud(InputStream image, int newWidth, int newHeight) throws Exception;
        Bitmap cutImage_keepRatio(File file, int newWidth, int newHeight) throws Exception;
        Bitmap cutImage_keepRatio(InputStream image, int newWidth, int newHeight) throws IOException;
        ByteArrayOutputStream compressImage(File imageFile, int quality, int width, int height)throws IOException;
        ByteArrayOutputStream compressImage(byte[] imageData, int quality, int width, int height) throws IOException;
        ByteArrayOutputStream compressImage(InputStream in, int quality, int width, int height) throws IOException;
    }

    class FalconInterfaceImpl implements FalconInterface {
        private FalconImgCut cut;

        public FalconInterfaceImpl() {
            cut = new FalconImgCut();
        }

        @Override
        public FalconImgCut getFalconImgCut() {
            return cut;
        }

        @Override
        public Bitmap cutImage_new(File file, int maxLen, int minLen, float scale) throws IOException {
            return cut.cutImage_new(file, maxLen, minLen, scale);
        }

        @Override
        public Bitmap cutImage_new(InputStream in, int maxLen, int minLen, float scale) throws IOException {
            return cut.cutImage_new(in, maxLen, minLen, scale);
        }

        @Override
        public Bitmap cutImage_backgroud(File file, int newWidth, int newHeight) throws Exception {
            return cut.cutImage_backgroud(file, newWidth, newHeight);
        }

        @Override
        public Bitmap cutImage_backgroud(InputStream image, int newWidth, int newHeight) throws Exception {
            return cut.cutImage_backgroud(image, newWidth, newHeight);
        }

        @Override
        public Bitmap cutImage_keepRatio(File file, int newWidth, int newHeight) throws Exception {
            return cut.cutImage_keepRatio(file, newWidth, newHeight);
        }

        @Override
        public Bitmap cutImage_keepRatio(InputStream image, int newWidth, int newHeight) throws IOException {
            return cut.cutImage_keepRatio(image, newWidth, newHeight);
        }

        @Override
        public ByteArrayOutputStream compressImage(File imageFile, int quality, int width, int height) throws IOException {
            long start = System.currentTimeMillis();
            logger.d("compressImage " + imageFile.getName() + ";quality;=;" + quality + ";width=" + width + ";height=" +
                    height + ";start at " + start);
            ByteArrayOutputStream baos = null;
            if (width <= 0 || height <= 0) {
                baos = mFalconCompress.GenerateCompressImage_new(imageFile, quality);
            } else {
                baos = mFalconCompress.GenerateCompressImage_new(imageFile, quality, width, height);
            }
            logger.d("compressImage cost time: " + (System.currentTimeMillis() - start));
            return baos;
        }

        @Override
        public ByteArrayOutputStream compressImage(byte[] imageData, int quality, int width, int height) throws IOException {
            ByteArrayInputStream bais = null;
            try {
                bais = new ByteArrayInputStream(imageData);
                return compressImage(bais, quality, width, height);
            } finally {
                IOUtils.closeQuietly(bais);
            }
        }

        @Override
        public ByteArrayOutputStream compressImage(InputStream in, int quality, int width, int height) throws IOException {
            ByteArrayOutputStream baos = null;
            if (width <= 0 || height <= 0) {
                baos = mFalconCompress.GenerateCompressImage_new(in, quality);
            } else {
                baos = mFalconCompress.GenerateCompressImage_new(in, quality, width, height);
            }
            return baos;
        }
    }


    private class FalconImageCutProxy implements InvocationHandler {

        private FalconInterface target;

        public FalconImageCutProxy(FalconInterface target) {
            this.target = target;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Object result = null;
            //检测是否cut方法，是cut的方法增加oom检测
            FalconImgCut cut = target.getFalconImgCut();
            FalconMemoryHandler handler = new FalconMemoryHandler();
            cut.registeCallBack(handler);
            try {
                result = method.invoke(target, args);
            } finally {
                cut.unregisteCallBack();
            }
            return result;
        }
    }
}
