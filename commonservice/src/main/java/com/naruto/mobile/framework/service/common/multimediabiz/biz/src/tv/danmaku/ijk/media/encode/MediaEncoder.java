package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.tv.danmaku.ijk.media.encode;///*
// * Copyright 2014 Google Inc. All rights reserved.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package tv.danmaku.ijk.media.encode;
//
//import android.annotation.TargetApi;
//import android.app.Activity;
//import android.content.Context;
//import android.media.MediaCodec;
//import android.media.MediaCodec.BufferInfo;
//import android.media.AudioFormat;
//import android.media.AudioRecord;
//import android.media.MediaCodecInfo;
//import android.media.MediaFormat;
//import android.media.MediaMuxer;
//import android.media.MediaRecorder;
//import android.os.Build;
//import android.os.Handler;
//import android.os.HandlerThread;
//import android.os.Looper;
//import android.os.Message;
//import android.util.Log;
//import android.view.Surface;
//import android.widget.Toast;
//
//import java.io.IOException;
//import java.lang.ref.WeakReference;
//import java.nio.ByteBuffer;
//import java.util.Timer;
//import java.util.TimerTask;
//import java.util.concurrent.ArrayBlockingQueue;
//
//@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
//public class MediaEncoder {
//    private static final String TAG = "MediaEncoder";
//    private static final boolean VERBOSE = true;
//
//    private static final String MIME_TYPE = "video/avc";    // H.264 Advanced Video Coding
//    private static final int IFRAME_INTERVAL = 5;           // sync frame every second
//    private static final String AUDIO_MIME_TYPE = "audio/mp4a-latm";
//
//    private HandlerThread mVideoThread;
//    private AudioThread mAudioThread;
//    
//    private VideoHandler mVideoHandler;
//    
//    private MediaCodec mVideoEncoder;
//    private MediaCodec mAudioEncoder;
//    
//    private TrackIndex mVideoTrackIndex = new TrackIndex();
//    private TrackIndex mAudioTrackIndex = new TrackIndex();
//    
//    private Surface mInputSurface;
//    private int mWidth;
//    private int mHeight;
//    private int mBitRate;
//    private int mFrameRate;
//    private BufferInfo mVideoBufferInfo;
//    private BufferInfo mAudioBufferInfo;
//    private boolean mMuxerStarted;
//    private MediaMuxer mMuxer;
//    
//    public static final int SAMPLE_RATE = 44100;
//    public static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
//    public static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
//    public static final int SAMPLE_PER_FRAME = 1024;
//    public static final int FRAMES_PER_BUFFER = 24;
//    ArrayBlockingQueue<byte[]> mDataBuffer = new ArrayBlockingQueue<byte[]>(50);
//    private AudioRecord mAudioRecorder;
//    private boolean mIsRecordingAudio;
//    private Activity mContext;
//    
//    private Timer mTimer = new Timer("record_timer", true);
//
//	public MediaEncoder(int width, int height, int bitRate, int frameRate)
//			throws IOException {
//		mWidth = width;
//		mHeight = height;
//		mBitRate = bitRate;
//		mFrameRate = frameRate;
//		mVideoBufferInfo = new BufferInfo();
//		mAudioBufferInfo = new BufferInfo();
//    }
//    /**
//     * Returns the encoder's input surface.
//     */
//    public Surface getInputSurface() {
//        return mInputSurface;
//    }
//    
//    public void start()
//    {
//    	Log.d(TAG, "######start begin");
//        initMuxer();
//    	prepareEncoder();
//    	prepareVideoThread();
//    	prepareAudioThread();
////        mTimer.schedule(new TimerTask() {
////			@Override
////			public void run() {
////				// TODO Auto-generated method stub
////				shutdown();
////				mContext.runOnUiThread(new Runnable() {
////					@Override
////					public void run() {
////						// TODO Auto-generated method stub
////						Toast.makeText(mContext, "视频录制完成", Toast.LENGTH_SHORT).show();
////					}
////				});
////			}
////		}, 7000);
//        Log.d(TAG, "#######start end");
//    }
//    private void prepareEncoder()
//    {
//        MediaFormat format = MediaFormat.createVideoFormat(MIME_TYPE, mWidth, mHeight);
//        format.setInteger(MediaFormat.KEY_COLOR_FORMAT,
//                MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
//        format.setInteger(MediaFormat.KEY_BIT_RATE, 384000);
//        format.setInteger(MediaFormat.KEY_FRAME_RATE, 19);
//        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 3);
//        if (VERBOSE) Log.d(TAG, "format: " + format);
//        try {
//        	mVideoEncoder = MediaCodec.createEncoderByType(MIME_TYPE);
//		} catch (IOException e) {
//			// TODO: handle exception
//		}
//        
//        mVideoEncoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
//        mInputSurface = mVideoEncoder.createInputSurface();
//        mVideoEncoder.start();
//
//        MediaFormat audioFormat = new MediaFormat();
//        audioFormat.setString(MediaFormat.KEY_MIME, AUDIO_MIME_TYPE);
//        audioFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
//        audioFormat.setInteger(MediaFormat.KEY_SAMPLE_RATE, 44100);
//        audioFormat.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 1);
//        audioFormat.setInteger(MediaFormat.KEY_BIT_RATE, 96000);
//        audioFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 16384);
//        if (VERBOSE) Log.d(TAG, "format: " + audioFormat);
//        try {
//        	mAudioEncoder = MediaCodec.createEncoderByType(AUDIO_MIME_TYPE);
//		} catch (IOException e) {
//			// TODO: handle exception
//		}
//        mAudioEncoder.configure(audioFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
//        mAudioEncoder.start();
//    }
//    private void prepareAudioThread()
//    {
//    	mAudioThread = new AudioThread();
//    	mAudioThread.start();
//    }
//
//    private void prepareVideoThread()
//    {
//        mVideoThread = new HandlerThread("video_encode");
//        mVideoThread.start();
//        mVideoHandler = new VideoHandler(mVideoThread.getLooper(), this);
//    }
//    private void initMuxer()
//    {
//    	try {
//        	mMuxer = new MediaMuxer("/sdcard/good.mp4", MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
//		} catch (Exception e)
//		{
//		}
//    }
//    public void shutdown() {
//        if (VERBOSE) Log.d(TAG, "releasing encoder objects");
//        if (mVideoThread != null && mVideoThread.getLooper() != null)
//        	mVideoThread.getLooper().quit();
//        mIsRecordingAudio = false;
//        try {
//        	if (mVideoThread != null)
//        		mVideoThread.join();
//        	if (mAudioThread != null)
//        		mAudioThread.join();
//        } catch (InterruptedException ie) {
//            Log.w(TAG, "Encoder thread join() was interrupted", ie);
//        }
//        if (mVideoEncoder != null) {
//            mVideoEncoder.stop();
//            mVideoEncoder.release();
//            mVideoEncoder = null;
//        }
//        
//        if (mAudioRecorder != null) {
//        	mAudioRecorder.setRecordPositionUpdateListener(null);
//            mAudioRecorder.release();
//            mAudioRecorder = null;
//            Log.i(TAG, "mAudioRecorder stopped");
//        }
//        
//        if (mAudioEncoder != null) {
//        	mAudioEncoder.stop();
//        	mAudioEncoder.release();
//        	mAudioEncoder = null;
//        }
//        
//        releaseMuxer();
//    }
//	
//	private void releaseMuxer()
//    {
//    	if (mMuxer != null)
//    	{
//    		mMuxer.stop();
//    		mMuxer.release();
//    		mMuxer = null;
//    	}
//    	mAudioTrackIndex.index = -1;
//    	mVideoTrackIndex.index = -1;
//    	mMuxerStarted = false;
//    }
////	private void encodeAudioData(long ts)
////	{
////        byte[] this_buffer;
////        if(mDataBuffer.isEmpty())
////        {
////            this_buffer = new byte[SAMPLE_PER_FRAME];
////            //Log.i(TAG, "Audio buffer empty. added new buffer");
////        }
////        else
////        {
////            this_buffer = mDataBuffer.poll();
////        }
////        int read_result = mAudioRecorder.read(this_buffer, 0, SAMPLE_PER_FRAME * 2);
////
////        if(read_result == AudioRecord.ERROR_BAD_VALUE || read_result == AudioRecord.ERROR_INVALID_OPERATION)
////        {
////            Log.e(TAG, "audio read error");
////        }
////        //buffer_write_index = (buffer_write_index + samples_per_frame) % buffer_size;
////        if(mAudioEncoder != null){
////            drainEncoder(mAudioEncoder, mAudioBufferInfo, mAudioTrackIndex);
////        }
////        
////        // send current frame data to encoder
////        try {
////            ByteBuffer[] inputBuffers = mAudioEncoder.getInputBuffers();
////            int inputBufferIndex = mAudioEncoder.dequeueInputBuffer(-1);
////            if (inputBufferIndex >= 0) {
////                ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
////                inputBuffer.clear();
////                inputBuffer.put(this_buffer);
////                Log.d(TAG, "audio queueInputBuffer ts: " + ts);
////                mAudioEncoder.queueInputBuffer(inputBufferIndex, 0, this_buffer.length, ts / 1000, 0);
////            }
////        } catch (Throwable t) {
////            Log.e(TAG, "offerAudioEncoder exception");
////        }
////        
////	}
//    public void frameAvailableSoon() {
//    	mVideoHandler.sendMessage(mVideoHandler.obtainMessage(
//                VideoHandler.MSG_FRAME_AVAILABLE_SOON));
//    }
//    
//    class TrackIndex {
//        int index = -1;
//    }
//    
//    private class AudioThread extends Thread
//    {	
//		@Override
//		public void run()
//		{
//	        int buffer_size = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);
//
//	        //data_buffer = new byte[samples_per_frame]; // filled directly by hardware
//	        for(int x=0; x < 25; x++)
//	            mDataBuffer.add(new byte[SAMPLE_PER_FRAME]);
//
//	        mAudioRecorder = new AudioRecord(
//	                MediaRecorder.AudioSource.CAMCORDER,       // source
//	                SAMPLE_RATE,                         // sample rate, hz
//	                CHANNEL_CONFIG,                      // channels
//	                AUDIO_FORMAT,                        // audio format
//	                buffer_size * 4);                        // buffer size (bytes)
//	        mAudioRecorder.startRecording();
//	        mIsRecordingAudio = true;
//	        while(mIsRecordingAudio)
//	        {
//	        	drainEncoder(mAudioEncoder, mAudioBufferInfo, mAudioTrackIndex);
//	        	sendAudioToEncoder(false);
//	        }
//	        sendAudioToEncoder(true);
//		}
//    }
//    
//    private void sendAudioToEncoder(boolean endOfStream)
//    {
//        // send current frame data to encoder
//        try {
//            ByteBuffer[] inputBuffers = mAudioEncoder.getInputBuffers();
//            int audioInputBufferIndex = mAudioEncoder.dequeueInputBuffer(-1);
//            if (audioInputBufferIndex >= 0) {
//                ByteBuffer inputBuffer = inputBuffers[audioInputBufferIndex];
//                inputBuffer.clear();
//                int audioInputLength = mAudioRecorder.read(inputBuffer, SAMPLE_PER_FRAME * 2);
//                long audioAbsolutePtsUs = (System.nanoTime()) / 1000L;
//                // We divide audioInputLength by 2 because audio samples are
//                // 16bit.
//                audioAbsolutePtsUs = getJitterFreePTS(audioAbsolutePtsUs, audioInputLength / 2);
//
//                if(audioInputLength == AudioRecord.ERROR_INVALID_OPERATION)
//                    Log.e(TAG, "Audio read error: invalid operation");
//                if (audioInputLength == AudioRecord.ERROR_BAD_VALUE)
//                    Log.e(TAG, "Audio read error: bad value");
////                if (VERBOSE)
////                    Log.i(TAG, "queueing " + audioInputLength + " audio bytes with pts " + audioAbsolutePtsUs);
//                if (endOfStream) {
//                    if (VERBOSE) Log.i(TAG, "EOS received in sendAudioToEncoder");
//                    mAudioEncoder.queueInputBuffer(audioInputBufferIndex, 0, audioInputLength, audioAbsolutePtsUs, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
//                } else {
//                	mAudioEncoder.queueInputBuffer(audioInputBufferIndex, 0, audioInputLength, audioAbsolutePtsUs, 0);
//                }
//            }
//        } catch (Throwable t) {
//            Log.e(TAG, "_offerAudioEncoder exception");
//        }
//    }
//    
//    long startPTS = 0;
//    long totalSamplesNum = 0;
//    
//    private long getJitterFreePTS(long bufferPts, long bufferSamplesNum) {
//        long correctedPts = 0;
//        long bufferDuration = (1000000 * bufferSamplesNum) / (SAMPLE_RATE);
//        bufferPts -= bufferDuration; // accounts for the delay of acquiring the audio buffer
//        if (totalSamplesNum == 0) {
//            // reset
//            startPTS = bufferPts;
//            totalSamplesNum = 0;
//        }
//        correctedPts = startPTS +  (1000000 * totalSamplesNum) / (SAMPLE_RATE);
//        if(bufferPts - correctedPts >= 2*bufferDuration) {
//            // reset
//            startPTS = bufferPts;
//            totalSamplesNum = 0;
//            correctedPts = startPTS;
//        }
//        totalSamplesNum += bufferSamplesNum;
//        return correctedPts;
//    }
//    
//    private static class VideoHandler extends Handler {
//        public static final int MSG_FRAME_AVAILABLE_SOON = 1;
//        private WeakReference<MediaEncoder> mReference;
//        
//        VideoHandler(Looper looper, MediaEncoder encoder)
//    	{
//    		super(looper);
//    		mReference = new WeakReference<MediaEncoder>(encoder);
//    	}
//
//        @Override
//        public void handleMessage(Message msg) {
//            int what = msg.what;
//            switch (what) {
//                case MSG_FRAME_AVAILABLE_SOON:
//                    frameAvailableSoon();
//                    break;
//                default:
//                    throw new RuntimeException("unknown message " + what);
//            }
//        }
//		
//        void frameAvailableSoon() {
//            if (VERBOSE) Log.d(TAG, "frameAvailableSoon");
//            MediaEncoder mediaEncoder = mReference.get();
//            if (mediaEncoder == null)
//            {
//            	Log.d(TAG, "encoder is dead!!!");
//            	return;
//            }
//            mediaEncoder.drainEncoder(mediaEncoder.mVideoEncoder, mediaEncoder.mVideoBufferInfo, mediaEncoder.mVideoTrackIndex);
//        }
//    }
//    
//	private synchronized void drainEncoder(MediaCodec encoder, BufferInfo bufferInfo, TrackIndex trackIndex)
//	{
//        final int TIMEOUT_USEC = 0;     // no timeout -- check for buffers, bail if none
//
//        ByteBuffer[] encoderOutputBuffers = encoder.getOutputBuffers();
//        while (true) {
//            int encoderStatus = encoder.dequeueOutputBuffer(bufferInfo, TIMEOUT_USEC);
//            if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
//                // no output available yet
//                break;
//            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
//                // not expected for an encoder
//                encoderOutputBuffers = encoder.getOutputBuffers();
//            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
//                // Should happen before receiving buffers, and should only happen once.
//                // The MediaFormat contains the csd-0 and csd-1 keys, which we'll need
//                // for MediaMuxer.  It's unclear what else MediaMuxer might want, so
//                // rather than extract the codec-specific data and reconstruct a new
//                // MediaFormat later, we just grab it here and keep it around.
//                if (mMuxerStarted) {
//                    //throw new RuntimeException("format changed twice");
//                	break;
//                }
//                MediaFormat newFormat = encoder.getOutputFormat();
//                Log.d(TAG, "encoder output format changed: " + newFormat);
//                // now that we have the Magic Goodies, start the muxer
//                trackIndex.index = mMuxer.addTrack(newFormat);
//                Log.d(TAG, "track index: " + trackIndex.index);
//                if (mVideoTrackIndex.index != -1 && mAudioTrackIndex.index != -1)
//                {
//	                mMuxer.start();
//	                mMuxerStarted = true;
//                }
//            } else if (encoderStatus < 0) {
//                Log.w(TAG, "unexpected result from encoder.dequeueOutputBuffer: " +
//                        encoderStatus);
//                // let's ignore it
//            } else {
//                ByteBuffer encodedData = encoderOutputBuffers[encoderStatus];
//                if (encodedData == null) {
//                    throw new RuntimeException("encoderOutputBuffer " + encoderStatus +
//                            " was null");
//                }
//
//                if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
//                    // The codec config data was pulled out when we got the
//                    // INFO_OUTPUT_FORMAT_CHANGED status.  The MediaMuxer won't accept
//                    // a single big blob -- it wants separate csd-0/csd-1 chunks --
//                    // so simply saving this off won't work.
//                    if (VERBOSE) Log.d(TAG, "ignoring BUFFER_FLAG_CODEC_CONFIG");
//                    bufferInfo.size = 0;
//                }
//
//                if (bufferInfo.size != 0 && mMuxerStarted)
//                {
//                    // adjust the ByteBuffer values to match BufferInfo (not needed?)
//                    encodedData.position(bufferInfo.offset);
//                    encodedData.limit(bufferInfo.offset + bufferInfo.size);
//                    String track = (trackIndex == mAudioTrackIndex) ? "audio" : "video";
//                    Log.d(TAG, "###" + track + "\tbufferInfo.presentationTimeUs: " + bufferInfo.presentationTimeUs);
//
////                    if (mPreviousPts != 0 && mVideoTrackIndex == trackIndex)
////                    {
////                    	long ts = bufferInfo.presentationTimeUs - mPreviousPts;
////                    	Log.d(TAG, "###delta ts: " + ts);
////                    }
//                    bufferInfo.presentationTimeUs = getNextRelativePts(bufferInfo.presentationTimeUs, trackIndex.index);
//                    mMuxer.writeSampleData(trackIndex.index, encodedData, bufferInfo);
//                    if (VERBOSE) Log.d(TAG, track +" sent " + bufferInfo.size + " bytes to muxer" + "\ttrack index:" + trackIndex.index + "\tts: " + bufferInfo.presentationTimeUs);
//
//                }
//                encoder.releaseOutputBuffer(encoderStatus, false);
//                if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0)
//                {
//	                    Log.w(TAG, "reached end of stream unexpectedly");
//	                    break;      // out of while
//                }
//              }
//           }
//        }
//    private long mFirstPts[] = {0, 0};
//    private long mLastPts[] = {0, 0};
//    protected long getNextRelativePts(long absPts, int trackIndex) {
//        if (mFirstPts[trackIndex] == 0) {
//            mFirstPts[trackIndex] = absPts;
//            return 0;
//        }
//        return getSafePts(absPts - mFirstPts[trackIndex], trackIndex);
//    }
//    
//    /**
//     * Sometimes packets with non-increasing pts are dequeued from the MediaCodec output buffer.
//     * This method ensures that a crash won't occur due to non monotonically increasing packet timestamp.
//     */
//    private long getSafePts(long pts, int trackIndex) {
//        if (mLastPts[trackIndex] >= pts) {
//            // Enforce a non-zero minimum spacing
//            // between pts
//            mLastPts[trackIndex] += 9643;
//            return mLastPts[trackIndex];
//        }
//        mLastPts[trackIndex] = pts;
//        return pts;
//    }
//}
