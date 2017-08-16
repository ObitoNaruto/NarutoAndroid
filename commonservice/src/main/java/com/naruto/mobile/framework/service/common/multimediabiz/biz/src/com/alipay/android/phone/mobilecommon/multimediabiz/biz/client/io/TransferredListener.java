/*
 * Copyright 2014 Alibaba.com All right reserved.
 * This software is the confidential and proprietary information of Alibaba.com ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.io;

/**
 * 
 * @author jinzhaoyu
 * <br>Modify Information:
 * <ol> 
 * 	<li>jinzhaoyu created 2014-3-20 下午4:43:21</li>
 * </ol>
 */
public interface TransferredListener {
	/**
	 * 已传输字节数发生变化时回调
	 * @param transferredCount
	 */
	void onTransferred(long transferredCount);
}
