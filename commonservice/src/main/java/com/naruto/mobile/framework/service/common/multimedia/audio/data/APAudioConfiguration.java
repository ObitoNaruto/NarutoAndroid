package com.naruto.mobile.framework.service.common.multimedia.audio.data;

/**
 * 音频相关全局配置
 * Created by jinmin on 15/4/10.
 */
public class APAudioConfiguration {
    private static final String TAG = APAudioConfiguration.class.getSimpleName();


//    public static APAudioConfiguration defaultAudioConfiguration() {
//        return new APAudioConfiguration();
//    }

    public enum PlayOutputMode {
        /**
         * 耳筒
         */
        MODE_EAR_PHONE,
        /**
         * 扬声器
         */
        MODE_PHONE_SPEAKER,
        /**
         * 跟随系统
         */
//        MODE_SYSTEM
    }

    private PlayOutputMode playOutputMode = PlayOutputMode.MODE_PHONE_SPEAKER;

    private boolean notifyWhileManualChange = true;

    public PlayOutputMode getPlayOutputMode() {
        return playOutputMode;
    }

    public void setPlayOutputMode(PlayOutputMode playOutputMode) {
        this.playOutputMode = playOutputMode;
    }

    public boolean isNotifyWhileManualChange() {
        return notifyWhileManualChange;
    }

    public void setNotifyWhileManualChange(boolean notifyWhileManualChange) {
        this.notifyWhileManualChange = notifyWhileManualChange;
    }

    @Override
    public String toString() {
        return "APAudioConfiguration{" +
                "playOutputMode=" + playOutputMode +
                '}';
    }
}
