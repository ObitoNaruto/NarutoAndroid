package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.video;

import android.text.TextUtils;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "tbl_video_cache")
public class VideoCacheModel
{
	/**
     * 自增长id
     */
    @DatabaseField(generatedId = true)
    public int id;

    @DatabaseField(columnName = "path", defaultValue = "")
    public String path;

    @DatabaseField(columnName = "cloud_id", defaultValue = "")
    public String cloudId;

    @DatabaseField(columnName = "local_id", defaultValue = "")
    public String localId;
    
    @DatabaseField(columnName = "deleteByUser", defaultValue = "0")
    public int deleteByUser;

    @DatabaseField(columnName = "create_time", defaultValue = "0")
    public long createTime;

    @DatabaseField(columnName = "type", defaultValue = "-1")
    public int type;

    public VideoCacheModel() {

    }

    public VideoCacheModel(String path, String cloudid, String localid, int type)
    {
        this.path = path;
        this.cloudId = cloudid;
        this.localId = localid;
        this.type = type;
        if (TextUtils.isEmpty(localid)) {
            this.createTime = System.currentTimeMillis();
        } else {
            this.createTime = Long.parseLong(localid);
        }
    }
}
