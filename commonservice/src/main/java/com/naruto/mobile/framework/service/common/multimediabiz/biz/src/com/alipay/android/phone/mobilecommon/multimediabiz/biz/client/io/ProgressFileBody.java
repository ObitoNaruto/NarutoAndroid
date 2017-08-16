/*
 * Copyright 2014 Alibaba.com All right reserved.
 * This software is the confidential and proprietary information of Alibaba.com ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.io;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.io.output.ProgressOutputStream;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.http.apache.entity.mine.content.FileBody;


/**
 * 
 * @author jinzhaoyu
 * <br>Modify Information:
 * <ol> 
 * 	<li>jinzhaoyu created 2014-3-20 下午4:40:22</li>
 * </ol>
 */
public class ProgressFileBody extends FileBody {

	private TransferredListener transferedChangedListener;
	private ProgressOutputStream progressOutputStream;

	/**
	 * @param file
	 * @param transferedChangedListener
	 */
	public ProgressFileBody(File file,TransferredListener transferedChangedListener) {
		super(file);
		this.transferedChangedListener = transferedChangedListener;
	}

	/* (non-Javadoc)
	 * @see org.apache.http.entity.mime.content.FileBody#writeTo(java.io.OutputStream)
	 */
	@Override
	public void writeTo(OutputStream out) throws IOException {
		progressOutputStream = new ProgressOutputStream(out,this.transferedChangedListener);
		super.writeTo(progressOutputStream);
	}
	
	

}
