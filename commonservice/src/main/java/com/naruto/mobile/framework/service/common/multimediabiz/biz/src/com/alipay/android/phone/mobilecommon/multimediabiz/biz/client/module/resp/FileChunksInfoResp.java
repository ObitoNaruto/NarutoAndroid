/*
 * Copyright 2014 Alibaba.com All right reserved.
 * This software is the confidential and proprietary information of Alibaba.com ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.resp;

import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.BaseResp;

public class FileChunksInfoResp extends BaseResp {
	@JSONField(name = "data")
    private ChunkInfo[] chunksInfo;

    public ChunkInfo[] getChunksInfo() {
        return chunksInfo;
    }

    public void setChunksInfo(ChunkInfo[] chunksInfo) {
        this.chunksInfo = chunksInfo;
    }

    public static class ChunkInfo {
        private long      chunkNo;
        private long      offset;
        private long      chunkSize;
        private String    extensionType;
        private String    md5;
        private List<TFS> tfsList;

        public long getChunkNo() {
            return chunkNo;
        }

        public void setChunkNo(long chunkNo) {
            this.chunkNo = chunkNo;
        }

        public long getOffset() {
            return offset;
        }

        public void setOffset(long offset) {
            this.offset = offset;
        }

        public long getChunkSize() {
            return chunkSize;
        }

        public void setChunkSize(long chunkSize) {
            this.chunkSize = chunkSize;
        }

        public String getExtensionType() {
            return extensionType;
        }

        public void setExtensionType(String extensionType) {
            this.extensionType = extensionType;
        }

        public String getMd5() {
            return md5;
        }

        public void setMd5(String md5) {
            this.md5 = md5;
        }

        public List<TFS> getTfsList() {
            return tfsList;
        }

        public void setTfsList(List<TFS> tfsList) {
            this.tfsList = tfsList;
        }
    }

    public static class TFS {
        private long   offset;
        private long   length;
        private String md5;
        private String key;

        public long getOffset() {
            return offset;
        }

        public void setOffset(long offset) {
            this.offset = offset;
        }

        public long getLength() {
            return length;
        }

        public void setLength(long length) {
            this.length = length;
        }

        public String getMd5() {
            return md5;
        }

        public void setMd5(String md5) {
            this.md5 = md5;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }
    }
}
