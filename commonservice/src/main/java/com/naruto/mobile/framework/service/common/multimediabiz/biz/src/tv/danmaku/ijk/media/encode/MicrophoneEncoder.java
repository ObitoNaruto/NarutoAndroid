package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.tv.danmaku.ijk.media.encode;

import android.annotation.TargetApi;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaRecorder;
import android.os.Build;
import android.util.Log;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;

import com.naruto.mobile.framework.service.common.multimedia.video.data.APVideoRecordRsp;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Logger;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class MicrophoneEncoder extends BaseMicEncoder implements Runnable {
    private static final boolean VERBOSE = true;
    private static final String TAG = "MicrophoneEncoder";
    private Logger logger = Logger.getLogger(TAG);

    protected static final int SAMPLES_PER_FRAME = 1024;     // AAC frame size. Audio encoder input size is a multiple of this
    protected static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    private final Object mReadyFence = new Object();    // Synchronize audio thread readiness
    private boolean mThreadReady;                       // Is audio thread ready
    private boolean mThreadRunning;                     // Is audio thread running
    private final Object mRecordingFence = new Object();

    private AudioRecord mAudioRecord;
    private AudioEncoderCore mEncoderCore;

    private boolean mRecordingRequested;
    private boolean mRecordingStoped;
    private Thread mAudioThread;

    public MicrophoneEncoder(SessionConfig config) throws IOException {
        init(config);
    }

    private void init(SessionConfig config) throws IOException {
        mEncoderCore = new AudioEncoderCore(config.getNumAudioChannels(),
                config.getAudioBitrate(),
                config.getAudioSamplerate(),
                config.getMuxer());
        mMediaCodec = null;
        mThreadReady = false;
        mThreadRunning = false;
        mRecordingRequested = false;
        startThread();
        logger.p("startThread finish");
        if (VERBOSE) logger.i("Finished init. encoder : " + mEncoderCore.mEncoder);
    }

    private boolean setupAudioRecord() {
        int minBufferSize = AudioRecord.getMinBufferSize(mEncoderCore.mSampleRate,
                mEncoderCore.mChannelConfig, AUDIO_FORMAT);


        if (minBufferSize > 0) {
            mAudioRecord = new AudioRecord(
                    MediaRecorder.AudioSource.CAMCORDER, // source
                    mEncoderCore.mSampleRate,            // sample rate, hz
                    mEncoderCore.mChannelConfig,         // channels
                    AUDIO_FORMAT,                        // audio format
                    minBufferSize * 4);                  // buffer size (bytes)


            return mAudioRecord.getState() == AudioRecord.STATE_INITIALIZED;
        }
        return false;
    }

    public void startRecording() {
        if (VERBOSE) logger.i("startRecording");
        synchronized (mRecordingFence) {
            totalSamplesNum = 0;
            startPTS = 0;
            mRecordingRequested = true;
            mRecordingFence.notify();
        }
    }

    public void stopRecording() {
        logger.d("stopRecording");
        synchronized (mRecordingFence) {
            mRecordingRequested = false;
            mRecordingStoped = true;
        }
        synchronized (mRecordingFence) {
            mRecordingFence.notify();
        }
        try {
			mAudioThread.join();
		} catch (Exception e) {
			logger.e(e, "stopRecording");
		}
    }

//    public void reset(SessionConfig config) throws IOException {
//        if (VERBOSE) Log.i(TAG, "reset");
//        if (mThreadRunning) Log.e(TAG, "reset called before stop completed");
//        init(config);
//    }

    public boolean isRecording() {
        return mRecordingRequested;
    }


    private void startThread() {
        synchronized (mReadyFence) {
            if (mThreadRunning) {
                logger.w("Audio thread running when start requested");
                return;
            }
            mAudioThread = new Thread(this, "MicrophoneEncoder");
            mAudioThread.setPriority(Thread.MAX_PRIORITY);
            mAudioThread.start();
            while (!mThreadReady) {
                try {
                    mReadyFence.wait();
                    if (!mThreadReady) {
                        throw new RuntimeException("AudioRecord thread prepared failed!");
                    }
                } catch (InterruptedException e) {
                    logger.e(e, "startThread");
                }
            }
        }
    }

    @Override
    public void run() {
        if (!setupAudioRecord()) {
            mThreadReady = false;
            synchronized (mReadyFence) {
                mReadyFence.notify();
            }
            return;//初始化失败直接退出
        }
        mAudioRecord.startRecording();
        synchronized (mReadyFence){
            mThreadReady = true;
            mReadyFence.notify();
        }

        synchronized (mRecordingFence) {
            while (!mRecordingRequested && !mRecordingStoped) {
                try {
                    mRecordingFence.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        if (mRecordingStoped)
        {
        	logger.d("stop before start");
            mThreadReady = false;
            mAudioRecord.stop();
            mEncoderCore.release();
            mThreadRunning = false;
        	return;
        }
        if (VERBOSE) logger.i("Begin Audio transmission to encoder. encoder : " + mEncoderCore.mEncoder);

        int retCode = 0;
        while (mRecordingRequested) {
            mEncoderCore.drainEncoder(false);
            retCode = sendAudioToEncoder(false);
            if (retCode != APVideoRecordRsp.CODE_SUCCESS) {
                mRecordingRequested = false;
            }
        }
        mThreadReady = false;
        logger.i("Exiting audio encode loop. Draining Audio Encoder");
        if (retCode == APVideoRecordRsp.CODE_SUCCESS) {
            sendAudioToEncoder(true);
        } else {
            notifyError(retCode);
        }
        mAudioRecord.stop();
        mEncoderCore.drainEncoder(true);
        mEncoderCore.release();
        mThreadRunning = false;
    }

    // Variables recycled between calls to sendAudioToEncoder
    MediaCodec mMediaCodec;
    int audioInputBufferIndex;
    int audioInputLength;
    long audioAbsolutePtsUs;

    private int sendAudioToEncoder(boolean endOfStream) {
        if (mMediaCodec == null)
            mMediaCodec = mEncoderCore.getMediaCodec();
        try {
            ByteBuffer[] inputBuffers = mMediaCodec.getInputBuffers();
            audioInputBufferIndex = mMediaCodec.dequeueInputBuffer(-1);
            if (audioInputBufferIndex >= 0) {
                ByteBuffer inputBuffer = inputBuffers[audioInputBufferIndex];
                inputBuffer.clear();
                audioInputLength = mAudioRecord.read(inputBuffer, SAMPLES_PER_FRAME * 2);
                audioAbsolutePtsUs = (System.nanoTime()) / 1000L;
                audioAbsolutePtsUs = adjustPTS(audioAbsolutePtsUs, audioInputLength / 2);

                if(audioInputLength == AudioRecord.ERROR_INVALID_OPERATION) {
                    logger.e("Audio read error: invalid operation");
                    return APVideoRecordRsp.CODE_ERR_MIC_INVALID_OPERATION;
                }
                if (audioInputLength == AudioRecord.ERROR_BAD_VALUE) {
                    logger.e("Audio read error: bad value");
                    return APVideoRecordRsp.CODE_ERR_MIC_BAD_VALUE;
                }
//                if (VERBOSE)
//                    Log.i(TAG, "queueing " + audioInputLength + " audio bytes with pts " + audioAbsolutePtsUs);
                if (endOfStream) {
                    if (VERBOSE) logger.i("EOS received in sendAudioToEncoder");
                    mMediaCodec.queueInputBuffer(audioInputBufferIndex, 0, audioInputLength, audioAbsolutePtsUs, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                } else {
                    mMediaCodec.queueInputBuffer(audioInputBufferIndex, 0, audioInputLength, audioAbsolutePtsUs, 0);
                }
                return APVideoRecordRsp.CODE_SUCCESS;
            }
            return APVideoRecordRsp.CODE_ERR_MIC_INVALID_BUFFER_INDEX;
        } catch (Exception t) {
            logger.e(t, "_offerAudioEncoder exception");
//            t.printStackTrace();
            return APVideoRecordRsp.CODE_ERR_MIC_UNKNOWN_ERROR;
        }
    }

    long startPTS = 0;
    long totalSamplesNum = 0;

    private long adjustPTS(long bufferPts, long bufferSamplesNum) {
        long correctedPts = 0;
        long bufferDuration = (1000000 * bufferSamplesNum) / (mEncoderCore.mSampleRate);
        //bufferPts -= bufferDuration; // accounts for the delay of acquiring the audio buffer
        if (totalSamplesNum == 0) {
            // reset
            startPTS = bufferPts;
            totalSamplesNum = 0;
        }
        correctedPts = startPTS +  (1000000 * totalSamplesNum) / (mEncoderCore.mSampleRate);
        if(bufferPts - correctedPts >= 2*bufferDuration) {
            // reset
            startPTS = bufferPts;
            totalSamplesNum = 0;
            correctedPts = startPTS;
        }
        totalSamplesNum += bufferSamplesNum;
        return correctedPts;
    }
}
