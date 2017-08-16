package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.tv.danmaku.ijk.media.encode;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import com.naruto.mobile.framework.service.common.multimedia.video.data.APVideoRecordRsp;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Logger;

public class FFmpegMicEncoder extends BaseMicEncoder {
	String TAG = "FFmpegMicEncoder";

	private Logger logger = Logger.getLogger(TAG);

	FFmpegMuxer muxing;
	FFmpegSessionConfig config;
	private AudioRecord audioRecord;
	private int minBufferSize = 0;
	private boolean hasInit = false;

	private volatile boolean isRecording;
	//private Context mContext;
	private Thread mAudioThread;
	public FFmpegMicEncoder(FFmpegSessionConfig sessionConfig) {
		isRecording = true;
		muxing = sessionConfig.getMuxer();
		config = sessionConfig;

		initAudioRecord();
		// boolean b = AcousticEchoCanceler.isAvailable();
		// Log.d(TAG, "AcousticEchoCanceler: " + b);
		// boolean b1 = NoiseSuppressor.isAvailable();
		// Log.d(TAG, "NoiseSuppressor: " + b1);
	}

	// =====================================

	public void startRecording() {
		Log.d(TAG, "startRecording");
		mAudioThread = new Thread(new Runnable() {
			@Override
			public void run() {
				doStartRecord();
			}
		}, "FFmpegMicEncoder");
		mAudioThread.start();
	}

	public void stop() {
		Log.d(TAG, "stop");
		isRecording = false;
		if (mAudioThread != null)
			try
			{
				mAudioThread.join();
			}
			catch (InterruptedException e){
			}
	}

	public boolean isRecording() {
		return isRecording;
	}

	// ================================

	private void doStartRecord() {
		Log.d(TAG, "doStartRecord");

		// NoiseSuppressor a =
		// NoiseSuppressor.create(audioRecord.getAudioSessionId());
		// a.setEnabled(true);
		//
		//
		// AcousticEchoCanceler b =
		// AcousticEchoCanceler.create(audioRecord.getAudioSessionId());
		// b.setEnabled(true);
		short[] tempBuffer;
		boolean hasError = false;
		try {
			audioRecord.startRecording();
			while (this.isRecording) {
				int ret;
				if (config.aEncodeAac == 1) {
					tempBuffer = new short[1024];
					ret = audioRecord.read(tempBuffer, 0, 1024);
				} else {
					tempBuffer = new short[minBufferSize];
					ret = audioRecord.read(tempBuffer, 0, minBufferSize);
				}

				if (ret == AudioRecord.ERROR_INVALID_OPERATION) {
//					throw new IllegalStateException(
//							"read() returned AudioRecord.ERROR_INVALID_OPERATION");
					hasError = true;
					notifyError(APVideoRecordRsp.CODE_ERR_MIC_INVALID_OPERATION);
					break;
				} else if (ret == AudioRecord.ERROR_BAD_VALUE) {
//					throw new IllegalStateException(
//							"read() returned AudioRecord.ERROR_BAD_VALUE");
					notifyError(APVideoRecordRsp.CODE_ERR_MIC_BAD_VALUE);
					hasError = true;
					break;
				}
				// 处理数据
				handleAudioData(tempBuffer, ret);
			}
			if (hasError) {
				logger.d("audio record hasError");
			} else {
				logger.d("audioRecord stop");
			}
		} catch (Exception e) {
//			Log.d(TAG, "stop failed");
			logger.e(e, "record fail");
			if (!hasError) notifyError(APVideoRecordRsp.CODE_ERR_MIC_UNKNOWN_ERROR);
			hasError = true;
		} finally {
			hasError = releaseAudioRecord(hasError);
		}
		hasInit = false;
		if (!hasError) {//notify 录音成功

		}
	}

	private boolean releaseAudioRecord(boolean hasError) {
		try {
            audioRecord.stop();
            audioRecord.release();
        } catch (Exception e) {
            logger.e(e, "stop fail");
            if (!hasError) notifyError(APVideoRecordRsp.CODE_ERR_MIC_STOP_FAILED);
			return true;
        }
		return false;
	}

	private int initAudioRecord() {
		android.os.Process
				.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

		minBufferSize = AudioRecord.getMinBufferSize(
				config.audioSamplerate, AudioFormat.CHANNEL_IN_MONO,
				config.audioFormat);

		logger.d("initAudioRecord minBufferSize:" + minBufferSize);

		if (minBufferSize <= 0) {
			throw new RuntimeException("initAudioRecord getMiniBufferSize err");
		}

		try {
			audioRecord = new AudioRecord(
					MediaRecorder.AudioSource.CAMCORDER, config.audioSamplerate,
					AudioFormat.CHANNEL_IN_MONO, config.audioFormat,
					minBufferSize * 4);
		} catch (Exception e) {
			logger.e(e, "initAudioRecord err");
//			notifyError(APVideoRecordRsp.CODE_ERR_MIC_PERMISSION_DENIED);
			throw new RuntimeException("initAudioRecord err");
		}
		if (audioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
			logger.e("initRecord maybe permission deny");
//			notifyError(APVideoRecordRsp.CODE_ERR_MIC_PERMISSION_DENIED);
//			return -1;
			throw new RuntimeException("initRecord maybe permission deny");
		}
		hasInit = true;
		return minBufferSize;
	}

	long startPTS = 0;
	long totalSamplesNum = 0;

//	private long adjustPTS(long bufferPts, long bufferSamplesNum) {
//		long correctedPts = 0;
//		long bufferDuration = (1000000 * bufferSamplesNum)
//				/ (config.audioSamplerate);
//		// bufferPts -= bufferDuration; // accounts for the delay of acquiring
//		// the audio buffer
//		if (totalSamplesNum == 0) {
//			// reset
//			startPTS = bufferPts;
//			totalSamplesNum = 0;
//		}
//		correctedPts = startPTS + (1000000 * totalSamplesNum)
//				/ (config.audioSamplerate);
//		if (bufferPts - correctedPts >= 2 * bufferDuration) {
//			// reset
//			startPTS = bufferPts;
//			totalSamplesNum = 0;
//			correctedPts = startPTS;
//		}
//		totalSamplesNum += bufferSamplesNum;
//		return correctedPts;
//	}

	private void handleAudioData(short[] data, final int size) {
		// convert to byte[]
		// Log.d("VideoCaptureActivity", "handleAudioData");
		// Log.d("ylf", "handleAudioData");
		ByteBuffer buffer = ByteBuffer.allocate(data.length * 2);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.asShortBuffer().put(data);
		buffer.limit(size * 2);
		final byte[] bytes = buffer.array();
		synchronized (muxing) {
//			Activity activity = (Activity) mContext;
//			activity.runOnUiThread(new Runnable() {
//				//long audioAbsolutePtsUs;
//
//				@Override
//				public void run() {
					// TODO Auto-generated method stub
					Log.d("ylf", "handleAudioData");
					//audioAbsolutePtsUs = (System.nanoTime()) / 1000L;
					//audioAbsolutePtsUs = adjustPTS(audioAbsolutePtsUs, size);
					muxing.putAudio(bytes, size * 2);
//				}
//			});

		}
	}

	@Override
	protected void finalize() throws Throwable {
		if (hasInit) releaseAudioRecord(true);
		super.finalize();
	}
}
