package com.naruto.mobile.framework.service.common.multimedia.audio.data;

/**
 * 音频上传回调数据结构
 * Created by jinmin on 15/3/31.
 */
public class APAudioUploadRsp extends APAudioRsp {
    /**
     * 边录边传上传失败
     */
    public static final int CODE_SYNC_UPLOAD_ERROR = 100;
    /**
     * 语音文件上传失败
     */
    public static final int CODE_FILE_UPLOAD_ERROR = 101;
    /**
     * 语音录音状态未知
     */
    public static final int STATE_RECORD_UNKNOWN = -1;
    /**
     * 语音录音状态完成
     */
    public static final int STATE_RECORD_FINISHED = 0;
    /**
     * 语音录音状态异常
     */
    public static final int STATE_RECORD_ERROR = 1;
    /**
     * 语音录音状态取消
     */
    public static final int STATE_RECORD_CANCEL = 2;
    /**
     * 语音录音状态录音中
     */
    public static final int STATE_RECORD_RECORDING = 3;

    public int recordState = STATE_RECORD_UNKNOWN;

    public int extCode;

    public String extMsg;

    @Override
    public String toString() {
        return "APAudioUploadRsp{" +
                "extCode=" + extCode +
                ", extMsg='" + extMsg + '\'' +
                ", recordState='" + recordState + '\'' +
                ", super=" + super.toString() +
                '}';
    }
}
