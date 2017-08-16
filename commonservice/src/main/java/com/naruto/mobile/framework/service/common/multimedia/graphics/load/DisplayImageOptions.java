package com.naruto.mobile.framework.service.common.multimedia.graphics.load;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.Drawable;

import java.util.concurrent.ExecutorService;

import com.naruto.mobile.framework.service.common.multimedia.graphics.APDisplayer;
import com.naruto.mobile.framework.service.common.multimedia.graphics.ImageWorkerPlugin;
import com.naruto.mobile.framework.service.common.multimedia.graphics.data.APImageMarkRequest;
import com.naruto.mobile.framework.service.common.multimedia.graphics.data.CutScaleType;
import com.naruto.mobile.framework.service.common.multimedia.graphics.data.Size;

public final class DisplayImageOptions {

    private final Drawable imageOnLoading;//加载时图片
    private final Drawable imageForEmptyUri; //请求url为空时返回的图片
    private final Drawable imageOnFail;//请求失败返回的图片
    private final boolean resetViewBeforeLoading;
    private final boolean cacheInMemory;
    private final boolean cacheOnDisk;
    private final CutScaleType cutScaleType;
    private final Options decodingOptions;
    private final int delayBeforeLoading;
    private final boolean considerExifParams;
    private final ImageWorkerPlugin processor;
    private final APDisplayer displayer;
    private final boolean isSyncLoading;
    private final Integer width;
    private final Integer height;
    private final Size originalSize;
    private final ExecutorService netloadExecutorService;
    private final APImageMarkRequest imageMarkRequest;

    private DisplayImageOptions(Builder builder) {
        imageOnLoading = builder.imageOnLoading;
        imageForEmptyUri = builder.imageForEmptyUri;
        imageOnFail = builder.imageOnFail;
        resetViewBeforeLoading = builder.resetViewBeforeLoading;
        cacheInMemory = builder.cacheInMemory;
        cacheOnDisk = builder.cacheOnDisk;
        decodingOptions = builder.decodingOptions;
        delayBeforeLoading = builder.delayBeforeLoading;
        considerExifParams = builder.considerExifParams;
        processor = builder.processor;
        displayer = builder.displayer;
        isSyncLoading = builder.isSyncLoading;
        originalSize = builder.originalSize;
        width = builder.width;
        height = builder.height;
        if (builder.cutScaleType != null) {
            cutScaleType = builder.cutScaleType;
        } else {
            cutScaleType = CutScaleType.KEEP_RATIO;
        }
        netloadExecutorService = builder.netloadExecutorService;
        imageMarkRequest = builder.imageMarkRequest;
    }

//    public boolean shouldShowImageOnLoading() {
//        return imageOnLoading != null;
//    }

//    public boolean shouldShowImageForEmptyUri() {
//        return imageForEmptyUri != null;
//    }

//    public boolean shouldShowImageOnFail() {
//        return imageOnFail != null;
//    }

    public boolean shouldProcess() {
        return processor != null;
    }

//    public boolean shouldDelayBeforeLoading() {
//        return delayBeforeLoading > 0;
//    }

    public Drawable getImageOnLoading() {
        return imageOnLoading;
    }

//    public Drawable getImageForEmptyUri() {
//        return imageForEmptyUri;
//    }

//    public Drawable getImageOnFail() {
//        return imageOnFail;
//    }

//    public boolean isResetViewBeforeLoading() {
//        return resetViewBeforeLoading;
//    }

//    public boolean isCacheInMemory() {
//        return cacheInMemory;
//    }

//    public boolean isCacheOnDisk() {
//        return cacheOnDisk;
//    }

    public CutScaleType getCutScaleType() {
        return cutScaleType;
    }

//    public Options getDecodingOptions() {
//        return decodingOptions;
//    }

//    public int getDelayBeforeLoading() {
//        return delayBeforeLoading;
//    }

//    public boolean isConsiderExifParams() {
//        return considerExifParams;
//    }


    public ImageWorkerPlugin getProcessor() {
        return processor;
    }


    public APDisplayer getDisplayer() {
        return displayer;
    }

    public boolean isSyncLoading() {
        return isSyncLoading;
    }

    public Integer getHeight() {
        if (CutScaleType.NONE.equals(cutScaleType)) {
            return Integer.MAX_VALUE;
        }
        return height;
    }

    public Integer getWidth() {
        if (CutScaleType.NONE.equals(cutScaleType)) {
            return Integer.MAX_VALUE;
        }
        return width;
    }

    public Size getOriginalSize() {
        return originalSize;
    }

    public boolean hasNetloadExecutorService() {
        return netloadExecutorService != null;
    }

    public ExecutorService getNetloadExecutorService() {
        return netloadExecutorService;
    }

    public APImageMarkRequest getImageMarkRequest() {
        return imageMarkRequest;
    }

    public static class Builder {
        private Drawable imageOnLoading = null;//设置加载默认图
        private Drawable imageForEmptyUri = null;//设置空path默认图
        private Drawable imageOnFail = null;//设置加载失败默认图
        private boolean resetViewBeforeLoading = false;//设置每次加载前是否重置ImageView
        private boolean cacheInMemory = false;//设置是否添加到内存缓存
        private boolean cacheOnDisk = false; //设置是否本地文件缓存
        private CutScaleType cutScaleType;//设置图片的缩放模式
        private Options decodingOptions = new Options();//指定图片decode的option/指定图片decode 的config
        private int delayBeforeLoading = 0;//延迟加载
        private boolean considerExifParams = false;//是否检测图片的exif信息
        private ImageWorkerPlugin processor = null;//设置图片处理，比如裁剪等操作
        private APDisplayer displayer = null;//自定义显示
        private boolean isSyncLoading = false;//是否同步加载
        private Size originalSize;//图片原始尺寸
        private Integer width = null; //指定图片宽
        private Integer height = null;//指定图片高
        private ExecutorService netloadExecutorService;//自定义网络加载线程池
        private APImageMarkRequest imageMarkRequest;//自定义水印参数

        public Builder() {
            decodingOptions.inPurgeable = true;
            decodingOptions.inInputShareable = true;
        }

        /**
         * 设置加载默认图
         * @param drawable
         * @return
         */
        public Builder showImageOnLoading(Drawable drawable) {
            imageOnLoading = drawable;
            return this;
        }

        /**
         * 设置空path默认图
         * @param drawable
         * @return
         */
        public Builder showImageForEmptyUri(Drawable drawable) {
            imageForEmptyUri = drawable;
            return this;
        }

        /**
         * 设置加载失败默认图
         * @param drawable
         * @return
         */
        public Builder showImageOnFail(Drawable drawable) {
            imageOnFail = drawable;
            return this;
        }

        /**
         * 设置每次加载前是否重置ImageView
         * @param resetViewBeforeLoading
         * @return
         */
        public Builder resetViewBeforeLoading(boolean resetViewBeforeLoading) {
            this.resetViewBeforeLoading = resetViewBeforeLoading;
            return this;
        }

        /**
         * 设置是否添加到内存缓存
         */
        public Builder cacheInMemory(boolean cacheInMemory) {
            this.cacheInMemory = cacheInMemory;
            return this;
        }

        /**
         * 设置是否本地文件缓存
         * @param cacheOnDisk
         * @return
         */
        public Builder cacheOnDisk(boolean cacheOnDisk) {
            this.cacheOnDisk = cacheOnDisk;
            return this;
        }

        /**
         * 设置图片的缩放模式
         * @param cutScaleType
         * @return
         */
        public Builder imageScaleType(CutScaleType cutScaleType) {
            this.cutScaleType = cutScaleType;
            return this;
        }

        /**
         * 指定图片decode 的config
         * @param bitmapConfig
         * @return
         */
        public Builder bitmapConfig(Bitmap.Config bitmapConfig) {
            if (bitmapConfig == null) throw new IllegalArgumentException("bitmapConfig can't be null");
            decodingOptions.inPreferredConfig = bitmapConfig;
            return this;
        }

        /**
         * 指定图片decode的option
         * @param decodingOptions
         * @return
         */
        public Builder decodingOptions(Options decodingOptions) {
            if (decodingOptions == null) throw new IllegalArgumentException("decodingOptions can't be null");
            this.decodingOptions = decodingOptions;
            return this;
        }

        /**
         * 延迟加载
         * @param delayInMillis
         * @return
         */
        public Builder delayBeforeLoading(int delayInMillis) {
            this.delayBeforeLoading = delayInMillis;
            return this;
        }

        /**
         * 是否检测图片的exif信息
         * @param considerExifParams
         * @return
         */
        public Builder considerExifParams(boolean considerExifParams) {
            this.considerExifParams = considerExifParams;
            return this;
        }

        /**
         * 设置图片处理
         * @param processor
         * @return
         */
        public Builder setProcessor(ImageWorkerPlugin processor) {
            this.processor = processor;
            return this;
        }

        /**
         * 自定义显示
         * @param displayer
         * @return
         */
        public Builder displayer(APDisplayer displayer) {
            this.displayer = displayer;
            return this;
        }

        /**
         * 是否同步加载
         * @param isSyncLoading
         * @return
         */
        public Builder syncLoading(boolean isSyncLoading) {
            this.isSyncLoading = isSyncLoading;
            return this;
        }

        /**
         * 图片原始尺寸
         * @param originalSize
         * @return
         */
        public Builder originalSize(Size originalSize) {
            this.originalSize = originalSize;
            return this;
        }

        /**
         * 指定图片宽
         * @param width
         * @return
         */
        public Builder width(Integer width) {
            this.width = width;
            return this;
        }

        /**
         * 指定图片高
         * @param height
         * @return
         */
        public Builder height(Integer height) {
            this.height = height;
            return this;
        }

        /**
         * 自定义网络加载线程池
         * @param service
         * @return
         */
        public Builder netloadExecutorService(ExecutorService service) {
            this.netloadExecutorService = service;
            return this;
        }

        /**
         * 自定义水印参数
         * @param imageMarkRequest
         * @return
         */
        public Builder imageMarkRequest(APImageMarkRequest imageMarkRequest) {
            this.imageMarkRequest = imageMarkRequest;
            return this;
        }

        /**
         * Sets all options equal to incoming options
         */
        public Builder cloneFrom(DisplayImageOptions options) {
            imageOnLoading = options.imageOnLoading;
            imageForEmptyUri = options.imageForEmptyUri;
            imageOnFail = options.imageOnFail;
            resetViewBeforeLoading = options.resetViewBeforeLoading;
            cacheInMemory = options.cacheInMemory;
            cacheOnDisk = options.cacheOnDisk;
            cutScaleType = options.cutScaleType;
            decodingOptions = options.decodingOptions;
            delayBeforeLoading = options.delayBeforeLoading;
            considerExifParams = options.considerExifParams;
            processor = options.processor;
            displayer = options.displayer;
            isSyncLoading = options.isSyncLoading;
            originalSize = options.originalSize;
            width = options.width;
            height = options.height;
            netloadExecutorService = options.netloadExecutorService;
            imageMarkRequest = options.imageMarkRequest;
            return this;
        }

        public DisplayImageOptions build() {
            return new DisplayImageOptions(this);
        }
    }

    /**
     * 创建默认Options
     * @return
     */
    public static DisplayImageOptions createSimple() {
        return new Builder().build();
    }

    @Override
    public String toString() {
        return "DisplayImageOptions["
                + "  width:" + width
                + ", height:" + height
                + ", imageOnLoading: " + imageOnLoading
                + ", imageForEmptyUri: " + imageForEmptyUri
                + ", imageOnFail: " + imageOnFail
                + ", resetViewBeforeLoading: " + resetViewBeforeLoading
                + ", cacheInMemory: " + cacheInMemory
                + ", cacheOnDisk: " + cacheOnDisk
                + ", cutScaleType:" + cutScaleType
                + ", decodingOptions:" + decodingOptions
                + ", delayBeforeLoading:" + delayBeforeLoading
                + ", considerExifParams:" + considerExifParams
                + ", processor:" + processor
                + ", displayer:" + displayer
                + ", isSyncLoading:" + isSyncLoading
                + ", originalSize:" + originalSize
                + ", netloadExecutorService:" + netloadExecutorService
                + ", imageMarkRequest:" + imageMarkRequest
                + "]";
    }
}
