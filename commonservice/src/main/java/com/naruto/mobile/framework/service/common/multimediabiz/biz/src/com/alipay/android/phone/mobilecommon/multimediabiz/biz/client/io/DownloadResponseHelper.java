/*
 * Copyright 2014 Alibaba.com All right reserved.
 * This software is the confidential and proprietary information of Alibaba.com ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */

package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.util.ByteUtil;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.util.DjangoConstant;


/**
 * 读取文件响应流的辅助类，分为读取单个文件和多个文件的方法<br/>
 * 有些下载接口，在下载时可以指定一个或多个文件，如果只指定了一个文件，那么Django Server给出的流内容直接就是文件内容<br/>
 * 但是，如果指定了多个文件ID，那么Django Server给出的流内容有了一定的格式:
 * <ul>
 * <li>响应流内的每个文件流，都是由固定大小为66 btyes的文件头和文件内容组成的</li>
 * <li>如果指定的多个文件ID中有一部分文件ID不存在的话，响应流内是不模拟这个文件ID的文件头和流的</li>
 * <li>如果指定的多个文件ID中有一部分文件是0字节的空文件，响应流内只包含这个文件的文件头，因为其length为0,所有是没有流，需要本地创建空文件</li>
 * <li>
 * 每个文件66 bytes的文件头的组成依次为：
 * <ol>
 * <li>32 bytes : File id </li>
 * <li> 8 bytes : File lenght </li>
 * <li>16 bytes : File md5 </li>
 * <li> 2 bytes : File type </li>
 * <li> 8 bytes : chunk no</li>
 * </ol>
 * </li>
 * </ul>
 * 可以参考：http://docs.alibaba-inc.com/display/CloudDrive/Django+1.0+API  中第 3.1 节
 *
 * @author jinzhaoyu
 */
public class DownloadResponseHelper {

    public static final int READ_BUFF_SIZE = 4096;

    /**
     * 读取单个文件的响应流，并写入指定的 OutputStream，<br/>
     * 这只是一个通用方法，有特殊需求的话，您可以使用decorator模式继续封装出多样的OutputStream<br/>
     * <B>注意：读取方法在读取完毕后会自动关闭输入和输出流！</B><br/>
     *
     * @param responseInputStream 文件响应流，不允许为null，一般是通过 {@code org.apache.http.HttpResponse.getEntity().getContent()}获取的
     * @param outputStream        读取的文件流输出目的地，不允许为null。
     *                            如果您需要监控输出进度可以直接使用,
     *                            如果您需要终止写入，可以在{@link TransferredListener#onTransferred(long)}中抛出
     * @throws IOException
     */
    public void writeSingleFile(InputStream responseInputStream, OutputStream outputStream) throws IOException {
        try {
            IOUtils.copyLarge(responseInputStream,outputStream);
        } finally {
            IOUtils.closeQuietly(responseInputStream);
            IOUtils.closeQuietly(outputStream);
        }
    }

    /**
     * 为断点续传而设的方法，读取单个文件响应流，并写入指定的文件
     * @param responseInputStream
     * @param file
     * @param offset 写入偏移量
     * @param transferredListener 传输监听器，可以为null，如果需要中断，可以在回调中抛出异常。如：
     * @throws IOException
     */
    public void writeSingleFile(InputStream responseInputStream,File file,long offset,TransferredListener transferredListener) throws IOException {
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(file,"rw");
            raf.seek(offset);

            long count = 0;
            int n = 0;
            byte[] buffer = new byte[1024 * 4];
            while ((n = responseInputStream.read(buffer)) != -1) {
                raf.write(buffer, 0, n);
                count += n;
                if(transferredListener != null){
                    transferredListener.onTransferred(count);
                }
            }
        }finally {
            IOUtils.closeQuietly(responseInputStream);
            if(raf != null){
                raf.close();
            }
        }
    }

    /**
     * 读取批量文件流，并写入指定的OutputStream<br/>
     * 这只是一个通用方法，有特殊需求的话，您可以使用decorator模式继续封装出多样的OutputStream<br/>
     * <B>注意：读取方法在读取完毕后会自动关闭输入和输出流！</B><br/>
     *
     * @param responseInputStream       批量文件流，不允许为null，一般是通过 {@code org.apache.http.HttpResponse.getEntity().getContent()}获取的
     * @param readBatchFileRespCallback 读取文件的回调，不允许为null，将通过此方法获取输出流.
     * @throws IOException
     */
    public void writeBatchFiles(InputStream responseInputStream, ReadBatchFileRespCallback readBatchFileRespCallback) throws IOException {
        FileHeader fileHeader;
        //不存在的文件，Server端不会在文件流里写入FileHeader
        while ((fileHeader = readFileHeader(responseInputStream)) != null) {
            boolean isEmptyFile = fileHeader.length == 0;
            OutputStream outputStream = readBatchFileRespCallback.onReadFile(fileHeader, isEmptyFile);
            if (! isEmptyFile) {
                //如果读取的是个正常文件，则读取并写入输出流
                writeFileInBatch(fileHeader, responseInputStream, outputStream);
            }
            IOUtils.closeQuietly(outputStream);
        }
        IOUtils.closeQuietly(responseInputStream);
    }

    /**
     * 读取并解析66 bytes文件头，一般情况下您不需要直接调用此方法.
     *
     * @param inputStream
     * @return May be null
     * @throws IOException
     */
    public FileHeader readFileHeader(InputStream inputStream) throws IOException {
        FileHeader fileHeader = null;
        final byte[] header = new byte[66];
        int l = inputStream.read(header);
        if (l != -1 && l == 66) {
            fileHeader = new FileHeader();

            ByteBuffer byteBuffer = ByteBuffer.wrap(header);
            byte[] tempBytes = new byte[32];
            byteBuffer.get(tempBytes);
            fileHeader.fileId = new String(tempBytes, DjangoConstant.DEFAULT_CHARSET_NAME);

            fileHeader.length = byteBuffer.getLong();

            byteBuffer.get(tempBytes, 0, 16);
            fileHeader.md5 = ByteUtil.byteArrayToHexString(tempBytes, 0, 16);

            fileHeader.type = byteBuffer.getShort();
            fileHeader.chunkNumber = byteBuffer.getLong();
        }
        return fileHeader;
    }

    /**
     * 将批量文件流写入文件
     *
     * @param fileHeader
     * @param inputStream
     * @param destOutputStream 如果为null，则跳过{@link com.taobao.django.client.io.DownloadResponseHelper.FileHeader#length}个字节
     * @throws IOException
     */
    private void writeFileInBatch(FileHeader fileHeader, InputStream inputStream, OutputStream destOutputStream) throws IOException {
        long fileSize = fileHeader.length;
        if (fileSize == 0) {
            return;
        }

        if(destOutputStream == null){
            IOUtils.skip(inputStream,fileSize);
            return;
        }

        byte[] tmp = new byte[READ_BUFF_SIZE];
        IOUtils.copyLarge(inputStream,destOutputStream,0,fileSize,tmp);

        destOutputStream.flush();
    }


    //=========Inner classes========

    /**
     * 读取批量文件流的回调
     */
    public static interface ReadBatchFileRespCallback {

        /**
         * 当读取到一个正常文件时的回调，这里要求您返回一个OutputStream，以便{@link DownloadResponseHelper}写入读取到的文件<br/>
         * 如果您需要监控写入进度，可以使用{@link com.taobao.django.client.io.output.ProgressOutputStream}
         *
         * @param fileHeader
         * @param isEmptyFile 指出当前的文件是否为空文件，如果为空文件，则本回调方法应该返回null
         * @return 返回要写入的OutputStream，可以为null，为null时跳过字节的读取
         */
        OutputStream onReadFile(FileHeader fileHeader, boolean isEmptyFile);

    }

    /**
     * 解析批量文件下载流中的文件头后，生成此对象。文件头占用66字节。
     *
     * @author jinzhaoyu
     */
    public static class FileHeader {
        /**
         * 文件ID，占用文件头的前32个字节
         */
        public String fileId;
        /**
         * 文件大小，在文件ID后，占用文件头8个字节
         */
        public long length;
        /**
         * 文件MD5，在文件length后，占用文件头16个字节
         */
        public String md5;
        /**
         * 文件类型,在文件MD5后，占用文件头2个字节
         */
        public short type;
        /**
         * 文件的分块数，在文件类型后，占用文件头8个字节
         */
        public long chunkNumber;

        @Override
        public String toString() {
            return "FileHeader [fileId=" + fileId + ", length=" + length
                    + ", md5=" + md5 + ", type=" + type + ", chunkNo="
                    + chunkNumber + "]";
        }

    }
}
