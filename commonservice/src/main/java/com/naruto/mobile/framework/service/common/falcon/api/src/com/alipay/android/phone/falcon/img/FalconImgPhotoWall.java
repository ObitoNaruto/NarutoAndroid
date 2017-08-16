package com.naruto.mobile.framework.service.common.falcon.api.src.com.alipay.android.phone.falcon.img;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


/**
 * 图片组合
 * @author wangnan.wn
 *
 */
public class FalconImgPhotoWall {
	//private ImageProperty IPbackground, IPdefault, IP1, IP2, IP3, IP4;


	private int getByteFormat(byte[] data)
	{
		if (null == data) {
			return 10;
		}
		String FormatString = dataFormat.judgeDataFormat(data);
		int result = -1;
		if (FormatString.equals("image/jpeg")) {
			result = 0;
		}
		else if (FormatString.equals("image/bmp")) {
			result = 1;
		}
		else if (FormatString.equals("image/png")) {
			result = 2;
		}
		
		return result;
	}
	
	
	/**
	 * 把4张头像合并成一个头像，排列在正方形4个角
	 * 头像数据流支持 jpg、bmp、png，背景图和默认头像必须 !=null，  第1-4个头像可以为null
	 * @author wangnan.wn
	 * @param bgByte			背景图数据流
	 * @param defaultByte		默认头像数据流
	 * @param p1Byte			第1个头像数据流
	 * @param p2Byte			第2个头像数据流
	 * @param p3Byte			第3个头像数据流
	 * @param p4Byte			第4个头像数据流
	 * @return					合成的图片的数据流
	 */
	public ByteArrayOutputStream combineImage(byte[] bgByte, byte[] defaultByte, byte[] p1Byte, byte[] p2Byte, byte[] p3Byte, byte[] p4Byte)
	{
		if ((bgByte == null) || (defaultByte == null)) {
			return null;
		}
		
		int bgFlagType = getByteFormat(bgByte);
		int defaultFlagType = getByteFormat(defaultByte);
		int p1FlagType = getByteFormat(p1Byte);
		int p2FlagType = getByteFormat(p2Byte);
		int p3FlagType = getByteFormat(p3Byte);
		int p4FlagType = getByteFormat(p4Byte);
		
		if (10 == p1FlagType) 
			p1Byte = defaultByte;
		if (10 == p2FlagType) 
			p2Byte = defaultByte;
		if (10 == p3FlagType) 
			p3Byte = defaultByte;
		if (10 == p4FlagType) 
			p4Byte = defaultByte;
		
		
		if ((bgFlagType < 0) || (defaultFlagType < 0)|| (p1FlagType < 0)|| (p2FlagType < 0)|| (p3FlagType < 0)|| (p4FlagType < 0)) {
			return null;
		}
		
		byte[] resultByte = JniFalconImg.combineImage(	bgByte, bgByte.length, bgFlagType,
														defaultByte, defaultByte.length, defaultFlagType,
														p1Byte, p1Byte.length, p1FlagType,
														p2Byte, p2Byte.length, p2FlagType,
														p3Byte, p3Byte.length, p3FlagType,
														p4Byte, p4Byte.length, p4FlagType);
		
		
		ByteArrayOutputStream bout=new ByteArrayOutputStream();
    	DataOutputStream dos=new DataOutputStream(bout);
    	try {
			dos.write(resultByte);
			dos.close();
	    	bout.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    	return bout;
	}
	
	
	
	public ByteArrayOutputStream combineImage(File bgFile, File defaultFile, File p1File, File p2File, File p3File, File p4File)
	{
		if ((bgFile == null) || (defaultFile == null)) {
			return null;
		}
		
		try {
			return combineImage(dataFormat.getBytesFromFile(bgFile), dataFormat.getBytesFromFile(defaultFile), 
					dataFormat.getBytesFromFile(p1File), dataFormat.getBytesFromFile(p2File), dataFormat.getBytesFromFile(p3File), dataFormat.getBytesFromFile(p4File));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	
//	private static byte[] getBytesFromFile(File f){
//        if (f == null){
//            return null;
//       }
//        try {
//            FileInputStream stream = new FileInputStream(f);
//            int fileLen = stream.available();
//            ByteArrayOutputStream out = new ByteArrayOutputStream(fileLen);
//            byte[] b = new byte[fileLen];
//            int n;
//            while ((n = stream.read(b)) != -1)
//                out.write(b, 0, n);
//             stream.close();
//             out.close();
//             return out.toByteArray();
//         } catch (IOException e){
//        }
//         return null;
//      }
	
	
	
	
	
	
	
	
	
	
	
//	/**合成群头像
//	 * @author wangnan.wn
//	 * @param backgroundFile	背景图File流
//	 * @param defaultImage		默认头像图File流
//	 * @param file1				左上角位置头像File流（为null时画默认头像）
//	 * @param file2				右上角位置头像File流（为null时画默认头像）
//	 * @param file3				左下角位置头像File流（为null时画默认头像）
//	 * @param file4				右下角位置头像File流（为null时画默认头像）
//	 * @return
//	 */
//	public ByteArrayOutputStream combineImage(File backgroundFile, File defaultImage, File file1, File file2, File file3, File file4)
//	{
//		if ((backgroundFile == null) || (defaultImage == null)) {
//			return null;
//		}
//		else if ((file1 == null) && (file2 == null) && (file3 == null) && (file4 == null)) {
//			return null;
//		}
//
//		IPbackground = new ImageProperty();
//		IPdefault = new ImageProperty();
//		IP1 = new ImageProperty();
//		IP2 = new ImageProperty();
//		IP3 = new ImageProperty();
//		IP4 = new ImageProperty();
//		
//		
//		IPbackground.InitImage(backgroundFile);
//		IPdefault.InitImage(defaultImage);
//		
//		
//		if (file1 != null) {
//			IP1.InitImage(file1);
//		}
//		if (file2 != null) {
//			IP2.InitImage(file2);
//		}
//		if (file3 != null) {
//			IP3.InitImage(file3);
//		}
//		if (file4 != null) {
//			IP4.InitImage(file4);
//		}
//		
//		JniFalconImg.MergeImage(IPbackground.getImageData(), IPbackground.getWidth(), IPbackground.getHeight(),
//				IPdefault.getImageData(), IPdefault.getWidth(), IPdefault.getHeight(),
//				IP1.getImageData(), IP1.getWidth(), IP1.getHeight(),
//				IP2.getImageData(), IP2.getWidth(), IP2.getHeight(),
//				IP3.getImageData(), IP3.getWidth(), IP3.getHeight(),
//				IP4.getImageData(), IP4.getWidth(), IP4.getHeight()
//				);
//		
//		Bitmap bitmap = Bitmap.createBitmap(IPbackground.getImageData(), IPbackground.getWidth(), IPbackground.getHeight(), Bitmap.Config.ARGB_8888);
//		
//		ByteArrayOutputStream bao_final = new ByteArrayOutputStream();
//		bitmap.compress(Bitmap.CompressFormat.PNG, 90, bao_final);
//		//bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bao_final);
//		
//		return bao_final;
//	}
}
