package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.audio;


import java.lang.ref.WeakReference;

import com.naruto.mobile.framework.service.common.multimedia.audio.APAudioPlayCallback;
import com.naruto.mobile.framework.service.common.multimedia.audio.data.APAudioConfiguration;
import com.naruto.mobile.framework.service.common.multimedia.audio.data.APAudioInfo;
import com.naruto.mobile.framework.service.common.multimedia.audio.data.APRequestParam;

/**
 * 音频播放任务
 * Created by jinmin on 15/3/31.
 */
public class AudioPlayTask extends AudioTask {

    private APAudioInfo mAudioInfo;
    private APAudioPlayCallback mPlayCallback;
    private AudioPlayWorker mPlayWorker;

    AudioPlayTask(APAudioInfo info, APRequestParam param) {
        this(info, param, null);
    }

    AudioPlayTask(APAudioInfo info, APRequestParam param, APAudioPlayCallback callback) {
        this.mAudioInfo = info;
        setRequestParam(param);
        this.mPlayCallback = callback;
        setState(STATE_INIT);
    }

    public void setPlayWorker(AudioPlayWorker worker) {
        mPlayWorker = worker;
    }

    public AudioPlayWorker getPlayWorker() {
        return mPlayWorker;
    }

    public APAudioInfo getAudioInfo() {
        return mAudioInfo;
    }

    public void setAudioInfo(APAudioInfo audioInfo) {
        this.mAudioInfo = audioInfo;
    }

    public APAudioPlayCallback getPlayCallback() {
        return mPlayCallback;
    }

    public void setPlayCallback(APAudioPlayCallback playCallback) {
        this.mPlayCallback = playCallback;
    }

    public void stop() {
        setState(STATE_CANCEL);
        AudioPlayWorker worker = getPlayWorker();
        if (worker != null) {
            worker.stop();
        }
        setPlayWorker(null);
    }

    public void pause() {
        AudioPlayWorker worker = getPlayWorker();
        if (worker != null) {
            worker.pause();
        }
    }

    public void resume() {
        AudioPlayWorker worker = getPlayWorker();
        if (worker != null) {
            worker.resume();
        }
    }

    public void updateAudioConfiguration(APAudioConfiguration configuration) {
        AudioPlayWorker worker = getPlayWorker();
        if (worker != null) {
            worker.updateAudioConfiguration(configuration);
        }
    }

    public long getCurrentPosition() {
        AudioPlayWorker worker = getPlayWorker();
        return worker == null ? -1 : worker.getCurrentPosition();
    }
}
