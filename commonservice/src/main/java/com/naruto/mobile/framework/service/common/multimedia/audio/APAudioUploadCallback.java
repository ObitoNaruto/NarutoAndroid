package com.naruto.mobile.framework.service.common.multimedia.audio;


import com.naruto.mobile.framework.service.common.multimedia.audio.data.APAudioInfo;
import com.naruto.mobile.framework.service.common.multimedia.audio.data.APAudioUploadRsp;

/**
 * 音频上传回调
 * Created by jinmin on 15/3/31.
 */
public interface APAudioUploadCallback {
    /**
     * 开始上传
     * @param info           包含录音任务id
     */
    public void onUploadStart(APAudioInfo info);

    /**
     * 上传结束
     * @param rsp           包含录音任务id、服务器文件id
     */
    public void onUploadFinished(APAudioUploadRsp rsp);

    /**
     * 上传出错
     * @param rsp           录音任务id、异常描述
     */
    public void onUploadError(APAudioUploadRsp rsp);
}
