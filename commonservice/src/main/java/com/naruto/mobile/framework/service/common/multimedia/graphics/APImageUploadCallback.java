package com.naruto.mobile.framework.service.common.multimedia.graphics;

import android.graphics.drawable.Drawable;

import com.naruto.mobile.framework.service.common.multimedia.api.data.APMultimediaTaskModel;
import com.naruto.mobile.framework.service.common.multimedia.graphics.data.APImageUploadRsp;

/**
 * 上传的回调接口
 * 
 * @author xiaofeng.dxf
 * 
 */
public interface APImageUploadCallback
{
	/**
	 * 压缩成功的时候回调
	 */
	public void onCompressSucc(Drawable drawable);

	/**
	 * 开始上传
	 * @param taskModel
	 */
	void onStartUpload(APMultimediaTaskModel taskModel);

	/**
	 * 上传过程中的进度回调
	 */
	public void onProcess(APMultimediaTaskModel task, int percentage);

	/**
	 * 上传结束的时候回调
	 */
	public void onSuccess(APImageUploadRsp rsp);

	/**
	 * 异常处理
	 */
	public void onError(APImageUploadRsp retMsg, Exception e);
}
