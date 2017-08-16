/*
 * Copyright 2014 Alibaba.com All right reserved.
 * This software is the confidential and proprietary information of Alibaba.com ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;

import android.annotation.SuppressLint;

public class ByteUtil {

    public static byte[] shortToByteArray(short value) {
        ByteBuffer buffer = ByteBuffer.allocate(2);
        return buffer.putShort(value).array();
    }

    public static byte[] intToByteArray(int value) {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        return buffer.putInt(value).array();
    }

    public static byte[] longToByteArray(long value) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        return buffer.putLong(value).array();
    }

    public static byte[] UUIDToByteArray(UUID value) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(value.getMostSignificantBits());
        bb.putLong(value.getLeastSignificantBits());
        return bb.array();
    }

    public static UUID byteArrayToUUID(byte[] value) {
        // nameUUIDFromBytes是创建了另一种UUID... 不能还原
//        UUID uuid = UUID.nameUUIDFromBytes(value);
//        return uuid.toString();
        ByteBuffer bb = ByteBuffer.wrap(value);
        long msb = bb.getLong();
        long lsb = bb.getLong();
        return new UUID(msb, lsb);
    }

    public static long byteArrayToLong(byte[] value) {
        int mask = 0xFF;
        int temp = 0;
        long res = 0;
        for (int i = 0; i < 8 && i < value.length; i++) {
            res <<= 8;
            temp = value[i] & mask;
            res |= temp;
        }
        return res;
    }

    public static long byteArrayToLong(byte[] value, int offset, int length) {
        int mask = 0xFF;
        int temp = 0;
        long res = 0;
        for (int i = 0; i < 8 && i < value.length; i++) {
            res <<= 8;
            temp = value[i + offset] & mask;
            res |= temp;
        }
        return res;
    }

    public static int byteArrayToInt(byte[] value) {
        int mask = 0xFF;
        int temp = 0;
        int res = 0;
        for (int i = 0; i < 4 && i < value.length; i++) {
            res <<= 8;
            temp = value[i] & mask;
            res |= temp;
        }
        return res;
    }

    public static int byteArrayToInt(byte[] value, int offset, int length) {
        int mask = 0xFF;
        int temp = 0;
        int res = 0;
        for (int i = 0; i < 4 && i < value.length; i++) {
            res <<= 8;
            temp = value[i + offset] & mask;
            res |= temp;
        }
        return res;
    }

    public static short byteArrayToShort(byte[] value) {
        int mask = 0xFF;
        int temp = 0;
        short res = 0;
        for (int i = 0; i < 2 && i < value.length; i++) {
            res <<= 8;
            temp = value[i] & mask;
            res |= temp;
        }
        return res;
    }

    public static short byteArrayToShort(byte[] value, int offset, int length) {
        int mask = 0xFF;
        int temp = 0;
        short res = 0;
        for (int i = 0; i < 2 && i < value.length; i++) {
            res <<= 8;
            temp = value[i + offset] & mask;
            res |= temp;
        }
        return res;
    }

    @SuppressLint("DefaultLocale")
	public static byte[] hexStringToByteArray(String hexStr) {

        hexStr = hexStr.toUpperCase();
        int length = hexStr.length() / 2;

        char[] hexChars = hexStr.toCharArray();
        byte[] data = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            data[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return data;
    }

    public static String byteArrayToHexString(byte[] src, int offset, int length) {

        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }

        for (int i = offset; i < src.length && i < length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }

        return stringBuilder.toString();
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

}
