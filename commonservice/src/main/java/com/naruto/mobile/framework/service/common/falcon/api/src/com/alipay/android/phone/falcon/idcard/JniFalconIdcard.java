package com.naruto.mobile.framework.service.common.falcon.api.src.com.alipay.android.phone.falcon.idcard;


import android.graphics.Bitmap;

import com.naruto.mobile.framework.service.common.falcon.api.src.com.alipay.android.phone.falcon.common.StringManager;

public class JniFalconIdcard {

    static {
        System.loadLibrary("FalconOCR");
    }
    
    public static native boolean SetThreshold(int TBlur,int TExposure);

    //初始化函数
    public static native boolean Initialize(byte[] datadir, byte[] configpath);

    //释放函数
    public static native void Release();

    //验证视频流中一帧是否有效：0，无效，1：包含4个边框但不清晰，2：包含框且比较清晰
    public static native int IsVideoValid(byte[] datayuv, int w, int h, float ratiox,float ratioy);

    //picture是否有效，true：有效，false：无效
//    public static native boolean IsPhotoValid(int[] datajpg, int width, int height, boolean bCropImg);
//    
//
//    public static native boolean IsPhotoValidBitmap(Bitmap bitmap,boolean bCropImg);
    
    public static native boolean IsPhotoValidBitmapSaveNew(Bitmap bitmap,boolean bCropImg,byte[] saveCropPath,boolean isForceSave);
    
    
    public static native boolean IsPhotoValidBitmapSave(Bitmap bitmap,boolean bCropImg,byte[] saveCropPath,boolean isForceSave);

    //识别函数，指定拍摄图片保存的完整路径和图片名字
    public static native boolean Recognize();

    //界面显示可能需要，返回拍摄图片的宽高
    public static native void GetCroppedImgSize(int[] msize);
    public static native void GetCroppedImgBitmap(Bitmap bitmap);

    //picture类型判断，0:不是身份证，1:身份证正面，2:身份证背面
    public static native int GetPhotoType();
    
    
  //反光点检测
    public static native int GetExpType();
    //模糊检测
    public static native int GetBlurType();
    //以下部分都是返回识别相关的信息
    public static native void GetName(byte[] text, int size);

    public static native void GetCardNumber(byte[] text, int size);

    public static native void GetGender(byte[] text, int size);

    public static native void GetBirthday(byte[] text, int size);

    public static native void GetAddress(byte[] text, int size);

    public static native void GetIssueAuthority(byte[] text, int size);

    public static native void GetValidPeriod(byte[] text, int size);

    public static native void GetNationality(byte[] text, int size);

    public static String GetName() {
        int textsize = 256;
        byte[] text = new byte[textsize];
        GetName(text, textsize);

        return StringManager.convertGbkToUnicode(text);
    }

    public static String GetCardNumber() {
        int textsize = 256;
        byte[] text = new byte[textsize];
        GetCardNumber(text, textsize);

        return StringManager.convertGbkToUnicode(text);
    }

    public static String GetGender() {
        int textsize = 256;
        byte[] text = new byte[textsize];
        GetGender(text, textsize);

        return StringManager.convertGbkToUnicode(text);
    }

    public static String GetBirthday() {
        int textsize = 256;
        byte[] text = new byte[textsize];
        GetBirthday(text, textsize);

        return StringManager.convertGbkToUnicode(text);
    }

    public static String GetAddress() {
        int textsize = 256;
        byte[] text = new byte[textsize];
        GetAddress(text, textsize);

        return StringManager.convertGbkToUnicode(text);
    }

    public static String GetIssueAuthority() {
        int textsize = 256;
        byte[] text = new byte[textsize];
        GetIssueAuthority(text, textsize);

        return StringManager.convertGbkToUnicode(text);
    }

    public static String GetValidPeriod() {
        int textsize = 256;
        byte[] text = new byte[textsize];
        GetValidPeriod(text, textsize);

        return StringManager.convertGbkToUnicode(text);
    }

    public static String GetNationality() {
        int textsize = 256;
        byte[] text = new byte[textsize];
        GetNationality(text, textsize);

        return StringManager.convertGbkToUnicode(text);
    }

}
