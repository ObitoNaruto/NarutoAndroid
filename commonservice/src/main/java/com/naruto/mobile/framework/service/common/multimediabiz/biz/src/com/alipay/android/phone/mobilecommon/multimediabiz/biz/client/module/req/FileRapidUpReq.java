/*
 * Copyright 2014 Alibaba.com All right reserved.
 * This software is the confidential and proprietary information of Alibaba.com ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */

package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.req;


import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.util.LiteStringUtils;

/**
 * 文件秒传请求
 * @author jinzhaoyu
 */
public class FileRapidUpReq {
    private String md5;
    private String gcid;
    private String ext;

    /**
     * 文件秒传请求，必须输入md5或者gcid，如果二者都为空字符串，则抛出异常
     * @param md5
     * @param gcid
     */
    public FileRapidUpReq(String md5,String gcid){
        if(LiteStringUtils.isBlank(md5) && LiteStringUtils.isBlank(gcid)){
            throw new IllegalArgumentException("Parameter md5 or gcid can not be null !");
        }
        this.md5 = md5;
        this.gcid = gcid;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getGcid() {
        return gcid;
    }

    public void setGcid(String gcid) {
        this.gcid = gcid;
    }

}
