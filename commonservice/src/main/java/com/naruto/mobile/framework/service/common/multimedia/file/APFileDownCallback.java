package com.naruto.mobile.framework.service.common.multimedia.file;


import com.naruto.mobile.framework.service.common.multimedia.api.data.APMultimediaTaskModel;
import com.naruto.mobile.framework.service.common.multimedia.file.data.APFileDownloadRsp;

public interface APFileDownCallback {

    void onDownloadStart(APMultimediaTaskModel taskInfo);

    void onDownloadProgress(APMultimediaTaskModel taskInfo, int progress, long hasDownSize, long total);

    /**
     * 批量的情况
     */
    void onDownloadBatchProgress(APMultimediaTaskModel taskInfo, int progress, int curIndex, long hasDownSize,
            long total);

    void onDownloadFinished(APMultimediaTaskModel taskInfo, APFileDownloadRsp rsp);

    void onDownloadError(APMultimediaTaskModel taskInfo, APFileDownloadRsp rsp);

}
