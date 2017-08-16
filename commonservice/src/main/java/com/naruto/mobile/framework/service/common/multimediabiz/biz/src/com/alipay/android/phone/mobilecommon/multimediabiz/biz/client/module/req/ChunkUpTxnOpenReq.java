/*
 * Copyright 2014 Alibaba.com All right reserved.
 * This software is the confidential and proprietary information of Alibaba.com ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.req;


import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.api.FileApi;

public class ChunkUpTxnOpenReq {
    private String md5;
    private long size;
    private int number;
    private long chunkSize;
    private String extension;

    /**
     * 设置文件大小 ，并用默认的每个分块大小去切分文件: {@link com.naruto.android.phone.mobilecommon.multimediabiz.biz.client.api.FileApi#DEFAULT_TRUNK_SIZE}<br/>
     *
     * @param size
     */
    public ChunkUpTxnOpenReq(long size) {
        this.size = size;
        chunkSize = FileApi.DEFAULT_TRUNK_SIZE;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    /**
     * @return the size
     */
    public long getSize() {
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(long size) {
        this.size = size;
    }

    /**
     * @return the number
     */
    public int getNumber() {
        return number;
    }

    /**
     * @param number the number to set
     */
    public void setNumber(int number) {
        this.number = number;
    }

    /**
     * 获取每个文件块的大小，默认为 {@link com.naruto.android.phone.mobilecommon.multimediabiz.biz.client.api.FileApi#DEFAULT_TRUNK_SIZE}
     *
     * @return the chunkSize
     */
    public long getChunkSize() {
        return chunkSize;
    }

    /**
     * @return the extension
     */
    public String getExtension() {
        return extension;
    }

    /**
     * @param extension the extension to set
     */
    public void setExtension(String extension) {
        this.extension = extension;
    }

}
