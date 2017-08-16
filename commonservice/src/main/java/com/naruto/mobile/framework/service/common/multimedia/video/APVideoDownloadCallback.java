package com.naruto.mobile.framework.service.common.multimedia.video;


import com.naruto.mobile.framework.service.common.multimedia.api.data.APMultimediaTaskModel;
import com.naruto.mobile.framework.service.common.multimedia.video.data.APVideoDownloadRsp;

public interface APVideoDownloadCallback {

	
    public void onDownloadStart(APMultimediaTaskModel taskinfo);

    /**
     * 上传结束
     * @param rsp           包含录音任务id、服务器文件id
     */
    public void onDownloadFinished(APVideoDownloadRsp rsp);
    
    /**
     * 上传出错
     * @param rsp           录音任务id、异常描述
     */
    public void onDownloadError(APVideoDownloadRsp rsp);
    
    public void onDownloadProgress(APMultimediaTaskModel taskInfo, int progress);
    
    public void onThumbDownloadFinished(APVideoDownloadRsp rsp);
}
