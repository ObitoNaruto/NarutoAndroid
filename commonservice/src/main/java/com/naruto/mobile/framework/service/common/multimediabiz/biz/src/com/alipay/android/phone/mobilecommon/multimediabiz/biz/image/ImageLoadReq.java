package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image;

import android.text.TextUtils;
import android.widget.ImageView;

import com.naruto.mobile.framework.service.common.multimedia.api.data.APMultimediaTaskModel;
import com.naruto.mobile.framework.service.common.multimedia.graphics.APImageDownLoadCallback;
import com.naruto.mobile.framework.service.common.multimedia.graphics.data.APImageDownloadRsp;
import com.naruto.mobile.framework.service.common.multimedia.graphics.data.CutScaleType;
import com.naruto.mobile.framework.service.common.multimedia.graphics.load.DisplayImageOptions;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.CacheUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.ImageUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.PathUtils;

/**
 * 图片加载任务请求包装
 * Created by jinmin on 15/7/13.
 */
public class ImageLoadReq {
    public ImageLoadEngine loadEngine;
    //缓存本地时的key
    public String path;
    //图片路径
    public String source;
    public byte[] data;
    public ImageView imageView;
    public APImageDownLoadCallback downLoadCallback;
    public DisplayImageOptions options;
    //缓存key
    public String cacheKey;
    public APMultimediaTaskModel taskModel;

    public APImageDownloadRsp downloadRsp;

    private String reqId;

    public boolean skipDisplay = false;


    // 测试数据统计
    //开始时间
    public long startTime;

    public ImageLoadReq() {
    }

    public ImageLoadReq(ImageLoadEngine engine, String source, ImageView imageView,
                        APImageDownLoadCallback downLoadCallback, DisplayImageOptions options) {
        this(engine, imageView, downLoadCallback, options);
        this.source = source;
        //有一套适合业务的生成规则，需要客户端和服务端以及服务器共同制定规则
        this.path = PathUtils.preferImageUrl(PathUtils.extractPath(source), options.getWidth(), options.getHeight());
        this.cacheKey = makeCacheKey();
        this.reqId = genId();

        this.downloadRsp.setCacheId(cacheKey);
        this.downloadRsp.setSourcePath(source);
    }

    public ImageLoadReq(ImageLoadEngine engine, byte[] data, ImageView imageView,
                        APImageDownLoadCallback downLoadCallback, DisplayImageOptions options) {
        this(engine, imageView, downLoadCallback, options);
        this.data = data;
        this.path = PathUtils.preferImageUrl(PathUtils.extractPath(source), options.getWidth(), options.getHeight());
        this.cacheKey = makeCacheKey();
        this.downloadRsp.setCacheId(cacheKey);
        this.reqId = genId();
    }

    public ImageLoadReq(ImageLoadEngine engine, ImageView imageView,
                        APImageDownLoadCallback downLoadCallback, DisplayImageOptions options) {
        this.loadEngine = engine;
        this.imageView = imageView;
        this.downLoadCallback = downLoadCallback;
        this.options = correctOptions(options);

        this.downloadRsp = new APImageDownloadRsp();
    }

    private DisplayImageOptions correctOptions(DisplayImageOptions options) {
        if (options.getCutScaleType() != CutScaleType.KEEP_RATIO || options.getOriginalSize() == null ) {
            return options;
        }
        DisplayImageOptions.Builder b = new DisplayImageOptions.Builder().cloneFrom(options);
        CutScaleType type = ImageUtils.calcCutScaleType(options.getOriginalSize(), Math.max(options.getWidth(), options.getHeight()));
        return b.imageScaleType(type).build();
    }

    protected String makeCacheKey() {
        return CacheUtils.makeImageCacheKey(options.getProcessor(), this.path,
                options.getWidth(), options.getHeight(), options.getCutScaleType(), options.getImageMarkRequest());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImageLoadReq loadReq = (ImageLoadReq) o;

        return loadReq.reqId.equals(reqId);

    }

    @Override
    public int hashCode() {
        int result = loadEngine != null ? loadEngine.hashCode() : 0;
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + (source != null ? source.hashCode() : 0);
        result = 31 * result + (imageView != null ? imageView.hashCode() : 0);
        result = 31 * result + (downLoadCallback != null ? downLoadCallback.hashCode() : 0);
        result = 31 * result + (options != null ? options.hashCode() : 0);
        result = 31 * result + (cacheKey != null ? cacheKey.hashCode() : 0);
        result = 31 * result + (taskModel != null ? taskModel.hashCode() : 0);
        result = 31 * result + (downloadRsp != null ? downloadRsp.hashCode() : 0);
        return result;
    }

    private String genId() {
        StringBuffer sb = new StringBuffer();
        sb.append(System.identityHashCode(imageView));
        sb.append("##").append(System.identityHashCode(downLoadCallback));
        sb.append("##").append(System.identityHashCode(cacheKey));
        return sb.toString();
    }

    public ImageLoadReq updateDisplayOptions(DisplayImageOptions options) {
        this.options = options;
        this.cacheKey = makeCacheKey();
        this.downloadRsp.setCacheId(this.cacheKey);
        this.reqId = genId();
        return this;
    }

    public ImageLoadReq clone() {
        ImageLoadReq loadReq = new ImageLoadReq();
        loadReq.source = source;
        loadReq.path = path;
        loadReq.options = options;
        loadReq.taskModel = taskModel;
        loadReq.cacheKey = cacheKey;
        loadReq.data = data;
        loadReq.downLoadCallback = downLoadCallback;
        loadReq.reqId = reqId;
        loadReq.downloadRsp = downloadRsp;
        loadReq.imageView = imageView;
        return loadReq;
    }



    @Override
    public String toString() {
        return "ImageLoadReq{" +
                "loadEngine=" + loadEngine +
                ", path='" + path + '\'' +
                ", source='" + source + '\'' +
                ", imageView=" + System.identityHashCode(imageView) +
                ", downLoadCallback=" + downLoadCallback +
                ", options=" + options +
                ", cacheKey='" + cacheKey + '\'' +
                '}';
    }
}
