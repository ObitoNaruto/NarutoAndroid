package com.naruto.mobile.framework.service.common.impl;

import java.util.ArrayList;
import java.util.concurrent.Future;

import org.apache.http.Header;

import android.os.Bundle;

import com.naruto.mobile.base.serviceaop.NarutoApplication;
import com.naruto.mobile.framework.rpc.myhttp.transport.TransportCallback;
import com.naruto.mobile.framework.rpc.myhttp.transport.download.DownloadManager;
import com.naruto.mobile.framework.rpc.myhttp.transport.download.DownloadRequest;
import com.naruto.mobile.framework.service.common.DownloadService;
//import com.alipay.mobile.common.transport.TransportCallback;
//import com.alipay.mobile.common.transport.download.DownloadManager;
//import com.alipay.mobile.framework.LauncherApplicationAgent;
//import com.alipay.mobile.framework.service.common.DownloadService;
//import com.alipay.mobile.common.transport.download.DownloadRequest;
/**
 * DownloadServiceImpl
 * 
 * @author sanping.li@alipay.com
 *
 */
public class DownloadServiceImpl extends DownloadService {
    private DownloadManager mDownloadManager;

    public DownloadServiceImpl() {
        mDownloadManager = new DownloadManager(NarutoApplication.getInstance().getApplicationContext());
    }

    @Override
    @Deprecated
    public Future<?> addDownload(String url, final String path, ArrayList<Header> headers,
                            TransportCallback callback) {
       return mDownloadManager.addDownload(url, path, headers, callback);
    }
    
    @Override
    public Future<?> addDownload(DownloadRequest downloadRequest) {
       return mDownloadManager.addDownload(downloadRequest);
    }

    @Override
    protected void onCreate(Bundle params) {

    }

    @Override
    protected void onDestroy(Bundle params) {
        mDownloadManager.close();
    }

}
