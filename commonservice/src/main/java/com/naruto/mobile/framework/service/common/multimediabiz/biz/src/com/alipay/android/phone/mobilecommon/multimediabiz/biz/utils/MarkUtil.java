package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils;

import android.text.TextUtils;

import com.naruto.mobile.framework.service.common.multimedia.graphics.data.APImageMarkRequest;
import com.naruto.mobile.framework.service.common.multimedia.graphics.load.DisplayImageOptions;

public class MarkUtil {

    public static boolean isValidMarkOption(DisplayImageOptions options) {
        if (options == null) {
            String err = "options cannot be null";
            throw new RuntimeException("isValidMarkRequest " + err);
        }
        return isValidMarkRequest(options.getImageMarkRequest());
    }

    public static boolean isValidMarkRequest(APImageMarkRequest req) {
        boolean ret = false;
        String err = "";
        do {
            if (req == null) {
                err = "APImageMarkRequest cannot be null";
                break;
            }
            if (TextUtils.isEmpty(req.getMarkId())) {
                err = "mark id cannot be null";
                break;
            }
            if (req.getPosition() == null || req.getPosition() < APImageMarkRequest.POS_LEFT_TOP
                    || req.getPosition() > APImageMarkRequest.POS_RIGHT_BOTTOM) {
                err = "position must between 1 and 9";
                break;
            }
            if (req.getTransparency() == null || req.getTransparency() < APImageMarkRequest.TRANSPARENCY_MIN
                    || req.getPosition() > APImageMarkRequest.TRANSPARENCY_MAX) {
                err = "transparency must between 1 and 100";
                break;
            }
            //TODO: 宽高+ PADDING限制??
            if (req.getMarkWidth() != null && req.getMarkWidth() < 0) {
                err = "mark width must big or equal to 0";
                break;
            }
            if (req.getMarkHeight() != null && req.getMarkHeight() < 0) {
                err = "mark height must big or equal to 0";
                break;
            }
            if (req.getPaddingX() == null) {
                err = "mark padding x must be set value";
                break;
            }
            if (req.getPaddingY() == null) {
                err = "mark padding y must be set value";
                break;
            }
            if (req.getPercent() != null && (req.getPercent() < APImageMarkRequest.PERCENT_MIN
                    || req.getPercent() > APImageMarkRequest.PERCENT_MAX)) {
                err = "mark percent must be null or (0,100]";
                break;
            }
            //percent和宽高必须要有一个
            if (req.getPercent() == null && (req.getMarkWidth() == null || req.getMarkHeight() == null)) {
                err = "mark must have percent or width&height";
                break;
            }
            ret = true;
        } while (false);
        Logger.P("MarkUtil", "isValidMarkRequest " + ret + " " + err);
        if (!ret) {
            throw new RuntimeException("isValidMarkRequest " + err);
        }
        return ret;
    }
}
