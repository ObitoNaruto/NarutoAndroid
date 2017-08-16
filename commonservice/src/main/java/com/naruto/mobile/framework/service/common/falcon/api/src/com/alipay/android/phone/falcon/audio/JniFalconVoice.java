package com.naruto.mobile.framework.service.common.falcon.api.src.com.alipay.android.phone.falcon.audio;

public class JniFalconVoice {
	
	
	public static void initJni()
	{
		System.loadLibrary("FalconVoice");
	}
	
	/***
	 * 初始化函数
	 * @param sample_rate   录音采样率
	 * @param packetsize    一帧数据长度 
	 * @param maxsec        最长录制时间，单位s
	 * @return 成功与否
	 */
	public static native boolean Init(int sample_rate,int packetsize,int maxsec);

	/***
	 * 释放函数
	*/
	public static native void Release();
	
	/***
	 * 去噪处理函数
	 * @param in          输入数据
	 * @param length      数据长度 
	 * @param bfinish     是否是最后一帧，true为最后一帧
	 * @return 处理后的数据
	 */
	public static native short[] RemoveNoise(short[] in, int length, boolean bfinish);
	
}
