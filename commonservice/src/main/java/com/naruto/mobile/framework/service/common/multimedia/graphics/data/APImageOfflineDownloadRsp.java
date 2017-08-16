package com.naruto.mobile.framework.service.common.multimedia.graphics.data;

import java.util.Map;

/**
 * 图片文件离线下载到Django服务器Response
 * Created by jinmin on 15/6/13.
 */
public class APImageOfflineDownloadRsp {
    private APImageOfflineDownloadReq req;

    private String cloudId;
    private int retCode;
    private int width;
    private int height;
    private Map<String, String> exif;

    public APImageOfflineDownloadReq getReq() {
        return req;
    }

    public void setReq(APImageOfflineDownloadReq req) {
        this.req = req;
    }

    public String getCloudId() {
        return cloudId;
    }

    public void setCloudId(String cloudId) {
        this.cloudId = cloudId;
    }

    public int getRetCode() {
        return retCode;
    }

    public void setRetCode(int retCode) {
        this.retCode = retCode;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Map<String, String> getExif() {
        return exif;
    }

    public void setExif(Map<String, String> exif) {
        this.exif = exif;
    }

    @Override
    public String toString() {
        return "APImageOfflineDownloadRsp{" +
                "req=" + req +
                ", cloudId='" + cloudId + '\'' +
                ", retCode=" + retCode +
                ", width=" + width +
                ", height=" + height +
                ", exif=" + exif +
                '}';
    }
}
