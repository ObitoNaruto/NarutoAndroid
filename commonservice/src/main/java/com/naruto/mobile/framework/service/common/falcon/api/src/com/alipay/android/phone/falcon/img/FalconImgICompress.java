package com.naruto.mobile.framework.service.common.falcon.api.src.com.alipay.android.phone.falcon.img;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import java.io.IOException;
import java.io.InputStream;


import android.R.integer;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;



public class FalconImgICompress {
	

	private CutCallBack callBack = null;
	private boolean bDebug = true;
    
    private int maxLen = 1280;//最长边最大尺寸
    
    
	public void registeCallBack(CutCallBack cb){
		callBack = cb;
	}
	
	public void unregisteCallBack(){
		callBack = null;
	}
    
    
	public void doCallBack(int width, int height,boolean isForce) {
		
		 if(callBack != null){
	   		  long size = width*height*5;
	   		  callBack.onCalcMemSize(size,isForce);
	   		  if(bDebug && size > 1024*1024*2 ){
	   			  
	   			  //Log.i("BITMAP_SIZE", "tmp_w="+width+";tmp_h="+height);
//	   			LoggerFactory.getTraceLogger().debug("FalconImg", "BITMAP_SIZE:"+"tmp_w="+width+";tmp_h="+height);
	   			      			  
	   		  }
	   	  	}
		
	}
	
	
	/***2015\7\24
	 * 根据输入的图像宽、高 判定是否为超高图
	 * @param ImgWidth  图像宽
	 * @param ImgHeight 图像高
	 * @param ScreenWidth  屏幕宽
	 * @param ScreenHeight 屏幕高
	 * @return   0  非超高图   1 超高图  2 超宽图
	 */
	public static int isSuperHeight(int ImgWidth, int ImgHeight,int ScreenWidth,int ScreenHeight) 
	{
		if(ImgHeight<=0  || ImgWidth<=0)
			return 0;

        float scale_ori = ((float) ImgWidth) / ImgHeight;

        if (scale_ori < 0.5f)
            return 1;

        if (scale_ori > 2.0f)
            return 2;

		return 0;
	}
	
	

   /***
    * zhangyu.zy 
    * @param file  待压缩图像 file
    * @param level  压缩水平   0低（最大程度压缩）    1（中等） 2（高  ）      推荐使用  2
    * @return  返回压缩流    默认最大边长 1280
    * @throws IOException 
    */
    
    public ByteArrayOutputStream GenerateCompressImage_new(File file, int level) throws IOException
	{
    	
    	if (file == null) {
			return null;
		}
    	
     	int rotatedegree = dataFormat.calc_rotate(file);
     	int imgtype = -1; //图片类型  	JPEG = 0, BMP, PNG
     	
     	FileInputStream fis_c = null;
     	ByteArrayOutputStream baos_c = null;
     	ByteArrayOutputStream bout_c = null;
      	DataOutputStream dos_c= null;
      	
      	Bitmap bitmap_c = null;
     	Bitmap rotateBitmap_c = null;
     	
    try{
     		
     	fis_c = new FileInputStream(file);  
             
        int fileLen= fis_c.available();
             

     	// decode image size
         BitmapFactory.Options o_c = new BitmapFactory.Options();
         o_c.inJustDecodeBounds = true;    
         BitmapFactory.decodeStream(fis_c, null, o_c);
         fis_c.close();

         // Find the correct scale value. It should be the power of 2.
         int []width_tmp = new int[1];
         width_tmp[0] = o_c.outWidth;
         int []height_tmp = new int[1];
         height_tmp[0]= o_c.outHeight;
         
         
    	 o_c.inJustDecodeBounds = false;
    	 o_c.inPurgeable = true;  
         o_c.inInputShareable = true;  
         o_c.inDither = true;
         o_c.inPreferredConfig = Bitmap.Config.RGB_565;
         
         if (fileLen/1024 < 50)//if image is small，just return the whole
         {
             fis_c = new FileInputStream(file);
            
             doCallBack(width_tmp[0], height_tmp[0],true);
           
            // bitmap_c = BitmapFactory.decodeStream(fis_c, null,o_c);
             bitmap_c = BitmapFactory.decodeFileDescriptor(fis_c.getFD(), null, o_c);
             fis_c.close();
      	 
             if(rotatedegree!=0)
             {
            	 Matrix matrix = new Matrix(); 
            	 matrix.postRotate(rotatedegree);
          	 
            	 rotateBitmap_c = Bitmap.createBitmap(bitmap_c, 0, 0, bitmap_c.getWidth(),    
                          bitmap_c.getHeight(), matrix, true);
          	 
            	 baos_c = new ByteArrayOutputStream();
            	 rotateBitmap_c.compress(Bitmap.CompressFormat.JPEG, 98, baos_c);
            	 baos_c.close();
          	 
            	 bitmap_c.recycle();
            	 rotateBitmap_c.recycle();
          	 
            	 return baos_c;
             }
      	
      	 
	      	 baos_c = new ByteArrayOutputStream();
	      	 bitmap_c.compress(Bitmap.CompressFormat.JPEG, 98, baos_c);
	      	 baos_c.close();
	      	 bitmap_c.recycle();
	      	 
	         return baos_c; 
             
         }
                 
         //获取图片类型
//         String reulString = "";
//         reulString = o.outMimeType;
//         imgtype = dataFormat.DataFormattoType(reulString);
         imgtype = 0;
                         
         int scaleimg = getScaleimg (width_tmp,height_tmp,maxLen,maxLen);		
	        // decode with inSampleSize
	        o_c.inSampleSize = scaleimg;

	        fis_c = new FileInputStream(file);	        
	        doCallBack(width_tmp[0],height_tmp[0],true);	        
	      //  bitmap_c = BitmapFactory.decodeStream(fis_c, null, o_c);
	        bitmap_c = BitmapFactory.decodeFileDescriptor(fis_c.getFD(), null, o_c);
	        fis_c.close();	        
	        if(rotatedegree!=0)
	        {
	        	 Matrix matrix = new Matrix(); 
	        	 matrix.postRotate(rotatedegree);	        	 
	        	 rotateBitmap_c = Bitmap.createBitmap(bitmap_c, 0, 0, bitmap_c.getWidth(),    
	                        bitmap_c.getHeight(), matrix, true);  	        	 	        	 
	        	//byte[] resultByte = JniFalconImg.CompressImageBitmapDefault(rotateBitmap, level);
	        	 
	        	 
	        	
	        	 byte[] resultByte = JniFalconImg.CompressImageBitmapDefaultnew(rotateBitmap_c, level,imgtype);
	        	 
	        	bout_c=new ByteArrayOutputStream();
	        	
	        	if(resultByte==null)
	        	{
	        		rotateBitmap_c.compress(Bitmap.CompressFormat.JPEG, 75, bout_c);
	        		bout_c.close();
	        	}
	          	else {
					
	          		dos_c=new DataOutputStream(bout_c);
		          	dos_c.write(resultByte);
		          	dos_c.close();
		          	bout_c.close();	    
				}
	  	        bitmap_c.recycle();
	  	        rotateBitmap_c.recycle();
	  	        
	          	      	
	          	return bout_c;
	        }
	       	        	     
	       	        
	      // byte[] resultByte = JniFalconImg.CompressImageBitmapDefault(bitmap, level);
	       byte[] resultByte = JniFalconImg.CompressImageBitmapDefaultnew(bitmap_c, level,imgtype);
	       
	       
	       bout_c=new ByteArrayOutputStream();
       		if(resultByte==null)
       		{
       			bitmap_c.compress(Bitmap.CompressFormat.JPEG, 75, bout_c);
       			
       		}
         	else {
				
         		dos_c=new DataOutputStream(bout_c);
	          	dos_c.write(resultByte);
	          	dos_c.close();
			}

       		bout_c.close();
	        bitmap_c.recycle();
	        
        	
        	return bout_c;
	       	       
	}catch (FileNotFoundException e) {
    }finally{
    	if(fis_c!=null)
    	{
    		fis_c.close();
    	}
    	
    	if(baos_c!=null)
    	{
    		baos_c.close();
    	}

    	
    	if(bout_c!=null)
    	{
    		bout_c.close();
    	}
    	
    	if(dos_c!=null)
    	{
    		dos_c.close();
    	}

    	if(bitmap_c!=null)
    		bitmap_c.recycle();
    	
    	if(rotateBitmap_c!=null)
    		rotateBitmap_c.recycle();
    }
    
    
     	return null;
	}
    
    /***
     * zhangyu.zy
     * @param file   待压缩图像file  保持长宽比
     * @param level   压缩水平     0  1  2  推荐使用  2
     * @param newWidth   所需裁剪结果图最大宽
     * @param newHeight  所需裁剪结果图最大高
     * @return   返回压缩流
     * @throws Exception 
     */
@SuppressWarnings("finally")
	public ByteArrayOutputStream GenerateCompressImage_new(File file, int level, int newWidth, int newHeight) throws IOException
    {
         // Bitmap imgmap = getDiskBitmap(ImgPath);
    	if ((newWidth <= 0) || (newHeight <= 0) || (file == null)) {
			return null;
		}
    	
    	int []desSize = {newWidth,newHeight};
    	
    	int rotatedegree = dataFormat.calc_rotate(file);
    	
      	int imgtype = -1; //图片类型  	JPEG = 0, BMP, PNG
      	FileInputStream fis_cn = null;
      	ByteArrayOutputStream bout_cn=null;
      	DataOutputStream dos_cn=null;
    //  	ByteArrayOutputStream baos_cn = null;
      	
        //获取图像信息
      	Bitmap bitmap_cn = null;
      	Bitmap rotateBitmap_cn = null;
      	BitmapFactory.Options o_cn = null;
      	
        int[] width_tmp = new int[1];
        int[] height_tmp = new int[1];
        int OldInsampleSize = 1;
      	
     	
     	try{
     		     		       
         fis_cn = new FileInputStream(file);  
        
         int fileLen= fis_cn.available();
         
    	// decode image size
        o_cn = new BitmapFactory.Options();

        int []ImgSize = new int[2];
        dataFormat.DecodeWidthHeight(o_cn, file, ImgSize);

        width_tmp[0] = ImgSize[0];
        height_tmp[0] = ImgSize[1];
         
         o_cn.inDither = true;
         o_cn.inJustDecodeBounds = false;
    	 o_cn.inPurgeable = true;  
         o_cn.inInputShareable = true;  
         o_cn.inPreferredConfig = Bitmap.Config.RGB_565;	
         o_cn.inSampleSize = 1;
         
         if (fileLen/1024 < 50)//if image is small，just return the whole
         {

             doCallBack(ImgSize[0], ImgSize[1],false);
             
            // bitmap_cn = BitmapFactory.decodeStream(fis_cn, null,o_cn);
             bitmap_cn = BitmapFactory.decodeFileDescriptor(fis_cn.getFD(), null, o_cn);
        	 fis_cn.close();
 
        	rotateBitmap_cn = dataFormat.ScaleRotateImg(bitmap_cn, 1.0f, rotatedegree);
            	 
            bout_cn = new ByteArrayOutputStream();
            rotateBitmap_cn.compress(Bitmap.CompressFormat.JPEG, 98, bout_cn);
            bout_cn.close();
            	 
            bitmap_cn.recycle();
            rotateBitmap_cn.recycle();
            
         }
         else {
        	 
        	 //获取图片类型
//           String reulString = "";
//           reulString = o.outMimeType;
//           imgtype = dataFormat.DataFormattoType(reulString);
           imgtype = 0;
           
           //按长宽比计算结果图尺寸         
           reCalcDesSize(width_tmp[0], height_tmp[0], desSize);
           newWidth = desSize[0];
           newHeight = desSize[1];
      	 if (rotatedegree == 90 || rotatedegree == 270) {
      		 int tempw = newWidth;
      		 newWidth = newHeight;	        		 
      		 newHeight = tempw;
  		}
                                
            //最大可缩放比例    
      	 
        int scaleimg = getScaleimg (width_tmp,height_tmp,newWidth,newHeight); 		
  	        // decode with inSampleSize
  	     o_cn.inSampleSize = scaleimg;
  	     OldInsampleSize = scaleimg;
  	     
  	       // o.inPreferredConfig = Bitmap.Config.RGB_565;
  	    // fis_cn = new FileInputStream(file);
  	     doCallBack(width_tmp[0],height_tmp[0],false);   	        
  	    // bitmap_cn = BitmapFactory.decodeStream(fis_cn, null, o_cn);
  	     
  	     bitmap_cn = BitmapFactory.decodeFileDescriptor(fis_cn.getFD(), null, o_cn);
  	     fis_cn.close();	        
    	    
  	     rotateBitmap_cn = dataFormat.ScaleRotateImg(bitmap_cn, 1.0f, rotatedegree);
  	        	 
      	if(rotateBitmap_cn.getWidth() < newWidth && rotateBitmap_cn.getHeight() < newHeight)
      	 {
      		 newWidth = rotateBitmap_cn.getWidth();
      		 newHeight = rotateBitmap_cn.getHeight();
      	 }
          	 
      	
      //	 Log.i("start","in");
          //	byte[] resultByte = JniFalconImg.CompressImageBitmapSizeDefault(rotateBitmap,newWidth, newHeight, level);
          byte[] resultByte = JniFalconImg.CompressImageBitmapSizenew(rotateBitmap_cn,newWidth, newHeight, level,imgtype);
          
          
          bout_cn = new ByteArrayOutputStream(); 
          
          //针对c代码层无法获取像素的情况
          if(resultByte==null)
          {
               rotateBitmap_cn.compress(Bitmap.CompressFormat.JPEG, 75, bout_cn);
               	 
          }
          else
          {
        	dos_cn=new DataOutputStream(bout_cn);
        	dos_cn.write(resultByte);
        	dos_cn.close();
        
          }
          	
      	bout_cn.close();
      //    Log.i("start","end");
          bitmap_cn.recycle();
          rotateBitmap_cn.recycle();
          

			
		}
   
        
      		       
	}catch (OutOfMemoryError e) {
		
		
//		LoggerFactory.getTraceLogger().debug("FalconImg", "compress with file out of memory,OldInsampleSize: "+OldInsampleSize);
   		
		try {
			
			fis_cn.close();
			fis_cn = new FileInputStream(file);
			
			o_cn.inSampleSize =OldInsampleSize*2;
			
			if(o_cn.inSampleSize!=0)
		    {
		    	 //doCallBack(o_cn.outWidth/o_cn.inSampleSize, o_cn.outHeight/o_cn.inSampleSize,true);
				doCallBack(width_tmp[0]/2, height_tmp[0]/2, true);
		    }

			//bitmap_cn = BitmapFactory.decodeStream(fis_cn, null,o_cn);
			bitmap_cn = BitmapFactory.decodeFileDescriptor(fis_cn.getFD(), null, o_cn);
			fis_cn.close();
			
			rotateBitmap_cn = dataFormat.ScaleRotateImg(bitmap_cn, 1.0f, rotatedegree);
			
			if(rotateBitmap_cn.getWidth() < newWidth && rotateBitmap_cn.getHeight() < newHeight)
	   	 	{
	   		 newWidth = rotateBitmap_cn.getWidth();
	   		 newHeight = rotateBitmap_cn.getHeight();
	   	 	}
	       	 
	       //	byte[] resultByte = JniFalconImg.CompressImageBitmapSizeDefault(rotateBitmap,newWidth, newHeight, level);
	       byte[] resultByte = JniFalconImg.CompressImageBitmapSizenew(rotateBitmap_cn,newWidth, newHeight, level,imgtype);
	       
          bout_cn = new ByteArrayOutputStream(); 
          
          //针对c代码层无法获取像素的情况
          if(resultByte==null)
          {
               rotateBitmap_cn.compress(Bitmap.CompressFormat.JPEG, 75, bout_cn);
               	 
          }
          else
          {
        	dos_cn=new DataOutputStream(bout_cn);
        	dos_cn.write(resultByte);
        	dos_cn.close();
        	
          }
          	
          bout_cn.close();
      //    Log.i("start","end");
          bitmap_cn.recycle();
          rotateBitmap_cn.recycle();
	       	
//	       bitmap_cn.recycle();
//	       rotateBitmap_cn.recycle();
//	       
//	     	bout_cn=new ByteArrayOutputStream();
//	     	dos_cn=new DataOutputStream(bout_cn);
//	     	dos_cn.write(resultByte);
//	     	dos_cn.close();
//	     	bout_cn.close();
			
		}
		catch (OutOfMemoryError e2) {
			
//			LoggerFactory.getTraceLogger().debug("FalconImg", "compress with file out of memory,OldInsampleSize: "+OldInsampleSize);
		   	
			// TODO: handle exception
		//	return null;
		}
			
			

		

    }finally{
    	if(fis_cn!=null)
    	{
    		fis_cn.close();
    	}

    	if(bout_cn!=null)
    	{
    		bout_cn.close();
    	}
    	
    	if(dos_cn!=null)
    	{
    		dos_cn.close();
    	}

    	if(bitmap_cn!=null)
    		bitmap_cn.recycle();
    	
    	if(rotateBitmap_cn!=null)
    		rotateBitmap_cn.recycle();
    	

    }
    
      	return bout_cn;
  }
//    public ByteArrayOutputStream GenerateCompressImage_new(File file, int level, int newWidth, int newHeight) throws IOException
//    {
//         // Bitmap imgmap = getDiskBitmap(ImgPath);
//    	if ((newWidth <= 0) || (newHeight <= 0) || (file == null)) {
//			return null;
//		}
//    	
//    	int []desSize = {newWidth,newHeight};
//    	
//    	int rotatedegree = dataFormat.calc_rotate(file);
//    	
//      	int imgtype = -1; //图片类型  	JPEG = 0, BMP, PNG
//      	FileInputStream fis_cn = null;
//      	ByteArrayOutputStream bout_cn=null;
//      	DataOutputStream dos_cn=null;
//      	ByteArrayOutputStream baos_cn = null;
//      	
//        //获取图像信息
//      	Bitmap bitmap_cn = null;
//      	Bitmap rotateBitmap_cn = null;
//     	
//     	try{
//     		     		       
//         fis_cn = new FileInputStream(file);  
//        
//         int fileLen= fis_cn.available();
//         
//    	// decode image size
//        BitmapFactory.Options o_cn = new BitmapFactory.Options();
//        o_cn.inJustDecodeBounds = true;
//       // o.inPreferredConfig = Bitmap.Config.RGB_565;        
//         BitmapFactory.decodeStream(fis_cn, null, o_cn);
//         fis_cn.close();
//
//         // Find the correct scale value. It should be the power of 2.
//         int []width_tmp = new int[1];
//         width_tmp[0] = o_cn.outWidth;
//         int []height_tmp = new int[1];
//         height_tmp[0] = o_cn.outHeight;
//         
//         o_cn.inJustDecodeBounds = false;
//    	 o_cn.inPurgeable = true;  
//         o_cn.inInputShareable = true;  
//         o_cn.inPreferredConfig = Bitmap.Config.RGB_565;	 
//         
//         if (fileLen/1024 < 50)//if image is small，just return the whole
//         {
//        	 
//             fis_cn = new FileInputStream(file);
//             doCallBack(width_tmp[0], height_tmp[0]);
//             
//             bitmap_cn = BitmapFactory.decodeStream(fis_cn, null,o_cn);
//        	 fis_cn.close();
//        	 
//        	 if(rotatedegree!=0)
//        	 {
//        		 Matrix matrix = new Matrix(); 
//            	 matrix.postRotate(rotatedegree);
//            	 
//            	 rotateBitmap_cn = Bitmap.createBitmap(bitmap_cn, 0, 0, bitmap_cn.getWidth(),    
//                            bitmap_cn.getHeight(), matrix, true);
//            	 
//            	 baos_cn = new ByteArrayOutputStream();
//            	 rotateBitmap_cn.compress(Bitmap.CompressFormat.JPEG, 98, baos_cn);
//            	 baos_cn.close();
//            	 
//            	 bitmap_cn.recycle();
//            	 rotateBitmap_cn.recycle();
//            	 
//            	 return baos_cn;
//        	 }
//        	
//        	 
//        	 baos_cn = new ByteArrayOutputStream();
//        	 bitmap_cn.compress(Bitmap.CompressFormat.JPEG, 98, baos_cn);
//        	 baos_cn.close();
//        	 bitmap_cn.recycle();
//        	 
//             return baos_cn;
//
//         }
//   
//         //获取图片类型
////         String reulString = "";
////         reulString = o.outMimeType;
////         imgtype = dataFormat.DataFormattoType(reulString);
//         imgtype = 0;
//         
//         //按长宽比计算结果图尺寸         
//         reCalcDesSize(width_tmp[0], height_tmp[0], desSize);
//         newWidth = desSize[0];
//         newHeight = desSize[1];
//    	 if (rotatedegree == 90 || rotatedegree == 270) {
//    		 int tempw = newWidth;
//    		 newWidth = newHeight;	        		 
//    		 newHeight = tempw;
//		}
//                              
//          //最大可缩放比例    
//    	 
//         int scaleimg = getScaleimg (width_tmp,height_tmp,newWidth,newHeight); 		
//	        // decode with inSampleSize
//	        o_cn.inSampleSize = scaleimg;
//	        
//	       // o.inPreferredConfig = Bitmap.Config.RGB_565;
//	        fis_cn = new FileInputStream(file);
//	        doCallBack(width_tmp[0],height_tmp[0]);   	        
//	        bitmap_cn = BitmapFactory.decodeStream(fis_cn, null, o_cn);
//	        fis_cn.close();	        
//	        if(rotatedegree!=0)
//	        {
//	        	 Matrix matrix = new Matrix(); 
//	        	 matrix.postRotate(rotatedegree);	        	 
//	        	 rotateBitmap_cn = Bitmap.createBitmap(bitmap_cn, 0, 0, bitmap_cn.getWidth(),    
//	                        bitmap_cn.getHeight(), matrix, true);  	     
//	        	 
//	        	 if(rotateBitmap_cn.getWidth() < newWidth && rotateBitmap_cn.getHeight() < newHeight)
//	        	 {
//	        		 newWidth = rotateBitmap_cn.getWidth();
//	        		 newHeight = rotateBitmap_cn.getHeight();
//	        	 }
//	        	 
//	        //	byte[] resultByte = JniFalconImg.CompressImageBitmapSizeDefault(rotateBitmap,newWidth, newHeight, level);
//	        	byte[] resultByte = JniFalconImg.CompressImageBitmapSizenew(rotateBitmap_cn,newWidth, newHeight, level,imgtype);
//	        	
//	  	        bitmap_cn.recycle();
//	  	        rotateBitmap_cn.recycle();
//	  	        
//	          	bout_cn=new ByteArrayOutputStream();
//	          	dos_cn=new DataOutputStream(bout_cn);
//	          	dos_cn.write(resultByte);
//	          	dos_cn.close();
//	          	bout_cn.close();
//	          	
//	          	return bout_cn;
//	        }
//	       	   
//	        
//	        if(bitmap_cn.getWidth() < newWidth && bitmap_cn.getHeight() < newHeight)
//	       	 {
//	       		 newWidth = bitmap_cn.getWidth();
//	       		 newHeight = bitmap_cn.getHeight();
//	       	 }
//	        
//	              
//	       // byte[] resultByte = JniFalconImg.CompressImageBitmapSizeDefault(bitmap,newWidth, newHeight, level);
//	        byte[] resultByte = JniFalconImg.CompressImageBitmapSizenew(bitmap_cn,newWidth, newHeight, level,imgtype);
//        	
//	        bitmap_cn.recycle();
//	        
//        	bout_cn=new ByteArrayOutputStream();
//        	dos_cn=new DataOutputStream(bout_cn);
//        	dos_cn.write(resultByte);
//        	dos_cn.close();
//        	bout_cn.close();
//        	
//        	return bout_cn;
//	       	       
//	}catch (FileNotFoundException e) {
//    }finally{
//    	if(fis_cn!=null)
//    	{
//    		fis_cn.close();
//    	}
//    	
//    	if(baos_cn!=null)
//    	{
//    		baos_cn.close();
//    	}
//
//    	
//    	if(bout_cn!=null)
//    	{
//    		bout_cn.close();
//    	}
//    	
//    	if(dos_cn!=null)
//    	{
//    		dos_cn.close();
//    	}
//
//    	if(bitmap_cn!=null)
//    		bitmap_cn.recycle();
//    	
//    	if(rotateBitmap_cn!=null)
//    		rotateBitmap_cn.recycle();
//
//    }
//    
//    
//     	return null;
//  }
   
   /***
    * zhangyu.zy
    * @param image 所需压缩图像 InputStream流   保持长宽比
    * @param level  压缩   0  1  2    推荐2高质量
    * @param newWidth  所需压缩最大宽
    * @param newHeight  所需压缩最大高
    * @return 压缩流
    * @throws IOException
    */
    
    public ByteArrayOutputStream GenerateCompressImage_new(InputStream image, int level, int newWidth, int newHeight) throws IOException
    {
         // Bitmap imgmap = getDiskBitmap(ImgPath);
    	if ((newWidth <= 0) || (newHeight <= 0) || (image == null)) {
			return null;
		}
    	  
    	return GenerateCompressImage_common(image, level, newWidth, newHeight, 0);
    
  }
    
    
    
    public ByteArrayOutputStream GenerateCompressImage_common(InputStream image, int level, int newWidth, int newHeight, int rotatedegree) throws IOException
    {
         // Bitmap imgmap = getDiskBitmap(ImgPath);
    	if ((newWidth <= 0) || (newHeight <= 0) || (image == null)) {
			return null;
		}
    	  
    	int []desSize = {newWidth,newHeight};
    	int imgtype = -1; //图片类型  	JPEG = 0, BMP, PNG
    	ByteArrayOutputStream baos_gcc = null;
    	RepeatableInputStream fisiInputStream_gcc = null;
    	DataOutputStream dos_gcc = null;
    	ByteArrayOutputStream bout_gcc = null;
    	
    	Bitmap bitmap_gcc = null;
    	Bitmap rotateBitmap_gcc = null;
    	
     	try{
     		
     		baos_gcc = dataFormat.InputStreamToByteArray(image);
     		fisiInputStream_gcc = new RepeatableInputStream(baos_gcc.toByteArray());
     		
      		// decode image size
      		BitmapFactory.Options o_gcc = new BitmapFactory.Options();
     		o_gcc.inJustDecodeBounds = true;                  
     		BitmapFactory.decodeStream(fisiInputStream_gcc, null, o_gcc);
     	//	stream1.close();
     		// Find the correct scale value. It should be the power of 2.
     		int width_tmp = o_gcc.outWidth, height_tmp = o_gcc.outHeight;
     		   					    		
     		o_gcc.inJustDecodeBounds = false;
	        o_gcc.inPurgeable = true;  
	        o_gcc.inInputShareable = true;  
	        o_gcc.inPreferredConfig = Bitmap.Config.RGB_565;	
	        o_gcc.inDither = true;
	             
     		 int fileLen= fisiInputStream_gcc.available();
     		 
     		 if(fileLen/1024 < 50)
     		 {
     			 if(rotatedegree==0)
     				 return baos_gcc;
     			 else {
					
     	             fisiInputStream_gcc.flip();
     	             doCallBack(width_tmp, height_tmp,true);
     	             
     	             bitmap_gcc = BitmapFactory.decodeStream(fisiInputStream_gcc, null,o_gcc);
     	            
     	             Matrix matrix = new Matrix(); 
     	             matrix.postRotate(rotatedegree);
              	 
     	             rotateBitmap_gcc = Bitmap.createBitmap(bitmap_gcc, 0, 0, bitmap_gcc.getWidth(),    
                              	bitmap_gcc.getHeight(), matrix, true);
              	 
     	             baos_gcc = new ByteArrayOutputStream();
     	             rotateBitmap_gcc.compress(Bitmap.CompressFormat.JPEG, 98, baos_gcc);
     	             baos_gcc.close();
              	 
     	             bitmap_gcc.recycle();
     	             rotateBitmap_gcc.recycle();
              	 
     	             return baos_gcc;
     	              
				}
     				 
     		 }
     		     		

     		imgtype = 0;
     		
            //按长宽比计算结果图尺寸         
            reCalcDesSize(width_tmp, height_tmp, desSize);
            newWidth = desSize[0];
            newHeight = desSize[1];
        
                 
     		int scaleimg = 1;
	        while (true) {
	            if (width_tmp / 2 < newWidth  && height_tmp /2 < newHeight)
	                break;
	            width_tmp /= 2;
	            height_tmp /= 2;
	            scaleimg *= 2;
	        }
	        
	        if(scaleimg!=1 && (width_tmp  < newWidth || height_tmp<newHeight))
	        {
	        	scaleimg/=2;
	        }
 		
	        // decode with inSampleSize
	        o_gcc.inSampleSize = scaleimg;


	        fisiInputStream_gcc.flip();
	        
	        doCallBack(width_tmp, height_tmp,true);
	        
	        bitmap_gcc = BitmapFactory.decodeStream(fisiInputStream_gcc, null, o_gcc);
	        fisiInputStream_gcc.close();
	        
	        
	        if(rotatedegree!=0)
	        {
	        	Matrix matrix = new Matrix(); 
	        	 matrix.postRotate(rotatedegree);
	        	 
	        	 rotateBitmap_gcc = Bitmap.createBitmap(bitmap_gcc, 0, 0, bitmap_gcc.getWidth(),    
	                        bitmap_gcc.getHeight(), matrix, true);  
	        	 
	        	 
	        	 if (rotatedegree == 90 || rotatedegree == 270) {
	        		 int tempw = newWidth;
	        		 newWidth = newHeight;	        		 
	        		 newHeight = tempw;
	        		 
				}
	        	 
	        	 if(rotateBitmap_gcc.getWidth() < newWidth && rotateBitmap_gcc.getHeight() < newHeight)
	        	 {
	        		 newWidth = rotateBitmap_gcc.getWidth();
	        		 newHeight = rotateBitmap_gcc.getHeight();
	        	 }
	        	 
	        	 
	        //	byte[] resultByte = JniFalconImg.CompressImageBitmapSizeDefault(rotateBitmap,newWidth, newHeight, level);
	        	byte[] resultByte = JniFalconImg.CompressImageBitmapSizenew(rotateBitmap_gcc,newWidth, newHeight, level,imgtype);
	        	
	  	        bitmap_gcc.recycle();
	  	        rotateBitmap_gcc.recycle();
	  	        
	          	bout_gcc=new ByteArrayOutputStream();
	          	dos_gcc=new DataOutputStream(bout_gcc);
	          	dos_gcc.write(resultByte);
	          	dos_gcc.close();
	          	bout_gcc.close();
	          	
	          	return bout_gcc;
	        }
	        
	        
	        
	     if(bitmap_gcc.getWidth() < newWidth && bitmap_gcc.getHeight() < newHeight)
       	 {
       		 newWidth = bitmap_gcc.getWidth();
       		 newHeight = bitmap_gcc.getHeight();
       	 }
	        
	      //  byte[] resultByte = JniFalconImg.CompressImageBitmapSizeDefault(bitmap,newWidth, newHeight, level);
	        byte[] resultByte = JniFalconImg.CompressImageBitmapSizenew(bitmap_gcc,newWidth, newHeight, level,imgtype);
	        bitmap_gcc.recycle();
	        
	        
        	bout_gcc=new ByteArrayOutputStream();
        	dos_gcc=new DataOutputStream(bout_gcc);
        	dos_gcc.write(resultByte);
        	dos_gcc.close();
        	bout_gcc.close();
        	
        	return bout_gcc;
	       	       
	}catch (FileNotFoundException e) {
    }finally{
    	
    	if(baos_gcc!=null)
    	{
    		baos_gcc.flush();
    		baos_gcc.close();
    	}
    	
    	if(dos_gcc!=null)
    	{
    		dos_gcc.close();
    	}
    	
    	if(fisiInputStream_gcc!=null)
    	{
    		fisiInputStream_gcc.close();
    	}
    	
    	if(bout_gcc!=null)
    	{
    		bout_gcc.close();
    	}
    	
    	if(bitmap_gcc!=null)
    		bitmap_gcc.recycle();
    	
    	if(rotateBitmap_gcc!=null)
    		rotateBitmap_gcc.recycle();
    }
    
    
     	return null;
	
    
  }
    
    public ByteArrayOutputStream GenerateCompressImage_common(InputStream image, int level, int rotatedegree) throws IOException
	{
    	
    	if ( (image == null)) {
			return null;
		}
  //  	int degree = readPictureDegree(file.getAbsolutePath());  	
     	
    	int imgtype = -1; //图片类型  	JPEG = 0, BMP, PNG
    	
    	ByteArrayOutputStream baos = null;
    	RepeatableInputStream fisiInputStream = null;
    	ByteArrayOutputStream bout = null;
    	DataOutputStream dos = null;
     	Bitmap bitmap = null;
     	Bitmap rotateBitmap = null;
     	
     	try{
     		
     		baos = dataFormat.InputStreamToByteArray(image);
     		
    		fisiInputStream = new RepeatableInputStream(baos.toByteArray());
         	// decode image size
             BitmapFactory.Options o = new BitmapFactory.Options();
             o.inJustDecodeBounds = true;
             BitmapFactory.decodeStream(fisiInputStream, null, o);
             int width_tmp = o.outWidth, height_tmp = o.outHeight;
             
            o.inJustDecodeBounds = false;
	        o.inPurgeable = true;  
	        o.inInputShareable = true;  
	        o.inDither = true;
	        o.inPreferredConfig = Bitmap.Config.RGB_565;
 		
     		if (baos.size()/1024 < 50) {
     			
     			if(rotatedegree==0)
     				return baos;
     			else {
     				
     	           //  
     	             
					fisiInputStream.flip();
					doCallBack(width_tmp, height_tmp,true);
					
					bitmap = BitmapFactory.decodeStream(fisiInputStream, null,o);
					
					 Matrix matrix = new Matrix(); 
	            	 matrix.postRotate(rotatedegree);
	            	 
	            	 rotateBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),    
	                            bitmap.getHeight(), matrix, true);
	            	 
	            	 baos = new ByteArrayOutputStream();
	            	 rotateBitmap.compress(Bitmap.CompressFormat.JPEG, 98, baos);
	            	 baos.close();
	            	 
	            	 bitmap.recycle();
	            	 rotateBitmap.recycle();
	            	 
	            	 return baos;
				}
			
     		}
 		
 
         	
     		//获取图片类型
//            String reulString = "";
//            reulString = o.outMimeType;
//            imgtype = dataFormat.DataFormattoType(reulString);
            imgtype = 0;
                 
         	int scaleimg = 1;
	        while (true) {
	            if (width_tmp / 2 < maxLen  && height_tmp /2 < maxLen)
	                break;
	            width_tmp /= 2;
	            height_tmp /= 2;
	            scaleimg *= 2;
	        }
 	        
	        if(scaleimg!=1 && (width_tmp  < maxLen && height_tmp< maxLen))
	        {
	        	scaleimg/=2;
	        }
 		
	        // decode with inSampleSize
	        o.inSampleSize = scaleimg;


	        fisiInputStream.flip();
	        
	        doCallBack(width_tmp, height_tmp,true);
	        
	        bitmap = BitmapFactory.decodeStream(fisiInputStream, null, o);

	        fisiInputStream.close();
	        
	        
	        if(rotatedegree!=0)
	        {
	        	Matrix matrix = new Matrix(); 
	        	 matrix.postRotate(rotatedegree);
	        	 
	        	 rotateBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),    
	                        bitmap.getHeight(), matrix, true);  
	        	 
	        	 
	        	//byte[] resultByte = JniFalconImg.CompressImageBitmapDefault(rotateBitmap, level);
	        	 byte[] resultByte = JniFalconImg.CompressImageBitmapDefaultnew(rotateBitmap, level,imgtype);
	          	
	  	        bitmap.recycle();
	  	        rotateBitmap.recycle();
	  	        
	          	bout=new ByteArrayOutputStream();
	          	dos=new DataOutputStream(bout);
	          	dos.write(resultByte);
	          	dos.close();
	          	bout.close();
	          	
	          	return bout;
	        }
	        
	        
	    //  byte[] resultByte = JniFalconImg.CompressImageBitmapDefault(bitmap, level);
	        byte[] resultByte = JniFalconImg.CompressImageBitmapDefaultnew(bitmap, level,imgtype);
	      
        	
	        bitmap.recycle();
	        
        	bout=new ByteArrayOutputStream();
        	dos=new DataOutputStream(bout);
        	dos.write(resultByte);
        	dos.close();
        	bout.close();
        	
        	return bout;
	       	       
	}catch (FileNotFoundException e) {
    }finally{
    	if(baos!=null)
    	{
    		baos.flush();
    		baos.close();
    	}
    	
    	if(fisiInputStream!=null)
    	{
    		fisiInputStream.close();
    	}
    	
    	if(dos!=null)
    	{
    		dos.close();
    	}
    	
    	if(bout!=null)
    	{
    		bout.close();
    	}
    	
    	if(bitmap!=null)
    		bitmap.recycle();
    	
    	if(rotateBitmap!=null)
    		rotateBitmap.recycle();
    }
    
    
     	return null;
	}
    
    
    /***
     * zhangyu.zy
     * @param image 待压缩文件InputStream
     * @param level  压缩水平   0  1  2   推荐使用  2  高水平
     * @return  返回压缩流   默认输出图像 最大边 1280  同微信一致
     * @throws IOException
     */
    
    public ByteArrayOutputStream GenerateCompressImage_new(InputStream image, int level) throws IOException
	{
    	
    	if ( (image == null)) {
			return null;
		}
    	
    	return GenerateCompressImage_common(image, level, 0);
	}
    
    /***
     * zhangyu.zy
     * @param srcWidth
     * @param srcHeight
     * @param desSize
     * @return
     */
    
    private boolean reCalcDesSize(int srcWidth, int srcHeight, int[] desSize)
    {
    	if(desSize[0]>maxLen)
    		desSize[0] = maxLen;
    	
    	if(desSize[1]>maxLen)
    		desSize[1]= maxLen;
    	
    	if(desSize[0]<20)
    		desSize[0] = 20;
    	
    	if(desSize[1]<20)
    		desSize[1]= 20;
    	
    	
      	float scaleWidth = (float)srcWidth / (float)desSize[0];
     	float scaleHeight = (float)srcHeight / (float)desSize[1];
     	
     	int dwidth,dheight;

     	if(scaleWidth > scaleHeight)
     	{
     		dwidth = desSize[0];
     		dheight = ((dwidth*srcHeight)/srcWidth);
     	}
     	else 
     	{
     		dheight = desSize[1];
     		dwidth = ((dheight*srcWidth)/srcHeight);
     	}
     	
     	desSize[0] = dwidth;
     	desSize[1] = dheight;
    	
    	return true;
    }
    

    
    private int getScaleimg (int[] width_tmp,int[] height_tmp,int newWidth,int newHeight )
    {
    	int scaleimg = 1;
        while (true) {
            if (width_tmp[0] / 2 < newWidth  && height_tmp[0] /2 < newHeight)
                break;
            width_tmp[0] /= 2;
            height_tmp[0] /= 2;
            scaleimg *= 2;
        }
	        
        if(scaleimg!=1 && (width_tmp[0]  < newWidth && height_tmp[0]< newHeight))
        {
        	scaleimg/=2;
        }
        return scaleimg;
    }
    
    
//    private void saveSourceImage(Bitmap bitmap){
//  	 // ConfigService configService =  mMicroApplicationContext.getExtServiceByInterface(ConfigService.class.getName());
//
//	//	  }
//	      FileOutputStream fOut = null;  
//	      try {
//
//				fOut = new FileOutputStream("/sdcard/result/img.jpg");
//				bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fOut);
//				fOut.close();
////				File file =  new File(UtilApp.completePicPathAndName(getBaseContext()));
////				while (file.length() >= max_size) {
////					int width = (int)(bitmap.getWidth()*factor);
////					int height = (int)(bitmap.getHeight()*factor);
////					if (file.length()/max_size >= 2) {
////						width = (int)(bitmap.getWidth()*0.5f);
////						height = (int)(bitmap.getHeight()*0.5f);
////					}
////					Bitmap scaleBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
////				    if (scaleBitmap != bitmap && !bitmap.isRecycled()) {
////				    	bitmap.recycle(); 
////				    	bitmap = null;
////				    }
////				    UtilApp.e("IdCardPhotoSaveImage", "IdCardPhotoSaveImage With Resize " + scaleBitmap.getWidth() + "  " + scaleBitmap.getHeight());
////				    bitmap = scaleBitmap;
////				    UtilApp.WriteFileToSD("crop", bitmap);	
////					fOut = new FileOutputStream(UtilApp.completePicPathAndName(getBaseContext()));
////					bitmap.compress(Bitmap.CompressFormat.JPEG, UtilApp.resize_quantity, fOut);
////					fOut.close();
//				  //  file = new File(UtilApp.completePicPathAndName(getBaseContext()));
////				}
//		  } catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}finally{
//			  
//		  }
//  }

}
