package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.manager;

import android.content.Context;

import java.sql.SQLException;

import com.naruto.mobile.framework.service.common.multimedia.api.data.APMultimediaTaskModel;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.persistence.db.DbPersistence;

/**
 * 多媒体任务存储
 * Created by jinmin on 15/4/14.
 */
public class MultiMediaTaskPersistence extends DbPersistence<APMultimediaTaskModel> {

    private static final String DATABASE_NAME = "MultiMediaTask.db";
    private static final int DATABASE_VERSION = 1;

    public MultiMediaTaskPersistence(Context context) throws SQLException {
        super(context, APMultimediaTaskModel.class, DATABASE_NAME, DATABASE_VERSION);
    }


}
