package com.naruto.mobile.framework.service.common;

import java.util.ArrayList;
import java.util.concurrent.Future;

import com.naruto.mobile.base.serviceaop.service.CommonService;
import com.naruto.mobile.framework.rpc.myhttp.transport.TransportCallback;
import com.naruto.mobile.framework.rpc.myhttp.transport.download.DownloadRequest;

import org.apache.http.Header;


/**
 * 下载服务
 * 
 * @author sanping.li@alipay.com
 *
 */
public abstract class  DownloadService extends CommonService {

    /**
     * 添加下载任务, 推荐使用带DownloadRequest入参的addDownload方法，将来扩展比较方便
     * 
     * @param url 下载地址
     * @param path 保存的路径
     * @param headers 需要的HTTP请求头
     * @param callback 回调处理
     */
    @Deprecated
    public abstract Future<?> addDownload(String url, final String path, ArrayList<Header> headers,
                            TransportCallback callback);
    /**
     * 添加下载任务， 推荐使用带DownloadRequest入参的方法，将来扩展比较方便
     * 
     * @param url downloadRequest.url 下载地址
     * @param path downloadRequest.path 保存的路径
     * @param headers downloadRequest.headers 需要的HTTP请求头
     * @param callback downloadRequest.callback 回调处理
     */
    public abstract Future<?> addDownload(DownloadRequest downloadRequest);

}
