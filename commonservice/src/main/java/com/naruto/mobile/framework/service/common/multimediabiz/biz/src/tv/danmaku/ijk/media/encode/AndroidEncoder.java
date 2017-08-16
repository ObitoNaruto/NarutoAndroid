package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.tv.danmaku.ijk.media.encode;

import android.annotation.TargetApi;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import java.nio.ByteBuffer;

/**
 * @hide
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public abstract class AndroidEncoder {
    private final static String TAG = "AndroidEncoder";
    private final static boolean VERBOSE = true;

    protected AndroidMuxer mMuxer;
    protected MediaCodec mEncoder;
    protected MediaCodec.BufferInfo mBufferInfo;
    protected int mTrackIndex;
    protected volatile boolean mForceEos = false;
    int mEosSpinCount = 0;
    final int MAX_EOS_SPINS = 10;

    /**
     * This method should be called before the last input packet is queued
     * Some devices don't honor MediaCodec#signalEndOfInputStream
     * e.g: Google Glass
     */
    public void signalEndOfStream() {
        mForceEos = true;
    }

    public void release(){
        if (mEncoder != null) {
            mEncoder.stop();
            mEncoder.release();
            mEncoder = null;
            if (VERBOSE) Log.i(TAG, this + " Released encoder#########");
        }
    }

    @SuppressWarnings("deprecation")
	public void drainEncoder(boolean endOfStream) {
        synchronized (mMuxer){
            final int TIMEOUT_USEC = 1000;
            if (VERBOSE) Log.d(TAG, "drainEncoder(" + endOfStream + ") track: " + mTrackIndex);

            if (endOfStream) {
                if (VERBOSE) Log.d(TAG, "sending EOS to encoder for track " + mTrackIndex);
            }

            ByteBuffer[] encoderOutputBuffers = mEncoder.getOutputBuffers();
            while (true) {
                int encoderStatus = mEncoder.dequeueOutputBuffer(mBufferInfo, TIMEOUT_USEC);
                if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                    // no output available yet
                    if (!endOfStream) {
                        break;      // out of while
                    } else {
                        mEosSpinCount++;
                        if (mEosSpinCount > MAX_EOS_SPINS) {
                            if (VERBOSE) Log.i(TAG, "Force shutting down Muxer");
                            mMuxer.forceStop();
                            break;
                        }
                        if (VERBOSE) Log.d(TAG, "no output available, spinning to await EOS");
                    }
                } else if (encoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                    // not expected for an encoder
                    encoderOutputBuffers = mEncoder.getOutputBuffers();
                    if (VERBOSE) Log.d(TAG, "encoder output buffer changed.");
                 } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    // should happen before receiving buffers, and should only happen once
                    MediaFormat newFormat = mEncoder.getOutputFormat();
                    if (VERBOSE) Log.d(TAG, "encoder output format changed: " + newFormat);

                    // now that we have the Magic Goodies, start the muxer
                    mTrackIndex = mMuxer.addTrack(newFormat);
                    // Muxer is responsible for starting/stopping itself
                    // based on knowledge of expected # tracks
                } else if (encoderStatus < 0) {
                    Log.w(TAG, "unexpected result from encoder.dequeueOutputBuffer: " +
                            encoderStatus);
                    // let's ignore it
                } else {
                    ByteBuffer encodedData = encoderOutputBuffers[encoderStatus];
                    if (encodedData == null) {
                        throw new RuntimeException("encoderOutputBuffer " + encoderStatus +
                                " was null");
                    }

                    if (mBufferInfo.size >= 0) { // Allow zero length buffer for purpose of sending 0 size video EOS Flag
                        // adjust the ByteBuffer values to match BufferInfo (not needed?)
                        encodedData.position(mBufferInfo.offset);
                        encodedData.limit(mBufferInfo.offset + mBufferInfo.size);
                        if (mForceEos) {
                            mBufferInfo.flags = mBufferInfo.flags | MediaCodec.BUFFER_FLAG_END_OF_STREAM;
                            Log.i(TAG, "Forcing EOS");
                        }
                        // It is the muxer's responsibility to release encodedData
                        if (VERBOSE) {
                            Log.d(TAG, "sent " + mBufferInfo.size + " bytes to muxer, \t ts=" +
                                    mBufferInfo.presentationTimeUs + "\ttrack " + mTrackIndex);
                        }
                        mMuxer.writeSampleData(mEncoder, mTrackIndex, encoderStatus, encodedData, mBufferInfo);
                    }

                    if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                        if (!endOfStream) {
                            Log.w(TAG, "reached end of stream unexpectedly");
                        } else {
                            if (VERBOSE) Log.d(TAG, "end of stream reached for track " + mTrackIndex);
                        }
                        break;      // out of while
                    }
                }
            }
            if (endOfStream && VERBOSE ) {
                if (isSurfaceInputEncoder()) {
                    Log.i(TAG, "final video drain complete");
                } else {
                    Log.i(TAG, "final audio drain complete");
                }
            }
        }
    }

    protected abstract boolean isSurfaceInputEncoder();
}
