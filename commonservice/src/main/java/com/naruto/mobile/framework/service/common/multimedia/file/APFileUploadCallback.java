package com.naruto.mobile.framework.service.common.multimedia.file;


import com.naruto.mobile.framework.service.common.multimedia.api.data.APMultimediaTaskModel;
import com.naruto.mobile.framework.service.common.multimedia.file.data.APFileUploadRsp;

public interface APFileUploadCallback {

    void onUploadStart(APMultimediaTaskModel taskInfo);

    void onUploadProgress(APMultimediaTaskModel taskInfo, int progress, long hasUploadSize, long total);

//    void onUploadBatchProgress(APMultimediaTaskModel taskInfo, int progress, int curIndex, long hasUploadSize, long total);

    void onUploadFinished(APMultimediaTaskModel taskInfo, APFileUploadRsp rsp);

    void onUploadError(APMultimediaTaskModel taskInfo, APFileUploadRsp rsp);
}
