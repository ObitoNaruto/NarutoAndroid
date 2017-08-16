package com.naruto.mobile.framework.service.common.multimedia.graphics;


import com.naruto.mobile.framework.service.common.multimedia.graphics.data.APImageDeleteRsp;

/**
 * 删除图片回调
 * 
 * @author xiaofeng.dxf
 * 
 */
public interface APImageDeleteCallback
{
	/**
	 * 结束的时候的回调函数
	 */
	public void onSucc(APImageDeleteRsp rsp);

	/**
	 * 异常处理
	 */
	public void onError(APImageDeleteRsp rsp, Exception e);
}
