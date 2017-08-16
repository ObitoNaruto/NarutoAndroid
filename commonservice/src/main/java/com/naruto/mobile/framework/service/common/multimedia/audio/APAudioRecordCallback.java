package com.naruto.mobile.framework.service.common.multimedia.audio;

import com.naruto.mobile.framework.service.common.multimedia.audio.data.APAudioInfo;
import com.naruto.mobile.framework.service.common.multimedia.audio.data.APAudioRecordRsp;

/**
 * 录音相关回调
 * Created by jinmin on 15/3/31.
 */
public interface APAudioRecordCallback {
    /**
     * 录音开始
     * @param info       包含录音任务的ID
     */
    void onRecordStart(APAudioInfo info);

    /**
     * 录音音量变化
     * @param info          录音信息
     * @param amplitude      当前amplitude
     */
    void onRecordAmplitudeChange(APAudioInfo info, int amplitude);

    /**
     * 录音进度变化回调
     * @param info
     * @param recordDuration     已录音时长，单位：s（秒）
     */
    void onRecordProgressUpdate(APAudioInfo info, int recordDuration);

    /**
     * 录音取消
     * @param info       包含录音任务的ID
     */
    void onRecordCancel(APAudioInfo info);

    /**
     * 录音结束
     * @param info       包含录音任务的ID、录音时长
     */
    void onRecordFinished(APAudioInfo info);

    /**
     * 录音出错
     * @param rsp       包含录音任务的ID、异常描述
     */
    void onRecordError(APAudioRecordRsp rsp);
}
