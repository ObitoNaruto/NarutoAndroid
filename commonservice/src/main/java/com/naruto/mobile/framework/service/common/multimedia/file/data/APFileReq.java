package com.naruto.mobile.framework.service.common.multimedia.file.data;

//import com.alipay.android.phone.mobilecommon.multimedia.audio.data.APRequestParam;

import com.naruto.mobile.framework.service.common.multimedia.audio.data.APRequestParam;

public class APFileReq{

    public static final String FILE_TYPE_IMAGE = "image";
    public static final String FILE_TYPE_COMPRESS_IMAGE = "compress_image";

    //请求参数, 识别业务,暂时不用传
    APRequestParam requestParam;

    //本地cacheId
    String cacheId;

    //下载地址或路径
    String cloudId;

    //完整的本地保存路径, 下载时存储到这，上传时从这里读取. 包含后缀
    String savePath;

    //下载地址或路径
    @Deprecated
    String url;

    //是否同步
    boolean isSync;

    //文件类型
    String type;

    boolean isNeedCache = true;

    public APRequestParam getRequestParam() {
        return requestParam;
    }

    public void setRequestParam(APRequestParam requestParam) {
        this.requestParam = requestParam;
    }

    public String getCacheId() {
        return cacheId;
    }

    public void setCacheId(String cacheId) {
        this.cacheId = cacheId;
    }

    public String getCloudId() {
        return cloudId;
    }

    public void setCloudId(String cloudId) {
        this.cloudId = cloudId;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    @Deprecated
    public String getUrl() {
        return url;
    }

    @Deprecated
    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isSync() {
        return isSync;
    }

    public void setSync(boolean isSync) {
        this.isSync = isSync;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isNeedCache() {
        return isNeedCache;
    }

    public void setIsNeedCache(boolean isNeedCache) {
        this.isNeedCache = isNeedCache;
    }

    @Override
    public String toString() {
        return "BaseLoadReq {" +
                "APRequestParam='" + requestParam + '\'' +
                ", cacheId=" + cacheId + '\'' +
                ", cloudId=" + cloudId + '\'' +
                ", savePath=" + savePath + '\'' +
                ", url=" + url + '\'' +
                ", isSync=" + isSync + '\'' +
                ", type=" + type + '\'' +
                '}';
    }
}
