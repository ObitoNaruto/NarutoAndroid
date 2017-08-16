package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.tv.danmaku.ijk.media.encode;

import java.io.File;


import android.media.AudioFormat;
import android.os.Environment;
import android.util.Log;

import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.video.VideoFileManager;

public class FFmpegSessionConfig {
	//For video
	public int vWidth;
	public int vHeight;
	public int vZoom;
	public int vEncode;
	public int vBitrate;
	public int vType;
	public int vHWEncoder;
	public int vFrameRate;
	public int vGOP;
	
	//For audio
	public int audioSamplerate;
	public int audioBitrate;
	public int audioFormat;
	public int aEncode;
	public int aEncodeAac;
	
	public int ffmpegLog;
	public String vPublishUrl;
	public String outType;
	private FFmpegMuxer mMuxer;
    
    public FFmpegSessionConfig()
    {
    	mMuxer = new FFmpegMuxer();
        File rootDir = new File(VideoFileManager.mBaseDir);
        File outputFile = new File(rootDir, String.format("%d.mp4", System.currentTimeMillis()));
        if (!rootDir.exists() && !rootDir.mkdirs())
        {
        	Log.e("file", "mkdirs failure!");
        }
        File nomedia = new File(VideoFileManager.mBaseDir + "/.nomedia" );  
        if (!nomedia.exists())
        {
               try {
                     nomedia.createNewFile();  
              } catch (Exception e) {
                    e.printStackTrace();  
              }
        }
        vPublishUrl = outputFile.getAbsolutePath();
//    	vPublishUrl = "/sdcard/abc.mp4";
    	//mMuxer.init(this);
    }
    public FFmpegMuxer getMuxer()
    {
    	return mMuxer;
    }

    public static FFmpegSessionConfig create()
    {
    	FFmpegSessionConfig session = new FFmpegSessionConfig();
    	
        session.vWidth = 320;
        session.vHeight = 240;
        //need zoom
        session.vZoom = 1;
        session.vBitrate = 384000;
        session.vFrameRate = 12;
        //session.vPublishUrl = "rtmp://112.124.132.20:1935/live/9223370601554113362~AB8LRN2DSRqEfkh-n_T7wCZZP1I?extra=2884,dd83491a847e487e9ff4fbc026593f52,e0c260fd66cd388413699fc8575122fb,null,null";
        //session.vPublishUrl = "/sdcard/abc.flv";
        session.outType = "mp4";
        session.vGOP = 12;
        session.vEncode = 1;
        session.vHWEncoder = 0;

        //audio config
        session.audioSamplerate = 16 * 1000;
        session.audioFormat = AudioFormat.ENCODING_PCM_16BIT;
        session.audioBitrate = 32000;
        session.aEncode = 1;
        session.ffmpegLog = 1;
        session.aEncodeAac = 1;
        
        return session;
    }
    public void swithFlv()
    {
    	vZoom = 0;
    	outType = "flv";
    	aEncodeAac = 0;
		audioSamplerate = 16000;
		audioBitrate = 32000;
    }
}

