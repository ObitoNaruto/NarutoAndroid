package com.naruto.mobile.framework.service.common.falcon.api.src.com.alipay.android.phone.falcon.img;


public interface CutCallBack {
	//主要用于业务根据当前传入的图片预估解码所需内存的大小
	//public void onCalcMemSize(long memSize);
	
	public void onCalcMemSize(long memSize, boolean isForceRelease);

}
