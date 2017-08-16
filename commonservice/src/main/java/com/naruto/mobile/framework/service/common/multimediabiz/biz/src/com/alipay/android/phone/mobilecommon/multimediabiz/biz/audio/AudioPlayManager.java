package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.audio;

import android.content.Context;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.naruto.mobile.framework.service.common.multimedia.audio.APAudioDownloadCallback;
import com.naruto.mobile.framework.service.common.multimedia.audio.APAudioDownloadPlayCallback;
import com.naruto.mobile.framework.service.common.multimedia.audio.APAudioPlayCallback;
import com.naruto.mobile.framework.service.common.multimedia.audio.data.APAudioConfiguration;
import com.naruto.mobile.framework.service.common.multimedia.audio.data.APAudioDownloadRsp;
import com.naruto.mobile.framework.service.common.multimedia.audio.data.APAudioInfo;
import com.naruto.mobile.framework.service.common.multimedia.audio.data.APAudioPlayRsp;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.audio.utils.AudioUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.UCLogUtil;

/**
 * 音频播放管理
 * Created by jinmin on 15/3/31.
 */
public class AudioPlayManager {
    private static final String TAG = AudioPlayManager.class.getSimpleName();

    private static AudioPlayManager sInstance;

    private Context mContext;
    private AudioFileManager mAudioFileManager;
    private ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private AudioPlayTask mCurrentPlayTask;
    private AudioPlayManager(Context context) {
        this.mContext = context;
        mAudioFileManager = AudioFileManager.getInstance(context);
    }

    public synchronized static AudioPlayManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new AudioPlayManager(context);
        }
        return sInstance;
    }

    public void play(final AudioPlayTask task) {
        stop();
        mCurrentPlayTask = task;
        APAudioDownloadCallback downloadListener = new APAudioDownloadCallback() {
            long start = System.currentTimeMillis();
            long size = 0;
            boolean bLocal = true;
            @Override
            public void onDownloadStart(APAudioInfo info) {
				start = System.currentTimeMillis();
                if (task.getPlayCallback() instanceof APAudioDownloadPlayCallback) {
                    bLocal = false;
                    ((APAudioDownloadPlayCallback) task.getPlayCallback()).onDownloadStart(info);
                }
            }
            @Override
            public void onDownloadFinished(APAudioInfo info) {
                if (task.isCanceled()) {
                    if (task.getPlayCallback() != null) {
                        task.getPlayCallback().onPlayCancel(info);
                    }
                    return;
                }

                if (task.getPlayCallback() instanceof APAudioDownloadPlayCallback &&
                        info.getExtra().getBoolean("notifyDownloadFinished", true)) {
                    ((APAudioDownloadPlayCallback) task.getPlayCallback()).onDownloadFinished(info);
                }
                task.setPlayCallback(new APAudioPlayCallbackWrapper(task.getPlayCallback()));
                task.setAudioInfo(task.getAudioInfo());
                final AudioPlayWorker worker = new AudioPlayWorker(mContext, task);
                task.setPlayWorker(worker);

                mExecutor.execute(worker);

                File file = new File(info.getSavePath());
                size = file.length();
                UCLogUtil.UC_MM_C05(0, bLocal?-1:size, (int) (System.currentTimeMillis() - start));
            }

            @Override
            public void onDownloadError(APAudioInfo info, APAudioDownloadRsp rsp) {
                if (task.getPlayCallback() instanceof APAudioDownloadPlayCallback) {
                    ((APAudioDownloadPlayCallback) task.getPlayCallback()).onDownloadError(info, rsp);
                } else if (task.getPlayCallback() != null) {
                    APAudioPlayRsp playRsp = new APAudioPlayRsp();
                    playRsp.setAudioInfo(info);
                    playRsp.setRetCode(rsp.getRetCode());
                    playRsp.setMsg(rsp.getMsg());
                    task.getPlayCallback().onPlayError(playRsp);
                }

                File file = new File(info.getSavePath());
                size = file.length();
                UCLogUtil.UC_MM_C05(rsp.getRetCode(),bLocal?-1:size,(int)(System.currentTimeMillis() -start));
                //UCLogUtil.UC_MM_C09(rsp.getRetCode(),size,(int)(System.currentTimeMillis() - start));
            }
        };
        mAudioFileManager.downloadAudio(task.getAudioInfo(), task.getRequestParam(), downloadListener);
    }

    public void stop() {
        AudioPlayTask playTask = mCurrentPlayTask;
        /*if (playTask != null && playTask.getAudioInfo() != null) {
            mAudioFileManager.cancelDownloadTask(playTask.getAudioInfo());
        }*/
        if (playTask != null) {
            if (playTask.isRunning()) {
                playTask.stop();
            } else {
                playTask.setState(AudioTask.STATE_CANCEL);
            }
        }
        mCurrentPlayTask = null;
    }

    public boolean isPlaying() {
        AudioPlayTask playTask = mCurrentPlayTask;
        return playTask != null && playTask.isRunning();
    }

    public APAudioInfo getPlayingAudioInfo() {
        AudioPlayTask playTask = mCurrentPlayTask;
        if (playTask != null && playTask.isRunning()) {
            return playTask.getAudioInfo();
        }
        return null;
    }

    public void pausePlayAudio() {
        AudioPlayTask playTask = mCurrentPlayTask;
        if (playTask != null && playTask.isRunning()) {
            playTask.pause();
        }
    }

    public void resumePlayAudio() {
        AudioPlayTask playTask = mCurrentPlayTask;
        if (playTask != null) {
            playTask.resume();
        }
    }

    public void setAudioConfiguration(APAudioConfiguration configuration) {
        AudioPlayTask playTask = mCurrentPlayTask;
        if (playTask != null && configuration != null) {
            playTask.updateAudioConfiguration(configuration);
        }
    }

    public long getCurrentPosition() {
        return mCurrentPlayTask == null ? -1 : mCurrentPlayTask.getCurrentPosition();
    }

    private class APAudioPlayCallbackWrapper implements APAudioPlayCallback {
        private final APAudioPlayCallback mCallback;
        private boolean mAutoPaused = false;

        private APAudioPlayCallbackWrapper(APAudioPlayCallback callback) {
            this.mCallback = callback;
        }

        public APAudioPlayCallback wrap(APAudioPlayCallback cb) {
            return new APAudioPlayCallbackWrapper(cb);
        }

        @Override
        public void onPlayStart(APAudioInfo info) {
            if (AudioUtils.isMusicActive()) {
                mAutoPaused = true;
                AudioUtils.pauseSystemAudio();
            }
            if (mCallback != null) {
                mCallback.onPlayStart(info);
            }
        }

        @Override
        public void onPlayCancel(APAudioInfo info) {
            resumeSystemAudio();
            mCurrentPlayTask = null;
            if (mCallback != null) {
                mCallback.onPlayCancel(info);
            }
        }

        @Override
        public void onPlayCompletion(APAudioInfo info) {
            resumeSystemAudio();
            mCurrentPlayTask = null;
            if (mCallback != null) {
                mCallback.onPlayCompletion(info);
            }
        }

        @Override
        public void onPlayError(APAudioPlayRsp rsp) {
            resumeSystemAudio();
            mCurrentPlayTask = null;
            if (mCallback != null) {
                mCallback.onPlayError(rsp);
            }
        }

        private void resumeSystemAudio() {
            if (mAutoPaused) {
                mAutoPaused = false;
                AudioUtils.resumeSystemAudio();
            }
        }
    }
}
