package com.naruto.mobile.framework.service.common.multimedia.api.data;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 */
@DatabaseTable(tableName = "multi_media_task")//通过注解在数据库建表
public class APMultimediaTaskModel {
    public final static int STATUS_WAIT = 0;
    public final static int STATUS_RUNNING = 1;
    public final static int STATUS_CANCEL = 2;
    public final static int STATUS_FAIL = 3;
    public final static int STATUS_SUCCESS = 4;


    //字段名
    public final static String F_CLOUD_ID = "cloud_id";
    public final static String F_TASK_STATUS = "status";
    public final static String F_CREATE_TIME = "create_time";

    /**
     * 文件id
     */
    @DatabaseField(id = true, columnName = "task_id")
    private String taskId;

    /**
     * 创建时间
     */
    @DatabaseField(columnName = F_CREATE_TIME)
    private long creatTime;

    /**
     * 文件更新时间
     */
    @DatabaseField(columnName = "update_time")
    private long updateTime;

    /**
     * 上传状态
     */
    @DatabaseField(columnName = F_TASK_STATUS)
    private int status = STATUS_WAIT;

    /**
     * 文件源地址
     *
     */
    @DatabaseField(columnName = "source_path")
    private String sourcePath;

    /**
     * 文件目标地址
     */
    @DatabaseField(columnName = "dest_path")
    private String destPath;

    /**
     * 文件总大小
     */
    @DatabaseField(columnName = "total_size")
    private long totalSize;

    /**
     * 文件当前上传或下载大小
     */
    @DatabaseField(columnName = "current_size")
    private long currentSize;

    /**
     * 文件所在缓存的id
     */
    @DatabaseField(columnName = "cache_id")
    private String cacheId;

    /**
     * 文件在云存储上的id
     */
    @DatabaseField(columnName = F_CLOUD_ID)
    private String cloudId;

    public String getCloudId() {
        return cloudId;
    }

    public void setCloudId(String cloudId) {
        this.cloudId = cloudId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public long getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(long creatTime) {
        this.creatTime = creatTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    public String getDestPath() {
        return destPath;
    }

    public void setDestPath(String destPath) {
        this.destPath = destPath;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public long getCurrentSize() {
        return currentSize;
    }

    public void setCurrentSize(long currentSize) {
        this.currentSize = currentSize;
    }

    public String getCacheId() {
        return cacheId;
    }

    public void setCacheId(String cacheId) {
        this.cacheId = cacheId;
    }
	
	public int getPercent(){
        int pg = 0;
        if(totalSize > 0){
            return pg = (int) ((float)(currentSize * 100.0f /totalSize));
        }

        return pg;
    }


    @Override
    public String toString() {
        return "APMultimediaTaskModel{" +
                "taskId='" + taskId + '\'' +
                ", creatTime=" + creatTime +
                ", updateTime=" + updateTime +
                ", status=" + status +
                ", sourcePath='" + sourcePath + '\'' +
                ", destPath='" + destPath + '\'' +
                ", totalSize=" + totalSize +
                ", currentSize=" + currentSize +
                ", cacheId='" + cacheId + '\'' +
                ", cloudId='" + cloudId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        APMultimediaTaskModel model = (APMultimediaTaskModel) o;

        return taskId.equals(model.taskId);

    }

}
