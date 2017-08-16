package com.naruto.mobile.framework.rpc.myhttp.transport.download;

import java.util.ArrayList;
import java.util.concurrent.Future;

import org.apache.http.Header;

import android.content.Context;

import com.naruto.mobile.framework.rpc.myhttp.transport.TransportCallback;
import com.naruto.mobile.framework.rpc.myhttp.transport.http.HttpManager;
import com.naruto.mobile.framework.rpc.myhttp.transport.http.HttpUrlRequest;
import com.naruto.mobile.framework.rpc.myhttp.transport.http.HttpWorker;
//import com.alipay.mobile.common.transport.TransportCallback;
//import com.alipay.mobile.common.transport.http.HttpManager;
//import com.alipay.mobile.common.transport.http.HttpUrlRequest;
//import com.alipay.mobile.common.transport.http.HttpWorker;

/**
 * HTTP下载管理器
 * 
 * 支持断点续传
 * 
 * @author sanping.li@alipay.com
 *
 */
public class DownloadManager extends HttpManager {
    /**
     * @param context 上下文
     */
    public DownloadManager(Context context) {
        super(context);
    }

    /**
     * 添加下载任务
     * 
     * @param url 下载地址
     * @param path 保存的路径
     * @param headers 需要的HTTP请求头
     * @param callback 回调处理
     * @return 
     */
    public Future<?> addDownload(String url, String path, ArrayList<Header> headers,
                            TransportCallback callback) {
        if (headers == null) {
            headers = new ArrayList<Header>();
        }
        HttpUrlRequest request = new DownloadRequest(url, path, null, headers);
        request.setTransportCallback(callback);
        return execute(request);
    }

    public Future<?> addDownload(DownloadRequest request){
        return execute(request);
    }

    protected HttpWorker generateWorker(HttpUrlRequest rpcHttpRequest) {
        return new DownloadWorker(this, rpcHttpRequest);
    }
}
