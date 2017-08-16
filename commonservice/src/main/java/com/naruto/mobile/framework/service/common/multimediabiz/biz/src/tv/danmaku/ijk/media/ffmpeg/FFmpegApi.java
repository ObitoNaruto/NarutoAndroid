package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.tv.danmaku.ijk.media.ffmpeg;


public class FFmpegApi {
	private final static int H264 = 28;
	private final static int AAC = 86018;

	public static native String avBase64Encode(byte in[]);

	private static native FFmpegVideoInfo dumpVideoInfo(String videoFile);

	// return 2 视频不需要压缩，源文件的 height、width、bit_rate小余或等于720P；0:压缩失败； 1：压缩成功
	public static native int videoCompress(FFmpegVideoCompressConfig cfg);

	public int getH264Code() {
		return H264;
	}

	public int getAACCode() {
		return AAC;
	}

	public static FFmpegVideoInfo getVideoInfo(String videoFile) {
		FFmpegVideoInfo info = dumpVideoInfo(videoFile);
		info.H264 = H264;
		info.AAC = AAC;
		return info;
	}

}
