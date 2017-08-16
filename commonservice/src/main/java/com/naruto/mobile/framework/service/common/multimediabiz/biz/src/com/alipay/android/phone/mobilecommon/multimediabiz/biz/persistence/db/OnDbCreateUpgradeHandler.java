package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.persistence.db;

//import com.alibaba.sqlcrypto.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.support.ConnectionSource;

/**
 * Db创建&升级处理
 * Created by jinmin on 15/4/15.
 */
public interface OnDbCreateUpgradeHandler {
    void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource);

    void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int oldVer, int newVer);
}
