/*
 * Copyright 2014 Alibaba.com All right reserved.
 * This software is the confidential and proprietary information of Alibaba.com ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.resp;

import com.alibaba.fastjson.annotation.JSONField;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.BaseResp;

public class FilesDelResp extends BaseResp {
	@JSONField(name = "data")
	private String[] failedFileIds;

	/**
	 * @return the failedFileIds
	 */
	public String[] getFailedFileIds() {
		return failedFileIds;
	}

	/**
	 * @param failedFileIds
	 *            the failedFileIds to set
	 */
	public void setFailedFileIds(String[] failedFileIds) {
		this.failedFileIds = failedFileIds;
	}

}
