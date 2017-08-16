package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.task;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import com.naruto.mobile.framework.service.common.multimedia.graphics.data.APImageOriginalQuery;
import com.naruto.mobile.framework.service.common.multimedia.graphics.data.APImageQueryResult;
import com.naruto.mobile.framework.service.common.multimedia.graphics.data.APImageRetMsg;
import com.naruto.mobile.framework.service.common.multimedia.graphics.data.APImageSourceCutQuery;
import com.naruto.mobile.framework.service.common.multimedia.graphics.data.CutScaleType;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.BitmapCacheLoader;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.ImageCacheContext;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.disc.ImageCacheManager;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.io.IOUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.ImageLoadReq;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.assist.FalconFacade;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.assist.ViewWrapper;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.CommonUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.CompareUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.FileUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.ImageUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Logger;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.PathUtils;


/**
 * 本地加载任务
 */
public class ImageLocalTask extends ImageTask {

    private Logger logger = Logger.getLogger("ImageLocalTask");

    public ImageLocalTask(ImageLoadReq loadReq, ViewWrapper wrapper) {
        super(loadReq, wrapper);
    }

    @Override
    public Object call() throws Exception {
        long start = System.currentTimeMillis();
        Bitmap bitmap = null;
        BitmapCacheLoader cacheLoader = getCacheLoader();
        if (isAssetTask()) {
            //asset文件加载
            bitmap = fromAsset();
        } else if (options.getImageMarkRequest() == null) {
            //水印不本地裁图
            //本地裁图
            bitmap = fromLocal();
        }
        //加载图片后统一处理，交业务自行process或缩放至目标尺寸
        bitmap = processOrZoom(bitmap);
        //有效图片，添加到缓存
        if (ImageUtils.checkBitmap(bitmap)) {
//            bitmap = processBitmap(bitmap);
            long start1 = System.currentTimeMillis();
            cacheLoader.put(loadReq.cacheKey, bitmap);
            Logger.TIME("ImageLocalTask call cacheLoader.put costTime: "
                    + (System.currentTimeMillis() - start1) + ", " + loadReq.path);
            display(bitmap, loadReq, viewWrapper);
            logger.p("loadFrom local " + loadReq.path);
        } else {//本地均无，从网络下载(此处暂不考虑使用工厂模式，生成不同netTask)
            if (checkImageViewReused()) {
                notifyReuse();
                return null;
            }
            Uri uri = Uri.parse(loadReq.source);
            boolean hasNetwork = CommonUtils.isActiveNetwork(mContext);
            if (hasNetwork && !isAssetTask() && !PathUtils.isLocalFile(uri)) {
                ImageNetTask netTask;
                if (PathUtils.isHttp(uri)) {
                    netTask = new ImageUrlTask(loadReq, viewWrapper);
                } else if (CutScaleType.NONE.equals(options.getCutScaleType())) {
                    netTask = new ImageDjangoOriginalTask(loadReq, viewWrapper);
                } else {
                    netTask = new ImageDjangoTask(loadReq, viewWrapper);
                }
                ImageTaskEngine.get().submit(netTask);
                logger.p("loadFrom network " + loadReq.path);
            } else {
                if (hasNetwork) {
                    notifyError(loadReq, APImageRetMsg.RETCODE.FILE_NOT_EXIST,
                            loadReq.source + " maybe not exist", null);
                } else {
                    notifyError(loadReq, APImageRetMsg.RETCODE.INVALID_NETWORK,
                            loadReq.source + " invalid network", null);
                }
            }
        }
        Logger.TIME("ImageLocalTask call costTime: "
                + (System.currentTimeMillis() - start) + ", " + loadReq.path);
        return null;
    }

    private boolean isAssetTask() {
        return PathUtils.isAlipayAssetsFile(loadReq.source);
    }

    private Bitmap fromAsset() {
        AssetManager am = mContext.getAssets();
        if (am != null) {
            InputStream in = null;
            try {
                in = am.open(loadReq.path);
                Bitmap bitmap = ImageUtils.decodeBitmap(in);
                if (loadReq.options.shouldProcess()) {
                    bitmap = loadReq.options.getProcessor().process(loadReq.taskModel, bitmap);
                }
                return bitmap;
            } catch (IOException e) {
                logger.e(e, "fromAssets error");
            } finally {
                IOUtils.closeQuietly(in);
            }
        }
        return null;
    }

    /**
     * 从本地加载
     * 1、判断是否为本地文件
     * 2、判断是否存在可裁的大图文件
     * @return
     */
    private Bitmap fromLocal() {
        long start = System.currentTimeMillis();
        ImageCacheManager cacheManager = getCacheManager();
        String sourcePath = ImageCacheContext.get().getRefPath(loadReq.path);
        if (TextUtils.isEmpty(sourcePath)) sourcePath = loadReq.path;
        logger.d("from local start... sourcePath: " + sourcePath);
        if (!FileUtils.checkFile(sourcePath) && !PathUtils.isLocalFile(Uri.parse(loadReq.source))) {
            APImageQueryResult result;
            if (options.getImageMarkRequest() != null) {//带水印图不从本地查询
                result = new APImageQueryResult();
                result.success = false;
            } else if (CompareUtils.in(options.getCutScaleType(), CutScaleType.NONE, CutScaleType.CENTER_CROP)) {//原图和裁切图只能从原图裁
                APImageOriginalQuery query = new APImageOriginalQuery(sourcePath);
                result = cacheManager.queryImageFor(query);
            } else {
                APImageSourceCutQuery query = new APImageSourceCutQuery(sourcePath);
                result = cacheManager.queryImageFor(query);
            }
            if (result.success) {
                sourcePath = result.path;
            }
        }
        Logger.TIME("fromLocal queryImageFor costTime: "
                + (System.currentTimeMillis() - start) + ", " + loadReq.path);

        start = System.currentTimeMillis();
        Bitmap bitmap = null;
        int[] fitSize = getFitSize(options.getWidth(), options.getHeight());
        logger.d("from local fitSize: " + Arrays.toString(fitSize));
        Logger.TIME("fromLocal getFitSize costTime: "
                + (System.currentTimeMillis() - start) + ", " + loadReq.path);
        if (FileUtils.checkFile(sourcePath)) {
            FalconFacade facade = FalconFacade.get();
            File sourceFile = new File(sourcePath);
            if (CutScaleType.CENTER_CROP.equals(options.getCutScaleType())){
                try {
                    bitmap = facade.cutImage(sourceFile, options.getWidth(), options.getHeight(), 0.5f);
                    logger.p("fromLocal cutImage, width: " + bitmap.getWidth() + ", height: " + bitmap.getHeight() + ", req: " + loadReq.cacheKey);
                } catch (IOException e) {
                    logger.e(e, "fromLocal err, info: " + loadReq);
                }
            } else {
                try {
                    long start1 = System.currentTimeMillis();
                    bitmap = facade.cutImageKeepRatio(sourceFile, fitSize[0], fitSize[1]);
                    if (System.currentTimeMillis()-start1 > 300) {
                        Logger.TIME("fromLocal cutImageKeepRatio costTime: "
                                + (System.currentTimeMillis() - start1) + ", " + loadReq.path);
                    } else {
                        Logger.P("CostTime", "fromLocal cutImageKeepRatio costTime: "
                                + (System.currentTimeMillis() - start1) + ", " + loadReq.path);
                    }
                } catch (Exception e) {
                    logger.e(e, "fromLocal err, info: " + loadReq );
                }
            }
        }
        Logger.TIME("fromLocal cutImage costTime: "
                + (System.currentTimeMillis() - start) + ", " + loadReq.path);
        return bitmap;
    }

    private Bitmap processOrZoom(Bitmap bitmap) {
        if (ImageUtils.checkBitmap(bitmap)) {

            bitmap = processBitmap(bitmap);
            if (ImageUtils.checkBitmap(bitmap) &&
                    !options.shouldProcess() &&
                    isNeedZoom(loadReq)) {
                int[] fitSize = getFitSize(options.getWidth(), options.getHeight());
                logger.p("processOrZoom fitSize: " + Arrays.toString(fitSize) + ", req: " + loadReq);
                bitmap = ImageUtils.zoomBitmap(bitmap, fitSize[0], fitSize[1]);
            }
        }
        return bitmap;
    }
}
