package com.naruto.mobile.framework.service.common.multimedia.audio;

import com.naruto.mobile.framework.service.common.multimedia.audio.data.APAudioDownloadRsp;
import com.naruto.mobile.framework.service.common.multimedia.audio.data.APAudioInfo;

/**
 * 音频下载回调
 * Created by jinmin on 15/4/3.
 */
public interface APAudioDownloadCallback {
    void onDownloadStart(APAudioInfo info);
    void onDownloadFinished(APAudioInfo info);
    void onDownloadError(APAudioInfo info, APAudioDownloadRsp rsp);
}
