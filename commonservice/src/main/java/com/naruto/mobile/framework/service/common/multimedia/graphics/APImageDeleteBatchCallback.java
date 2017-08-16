package com.naruto.mobile.framework.service.common.multimedia.graphics;


import com.naruto.mobile.framework.service.common.multimedia.graphics.data.APImageDeleteBatchRsp;

public interface APImageDeleteBatchCallback
{
	/**
	 * 结束的时候的回调函数
	 */
	public void onSucc(APImageDeleteBatchRsp rsp);

	/**
	 * 异常处理
	 */
	public void onError(APImageDeleteBatchRsp rsp, Exception e);
}
