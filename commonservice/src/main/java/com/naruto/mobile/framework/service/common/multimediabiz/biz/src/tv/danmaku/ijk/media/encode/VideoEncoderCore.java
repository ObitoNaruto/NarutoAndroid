/*
 * Copyright 2014 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.tv.danmaku.ijk.media.encode;

import android.annotation.TargetApi;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Build;
import android.util.Log;
import android.view.Surface;
import java.io.IOException;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class VideoEncoderCore extends AndroidEncoder
{
    private static final String TAG = "VideoEncoderCore";
    private static final boolean VERBOSE = false;

    private static final String MIME_TYPE = "video/avc";
    private static final int FRAME_RATE = 19;    
    private static final int IFRAME_INTERVAL = 3;

    private Surface mInputSurface;


    public VideoEncoderCore(int width, int height, int bitRate, AndroidMuxer muxer) throws IOException {
        mMuxer = muxer;
        mBufferInfo = new MediaCodec.BufferInfo();

        MediaFormat format = MediaFormat.createVideoFormat(MIME_TYPE, width, height);

        format.setInteger(MediaFormat.KEY_COLOR_FORMAT,
                MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        format.setInteger(MediaFormat.KEY_BIT_RATE, bitRate);
        format.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL);
        if (VERBOSE) Log.d(TAG, "format: " + format);

        mEncoder = MediaCodec.createEncoderByType(MIME_TYPE);
        mEncoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mInputSurface = mEncoder.createInputSurface();
        mEncoder.start();
        mTrackIndex = -1;
    }


    public Surface getInputSurface() {
        return mInputSurface;
    }

    @Override
    protected boolean isSurfaceInputEncoder() {
        return true;
    }
}