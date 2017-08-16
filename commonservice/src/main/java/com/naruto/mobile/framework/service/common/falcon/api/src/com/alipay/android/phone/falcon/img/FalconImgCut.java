package com.naruto.mobile.framework.service.common.falcon.api.src.com.alipay.android.phone.falcon.img;


import java.io.*;



import android.R.bool;
import android.R.integer;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.graphics.Matrix;


import android.text.TextUtils;
import android.util.Log;


public class FalconImgCut {

    private int maxLen = 400;
    private int minLen = 200;
    private float scale = 0.5f;
    private CutCallBack callBack = null;
    private boolean bDebug = true;
    private int maxHeight = 50000;
       
    
    private int judgeBeyondRatio(int width, int height, float minScale) {
        float scale = ((float) width) / ((float) height);
        if (scale < minScale) {
            return 2;//SUPERHEIGHT;
        } else if (scale > (1.0f / minScale)) {
            return 1;//SUPERWIDTH;
        } else {
            return 0;//NORMAL;
        }
    }


    public void registeCallBack(CutCallBack cb) {
        callBack = cb;
    }

    public void unregisteCallBack() {
        callBack = null;
    }

//	public void doCallBack(int width, int height) {
//		
//		 if(callBack != null){
//	   		  long size = width*height*5;
//	   		  callBack.onCalcMemSize(size);
//	   		  if(bDebug && size > 1024*1024*2 ){
//	   			  
//	   			  Log.i("BITMAP_SIZE", "tmp_w="+width+";tmp_h="+height);
//	   			      			  
//	   		  }
//	   	  	}
//		
//	}

    public void doCallBackForce(int width, int height, boolean ForceRelease) {

        if (callBack != null) {
            long size = width * height * 5;
            callBack.onCalcMemSize(size, ForceRelease);
            if (bDebug && size > 1024 * 1024 * 2) {

                falconImgLog("tmp_w=" + width + ";tmp_h=" + height);
            }
        }

    }


    /**
     * 输入图片大小和frame大小，返回切割后图片的大小
     *
     * @param width  图片宽度
     * @param height 图片高度
     * @param maxLen frame最大值
     * @param desLen 返回的宽高，desLen[0]为宽，desLen[1]为高
     */
    public void calcultDesWidthHeight(int width, int height, int maxLen, int[] desLen) {
        if ((width > 0) && (height > 0) && (maxLen > 0)) {
            int minLen = maxLen / 2;
            float scale = 0.5f;
            JniFalconImg.calcultDesWidthHeight(width, height, maxLen, minLen, scale, desLen);
        }

    }


    //////////////////////zy////////////////////////////////////

    /***
     * 根据输入最大宽高、图像角度、scale 给出缩略图
     *
     * @param image
     * @param maxLen
     * @param minLen
     * @param scale
     * @param rotatedegree
     * @return
     * @throws IOException
     */
    public Bitmap cutImage_common(InputStream image, int maxLen, int minLen, float scale, int rotatedegree) throws IOException {
        if (image == null) {
            return null;
        }

		minLen = (int)(maxLen*scale);
		 
        Bitmap resizedBitmap = null;
        int desWidth = 0;
        int desHeight = 0;

        ByteArrayOutputStream baos = null;
        RepeatableInputStream fis = null;

        try {


            baos = dataFormat.InputStreamToByteArray(image);


            Bitmap bitmap = null;
            // decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;

            //InputStream fis = new ByteArrayInputStream(baos.toByteArray());;
            fis = new RepeatableInputStream(baos.toByteArray());
            BitmapFactory.decodeStream(fis, null, o);
            //	fis.close();

            // Find the correct scale value. It should be the power of 2.
            int width_tmp = o.outWidth, height_tmp = o.outHeight;

            int imgtype = judgeBeyondRatio(width_tmp, height_tmp, scale);
            int scaleimg = 1;

            Matrix matrix = new Matrix();

// 		 o.inJustDecodeBounds = false;
// 	     o.inPurgeable = true;  
// 	     o.inInputShareable = true;  

// 		if(o.outMimeType.isEmpty())
// 			return null;

//            if (TextUtils.isEmpty(o.outMimeType))
//                return null;
//
//            int datatype = dataFormat.DataFormattoType(o.outMimeType);

// 		if(datatype==2)
// 		{
// 			o.inPreferredConfig = Bitmap.Config.ARGB_8888;	 
// 		}
// 		else {
// 			o.inPreferredConfig = Bitmap.Config.RGB_565;	 
//		}
// 		
            setBitmapFactory(o, 2);

            switch (imgtype) {
                //正常
                case 0:
                    float scaleWidth = (float) width_tmp / (float) maxLen;
                    float scaleHeight = (float) height_tmp / (float) maxLen;


                    if (scaleWidth > scaleHeight) {
                        desWidth = maxLen;
                        desHeight = (int) (((float) desWidth * (float) height_tmp) / (float) width_tmp);
                    } else {
                        desHeight = maxLen;
                        desWidth = (int) (((float) desHeight * (float) width_tmp) / (float) height_tmp);
                    }

                    scaleimg = 1;
                    while (true) {
                        if (width_tmp / 2 < desWidth)
                            break;
                        width_tmp /= 2;
                        height_tmp /= 2;
                        scaleimg *= 2;
                    }

                    if (scaleimg != 1 && width_tmp < desWidth) {
                        scaleimg /= 2;
                    }


                    o.inSampleSize = scaleimg;

                    fis.flip();
                    //////////////callback////////////////
                    doCallBackForce(width_tmp, height_tmp, true);

                    bitmap = BitmapFactory.decodeStream(fis, null, o);
//                    fis.close();

                    scaleWidth = ((float) desWidth) / bitmap.getWidth();

                    matrix.postScale(scaleWidth, scaleWidth);
                    // if you want to rotate the Bitmap
                    matrix.postRotate(rotatedegree);
                    resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                            bitmap.getHeight(), matrix, true);


                    break;
                //超宽
                case 1:
                    scaleimg = 1;
                    while (true) {
                        if (height_tmp / 2 < maxLen * scale)
                            break;
                        width_tmp /= 2;
                        height_tmp /= 2;
                        scaleimg *= 2;
                    }

                    if (scaleimg != 1 && height_tmp < maxLen * scale) {
                        scaleimg /= 2;
                    }

                    // decode with inSampleSize
                    o.inSampleSize = scaleimg;

                    fis.flip();

                    doCallBackForce(width_tmp, height_tmp, true);

                    bitmap = BitmapFactory.decodeStream(fis, null, o);
//                    fis.close();
                    int useWidth = (int) ((float) bitmap.getHeight() / scale);

                    //cutImageData = cut(imageData, width, height, nchannel, startX, startY, useWidth, useHeight);
                    scaleWidth = ((float) maxLen) / useWidth;

                    //////////////
                    desWidth = maxLen;
                    desHeight = minLen;
                    ///////////

                    matrix.postScale(scaleWidth, scaleWidth);
                    matrix.postRotate(rotatedegree);

                    resizedBitmap = Bitmap.createBitmap(bitmap, (bitmap.getWidth() - useWidth) / 2, 0, useWidth, bitmap.getHeight(), matrix, true);

                    break;

                //超长
                case 2:
                    scaleimg = 1;
                    while (true) {
                        if (width_tmp / 2 < maxLen * scale)
                            break;
                        width_tmp /= 2;
                        height_tmp /= 2;
                        scaleimg *= 2;
                    }

                    if (scaleimg != 1 && width_tmp < maxLen * scale) {
                        scaleimg /= 2;
                    }

                    // decode with inSampleSize

                    o.inSampleSize = scaleimg;


                    fis.flip();

                    doCallBackForce(width_tmp, height_tmp, true);

                    bitmap = BitmapFactory.decodeStream(fis, null, o);
//                    fis.close();
                    int useHeight = (int) ((float) bitmap.getWidth() / scale);

                    ////////////////
                    desWidth = minLen;
                    desHeight = maxLen;
                    //////////////////

                    scaleWidth = ((float) maxLen) / useHeight;
                    matrix.postScale(scaleWidth, scaleWidth);
                    matrix.postRotate(rotatedegree);
                    resizedBitmap = Bitmap.createBitmap(bitmap, 0, (bitmap.getHeight() - useHeight) / 2, bitmap.getWidth(), useHeight, matrix, true);

                default:
                    break;
            }

            //bitmap.recycle();
            // fis.close();

        } catch (FileNotFoundException e) {
        } finally {
            if (baos != null) {
                baos.flush();
                closeQuietly(baos);
            }
            closeQuietly(fis);
        }

        if (resizedBitmap == null)
            return null;
        /////////////////防止相差一两个像素//////////////////
        if (rotatedegree == 90 || rotatedegree == 270) {
            int temp_w_h = desWidth;
            desWidth = desHeight;
            desHeight = temp_w_h;
        }


        if (resizedBitmap.getWidth() == desWidth && resizedBitmap.getHeight() == desHeight) {

            return resizedBitmap;
        }

        Bitmap dst = Bitmap.createScaledBitmap(resizedBitmap, desWidth, desHeight, false);
        if (dst != resizedBitmap) {

            resizedBitmap.recycle();
        }


        return dst;
    }


    /***
     * zhangyu.zy
     *
     * @param file   输入图像File
     * @param maxLen 裁剪最大边长度
     * @param minLen 裁剪最小长度   0.5*maxlen
     * @param scale  0.5
     * @return 裁剪结果 bitmap
     * @throws IOException
     */
    public Bitmap cutImage_new(File file, int maxLen, int minLen, float scale) throws IOException {
        if (file == null) {
            return null;
        }
		
		minLen = (int)(maxLen*scale);

        Bitmap resizedBitmap_n = null;
        int desWidth_n = 0;
        int desHeight_n = 0;

        /////////////new//////
        int rotatedegree = dataFormat.calc_rotate(file);
   //     FileInputStream fis_n = null;

        try {


            Bitmap bitmap_n = null;
            // decode image size
            BitmapFactory.Options o_n = new BitmapFactory.Options();
            o_n.inJustDecodeBounds = true;

//            fis_n = new FileInputStream(file);
//            BitmapFactory.decodeStream(fis_n, null, o_n);
//            fis_n.close();
            BitmapFactory.decodeFile(file.getAbsolutePath(), o_n);

            // Find the correct scale value. It should be the power of 2.
            int width_tmp_n = o_n.outWidth, height_tmp_n = o_n.outHeight;

//        if(o_n.outMimeType.isEmpty())
//        	return null;

//            if (TextUtils.isEmpty(o_n.outMimeType))
//                return null;

//            int datatye = dataFormat.DataFormattoType(o_n.outMimeType);

            setBitmapFactory(o_n, 2);

            int imgtype = judgeBeyondRatio(width_tmp_n, height_tmp_n, scale);
            int scaleimg = 1;


            Matrix matrix = new Matrix();

            switch (imgtype) {
                //正常
                case 0:
                    float scaleWidth = (float) width_tmp_n / (float) maxLen;
                    float scaleHeight = (float) height_tmp_n / (float) maxLen;

                    if (scaleWidth > scaleHeight) {
                        desWidth_n = maxLen;
                        desHeight_n = (int) (((float) desWidth_n * (float) height_tmp_n) / (float) width_tmp_n);
                    } else {
                        desHeight_n = maxLen;
                        desWidth_n = (int) (((float) desHeight_n * (float) width_tmp_n) / (float) height_tmp_n);
                    }

                    scaleimg = 1;
                    while (true) {
                        if (width_tmp_n / 2 < desWidth_n)
                            break;
                        width_tmp_n /= 2;
                        height_tmp_n /= 2;
                        scaleimg *= 2;
                    }

                    if (scaleimg != 1 && width_tmp_n < desWidth_n) {
                        scaleimg /= 2;
                    }

                    // decode with inSampleSize
                    o_n.inSampleSize = scaleimg;

                //    fis_n = new FileInputStream(file);
                    doCallBackForce(width_tmp_n, height_tmp_n, true);


                    //     Log.i("zyinfo","start decode");

                    //   bitmap_n = BitmapFactory.decodeStream(fis_n, null, o_n);
//                    bitmap_n = BitmapFactory.decodeFileDescriptor(fis_n.getFD(), null, o_n);
                    //    Log.i("zyinfo","end decode");
                    bitmap_n = BitmapFactory.decodeFile(file.getAbsolutePath(), o_n);

//                    fis_n.close();
                    scaleWidth = ((float) desWidth_n) / bitmap_n.getWidth();

                    matrix.postScale(scaleWidth, scaleWidth);

                    // if you want to rotate the Bitmap
                    matrix.postRotate(rotatedegree);

                    resizedBitmap_n = Bitmap.createBitmap(bitmap_n, 0, 0, bitmap_n.getWidth(),
                            bitmap_n.getHeight(), matrix, true);

                    break;
                //超宽
                case 1:
                    scaleimg = 1;
                    while (true) {
                        if (height_tmp_n / 2 < maxLen * scale)
                            break;
                        width_tmp_n /= 2;
                        height_tmp_n /= 2;
                        scaleimg *= 2;
                    }

                    if (scaleimg != 1 && height_tmp_n < maxLen * scale) {
                        scaleimg /= 2;
                    }

                    // decode with inSampleSize

                    o_n.inSampleSize = scaleimg;

//                    fis_n = new FileInputStream(file);

                    doCallBackForce(width_tmp_n, height_tmp_n, true);

                    //    bitmap_n = BitmapFactory.decodeStream(fis_n, null, o_n);
//                    bitmap_n = BitmapFactory.decodeFileDescriptor(fis_n.getFD(), null, o_n);
                    bitmap_n = BitmapFactory.decodeFile(file.getAbsolutePath(), o_n);

//                    fis_n.close();
                    int useWidth = (int) ((float) bitmap_n.getHeight() / scale);

                    ////////////////////////
                    desWidth_n = maxLen;
                    desHeight_n = minLen;
                    /////////////////////

                    //cutImageData = cut(imageData, width, height, nchannel, startX, startY, useWidth, useHeight);
                    scaleWidth = ((float) maxLen) / useWidth;
                    matrix.postScale(scaleWidth, scaleWidth);

                    matrix.postRotate(rotatedegree);

                    resizedBitmap_n = Bitmap.createBitmap(bitmap_n, (bitmap_n.getWidth() - useWidth) / 2, 0, useWidth, bitmap_n.getHeight(), matrix, true);

                    break;

                //超长
                case 2:
                    scaleimg = 1;
                    while (true) {
                        if (width_tmp_n / 2 < maxLen * scale)
                            break;
                        width_tmp_n /= 2;
                        height_tmp_n /= 2;
                        scaleimg *= 2;
                    }

                    if (scaleimg != 1 && width_tmp_n < maxLen * scale) {
                        scaleimg /= 2;
                    }

                    // decode with inSampleSize
                    o_n.inSampleSize = scaleimg;


//                    fis_n = new FileInputStream(file);
                    doCallBackForce(width_tmp_n, height_tmp_n, true);

                    //   bitmap_n = BitmapFactory.decodeStream(fis_n, null, o_n);

//                    bitmap_n = BitmapFactory.decodeFileDescriptor(fis_n.getFD(), null, o_n);
                    bitmap_n = BitmapFactory.decodeFile(file.getAbsolutePath(), o_n);

//                    fis_n.close();
                    int useHeight = (int) ((float) bitmap_n.getWidth() / scale);

                    scaleWidth = ((float) maxLen) / useHeight;
                    matrix.postScale(scaleWidth, scaleWidth);

                    ////////////////////////
                    desWidth_n = minLen;
                    desHeight_n = maxLen;
                    /////////////////////

                    matrix.postRotate(rotatedegree);

                    resizedBitmap_n = Bitmap.createBitmap(bitmap_n, 0, (bitmap_n.getHeight() - useHeight) / 2, bitmap_n.getWidth(), useHeight, matrix, true);

                default:
                    break;
            }

            //bitmap.recycle();
            //  fis.close();

        } finally {

//            if (fis_n != null) {
//                fis_n.close();
//                fis_n = null;
//            }
        }

        /////////////////防止相差一两个像素//////////////////

        if (rotatedegree == 90 || rotatedegree == 270) {
            int temp_w_h = desWidth_n;
            desWidth_n = desHeight_n;
            desHeight_n = temp_w_h;
        }

        if (resizedBitmap_n == null) {
            return null;
        }

        if (resizedBitmap_n.getWidth() == desWidth_n && resizedBitmap_n.getHeight() == desHeight_n) {

            return resizedBitmap_n;
        }

        Bitmap dst = Bitmap.createScaledBitmap(resizedBitmap_n, desWidth_n, desHeight_n, false);
        if (dst != resizedBitmap_n) {

            resizedBitmap_n.recycle();
        }

        return dst;

    }

    /***
     * zhangyu.zy
     *
     * @param image  待裁剪图像的InputStream
     * @param maxLen 裁剪结果图的最大边长度
     * @param minLen 裁剪结果图的最小边长度  0.5*maxlen
     * @param scale  0.5
     * @return 裁剪结果bitmap
     * @throws IOException
     */
    public Bitmap cutImage_new(InputStream image, int maxLen, int minLen, float scale) throws IOException {
        if (image == null)
            return null;

        int rotatedegree = 0;

        return cutImage_common(image, maxLen, minLen, scale, rotatedegree);
    }

    /***
     * zhangyu.zy
     *
     * @param image 输入图像 InputStream流  使用默认的 maxlen minlen scale   400  200  0.5
     * @return 裁剪结果bitmap
     * @throws IOException
     */

    public Bitmap cutImage_new(InputStream image) throws IOException {
        if (image == null) {

            //	Log.i("zyinfo","image null");

            return null;
        }
        return cutImage_new(image, maxLen, minLen, scale);
    }


    /***
     * zhangyu.zy
     *
     * @param file 需要裁剪图像File   使用设定的maxlen  minlen  scale  400  200  0.5
     * @return 裁剪结果bitmap
     * @throws IOException
     */
    public Bitmap cutImage_new(File file) throws IOException {
        if (file == null) {
            //	Log.i("zyinfo","file null");
            return null;
        }

        return cutImage_new(file, maxLen, minLen, scale);
    }
    
    //增加超高图 原图显示逻辑
    public Bitmap cutImage_keepRatio_common_setColor(InputStream image, int newWidth, int newHeight, int rotatedegree,boolean isARGB8888) throws IOException {

        if (image == null || newWidth <= 0 || newHeight <= 0) {
            return null;
        }

        Bitmap resizedBitmap_k = null;
        int desWidth_k = 0;
        int desHeight_k = 0;

        if (rotatedegree == 90 || rotatedegree == 270) {
            desWidth_k = newHeight;
            desHeight_k = newWidth;

        } else {
            desWidth_k = newWidth;
            desHeight_k = newHeight;

        }

        ByteArrayOutputStream baos_k = null;
        RepeatableInputStream fis_k = null;

        try {

            baos_k = dataFormat.InputStreamToByteArray(image);

            Bitmap bitmap = null;
            // decode image size
            BitmapFactory.Options o_k = new BitmapFactory.Options();
            o_k.inJustDecodeBounds = true;

            fis_k = new RepeatableInputStream(baos_k.toByteArray());
            BitmapFactory.decodeStream(fis_k, null, o_k);
// 		fis.close();

            int datatype = dataFormat.DataFormattoType(o_k.outMimeType);
            if (datatype < 0)
                return null;

            setBitmapFactory(o_k, datatype);
            
            if(isARGB8888)
            {
            	o_k.inPreferredConfig = Bitmap.Config.ARGB_8888;	
            }else {
            	o_k.inPreferredConfig = Bitmap.Config.RGB_565;	
			}

            // Find the correct scale value. It should be the power of 2.
            int width_tmp = o_k.outWidth, height_tmp = o_k.outHeight;


            //判断长宽与目标大小的关系
            if (width_tmp <= desWidth_k && height_tmp <= desHeight_k) {
                //直接解压返回
                doCallBackForce(width_tmp, height_tmp, true);


                fis_k.flip();
                bitmap = BitmapFactory.decodeStream(fis_k, null, o_k);
//                fis_k.close();

                //旋转
                if (rotatedegree != 0) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(rotatedegree); //rotate

                    resizedBitmap_k = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                            bitmap.getHeight(), matrix, true);

                    return resizedBitmap_k;

                }

                return bitmap;

            } else {
            	
            	int[] OriImgSize = new int[2];
            	int[] DesImgWidth = new int[1];
            	int[] DesImgHeight = new int[1];
            	
            	DesImgWidth[0] = desWidth_k;
            	DesImgHeight[0] = desHeight_k;
            	
            	OriImgSize[0] = width_tmp;
            	OriImgSize[1] = height_tmp;
            	
            	
            	//计算目标图像大小
            	o_k.inSampleSize = CalcInSampleSize(OriImgSize, DesImgWidth, DesImgHeight, rotatedegree);
            	

                Matrix matrix = new Matrix();

                if (o_k.inSampleSize != 0) {

                    doCallBackForce(o_k.outWidth / o_k.inSampleSize, o_k.outHeight / o_k.inSampleSize, true);
                }
                else {
                	doCallBackForce(o_k.outWidth, o_k.outHeight, true);
				}

                fis_k.flip();
                bitmap = BitmapFactory.decodeStream(fis_k, null, o_k);
//                fis_k.close();

                //float scaleWidth = ((float) DesImgWidth[0]) / bitmap.getWidth();
                float scaleWidth = 1;
                matrix.postScale(scaleWidth, scaleWidth);    //scale
                matrix.postRotate(rotatedegree);

                resizedBitmap_k = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                        bitmap.getHeight(), matrix, true);


            }


        } catch (FileNotFoundException e) {
        } finally {
            if (baos_k != null) {
                baos_k.flush();
//                baos_k.close();
                closeQuietly(baos_k);
            }

//            if (fis_k != null) {
//                fis_k.close();
//            }
            closeQuietly(fis_k);

        }


        return resizedBitmap_k;
    }

    //增加超高图 原图显示逻辑
    public Bitmap cutImage_keepRatio_common(InputStream image, int newWidth, int newHeight, int rotatedegree) throws IOException {

        if (image == null || newWidth <= 0 || newHeight <= 0) {
            return null;
        }

        Bitmap resizedBitmap_k = null;
        int desWidth_k = 0;
        int desHeight_k = 0;

        if (rotatedegree == 90 || rotatedegree == 270) {
            desWidth_k = newHeight;
            desHeight_k = newWidth;

        } else {
            desWidth_k = newWidth;
            desHeight_k = newHeight;

        }

        ByteArrayOutputStream baos_k = null;
        RepeatableInputStream fis_k = null;

        try {

            baos_k = dataFormat.InputStreamToByteArray(image);

            Bitmap bitmap = null;
            // decode image size
            BitmapFactory.Options o_k = new BitmapFactory.Options();
            o_k.inJustDecodeBounds = true;

            fis_k = new RepeatableInputStream(baos_k.toByteArray());
            BitmapFactory.decodeStream(fis_k, null, o_k);
// 		fis.close();

            int datatype = dataFormat.DataFormattoType(o_k.outMimeType);
            if (datatype < 0)
                return null;

            setBitmapFactory(o_k, datatype);

            // Find the correct scale value. It should be the power of 2.
            int width_tmp = o_k.outWidth, height_tmp = o_k.outHeight;


            //判断长宽与目标大小的关系
            if (width_tmp <= desWidth_k && height_tmp <= desHeight_k) {
                //直接解压返回
                doCallBackForce(width_tmp, height_tmp, true);


                fis_k.flip();
                bitmap = BitmapFactory.decodeStream(fis_k, null, o_k);
//                fis_k.close();

                //旋转
                if (rotatedegree != 0) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(rotatedegree); //rotate

                    resizedBitmap_k = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                            bitmap.getHeight(), matrix, true);

                    return resizedBitmap_k;

                }

                return bitmap;

            } else {
            	
            	int[] OriImgSize = new int[2];
            	int[] DesImgWidth = new int[1];
            	int[] DesImgHeight = new int[1];
            	
            	DesImgWidth[0] = desWidth_k;
            	DesImgHeight[0] = desHeight_k;
            	
            	OriImgSize[0] = width_tmp;
            	OriImgSize[1] = height_tmp;
            	
            	
            	//计算目标图像大小
            	o_k.inSampleSize = CalcInSampleSize(OriImgSize, DesImgWidth, DesImgHeight, rotatedegree);
            	

                Matrix matrix = new Matrix();

                if (o_k.inSampleSize != 0) {

                    doCallBackForce(o_k.outWidth / o_k.inSampleSize, o_k.outHeight / o_k.inSampleSize, true);
                }
                else {
                	doCallBackForce(o_k.outWidth, o_k.outHeight, true);
				}

                if(o_k.outHeight >=10000 || o_k.outWidth >= 10000)
                {
                	o_k.inPreferredConfig = Bitmap.Config.RGB_565;	 
                }
                /////////////////////
                
                fis_k.flip();
                bitmap = BitmapFactory.decodeStream(fis_k, null, o_k);
//                fis_k.close();

                //float scaleWidth = ((float) DesImgWidth[0]) / bitmap.getWidth();
                float scaleWidth = 1;
                matrix.postScale(scaleWidth, scaleWidth);    //scale
                matrix.postRotate(rotatedegree);

                resizedBitmap_k = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                        bitmap.getHeight(), matrix, true);


            }


        } catch (FileNotFoundException e) {
        } finally {
            if (baos_k != null) {
                baos_k.flush();
//                baos_k.close();
                closeQuietly(baos_k);
            }

//            if (fis_k != null) {
//                fis_k.close();
//            }
            closeQuietly(fis_k);

        }


        return resizedBitmap_k;
    }

    
    ///////////////////////////////////////////////////////////////////////////////
    //未考虑超高图原图显示需求  9.0线上版本
//    public Bitmap cutImage_keepRatio_common(InputStream image, int newWidth, int newHeight, int rotatedegree) throws IOException {
//
//        if (image == null || newWidth <= 0 || newHeight <= 0) {
//            return null;
//        }
//
//        Bitmap resizedBitmap_k = null;
//        int desWidth_k = 0;
//        int desHeight_k = 0;
//
//        if (rotatedegree == 90 || rotatedegree == 270) {
//            desWidth_k = newHeight;
//            desHeight_k = newWidth;
//
//        } else {
//            desWidth_k = newWidth;
//            desHeight_k = newHeight;
//
//        }
//
//        ByteArrayOutputStream baos_k = null;
//        RepeatableInputStream fis_k = null;
//
//        try {
//
//            baos_k = dataFormat.InputStreamToByteArray(image);
//
//            Bitmap bitmap = null;
//            // decode image size
//            BitmapFactory.Options o_k = new BitmapFactory.Options();
//            o_k.inJustDecodeBounds = true;
//
//            fis_k = new RepeatableInputStream(baos_k.toByteArray());
//            BitmapFactory.decodeStream(fis_k, null, o_k);
//// 		fis.close();
//
//            int datatype = dataFormat.DataFormattoType(o_k.outMimeType);
//            if (datatype < 0)
//                return null;
//
//            setBitmapFactory(o_k, datatype);
//
//            // Find the correct scale value. It should be the power of 2.
//            int width_tmp = o_k.outWidth, height_tmp = o_k.outHeight;
//
//
//            //判断长宽与目标大小的关系
//            if (width_tmp <= desWidth_k && height_tmp <= desHeight_k) {
//                //直接解压返回
//                doCallBackForce(width_tmp, height_tmp, true);
//
//
//                fis_k.flip();
//                bitmap = BitmapFactory.decodeStream(fis_k, null, o_k);
////                fis_k.close();
//
//                //旋转
//                if (rotatedegree != 0) {
//                    Matrix matrix = new Matrix();
//                    matrix.postRotate(rotatedegree); //rotate
//
//                    resizedBitmap_k = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
//                            bitmap.getHeight(), matrix, true);
//
//                    return resizedBitmap_k;
//
//                }
//
//                return bitmap;
//
//            } else {
//                float scale_w_h = ((float) desWidth_k) / desHeight_k;
//                float scale_ori = ((float) width_tmp) / height_tmp;
//
//                if (scale_ori > scale_w_h) {
//                    //超宽  按宽缩放
//                    desHeight_k = (int) ((((float) desWidth_k) / width_tmp) * height_tmp);
//                } else if (scale_ori < scale_w_h) {
//                    //超长  按高缩放
//                    desWidth_k = (int) ((((float) desHeight_k) / height_tmp) * width_tmp);
//                }
//
//                int scaleimg = 1;
//
//                Matrix matrix = new Matrix();
//
//                scaleimg = 1;
//                while (true) {
//                    if (width_tmp / 2 < desWidth_k)
//                        break;
//                    width_tmp /= 2;
//                    height_tmp /= 2;
//                    scaleimg *= 2;
//                }
//
//                if (scaleimg != 1 && width_tmp < desWidth_k) {
//                    scaleimg /= 2;
//                }
//
//                doCallBackForce(width_tmp, height_tmp, true);
//
//                // decode with inSampleSize
//                o_k.inSampleSize = scaleimg;
//
//
//                //   fis = new ByteArrayInputStream(baos.toByteArray());
//                fis_k.flip();
//                bitmap = BitmapFactory.decodeStream(fis_k, null, o_k);
////                fis_k.close();
//
//                float scaleWidth = ((float) desWidth_k) / bitmap.getWidth();
//
//                matrix.postScale(scaleWidth, scaleWidth);    //scale
//                matrix.postRotate(rotatedegree);
//
//                resizedBitmap_k = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
//                        bitmap.getHeight(), matrix, true);
//
//
//            }
//
//
//        } catch (FileNotFoundException e) {
//        } finally {
//            if (baos_k != null) {
//                baos_k.flush();
////                baos_k.close();
//                closeQuietly(baos_k);
//            }
//
////            if (fis_k != null) {
////                fis_k.close();
////            }
//            closeQuietly(fis_k);
//
//        }
//
//
//        return resizedBitmap_k;
//    }


    /***
     * zhangyu.zy  查看原图显示  保持长宽比 不裁剪
     *
     * @param file
     * @param newWidth
     * @param newHeight
     * @return
     * @throws IOException
     */

//public Bitmap cutImage_keepRatio(File file,int newWidth, int newHeight) throws IOException
//{
//
//	if (file == null || newWidth<= 0 || newHeight<= 0) {
//		return null;
//	}
//	
//	 Bitmap resizedBitmap_f = null;
//	 int desWidth_f =0;
//	 int desHeight_f =0;
//
//	//calc angle
//	int rotatedegree = dataFormat.calc_rotate(file);
//	
//	//旋转
//	float scale_w_h = 0.0f;
//	if(rotatedegree==90 || rotatedegree == 270)	
//	{
//		desWidth_f = newHeight;
//		desHeight_f = newWidth;
//		
//	}
//	else {
//		desWidth_f = newWidth;
//		desHeight_f = newHeight;		
//		
//	}
//	
//	 FileInputStream fis_f = null;
//	
//	try{
//					
//		Bitmap bitmap = null;
//		// decode image size
//	    BitmapFactory.Options o_f = new BitmapFactory.Options();
//	    o_f.inJustDecodeBounds = true;
//	    
//	    fis_f = new FileInputStream(file);  
//	    BitmapFactory.decodeStream(fis_f, null, o_f);
//	    fis_f.close();
//	
//	    // Find the correct scale value. It should be the power of 2.
//	    int width_tmp = o_f.outWidth, height_tmp = o_f.outHeight;
//	    
//	    int datatyepe_k = dataFormat.DataFormattoType(o_f.outMimeType);
//	    setBitmapFactory(o_f, datatyepe_k);
//	   
//	    	    
//	    //判断长宽与目标大小的关系
//	    if(width_tmp<= desWidth_f && height_tmp<= desHeight_f)
//	    {
//	    	//直接解压返回    	
//	    	doCallBack(width_tmp, height_tmp);
//
//	          fis_f = new FileInputStream(file);
//	          bitmap = BitmapFactory.decodeStream(fis_f, null, o_f);
//	          fis_f.close();
//	          
//	          //旋转
//	          if(rotatedegree!=0)
//	          {
//	        	  Matrix matrix = new Matrix(); 
//		          matrix.postRotate(rotatedegree); //rotate
//		            
//		          resizedBitmap_f = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),    
//		                            bitmap.getHeight(), matrix, true);  
//		          
//		          return resizedBitmap_f;
//	        	  
//	          }
//	          
//	          
//	          return bitmap;
//	    	
//	    }
//	    else {
//	    	scale_w_h = ((float)desWidth_f)/desHeight_f;
//	    	float scale_ori = ((float)width_tmp)/height_tmp;
//	    	
//	    	 if(scale_ori> scale_w_h)
//		    {
//		    	//超宽  按宽缩放
//	    		 desHeight_f = (int)((((float)desWidth_f)/width_tmp)*height_tmp);
//		    }
//		    else if(scale_ori< scale_w_h)
//		    {
//				//超长  按高缩放
//		    	desWidth_f = (int)((((float)desHeight_f)/height_tmp)*width_tmp);
//			}
//	    	 
//    	   int scaleimg = 1;
//
//    	    Matrix matrix = new Matrix(); 
//    	    
//    	    scaleimg = 1;
//            while (true) {
//                if (width_tmp / 2 < desWidth_f)
//                    break;
//                width_tmp /= 2;
//                height_tmp /= 2;
//                scaleimg *= 2;
//            }
//    	        
//            if(scaleimg!=1 && width_tmp  < desWidth_f)
//            {
//            	scaleimg/=2;
//            }
//    		
//            doCallBack(width_tmp, height_tmp);
//            
//            o_f.inSampleSize = scaleimg;
//            
//            fis_f = new FileInputStream(file);
//            bitmap = BitmapFactory.decodeStream(fis_f, null, o_f);
//            fis_f.close();
//            
//            float scaleWidth = ((float)  desWidth_f ) / bitmap.getWidth();    
//   	     
//            matrix.postScale(scaleWidth, scaleWidth);    //scale
//            
//            matrix.postRotate(rotatedegree); //rotate
//            
//            resizedBitmap_f = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),    
//                            bitmap.getHeight(), matrix, true);  
//	
//	    	
//		}
//	    
//	    
//	
//	
//	
//	    
//		} catch (FileNotFoundException e) {
//		}finally{
//			if(fis_f != null){
//				fis_f.close();
//				fis_f = null;
//			}
//		}
//	
//		
//	return resizedBitmap_f;
//}





//未考虑超高图
//private int CalcInSampleSize(int []ImgSize, int []desWidth, int []desHeight) {
//	
//	int insamplesize = 1;
//	int width_tmp = ImgSize[0];
//	int height_tmp = ImgSize[1];
//	
//	if(ImgSize[0]>desWidth[0] || ImgSize[1]>desHeight[0])
//	{
//		float scale_w_h = ((float)desWidth[0])/desHeight[0];
//		float scale_ori = ((float)width_tmp)/height_tmp;
//		
//		 if(scale_ori> scale_w_h)
//	    {
//	    	//超宽  按宽缩放
//			 desHeight[0] = (int)((((float)desWidth[0])/width_tmp)*height_tmp);
//	    }
//	    else if(scale_ori< scale_w_h)
//	    {
//			//超长  按高缩放
//	    	desWidth[0] = (int)((((float)desHeight[0])/height_tmp)*width_tmp);
//		}
//		 
//		 
//         while (true) {
//             if (width_tmp / 2 < desWidth[0])
//                 break;
//             width_tmp /= 2;
//             height_tmp /= 2;
//             insamplesize *= 2;
//         }
//         
//   
//         if(insamplesize!=1 && width_tmp  < desWidth[0])
//         {
//        	 insamplesize/=2;
//         }
//         
//
//	}
//	
//	return insamplesize;
//	
//}
    
    private int CalcInSampleSize(int[] ImgSize, int[] desWidth, int[] desHeight, int rotate) {

        int insamplesize = 1;
        int width_tmp = ImgSize[0];
        int height_tmp = ImgSize[1];

        float scale_w_h = ((float) desWidth[0]) / desHeight[0];
        float scale_ori = ((float) width_tmp) / height_tmp;

        int flag_superH = 0;
        int flag_superW = 0;

        if (rotate == 0 || rotate == 180) {
            if (scale_ori < 0.5f)
                flag_superH = 1;
            
            if(scale_ori >2.0f)
            {
            	flag_superW = 1;
            }
        }

        if (rotate == 90 || rotate == 270) {
            if (scale_ori > 2.0f)
                flag_superH = 2;
            
            if(scale_ori < 0.5f)
            	flag_superW = 2;
        }


        if (flag_superH > 0 || flag_superW > 0) {
            //0 180
            if (flag_superH == 1 || flag_superW == 2) {

                while (true) {
                    if (width_tmp / 2 < desWidth[0] && height_tmp < maxHeight)
                        break;
                    width_tmp /= 2;
                    height_tmp /= 2;
                    insamplesize *= 2;
                }


                if (insamplesize != 1 && width_tmp < desWidth[0]) {
                    insamplesize /= 2;
                }

                desHeight[0] = (int) ((((float) desWidth[0]) / width_tmp) * height_tmp);


            } else {

                while (true) {
                    if (height_tmp / 2 < desHeight[0] && width_tmp < maxHeight)
                        break;
                    height_tmp /= 2;
                    width_tmp /= 2;
                    insamplesize *= 2;
                }


                if (insamplesize != 1 && height_tmp < desHeight[0]) {
                    insamplesize /= 2;
                }

                desWidth[0] = (int) ((((float) desHeight[0]) / height_tmp) * width_tmp);
            }
        }else {

            if (ImgSize[0] > desWidth[0] || ImgSize[1] > desHeight[0]) {


                if (scale_ori > scale_w_h) {
                    //超宽  按宽缩放
                    desHeight[0] = (int) ((((float) desWidth[0]) / width_tmp) * height_tmp);
                } else if (scale_ori < scale_w_h) {
                    //超长  按高缩放
                    desWidth[0] = (int) ((((float) desHeight[0]) / height_tmp) * width_tmp);
                }


                while (true) {
                    if (width_tmp / 2 < desWidth[0])
                        break;
                    width_tmp /= 2;
                    insamplesize *= 2;
                }


                if (insamplesize != 1 && width_tmp < desWidth[0]) {
                    insamplesize /= 2;
                }


            }
        }


        return insamplesize;

    }
    
//    private int CalcInSampleSize(int[] ImgSize, int[] desWidth, int[] desHeight, int rotate) {
//
//        int insamplesize = 1;
//        int width_tmp = ImgSize[0];
//        int height_tmp = ImgSize[1];
//
//        float scale_w_h = ((float) desWidth[0]) / desHeight[0];
//        float scale_ori = ((float) width_tmp) / height_tmp;
//
//        int flag_superH = 0;
//
//        if (rotate == 0 || rotate == 180) {
//            if (scale_ori < 0.5f)
//                flag_superH = 1;
//        }
//
//        if (rotate == 90 || rotate == 270) {
//            if (scale_ori > 2.0f)
//                flag_superH = 2;
//        }
//
//
//        if (flag_superH > 0) {
//            //0 180
//            if (flag_superH == 1) {
//
//                while (true) {
//                    if (width_tmp / 2 < desWidth[0] && height_tmp < maxHeight)
//                        break;
//                    width_tmp /= 2;
//                    height_tmp /= 2;
//                    insamplesize *= 2;
//                }
//
//
//                if (insamplesize != 1 && width_tmp < desWidth[0]) {
//                    insamplesize /= 2;
//                }
//
//                desHeight[0] = (int) ((((float) desWidth[0]) / width_tmp) * height_tmp);
//
//
//            } else {
//
//                while (true) {
//                    if (height_tmp / 2 < desHeight[0] && width_tmp < maxHeight)
//                        break;
//                    height_tmp /= 2;
//                    width_tmp /= 2;
//                    insamplesize *= 2;
//                }
//
//
//                if (insamplesize != 1 && height_tmp < desHeight[0]) {
//                    insamplesize /= 2;
//                }
//
//                desWidth[0] = (int) ((((float) desHeight[0]) / height_tmp) * width_tmp);
//            }
//        } else {
//
//            if (ImgSize[0] > desWidth[0] || ImgSize[1] > desHeight[0]) {
//
//
//                if (scale_ori > scale_w_h) {
//                    //超宽  按宽缩放
//                    desHeight[0] = (int) ((((float) desWidth[0]) / width_tmp) * height_tmp);
//                } else if (scale_ori < scale_w_h) {
//                    //超长  按高缩放
//                    desWidth[0] = (int) ((((float) desHeight[0]) / height_tmp) * width_tmp);
//                }
//
//
//                while (true) {
//                    if (width_tmp / 2 < desWidth[0])
//                        break;
//                    width_tmp /= 2;
//                    insamplesize *= 2;
//                }
//
//
//                if (insamplesize != 1 && width_tmp < desWidth[0]) {
//                    insamplesize /= 2;
//                }
//
//
//            }
//        }
//
//
//        return insamplesize;
//
//    }


    private BitmapFactory.Options GetFactoryOption(File mfile, int[] desWidth, int[] desHeight, int rotate) throws IOException {
        int[] ImgSize = new int[2];

        BitmapFactory.Options options = new BitmapFactory.Options();

        dataFormat.DecodeWidthHeight(options, mfile, ImgSize);

        int datatyepe_k = dataFormat.DataFormattoType(options.outMimeType);
        setBitmapFactory(options, datatyepe_k);


        options.inSampleSize = CalcInSampleSize(ImgSize, desWidth, desHeight, rotate);

        return options;


    }

    private float CalcScale(int ImgWidth, int ImgHeight, int desWidth, int desHeight) {

        float scaleWidth = 1.0f;
        if (ImgWidth > desWidth && ImgHeight > desHeight) {
            scaleWidth = ((float) desWidth) / ImgWidth;
        }

        return scaleWidth;
    }

//private Bitmap ScaleRotateImg(Bitmap bitmap, float scale, int rotate)
//{
//	try
//	{
//		Matrix matrix = new Matrix(); 
//		matrix.postScale(scale, scale);    //scale
//	    
//	    matrix.postRotate(rotate); //rotate
//	    
//	    Bitmap resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);  
//	    
//	    matrix = null;
//	    return resizeBitmap;
//		
//	}finally
//	{
//		
//	}
//	
//}
    
    public Bitmap cutImage_keepRatio_setColor(File file, int newWidth, int newHeight, boolean isARGB8888) throws IOException {

        if (file == null || newWidth <= 0 || newHeight <= 0) {
            return null;
        }

        int[] desWidth_f = new int[1];
        int[] desHeight_f = new int[1];
        desWidth_f[0] = newWidth;
        desHeight_f[0] = newHeight;

        //calc angle
        int rotatedegree = dataFormat.calc_rotate(file);


        int inSampleSizeOld = 1;
        int OriWidth = 0;
        int OriHeight = 0;

        //旋转
        dataFormat.GetRotateSize(rotatedegree, desWidth_f, desHeight_f);

//        FileInputStream fis_f = null;
        Bitmap bitmap = null;
        Bitmap resultBitmap = null;
        BitmapFactory.Options o_f = null;
        try {

            // decode image size
            o_f = GetFactoryOption(file, desWidth_f, desHeight_f, rotatedegree);

            if(isARGB8888)
            {
            	 o_f.inPreferredConfig = Bitmap.Config.ARGB_8888;
            }
            else {
            	o_f.inPreferredConfig = Bitmap.Config.RGB_565;
			}
//	    if(o_f.outMimeType.isEmpty())
//	    	return null;
//	    if(TextUtils.isEmpty(o_f.outMimeType))
// 			return null;

            if (o_f.inSampleSize != 0) {
                inSampleSizeOld = o_f.inSampleSize;
                OriWidth = o_f.outWidth;
                OriHeight = o_f.outHeight;

                doCallBackForce(o_f.outWidth / o_f.inSampleSize, o_f.outHeight / o_f.inSampleSize, false);
            }

//            fis_f = new FileInputStream(file);
//            // bitmap = BitmapFactory.decodeStream(fis_f, null, o_f);
//            bitmap = BitmapFactory.decodeFileDescriptor(fis_f.getFD(), null, o_f);
//            fis_f.close();
            bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), o_f);

           // float scaleWidth = CalcScale(bitmap.getWidth(), bitmap.getHeight(), desWidth_f[0], desHeight_f[0]);
 			float scaleWidth = 1;
            resultBitmap = dataFormat.ScaleRotateImg(bitmap, scaleWidth, rotatedegree);


        } catch (OutOfMemoryError e) {

            falconImgLog("keep ratio out of memory:inSampleSizeOld"+inSampleSizeOld);
            try {

                o_f.inSampleSize = inSampleSizeOld * 2;
//                fis_f.close();

//                fis_f = new FileInputStream(file);

                if (o_f.inSampleSize != 0) {
                    doCallBackForce(OriWidth / o_f.inSampleSize, OriHeight / o_f.inSampleSize, true);
                }

                //  bitmap = BitmapFactorhiy.decodeStream(fis_f, null, o_f);
                bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), o_f);
//                fis_f.close();

                resultBitmap = dataFormat.ScaleRotateImg(bitmap, 1.0f, rotatedegree);

            } catch (OutOfMemoryError e1) {
                return null;

            }

        } finally {
//            if (fis_f != null) {
//                fis_f.close();
//                fis_f = null;
//            }


        }

        return resultBitmap;
    }

    public Bitmap cutImage_keepRatio(File file, int newWidth, int newHeight) throws IOException {

        if (file == null || newWidth <= 0 || newHeight <= 0) {
            return null;
        }

        int[] desWidth_f = new int[1];
        int[] desHeight_f = new int[1];
        desWidth_f[0] = newWidth;
        desHeight_f[0] = newHeight;

        //calc angle
        int rotatedegree = dataFormat.calc_rotate(file);


        int inSampleSizeOld = 1;
        int OriWidth = 0;
        int OriHeight = 0;

        //旋转
        dataFormat.GetRotateSize(rotatedegree, desWidth_f, desHeight_f);

//        FileInputStream fis_f = null;
        Bitmap bitmap = null;
        Bitmap resultBitmap = null;
        BitmapFactory.Options o_f = null;
        try {

            // decode image size
            o_f = GetFactoryOption(file, desWidth_f, desHeight_f, rotatedegree);

//	    if(o_f.outMimeType.isEmpty())
//	    	return null;
//	    if(TextUtils.isEmpty(o_f.outMimeType))
// 			return null;

            if (o_f.inSampleSize != 0) {
                inSampleSizeOld = o_f.inSampleSize;
                OriWidth = o_f.outWidth;
                OriHeight = o_f.outHeight;

                doCallBackForce(o_f.outWidth / o_f.inSampleSize, o_f.outHeight / o_f.inSampleSize, false);
            }

            if(o_f.outHeight >=10000 || o_f.outWidth >= 10000)
            {
            	o_f.inPreferredConfig = Bitmap.Config.RGB_565;	 
            }
            
//            fis_f = new FileInputStream(file);
//            // bitmap = BitmapFactory.decodeStream(fis_f, null, o_f);
//            bitmap = BitmapFactory.decodeFileDescriptor(fis_f.getFD(), null, o_f);
//            fis_f.close();
            bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), o_f);

           // float scaleWidth = CalcScale(bitmap.getWidth(), bitmap.getHeight(), desWidth_f[0], desHeight_f[0]);
 			float scaleWidth = 1;
            resultBitmap = dataFormat.ScaleRotateImg(bitmap, scaleWidth, rotatedegree);


        } catch (OutOfMemoryError e) {

            falconImgLog("keep ratio out of memory: inSampleSizeOld:"+inSampleSizeOld);
            try {

                o_f.inSampleSize = inSampleSizeOld * 2;
//                fis_f.close();

//                fis_f = new FileInputStream(file);

                if (o_f.inSampleSize != 0) {
                    doCallBackForce(OriWidth / o_f.inSampleSize, OriHeight / o_f.inSampleSize, true);
                }

                //  bitmap = BitmapFactorhiy.decodeStream(fis_f, null, o_f);
                bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), o_f);
//                fis_f.close();

                resultBitmap = dataFormat.ScaleRotateImg(bitmap, 1.0f, rotatedegree);

            } catch (OutOfMemoryError e1) {
                return null;

            }

        } finally {
//            if (fis_f != null) {
//                fis_f.close();
//                fis_f = null;
//            }


        }

        return resultBitmap;
    }

//public Bitmap cutImage_keepRatio(File file,int newWidth, int newHeight) throws Exception
//{
//
//	if (file == null || newWidth<= 0 || newHeight<= 0) {
//		return null;
//	}
//	
//	 int []desWidth_f = new int[1];
//	 int []desHeight_f =new int[1];
//	 desWidth_f[0] = newWidth;
//	 desHeight_f[0] = newHeight;
//
//	//calc angle
//	int rotatedegree = dataFormat.calc_rotate(file);
//	
//	//旋转
//	GetRotateSize(rotatedegree, desWidth_f, desHeight_f);
//	
//	 FileInputStream fis_f = null;
//	 Bitmap bitmap = null;
//	 Bitmap resultBitmap = null;
//	 BitmapFactory.Options o_f = null;
//	try{
//					
//		// decode image size
//	    o_f = GetFactoryOption(file, desWidth_f, desHeight_f);
//	   
//	    if(o_f.inSampleSize!=0)
//	    {
//	    	 doCallBack(o_f.outWidth/o_f.inSampleSize, o_f.outHeight/o_f.inSampleSize);
//	    }
//	   
//	    fis_f = new FileInputStream(file);
//        bitmap = BitmapFactory.decodeStream(fis_f, null, o_f);
//        fis_f.close();
//        
//        float scaleWidth = CalcScale(bitmap.getWidth(), bitmap.getHeight(), desWidth_f[0], desHeight_f[0]);    
//        
//        resultBitmap = ScaleRotateImg(bitmap, scaleWidth, rotatedegree);
//  	     
//	    
//		}finally{
//			if(fis_f != null){
//				fis_f.close();
//				fis_f = null;
//			}
//			
//			return resultBitmap;
//		}
//	
//	
//}


    /***
     * zhangyu.zy     原图显示 不裁剪 只做缩放
     *
     * @param image     输入图像流
     * @param newWidth
     * @param newHeight
     * @return bitmap
     * @throws IOException
     */
    public Bitmap cutImage_keepRatio(InputStream image, int newWidth, int newHeight) throws IOException {

        if (image == null || newWidth <= 0 || newHeight <= 0) {
            return null;
        }

        return cutImage_keepRatio_common(image, newWidth, newHeight, 0);
    }
    
    /***
     * zhangyu.zy     原图显示 不裁剪 只做缩放
     *
     * @param image     输入图像流
     * @param newWidth
     * @param newHeight
     * @return bitmap
     * @throws IOException
     */
    public Bitmap cutImage_keepRatio_setColor(InputStream image, int newWidth, int newHeight, boolean isARGB8888) throws IOException {

        if (image == null || newWidth <= 0 || newHeight <= 0) {
            return null;
        }

        return cutImage_keepRatio_common_setColor(image, newWidth, newHeight, 0, isARGB8888);
    }


    /***
     * zhangyu.zy  估算bitmap存储空间
     *
     * @param newWidth
     * @param newHeight
     * @return
     * @throws IOException
     */

    public int cutImage_keepRatio_size(int newWidth, int newHeight) throws IOException {
        if (newWidth <= 0 || newHeight <= 0) {
            return -1;
        }

        return newWidth * newHeight * 5;
    }


    /****
     * zhangyu.zy    裁剪缩放图片  保证输出宽度为newWidth 高度为newHeight
     *
     * @param file      待裁剪缩放图像流
     * @param newWidth  需求宽
     * @param newHeight 需求高
     * @return 裁剪结果的 btimap
     * @throws IOException
     */

    public Bitmap cutImage_backgroud(File file, int newWidth, int newHeight) throws Exception {

        if (file == null || newWidth <= 0 || newHeight <= 0) {
            return null;
        }

        Bitmap resizedBitmap_b = null;
        int desWidth_b = 0;
        int desHeight_b = 0;

        //calc angle
        int rotatedegree = dataFormat.calc_rotate(file);

        //w/h
        float scale_w_h = 0.0f;
        if (rotatedegree == 90 || rotatedegree == 270) {
            desWidth_b = newHeight;
            desHeight_b = newWidth;

            scale_w_h = ((float) desWidth_b) / desHeight_b;
        } else {
            desWidth_b = newWidth;
            desHeight_b = newHeight;

            scale_w_h = ((float) desWidth_b) / desHeight_b;
        }

//        FileInputStream fis_b = null;

        try {

            Bitmap bitmap_b = null;
            // decode image size
            BitmapFactory.Options o_b = new BitmapFactory.Options();
            o_b.inJustDecodeBounds = true;

//            fis_b = new FileInputStream(file);
//            BitmapFactory.decodeStream(fis_b, null, o_b);
//            fis_b.close();
            BitmapFactory.decodeFile(file.getAbsolutePath(), o_b);

            // Find the correct scale value. It should be the power of 2.
            int width_tmp = o_b.outWidth, height_tmp = o_b.outHeight;

//            int datatype_b = dataFormat.DataFormattoType(o_b.outMimeType);
//            if (datatype_b < 0)
//                return null;

            if(desHeight_b> 850 || desWidth_b>850)
            {
            	setBitmapFactoryBackgroud(o_b, 1);
            }
            else
            {
            	setBitmapFactoryBackgroud(o_b, 2);
			}
            

            float scale_ori = ((float) width_tmp) / height_tmp;
            int imgtype = 0; //等比
            if (scale_ori > scale_w_h) {
                imgtype = 1;  //超宽
            } else if (scale_ori < scale_w_h) {
                imgtype = 2;//超长
            }


            int scaleimg = 1;

            Matrix matrix = new Matrix();

            switch (imgtype) {
                //等比
                case 0:

                    scaleimg = 1;
                    while (true) {
                        if (width_tmp / 2 < desWidth_b)
                            break;
                        width_tmp /= 2;
                        height_tmp /= 2;
                        scaleimg *= 2;
                    }

                    if (scaleimg != 1 && width_tmp < desWidth_b) {
                        scaleimg /= 2;
                    }


                    o_b.inSampleSize = scaleimg;

//                    fis_b = new FileInputStream(file);
                    doCallBackForce(width_tmp, height_tmp, true);


                    // bitmap_b = BitmapFactory.decodeStream(fis_b, null, o_b);
//                    bitmap_b = BitmapFactory.decodeFileDescriptor(fis_b.getFD(), null, o_b);
//                    fis_b.close();
                    bitmap_b = BitmapFactory.decodeFile(file.getAbsolutePath(), o_b);

                    float scaleWidth = ((float) desWidth_b) / bitmap_b.getWidth();

                    matrix.postScale(scaleWidth, scaleWidth);    //scale

                    matrix.postRotate(rotatedegree); //rotate

                    resizedBitmap_b = Bitmap.createBitmap(bitmap_b, 0, 0, bitmap_b.getWidth(),
                            bitmap_b.getHeight(), matrix, true);

                    break;
                //超宽
                case 1:

                    scaleimg = 1;
                    while (true) {
                        if (height_tmp / 2 < desHeight_b)
                            break;
                        width_tmp /= 2;
                        height_tmp /= 2;
                        scaleimg *= 2;
                    }

                    if (scaleimg != 1 && height_tmp < desHeight_b) {
                        scaleimg /= 2;
                    }

                    // decode with inSampleSize
                    o_b.inSampleSize = scaleimg;


//                    fis_b = new FileInputStream(file);
                    doCallBackForce(width_tmp, height_tmp, true);

//                    bitmap_b = BitmapFactory.decodeFileDescriptor(fis_b.getFD(), null, o_b);
                    //bitmap_b = BitmapFactory.decodeStream(fis_b, null, o_b);
//                    fis_b.close();
                    bitmap_b = BitmapFactory.decodeFile(file.getAbsolutePath(), o_b);

                    //cutImageData = cut(imageData, width, height, nchannel, startX, startY, useWidth, useHeight);
                    scaleWidth = ((float) desHeight_b) / bitmap_b.getHeight();
                    int useWidth = (int) ((float) desWidth_b / scaleWidth);

                    matrix.postScale(scaleWidth, scaleWidth);

                    matrix.postRotate(rotatedegree);

                    resizedBitmap_b = Bitmap.createBitmap(bitmap_b, (bitmap_b.getWidth() - useWidth) / 2, 0, useWidth, bitmap_b.getHeight(), matrix, true);

                    break;

                //超长
                case 2:

                    scaleimg = 1;
                    while (true) {
                        if (width_tmp / 2 < desWidth_b)
                            break;
                        width_tmp /= 2;
                        height_tmp /= 2;
                        scaleimg *= 2;
                    }

                    if (scaleimg != 1 && width_tmp < desWidth_b) {
                        scaleimg /= 2;
                    }

                    // decode with inSampleSize
                    o_b.inSampleSize = scaleimg;


//                    fis_b = new FileInputStream(file);

                    doCallBackForce(width_tmp, height_tmp, true);

                    //bitmap_b = BitmapFactory.decodeStream(fis_b, null, o_b);
//                    bitmap_b = BitmapFactory.decodeFileDescriptor(fis_b.getFD(), null, o_b);
//                    fis_b.close();
                    bitmap_b = BitmapFactory.decodeFile(file.getAbsolutePath(), o_b);

                    scaleWidth = ((float) desWidth_b) / bitmap_b.getWidth();

                    int useHeight = (int) ((float) desHeight_b / scaleWidth);


                    matrix.postScale(scaleWidth, scaleWidth);

                    matrix.postRotate(rotatedegree);

                    resizedBitmap_b = Bitmap.createBitmap(bitmap_b, 0, (bitmap_b.getHeight() - useHeight) / 2, bitmap_b.getWidth(), useHeight, matrix, true);

                default:
                    break;
            }

            //bitmap.recycle();
            //  fis.close();

        } catch (Exception e) {
        } finally {
//            if (fis_b != null) {
//                fis_b.close();
//                fis_b = null;
//            }
        }

        /////////////////防止相差一两个像素//////////////////
        if (resizedBitmap_b.getWidth() == newWidth && resizedBitmap_b.getHeight() == newHeight) {

            return resizedBitmap_b;
        }

        Bitmap dst = Bitmap.createScaledBitmap(resizedBitmap_b, newWidth, newHeight, false);
        if (dst != resizedBitmap_b) {

            resizedBitmap_b.recycle();
        }

        return dst;
    }
    



    public Bitmap cutImage_backgroud(InputStream image, int newWidth, int newHeight) throws Exception {

        if (image == null || newWidth <= 0 || newHeight <= 0) {
            return null;
        }

        return cutImage_backgroud_common(image, newWidth, newHeight, 0);
    }


    public Bitmap cutImage_backgroud_common(InputStream image, int newWidth, int newHeight, int rotatedegree) throws Exception {

        if (image == null || newWidth <= 0 || newHeight <= 0) {
            return null;
        }

        Bitmap resizedBitmap_bi = null;
        int desWidth_bi = 0;
        int desHeight_bi = 0;

        ByteArrayOutputStream baos_bi = null;
        RepeatableInputStream fis_bi = null;


        //w/h
        float scale_w_h = 0.0f;
        if (rotatedegree == 90 || rotatedegree == 270) {
            desWidth_bi = newHeight;
            desHeight_bi = newWidth;

            scale_w_h = ((float) desWidth_bi) / desHeight_bi;
        } else {
            desWidth_bi = newWidth;
            desHeight_bi = newHeight;

            scale_w_h = ((float) desWidth_bi) / desHeight_bi;
        }


        try {

            baos_bi = dataFormat.InputStreamToByteArray(image);

            Bitmap bitmap_bi = null;
            // decode image size
            BitmapFactory.Options o_bi = new BitmapFactory.Options();
            o_bi.inJustDecodeBounds = true;

            fis_bi = new RepeatableInputStream(baos_bi.toByteArray());
            BitmapFactory.decodeStream(fis_bi, null, o_bi);


            // Find the correct scale value. It should be the power of 2.
            int width_tmp = o_bi.outWidth, height_tmp = o_bi.outHeight;

            float scale_ori = ((float) width_tmp) / height_tmp;
            int imgtype = 0; //等比
            if (scale_ori > scale_w_h) {
                imgtype = 1;  //超宽
            } else if (scale_ori < scale_w_h) {
                imgtype = 2;//超长
            }


            int scaleimg = 1;

            Matrix matrix = new Matrix();
            // o.inJustDecodeBounds = false;
            //o.inPreferredConfig = Bitmap.Config.ARGB_8888;

//            int datatype = dataFormat.DataFormattoType(o_bi.outMimeType);
//            if (datatype < 0)
//                return null;

            if(desWidth_bi>850 || desHeight_bi>850)
            {
            	 setBitmapFactoryBackgroud(o_bi, 1);
            }
            else {
            	setBitmapFactoryBackgroud(o_bi, 2);
			}
           

            switch (imgtype) {
                //等比
                case 0:

                    scaleimg = 1;
                    while (true) {
                        if (width_tmp / 2 < desWidth_bi)
                            break;
                        width_tmp /= 2;
                        height_tmp /= 2;
                        scaleimg *= 2;
                    }

                    if (scaleimg != 1 && width_tmp < desWidth_bi) {
                        scaleimg /= 2;
                    }

                    // decode with inSampleSize
                    o_bi.inSampleSize = scaleimg;


                    fis_bi.flip();

                    doCallBackForce(width_tmp, height_tmp, true);

                    bitmap_bi = BitmapFactory.decodeStream(fis_bi, null, o_bi);
//                    fis_bi.close();

                    float scaleWidth = ((float) desWidth_bi) / bitmap_bi.getWidth();

                    matrix.postScale(scaleWidth, scaleWidth);    //scale

                    matrix.postRotate(rotatedegree); //rotate

                    resizedBitmap_bi = Bitmap.createBitmap(bitmap_bi, 0, 0, bitmap_bi.getWidth(),
                            bitmap_bi.getHeight(), matrix, true);

                    break;
                //超宽
                case 1:

                    scaleimg = 1;
                    while (true) {
                        if (height_tmp / 2 < desHeight_bi)
                            break;
                        width_tmp /= 2;
                        height_tmp /= 2;
                        scaleimg *= 2;
                    }

                    if (scaleimg != 1 && height_tmp < desHeight_bi) {
                        scaleimg /= 2;
                    }

                    // decode with inSampleSize
                    o_bi.inSampleSize = scaleimg;


                    fis_bi.flip();

                    doCallBackForce(width_tmp, height_tmp, true);

                    bitmap_bi = BitmapFactory.decodeStream(fis_bi, null, o_bi);
//                    fis_bi.close();

                    //cutImageData = cut(imageData, width, height, nchannel, startX, startY, useWidth, useHeight);
                    scaleWidth = ((float) desHeight_bi) / bitmap_bi.getHeight();
                    int useWidth = (int) ((float) desWidth_bi / scaleWidth);

                    matrix.postScale(scaleWidth, scaleWidth);

                    matrix.postRotate(rotatedegree);

                    resizedBitmap_bi = Bitmap.createBitmap(bitmap_bi, (bitmap_bi.getWidth() - useWidth) / 2, 0, useWidth, bitmap_bi.getHeight(), matrix, true);

                    break;

                //超长
                case 2:

                    scaleimg = 1;
                    while (true) {
                        if (width_tmp / 2 < desWidth_bi)
                            break;
                        width_tmp /= 2;
                        height_tmp /= 2;
                        scaleimg *= 2;
                    }

                    if (scaleimg != 1 && width_tmp < desWidth_bi) {
                        scaleimg /= 2;
                    }

                    // decode with inSampleSize
                    o_bi.inSampleSize = scaleimg;


                    fis_bi.flip();

                    doCallBackForce(width_tmp, height_tmp, true);

                    bitmap_bi = BitmapFactory.decodeStream(fis_bi, null, o_bi);
//                    fis_bi.close();

                    scaleWidth = ((float) desWidth_bi) / bitmap_bi.getWidth();

                    int useHeight = (int) ((float) desHeight_bi / scaleWidth);


                    matrix.postScale(scaleWidth, scaleWidth);

                    matrix.postRotate(rotatedegree);

                    resizedBitmap_bi = Bitmap.createBitmap(bitmap_bi, 0, (bitmap_bi.getHeight() - useHeight) / 2, bitmap_bi.getWidth(), useHeight, matrix, true);

                default:
                    break;
            }

            //bitmap.recycle();
            //  fis.close();

        } catch (Exception e) {
        } finally {

            if (baos_bi != null) {
                baos_bi.flush();
//                baos_bi.close();
                closeQuietly(baos_bi);
            }
//            if (fis_bi != null) {
//                fis_bi.close();
//            }
            closeQuietly(fis_bi);
        }

        /////////////////防止相差一两个像素//////////////////
        if (resizedBitmap_bi.getWidth() == newWidth && resizedBitmap_bi.getHeight() == newHeight) {

            return resizedBitmap_bi;
        }

        Bitmap dst = Bitmap.createScaledBitmap(resizedBitmap_bi, newWidth, newHeight, false);
        if (dst != resizedBitmap_bi) {

            resizedBitmap_bi.recycle();
        }

        return dst;
    }


///////////////////////旋转计算 占位宽高/////////////////

    /***
     * zhangyu.zy
     *
     * @param file
     * @param width
     * @param height
     * @param maxLen
     * @param desLen output
     * @throws IOException
     * 占位图计算新规则，外面传入maxLen  与scale
     */
    public boolean calcultDesWidthHeight_new(File file, int width, int height, int maxLen, float scale,int[] desLen) throws IOException {
        /////////////new//////
//	if(file==null  )
//		return false;

        FileInputStream fis = null;
        boolean isOK = false;
        try {

            if ((width > 0) && (height > 0) && (maxLen > 0)) {
                int minLen = (int)(maxLen*scale);

                if (file == null) {
                    JniFalconImg.calcultDesWidthHeight(width, height, maxLen, minLen, scale, desLen);
                    isOK = true;
                } else {

                    BitmapFactory.Options o = new BitmapFactory.Options();
                    o.inJustDecodeBounds = true;

//                    fis = new FileInputStream(file);
//                    BitmapFactory.decodeStream(fis, null, o);
//                    fis.close();
                    BitmapFactory.decodeFile(file.getAbsolutePath(), o);

                    if (TextUtils.isEmpty(o.outMimeType) || (o.outWidth <= 0) || (o.outHeight <= 0)) {
                        //Log.i("falcon", "Decode Fail ,AbsolutePath:" + file.getAbsolutePath());
                    	falconImgLog("Decode Fail ,AbsolutePath:" + file.getAbsolutePath());
                        return false;
                    } else {

                        // Find the correct scale value. It should be the power of 2.
                        width = o.outWidth;
                        height = o.outHeight;

                        int rotatedegree = dataFormat.calc_rotate(file);
                        if (rotatedegree == 90 || rotatedegree == 270) {
                            int temp = width;
                            width = height;
                            height = temp;
                        }
                    }


                    JniFalconImg.calcultDesWidthHeight(width, height, maxLen, minLen, scale, desLen);

                }


                isOK = true;
            } else {
                isOK = false;

            }


        } catch (Exception e) {
            // TODO: handle exception
            isOK = false;
        } finally {

//            if (fis != null) {
//                fis.close();
//                fis = null;
//            }
        }

        return isOK;
    }

    
    

    /***
     * zhangyu.zy
     *
     * @param file
     * @param width
     * @param height
     * @param maxLen
     * @param desLen output
     * @throws IOException
     * 
     */
    public boolean calcultDesWidthHeight_new(File file, int width, int height, int maxLen, int[] desLen) throws IOException {
        /////////////new//////
//	if(file==null  )
//		return false;

        FileInputStream fis = null;
        boolean isOK = false;
        try {

            if ((width > 0) && (height > 0) && (maxLen > 0)) {
                int minLen = maxLen / 2;
                float scale = 0.5f;

                if (file == null) {
                    JniFalconImg.calcultDesWidthHeight(width, height, maxLen, minLen, scale, desLen);
                    isOK = true;
                } else {

                    BitmapFactory.Options o = new BitmapFactory.Options();
                    o.inJustDecodeBounds = true;

//                    fis = new FileInputStream(file);
//                    BitmapFactory.decodeStream(fis, null, o);
//                    fis.close();
                    BitmapFactory.decodeFile(file.getAbsolutePath(), o);

                    if (TextUtils.isEmpty(o.outMimeType) || (o.outWidth <= 0) || (o.outHeight <= 0)) {
                      //  Log.i("falcon", "Decode Fail ,AbsolutePath:" + file.getAbsolutePath());
                    	falconImgLog("Decode Fail ,AbsolutePath:" + file.getAbsolutePath());
                        return false;
                    } else {

                        // Find the correct scale value. It should be the power of 2.
                        width = o.outWidth;
                        height = o.outHeight;

                        int rotatedegree = dataFormat.calc_rotate(file);
                        if (rotatedegree == 90 || rotatedegree == 270) {
                            int temp = width;
                            width = height;
                            height = temp;
                        }
                    }


                    JniFalconImg.calcultDesWidthHeight(width, height, maxLen, minLen, scale, desLen);

                }


                isOK = true;
            } else {
                isOK = false;

            }


        } catch (Exception e) {
            // TODO: handle exception
            isOK = false;
        } finally {

//            if (fis != null) {
//                fis.close();
//                fis = null;
//            }
        }

        return isOK;
    }

    public boolean calcultDesWidthHeight_new(File file, int[] desLen) throws IOException {

        FileInputStream fis = null;
        boolean isSucess = false;
        try {

            if (file == null)
                return false;
            else {

                BitmapFactory.Options o = new BitmapFactory.Options();
                o.inJustDecodeBounds = true;

//                fis = new FileInputStream(file);
//                BitmapFactory.decodeStream(fis, null, o);
//                fis.close();
                BitmapFactory.decodeFile(file.getAbsolutePath(), o);


                if (TextUtils.isEmpty(o.outMimeType) || (o.outWidth <= 0) || (o.outHeight <= 0)) {
                   // Log.i("falcon", "Decode Fail ,AbsolutePath:" + file.getAbsolutePath());
                	falconImgLog("Decode Fail ,AbsolutePath:" + file.getAbsolutePath());
                    //return false;
                    return false;
                }
                // Find the correct scale value. It should be the power of 2.
                desLen[0] = o.outWidth;
                desLen[1] = o.outHeight;

                int rotatedegree = dataFormat.calc_rotate(file);
                if (rotatedegree == 90 || rotatedegree == 270) {
                    int temp = desLen[0];
                    desLen[0] = desLen[1];
                    desLen[1] = temp;
                }

                isSucess = true;
                return true;
            }


        } catch (Exception e) {
            // TODO: handle exception
        } finally {
//            if (fis != null) {
//                fis.close();
//                fis = null;
//            }
        }

        return isSucess;

    }


    private void setBitmapFactory(BitmapFactory.Options options, int datatype) {

//	if(datatype==2)
//		{
//			options.inPreferredConfig = Bitmap.Config.ARGB_8888;	 
//		}
//		else {
//			options.inPreferredConfig = Bitmap.Config.RGB_565;	 
//	}

        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        options.inJustDecodeBounds = false;
        // o.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inPurgeable = true;
        options.inInputShareable = true;

        options.inDither = true;

    }
    
    private void setBitmapFactoryBackgroud(BitmapFactory.Options backoptions, int imgtype)
    {

		if(imgtype==2)
		{
			backoptions.inPreferredConfig = Bitmap.Config.ARGB_8888;	 
		}
		else 
		{
			backoptions.inPreferredConfig = Bitmap.Config.RGB_565;	 
		}

     //   options.inPreferredConfig = Bitmap.Config.ARGB_8888;

		backoptions.inJustDecodeBounds = false;
        // o.inPreferredConfig = Bitmap.Config.ARGB_8888;
		backoptions.inPurgeable = true;
		backoptions.inInputShareable = true;

		backoptions.inDither = true;

    }


    private void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                //ignore
            }
        }
    }
    
    
	public void falconImgLog(String loginfo)
	{
//		LoggerFactory.getTraceLogger().debug("FalconImg", loginfo);
		// Log.i("falconLog",loginfo);
	}

}
