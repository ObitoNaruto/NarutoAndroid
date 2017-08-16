package com.naruto.mobile.framework.service.common.falcon.api.src.com.alipay.android.phone.falcon.img;

import java.security.PublicKey;

import android.R.integer;
import android.app.NativeActivity;
import android.graphics.Bitmap;



public class JniFalconImg {
	
	
	public static void initJni()
	{
		System.loadLibrary("FalconImg");
	}
	
	


	//////////////zy/////////////
//	public static native byte[] CompressImage_JPEG(byte[] pData, int dataLen,int width,int height, int level);
//	public static native byte[] CompressImageDefault_JPEG(byte[] pData, int dataLen,int level);
	
	
	/***
	 * 对bitmap进行智能压缩，保持原图长宽不变
	 * @param bitmap   支持rgb565 与 argb8888格式
	 * @param level   0 1 2  三个等级，反应压缩质量   推荐使用2 
	 * @param imageType  目前只提供 0  jpeg压缩
	 * @return 压缩流
	 */
	public static native byte[] CompressImageBitmapDefaultNoChange(Bitmap bitmap, int level, int imageType);	
	
	/***
	 * 对bitmap进行智能压缩，默认最大边不超过1280
	 * @param bitmap   支持rgb565 与 argb8888格式
	 * @param level   0 1 2  三个等级，反应压缩质量   推荐使用2 
	 * @param imageType  目前只提供 0  jpeg压缩
	 * @return 压缩流
	 */
	public static native byte[] CompressImageBitmapDefaultnew(Bitmap bitmap, int level, int imageType);	
	
	
	/***
	 * 对bitmap进行智能压缩，按照输入的目标宽高，及原始图像长宽比，计算出最终的不变形宽高
	 * @param bitmap   支持rgb565 与 argb8888格式
	 * @param level   0 1 2  三个等级，反应压缩质量   推荐使用2 
	 * @param imageType  目前只提供 0  jpeg压缩
	 * @return 压缩流
	 */
	public static native byte[] CompressImageBitmapSizenew(Bitmap bitmap, int deswidth, int desheight, int level, int imageType);
	
	
	///////////////////////////////////////////////

		
	public static native byte[] ResizeImage(byte[] pData, int dataLen,int width,int height, int imageType);
	
	

	
	public static native byte[] combineImage(byte[] imageBG, int lenBG, int imageTypeBG, 
											byte[] imageDefault, int lenDefault, int imageTypeDefault,
											byte[] image1, int len1, int imageType1,
											byte[] image2, int len2, int imageType2,
											byte[] image3, int len3, int imageType3,
											byte[] image4, int len4, int imageType4);
	
	public static native byte[] CutImage(byte[] pData, int dataLen, int imageType);
	public static native byte[] CutImage1(byte[] pData, int dataLen, int imageType, int maxLen, int minLen, float scale);
	public static native void calcultDesWidthHeight(int width, int height, int maxLen, int minLen, float scale, int[] desLen);
	
	public static native int[] CutImageBGR(byte[] pData, int dataLen, int imageType, int maxLen, int minLen, float scale, int[] size);

//public static native int CutImageKeepRatioJpeg(byte[] path, Bitmap bitmap, int insampleSize, int degree);
	}
