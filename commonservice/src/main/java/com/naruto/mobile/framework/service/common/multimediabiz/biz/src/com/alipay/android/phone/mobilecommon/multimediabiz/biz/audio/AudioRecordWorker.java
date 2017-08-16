package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.audio;

import android.content.Context;
import android.os.Handler;

import java.io.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import com.naruto.mobile.framework.service.common.multimedia.audio.APAudioRecordUploadCallback;
import com.naruto.mobile.framework.service.common.multimedia.audio.data.APAudioInfo;
import com.naruto.mobile.framework.service.common.multimedia.audio.data.APAudioRecordRsp;
import com.naruto.mobile.framework.service.common.multimedia.audio.data.APAudioRsp;
import com.naruto.mobile.framework.service.common.multimedia.audio.data.APAudioUploadRsp;
import com.naruto.mobile.framework.service.common.multimedia.audio.data.APAudioUploadState;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.audio.silk.SilkApi;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.audio.silk.SilkEncoder;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.audio.silk.SilkRecorder;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.audio.silk.SilkUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.io.IOUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.resp.FileUpResp;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.CompareUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.ExceptionUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Logger;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.UCLogUtil;

/**
 * 录音Worker, 调度由AudioRecordManager控制
 * Created by jinmin on 15/3/31.
 */
public class AudioRecordWorker implements Runnable {
    private static final String TAG = AudioRecordWorker.class.getSimpleName();
    
    private Logger logger = Logger.getLogger("AudioRecordWorker");

    private Context mContext;
    private AudioRecordTask mRecordTask;
    private APAudioRecordUploadCallback mRecordUploadCallback;

    private Timer mRecordAmplitudeTimer = null;
    private Timer mRecordProgressUpdateTimer = null;
    private Timer mRecordMaxTimeTimer = null;
    private TimerTask mRecordAmplitudeTimerTask;
    private TimerTask mRecordProgressUpdateTimerTask;
    private TimerTask mRecordMaxTimeTimerTask;
    private static final int RECORD_AMPLITUDE_CHANGE_UPDATE_PERIOD = 300;

    private String mSavePath;
    private volatile BufferedOutputStream mLocalDataOutputStream;
    private volatile DataOutputStream mDjangoDataOutputStream;
    private final Object DJANGO_SYNC = new Object();
    private boolean bSyncUpload;
    private final AtomicBoolean bReset = new AtomicBoolean(false);
    private final AtomicBoolean bPrepared = new AtomicBoolean(false);
    private final AtomicBoolean bStopping = new AtomicBoolean(false);
    private APAudioUploadState mUploadState = new APAudioUploadState(APAudioUploadState.STATE_UNKNOWN);
    private int mRecordState = APAudioUploadRsp.STATE_RECORD_UNKNOWN;

    public AudioRecordWorker(Context context, AudioRecordTask task) {
        this.mContext = context;
        this.mRecordTask = task;
        this.mAudioInfo = mRecordTask.getAudioInfo();
        this.bSyncUpload = mAudioInfo.isSyncUpload();
        mRecorder = new SilkRecorder();
    }

    @Override
    public void run() {
        mRecordUploadCallback = (APAudioRecordUploadCallback) mRecordTask.getAudioRecordUploadCallback();
        try {
            mRecorder.reset();
            logger.d("recordPrepare begin");
            recordPrepare();
            bPrepared.set(true);
            logger.d("recordPrepare end");
        } catch (Exception e) {
            ExceptionUtils.checkAndResetDiskCache(e);
            logger.e(e, "recordPrepare exception, audioInfo: " + mAudioInfo);
            APAudioRecordRsp rsp = new APAudioRecordRsp();
            if (e instanceof SilkRecorder.RecordPermissionDeniedException) {
                rsp.setRetCode(APAudioRecordRsp.MEDIA_RECORDER_PERMISSION_DENIED);
                rsp.setMsg(e.getMessage());
            } else if (e instanceof SilkRecorder.RecordUnsupportedException) {
                rsp.setRetCode(APAudioRecordRsp.MEDIA_RECORDER_UNSUPPORTED);
                rsp.setMsg(e.getMessage());
            } else if (e instanceof IOException) {
                rsp.setRetCode(APAudioRecordRsp.CODE_ERR_SDCARD_ERR);
                rsp.setMsg("SD卡无法写入");
            } else {
                rsp.setRetCode(APAudioRecordRsp.CODE_ERROR_PREPARED);
                rsp.setMsg("请检测录音器是否被占用");
            }
            rsp.setAudioInfo(mAudioInfo);
            notifyRecordError(rsp);
            logger.d("recordPrepare error: " + rsp.getMsg());
            return;
        }
        try {
            logger.d("recordStart begin");
            recordStart();
            logger.d("recordStart end");
        } catch (Exception e) {
            logger.e(e, "recordStart exception, audioInfo: " + mAudioInfo);
            APAudioRecordRsp rsp = new APAudioRecordRsp();
            if (e instanceof SilkRecorder.RecordPermissionDeniedException) {
                rsp.setRetCode(APAudioRecordRsp.MEDIA_RECORDER_PERMISSION_DENIED);
                rsp.setMsg(e.getMessage());
            } else if (e instanceof RecordPermissionRequestException) {
                rsp.setRetCode(APAudioRecordRsp.MEDIA_RECORDER_PERMISSION_PROMPT);
                rsp.setMsg(e.getMessage());
            } else if (e instanceof RecordIllegalStateException) {
                rsp.setRetCode(APAudioRecordRsp.MEDIA_RECORDER_ILLEGAL_STATE);
                rsp.setMsg(e.getMessage());
            } else if (e instanceof IllegalStateException) {
                rsp.setRetCode(APAudioRsp.CODE_ERROR_START);
                rsp.setMsg("Device prepare recorder failed with IllegalStateException!");
            } else {
                rsp.setRetCode(APAudioRsp.CODE_ERROR_START);
                rsp.setMsg("Device prepare recorder failed!");
            }
            rsp.setAudioInfo(mAudioInfo);
            notifyRecordError(rsp);
            logger.d("recordStart error: " + rsp.getMsg());
            return;
        }
        setupTimer();
        notifyRecordStart();
        mRecordTask.setState(AudioRecordTask.STATE_RUNNING);
    }

    public void cancel() {
        recordCancel();
    }

    public void stop() {
        recordStop();
        mRecordTask.setState(AudioTask.STATE_FINISH);
    }


    //======================== 录音相关实现  ===============================
    /**
     * Recorder 空闲状态
     */
    private static final int STATE_IDLE = 0;
    /**
     * Recorder 就绪状态
     */
    private static final int STATE_PREPARED = 1;
    /**
     * Recorder 正在录音状态
     */
    private static final int STATE_RECORDING = 2;
    /**
     * Recorder 录音结束状态
     */
    private static final int STATE_STOP = 3;
    /**
     * Recorder 录音取消状态
     */
    private static final int STATE_CANCEL = 4;
    /**
     * Recorder 已释放所有资源状态
     */
    private static final int STATE_RELEASE = 5;

    /**
     * 系统Recorder
     */
//    private static MediaRecorder mRecorder = new MediaRecorder();
    private SilkRecorder mRecorder;
    /**
     * 录音权限提示检测时间，超过这时间则认为有录音权限提示框，回调业务录音失败
     */
    private static final int RECORD_PERMISSION_DETECT_TIME = 500;

    private long mRecordStartTime;

    private APAudioInfo mAudioInfo;

    private AudioDjangoExecutor.UploadIntervalTask mUploadIntervalTask;

    private int mState = STATE_IDLE;

    private void recordPrepare() throws Exception {
        bStopping.set(false);
        mRecorder.setFrequency(SilkApi.SAMPLE_RATE_44_1K);
        mRecorder.setupSilkEncoder(SilkApi.COMPRESSION_LOW, SilkApi.SAMPLE_RATE_44_1K, SilkApi.TARGET_RATE_16K);
        setupOutput();
        mRecorder.setRecordErrorListener(new SilkRecorder.OnRecordErrorListener() {
            @Override
            public void onRecordError(SilkRecorder recorder, Exception e) {
                logger.e(e, "OnRecordErrorListener audioInfo: " + mAudioInfo);
                if (mRecorder.isRecording()) {
                    if (e instanceof SilkRecorder.RecordPermissionDeniedException) {
                        notifyRecordError(APAudioRecordRsp.MEDIA_RECORDER_PERMISSION_DENIED, e.getMessage());
                    } else {
                        notifyRecordError(APAudioRsp.CODE_ERROR, e.getMessage());
                    }

                    recordStop();
                }
            }
        });
        mRecorder.prepare();
    }


    /**
     * 开始录音
     */
    private boolean recordStart() {
        long preStartTime = System.currentTimeMillis();
        mRecorder.start();
        long startedTime = System.currentTimeMillis();
        long diff = startedTime - preStartTime;
        logger.d("recordStart usdTime: " + diff);
        if (diff >= RECORD_PERMISSION_DETECT_TIME) {
            reset();
            throw new RecordPermissionRequestException();
        }
        logger.d("mState = " + mState);
        if (mState == STATE_STOP) {
            logger.d("already stop, should end");
            reset();
            throw new RecordIllegalStateException();
        }
        bReset.set(false);
        setState(STATE_RECORDING);
        mRecordStartTime = System.currentTimeMillis();
        return true;
    }

    private void recordStop() {
        recordStop(true);
    }

    /**
     * 停止录音
     */
    private void recordStop(boolean needStop) {
        logger.d("recordStop, recording? " + isRecording());
//        try {
//            closeUploadTask();
//        } catch (Exception e) {/*ignore*/}
        if (isRecording()) {
//            if (needStop) {
//                mRecorder.stop();
//            }
            reset(false);
            mRecordTask.setState(AudioRecordTask.STATE_FINISH);
            long duration = System.currentTimeMillis() - mRecordStartTime;;
            if (duration < mAudioInfo.getRecordMinTime()) {
                cancelSyncUploadTask();
                notifyMinRecordError();
            } else {
                if (duration > mAudioInfo.getRecordMaxTime()) {
                    duration = mAudioInfo.getRecordMaxTime();
                }
                logger.d("recordStop msg: normal stop");
                mAudioInfo.setDuration((int) duration);
                notifyRecordFinished();
            }
        } else {
            logger.d("no record start, but stopped!!!");
            cancelTimer();
            setState(STATE_STOP);
        }
        bStopping.set(false);
    }

    private void reset() {
        reset(true);
    }

    private void reset(boolean interruptEncode) {
        if (!bReset.get() && bPrepared.get()) {
            mRecorder.reset(interruptEncode);
        }
        bReset.set(true);
        bPrepared.set(false);
        cancelTimer();
    }

    private void closeUploadTask() {
//        if (mDjangoDataOutputStream != null) {
//            try {
//                mDjangoDataOutputStream.flush();
//            } catch (Exception e) {
//                logger.e(e, "django data output stream flush error");
//            } finally {//此处不用调用close，真正的close在UploadIntervalTask
//
//            }
//        }
        mDjangoDataOutputStream = null;
        if (mUploadIntervalTask != null) {
            //这里会调用真正的close
            mUploadIntervalTask.notifyStop();
//            mUploadIntervalTask = null;
        }

    }

    private void cancelSyncUploadTask() {
        if (mUploadIntervalTask != null) {
            mUploadIntervalTask.cancel();
        }
    }


    private void recordCancel() {
        setState(STATE_CANCEL);
        reset();
        cancelSyncUploadTask();
        closeUploadTask();
        notifyRecordCancel();
    }

    private void recordRelease() {
        recordReset();
    }

    private void recordReset() {
        reset();
        cancelTimer();
    }

    private void setState(int state) {
        this.mState = state;
    }

    private boolean isRecording() {
        return STATE_RECORDING == mState;
    }

    //设定输出文件
    private void setupOutput() throws IOException {
        mSavePath = mAudioInfo.getSavePath();
//        mAudioInfo.setSavePath(null);//只有成功才回调业务
        File saveFile = new File(mSavePath);
        saveFile.createNewFile();

        FileOutputStream fos = new FileOutputStream(saveFile);
        mLocalDataOutputStream = new BufferedOutputStream(fos);
        //先写入头部
        mLocalDataOutputStream.write(SilkApi.SILK_HEAD.getBytes());

        if (bSyncUpload) {
            mUploadIntervalTask = AudioDjangoExecutor.getInstance(mContext)
                    .uploadAudioInterval(mAudioInfo, null, new AudioDjangoExecutor.UploadIntervalListener() {
                        @Override
                        public void onUploadProgress(APAudioInfo info, long send) {
//                            logger.p("onUploadProgress: " + send);
                        }

                        @Override
                        public boolean onUploadFinished(APAudioInfo info) {
                            logger.d("onUploadFinished, info: " + info);
                            logger.d("onUploadFinished, state: " + mState);
                            if (mState == STATE_STOP || mState == STATE_RECORDING) {
//                                notifyRecordFinished();
                                if (mRecordState == APAudioUploadRsp.STATE_RECORD_FINISHED) {
                                    mUploadIntervalTask.copyToCacheWhileSuccess();
                                    notifyUploadFinished();
                                    return true;
                                } else {
                                    notifyUploadError(APAudioUploadRsp.CODE_SYNC_UPLOAD_ERROR, "record error, upload success, ignore!");
                                }
                            }
                            return false;
                        }

                        @Override
                        public void onUploadError(APAudioInfo info, FileUpResp rsp) {
                            synchronized (DJANGO_SYNC) {
                                try{
                                    notifyUploadError(rsp.getCode(), rsp.getMsg());
                                }catch (Exception e){
                                    logger.e(e, "notifyUploadError exp");
                                } finally {
                                    //发生异常，要close上传任务
                                    closeUploadTask();
                                    mDjangoDataOutputStream = null;
                                }
                            }
                        }
                    });
            mDjangoDataOutputStream = new DataOutputStream(mUploadIntervalTask.getTaskOutput());
        }

        mRecorder.setOutputHandler(new SilkEncoder.EncodeOutputHandler() {
            int errorTimes = 0;
            boolean errorStop = false;
            boolean first = true;
            boolean syncUploadErr = false;
            @Override
            public void handle(byte[] encodeData, int length) {
                if (CompareUtils.in(mRecordState, APAudioUploadRsp.STATE_RECORD_ERROR, APAudioUploadRsp.STATE_RECORD_CANCEL)) return;
                //logger.p("handle encodeData, len: " + length);
                if (length < 0) { //异常编码检测
                    logger.e("handle encodeData length: " + length + ", errorTimes: " + errorTimes + ", info: " + mAudioInfo);
                    errorTimes++;
                    if (errorTimes > 5 && !errorStop) {
                        reset();
                        notifyRecordError(APAudioRecordRsp.MEDAI_RECORDER_ENCODE_ERR, "record encode error");
                        errorStop = true;
                    }
                    return;
                }
                errorTimes = 0;
                try {
                    mLocalDataOutputStream.write(SilkUtils.convertToLittleEndian((short) length));
                    mLocalDataOutputStream.write(encodeData, 0, length);
                } catch (Exception e) {
//                    e.printStackTrace();
                    logger.e(e, "write local data err");
                }
                synchronized (DJANGO_SYNC) {
                    //logger.p("syncUploadErr: %s, bSyncUpload: %s, mDjangoDataOutputStream: %s", syncUploadErr, bSyncUpload, mDjangoDataOutputStream );
                    if (!syncUploadErr && (bSyncUpload && mDjangoDataOutputStream != null)) {
                        //同步上传的话，同时把数据丢服务器
                        try {
                            if (first) {
                                //先写入头部
                                first = false;
                                mDjangoDataOutputStream.write(SilkApi.SILK_HEAD.getBytes());
                            }
                            if(mUploadState == null || !CompareUtils.in(mUploadState.getState(),
                                    APAudioUploadState.STATE_UPLOADING, APAudioUploadState.STATE_ERROR)) {
                                mUploadState = new APAudioUploadState(APAudioUploadState.STATE_UPLOADING);
                            }
                            mDjangoDataOutputStream.write(SilkUtils.convertToLittleEndian((short) length));
                            mDjangoDataOutputStream.write(encodeData, 0, length);
                        } catch (Exception e) {
                            syncUploadErr = true;
//                        e.printStackTrace();
                            logger.e(e, "write django data err");
//                        IOUtils.closeQuietly(mDjangoDataOutputStream);
//                        mDjangoDataOutputStream = null;
                            mUploadState = new APAudioUploadState(APAudioUploadState.STATE_ERROR);
                        }
                    }
                }
            }

            @Override
            public void handleFinished() {
//                notifyRecordFinished();
                if (errorStop || CompareUtils.in(mRecordState, APAudioUploadRsp.STATE_RECORD_ERROR, APAudioUploadRsp.STATE_RECORD_CANCEL)) {
                    IOUtils.closeQuietly(mLocalDataOutputStream);
//                    IOUtils.closeQuietly(mDjangoDataOutputStream);
                    return;
                }
                try {
                    mLocalDataOutputStream.write(SilkApi.SILK_END);
                    mLocalDataOutputStream.flush();
                } catch (IOException e) {
                    logger.e(e, "handleFinished write file silk end error, audioInfo: " + mAudioInfo);
                } finally {
                    IOUtils.closeQuietly(mLocalDataOutputStream);
                }

                if (bSyncUpload && mDjangoDataOutputStream != null) {
                    try {
                        mDjangoDataOutputStream.write(SilkApi.SILK_END);
                        mDjangoDataOutputStream.flush();
                    } catch (IOException e) {
                        logger.e(e, "handleFinished write django silk end error, audioInfo: " + mAudioInfo);
                    }

                    try {
                        closeUploadTask();
                    } catch (Exception e) {
                        logger.e(e, "handleFinished closeUploadTask err");
                    }
                }

                reset();
            }
        });
    }


    private void setupTimer() {
        cancelTimer();
        mRecordAmplitudeTimerTask = new TimerTask() {
            @Override
            public void run() {
                notifyRecordAmplitudeChange();
            }
        };
        mRecordAmplitudeTimer = new Timer("Record_Amplitude_Timer", true);
        mRecordAmplitudeTimer.schedule(mRecordAmplitudeTimerTask, 50, RECORD_AMPLITUDE_CHANGE_UPDATE_PERIOD);

        if (mAudioInfo.getProgressUpdateInterval() > 0) {
            mRecordProgressUpdateTimerTask = new TimerTask() {
                @Override
                public void run() {
                    notifyUpdateProgress();
                }
            };
            mRecordProgressUpdateTimer = new Timer("Record_Progress_Update_Timer", true);
            mRecordProgressUpdateTimer.schedule(mRecordProgressUpdateTimerTask, 1, mAudioInfo.getProgressUpdateInterval());
        }

        if (mAudioInfo.getRecordMaxTime() > 0) {
            mRecordMaxTimeTimerTask = new TimerTask() {
                @Override
                public void run() {
                    recordStop();
                }
            };
            mRecordMaxTimeTimer = new Timer("Record_Max_Time_Timer", true);
            mRecordMaxTimeTimer.schedule(mRecordMaxTimeTimerTask, mAudioInfo.getRecordMaxTime());
        }
    }

    private void cancelTimer() {
        logger.p("cancelTimer: mRecordAmplitudeTimerTask: %s", mRecordAmplitudeTimerTask);
        if (mRecordAmplitudeTimerTask != null) {
            mRecordAmplitudeTimerTask.cancel();
            mRecordAmplitudeTimerTask = null;
        }
        if (mRecordAmplitudeTimer != null) {
            mRecordAmplitudeTimer.cancel();
            mRecordAmplitudeTimer = null;
        }
        logger.p("cancelTimer: mRecordProgressUpdateTimerTask: %s", mRecordProgressUpdateTimerTask);
        if (mRecordProgressUpdateTimerTask != null) {
            mRecordProgressUpdateTimerTask.cancel();
            mRecordProgressUpdateTimerTask = null;
        }
        if (mRecordProgressUpdateTimer != null) {
            mRecordProgressUpdateTimer.cancel();
            mRecordProgressUpdateTimer = null;
        }
        logger.p("cancelTimer: mRecordMaxTimeTimerTask: %s", mRecordMaxTimeTimerTask);
        if (mRecordMaxTimeTimerTask != null) {
            mRecordMaxTimeTimerTask.cancel();
            mRecordMaxTimeTimerTask = null;
        }
        if (mRecordMaxTimeTimer != null) {
            mRecordMaxTimeTimer.cancel();
            mRecordMaxTimeTimer = null;
        }
    }

    //回调相关
    private void notifyRecordStart() {
        mRecordState = APAudioUploadRsp.STATE_RECORD_RECORDING;
        if (mRecordUploadCallback != null) {
            mRecordUploadCallback.onRecordStart(mAudioInfo);
        }
    }

    private void notifyRecordAmplitudeChange() {
        if (mRecordUploadCallback != null) {
            if (isRecording()) {
                mRecordUploadCallback.onRecordAmplitudeChange(mAudioInfo, mRecorder.getMaxAmplitude());
            }
        }
    }

    private void notifyUpdateProgress() {
        if (mRecordUploadCallback != null) {
            if (isRecording()) {
                int recordDuration = (int) ((System.currentTimeMillis() - mRecordStartTime) / 1000);
                mRecordUploadCallback.onRecordProgressUpdate(mAudioInfo, recordDuration);
            }
        }
    }

    private void notifyMinRecordError() {
        mRecordState = APAudioUploadRsp.STATE_RECORD_ERROR;
        APAudioRecordRsp rsp = new APAudioRecordRsp();
        rsp.setRetCode(APAudioRecordRsp.CODE_ERR_MIN_TIME);
        rsp.setAudioInfo(mAudioInfo);
        rsp.setMsg("Record time is less than expect time: " + mAudioInfo.getRecordMinTime());
        logger.d("recordStop msg: " + rsp.getMsg());
        notifyRecordError(rsp);
    }

    private void notifyRecordError(int errorCode, String err) {
        mRecordState = APAudioUploadRsp.STATE_RECORD_ERROR;
        if (mRecordUploadCallback != null) {
            mAudioInfo.getExtra().putInt(APAudioInfo.KEY_UPLOAD_TYPE, APAudioInfo.INT_UPLOAD_TYPE_SYNC);
            if (mState == STATE_STOP) {
                mAudioInfo.getExtra().putBoolean("upload", true);
            }
            APAudioRecordRsp rsp = new APAudioRecordRsp();
            rsp.setRetCode(errorCode);
            rsp.setMsg(err);
            rsp.setAudioInfo(mAudioInfo);
            notifyRecordError(rsp);
        }
    }

    private void notifyRecordError(APAudioRecordRsp rsp) {
        mRecordState = APAudioUploadRsp.STATE_RECORD_ERROR;
        logger.e("notifyRecordError rsp: " + rsp);
        if (APAudioRecordRsp.CODE_ERR_MIN_TIME != rsp.getRetCode()) {
            UCLogUtil.UC_MM_C11(rsp.getRetCode(), rsp.getMsg());
        }
        try {
            reset();
        } catch (Exception e){
            //ignore
        }
        mRecorder = new SilkRecorder();
        if (mRecordUploadCallback != null) {
            mRecordUploadCallback.onRecordError(rsp);
        }
        IOUtils.closeQuietly(mLocalDataOutputStream);
    }

    private void notifyRecordFinished() {
        mRecordState = APAudioUploadRsp.STATE_RECORD_FINISHED;
        mState = STATE_STOP;
        if (mRecordUploadCallback != null) {
            mAudioInfo.setSavePath(mSavePath);
//            mAudioInfo.getExtra().putBoolean("upload", bNeedUpload);
//            mAudioInfo.getExtra().putBoolean("uploadFinish", !bNeedUpload);
            if (bSyncUpload && mUploadState == null) {
                mUploadState = new APAudioUploadState(APAudioUploadState.STATE_UPLOADING);
            }
            mAudioInfo.setUploadState(mUploadState);
            logger.d("notifyRecordFinished mAudioInfo: " + mAudioInfo);
            mRecordUploadCallback.onRecordFinished(mAudioInfo);
        }
        UCLogUtil.UC_MM_C11(0, null);
//        IOUtils.closeQuietly(mLocalDataOutputStream);
    }

    private void notifyRecordCancel() {
        mRecordState = APAudioUploadRsp.STATE_RECORD_CANCEL;
        if (mRecordUploadCallback != null) {
            mRecordUploadCallback.onRecordCancel(mAudioInfo);
        }
        UCLogUtil.UC_MM_C11(0, "cancel");
        IOUtils.closeQuietly(mLocalDataOutputStream);
    }

    private void notifyUploadFinished() {
        mUploadState = new APAudioUploadState(APAudioUploadState.STATE_SUCCESS);
        if (mRecordUploadCallback != null) {
//            mAudioInfo.setSavePath();
            mAudioInfo.getExtra().putInt(APAudioInfo.KEY_UPLOAD_TYPE, APAudioInfo.INT_UPLOAD_TYPE_SYNC);
            mAudioInfo.setUploadState(mUploadState);
            APAudioUploadRsp uploadRsp = new APAudioUploadRsp();
            uploadRsp.setRetCode(APAudioRsp.CODE_SUCCESS);
            uploadRsp.setAudioInfo(mAudioInfo);
            uploadRsp.setMsg("upload success");
            uploadRsp.recordState = mRecordState;
            mRecordUploadCallback.onUploadFinished(uploadRsp);

            logger.p("notifyUploadFinished uploadRsp: " + uploadRsp);
        }
    }

    private void notifyUploadStart() {
        if (mRecordUploadCallback != null) {
            mAudioInfo.getExtra().putInt(APAudioInfo.KEY_UPLOAD_TYPE, APAudioInfo.INT_UPLOAD_TYPE_SYNC);
            mRecordUploadCallback.onUploadStart(mAudioInfo);
        }
    }

    private void notifyUploadError(int code, String msg) {
        mUploadState = new APAudioUploadState(APAudioUploadState.STATE_ERROR);
        logger.d("notifyUploadError code: " + code + ", msg: " + msg + ", info: " + mAudioInfo);
        if (mRecordUploadCallback != null) {
            mAudioInfo.getExtra().putInt(APAudioInfo.KEY_UPLOAD_TYPE, APAudioInfo.INT_UPLOAD_TYPE_SYNC);
            mAudioInfo.setUploadState(mUploadState);
            APAudioUploadRsp rsp = new APAudioUploadRsp();
            rsp.setRetCode(APAudioUploadRsp.CODE_SYNC_UPLOAD_ERROR);
            rsp.setMsg("audio sync upload error, code: " + code + ", msg: " + msg);
            rsp.setAudioInfo(mAudioInfo);
            rsp.recordState = mRecordState;
            logger.e("notifyUploadError rsp: " + rsp);
            mRecordUploadCallback.onUploadError(rsp);
        }
    }



    private class RecordPermissionRequestException extends RuntimeException {
        @Override
        public String getMessage() {
            return "record permission interrupted exception";
        }
    }

    private class RecordIllegalStateException extends RuntimeException {
        @Override
        public String getMessage() {
            return "record sequence error";
        }
    }

    @Override
    protected void finalize() throws Throwable {
        IOUtils.closeQuietly(mLocalDataOutputStream);
        super.finalize();
    }
}
