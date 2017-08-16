package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils;

import android.util.Log;

import java.util.Random;

import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.req.ThumbnailsDownReq;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.config.ConfigConstants;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.config.ConfigManager;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.http.util.TextUtils;

/**
 * Created by xiangui.fxg on 2015/6/4.
 */
public class UCLogUtil {
    private static final boolean bDisable = true;

    private static int mLogSampleVal = ConfigConstants.LOG_SAMPLE_INTERVAL_DEFAULT;
    private static Random rd = new Random();

    private static int creatLogInterval() {
        return rd.nextInt(99) + 1;
    }

    /**
     * 用例编号:UC_MM_C01, 版本:9.0, 行为解释:上传图片
     *
     * @param ret      成功传LogItem.SUCCESS, 失败写具体的error code
     * @param size     文件长度
     * @param time     耗时
     * @param rapid    是否秒传 ra=0 for not rapid, ra=1 for rapid
     * @param compress 是否压缩：co=0 for low, co=1 for mid co=2 for high, co=3 for none
     * @param type     图片类型：it=0 for small, it=1 for big it=2 for original
     */
    public static void UC_MM_C01(String ret, long size, int time, int rapid, int compress, int type, String md5,
            String exp, String traceId) {
        if (ret == null) {
            ret = "";
        }

        LogItem logItem = new LogItem("UC-MM-C01", LogItem.CLICKED, "UploadImage",
                ret, String.valueOf(size), String.valueOf(time));
        logItem.addExtParam(LogItem.MM_C01_K4_RD, String.valueOf(rapid));
        logItem.addExtParam(LogItem.MM_C01_K4_CO, String.valueOf(compress));
        logItem.addExtParam(LogItem.MM_C01_K4_IT, String.valueOf(type));
        if (ret.startsWith("s") || !"0".equalsIgnoreCase(ret)) {
            if (!TextUtils.isEmpty(md5)) {
                logItem.addExtParam(LogItem.MM_C01_K4_MD, md5);
            }
        }

        if (!TextUtils.isEmpty(exp)) {
            logItem.addExtParam(LogItem.MM_C01_K4_EXP, exp);
        }
        if (!TextUtils.isEmpty(traceId)) {
            logItem.addExtParam(LogItem.MM_K4_TRACE, traceId);
        }
        logItem.log(logItem);
    }

    /**
     * 用例编号:UC_MM_C02, 版本:9.0, 行为解释:上传音频
     *
     * @param ret     成功传LogItem.SUCCESS, 失败写具体的error code
     * @param size    文件长度
     * @param time    耗时
     * @param timeLen 音频时长，单位秒
     */
     public static void UC_MM_C02(int ret,long size,int time,long timeLen, String traceId,String exp) {
        LogItem logItem = new LogItem("UC-MM-C02", LogItem.CLICKED, "UploadVoice",
                ret == 0 ? LogItem.SUCCESS : String.valueOf(ret), String.valueOf(size), String.valueOf(time));
        logItem.addExtParam(LogItem.MM_C02_K4_LEN, String.valueOf(timeLen));
        if (!TextUtils.isEmpty(traceId)) {
            logItem.addExtParam(LogItem.MM_K4_TRACE, traceId);
        }
         if (!TextUtils.isEmpty(exp)) {
             logItem.addExtParam(LogItem.MM_C02_K4_EXP, exp);
         }
        logItem.log(logItem);
    }

    /**
     * 用例编号:UC_MM_C03, 版本:9.0, 行为解释:上传文件
     *
     * @param ret  成功传LogItem.SUCCESS, 失败写具体的error code
     * @param size 文件长度
     * @param time 耗时
     */
    public static void UC_MM_C03(String ret, long size, int time, String exp, String traceId, String md5) {
        if (ret == null) {
            ret = "";
        }
        LogItem logItem = new LogItem("UC-MM-C03", LogItem.CLICKED, "UploadFile",
                ret, String.valueOf(size), String.valueOf(time));
        if (!TextUtils.isEmpty(traceId)) {
            logItem.addExtParam(LogItem.MM_K4_TRACE, traceId);
        }
        if (!TextUtils.isEmpty(exp)) {
            logItem.addExtParam(LogItem.MM_C03_K4_EXP, exp);
        }
        if (!TextUtils.isEmpty(md5)) {
            logItem.addExtParam(LogItem.MM_C03_K4_MD5, md5);
        }
        logItem.log(logItem);
    }

    /**
     * 用例编号:UC_MM_C04, 版本:9.0, 行为解释:下载图片
     *
     * @param ret  成功传LogItem.SUCCESS, 失败写具体的error code
     * @param size 文件长度,如果是从本地缓存中取到则为-1
     * @param time 耗时
     * @param zoom 尺寸标识
     * @param type 图片类型tp=0 for samll ,tp=1 for big,tp=2 for original
     */
     public static void UC_MM_C04(String ret,long size,int time,String zoom,int type,boolean bLocal,String exp, String traceId) {
         UC_MM_C04(ret,size,time,zoom,type,bLocal,exp,traceId,null);
     }
     public static void UC_MM_C04(String ret,long size,int time,String zoom,int type,boolean bLocal,String exp, String traceId,String fileId) {
         //只针对本地图片做采用埋点
		 //if (bLocal && !checkInSample()){
         //    return;
         //}
		 
		 if(ret == null){
		 	ret = "";
		 }
		 
         //原图使用0x0表示
        if(ThumbnailsDownReq.DJANGO_ORIGINAL.equalsIgnoreCase(zoom)){
            zoom = "0x0";
        }
        LogItem logItem = new LogItem("UC-MM-C04", LogItem.CLICKED, "DownloadImage",
                ret, String.valueOf(size), String.valueOf(time));
        logItem.addExtParam(LogItem.MM_C04_K4_ZO, zoom);
        logItem.addExtParam(LogItem.MM_C04_K4_TYPE, String.valueOf(type));
        if (!TextUtils.isEmpty(exp)) {
            logItem.addExtParam(LogItem.MM_C04_K4_EXP, exp);
        }

        if (!TextUtils.isEmpty(traceId)) {
            logItem.addExtParam(LogItem.MM_K4_TRACE, traceId);
        }

        if (!TextUtils.isEmpty(fileId) && !LogItem.SUCCESS.equalsIgnoreCase(ret)) {
            logItem.addExtParam(LogItem.MM_K4_FILEID, fileId);
        }

        logItem.log(logItem);

    }

    /**
     * 用例编号:UC_MM_C05, 版本:9.0, 行为解释:下载音频
     *
     * @param ret  成功传LogItem.SUCCESS, 失败写具体的error code
     * @param size 文件长度,如果是从本地缓存中取到则为-1
     * @param time 耗时
     */
    public static void UC_MM_C05(int ret, long size, int time) {
        LogItem logItem = new LogItem("UC-MM-C05", LogItem.CLICKED, "DownloadVoice",
                ret == 0 ? LogItem.SUCCESS : String.valueOf(ret), String.valueOf(size), String.valueOf(time));
        logItem.log(logItem);
    }

    /**
     * 用例编号:UC_MM_C06, 版本:9.0, 行为解释:下载文件
     *
     * @param ret   成功传LogItem.SUCCESS, 失败写具体的error code
     * @param size  文件长度
     * @param time  耗时
     * @param isZip 是否压缩 zi=0 for not zip,zi=1 for zip
     */
    public static void UC_MM_C06(String ret, long size, int time, int isZip, String exp, String traceId, String fileId) {
        if (ret == null) {
            ret = "";
        }
        LogItem logItem = new LogItem("UC-MM-C06", LogItem.CLICKED, "DownloadFile",
                ret, String.valueOf(size), String.valueOf(time));
        logItem.addExtParam(LogItem.MM_C06_K4_ZI, String.valueOf(isZip));
        if (!TextUtils.isEmpty(exp)) {
            logItem.addExtParam(LogItem.MM_C06_K4_EXP, exp);
        }
        if (!TextUtils.isEmpty(traceId)) {
            logItem.addExtParam(LogItem.MM_K4_TRACE, traceId);
        }
        if (!TextUtils.isEmpty(fileId)) {
            logItem.addExtParam(LogItem.MM_C06_K4_FI, fileId);
        }
        logItem.log(logItem);
    }

    /**
     * 用例编号:UC_MM_C07, 版本:9.0, 行为解释:压缩/裁剪
     *
     * @param size 文件长度
     * @param time 耗时
     * @param tp   类型：tp=0 for compress ,tp=1 for cut
     */
    public static void UC_MM_C07(long size, int time, int tp) {
        //只针对cut做采用埋点
        if (tp == 1 && !checkInSample()) {
            return;
        }

        LogItem logItem = new LogItem("UC-MM-C07", LogItem.CLICKED, "CompressImage",
                LogItem.DEFAULT_PARAM, String.valueOf(size), String.valueOf(time));
        logItem.addExtParam(LogItem.MM_C07_K4_TYPE, String.valueOf(tp));
        logItem.log(logItem);
    }

    /**
     * 用例编号:UC_MM_C08, 版本:9.0, 行为解释:取图片（可能会太频繁）
     *
     * @param ret  成功传LogItem.SUCCESS, 失败写具体的error code
     * @param size 文件长度
     * @param time 耗时
     */
    public static void UC_MM_C08(int ret, long size, int time) {
        if (bDisable) {
            return;
        }
        LogItem logItem = new LogItem("UC-MM-C08", LogItem.CLICKED, "GetImage",
                ret == 0 ? LogItem.SUCCESS : String.valueOf(ret), String.valueOf(size), String.valueOf(time));
        logItem.log(logItem);
    }

    /**
     * 用例编号:UC_MM_C09, 版本:9.0, 行为解释:取音频
     *
     * @param ret  成功传LogItem.SUCCESS, 失败写具体的error code
     * @param size 文件长度
     * @param time 耗时
     */
    public static void UC_MM_C09(int ret, long size, int time) {
        if (bDisable) {
            return;
        }
        LogItem logItem = new LogItem("UC-MM-C09", LogItem.CLICKED, "GetVoice",
                ret == 0 ? LogItem.SUCCESS : String.valueOf(ret), String.valueOf(size), String.valueOf(time));
        logItem.log(logItem);
    }

    /**
     * 用例编号:UC_MM_C10, 版本:9.0, 行为解释:清除缓存
     *
     * @param ret  成功传LogItem.SUCCESS, 失败写具体的error code
     * @param type 缓存类型：cache(tp=0) disk(tp=1)
     */
    public static void UC_MM_C10(int ret, int type) {
        LogItem logItem = new LogItem("UC-MM-C10", LogItem.EVENT, "CleanMemory",
                ret == 0 ? LogItem.SUCCESS : String.valueOf(ret), LogItem.DEFAULT_PARAM, LogItem.DEFAULT_PARAM);
        logItem.addExtParam(LogItem.MM_C10_K4_TYPE, String.valueOf(type));
        logItem.log(logItem);
    }

    /**
     * 用例编号:UC_MM_C11, 版本:9.0, 行为解释:录音统计
     * @param ret           0-成功录音：LogItem.SUCCESS, 失败写具体的error code
     * @param err           异常信息
     */
    public static void UC_MM_C11(int ret, String err) {
        LogItem logItem = new LogItem("UC-MM-C11", LogItem.CLICKED, "RecordVoice",
                ret==0?LogItem.SUCCESS:String.valueOf(ret), LogItem.DEFAULT_PARAM, LogItem.DEFAULT_PARAM);
        logItem.addExtParam(LogItem.MM_C11_K4_EXP, err == null ? "" : err);
        if (ret != 0) {
            if (android.text.TextUtils.isEmpty(""/*FileUtils.getSDPath()*/)) {
                logItem.addExtParam(LogItem.MM_C11_K4_ST, "0");
                logItem.addExtParam(LogItem.MM_C11_K4_SP, FileUtils.getPhoneAvailableSize());
            } else {
                logItem.addExtParam(LogItem.MM_C11_K4_ST, "1");
                logItem.addExtParam(LogItem.MM_C11_K4_SP, FileUtils.getSdAvailableSize());
            }
        }
        logItem.log(logItem);

        //Log.d("UCLogUtil", "UC_MM_C11 logItem: " + logItem);
    }

    /**
     * 用例编号：UC_MM_C12，版本：9.0，行为解释：播放统计
     * @param ret       0-成功播放：LogItem.SUCCESS, 失败写具体的error code
     * @param id        语音的id
     * @param e         异常信息
     */
    public static void UC_MM_C12(int ret, String id, String e) {
        LogItem logItem = new LogItem("UC-MM-C12", LogItem.CLICKED, "PlayVoice",
                ret==0?LogItem.SUCCESS:String.valueOf(ret), LogItem.DEFAULT_PARAM, LogItem.DEFAULT_PARAM);
        logItem.addExtParam(LogItem.MM_C12_K4_ID, ret != 0 ? id : LogItem.DEFAULT_PARAM);
        logItem.addExtParam(LogItem.MM_C12_K4_EXP, e == null ? "" : e);
        logItem.log(logItem);

        //Log.d("UCLogUtil", "UC_MM_C12 logItem: " + logItem);
    }

    private static boolean checkInSample(){
        int rd = creatLogInterval();
        mLogSampleVal = ConfigManager.getInstance()
                .getIntValue(ConfigConstants.LOG_SAMPLE_INTERVAL_KEY, ConfigConstants.LOG_SAMPLE_INTERVAL_DEFAULT);
        //Log.d("LogItem","isInSample rd="+rd+";mLogSampleVal="+mLogSampleVal);
        if (mLogSampleVal > 100) {
            mLogSampleVal = 100;
        }

        if (rd >= mLogSampleVal) {
            return false;
        }

        return true;
    }
}
