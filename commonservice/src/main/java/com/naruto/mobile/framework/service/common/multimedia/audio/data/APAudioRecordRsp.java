package com.naruto.mobile.framework.service.common.multimedia.audio.data;

/**
 * 音频录制回调数据结构
 * Created by jinmin on 15/3/31.
 */
public class APAudioRecordRsp extends APAudioRsp {
    /**
     * 录制时长过短
     */
    public static final int CODE_ERR_MIN_TIME = 101;
    /**
     * SD card不可写
     */
    public static final int CODE_ERR_SDCARD_ERR = 102;
    /**
     * Media未知错误
     */
    public static final int MEDIA_RECORDER_ERROR_UNKNOWN = 103;
    /**
     * Media服务异常
     */
    public static final int MEDIA_ERROR_SERVER_DIED = 104;
    /**
     * 录音权限申请提示框
     */
    public static final int MEDIA_RECORDER_PERMISSION_PROMPT = 105;
    /**
     * 录音时序异常
     */
    public static final int MEDIA_RECORDER_ILLEGAL_STATE = 106;
    /**
     * 录音编码异常
     */
    public static final int MEDAI_RECORDER_ENCODE_ERR = 107;
    /**
     * 录音权限受限
     */
    public static final int MEDIA_RECORDER_PERMISSION_DENIED = 108;
    /**
     * 录音采样均不支持
     */
    public static final int MEDIA_RECORDER_UNSUPPORTED = 109;
}
