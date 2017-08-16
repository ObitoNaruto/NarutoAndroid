package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.audio.silk;

import android.util.Log;

import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Logger;

/**
 * Silk api
 * Created by jinmin on 15/5/13.
 */
public class SilkApi {
    private static final String TAG = SilkApi.class.getSimpleName();

    public static final String SILK_HEAD = "#!SILK_V3";
    public static final short SILK_END_SHORT = -1;
    public static final byte[] SILK_END = SilkUtils.convertToLittleEndian(SILK_END_SHORT);
    //压缩率
    public static final int COMPRESSION_LOW = 0;
    public static final int COMPRESSION_NORMAL = 1;
    public static final int COMPRESSION_HIGH = 2;
    //采样率
    public static final int SAMPLE_RATE_8K = 8000;
    public static final int SAMPLE_RATE_12K = 12000;
    public static final int SAMPLE_RATE_16K = 16000;
    public static final int SAMPLE_RATE_24K = 24000;
    public static final int SAMPLE_RATE_32K = 32000;
    public static final int SAMPLE_RATE_44_1K = 44100;
    public static final int SAMPLE_RATE_48K = 48000;
    //保存比特率
    public static final int TARGET_RATE_8K = 8000;
    public static final int TARGET_RATE_12K = 12000;
    public static final int TARGET_RATE_16K = 16000;
    public static final int TARGET_RATE_25K = 25000;
    public static final int TARGET_RATE_44K = 44000;
    public static final int TARGET_RATE_48K = 48000;
    //SKP_Silk_SDK_Get_Encoder_Size
    static {
        System.loadLibrary("silk_jni");
        Logger.D(TAG, "loadLibrary finish");
    }


    public SilkApi() {
    }

    public native int openEncoder(int compression, int sampleRate, int targetRate);
    public native int openDecoder(int sampleRate);
    public native int decode(byte encoded[], short[] lin, int size);
    public native int encode(short[] lin, int offset, byte[] encoded, int size);
    public native void closeEncoder();
    public native void closeDecoder();
}
