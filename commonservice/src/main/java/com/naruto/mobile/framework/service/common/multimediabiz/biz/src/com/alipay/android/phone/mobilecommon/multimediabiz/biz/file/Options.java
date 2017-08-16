package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.file;


import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.req.BaseUpReq;

public class Options {
    /**
     * 普通模式，大于10M使用Chunk上传
     */
    public static final int UPLOAD_TYPE_NOR = 0;
    /**
     * 分片模式，小文件弱网上传建议使用
     */
    public static final int UPLOAD_TYPE_SLICE = 1;

    public int uploadType = UPLOAD_TYPE_NOR;
    public boolean skipRapid = false;
    public int sliceSize = BaseUpReq.DEFAULT_SLICE_LENGTH;
}