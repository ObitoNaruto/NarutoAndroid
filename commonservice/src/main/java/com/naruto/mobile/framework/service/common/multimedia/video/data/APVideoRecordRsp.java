package com.naruto.mobile.framework.service.common.multimedia.video.data;

import android.R.integer;

public class APVideoRecordRsp {

	public static final int CODE_SUCCESS = 0;

	/**
	 * 麦克风未知错误
	 */
	public static final int CODE_ERR_MIC_UNKNOWN_ERROR = 0;
	/**
	 * 麦克风权限受限
	 */
	public static final int CODE_ERR_MIC_PERMISSION_DENIED = 1;
	/**
	 * 麦克风无效操作
	 */
	public static final int CODE_ERR_MIC_INVALID_OPERATION = 2;
	/**
	 * 麦克风读取无效值
	 */
	public static final int CODE_ERR_MIC_BAD_VALUE = 3;
	/**
	 * 获取BufferIndex异常
	 */
	public static final int CODE_ERR_MIC_INVALID_BUFFER_INDEX = 4;
	/**
	 * 停用Mic失败
	 */
	public static final int CODE_ERR_MIC_STOP_FAILED = 5;

	/***********************  视频相关  ***************************/
	/**
	 * 摄像头打开异常
	 */
	public static final int CODE_ERR_CAMERA_OPEN = 100;

	public String mId = "";
	public int mRspCode = -1;
	public int mWidth;
	public int mHeight;


	@Override
	public String toString() {
		return "APVideoRecordRsp{" +
				"mId='" + mId + '\'' +
				", mRspCode=" + mRspCode +
				'}';
	}
}
