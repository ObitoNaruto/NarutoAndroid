/*
 * Copyright 2014 Alibaba.com All right reserved.
 * This software is the confidential and proprietary information of Alibaba.com ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.io.output.ProgressOutputStream;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.http.apache.entity.mine.content.InputStreamBody;


/**
 * 可以监控进度的输入流FileBody
 * @author jinzhaoyu
 * <br>Modify Information:
 * <ol> 
 * 	<li>jinzhaoyu created 2014-3-20 下午4:40:22</li>
 * </ol>
 */
public class ProgressInputStreamBody extends InputStreamBody {

	protected TransferredListener transferedChangedListener;
//	protected ProgressOutputStream progressOutputStream;

	//文件流长度
	private long contentLength = -1;

	/**
	 * @param inputStream
	 * @param fileName
	 * @param transferedChangedListener
	 */
	public ProgressInputStreamBody(InputStream inputStream,String fileName, long contentLength,  TransferredListener transferedChangedListener) {
		super(inputStream,fileName);
		this.transferedChangedListener = transferedChangedListener;
		this.contentLength = contentLength;
	}
	/**
	 * @param inputStream
     * @param fileName
	 * @param transferedChangedListener
	 */
	public ProgressInputStreamBody(InputStream inputStream,String fileName, TransferredListener transferedChangedListener) throws IOException {
		this(inputStream, fileName, inputStream.available(), transferedChangedListener);
	}

	/* (non-Javadoc)
	 * @see org.apache.http.entity.mime.content.FileBody#writeTo(java.io.OutputStream)
	 */
	@Override
	public void writeTo(OutputStream out) throws IOException {
		OutputStream progressOutputStream = createProgressOutputStream(out);
		super.writeTo(progressOutputStream);
	}

	protected ProgressOutputStream createProgressOutputStream(OutputStream out) {
		return new ProgressOutputStream(out, this.transferedChangedListener);
	}

	@Override
	public long getContentLength() {
		return contentLength;
	}

	public void setContentLength(long contentLength) {
		this.contentLength = contentLength;
	}
}
