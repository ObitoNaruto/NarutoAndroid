/*
 * Copyright 2014 Alibaba.com All right reserved.
 * This software is the confidential and proprietary information of Alibaba.com ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.req;

import java.io.File;

import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.api.FileApi;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.io.ChunkTransferredListener;


public class ChunkUpTxnProcessReq {
    private String fileId;
    private String md5;
    private String gcid;
    private long chunkNumber;
    private int sequence;
    private long chunkSize = FileApi.DEFAULT_TRUNK_SIZE;
    private long realChunkSize; //块的实际大小, 用于秒传时的onTran回调参数
    private File file;
    private ChunkTransferredListener chunkTransListener;

    /**
     * 文件上传的第二步的请求。文件的每个分块大小使用默认的
     *
     * @param fileId
     * @param file     文件
     * @param sequence 从1开始的块编号
     */
    public ChunkUpTxnProcessReq(String fileId, File file, int sequence) {
        this.fileId = fileId;
        this.file = file;
        this.sequence = sequence;
    }

    /**
     * 文件上传的第二步的请求。文件的每个分块大小使用默认的
     * @param fileId
     * @param file
     * @param sequence
     * @param chunkTransListener 上传进度的回调
     */
    public ChunkUpTxnProcessReq(String fileId, File file, int sequence, ChunkTransferredListener chunkTransListener) {
        this(fileId,file,sequence);
        this.chunkTransListener = chunkTransListener;
    }

    public long getChunkNumber() {
        return chunkNumber;
    }

    public void setChunkNumber(long chunkNumber) {
        this.chunkNumber = chunkNumber;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    /**
     * @return the chunkSize
     */
    public long getChunkSize() {
        return chunkSize;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setChunkSize(long chunkSize) {
        this.chunkSize = chunkSize;
    }

    public long getRealChunkSize() {
        return realChunkSize;
    }

    public void setRealChunkSize(long realChunkSize) {
        this.realChunkSize = realChunkSize;
    }

    public ChunkTransferredListener getChunkTransListener() {
        return chunkTransListener;
    }

    public void setChunkTransListener(ChunkTransferredListener chunkTransListener) {
        this.chunkTransListener = chunkTransListener;
    }

    public String getGcid() {
        return gcid;
    }

    public void setGcid(String gcid) {
        this.gcid = gcid;
    }

    @Override
    public String toString() {
        return "ChunkUpTxnProcessReq{" +
                "fileId='" + fileId + '\'' +
                ", md5='" + md5 + '\'' +
                ", chunkNumber=" + chunkNumber +
                ", sequence=" + sequence +
                ", chunkSize=" + chunkSize +
                ", file=" + file +
                ", gcid=" + gcid +
                '}';
    }
}
