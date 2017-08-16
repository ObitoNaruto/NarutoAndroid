package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.audio;

import android.content.Context;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.naruto.mobile.framework.service.common.multimedia.audio.APAudioRecordCallback;
import com.naruto.mobile.framework.service.common.multimedia.audio.APAudioRecordUploadCallback;
import com.naruto.mobile.framework.service.common.multimedia.audio.APAudioUploadCallback;
import com.naruto.mobile.framework.service.common.multimedia.audio.data.APAudioInfo;
import com.naruto.mobile.framework.service.common.multimedia.audio.data.APAudioRecordRsp;
import com.naruto.mobile.framework.service.common.multimedia.audio.data.APAudioUploadRsp;
import com.naruto.mobile.framework.service.common.multimedia.audio.data.APRequestParam;

/**
 * Created by jinmin on 15/3/31.
 */
public class AudioRecordManager {
    private static final String TAG = AudioRecordManager.class.getSimpleName();

    private static AudioRecordManager sInstance;

    private Context mContext;
    private AudioRecordTask mRecordTask;
    private AudioFileManager mAudioFileManager;
    private ExecutorService mExecutor;

    private AudioRecordManager(Context context) {
        this.mContext = context;
        this.mAudioFileManager = AudioFileManager.getInstance(context);
        this.mExecutor = Executors.newSingleThreadExecutor();
    }

    public synchronized static AudioRecordManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new AudioRecordManager(context);
        }
        return sInstance;
    }

    public void startRecord(AudioRecordTask task) {
        cancelRecord();
        checkAndSetSavePath(task.getAudioInfo());
        AudioRecordWorker worker = new AudioRecordWorker(mContext, task);
        task.setAudioRecordUploadCallback(new APAudioRecordCallbackWrapper(task.getRequestParam(), task.getAudioRecordUploadCallback()));
        task.setRecordWorker(worker);
        mRecordTask = task;
        mExecutor.execute(worker);
    }

    public void stopRecord() {
        if (mRecordTask != null) {
            mRecordTask.stop();
            mRecordTask = null;
        }
    }

    public void cancelRecord() {
        if (mRecordTask != null) {
            mRecordTask.cancel();
            mRecordTask = null;
        }
    }

    public void uploadAudio(APAudioInfo info, APRequestParam param, APAudioUploadCallback cb) {
        mAudioFileManager.uploadAudio(info, param, cb);
    }

    public APAudioUploadRsp uploadAudioSync(APAudioInfo info, APRequestParam param) {
        return mAudioFileManager.uploadAudioSync(info, param);
    }

    private void checkAndSetSavePath(APAudioInfo info) {
        info.setSavePath(mAudioFileManager.generateSavePath(info));
    }

    private class APAudioRecordCallbackWrapper implements APAudioRecordUploadCallback {
        private APAudioRecordCallback mCallback;
        private APRequestParam mReqParam;
        public APAudioRecordCallbackWrapper(APRequestParam param, APAudioRecordCallback callback) {
            this.mReqParam = param;
            this.mCallback = callback;
        }


        @Override
        public void onRecordStart(APAudioInfo info) {
            if (mCallback != null) {
                mCallback.onRecordStart(info);
            }
        }

        @Override
        public void onRecordAmplitudeChange(APAudioInfo info, int amplitude) {
            if (mCallback != null) {
                mCallback.onRecordAmplitudeChange(info, amplitude);
            }
        }

        @Override
        public void onRecordProgressUpdate(APAudioInfo info, int recordDuration) {
            if (mCallback != null) {
                mCallback.onRecordProgressUpdate(info, recordDuration);
            }
        }

        @Override
        public void onRecordCancel(APAudioInfo info) {
            mRecordTask = null;
            if (mCallback != null) {
                mCallback.onRecordCancel(info);
            }
        }

        @Override
        public void onRecordFinished(APAudioInfo info) {
            mRecordTask = null;
            if (mCallback != null) {
                mCallback.onRecordFinished(info);
            }
            //通知上传
            if (mCallback instanceof APAudioUploadCallback && !info.isSyncUpload()) {
//                if (info.getExtra().getBoolean("upload", true)) {
                uploadAudio(info, mReqParam, (APAudioUploadCallback) mCallback);
            }
//               else  if (info.getExtra().getBoolean("uploadFinish", false)) {
//                    APAudioUploadRsp rsp = new APAudioUploadRsp();
//                    rsp.setAudioInfo(info);
//                    rsp.setRetCode(APAudioRsp.CODE_SUCCESS);
//                    ((APAudioUploadCallback) mCallback).onUploadFinished(rsp);
//                }
//            }
        }

        @Override
        public void onRecordError(APAudioRecordRsp rsp) {
            mRecordTask = null;
            if (mCallback != null) {
                mCallback.onRecordError(rsp);
            }
        }

        @Override
        public void onUploadStart(APAudioInfo info) {
            if (mCallback instanceof APAudioUploadCallback) {
                ((APAudioUploadCallback) mCallback).onUploadStart(info);
            }
        }

        @Override
        public void onUploadFinished(APAudioUploadRsp rsp) {
            if (mCallback instanceof APAudioUploadCallback) {
                ((APAudioUploadCallback) mCallback).onUploadFinished(rsp);
            }
        }

        @Override
        public void onUploadError(APAudioUploadRsp rsp) {
            if (mCallback instanceof APAudioUploadCallback) {
                ((APAudioUploadCallback) mCallback).onUploadError(rsp);
            }
        }
    }
}
