package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.audio;

import android.content.Context;
import android.text.TextUtils;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

import com.naruto.mobile.framework.service.common.multimedia.audio.APAudioPlayCallback;
import com.naruto.mobile.framework.service.common.multimedia.audio.APAudioPlayOutputModeChangeListener;
import com.naruto.mobile.framework.service.common.multimedia.audio.data.APAudioConfiguration;
import com.naruto.mobile.framework.service.common.multimedia.audio.data.APAudioInfo;
import com.naruto.mobile.framework.service.common.multimedia.audio.data.APAudioPlayRsp;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.audio.silk.SilkApi;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.audio.silk.SilkPlayer;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Logger;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.UCLogUtil;

/**
 * 音频播放Worker
 * Created by jinmin on 15/3/31.
 */
public class AudioPlayWorker implements Runnable, AudioHelper.OnSensorChangeListener {

    private static final String TAG = AudioPlayWorker.class.getSimpleName();

    private Logger logger = Logger.getLogger("AudioPlayWorker");

    private Context mContext;
    private AudioPlayTask mPlayTask;
    private APAudioInfo mAudioInfo;
    private APAudioPlayCallback mPlayCallback;
    private SilkPlayer.PathAudioParam mAudioParam;

    private AtomicBoolean hasError = new AtomicBoolean(false);
    private AudioTaskManager mAudioTaskManager;


    AudioPlayWorker(Context context, AudioPlayTask task) {
        this.mContext = context;
        this.mPlayTask = task;
        this.mAudioInfo = task.getAudioInfo();
        this.mPlayCallback = task.getPlayCallback();
        this.mAudioTaskManager = AudioTaskManager.getInstance(mContext);

        init();
    }

    private void init() {
        mAudioHelper = AudioHelper.getInstance();
//        mAudioHelper.initAudioRegulator(mContext);
        mSource = new MediaSource(mAudioInfo.getSavePath());

        mAudioParam = new SilkPlayer.PathAudioParam();
//        if (mPlayer != null) {
//            mPlayer.reset();
//            mPlayer.release();
//        }
        mPlayer = new SilkPlayer(mAudioParam);
//        mPlayer.reset();
    }

    @Override
    public void run() {
        if (mPlayTask.isCanceled() || isStop()) {
            notifyPlayCancel();
            return;
        }
        play();

    }

    //==================  Player  ==================
    private static final int STATE_UNKNOWN = -1;
    private static final int STATE_INIT = 0;
    private static final int STATE_PREPARED = 1;
    private static final int STATE_PLAYING = 2;
    private static final int STATE_PAUSE = 3;
    private static final int STATE_STOP = 4;
    private static final int STATE_RELEASE = 5;
    private static final int STATE_FINISH = 5;


    private SilkPlayer mPlayer;
    private MediaSource mSource;

    private int mState = STATE_UNKNOWN;
    private AudioHelper mAudioHelper;

    public void prepare() throws Exception {
        if (mSource == null) {
            throw new NullPointerException("MediaSource is null. Please setup mediaSource first");
        }
        if (!TextUtils.isEmpty(mSource.getSourcePath())) {
            mAudioParam.setPath(mSource.getSourcePath());
        }
        mAudioParam.setSampleRateInHz(SilkApi.SAMPLE_RATE_44_1K);

        setState(STATE_INIT);

        mPlayer.setPlayListener(new MediaPlayerListener());
        mPlayer.prepare();
        setState(STATE_PREPARED);
    }


    private void applyAudioConfiguration(boolean noPlay) {
        logger.d("applyAudioConfiguration start");
        APAudioConfiguration configuration = mAudioTaskManager.getAudioConfiguration();
        applyAudioConfiguration(configuration, true, noPlay);
        logger.d("applyAudioConfiguration finish");
    }
//
    private void applyAudioConfiguration(APAudioConfiguration configuration, boolean notify, boolean noPlay) {
        applyAudioConfiguration(configuration, notify, noPlay, false);
    }
    private void applyAudioConfiguration(APAudioConfiguration configuration, boolean notify, boolean noPlay, boolean manual) {
        logger.d("applyAudioConfiguration start: " + configuration);
        if (configuration != null) {
            switch (configuration.getPlayOutputMode()) {
                case MODE_EAR_PHONE:
                    mAudioHelper.unregisterSensorMonitor(mContext);
                    mPlayer.useEarphonePlay(true, noPlay, manual);
                    if (notify) {
                        notifyPlayOutChanged(true);
                    }
//                    mAudioHelper.unregisterSensorChangeListener(this);
                    break;
                case MODE_PHONE_SPEAKER:
                    logger.d("applyAudioConfiguration isUsingSpeakerphone ? %s, notify? %s", isUsingSpeakerphone(), notify);
                    if (!isUsingSpeakerphone()) {
                        mPlayer.useEarphonePlay(false, noPlay, manual);
                        if (notify) {
                            notifyPlayOutChanged(false);
                        }
                    }
                    mAudioHelper.registerSensorMonitor(mContext);
                    mAudioHelper.registerSensorChangeListener(this);
                    break;
                default:
                    break;
            }
        }
    }

    private boolean isUsingSpeakerphone() {
        return mPlayer.isUsingSpeakerphone();
    }

    /**
     * 播放已设定好的MediaSource
     */
    public void play() {
        try {
            prepare();
        } catch (Exception e) {
            hasError.set(true);
            logger.e(e, "play-prepare error");
            notifyPlayError(APAudioPlayRsp.CODE_ERROR_PREPARED, "MediaPlayer prepare fail, msg: " + e.getMessage());
            return;
        }
        applyAudioConfiguration(true);
        mPlayer.start();
        setState(STATE_PLAYING);
    }

    /**
     * 停止播放
     */
    public void stop() {
        stop(true);
    }

    /**
     * 停止播放
     * @param notify    是否通知监听者
     */
    private void stop(boolean notify) {
        setState(STATE_STOP);
        if (hasPrepared() && (mPlayer.isPlaying() || mPlayer.isPaused())) {
            mPlayer.stop();
        }
        if (notify) {
            notifyPlayCancel();
        }
    }

    /**
     * 暂停播放
     */
    public void pause() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mAudioHelper.unregisterSensorMonitor(mContext);
//            mAudioHelper.unregisterSensorChangeListener(this);
            mPlayer.pause();
            setState(STATE_PAUSE);
        }
    }

    /**
     * 继续播放
     */
    public void resume() {
        if (mPlayer != null && isPaused()) {
            applyAudioConfiguration(false);
            mPlayer.start();
            setState(STATE_PLAYING);
        }
    }

    /**
     * 播放器重置
     */
    public void reset() {
        setState(STATE_INIT);
        mPlayer.reset();
    }

    /**
     * 播放器释放，释放播放器资源
     */
    public void release() {
        reset();
        setState(STATE_RELEASE);
    }

    public boolean isPlaying() {
        return STATE_PLAYING == mState;
    }

    public boolean isStop() {
        return STATE_STOP == mState;
    }

    public boolean isPaused() {
        return STATE_PAUSE == mState;
    }

    public boolean hasPrepared() {
        return mState >= STATE_PREPARED && mState < STATE_RELEASE;
    }

    private void setState(int state) {
        this.mState = state;
    }

    public void updateAudioConfiguration(APAudioConfiguration configuration) {
        if (isPlaying() || isPaused()) {
            if (configuration.getPlayOutputMode() == APAudioConfiguration.PlayOutputMode.MODE_EAR_PHONE) {
                applyAudioConfiguration(configuration, configuration.isNotifyWhileManualChange(), isPaused(), true);
            } else if (configuration.getPlayOutputMode() == APAudioConfiguration.PlayOutputMode.MODE_PHONE_SPEAKER) {
                applyAudioConfiguration(configuration, configuration.isNotifyWhileManualChange(), isPaused(), true);
            }
        }
    }

    @Override
    public void onSensorChanged(boolean closeToFace) {
        logger.d("onSensorChanged isUsingSpeakerphone? %s, closeToFace? %s", isUsingSpeakerphone(), closeToFace);
        if (!isUsingSpeakerphone() && !closeToFace) {//听筒切换到扬声器，要通知，每次靠近耳朵要通知
            notifyPlayOutChanged(false);
        } else if (closeToFace) {
            notifyPlayOutChanged(true);
        }
        mPlayer.useEarphonePlay(closeToFace, false);
    }

    public long getCurrentPosition() {
        return mPlayer == null ? -1 : mPlayer.getCurrentPosition();
    }

    /**
     * 播放源封装
     */
    public static class MediaSource {
        private String sourcePath;


        public MediaSource(String sourcePath) {
            this.sourcePath = sourcePath;
        }

        public String getSourcePath() {
            return sourcePath;
        }


        @Override
        public String toString() {
            return "MediaSource{" +
                    ", sourcePath='" + sourcePath + '\'' +
                    '}';
        }
    }

    /**
     * MediaPlayer监听器
     */
    private class MediaPlayerListener implements SilkPlayer.IPlayListener {

        @Override
        public void onStart(SilkPlayer player) {
            notifyPlayStart();
        }

        @Override
        public void onPause(SilkPlayer player) {

        }

        @Override
        public void onResume(SilkPlayer player) {
        }

        @Override
        public void onStop(SilkPlayer player) {
            notifyPlayCancel();
        }

        @Override
        public void onComplete(SilkPlayer player) {
            if (!hasError.get()) {
                setState(STATE_FINISH);
                notifyPlayCompleted();
            }
        }

        @Override
        public void onError(SilkPlayer player, Exception e) {
            hasError.set(true);
            logger.e(e, "MediaPlayerListener onError, id: " + mAudioInfo.getCloudId());
            notifyPlayError(APAudioPlayRsp.CODE_ERROR, e.getMessage());
        }

    }

    //======= 回调相关通知 =======
    private void notifyPlayStart() {
        APAudioConfiguration configuration = AudioTaskManager.getInstance(mContext).getAudioConfiguration();
        if (configuration == null ||
                configuration.getPlayOutputMode() != APAudioConfiguration.PlayOutputMode.MODE_EAR_PHONE) {
        }
        mPlayTask.setState(AudioTask.STATE_RUNNING);
        if (mPlayCallback != null && !hasError.get()) {
            mPlayCallback.onPlayStart(mAudioInfo);
        }
    }

    private void notifyPlayCancel() {
        mPlayTask.setState(AudioTask.STATE_CANCEL);
        mAudioHelper.unregisterSensorMonitor(mContext);
//        mAudioHelper.unregisterSensorChangeListener(this);
        if (mPlayCallback != null && !hasError.get()) {
            mPlayCallback.onPlayCancel(mAudioInfo);
        }
        UCLogUtil.UC_MM_C11(0, "cancel");
    }

    private void notifyPlayCompleted() {
        mPlayTask.setState(AudioTask.STATE_FINISH);
        mAudioHelper.unregisterSensorMonitor(mContext);
//        mAudioHelper.unregisterSensorChangeListener(this);
        if (mPlayCallback != null && !hasError.get()) {
            mPlayCallback.onPlayCompletion(mAudioInfo);
        }
        UCLogUtil.UC_MM_C12(0, mAudioInfo.getCloudId(), null);
    }

    private void notifyPlayError(int errCode, String msg) {
        notifyPlayError(errCode, msg, -1, -1);
    }

    private void notifyPlayError(int errCode, String msg, int what, int extra) {
        if (mPlayCallback != null) {
            APAudioPlayRsp rsp = new APAudioPlayRsp();
            rsp.setAudioInfo(mAudioInfo);
            rsp.setRetCode(errCode);
            rsp.setMsg(msg);
            rsp.setWhat(what);
            rsp.setExtra(extra);
            notifyPlayError(rsp);
        } else {
            mAudioHelper.unregisterSensorMonitor(mContext);
//            mAudioHelper.unregisterSensorChangeListener(this);
            mPlayTask.setState(AudioTask.STATE_FINISH);
            resetPlayer();
            UCLogUtil.UC_MM_C12(errCode, mAudioInfo.getCloudId(), msg);
        }
    }

    private void notifyPlayError(APAudioPlayRsp rsp) {
        resetPlayer();
        mPlayTask.setState(AudioTask.STATE_FINISH);
        mAudioHelper.unregisterSensorMonitor(mContext);
//        mAudioHelper.unregisterSensorChangeListener(this);
        if (mPlayCallback != null) {
            mPlayCallback.onPlayError(rsp);
        }
        logger.e("notifyPlayError rsp: " + rsp);
        UCLogUtil.UC_MM_C12(rsp.getRetCode(), mAudioInfo.getCloudId(), rsp.getMsg());
    }

    private void notifyPlayOutChanged(boolean earPhone) {
        Iterator<APAudioPlayOutputModeChangeListener> listenerIterator = mAudioTaskManager.getAudioPlayOutputModeChangeListeners();
        while (listenerIterator.hasNext()) {
            APAudioPlayOutputModeChangeListener l = listenerIterator.next();
            if (earPhone) {
                l.onAudioPlayOutputModeChange(APAudioConfiguration.PlayOutputMode.MODE_EAR_PHONE);
            } else {
                l.onAudioPlayOutputModeChange(APAudioConfiguration.PlayOutputMode.MODE_PHONE_SPEAKER);
            }
        }
    }

    private void resetPlayer() {
        if (mPlayer != null) {
            try {
                mPlayer.reset();
//                mPlayer.release();
            } catch (Exception e) {
                //ignore
            }
            mPlayer = new SilkPlayer(mAudioParam);
        }
        mAudioHelper.unregisterSensorMonitor(mContext);
//        mAudioHelper.unregisterSensorChangeListener(this);
    }

//    @Override
//    protected void finalize() throws Throwable {
//        if (isPaused() || isPlaying()) {
//            stop();
//        }
//        super.finalize();
//    }
}
