/*
 * Copyright 2014 Alibaba.com All right reserved.
 * This software is the confidential and proprietary information of Alibaba.com ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.resp;

import com.alibaba.fastjson.annotation.JSONField;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.BaseResp;

public class GetChunksMetaResp extends BaseResp {
	@JSONField(name = "data")
    private ChunkMeta[] chunksMeta;

    public ChunkMeta[] getChunksMeta() {
        return chunksMeta;
    }

    public void setChunksMeta(ChunkMeta[] chunksMeta) {
        this.chunksMeta = chunksMeta;
    }

    public static class ChunkMeta {
        private String id;
        private String sequence;
        private String size;
        private String md5;
        private String createTime;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getSequence() {
            return sequence;
        }

        public void setSequence(String sequence) {
            this.sequence = sequence;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public String getMd5() {
            return md5;
        }

        public void setMd5(String md5) {
            this.md5 = md5;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }
    }
}
