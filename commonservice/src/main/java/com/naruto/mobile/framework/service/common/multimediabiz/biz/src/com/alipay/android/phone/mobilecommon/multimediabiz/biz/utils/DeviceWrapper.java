package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils;

import android.os.Build;

public class DeviceWrapper {

    private static Boolean isSamSung = null;
    private static final int sdkInt = Build.VERSION.SDK_INT;

    public static boolean isSamSung() {
        if (isSamSung == null) {
            isSamSung = ("samsung".equalsIgnoreCase(Build.MANUFACTURER));
        }
        return isSamSung;
    }

    /**
     * 三星3.2平板ListView 由于bitmap reuse 会有串图问题
     */
    public static boolean hasBitmapReuseablity() {
        if (isSamSung() && sdkInt == 13) { //HONEYCOMB_MR2 3.2
            return false;
        }
        return true;
    }
}
