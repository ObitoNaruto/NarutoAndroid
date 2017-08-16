package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.file;

import android.content.Context;
import org.apache.http.HttpStatus;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.naruto.mobile.framework.service.common.multimedia.api.data.APMultimediaTaskModel;
import com.naruto.mobile.framework.service.common.multimedia.file.APFileUploadCallback;
import com.naruto.mobile.framework.service.common.multimedia.file.data.APFileReq;
import com.naruto.mobile.framework.service.common.multimedia.file.data.APFileRsp;
import com.naruto.mobile.framework.service.common.multimedia.file.data.APFileUploadRsp;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.api.FileApi;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.io.TransferredListener;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.req.FileRapidUpReq;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.req.FileUpReq;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.resp.FileUpResp;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.util.MD5Utils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.CommonUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.FileUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Logger;

/**
 * 小文件弱网分片上传
 * Created by jinmin on 15/8/6.
 */
public class FileSliceUploadTask extends FileTask {

    private static final String TAG = "FileSliceUploadTask";

    private Logger logger = Logger.getLogger(TAG);

    private APFileUploadCallback mCallback;
    private AtomicLong mUploadSize;
    private Options mOptions;
    private long totalSize;
    private int progress;
    private int fileIndex = 0;

    public FileSliceUploadTask(Context context, List reqList, Options options, APMultimediaTaskModel taskInfo, APFileUploadCallback cb) {
        super(context, reqList, taskInfo);
        this.mCallback = cb;
        this.mUploadSize = new AtomicLong(0);
        this.mOptions = options;
    }


    @Override
    public APFileRsp call() throws Exception {
        calculateTotalSize();
        APFileUploadRsp rsp = null;
        FileUpResp upResp = null;
        notifyUploadStart();
        for (Object o : fileReqList) {
            logger.d("progress fileIndex: " + fileIndex + ", req: " + o);
            fileIndex++;
            APFileReq req = (APFileReq)o;
            try {
                upResp = doSliceUpload(req);
                if (upResp == null) {
                    throw new NullPointerException("doSliceUpload error, get null!");
                }
            } catch (Exception e) {
                logger.e(e, "doSliceUpload err req: " + req);
                rsp = new APFileUploadRsp();
                if (e instanceof NullPointerException) {
                    rsp.setRetCode(APFileRsp.CODE_ERR_RSP_NULL);
                } else {
                    rsp.setRetCode(APFileRsp.CODE_ERR_EXCEPTION);
                }
                rsp.setMsg(e.getMessage());
                rsp.setFileReq(req);
                rsp.setTraceId("");
                notifyUploadError(rsp);
                break;
            }

            if (!upResp.isSuccess()) {
                logger.e("doSliceUpload fail req: " + req + ", upResp: " + upResp);
                rsp = new APFileUploadRsp();
                rsp.setRetCode(upResp.getCode());
                rsp.setMsg(upResp.getMsg());
                rsp.setFileReq(req);
                rsp.setTraceId(upResp.getTraceId());
                notifyUploadError(rsp);
                break;
            }
        }

        if (rsp == null && upResp != null) {
            rsp = new APFileUploadRsp();
            rsp.setRetCode(APFileRsp.CODE_SUCCESS);
            APFileReq req = (APFileReq) fileReqList.get(fileIndex-1);
            rsp.setFileReq(req);
            req.setCloudId(upResp.getFileInfo().getId());
            rsp.setTraceId(upResp.getTraceId());
            notifyUploadSuccess(rsp);
        }
        return rsp;
    }

    private FileUpResp doSliceUpload(APFileReq req) throws Exception {
        String md5 = null;
        File upFile = new File(req.getSavePath());
        try {
            md5 = MD5Utils.getFileMD5String(upFile);
        } catch (IOException e) {
            logger.e(e, "doSliceUpload getMd5 fail: " + req);
            throw e;
        }
        FileApi fileApi = getDjangoClient(req.getRequestParam()).getFileApi();
        //秒传和已上传range检测
        FileUpResp rsp = checkRapidRange(fileApi, md5, req);
        if (rsp != null) {
            if (!rsp.isRapid() && !rsp.isSuccess()) {//不是秒传，从结果开始进行断点续上传
                SliceTransferListener listener = new SliceTransferListener();
                mUploadSize.set(rsp.getRange());
                do {
                    int offset = rsp.getRange();
                    listener.reset();
                    rsp = sliceUpload(fileApi, offset, req, md5, listener);
                } while (rsp != null && rsp.getCode() == HttpStatus.SC_PARTIAL_CONTENT);
            }
        }
        return rsp;
    }

    private FileUpResp checkRapidRange(FileApi api, String md5, APFileReq req) {
        FileRapidUpReq rapidUpReq = new FileRapidUpReq(md5, null);
        rapidUpReq.setExt(FileUtils.getSuffix(req.getSavePath()));
        FileUpResp rsp = api.uploadRapidRange(rapidUpReq);
        return rsp;
    }

    private FileUpResp sliceUpload(FileApi api, int offset, APFileReq req, String md5, TransferredListener listener) {
        File file = new File(req.getSavePath());
        FileUpReq upReq = new FileUpReq(file, listener);
        if (APFileReq.FILE_TYPE_COMPRESS_IMAGE.equals(req.getType())) {
            upReq.setExt(".jpg");
            upReq.setFileName(file.getName() + ".jpg");
        } else /*if (APFileReq.FILE_TYPE_IMAGE.equals(req.getType()))*/ {
            upReq.setFileName(file.getName());
            upReq.setExt(FileUtils.getSuffix(file.getName()));
        }
        upReq.setMd5(md5);
        upReq.setStartPos(offset);
        int endPos = CommonUtils.isWifiNetwork() ? (int)file.length()-1 : offset + mOptions.sliceSize;
        if (endPos >= file.length()) {
            endPos = (int) (file.length()-1);
        }
        upReq.setEndPos(endPos);
        FileUpResp resp = null;
        try {
            resp = api.uploadRange(upReq);
        } catch (Exception e) {
            logger.e(e, "sliceUpload err, upReq: " + upReq);
            resp = new FileUpResp();
            resp.setCode(APFileRsp.CODE_ERR_EXCEPTION);
            resp.setMsg(e.getMessage());
            resp.setTraceId("");
        }
        return resp;
    }

    private void calculateTotalSize() {
        totalSize = 0;
        for (Object o : fileReqList) {
            APFileReq req = (APFileReq)o;
            totalSize += new File(req.getSavePath()).length();
            logger.d("calculateTotalSize req.file: " + req.getSavePath() + ", curTotalSize: " + totalSize);
        }
    }

    private void notifyUploadStart() {
        logger.d("notifyUploadStart req: " + fileReqList.get(0));
        if (mCallback != null) {
            mCallback.onUploadStart(taskInfo);
        }
    }

    private void notifyUploadProgress() {
        if (mCallback != null) {
            int curProgress = (int) (mUploadSize.longValue() * 100 / totalSize);
            if (curProgress != progress) {
                progress = curProgress;
                mCallback.onUploadProgress(taskInfo, progress, mUploadSize.get(), totalSize);
            }
        }
    }

    private void notifyUploadSuccess(APFileUploadRsp rsp) {
        logger.d("notifyUploadSuccess rsp: " + rsp);
        if (mCallback != null) {
            mCallback.onUploadFinished(taskInfo, rsp);
        }
    }

    private void notifyUploadError(APFileUploadRsp rsp) {
        logger.d("notifyUploadError rsp: " + rsp);
        if (mCallback != null) {
            mCallback.onUploadError(taskInfo, rsp);
        }
    }

    private class SliceTransferListener implements TransferredListener {

        private long pre = 0;
        @Override
        public void onTransferred(long transferredCount) {
            mUploadSize.addAndGet(transferredCount-pre);
            pre = transferredCount;
            notifyUploadProgress();
        }

        public void reset() {
            pre = 0;
        }
    }

}
