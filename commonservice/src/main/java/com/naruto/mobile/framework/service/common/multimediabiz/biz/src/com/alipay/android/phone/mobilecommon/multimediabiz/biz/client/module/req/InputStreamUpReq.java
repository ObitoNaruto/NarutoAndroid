/*
 * Copyright 2014 Alibaba.com All right reserved.
 * This software is the confidential and proprietary information of Alibaba.com ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.req;

import java.io.InputStream;

import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.io.TransferredListener;


/**
 *
 */
public class InputStreamUpReq extends BaseUpReq<InputStream> {
    /**
     * @param inputStream
     * @param fileName
     */
    public InputStreamUpReq(InputStream inputStream,String fileName) {
        this.inputSource = inputStream;
        this.fileName = fileName;
    }

    public InputStreamUpReq(InputStream inputStream, String fileName, TransferredListener listener) {
        this(inputStream, fileName, listener, -1);
        try {
            this.totalLength = inputStream.available();
        } catch (Exception e) {

        }
    }

    /**
     * 如果关心传输进度的话，可以设置 transferedListener
     *
     * @param inputStream
     * @param fileName
     * @param transferredListener
     */
    public InputStreamUpReq(InputStream inputStream,String fileName, TransferredListener transferredListener, long length) {
        this.inputSource = inputStream;
        this.fileName = fileName;
        this.transferedListener = transferredListener;
        this.totalLength = length;
    }

    public InputStream getInputStream() {
        return getInputSource();
    }

    public void setInputStream(InputStream inputStream) {
        setInputSource(inputStream);
    }

    public long getLength() {
        return getTotalLength();
    }
}
