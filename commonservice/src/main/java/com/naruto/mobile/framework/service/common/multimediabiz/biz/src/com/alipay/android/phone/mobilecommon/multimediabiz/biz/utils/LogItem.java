package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;
//import com.alipay.mobile.common.logging.api.LoggerFactory;
//import com.alipay.mobile.common.logging.api.behavor.Behavor;
/**
 * Created by xiangui.fxg on 2015/6/4.
 */
public class LogItem {
    private static final String TAG = "LogItem";

    public static final String APP_ID_MULTIMEDIA = "APMultiMedia";

    public static final String CLICKED = "clicked";
    public static final String EVENT = "event";

    //多媒体操作过程中异常信息的key
    public static final String KEY_AUTH_EXP = "exp";
    public static final String KEY_TIME_COST = "tc";
    public static final String DEFAULT_PARAM = "";

    /*用例编号:UC_MM_C01扩展4*/
    //是否秒传：is rapid: ra=0 for not rapid, ra=1 for rapid
    public static final String MM_C01_K4_RD = "rd";
    //是否压缩：co=0 for low, co=1 for mid co=2 for high, co=3 for none
    public static final String MM_C01_K4_CO = "co";
    //图片类型：image type: it=0 for small, it=1 for big it=2 for original
    public static final String MM_C01_K4_IT = "it";
    //图片MD5
    public static final String MM_C01_K4_MD = "md";
    //异常信息
    public static final String MM_C01_K4_EXP = "exp";
    //traceId
    public static final String MM_K4_TRACE = "ti";
    //fileId
    public static final String MM_K4_FILEID = "fi";

    /*用例编号:UC_MM_C02扩展4*/
    public static final String MM_C02_K4_LEN = "le"; //音频时长，单位秒
    public static final String MM_C02_K4_EXP = "exp";

    /*用例编号:UC_MM_C03扩展4*/
    public static final String MM_C03_K4_MD5 = "md"; //md5,失败的时候才有
    public static final String MM_C03_K4_EXP = "exp";

    /*用例编号:UC_MM_C04扩展4*/
    public static final String MM_C04_K4_ZO = "zo"; //zo=zoom or url if external 原图是0x0
    public static final String MM_C04_K4_TYPE = "tp"; //tp=0 for samll ,tp=1 for big,tp=2 for original
    public static final String MM_C04_K4_EXP = "exp";

    /*用例编号:UC_MM_C06扩展4*/
    public static final String MM_C06_K4_ZI = "zi"; //是否压缩，zi=0 for not zip,zi=1 for zip
    public static final String MM_C06_K4_FI = "fi"; //fileId,失败的时候才有
    public static final String MM_C06_K4_EXP = "exp";

    /*用例编号:UC_MM_C07扩展4*/
    public static final String MM_C07_K4_TYPE = "tp"; //类型，tp=0 for compress ,tp=1 for cut
    /*用例编号:UC_MM_C10扩展4*/
    public static final String MM_C10_K4_TYPE = "tp"; //缓存类型：cache(tp=ca) disk(tp=dk)
    /**
     * 用例编号：UC_MM_C11扩展4
     * 异常信息
     */
    public static final String MM_C11_K4_EXP = "exp";
    /**
     * 用例编号：UC_MM_C11扩展4
     * 存储类型：0：手机盘 1：sdcard
     */
    public static final String MM_C11_K4_ST = "st";
    /**
     * 用例编号：UC_MM_C11扩展4
     * 剩余空间：单位 MB
     */
    public static final String MM_C11_K4_SP = "sp";
    /**
     * 用例编号：UC_MM_C12扩展4
     * 异常信息
     */
    public static final String MM_C12_K4_EXP = "exp";
    /**
     * 用例编号：UC_MM_C12扩展4
     * 异常的cloud id
     */
    public static final String MM_C12_K4_ID = "ci";

    private String caseID; // 用例ID
    private String behaviorID; // 行为ID
    private String seedID; // 埋点ID
    private String extParam1; // 扩展1
    private String extParam2; // 扩展2
    private String extParam3; // 扩展3
    private Map<String, String> extParams; // 扩展4

    public static final String SUCCESS = "0";

    public LogItem(String caseId, String behaviorId, String seedId, String extParam1, String extParam2, String extParam3) {
        this.caseID = caseId;
        this.behaviorID = behaviorId;//BehaviourIdEnum.convert(behaviorId);
        this.seedID = seedId;
        this.extParam1 = extParam1;
        this.extParam2 = extParam2;
        this.extParam3 = extParam3;
    }

    public void addExtParam(String key, String value) {
        if (extParams == null) {
            extParams = new HashMap<String, String>();
        }
        extParams.put(key, value);
    }

    public void log(LogItem logItem) {

//        if (logItem == null) {
//            return;
//        }
//
//        Behavor behavor = new Behavor();
//        behavor.setAppID(APP_ID_MULTIMEDIA);
//        behavor.setUserCaseID(logItem.caseID);
//        behavor.setSeedID(logItem.seedID);
//        behavor.setParam1(logItem.extParam1);
//        behavor.setParam2(logItem.extParam2);
//        behavor.setParam3(logItem.extParam3);
//        if (logItem.extParams != null) {
//            for (String key : logItem.extParams.keySet()) {
//                behavor.addExtParam(key, logItem.extParams.get(key));
//            }
//        }
//
//        if (CLICKED.equals(logItem.behaviorID)) {
//            LoggerFactory.getBehavorLogger().click(behavor);
//        }else {
//            LoggerFactory.getBehavorLogger().event("", behavor);
//        }
//
//        //Log.d(TAG,logItem.toString());
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("caseId:" + caseID + ",");
        sb.append("behaviorID:" + behaviorID + ",");
        sb.append("seedId:" + seedID + ",");
        sb.append("extParam1:" + extParam1 + ",");
        sb.append("extParam2:" + extParam2 + ",");
        sb.append("extParam3:" + extParam3 + ",");
        sb.append("extParams:" + extParams);
        return sb.toString();
    }
}
