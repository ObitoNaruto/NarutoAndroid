package com.naruto.mobile.framework.service.common.multimedia.graphics.data;

public class APImageDownloadRsp {
    /**
     * 返回码跟提示消息
     */
    private APImageRetMsg retmsg;
    /**
     * down请求的key,如django中的fileId, 本地图片的路径等
     */
    private String sourcePath;
    /**
     * 本地缓存id
     */
    private String cacheId;

    /**
     * 存储到本地的路径
     */
    private String storeFilePath;


    public APImageRetMsg getRetmsg() {
        return retmsg;
    }

    public void setRetmsg(APImageRetMsg retmsg) {
        this.retmsg = retmsg;
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    public String getCacheId() {
        return cacheId;
    }

    public void setCacheId(String cacheId) {
        this.cacheId = cacheId;
    }

    public String getStoreFilePath() {
        return storeFilePath;
    }

    public void setStoreFilePath(String storeFilePath) {
        this.storeFilePath = storeFilePath;
    }

    @Override
    public String toString() {
        return "APImageDownloadRsp{" +
                "retmsg=" + retmsg +
                ", sourcePath='" + sourcePath + '\'' +
                ", cacheId='" + cacheId + '\'' +
                ", storeFilePath='" + storeFilePath + '\'' +
                '}';
    }
}
