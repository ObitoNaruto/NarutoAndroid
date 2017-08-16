package com.naruto.mobile.framework.service.common.multimedia.audio.data;

/**
 * 音频相关回调基本数据结构
 * Created by jinmin on 15/3/31.
 */
public class APAudioRsp {

    /**
     * 公共Code： 0-100， 录音Code：101-200， 播放Code：201-300
     */
    public static final int CODE_SUCCESS = 0;
    public static final int CODE_ERROR = 1;
    public static final int CODE_ERROR_PREPARED = 2;
    public static final int CODE_ERROR_START = 3;
    public static final int CODE_ERROR_ILLEGAL_STATE = 4;//IllegalState



    private APAudioInfo audioInfo;
    private int retCode;
    private String msg;

    public APAudioInfo getAudioInfo() {
        return audioInfo;
    }

    public void setAudioInfo(APAudioInfo audioInfo) {
        this.audioInfo = audioInfo;
    }

    public int getRetCode() {
        return retCode;
    }

    public void setRetCode(int retCode) {
        this.retCode = retCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isSuccess() {
        return retCode == CODE_SUCCESS;
    }

    @Override
    public String toString() {
        return "APAudioRsp{" +
                "audioInfo=" + audioInfo +
                ", retCode=" + retCode +
                ", msg='" + msg + '\'' +
                '}';
    }
}
