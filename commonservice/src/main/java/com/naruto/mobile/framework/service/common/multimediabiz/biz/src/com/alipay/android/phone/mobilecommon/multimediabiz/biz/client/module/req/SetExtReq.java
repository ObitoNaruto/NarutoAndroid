/*
 * Copyright 2014 Alibaba.com All right reserved.
 * This software is the confidential and proprietary information of Alibaba.com ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.req;

import java.util.HashMap;
import java.util.Map;

/**
 * 设置文件扩展属性的请求，请参考:
 *  <p>http://docs.alibaba-inc.com/display/CloudDrive/Django+1.0+API</p>
 *  <p>第4.2节</p>
 *  <p>
 *      相关的属性Key也可以选择使用常理类：{@link ExifFieldKey}、
 *      {@link PrivilegeKey}
 *  </p>
 * @author jinzhaoyu
 */
public class SetExtReq {
    public SetExtReq(String fileId) {
        this.fileId = fileId;
    }

    private String fileId;
    private Map<String, Map<String, String>> ext = new HashMap<String, Map<String, String>>();

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public Map<String, Map<String, String>> getExt() {
        return ext;
    }

    public void setExt(Map<String, Map<String, String>> ext) {
        this.ext = ext;
    }

    /**
     * 设置文件exif信息的Key
     */
    public static class ExifFieldKey {
        public static String digitizedTime = "digitizedTime";
        public static String exploreBias = "exploreBias";
        public static String exploreTime = "exploreTime";
        public static String fNumber = "fNumber";
        public static String height = "height";
        public static String isoEquivalent = "isoEquivalent";
        public static String make = "make";
        public static String model = "model";
        public static String orientation = "orientation";
        public static String originalTime = "originalTime";
        public static String software = "software";
        public static String version = "version";
        public static String width = "width";
        public static String xResolution = "xResolution";
        public static String yResolution = "yResolution";
    }

    /**
     * 设置文件权限的key及value。
     */
    public static class PrivilegeKey{
        public static String public_key = "public";
        public static String public_value_true = "1";
        public static String public_value_false = "0";
    }

}
