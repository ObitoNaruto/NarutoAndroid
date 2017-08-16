package com.naruto.mobile.framework.service.common.multimedia.video;


import com.naruto.mobile.framework.service.common.multimedia.api.data.APMultimediaTaskModel;
import com.naruto.mobile.framework.service.common.multimedia.video.data.APVideoUploadRsp;

/**
 * 音频上传回调
 * Created by jinmin on 15/3/31.
 */
public interface APVideoUploadCallback {
    /**
     * 开始上传
     * @param info           包含录音任务id
     */
    public void onUploadStart(APMultimediaTaskModel taskinfo);

    /**
     * 上传结束
     * @param rsp           包含录音任务id、服务器文件id
     */
    public void onUploadFinished(APVideoUploadRsp rsp);

    /**
     * 上传出错
     * @param rsp           录音任务id、异常描述
     */
    public void onUploadError(APVideoUploadRsp rsp);
    
    public void onUploadProgress(APMultimediaTaskModel taskInfo, int progress);
}
