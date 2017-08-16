package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.audio;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.naruto.mobile.framework.service.common.multimedia.api.data.APMultimediaTaskModel;
import com.naruto.mobile.framework.service.common.multimedia.audio.APAudioDownloadCallback;
import com.naruto.mobile.framework.service.common.multimedia.audio.APAudioPlayCallback;
import com.naruto.mobile.framework.service.common.multimedia.audio.APAudioPlayOutputModeChangeListener;
import com.naruto.mobile.framework.service.common.multimedia.audio.APAudioRecordCallback;
import com.naruto.mobile.framework.service.common.multimedia.audio.APAudioUploadCallback;
import com.naruto.mobile.framework.service.common.multimedia.audio.data.APAudioConfiguration;
import com.naruto.mobile.framework.service.common.multimedia.audio.data.APAudioDownloadRsp;
import com.naruto.mobile.framework.service.common.multimedia.audio.data.APAudioInfo;
import com.naruto.mobile.framework.service.common.multimedia.audio.data.APAudioPlayRsp;
import com.naruto.mobile.framework.service.common.multimedia.audio.data.APAudioRsp;
import com.naruto.mobile.framework.service.common.multimedia.audio.data.APAudioUploadRsp;
import com.naruto.mobile.framework.service.common.multimedia.audio.data.APRequestParam;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Logger;

/**
 * 音频任务管理，主要负责Record 和 Play的任务管理
 * Created by jinmin on 15/3/31.
 */
public class AudioTaskManager {
    private static final String TAG = AudioTaskManager.class.getSimpleName();

    private static AudioTaskManager sInstance;

    private Logger logger = Logger.getLogger("AudioTaskManager");

    private Context mContext;
    private AudioRecordManager mRecordManager;
    private AudioPlayManager mPlayManager;
    private AudioFileManager mFileManager;
    private APAudioConfiguration audioConfiguration;
    private Set<APAudioPlayOutputModeChangeListener> mAudioPlayOutputModeChangeListeners = new HashSet<APAudioPlayOutputModeChangeListener>();

//    private Map<String, AudioTask> mLocalIdTaskMap = new HashMap<String, AudioTask>();

    private AudioTaskManager(Context context) {
        this.mContext = context;
        this.mRecordManager = AudioRecordManager.getInstance(context);
        this.mPlayManager = AudioPlayManager.getInstance(context);
        this.mFileManager = AudioFileManager.getInstance(context);
    }

    public synchronized static AudioTaskManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new AudioTaskManager(context);
        }
        return sInstance;
    }

    /**============= Record ================*/
    public void startRecord(APAudioInfo info, APAudioRecordCallback cb) {
        startRecord(info, null, cb);
    }

    public void startRecord(APAudioInfo info, APRequestParam param, APAudioRecordCallback callback) {
        logger.d("startRecord enter");
        String localId = info.getLocalId();
        //没有LocalId才认为是录音任务
        if (TextUtils.isEmpty(localId)) {
            localId = generateLocalId();
            info.setLocalId(localId);
        }
        logger.d("startRecord info: " + info);
        AudioRecordTask task = new AudioRecordTask(info, param, callback);
//        mLocalIdTaskMap.put(localId, task);
        mRecordManager.startRecord(task);
        logger.d("startRecord end");
    }

    public void stopRecord() {
        mRecordManager.stopRecord();
    }

    public void cancelRecord() {
        mRecordManager.cancelRecord();
    }

    public void uploadAudio(APAudioInfo info, APRequestParam param, APAudioUploadCallback cb) {
        mRecordManager.uploadAudio(info, param, cb);
    }

    public APAudioUploadRsp uploadAudioSync(APAudioInfo info, APRequestParam param) {
        return mRecordManager.uploadAudioSync(info, param);
    }

    /**================= Play =================*/
    public void playAudio(APAudioInfo info, APRequestParam param, APAudioPlayCallback callback) {
        if (TextUtils.isEmpty(info.getLocalId()) &&
                TextUtils.isEmpty(info.getCloudId()) &&
                TextUtils.isEmpty(info.getSavePath())) {
            logger.d("Invalid params");
            if (callback != null) {
                APAudioPlayRsp rsp = new APAudioPlayRsp();
                rsp.setRetCode(APAudioRsp.CODE_ERROR);
                rsp.setAudioInfo(info);
                rsp.setMsg("Invalid audioInfo!");
                callback.onPlayError(rsp);
            }
            return;
        }
        AudioPlayTask task = new AudioPlayTask(info, param, callback);
        mPlayManager.play(task);
    }

    public long getPlayCurrentPosition() {
        return mPlayManager.getCurrentPosition();
    }

    public void stopPlayAudio() {
        mPlayManager.stop();
    }

    public boolean isPlaying() {
        return mPlayManager.isPlaying();
    }

    public APAudioInfo getPlayingAudioInfo() {
        return mPlayManager.getPlayingAudioInfo();
    }

    public void pausePlayAudio() {
        mPlayManager.pausePlayAudio();
    }

    public void resumePlayAudio() {
        mPlayManager.resumePlayAudio();
    }

    public boolean checkAudioOk(APAudioInfo info) {
        return mFileManager.checkAudioOk(info);
    }

    public APAudioDownloadRsp downloadAudio(APAudioInfo info, APRequestParam param) {
        return mFileManager.downloadAudio(info, param);
    }

    public APMultimediaTaskModel submitAudioDownloadTask(APAudioInfo info, APRequestParam param, APAudioDownloadCallback cb) {
        return mFileManager.downloadAudio(info, param, cb);
    }


    public void setAudioConfiguration(APAudioConfiguration audioConfiguration) {
        this.audioConfiguration = audioConfiguration;
        logger.d("setAudioConfiguration " + audioConfiguration);

        mPlayManager.setAudioConfiguration(audioConfiguration);
    }

    public APAudioConfiguration getAudioConfiguration() {
        return audioConfiguration;
    }
    //

    //=======================================
//    private <T extends AudioTask> T getTask(APAudioInfo info) {
//        AudioTask task = mLocalIdTaskMap.get(info.getLocalId());
//        try {
//            return (T) task;
//        } catch (Exception e) {
//            return null;
//        }
//    }

    //=======================================
    private String generateLocalId() {
        return String.valueOf(System.currentTimeMillis());
    }


    public void registerAudioPlayOutputModeChangeListener(APAudioPlayOutputModeChangeListener listener) {
        mAudioPlayOutputModeChangeListeners.add(listener);
    }

    public void unregisterAudioPlayOutputModeChangeListener(APAudioPlayOutputModeChangeListener listener) {
        mAudioPlayOutputModeChangeListeners.remove(listener);
    }

    public Iterator<APAudioPlayOutputModeChangeListener> getAudioPlayOutputModeChangeListeners() {
        return new HashSet<APAudioPlayOutputModeChangeListener>(mAudioPlayOutputModeChangeListeners).iterator();
    }

    public int deleteCache(String path) {
        return mFileManager.deleteCache(path);
    }
}
