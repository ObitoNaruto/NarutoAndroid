package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.req;

import java.util.List;

/**
 * 文件离线上传请求
 * token: 必须，access token
 * download_url: 必须，文件离线下载 HTTP URL
 * synchronous: 可选，默认为 false 异步下载至 django 服务器
 * md5: 可选
 * size: 可选
 * type: 可选，例如 jpg, doc 等
 * download_header_XXX: 可选，离线下载 URL 访问时需要提供的 http header, header name 为 XXX
 * notify_url: 可选，文件现在真正传输完成后回调通知 URL
 * notify_header_XXX: 可选，回调通知 URL 访问时需要提供的 http header, header name 为 XXX
 * Created by jinmin on 15/6/12.
 */
public class FileOfflineUploadReq {
    public String token;
    public String downloadUrl;
    public boolean synchoronous = false;
    public String md5;
    public long size;
    public String type;
    public List<String> downloadHeaders;
    public String notifyUrl;
    public List<String> notifyHeaders;

    @Override
    public String toString() {
        return "FileOfflineUploadReq{" +
                "token='" + token + '\'' +
                ", downloadUrl='" + downloadUrl + '\'' +
                ", synchoronous=" + synchoronous +
                ", md5='" + md5 + '\'' +
                ", size=" + size +
                ", type='" + type + '\'' +
                ", downloadHeaders=" + downloadHeaders +
                ", notifyUrl='" + notifyUrl + '\'' +
                ", notifyHeaders=" + notifyHeaders +
                '}';
    }
}
