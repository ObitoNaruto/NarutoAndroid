package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.persistence.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
//import com.alibaba.sqlcrypto.sqlite.SQLiteDatabase;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.persistence.Persistence;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.persistence.condition.Condition;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Logger;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * sqlite 持久化
 * Created by jinmin on 15/4/11.
 */
public class DbPersistence<T> implements Persistence<T> {

    private static final String TAG = "DbPersistence";

    private DbHelper mDbHelper;
    protected Dao<T, String> mDao;

    public DbPersistence(Context context, final Class<T> clazz, String dbName, int dbVersion) throws SQLException {
        mDbHelper = new DbHelper(context, dbName, dbVersion, new OnDbCreateUpgradeHandler() {
            @Override
            public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
                try {
                    TableUtils.createTable(connectionSource, clazz);
                } catch (SQLException e) {
                    Logger.E(TAG, e, "DbHelper onCreate error");
                }
            }

            @Override
            public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int oldVer,
                    int newVer) {
                try {
                    TableUtils.dropTable(connectionSource, clazz, true);
                    onCreate(sqLiteDatabase, connectionSource);
                } catch (SQLException e) {
                    Logger.E(TAG, e, "DbHelper onUpgrade error");
                }
            }
        });
        mDao = mDbHelper.getDao(clazz);
    }

    public DbPersistence(Context context, Class<T> clazz) throws SQLException {
        this(context, clazz, "multimedia.db", 1);
    }

    public DbPersistence(Context context, Class<T> clazz, int dbVer) throws SQLException {
        this(context, clazz, "multimedia.db", dbVer);
    }

    @Override
    public T save(T obj) throws SQLException {
        mDao.createOrUpdate(obj);
        return obj;
    }

    @Override
    public List<T> save(final List<T> objects) throws SQLException {
        TransactionManager.callInTransaction(mDbHelper.getConnectionSource(), new Callable<List<T>>() {
            @Override
            public List<T> call() throws Exception {
                for (T obj : objects) {
                    mDao.createOrUpdate(obj);
                }
                return objects;
            }
        });
        return objects;
    }

    @Override
    public T[] save(final T... objects) throws SQLException {
        TransactionManager.callInTransaction(mDbHelper.getConnectionSource(), new Callable<T[]>() {
            @Override
            public T[] call() throws Exception {
                for (T obj : objects) {
                    mDao.createOrUpdate(obj);
                }
                return objects;
            }
        });
        return objects;
    }

    @Override
    public T add(T obj) throws SQLException {
        mDao.create(obj);
        return obj;
    }

    @Override
    public List<T> add(final List<T> objects) throws SQLException {
        TransactionManager.callInTransaction(mDbHelper.getConnectionSource(), new Callable<List<T>>() {
            @Override
            public List<T> call() throws Exception {
                for (T obj : objects) {
                    mDao.create(obj);
                }
                return objects;
            }
        });
        return objects;
    }

    @Override
    public T[] add(final T... objects) throws SQLException {
        TransactionManager.callInTransaction(mDbHelper.getConnectionSource(), new Callable<T[]>() {
            @Override
            public T[] call() throws Exception {
                for (T obj : objects) {
                    mDao.create(obj);
                }
                return objects;
            }
        });
        return objects;
    }

    @Override
    public T update(T obj, String... fields) throws SQLException {
        mDao.update(obj);
        return obj;
    }

    @Override
    public List<T> update(final List<T> objects, String... fields) throws SQLException {
        TransactionManager.callInTransaction(mDbHelper.getConnectionSource(), new Callable<List<T>>() {
            @Override
            public List<T> call() throws Exception {
                for (T obj : objects) {
                    mDao.update(obj);
                }
                return objects;
            }
        });
        return objects;
    }

    @Override
    public List<T> update(final List<T> objects, List<Condition> conditions, String... fields) throws SQLException {
        throw new RuntimeException("interface have not implement!");
//        TransactionManager.callInTransaction(mDbHelper.getConnectionSource(), new Callable<List<T>>() {
//            @Override
//            public List<T> call() throws Exception {
//                //todo：Condition到Ormlite Condition切换
//                mDao.updateBuilder().update();
//                return objects;
//            }
//        });
//        return objects;
    }

    @Override
    public T delete(Class<T> clazz, String id) throws Exception {
        T obj = query(clazz, id);
        if (obj != null) {
            mDao.deleteById(id);
        }
        return obj;
    }

    @Override
    public T delete(T obj) throws SQLException {
        mDao.delete(obj);
        return obj;
    }

    @Override
    public T[] delete(final T... objects) throws SQLException {
        TransactionManager.callInTransaction(mDbHelper.getConnectionSource(), new Callable<T[]>() {
            @Override
            public T[] call() throws Exception {
                for (T obj : objects) {
                    mDao.delete(obj);
                }
                return objects;
            }
        });
        return objects;
    }

    @Override
    public List<T> delete(final List<T> objects) throws SQLException {
        TransactionManager.callInTransaction(mDbHelper.getConnectionSource(), new Callable<List<T>>() {
            @Override
            public List<T> call() throws Exception {
                for (T obj : objects) {
                    mDao.delete(obj);
                }
                return objects;
            }
        });
        return objects;
    }

    @Override
    public T query(Class<T> clazz, String id) throws SQLException {
        T result = mDao.queryForId(id);
        return result;
    }

    @Override
    public List<T> queryForEq(Class<T> clazz, String fieldName, String value) throws Exception {
        SelectArg selectArg = new SelectArg(value);
        return mDao.queryForEq(fieldName, selectArg);
    }

    @Override
    public List<T> query(Class<T> clazz, List<Condition> conditions) throws SQLException {
        throw new RuntimeException("interface have not implement!");
//        //todo 还有问题需要fix
//        List<T> result = null;
//        QueryBuilder<T, String> builder = mDao.queryBuilder();
//        Where where = builder.where();
//        StringBuilder sb = new StringBuilder();
//        for (Condition condition : conditions) {
//            sb.append(OrmliteConditionConvert.convert2String(condition));
//        }
//        where.raw(sb.toString());
//        result = builder.query();
//        return result;
    }

    @Override
    public List<T> queryAll(Class<T> clazz) throws Exception {
        return mDao.queryForAll();
    }

    @Override
    public QueryBuilder<T, String> queryBuilder() {
        return mDao.queryBuilder();
    }

    @Override
    public UpdateBuilder<T, String> updateBuilder() {
        return mDao.updateBuilder();
    }

    @Override
    public DeleteBuilder<T, String> deleteBuilder() {
        return mDao.deleteBuilder();
    }

    @Override
    public long countOf() throws SQLException {
        return mDao.countOf();
    }
}
