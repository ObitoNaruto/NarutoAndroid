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


/**
 * 
 * @author jinzhaoyu
 * <br>Modify Information:
 * <ol> 
 * 	<li>jinzhaoyu created 2014-3-21 下午4:48:06</li>
 * </ol>
 */
public class ProgressChunkFileBody extends ChunkFileBody implements TransferredListener{

	private ChunkTransferredListener  chunkTransferredListener;

	/**
	 * @param file
	 * @param chunkSequence 从1开始的块索引
	 * @param chunkSize 每块大小，应该是一个常量值
	 * @param chunkTransferredListener 上传进度回调，可以为null
	 */
	public ProgressChunkFileBody(File file, int chunkSequence, long chunkSize,
			ChunkTransferredListener  chunkTransferredListener) {
		super(file, chunkSequence, chunkSize);
		this.chunkTransferredListener = chunkTransferredListener;
	}

	/* (non-Javadoc)
	 * @see com.taobao.django.client.io.ChunkFileBody#writeTo(java.io.OutputStream)
	 */
	@Override
	public void writeTo(OutputStream out) throws IOException {
		ProgressOutputStream outputStream = new ProgressOutputStream(out, this);
		super.writeTo(outputStream);
	}

	/* (non-Javadoc)
	 * @see com.taobao.django.client.io.TransferedListener#onTransferred(long)
	 */
	@Override
	public void onTransferred(long transferredCount) {
		if(chunkTransferredListener != null){
			chunkTransferredListener.onChunkTransferred(getChunkSequence(), transferredCount);
		}
	}

	
	
}
