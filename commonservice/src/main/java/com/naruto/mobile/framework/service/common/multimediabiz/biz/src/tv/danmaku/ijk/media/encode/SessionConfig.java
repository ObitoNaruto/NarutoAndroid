package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.tv.danmaku.ijk.media.encode;

import android.util.Log;

import java.io.File;
import java.util.UUID;

import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.video.VideoFileManager;


public class SessionConfig {
    private int mWidth = 320;
    private int mHeight = 568;
    private int mVideoBitrate = 700000;

    private int mAudioSamplerate = 44100;
    private int mAudioBitrate = 16000;
    private int mNumAudioChannels = 1;

    private AndroidMuxer mMuxer;

    private File mOutputFile;
    private UUID mUUID;

    public SessionConfig() {
        mUUID = UUID.randomUUID();

//        File rootDir = new File(Environment.getExternalStorageDirectory(), "ylf");
//        File outputDir = new File(rootDir, mUUID.toString());
//        File outputFile = new File(outputDir, String.format("%d.mp4", System.currentTimeMillis()));
//        outputDir.mkdirs();
//        mOutputDirectory = outputDir;
//        mMuxer = AndroidMuxer.create(outputFile.getAbsolutePath());
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
        mMuxer = AndroidMuxer.create(outputFile.getAbsolutePath());
        mOutputFile = outputFile;
    }

    public UUID getUUID() {
        return mUUID;
    }
    public File getOutputFile()
    {
    	return mOutputFile;
    }
    public AndroidMuxer getMuxer() {
        return mMuxer;
    }

    public int getTotalBitrate() {
        return mVideoBitrate + mAudioBitrate;
    }

    public int getVideoWidth() {
        return mWidth;
    }

    public int getVideoHeight() {
        return mHeight;
    }

    public int getVideoBitrate() {
        return mVideoBitrate;
    }

    public int getNumAudioChannels() {
        return mNumAudioChannels;
    }

    public int getAudioBitrate() {
        return mAudioBitrate;
    }

    public int getAudioSamplerate() {
        return mAudioSamplerate;
    }
}
