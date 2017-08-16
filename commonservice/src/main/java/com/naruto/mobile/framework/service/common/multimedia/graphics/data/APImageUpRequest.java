package com.naruto.mobile.framework.service.common.multimedia.graphics.data;


import com.naruto.mobile.framework.service.common.multimedia.api.data.BaseLoadReq;
import com.naruto.mobile.framework.service.common.multimedia.graphics.APImageUploadCallback;
import com.naruto.mobile.framework.service.common.multimedia.graphics.APImageUploadOption;

/**
 * Created by zhenghui on 15/4/11.
 */
public class APImageUpRequest extends BaseLoadReq {
    public final static int TYPE_ORIGINAL = 0;
    public final static int TYPE_HIGH = 1;
    public final static int TYPE_MIDDLE = 2;
    public final static int TYPE_LOW = 3;
    public final static int TYPE_DEFAULT = 4;

    public final static int DEFAULT_UP_W = 1280;
    public final static int DEFAULT_UP_H = 1280;
    public int width = DEFAULT_UP_W;
    public int height = DEFAULT_UP_H;
    public int uploadType = TYPE_DEFAULT;
    public byte[] fileData;
    public APImageUploadCallback callback;
    public APImageUploadOption option;
}
