package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.tv.danmaku.ijk.media.ffmpeg;

public class FFmpegVideoCompressConfig {
	// For video
	public int width;
	public int height;
	public int bitrate;

	public String inputPath;
	public String outputPath;

	// 1080P
	// Duration: 00:00:22.66, start: 0.000000, bitrate: 20426 kb/s
	// Stream #0:0(eng): Video: h264 (Baseline) (avc1 / 0x31637661), yuv420p,
	// 1920x1080, 19956 kb/s, SAR 1:1 DAR 16:9, 27.60 fps, 25.25 tbr, 90k tbn,
	// 180k tbc (default)
	// Metadata:
	// rotate : 90
	// creation_time : 2015-07-24 12:45:50
	// handler_name : VideoHandle
	// Side data:
	// displaymatrix: rotation of -90.00 degrees
	// Stream #0:1(eng): Audio: aac (LC) (mp4a / 0x6134706D), 48000 Hz, stereo,
	// fltp, 192 kb/s (default)
	// Metadata:
	// creation_time : 2015-07-24 12:45:50
	// handler_name : SoundHandle

	// 720P
	// Duration: 00:00:14.81, start: 0.000000, bitrate: 12575 kb/s
	// Stream #0:0(eng): Video: h264 (Baseline) (avc1 / 0x31637661), yuv420p,
	// 1280x720, 11949 kb/s, SAR 1:1 DAR 16:9, 30.34 fps, 30.35 tbr, 90k tbn,
	// 180k tbc (default)
	// Metadata:
	// creation_time : 2015-07-26 03:19:39
	// handler_name : VideoHandle
	// Stream #0:1(eng): Audio: aac (LC) (mp4a / 0x6134706D), 48000 Hz, stereo,
	// fltp, 192 kb/s (default)
	//文件大小
	// >>> str(float((20426*1000/8)*float(22.68)/1024/1024))+"M"
	// '55.2250957489M'
	// >>> str(float((11537*1000/8)*float(22.68)/1024/1024))+"M"
	// '31.1922025681M'
	public static FFmpegVideoCompressConfig create720P() {
		FFmpegVideoCompressConfig config = new FFmpegVideoCompressConfig();
		// need zoom
//		config.width = 1280;
//		config.height = 720;
//		config.bitrate = 791 * 1000;
		config.width = 360;
		config.height = 640;
		config.bitrate = 791 * 1000;
		return config;
	}
}
