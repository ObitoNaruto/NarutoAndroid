/*
 * Copyright 2014 Alibaba.com All right reserved.
 * This software is the confidential and proprietary information of Alibaba.com ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.io;

/**
 * 文件分块上传进度回调
 * @author jinzhaoyu
 * <br>Modify Information:
 * <ol> 
 * 	<li>jinzhaoyu created 2014-3-21 下午4:50:12</li>
 * </ol>
 */
public interface ChunkTransferredListener {
	/**
	 * 文件分块上传进度回调
	 * @param chunkSequence 正在传输的，从1开始的块索引
	 * @param transferredCount 此分块已传输字节数
	 */
	void onChunkTransferred(int chunkSequence, long transferredCount);
}
