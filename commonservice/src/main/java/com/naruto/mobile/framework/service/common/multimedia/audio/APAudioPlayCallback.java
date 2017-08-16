package com.naruto.mobile.framework.service.common.multimedia.audio;


import com.naruto.mobile.framework.service.common.multimedia.audio.data.APAudioInfo;
import com.naruto.mobile.framework.service.common.multimedia.audio.data.APAudioPlayRsp;

/**
 * 音频播放回调
 * Created by jinmin on 15/3/31.
 */
public interface APAudioPlayCallback {
//    public void onPlayWaiting(String cloudId);

    /**
     * 开始播放
     * @param info       包含播放对应文件id
     */
    void onPlayStart(APAudioInfo info);

    /**
     * 播放停止（回调场景：播放A时，直接播放B，框架直接停止A的播放，回调A播放停止）
     * @param info       包含播放对应文件id
     */
    void onPlayCancel(APAudioInfo info);

    /**
     * 播放完成
     * @param info      包含播放对应文件id
     */
    void onPlayCompletion(APAudioInfo info);

    /**
     * 播放出错
     * @param rsp     包含出错相关信息
     */
    void onPlayError(APAudioPlayRsp rsp);
}
