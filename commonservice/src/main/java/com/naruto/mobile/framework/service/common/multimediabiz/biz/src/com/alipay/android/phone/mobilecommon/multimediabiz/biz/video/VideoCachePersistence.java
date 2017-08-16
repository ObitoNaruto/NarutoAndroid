package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.video;

import android.content.Context;
import com.j256.ormlite.stmt.QueryBuilder;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.persistence.db.DbPersistence;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Logger;

import java.sql.SQLException;
import java.util.List;

/**
 * 视频db持久化
 * Created by jinmin on 15/5/22.
 */
public class VideoCachePersistence extends DbPersistence<VideoCacheModel> {

    private Logger logger = Logger.getLogger("VideoCachePersistence");

    private static final int DB_VER = 1;

    public VideoCachePersistence(Context context) throws SQLException {
        super(context, VideoCacheModel.class, DB_VER);
    }

    /**
     * 查询时间间隔视频缓存信息
     * @param interval          至今时间间隔
     * @return
     * @throws SQLException
     */
    public List<VideoCacheModel> queryVideoCacheModelsByTimeInterval(long interval, boolean descOrder) throws SQLException {
        List<VideoCacheModel> models = null;
        QueryBuilder<VideoCacheModel, ?> builder = mDao.queryBuilder();
        builder.where().ge("create_time", System.currentTimeMillis()-interval).and().eq("deleteByUser", 0);
        builder.orderBy("create_time", !descOrder);
        models = builder.query();
        return models;
    }

    /**
     * 根据LocalId查询视频缓存
     * @param localId
     * @return
     */
    public VideoCacheModel queryVideoCacheByLocalId(String localId) {
        List<VideoCacheModel> models = queryAllVideoCacheModelsByLocalId(localId);
        return (models == null || models.isEmpty()) ? null : models.get(0);
    }

    public List<VideoCacheModel> queryAllVideoCacheModelsByLocalId(String localId) {
        List<VideoCacheModel> models = null;
        try {
            models = mDao.queryForEq("local_id", localId);
        } catch (SQLException e) {
            logger.e(e, "queryAllVideoCacheModelsByLocalId");
        }
        return models;
    }

    public VideoCacheModel queryVideoCacheByPath(String path) {
        List<VideoCacheModel> models = queryAllVideoCacheModelsByPath(path);
        return (models == null || models.isEmpty()) ? null : models.get(0);
    }
    
    public List<VideoCacheModel> queryAllVideoCacheModelsByPath(String path) {
        List<VideoCacheModel> models = null;
        try {
            models = mDao.queryForEq("path", path);
        } catch (SQLException e) {
            logger.e(e, "queryAllVideoCacheModelsByPath");
        }
        return models;
    }
    
    public VideoCacheModel queryVideoCacheByCloudId(String cloudId) {
        List<VideoCacheModel> models = queryAllVideoCacheModelsByCloudId(cloudId);
        return (models == null || models.isEmpty()) ? null : models.get(0);
    }

    public List<VideoCacheModel> queryAllVideoCacheModelsByCloudId(String cloudId) {
        List<VideoCacheModel> models = null;
        try {
            models = mDao.queryForEq("cloud_id", cloudId);
        } catch (SQLException e) {
            logger.e(e, "queryAllVideoCacheModelsByCloudId");
        }
        return models;
    }
}