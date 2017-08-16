package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.download;

//import com.alipay.mobile.common.transport.Request;
//import com.alipay.mobile.common.transport.Response;
//import com.alipay.mobile.common.transport.TransportCallback;
import com.naruto.mobile.framework.rpc.myhttp.transport.Request;
import com.naruto.mobile.framework.rpc.myhttp.transport.Response;
import com.naruto.mobile.framework.rpc.myhttp.transport.TransportCallback;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.ImageLoadReq;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Logger;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class HttpTransListener implements TransportCallback {

    private Logger logger = Logger.getLogger("HttpTransListener");

    private Set<ImageLoadReq> loadReqs;
    private int mProgress = -1;

    public HttpTransListener(Set<ImageLoadReq> loadReqs) {
        this.loadReqs = Collections.synchronizedSet(new HashSet<ImageLoadReq>(loadReqs));
    }

    @Override
    public void onCancelled(Request request) {
        logger.i("onCancelled " + request);
    }

    @Override
    public void onPreExecute(Request request) {
        logger.i("onPreExecute " + request);
    }

    @Override
    public void onPostExecute(Request request, Response response) {
        logger.i("onPostExecute " + request);
    }

    @Override
    public void onProgressUpdate(Request request, double percent) {
        int progress = (int) (100 * percent);
        if (mProgress == progress) {
            return;
        }
        if (mProgress <= 1 || mProgress >= 99) {
            logger.d("onProgressUpdate " + request + ", percent: " + percent);
        } else {
            logger.p("onProgressUpdate " + request + ", percent: " + percent);
        }

        mProgress = progress;
//        onProgress(mProgress);
        if (loadReqs != null && !loadReqs.isEmpty()) {
            for (ImageLoadReq loadReq : loadReqs) {
                if (loadReq.downLoadCallback != null) {
                    loadReq.downLoadCallback.onProcess(loadReq.source, mProgress);
                }
            }
        }
    }

    @Override
    public void onFailed(Request request, int code, String msg) {
        logger.i("onFailed " + request + ", code: " + code + ", msg: " + msg);
    }
}