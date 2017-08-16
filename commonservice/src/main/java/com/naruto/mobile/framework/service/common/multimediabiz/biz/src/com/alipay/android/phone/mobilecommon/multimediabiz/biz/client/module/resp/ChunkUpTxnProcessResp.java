/*
 * Copyright 2014 Alibaba.com All right reserved.
 * This software is the confidential and proprietary information of Alibaba.com ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.resp;


import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.BaseResp;

public class ChunkUpTxnProcessResp extends BaseResp {
    private Data data;
    private boolean isRapid;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public boolean isRapid() {
        return isRapid;
    }

    public void setRapid(boolean isRapid) {
        this.isRapid = isRapid;
    }

    public static class Data {
        private String md5;
        private String gcid;

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
}
