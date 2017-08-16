package com.naruto.mobile.framework.service.common.multimedia.graphics.data;

/**
 * Created by jinmin on 15/6/13.
 */
public class APImageOfflineDownloadReq {
    private String downloadUrl;
    private boolean waitDownloadFinished;

    public APImageOfflineDownloadReq(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public boolean isWaitDownloadFinished() {
        return waitDownloadFinished;
    }

    public void setWaitDownloadFinished(boolean waitDownloadFinished) {
        this.waitDownloadFinished = waitDownloadFinished;
    }

    @Override
    public String toString() {
        return "APImageOfflineDownloadReq{" +
                "downloadUrl='" + downloadUrl + '\'' +
                ", waitDownloadFinished=" + waitDownloadFinished +
                '}';
    }
}
