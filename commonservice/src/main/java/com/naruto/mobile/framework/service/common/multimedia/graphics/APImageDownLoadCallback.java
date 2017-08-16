package com.naruto.mobile.framework.service.common.multimedia.graphics;

import com.naruto.mobile.framework.service.common.multimedia.graphics.data.APImageDownloadRsp;

/**
 * 下载的回调接口
 * 
 */
public interface APImageDownLoadCallback
{
	/**
	 * 结束的时候的回调函数
	 */
	public void onSucc(APImageDownloadRsp rsp);

	/**
	 * 进度回调函数
	 */
	public void onProcess(String path, int percentage);

	/**
	 * 异常处理
	 */
	public void onError(APImageDownloadRsp rsp, Exception e);
}
