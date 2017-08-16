package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.audio;


import java.lang.ref.WeakReference;

import com.naruto.mobile.framework.service.common.multimedia.audio.APAudioRecordCallback;
import com.naruto.mobile.framework.service.common.multimedia.audio.APAudioRecordUploadCallback;
import com.naruto.mobile.framework.service.common.multimedia.audio.data.APAudioInfo;
import com.naruto.mobile.framework.service.common.multimedia.audio.data.APRequestParam;

/**
 * 录音任务
 * Created by jinmin on 15/3/31.
 */
public class AudioRecordTask extends AudioTask {

    private static final String TAG = AudioRecordTask.class.getSimpleName();

    private APAudioInfo mAudioInfo;
    private APAudioRecordCallback mAudioRecordUploadCallback;
    private WeakReference<AudioRecordWorker> mRecordWorker;

    AudioRecordTask(APAudioInfo audioInfo, APRequestParam param) {
        this(audioInfo, param, null);
    }

    AudioRecordTask(APAudioInfo audioInfo, APRequestParam param, APAudioRecordCallback callback) {
        this.mAudioInfo = audioInfo;
        setRequestParam(param);
        this.mAudioRecordUploadCallback = callback;
        setState(STATE_INIT);
    }

    public APAudioInfo getAudioInfo() {
        return mAudioInfo;
    }

    public APAudioRecordCallback getAudioRecordUploadCallback() {
        return mAudioRecordUploadCallback;
    }

    public void setAudioRecordUploadCallback(APAudioRecordUploadCallback audioRecordCallback) {
        this.mAudioRecordUploadCallback = audioRecordCallback;
    }

    public void cancel() {
        setState(STATE_CANCEL);
        AudioRecordWorker worker = mRecordWorker == null ? null : mRecordWorker.get();
        if (worker != null) {
            worker.cancel();
        }
    }

    public void stop() {
        AudioRecordWorker worker = mRecordWorker == null ? null : mRecordWorker.get();
        if (worker != null) {
            worker.stop();
        }
        setState(STATE_FINISH);
    }

    protected void setRecordWorker(AudioRecordWorker worker) {
        mRecordWorker = new WeakReference<AudioRecordWorker>(worker);
    }
}
