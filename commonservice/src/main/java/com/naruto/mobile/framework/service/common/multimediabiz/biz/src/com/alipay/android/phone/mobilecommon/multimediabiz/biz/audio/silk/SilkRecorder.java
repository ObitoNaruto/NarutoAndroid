package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.audio.silk;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.AppUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Logger;

/**
 * Silk 录音采集器
 * Created by jinmin on 15/5/21.
 */
public class SilkRecorder {

    private static final String TAG = "SilkRecorder";

    private static final int TEST_ZERO_PACKET_SIZE = 30;

    private Logger logger = Logger.getLogger(TAG);

    public interface OnRecordErrorListener {
        void onRecordError(SilkRecorder recorder, Exception e);
    }

    public static final int FREQUENCY_8000 = 8000;
    public static final int FREQUENCY_11025 = 11025;
    public static final int FREQUENCY_22050 = 22050;
    public static final int FREQUENCY_44100 = 44100;

    private volatile boolean isRecording;
    private final Object mutex = new Object();
    private int frequency = FREQUENCY_22050;
    private int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    public int packetSize = 480;
    private AudioRecord mRecorder;
    private OnRecordErrorListener mErrorListener;

    private int maxAmplitude = 0;

    private SilkEncoder mEncoder;
    private int encodeCompression = SilkApi.COMPRESSION_LOW;
    private int encodeSampleRate = SilkApi.SAMPLE_RATE_16K;
    private int encodeTargetRate = SilkApi.TARGET_RATE_16K;
    private SilkEncoder.EncodeOutputHandler encodeOutputHandler;

    private Thread mThread;

    public SilkRecorder() {

    }

    public void setOutputHandler(SilkEncoder.EncodeOutputHandler handler) {
        this.encodeOutputHandler = handler;
    }

    public void setRecordErrorListener(OnRecordErrorListener listener) {
        this.mErrorListener = listener;
    }

    public void setAudioEncoding(int audioEncoding) {
        this.audioEncoding = audioEncoding;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public void setupSilkEncoder(int compression, int sampleRate, int targetRate) {
        this.encodeCompression = compression;
        this.encodeSampleRate = sampleRate;
        this.encodeTargetRate = targetRate;
    }

    public void prepare() {
        //silk encoder prepare
        prepareSilkEncoder();
        //silk recorder prepare
        prepareAudioRecorder();
    }

    private void prepareSilkEncoder() {
        packetSize = frequency * 20 / 1000;
        mEncoder = new SilkEncoder(encodeCompression, encodeSampleRate, encodeTargetRate);
        mEncoder.setEncodeHandler(encodeOutputHandler);
        logger.d("prepareSilkEncoder encodeCompression: " + this.encodeCompression + "，encodeSampleRate: " +
                this.encodeSampleRate + ", encodeTargetRate: " + encodeTargetRate);
    }

    private void prepareAudioRecorder() {
        int bufferSize = AudioRecord.getMinBufferSize(frequency, AudioFormat.CHANNEL_IN_MONO, audioEncoding);

        bufferSize = Math.max(packetSize, bufferSize);
        mRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                frequency, AudioFormat.CHANNEL_IN_MONO, audioEncoding, bufferSize);

        logger.d("prepareAudioRecorder bufferSize: " + bufferSize + "，frequency: " +
                frequency + ", audioEncoding: " + audioEncoding + ", state: " + mRecorder.getState());
        if (mRecorder.getState() != AudioRecord.STATE_INITIALIZED) {
            throwPermissionDenied();
        }
    }

    public void start() {
        if (!isRecording()) {
            setRecording(true);
            try {
                mRecorder.startRecording();
            } catch (Exception e) {
                throw new RecordPermissionDeniedException("Record Permission denied, maybe 360 refused!!");
            }
        }
        logger.d("start, recordState: %s, recordingState: %s", mRecorder.getState(), mRecorder.getRecordingState());
		//mx fixed
        if (mRecorder.getState() == AudioRecord.STATE_INITIALIZED && mRecorder.getRecordingState() == AudioRecord.RECORDSTATE_STOPPED) {
            throwPermissionDenied();
        }
        if (mThread == null || mThread.isInterrupted() || !mThread.isAlive()) {
            mThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    if (mEncoder != null) {
                        mEncoder.start();
                    }
                    doRecord();
                }
            });
            mThread.start();
        }
    }

    public void stop() {
        setRecording(false);
    }

    public void reset() {
        reset(true);
    }

    public void reset(boolean resetEncoder) {
        stop();
        if (mThread != null && (mThread.isAlive() || !mThread.isInterrupted())) {
            mThread.interrupt();
            mThread = null;
        }
        if (mRecorder != null && mRecorder.getState() == AudioRecord.STATE_INITIALIZED) {
            mRecorder.release();
            mRecorder = null;
        }
        if (mEncoder != null) {
            mEncoder.stop();
            if (resetEncoder) {
                mEncoder.reset();
                mEncoder = null;
            }
        }
        if (mRecorder == null && mEncoder == null) {
            mErrorListener = null;
        }
    }



    public int getMaxAmplitude() {
        if (mRecorder.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
            return maxAmplitude;
        }
        maxAmplitude = 0;
        return maxAmplitude;
    }

    private void doRecord() {

        synchronized (mutex) {
            while (!this.isRecording) {
                try {
                    mutex.wait();
                } catch (InterruptedException e) {
                    throw new IllegalStateException("Wait() interrupted!", e);
                }
            }
        }
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

        short[] tempBuffer = new short[packetSize];
        short[] cacheBuffer = new short[packetSize];
        int cacheSize = 0;
        int restLength = 0;

        boolean readErr = false;//记录是否有异常信息
        boolean firstEnter = true;//标志是否第一次获取采样数据
        int testZeroPacket = 0;//统计全0数据包，有数据就直接归0
        boolean hasData = false;//有数据，就不再每次检查数据了，提高性能
        int bufferRead;
        while (this.isRecording) {
            bufferRead = mRecorder.read(tempBuffer, 0, packetSize);
            if (bufferRead == AudioRecord.ERROR_INVALID_OPERATION) {
                logger.e("doRecord bufferRead ERROR_INVALID_OPERATION");
                if (mErrorListener != null) {
                    mErrorListener.onRecordError(SilkRecorder.this,
                            new IllegalStateException("read() returned AudioRecord.ERROR_INVALID_OPERATION"));
                }
                break;
            } else if (bufferRead == AudioRecord.ERROR_BAD_VALUE) {
                logger.e("doRecord bufferRead ERROR_BAD_VALUE");
                if (mErrorListener != null) {
                    mErrorListener.onRecordError(SilkRecorder.this,
                            new IllegalStateException("read() returned AudioRecord.ERROR_BAD_VALUE"));
                }
                break;
            } else if (bufferRead == 0) {
                if (!readErr) {
                    readErr = true;
                    AppUtils.sleep(20);
                    continue;
                }
                logger.e("doRecord firstEnter but read bufferRead: %s", bufferRead);
                notifyRecordPermissionDenied("maybe huawei permission denied");
                break;
            } else if (bufferRead < 0 && firstEnter) {
                logger.e("doRecord firstEnter but read bufferRead: %s", bufferRead);
            }
            //已经有数据的，就不再检查数据了，没有数据，检查 TEST_ZERO_PACKET_SIZE 个连续的包是否都为 0
            if (!hasData && !checkData(tempBuffer)) {
//                    throwPermissionDenied();
                if (testZeroPacket++ > TEST_ZERO_PACKET_SIZE) {
                    logger.e("doRecord firstEnter but all data is zero!!");
                    notifyRecordPermissionDenied("maybe lbe permission denied");
                    break;
                }
                continue;
            }
            hasData = true;
            testZeroPacket = 0;
            firstEnter = false;
            readErr = false;
            if (bufferRead < packetSize) {
                int length = packetSize - cacheSize;
                length = Math.min(length, bufferRead);
                restLength = bufferRead > length ? bufferRead - length : 0;
                System.arraycopy(tempBuffer, 0, cacheBuffer, cacheSize, length);
                cacheSize += length;
            } else {
                System.arraycopy(tempBuffer, 0, cacheBuffer, 0, bufferRead);
                cacheSize = packetSize;
            }
            if (cacheSize == packetSize) {
                mEncoder.add(cacheBuffer, cacheSize);
                cacheSize = 0;
            }
            if (restLength > 0) {
                System.arraycopy(tempBuffer, bufferRead-restLength, cacheBuffer, cacheSize, restLength);
                cacheSize += restLength;
            }
            maxAmplitude = 0;
            for (int i = 0; i < bufferRead; i++) {
                if (maxAmplitude < tempBuffer[i]) {
                    maxAmplitude = tempBuffer[i];
                }
            }
        }
        if (mRecorder != null && mRecorder.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
            mRecorder.stop();
        }
        //tell encoder to stop.
        if (mEncoder != null) {
            mEncoder.stop();
        }
    }

    private void notifyRecordPermissionDenied(String msg) {
        if (mErrorListener != null) {
            mErrorListener.onRecordError(SilkRecorder.this,
                    new RecordPermissionDeniedException(msg));
        }
    }

    private void throwPermissionDenied() {
        reset();
        throw new RecordPermissionDeniedException("Record Permission denied");
    }

    private void setRecording(boolean isRecording) {
        synchronized (mutex) {
            this.isRecording = isRecording;
            if (this.isRecording) {
                mutex.notify();
            }
        }
    }

    public boolean isRecording() {
        synchronized (mutex) {
            return isRecording;
        }
    }

    private boolean checkData(short[] data) {
        if (data != null && data.length > 0) {
            for (short d : data) {
                if (d != 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public static class RecordPermissionDeniedException extends RuntimeException {
        public RecordPermissionDeniedException(String msg) {
            super(msg);
        }
    }

    public static class RecordUnsupportedException extends RuntimeException {
        public RecordUnsupportedException(String msg) {
            super(msg);
        }
    }
}
