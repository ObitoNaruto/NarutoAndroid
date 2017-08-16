package com.naruto.mobile.framework.service.common.falcon.api.src.com.alipay.android.phone.falcon.img;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.text.TextUtils;

public class dataFormat {

	static public String judgeDataFormat(byte[] data)
	{
		String reulString = "";
		
		
		//解析文件头，获取图片格式
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true; //确保图片不加载到内存
		Bitmap bmp=BitmapFactory.decodeByteArray(data, 0, data.length, opts);


		reulString = opts.outMimeType;  //图片类型
//		opts.outWidth, optsHeight;  //图片大小
//
//		//解码时指定解出图片的大小
//		int height = options.outHeight * 200 / options.outWidth;
//		options.outWidth = 200；
//		options.outHeight = height; 
//		options.inJustDecodeBounds = false;
//		Bitmap bmp = BitmapFactory.decodeFile(path, options);
//		image.setImageBitmap(bmp);
		
		
		return reulString;
	}
	
	
	/***
	 * zhangyu.zy
	 * @param resulString
	 * @return
	 */
	static public int DataFormattoType(String resulString)
	{
		int imageType = -1;
		
//		if(resulString.isEmpty())
//		{
//			return imageType;
//		}
		
		if( TextUtils.isEmpty(resulString) )
			return imageType;
		
    	if (resulString.equals("image/jpeg")) {
    		imageType = 0;
		}
    	else if (resulString.equals("image/bmp")){
			imageType = 1;
		}
    	else if(resulString.equals("image/png")){
			imageType = 2;
		}
    	else if(resulString.equals("image/gif")){
			imageType = 3;
		}
    	else if(resulString.equals("image/webp")){
			imageType = 4;
		}
    	
    	return imageType;
	}
	
	
	static public int calc_rotate(File file) {
		
		int degree = 0;
		
		String path = "";
		
		if(file!=null)
			path = file.getAbsolutePath();
		else {
			return 0;
		}
		
	    try {
	            ExifInterface exifInterface = new ExifInterface(path);
	            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
	            switch (orientation) {
	            case ExifInterface.ORIENTATION_ROTATE_90:
	                    degree = 90;
	                    break;
	            case ExifInterface.ORIENTATION_ROTATE_180:
	                    degree = 180;
	                    break;
	            case ExifInterface.ORIENTATION_ROTATE_270:
	                    degree = 270;
	                    break;
	            }
	    } catch (IOException e) {
	            e.printStackTrace();
	    }


		
		return degree;
	}
	
	
	
	
	static public ByteArrayOutputStream InputStreamToByteArray(InputStream image) throws IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();  
 		//int fileLen= image.available();
 		
 		byte[] buffer = new byte[4096];  
 		int len;  
 		while ((len = image.read(buffer)) > -1 ) {  
 		    baos.write(buffer, 0, len);  
 		}  
 		baos.flush();     
 		baos.close();
 		
 		return baos;
	}
	
	
	
	public static byte[] streamToBytes(InputStream in) {
		ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
		byte[] buffer = new byte[1024];
		int len = -1;
		try {
			while ((len = in.read(buffer)) >= 0) {
				out.write(buffer, 0, len);
				out.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return out.toByteArray();
	}
	
	
	
	public static Bitmap ScaleRotateImg(Bitmap bitmap, float scale, int rotate)
	{
		try
		{
			Matrix matrix = new Matrix(); 
			matrix.postScale(scale, scale);    //scale
		    
		    matrix.postRotate(rotate); //rotate
		    
		    Bitmap resizeBitmap = null;
		    
		    if(bitmap!=null)
		    {
		    	resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);  
		    }
		    
		    
		    matrix = null;
		    return resizeBitmap;
			
		}finally
		{
			
		}
		
	}
	
	public static void GetRotateSize(int rotatedegree, int[] Iwidth, int[] Iheight) {
		
		if(rotatedegree==90 || rotatedegree == 270)	
		{
			int tempSize = Iwidth[0];
			Iwidth[0] = Iheight[0];
			Iheight[0] = tempSize;
			
		}

	}
	
	
	public static void DecodeWidthHeight(BitmapFactory.Options options, File mfile,int []Isize) throws IOException {
		
//		FileInputStream fis_f = null;
		Isize[0] = 0;
		Isize[1] = 0;
		try{
			
			options.inJustDecodeBounds = true;
			    
//			 fis_f = new FileInputStream(mfile);
//			 BitmapFactory.decodeStream(fis_f, null, options);
			 BitmapFactory.decodeFile(mfile.getAbsolutePath(), options);
//			 fis_f.close();
			 
			  // Find the correct scale value. It should be the power of 2.
			 Isize[0] = options.outWidth;
			 Isize[1] = options.outHeight;

			
		}finally{
			
//			fis_f.close();
			
		}
		   
	}
	

	public static boolean isImage(File mfile) throws IOException {
		
		try{
			
			BitmapFactory.Options option_decodeOptions = new BitmapFactory.Options();
			option_decodeOptions.inJustDecodeBounds = true;
			    
			 BitmapFactory.decodeFile(mfile.getAbsolutePath(), option_decodeOptions);
			 
			  // Find the correct scale value. It should be the power of 2.
			if(option_decodeOptions.outMimeType==null || option_decodeOptions.outWidth<=0 || option_decodeOptions.outHeight<=0)
			{
				return false;
			}
			
			return true;
		}finally{
			
			
		}
		   
	}
	
	
	
    public static byte[] getBytesFromFile(File f) throws IOException{
        if (f == null){
            return null;
       }
        
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(f);
            int fileLen = stream.available();
            ByteArrayOutputStream out = new ByteArrayOutputStream(fileLen);
            byte[] b = new byte[fileLen];
            int n;
            while ((n = stream.read(b)) != -1)
                out.write(b, 0, n);
             stream.close();
             out.close();
             return out.toByteArray();
         } catch (IOException e){
        }
        finally{
        	stream.close();
        }
         return null;
      }
	
}
