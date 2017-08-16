/*
 * Copyright 2014 Alibaba.com All right reserved.
 * This software is the confidential and proprietary information of Alibaba.com ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.resp;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.BaseResp;

public class GetFilesMetaResp extends BaseResp {
	@JSONField(name = "data")
    private List<DjangoFileInfoResp> filesMeta;

    public List<DjangoFileInfoResp> getFilesMeta() {
    	if(filesMeta == null){
    		filesMeta = new ArrayList<DjangoFileInfoResp>();
    	}
        return filesMeta;
    }

    public void setFilesMeta(List<DjangoFileInfoResp> filesMeta) {
        this.filesMeta = filesMeta;
    }

}
