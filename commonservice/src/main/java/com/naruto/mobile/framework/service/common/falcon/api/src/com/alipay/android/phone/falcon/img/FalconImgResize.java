package com.naruto.mobile.framework.service.common.falcon.api.src.com.alipay.android.phone.falcon.img;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


import android.R.integer;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


/**
 * 图片resize
 * @author wangnan.wn
 *
 */
public class FalconImgResize {

    /**
     * 等比例缩图，指定宽高，若指定的宽高大于默认值，则采用默认值
     * @param image				图片数据流，支持jpg,png,bmp
     * @param newWidth			指定最大宽度
     * @param newHeight			指定最大高度
     * @return					缩图之后的数据流，jpg,png,bmp输入流返回jpeg图片，其他输入流返回原数据流
     * @throws IOException
     */
    public ByteArrayOutputStream resizeImage(byte[] image, int newWidth, int newHeight) throws IOException
    {
    	if (image == null) {
			return null;
		}
    	
    	String dataFormatString = dataFormat.judgeDataFormat(image);
    	if ((!dataFormatString.equals("image/jpeg")) && (!dataFormatString.equals("image/bmp"))) {
    		ByteArrayOutputStream bout=new ByteArrayOutputStream();
        	DataOutputStream dos=new DataOutputStream(bout);
        	dos.write(image);
        	dos.close();
        	bout.close();
        	
        	return bout;
		}
    	
    	
    	
    	int imageType = 0;
    	if (dataFormatString.equals("image/jpeg")) {
    		imageType = 0;
		}
    	else if (dataFormatString.equals("image/bmp")){
			imageType = 1;
		}
    	byte[] resultByte = JniFalconImg.ResizeImage(image, image.length, newWidth, newHeight, imageType);
    	
    	
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
    
    
    
    /**
     * 等比例缩图，指定宽高，若指定的宽高大于默认值，则采用默认值
     * @param image				图片数据流，支持jpg,png,bmp
     * @param newWidth			指定最大宽度
     * @param newHeight			指定最大高度
     * @return					缩图之后的数据流，jpg,png,bmp输入流返回jpeg图片，其他输入流返回原数据流
     * @throws IOException
     */
    public ByteArrayOutputStream resizeImage(File file, int newWidth, int newHeight) throws IOException
    {
    	if (file == null) {
			return null;
		}
    	
    	
    	return resizeImage(dataFormat.getBytesFromFile(file), newWidth, newHeight);
    }
    
    
//    private static byte[] getBytesFromFile(File f){
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
    
//	
//	public void setDefaultLength(int defaultLength)
//	{
//		if (defaultLength > 0) {
//			this.defaultLength = defaultLength;
//		}
//	}
//	
//	public int getDefaultLength()
//	{
//		return this.defaultLength;
//	}
//	
//	/**
//	 * 等比缩放图片，若传入新的宽高与原图片宽高不一致，会等比缩放，宽高最大填充目标宽高
//	 * @param file			图片文件
//	 * @param newWidth		目标图片宽度
//	 * @param newHeight		目标图片高度
//	 * @return
//	 */
//	@SuppressWarnings("unused")
//	public ByteArrayOutputStream resizeImage(File file, int newWidth, int newHeight)
//	{
//		if ((newWidth <= 0) || (newHeight <= 0)) {
//			return null;
//		}
//		FileInputStream fileInputStream = null;
//		try {
//			fileInputStream = new FileInputStream(file);
//		} catch (FileNotFoundException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		
//		
//		Bitmap imgmap = BitmapFactory.decodeStream(fileInputStream);
//		if(imgmap!=null)
//        {
//			width = imgmap.getWidth();
//    		height = imgmap.getHeight();
//    		float scaleWidth = (float)width / (float)newWidth;
//    		float scaleHeight = (float)height / (float)newHeight;
//    		
//    		if(scaleWidth > scaleHeight)
//    		{
//    			dwidth = newWidth;
//    			dheight = ((dwidth*height)/width);
//    		}
//    		else 
//    		{
//    			dheight = newHeight;
//    			dwidth = ((dheight*width)/height);
//    		}
//    		
//    		mPicData = new int[width * height];
//    		mPicData_scale = new int[dwidth*dheight];
//    		imgmap.getPixels(mPicData,0,width,0,0,width,height);
//    		JniFalconImg.BiLinearInsert(mPicData, width, height, width*4, mPicData_scale, dwidth, dheight, dwidth*4, 4);
//    		
//    		Bitmap bitmap = Bitmap.createBitmap(mPicData_scale, dwidth, dheight, Bitmap.Config.ARGB_8888);
//    		ByteArrayOutputStream bao_final = new ByteArrayOutputStream();
//    		bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bao_final);
//    		
//    		return bao_final;
//        }
//		else {
//			return null;
//		}
//	}
//	
//	/**更改图片大小，等比缩放
//	 * @author wangnan.wn
//	 * @param file				要改变尺寸的文件流
//	 * @param targetLength		改变后最大边长度
//	 * @return
//	 */
//	@SuppressWarnings("unused")
//	public ByteArrayOutputStream resizeImage(File file, int targetLength)
//	{
//		FileInputStream fileInputStream = null;
//		try {
//			fileInputStream = new FileInputStream(file);
//		} catch (FileNotFoundException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		setDefaultLength(targetLength);
//		
//		Bitmap imgmap = BitmapFactory.decodeStream(fileInputStream);
//		if(imgmap!=null)
//        {
//			width = imgmap.getWidth();
//    		height = imgmap.getHeight();
//    		
//    		if(width > height)
//    		{
//    			dwidth = defaultLength;
//    			dheight = ((dwidth*height)/width);
//    		}
//    		else 
//    		{
//    			dheight = defaultLength;
//    			dwidth = ((dheight*width)/height);
//    		}
//    		
//    		mPicData = new int[width * height];
//    		mPicData_scale = new int[dwidth*dheight];
//    		imgmap.getPixels(mPicData,0,width,0,0,width,height);
//    		JniFalconImg.BiLinearInsert(mPicData, width, height, width*4, mPicData_scale, dwidth, dheight, dwidth*4, 4);
//    		
//    		Bitmap bitmap = Bitmap.createBitmap(mPicData_scale, dwidth, dheight, Bitmap.Config.ARGB_8888);
//    		ByteArrayOutputStream bao_final = new ByteArrayOutputStream();
//    		bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bao_final);
//    		
//    		return bao_final;
//        }
//		else {
//			return null;
//		}
//	}
//	
//	/**更改图片大小，等比缩放
//	 * @author wangnan.wn
//	 * @param file				要改变尺寸的文件流，尺寸为默认尺寸
//	 * @return
//	 */
//	public ByteArrayOutputStream resizeImage(File file)
//	{
//		return resizeImage(file, defaultLength);
//	}
}
