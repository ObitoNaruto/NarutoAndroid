package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.audio.silk;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Silk 辅助工具
 * Created by jinmin on 15/5/23.
 */
public class SilkUtils {

    public static short[] getShortArray(byte[] bytes, int size) {
        if (bytes != null) {
            short[] shorts = new short[size/2];
            ByteBuffer byteBuffer = ByteBuffer.allocate(size);
            byteBuffer.put(bytes, 0, size);
            byteBuffer.flip();
            for (int i = 0; i < shorts.length; i++) {
                shorts[i] = byteBuffer.getShort();
            }
            return shorts;
        }
        return null;
    }

    public static short getLittleEndianShort(byte[] bytes) {
        return (short) (bytes[0] | (bytes[1] << 8));
    }

    public static byte[] convertToLittleEndian(short input) {
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putShort(input);
        return buffer.array();
    }
}
