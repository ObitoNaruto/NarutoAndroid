package com.naruto.mobile.framework.service.common.multimedia.graphics;


import com.naruto.mobile.framework.service.common.multimedia.graphics.data.APImageDownloadBatchRsp;

public interface APImageDownloadBatchCallback
{
	/**
	 * 结束的时候的回调函数
	 */
	public void onSucc(APImageDownloadBatchRsp rsp);

	/**
	 * 进度回调函数
	 */
	public void onProcess(int percentage);

	/**
	 * 异常处理
	 */
	public void onError(APImageDownloadBatchRsp rsp, Exception e);
}
