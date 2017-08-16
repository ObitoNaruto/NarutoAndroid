package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.disc;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

import java.io.File;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import com.naruto.mobile.framework.service.common.multimedia.graphics.data.APImageBigQuery;
import com.naruto.mobile.framework.service.common.multimedia.graphics.data.APImageCacheQuery;
import com.naruto.mobile.framework.service.common.multimedia.graphics.data.APImageClearCacheQuery;
import com.naruto.mobile.framework.service.common.multimedia.graphics.data.APImageOriginalQuery;
import com.naruto.mobile.framework.service.common.multimedia.graphics.data.APImageQueryResult;
import com.naruto.mobile.framework.service.common.multimedia.graphics.data.APImageSourceCutQuery;
import com.naruto.mobile.framework.service.common.multimedia.graphics.data.APImageThumbnailQuery;
import com.naruto.mobile.framework.service.common.multimedia.graphics.data.APImageUpRequest;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.ImageCacheContext;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.disc.db.ImageCacheModel;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.disc.db.ImageCachePersistence;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.disc.impl.BaseDiskCache;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.util.DjangoConstant;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.AppUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.CacheUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.FileUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.ImageUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Logger;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.PathUtils;

/**
 * 图片缓存管理
 * Created by jinmin on 15/5/22.
 */
public class ImageCacheManager {

    private static final String TAG = "ImageCacheManager";

    private Logger logger = Logger.getLogger(TAG);

    private Context mContext;

    //    private ImageCachePersistence mCachePersistence;
    public ImageCacheManager() {
        mContext = AppUtils.getApplicationContext();
    }

    private ImageCachePersistence getCachePersistence() {
        return ((BaseDiskCache) ImageCacheContext.get().getDiskCache()).getCachePersistence();
    }

    /**
     * 查找指定尺寸的cache图片文件
     *
     * @param query
     * @return
     */
    public APImageQueryResult<APImageCacheQuery> queryImageFor(APImageCacheQuery query) {
        APImageQueryResult<APImageCacheQuery> result = new APImageQueryResult<APImageCacheQuery>();
        result.success = false;
        result.query = query;
        if (query != null && getDiskCache() != null) {
            String key = CacheUtils.makeImageCacheKey(query.plugin, query.path, query.width, query.height,
                    query.cutScaleType, null); //TODO: 增加水印参数
            File cacheFile = getDiskCache().getFile(key);
            if (CacheUtils.checkCacheFile(cacheFile)) {
                result.success = true;
                result.width = query.width;
                result.height = query.height;
                result.path = cacheFile.getAbsolutePath();
            }
        }
        return result;
    }

    private DiskCache<Bitmap> getDiskCache() {
        return ImageCacheContext.get().getDiskCache();
    }

    /**
     * 查询原图
     *
     * @param query
     * @return
     */
    public APImageQueryResult<APImageOriginalQuery> queryImageFor(APImageOriginalQuery query) {
        APImageQueryResult<APImageOriginalQuery> result = new APImageQueryResult<APImageOriginalQuery>();
        result.success = false;
        result.query = query;
        if (query != null) {
            String path = "";
            if (!TextUtils.isEmpty(query.path)) {
                try {
                    query.path = PathUtils.extractPath(query.path);
                    //先从本地映射关系查找
                    String refPath = ImageCacheContext.get().getRefPath(query.path);
                    if (!TextUtils.isEmpty(refPath) && CacheUtils.checkCacheFile(refPath)) {
                        path = refPath;
                    } else {//本地映射关系有问题，从自身关系查找
                        File storeFile = new File(query.path);
                        if (storeFile.exists()) {
                            path = storeFile.getAbsolutePath();
                        } else {
                            String dir = FileUtils.getMediaDir(DjangoConstant.IMAGE_PATH);
                            String fileName = CacheUtils.makeCacheFileName(query.path);
                            logger.d("getOriginalImagePath fileName=" + fileName);
                            storeFile = new File(dir, fileName);
                            if (storeFile.exists()) {
                                path = storeFile.getAbsolutePath();
                            }
                        }

                        //校验文件长度,长度为0的，认为是无效文件
                        if (!CacheUtils.checkCacheFile(storeFile)) {
                            logger.d("getOriginalImagePath old del: " + storeFile + ", len: " + storeFile.length());
                            //不再del，防止多线程操作导致，刚下载好的文件被删除
//                        boolean delete = storeFile.delete();
//                        if (!delete) logger.d("getOriginalImagePath fail del: " + storeFile);
                            path = "";
                        }
                    }
                } catch (Exception e) {
                    logger.d("getOriginalImagePath error=" + e.toString());
                }
            }
            if (!TextUtils.isEmpty(path)) {
                result.success = true;
                result.path = path;
                int[] wh = ImageUtils.calculateDesWidthHeight(path);
                if (wh != null) {
                    result.width = wh[0];
                    result.height = wh[1];
                }
            }
        }
        return result;
    }

    /**
     * 查询大图
     * @param query
     * @return
     */
    public APImageQueryResult<APImageBigQuery> queryImageFor(APImageBigQuery query) {
        APImageQueryResult<APImageBigQuery> result = new APImageQueryResult<APImageBigQuery>();
        result.success = false;
        result.query = query;
        if (query != null) {
            //step 1: 查询尺寸为 0 0
            APImageCacheQuery cacheQuery = new APImageCacheQuery(query.path, 0, 0);
            APImageQueryResult<APImageCacheQuery> cacheQueryResult = queryImageFor(cacheQuery);
            if (!cacheQueryResult.success) {
                //step 2: 查询尺寸为 1280 1280的
                cacheQuery.width = APImageUpRequest.DEFAULT_UP_W;
                cacheQuery.height = APImageUpRequest.DEFAULT_UP_H;
                cacheQueryResult = queryImageFor(cacheQuery);
            }
            //结果判定
            if (cacheQueryResult.success) {
                result.success = true;
                result.path = cacheQueryResult.path;
                result.width = cacheQueryResult.width;
                result.height = cacheQueryResult.height;
            }
        }
        return result;
    }

    public APImageQueryResult<APImageClearCacheQuery> queryImageFor(APImageClearCacheQuery query) {
        APImageQueryResult<APImageClearCacheQuery> result = new APImageQueryResult<APImageClearCacheQuery>();
        result.success = false;
        result.query = query;
        if (query != null && !TextUtils.isEmpty(query.path)) {
            //step 1:先查原图
            APImageOriginalQuery originalQuery = new APImageOriginalQuery(query.path);
            APImageQueryResult queryResult = queryImageFor(originalQuery);
            logger.d("queryClearCacheImage queryOriginal result: " + queryResult);
            if (!queryResult.success) {
                //step 2:查询大图
                APImageBigQuery bigQuery = new APImageBigQuery(query.path);
                queryResult = queryImageFor(bigQuery);
                logger.d("queryClearCacheImage queryBig result: " + queryResult);
            }

            if (!queryResult.success && getCachePersistence() != null && getDiskCache() != null) {
                //step 3:获取缓存图片的文件
                try {
                    List<ImageCacheModel> models = getCachePersistence().queryAllImageCacheModels(query.path);
                    if (models != null) {
                        logger.d("queryClearCacheImage queryCache result: " + models.size());
                        for (ImageCacheModel model : models) {
                            if (!TextUtils.isEmpty(model.cacheKey)) {
                                File file = getDiskCache().getFile(model.cacheKey);
                                logger.d("queryClearCacheImage cacheKey refer to file, key: " + model.cacheKey + ", f: " + file + ", len: " + (file == null ? 0 : file.length()) );
                                if (CacheUtils.checkCacheFile(file)) {
                                    result.success = true;
                                    result.path = model.path;
                                    result.width = model.width;
                                    result.height = model.height;
                                    break;
                                }
                            }
                        }
                    }
                } catch (SQLException e) {
                    logger.e(e, "queryClearCacheImage");
                }
            }

            if (queryResult.success) {
                result.success = true;
                result.path = queryResult.path;
                result.width = queryResult.width;
                result.height = queryResult.height;
            } else {
                String dir = FileUtils.getMediaDir(DjangoConstant.IMAGE_PATH);
                File originalDir = new File(dir);
                File[] files = originalDir.listFiles();
                for (File f : files) {
                    logger.d("queryClearCache fail, list f: " + f + ", lastModified: " + new Date(f.lastModified()));
                }
            }

        }
        logger.d("queryClearCacheImage result: " + result);
        return result;
    }

    public APImageQueryResult<APImageSourceCutQuery> queryImageFor(APImageSourceCutQuery query) {
        APImageQueryResult<APImageSourceCutQuery> result = new APImageQueryResult<APImageSourceCutQuery>();
        result.success = false;
        result.query = query;
        if (query != null && !TextUtils.isEmpty(query.path)) {
            //先查询大图，裁切更快
            APImageBigQuery bigQuery = new APImageBigQuery(query.path);
            APImageQueryResult queryResult = queryImageFor(bigQuery);
            if (!queryResult.success) {
                //大图不存在，查询原图
                APImageOriginalQuery originalQuery = new APImageOriginalQuery(query.path);
                queryResult = queryImageFor(originalQuery);
            }
            if (queryResult.success) {
                result.success = true;
                result.path = queryResult.path;
                result.width = queryResult.width;
                result.height = queryResult.height;
            }
        }
        return result;
    }

    public APImageQueryResult<APImageThumbnailQuery> queryImageFor(APImageThumbnailQuery query) {
        APImageQueryResult<APImageThumbnailQuery> result = new APImageQueryResult<APImageThumbnailQuery>();
        result.success = false;
        result.query = query;
        if (query != null && !TextUtils.isEmpty(query.path)) {
            String path = PathUtils.extractPath(query.path);
            //先查询自身对应的缩略图
            try {
                ImageCacheModel resultModel = null;
                List<ImageCacheModel> imageCacheModels = getCachePersistence().queryAllImageCacheModels(path);
                ImageCacheModel cloudCacheModel = findMatchThumbnailCacheModel(imageCacheModels);
                ImageCacheModel localCacheModel = null;
                String localPath = ImageCacheContext.get().getRefPath(path);
                if (!TextUtils.isEmpty(localPath)) {
//                    String localPath = ImageCacheContext.get().getClouldToLocalMap().get(path);
                    if (!TextUtils.isEmpty(localPath)) {
                        imageCacheModels = getCachePersistence().queryAllImageCacheModels(localPath);
                        localCacheModel = findMatchThumbnailCacheModel(imageCacheModels);
                    }
                }
                logger.d("APImageThumbnailQuery query: %s, cloudCache: %s, localCache: %s",
                        query, cloudCacheModel, localCacheModel);
                //都存在，把最小的那个返回
                if (cloudCacheModel != null && localCacheModel != null) {
                    double cloudValue = calcCacheModelSizeValue(cloudCacheModel);
                    double localValue = calcCacheModelSizeValue(localCacheModel);
                    resultModel = cloudValue < localValue ? cloudCacheModel : localCacheModel;
                } else if (cloudCacheModel == null) {
                    resultModel = localCacheModel;
                } else {
                    resultModel = cloudCacheModel;
                }
                if (resultModel != null) {
                    result.success = true;
                    result.path = resultModel.path;
                    result.width = resultModel.width;
                    result.height = resultModel.height;
                }
            } catch (SQLException e) {
                logger.e(e, "APImageThumbnailQuery query: %s", query);
            }
        }
        logger.d("APImageThumbnailQuery query:%s, result: %s", query, result);
        return result;
    }

    private ImageCacheModel findMatchThumbnailCacheModel(List<ImageCacheModel> cacheModels) {
        if (cacheModels != null && !cacheModels.isEmpty()) {
            for (int i = cacheModels.size() - 1; i >= 0; i--) {
                ImageCacheModel m = cacheModels.get(i);
                if ((m.width > 0 && m.height > 0) && (m.width < Integer.MAX_VALUE && m.height < Integer.MAX_VALUE)
                        && CacheUtils.checkCacheFile(m.path)) {
                    return m;
                }
            }
        }
        return null;
    }

    private double calcCacheModelSizeValue(ImageCacheModel model) {
        if (model != null) {
            return model.pixels;
        }
        return Integer.MAX_VALUE;
    }


    public int deleteCache(String path) {
        int deleteCount = 0;
        if (!TextUtils.isEmpty(path)) {
            //查询原图是否存在
            APImageOriginalQuery originalQuery = new APImageOriginalQuery(path);
            APImageQueryResult result = queryImageFor(originalQuery);
            if (result.success) {
                if (FileUtils.delete(result.path)) {
                    deleteCount++;
                }
            }
            //查询cache图片
            try {
                List<ImageCacheModel> models = getCachePersistence().queryAllImageCacheModels(path);
                for (ImageCacheModel m : models) {
                    if (!TextUtils.isEmpty(m.cacheKey) && getDiskCache().remove(m.cacheKey)) {
                        ImageCacheContext.get().getMemCache().remove(m.cacheKey);
                        deleteCount++;
                    }
                }
            } catch (SQLException e) {
                logger.e(e, "deleteCache error: " + path);
            }
        }
        logger.d("deleteCache for: " + path + ", deleted: " + deleteCount);
        return deleteCount;
    }
}
