package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.download;

import android.os.Bundle;
import android.text.TextUtils;
//import com.alipay.mobile.common.transport.Request;
//import com.alipay.mobile.common.transport.Response;
//import com.alipay.mobile.common.transport.TransportCallback;
//import com.alipay.mobile.common.transport.http.HttpUrlResponse;
//import com.alipay.mobile.framework.LauncherApplicationAgent;
//import com.alipay.mobile.framework.MicroApplicationContext;
//import com.alipay.mobile.framework.service.common.DownloadService;
import org.apache.http.HttpStatus;

import java.io.File;
import java.util.concurrent.Future;

import com.naruto.mobile.base.serviceaop.NarutoApplication;
import com.naruto.mobile.framework.rpc.myhttp.transport.Request;
import com.naruto.mobile.framework.rpc.myhttp.transport.Response;
import com.naruto.mobile.framework.rpc.myhttp.transport.TransportCallback;
import com.naruto.mobile.framework.rpc.myhttp.transport.http.HttpUrlResponse;
import com.naruto.mobile.framework.service.common.DownloadService;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.exception.DjangoClientException;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.resp.ThumbnailsDownResp;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.util.DjangoConstant;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.util.LiteStringUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.ImageLoadReq;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.ExceptionUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Logger;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.UCLogUtil;

public class NetDownloader implements ImageDownloader<ThumbnailsDownResp>, TransportCallback {

    private Logger logger = Logger.getLogger("NetDownloader");

    private ImageLoadReq loadReq;
    private TransportCallback transportCallback;
    private DownloadService downloadService;
    private String path;
    private long size;

    public NetDownloader(ImageLoadReq req, String savePath, TransportCallback transportCallback) {
        this.loadReq = req;
        this.path = savePath;
        this.transportCallback = transportCallback;
    }

    public DownloadService getDownloadService() {
//        LauncherApplicationAgent launcherApplicationAgent = LauncherApplicationAgent.getInstance();
//        if (launcherApplicationAgent == null) {
//            return null;
//        }
//        MicroApplicationContext microApplicationContext = launcherApplicationAgent.getMicroApplicationContext();
//        if (microApplicationContext == null) {
//            return null;
//        }
        return NarutoApplication.getInstance().getNarutoApplicationContext().findServiceByInterface(DownloadService.class.getName());
    }



    @Override
    public ThumbnailsDownResp download(ImageLoadReq req, Bundle extra) {
        ThumbnailsDownResp response = new ThumbnailsDownResp();
        String url = req.path;
        Response rsp = null;
        HttpUrlResponse httpResponse = null;
        String tmpPath = this.path + "." + System.currentTimeMillis();
        File tmpFile = new File(tmpPath);

        long start = 0;
        try {
            if (LiteStringUtils.isBlank(url)) {
                throw new DjangoClientException("url can not be null");
            }
            downloadService = getDownloadService();
            if (downloadService == null) {
                throw new RuntimeException("DownloadService can not be null");
            }
            start = System.currentTimeMillis();
            Future<?> future = downloadService.addDownload(url, tmpPath, null, this);
            try {
                rsp = (Response) future.get();
            } catch (InterruptedException e) {}

            if (rsp != null) {
                httpResponse = (HttpUrlResponse) rsp;
                File saveFile = new File(path);

                size = tmpFile.length();
                if (httpResponse.getCode() == HttpStatus.SC_OK
                        /*|| httpResponse.getCode() == HttpStatus.SC_PARTIAL_CONTENT*/) {

                    if (saveFile.exists() && saveFile.isFile()) {
                        boolean deleted = saveFile.delete();
                        logger.d("downloadImage success, delete exists file: " + saveFile + ", deleted: " + deleted);
                    }
                    boolean success = tmpFile.renameTo(saveFile);
                    logger.d("saveFile source:" + url + ", dst: " + saveFile + ", len: " + saveFile.length() + ", ret? " + success);
                    if (success) {
                        response.setCode(DjangoConstant.DJANGO_OK);
                        response.setSavePath(path);
                    } else {
                        response.setCode(DjangoConstant.DJANGO_400);
                    }
                    response.setData(rsp.getResData());
                } else {
                    response.setCode(httpResponse.getCode());
                    response.setMsg("Http invoker error :" + response.getCode());
                    logger.e("download err, path: " + req.path + ", code: " + response.getCode() + ", msg: " + ((HttpUrlResponse) rsp).getMsg());
                }
            } else {
                response.setCode(DjangoConstant.DJANGO_400);
                response.setMsg("httpManager execute return null");
                logger.e("download err, path: " + req.path + ", code: " + response.getCode() + ", msg: " + response.getMsg());
            }
        } catch (Exception e) {
            logger.e(e, "download error: " + e.getMessage());
            response.setCode(DjangoConstant.DJANGO_400);
            response.setMsg(e.getMessage());

            ExceptionUtils.checkAndResetDiskCache(e);
        } finally {
            boolean deleted = tmpFile.delete();
            logger.d("downloadImage finally delete tmpFile: " + tmpFile + ", deleted? " + deleted + ", size:" + size);
            UCLogUtil.UC_MM_C04(String.valueOf(response.getCode()), size, (int) (System.currentTimeMillis() - start),
                    "url", 2, false, response.getMsg(), "");
            Logger.TIME("NetDownloader costTime " + (System.currentTimeMillis() - start));
        }
        return response;
    }

    @Override
    public void cancel() {

    }

    @Override
    public void onCancelled(Request request) {
        if (transportCallback != null) {
            transportCallback.onCancelled(request);
        }
    }

    @Override
    public void onPreExecute(Request request) {
        if (transportCallback != null) {
            transportCallback.onPreExecute(request);
        }
    }

    @Override
    public void onPostExecute(Request request, Response response) {
        if (transportCallback != null) {
            transportCallback.onPostExecute(request, response);
        }
    }

    @Override
    public void onProgressUpdate(Request request, double v) {
        if (transportCallback != null) {
            transportCallback.onProgressUpdate(request, v);
        }
    }

    @Override
    public void onFailed(Request request, int i, String s) {
        logger.e("onFailed path: " + loadReq.path + ", i: " + i + ", s: " + s);
        if (transportCallback != null) {
            transportCallback.onFailed(request, i, s);
        }
    }
}