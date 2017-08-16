package com.naruto.mobile.framework.service.common.multimedia.audio;


import com.naruto.mobile.framework.service.common.multimedia.audio.data.APAudioConfiguration;

/**
 * 音频播放输出方式改变监听器
 * Created by jinmin on 15/4/10.
 */
public interface APAudioPlayOutputModeChangeListener {
    /**
     * 播放输出方式改变
     * @param mode
     */
    void onAudioPlayOutputModeChange(APAudioConfiguration.PlayOutputMode mode);
}
