/*
 * Copyright 2014 Alibaba.com All right reserved.
 * This software is the confidential and proprietary information of Alibaba.com ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.req;

import java.io.File;

import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.io.TransferredListener;


public class FileUpReq extends BaseUpReq<File> {

    public FileUpReq(File file) {
        this.inputSource = file;
    }

    /**
     * 如果关心传输进度的话，可以设置 transferedListener
     *
     * @param file
     * @param transferedListener
     */
    public FileUpReq(File file, TransferredListener transferedListener) {
        this.inputSource = file;
        this.transferedListener = transferedListener;
    }

    public FileUpReq(FileUpReq src) {
        this.md5 = src.md5;
        this.gcid = src.gcid;
        this.ext = src.ext;
        this.inputSource = src.inputSource;
        this.startPos = src.startPos;
        this.endPos = src.endPos;
        this.skipRapid = src.skipRapid;
        this.transferedListener = src.transferedListener;
    }

    public File getFile() {
        return getInputSource();
    }

    @Override
    public long getTotalLength() {
        return inputSource.length();
    }

    @Override
    public String getFileName() {
        return inputSource.getName();
    }
}
