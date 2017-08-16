package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.disc.db;

import android.content.Context;
import com.j256.ormlite.stmt.QueryBuilder;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.persistence.db.DbPersistence;

import java.sql.SQLException;
import java.util.List;

/**
 * 图片缓存db持久化
 * Created by jinmin on 15/5/22.
 */
public class ImageCachePersistence extends DbPersistence<ImageCacheModel> {

    private static final int DB_VER = 4;

    public ImageCachePersistence(Context context) throws SQLException {
        super(context, ImageCacheModel.class, DB_VER);
    }

    public void deleteByCacheKey(String cacheKey) throws Exception {
        List<ImageCacheModel> models = queryForEq(ImageCacheModel.class, "cache_key", cacheKey);
        delete(models);
    }

    public List<ImageCacheModel> queryAllImageCacheModels(String srcPath) throws SQLException {
        List<ImageCacheModel> models = null;
        QueryBuilder<ImageCacheModel, ?> builder = mDao.queryBuilder();
        builder.where().eq("src_path", srcPath);
        builder.orderBy("pixels", false);
        models = builder.query();
        return models;
    }
}
