package com.naruto.mobile.framework.service.common.multimedia.audio.data;

import android.os.Bundle;

/**
 * 音频相关信息
 * Created by jinmin on 15/3/31.
 */
public class APAudioInfo {
    private static final String TAG = APAudioInfo.class.getSimpleName();

    /**
     * 语音上传方式
     */
    public static final String KEY_UPLOAD_TYPE = "uploadType";
    /**
     * 文件形式上传
     */
    public static final int INT_UPLOAD_TYPE_FILE = 0;
    /**
     * 边录边传
     */
    public static final int INT_UPLOAD_TYPE_SYNC = 1;

    /**
     * 本地任务ID
     */
    private String mLocalId;
    /**
     * Django服务器ID
     */
    private String mCloudId;
    /**
     * 本地存储路径
     */
    private String mSavePath;
    /**
     * 音频路径，可以是本地或url
     * todo: 待扩展实现
     */
    private String mPath;
    /**
     * 文件MD5值
     */
    private String mFileMD5;
    /**
     * 最短录制时间
     */
    private int mRecordMinTime = 1 * 1000;
    /**
     * 最长录制时间
     */
    private int mRecordMaxTime = 60 * 1000;
    /**
     * 进度刷新间隔时间
     */
    private int mProgressUpdateInterval = 1000;
    /**
     * 音频时长
     */
    private int mDuration;
    /**
     * 边录边传开关
     */
    private boolean mSyncUpload = false;

    private Bundle mExtra = new Bundle();

    private APAudioUploadState mUploadState;

    public static APAudioInfo fromPath(String path) {
        return new APAudioInfo(path);
    }

    public static APAudioInfo fromCloudId(String cloudId) {
        return new APAudioInfo(null, cloudId, null);
    }

    public static APAudioInfo fromLocalId(String localId) {
        return new APAudioInfo(localId, null, null);
    }

    public APAudioInfo() {
        this(null, null, null);
    }

    public APAudioInfo(String savePath) {
        this(null, null, savePath);
    }

    public APAudioInfo(String localId, String savePath) {
        this(localId, null, savePath);
    }

    public APAudioInfo(String localId, String cloudId, String path) {
        this.mLocalId = localId;
        this.mCloudId = cloudId;
        setSavePath(path);
    }

    public String getLocalId() {
        return mLocalId;
    }

    public void setLocalId(String localId) {
        this.mLocalId = localId;
    }

    public String getCloudId() {
        return mCloudId;
    }

    public void setCloudId(String cloudId) {
        this.mCloudId = cloudId;
    }

    public String getSavePath() {
        return mSavePath;
    }

    public void setSavePath(String savePath) {
        this.mSavePath = savePath;
        setPath(savePath);
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        this.mPath = path;
    }

    public String getFileMD5() {
        return mFileMD5;
    }

    public void setFileMD5(String fileMD5) {
        this.mFileMD5 = fileMD5;
    }

    public int getRecordMinTime() {
        return mRecordMinTime;
    }

    public void setRecordMinTime(int recordMinTime) {
        this.mRecordMinTime = recordMinTime;
    }

    public int getRecordMaxTime() {
        return mRecordMaxTime;
    }

    public void setRecordMaxTime(int recordMaxTime) {
        this.mRecordMaxTime = recordMaxTime;
    }

    public int getProgressUpdateInterval() {
        return mProgressUpdateInterval;
    }

    public void setProgressUpdateInterval(int progressUpdateInterval) {
        this.mProgressUpdateInterval = progressUpdateInterval;
    }

    public int getDuration() {
        return mDuration;
    }

    public void setDuration(int duration) {
        this.mDuration = duration;
    }

    public boolean isSyncUpload() {
        return mSyncUpload;
    }

    public void setSyncUpload(boolean syncUpload) {
        this.mSyncUpload = syncUpload;
    }

    public Bundle getExtra() {
        return mExtra;
    }

    public APAudioUploadState getUploadState() {
        return mUploadState;
    }

    public void setUploadState(APAudioUploadState uploadState) {
        this.mUploadState = uploadState;
    }

    @Override
    public String toString() {
        return "APAudioInfo{" +
                "mLocalId='" + mLocalId + '\'' +
                ", mCloudId='" + mCloudId + '\'' +
                ", mSavePath='" + mSavePath + '\'' +
                ", mPath='" + mPath + '\'' +
                ", mFileMD5='" + mFileMD5 + '\'' +
                ", mRecordMinTime=" + mRecordMinTime +
                ", mRecordMaxTime=" + mRecordMaxTime +
                ", mProgressUpdateInterval=" + mProgressUpdateInterval +
                ", mDuration=" + mDuration +
                ", mSyncUpload=" + mSyncUpload +
                ", mExtra=" + mExtra +
                ", mUploadState=" + mUploadState +
                '}';
    }
}
