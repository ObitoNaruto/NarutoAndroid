package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.persistence.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
//import com.alibaba.sqlcrypto.sqlite.SQLiteDatabase;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;

import java.io.File;
import java.io.InputStream;

/**
 * DbHelper
 * Created by jinmin on 15/4/15.
 */
public class DbHelper extends OrmLiteSqliteOpenHelper{
    private static final String TAG = DbHelper.class.getSimpleName();

    private OnDbCreateUpgradeHandler mHandler;

    public DbHelper(Context context, String databaseName, int databaseVersion) {
        this(context, databaseName, databaseVersion, null);
    }

    public DbHelper(Context context, String databaseName, int databaseVersion, OnDbCreateUpgradeHandler handler) {
        this(context, databaseName, null, databaseVersion);
        this.mHandler = handler;
    }

    private DbHelper(Context context, String databaseName, SQLiteDatabase.CursorFactory factory, int databaseVersion) {
        super(context, databaseName, factory, databaseVersion);
    }

    private DbHelper(Context context, String databaseName, SQLiteDatabase.CursorFactory factory, int databaseVersion, int configFileId) {
        super(context, databaseName, factory, databaseVersion, configFileId);
    }

    private DbHelper(Context context, String databaseName, SQLiteDatabase.CursorFactory factory, int databaseVersion, File configFile) {
        super(context, databaseName, factory, databaseVersion, configFile);
    }

    private DbHelper(Context context, String databaseName, SQLiteDatabase.CursorFactory factory, int databaseVersion, InputStream stream) {
        super(context, databaseName, factory, databaseVersion, stream);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        if (mHandler != null) {
            mHandler.onCreate(sqLiteDatabase, connectionSource);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int oldVer, int newVer) {
        if (mHandler != null) {
            mHandler.onUpgrade(sqLiteDatabase, connectionSource, oldVer, newVer);
        }
    }


    @Override
    public void onConfigure(SQLiteDatabase sqLiteDatabase) {

    }

}
