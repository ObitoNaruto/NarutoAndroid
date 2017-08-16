/*
 * Copyright 2014 Alibaba.com All right reserved.
 * This software is the confidential and proprietary information of Alibaba.com ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.util;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.security.DigestInputStream;
import java.security.MessageDigest;

import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.io.IOUtils;

/**
 * MD5支持工具类，提供对字符串、文件、字节数组的转换
 *
 * @author jinzhaoyu
 */
public class MD5Utils {

    public static final String ALGORIGTHM_MD5 = "MD5";

    /**
     * 空内容的MD5
     */
    public static final String EMPTY_CONTENT_MD5 = "d41d8cd98f00b204e9800998ecf8427e";
    private static final int READ_BUFFER_SIZE = 1024;

    /**
     * 默认的密码字符串组合，用来将字节转换成 16 进制表示的字符
     */
    protected static char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6',
            '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * 获取MD5实例
     *
     * @return
     * @throws java.security.NoSuchAlgorithmException
     */
    public static MessageDigest getMD5Digest() {
        try {
            return MessageDigest.getInstance(ALGORIGTHM_MD5);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 生成字符串的md5校验值
     *
     * @param s
     * @return
     */
    public static String getMD5String(String s) {
        if (s == null) {
            return "";
        }
        return getMD5String(s.getBytes());
    }

    /**
     * 生成文件的md5校验值
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static String getFileMD5String(File file) throws IOException {
        InputStream fis = new FileInputStream(file);
        String fileMd5 = getInputStreamMD5String(fis);
        IOUtils.closeQuietly(fis);
        return fileMd5;
    }

    /**
     * 生成输入流的md5校验值
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static String getInputStreamMD5String(InputStream inputStream) throws IOException {
        MessageDigest messagedigest = getMD5Digest();

        DigestInputStream din = new DigestInputStream(inputStream, messagedigest);
        byte[] buffer = new byte[READ_BUFFER_SIZE];
        while (din.read(buffer) > 0) {
        }
        messagedigest = din.getMessageDigest();

        return bytesToHex(messagedigest.digest());
    }

    /**
     * 获取文件分块MD5,指定某个分块和每个分块的大小，由本工具类计算读取偏移量
     *
     * @param file
     * @param chunkSequence 要读取的文件块序列，从 1 开始
     * @param chunkSize     分块文件时使用的每个块大小
     * @return
     */
    public static String getFileChunkMD5String(File file, int chunkSequence, long chunkSize) throws IOException {
        if (chunkSequence < 1 || file == null) {
            throw new IllegalArgumentException("Invalide parameter!");
        }
        long fileLength = file.length();
        long chunkNumer = fileLength / chunkSize;
        if (fileLength % chunkSize != 0) chunkNumer++;
        if (chunkSequence < 1 || chunkNumer < chunkSequence) {
            throw new IllegalArgumentException("Chunk sequence greater than file size !");
        }
        long offset = (chunkSequence - 1) * chunkSize;
        long readLength = chunkSize;
        long destChunkSize = chunkSequence * chunkSize;
        if (destChunkSize > fileLength) {
            readLength = fileLength - offset;
        }
        return getFileChunkMD5String(file, offset, readLength);
    }

    /**
     * 获取文件分块的MD5字符串
     *
     * @param file
     * @param offset 分块在文件中的位置
     * @param length
     * @return
     */
    public static String getFileChunkMD5String(File file, long offset, long length) throws IOException {
        MessageDigest messagedigest = getMD5Digest();
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
        try {
            if (offset > 0) {
                randomAccessFile.seek(offset);
            }

            byte[] buffer = new byte[READ_BUFFER_SIZE];
            long remain = length;
            while (remain > 0) {
                int n = randomAccessFile.read(buffer, 0, (int) Math.min(remain, READ_BUFFER_SIZE));
                if (n < 0) { // EOF
                    break;
                }
                messagedigest.update(buffer, 0, n);
                remain -= n;
            }
        } finally {
            randomAccessFile.close();
        }
        return bytesToHex(messagedigest.digest());
    }

    /**
     * 将指定的字节数组转换为MD5字符串
     *
     * @param bytes
     * @return
     */
    public static String getMD5String(byte[] bytes) {
        MessageDigest messagedigest = getMD5Digest();
        messagedigest.update(bytes);
        return bytesToHex(messagedigest.digest());
    }

    /**
     * 将指定的字节数组转换为MD5字符串
     *
     * @param bytes
     * @param offset
     * @param length
     * @return
     */
    public static String getMD5String(byte[] bytes, int offset, int length) {
        MessageDigest messagedigest = getMD5Digest();
        messagedigest.update(bytes, offset, length);
        return bytesToHex(messagedigest.digest());
    }

    /**
     * @param bytes
     * @return
     */
    public static String bytesToHex(byte bytes[]) {
        return bytesToHex(bytes, 0, bytes.length);
    }

    /**
     * @param bytes
     * @param m
     * @param n
     * @return
     */
    public static String bytesToHex(byte bytes[], int m, int n) {
        StringBuffer stringbuffer = new StringBuffer(2 * n);
        int k = m + n;
        for (int l = m; l < k; l++) {
            appendHexPair(bytes[l], stringbuffer);
        }
        return stringbuffer.toString();
    }

    private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
        char c0 = hexDigits[(bt & 0xf0) >> 4];// 取字节中高 4 位的数字转换, >>>
        // 为逻辑右移，将符号位一起右移,此处未发现两种符号有何不同
        char c1 = hexDigits[bt & 0xf];// 取字节中低 4 位的数字转换
        stringbuffer.append(c0);
        stringbuffer.append(c1);
    }
}
