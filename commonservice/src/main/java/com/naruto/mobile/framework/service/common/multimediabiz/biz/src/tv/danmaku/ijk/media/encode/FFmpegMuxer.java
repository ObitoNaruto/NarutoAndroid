package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.tv.danmaku.ijk.media.encode;

import android.util.Log;

/**
 * 
 * 
 * Java wrapper of muxing.
 */
public final class FFmpegMuxer {
	private final static String TAG = "FFmpegMuxer";

	// private FFmpegSessionConfig config;

	private static volatile boolean mIsLibLoaded = false;

	public static void loadLibrariesOnce() {
		synchronized (FFmpegMuxer.class) {
			if (!mIsLibLoaded) {
				System.loadLibrary("ijkffmpeg");
				System.loadLibrary("ijkutil");
				System.loadLibrary("ijksdl");
				System.loadLibrary("ijkmuxing");
				System.loadLibrary("ijkplayer");
				mIsLibLoaded = true;
			}
		}
	}

	public FFmpegMuxer() {
		loadLibrariesOnce();
	}

	public int init(FFmpegSessionConfig cfg) {
		// this.config = cfg;
		int result = -1;

		Log.w(TAG, "set Muxing to softencoder");
		result = _init(cfg);
		if (result != 0) {
			Log.w(TAG, "Muxing init failed");
			return result;
		}
		return result;
	}

//	public int convert(String videoFile, String outPath) {
//		Log.i(TAG, "Muxing convert");
//		return videoConvertPicture(videoFile, outPath);
//	}

	public void uninit() {
		Log.i(TAG, "Muxing uninit");
		_uninit();
	}

	public void putAudio(byte[] data, int size) {
		putAudioData(data, size);
	}

	public void putVideo(byte[] data, int size, long pts, int rotation) {
		putVideoData(data, size, pts, rotation);
	}

	private native int _init(FFmpegSessionConfig cfg);

	private native void _uninit();

	private native void putAudioData(byte[] data, int size);

	private native void putVideoData(byte[] data, int size, long pts, int rotation);

}
