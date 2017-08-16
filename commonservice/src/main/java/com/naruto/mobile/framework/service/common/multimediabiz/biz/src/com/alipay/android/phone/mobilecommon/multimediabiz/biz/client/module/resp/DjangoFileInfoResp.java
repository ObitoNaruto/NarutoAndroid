/*
 * Copyright 2014 Alibaba.com All right reserved.
 * This software is the confidential and proprietary information of Alibaba.com ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.resp;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.Map;

public class DjangoFileInfoResp{
    private String                           id;
    private String                           md5;
    private long                             size;
    private long                             createTime;
    private String                           appKey;
    private short                            type;
    private int                              chunkNumber;
    private String                           name;
    private int                              status = 20;
    private Map<String, Map<String, String>> ext    = new HashMap<String, Map<String, String>>(2);

    public Map<String, Map<String, String>> getExt() {
        return ext;
    }

    public void setExt(Map<String, Map<String, String>> ext) {
        this.ext = ext;
    }

    public String toString() {
    	return JSON.toJSONString(this);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public short getType() {
        return type;
    }

    public void setType(short type) {
        this.type = type;
    }

    public int getChunkNumber() {
        return chunkNumber;
    }

    public void setChunkNumber(int chunkNumber) {
        this.chunkNumber = chunkNumber;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
