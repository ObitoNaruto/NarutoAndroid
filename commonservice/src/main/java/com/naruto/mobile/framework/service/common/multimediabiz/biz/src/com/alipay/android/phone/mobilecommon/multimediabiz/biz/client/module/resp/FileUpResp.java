/*
 * Copyright 2014 Alibaba.com All right reserved.
 * This software is the confidential and proprietary information of Alibaba.com ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.resp;

public class FileUpResp extends DjangoFileInfoRespWrapper {
    private boolean isRapid;

    private int range;

    /**
     * 获取此响应是否是秒传成功的响应
     * @return
     */
    public boolean isRapid() {
        return isRapid;
    }

    /**
     * 设置此响应是否是秒传成功的响应
     * @param isRapid
     */
    public void setRapid(boolean isRapid) {
        this.isRapid = isRapid;
    }

    /**
     * 断点续上传开始位置
     * @return
     */
    public int getRange() {
        return range;
    }

    /**
     * 设置断点续上传位置
     * @param range
     */
    public void setRange(int range) {
        this.range = range;
    }

    @Override
    public String toString() {
        return "FileUpResp{" +
                "isRapid=" + isRapid +
                ", isSuccess=" + isSuccess() +
                "}\n" + super.toString();
    }
}
