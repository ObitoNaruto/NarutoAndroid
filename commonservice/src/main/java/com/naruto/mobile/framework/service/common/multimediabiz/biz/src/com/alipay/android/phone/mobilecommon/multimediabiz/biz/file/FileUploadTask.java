package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.file;

import android.content.Context;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.naruto.mobile.framework.service.common.multimedia.api.data.APMultimediaTaskModel;
import com.naruto.mobile.framework.service.common.multimedia.file.APFileUploadCallback;
import com.naruto.mobile.framework.service.common.multimedia.file.data.APFileReq;
import com.naruto.mobile.framework.service.common.multimedia.file.data.APFileRsp;
import com.naruto.mobile.framework.service.common.multimedia.file.data.APFileUploadRsp;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.DjangoClient;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.io.ChunkTransferredListener;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.io.TransferredListener;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.req.ChunkUpTxnCommitReq;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.req.ChunkUpTxnOpenReq;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.req.ChunkUpTxnProcessReq;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.req.FileUpReq;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.resp.ChunkUpTxnCommitResp;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.resp.ChunkUpTxnOpenResp;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.resp.ChunkUpTxnProcessResp;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.resp.FileUpResp;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.util.DjangoUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.util.MD5Utils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Logger;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.UCLogUtil;

public class FileUploadTask extends FileTask {
    private static final String TAG = FileUploadTask.class.getSimpleName();
    public static final long BIG_FILE_SIZE_THRESHOLD = 10 * 1024 * 1024;
    private static final int MAX_CHUNK_THREAD_NUM = 1;
    private APFileUploadCallback callback;

    private AtomicInteger lastProgress = new AtomicInteger(-1);
    private AtomicLong chunkTotalUploadByte = new AtomicLong(0);

    private String traceId;

    private Logger logger = Logger.getLogger(TAG);
    private boolean bServerCode = false;

    public FileUploadTask(Context context, List reqList, APMultimediaTaskModel taskInfo,
            APFileUploadCallback callback) {
        super(context, reqList, taskInfo);
        this.callback = callback;
    }

    private APFileUploadRsp uploadSync(final List reqList, final APFileUploadCallback callback) {
        logger.d("uploadSync start reqList size = " + reqList.size());
        if (callback != null) {
            callback.onUploadStart(taskInfo);
        }
        APFileUploadRsp rsp = new APFileUploadRsp();
        try {
            uploadToDjango(reqList, callback, rsp);
        } catch (RuntimeException e1) {
            if (TASK_CANCELED.equals(e1.getMessage())) {
                rsp.setRetCode(APFileRsp.CODE_ERR_TASK_CANCELED);
                rsp.setMsg(e1.getMessage());
            } else {
                logger.e(e1, "");
                rsp.setRetCode(APFileRsp.CODE_ERR_EXCEPTION);
                rsp.setMsg(e1.getMessage());
            }
        } catch (Exception e) {
            logger.e(e, "");
            rsp.setRetCode(APFileRsp.CODE_ERR_EXCEPTION);
            rsp.setMsg(e.getMessage());
        }
        // 如果cancel导致了其他的exception, 需要在这里设置下error code, 防止DownloadListener remove task
        if (isCanceled() || APFileRsp.CODE_ERR_TASK_CANCELED == taskInfo.getStatus()) {
            rsp.setRetCode(APFileRsp.CODE_ERR_TASK_CANCELED);
            rsp.setMsg(TASK_CANCELED);
        }
        if (callback != null) {
            if (APFileRsp.CODE_SUCCESS == rsp.getRetCode()) {
                callback.onUploadFinished(taskInfo, rsp);
            } else if (APFileRsp.CODE_ERR_TASK_CANCELED == rsp.getRetCode()) {
                //todo: add onCancel
                callback.onUploadError(taskInfo, rsp);
            } else {
                callback.onUploadError(taskInfo, rsp);
            }
        }
        return rsp;
    }

    private void uploadToDjango(List reqList, final APFileUploadCallback callback, APFileUploadRsp rsp)
            throws Exception {
        APFileReq info = (APFileReq) reqList.get(0);
        uploadSingleToDjango(info, rsp, callback);
    }

    private void uploadSingleToDjango(APFileReq info, APFileUploadRsp rsp, final APFileUploadCallback callback)
            throws Exception {
        DjangoClient djangoClient = null;
        long start = System.currentTimeMillis();
        long size = 0;
        String exp = null;
        String md5 = null;
        try {
            File file = new File(info.getSavePath());
            final long totalBytes = file.length();
            size = totalBytes;
            if (totalBytes > 0) {
                try {
                    md5 = MD5Utils.getFileMD5String(file);
                    if (TextUtils.isEmpty(md5)) {
                        rsp.setRetCode(APFileRsp.CODE_ERR_FILE_MD5_WRONG);
                        rsp.setMsg("fileUpResp calc md5 fail");
                        return;
                    }
                } catch (IOException e) {
                    logger.e(e, "");
                    rsp.setRetCode(APFileRsp.CODE_ERR_FILE_MD5_WRONG);
                    rsp.setMsg("fileUpResp calc md5 exception");
                    return;
                }
                checkCanceled();
                djangoClient = getDjangoClient(info.getRequestParam());
                if (totalBytes >= BIG_FILE_SIZE_THRESHOLD) {
                    uploadBigFile(info, rsp, callback, djangoClient, file, md5);
                } else {
                    uploadSmallFile(info, rsp, callback, djangoClient, file, md5);
                }
                exp = rsp.getMsg();
            } else {
                rsp.setRetCode(APFileRsp.CODE_ERR_FILE_SIZE_ZERO);
                rsp.setMsg("local file size is zero");
            }
        } catch (Exception e) {
            logger.e(e, "");
            exp = e.getMessage();
            rsp.setRetCode(APFileRsp.CODE_ERR_EXCEPTION);
            throw e;
        } finally {
            if (djangoClient != null && djangoClient.getConnectionManager() != null) {
                djangoClient.getConnectionManager().shutdown();
            }
            String ret = String.valueOf(rsp.getRetCode());
            String logMd5 = null;
            if (APFileRsp.CODE_SUCCESS != rsp.getRetCode()) {
                logMd5 = md5;
            } else {
                exp = null;
                bServerCode = false;
            }
            UCLogUtil.UC_MM_C03(bServerCode ? ("s" + ret) : ret, size, (int) (System.currentTimeMillis() - start),
                    exp, traceId, logMd5);
        }
    }

    private void uploadSmallFile(APFileReq info, APFileUploadRsp rsp, final APFileUploadCallback callback,
            final DjangoClient djangoClient, File file, String md5) {
        final long totalBytes = file.length();
        FileUpReq fileUpReq = new FileUpReq(file, new TransferredListener() {
            @Override
            public void onTransferred(long transferredCount) {
                // Log.i(TAG, "uploadSync onTransferred transferredCount: " + transferredCount);
                if (isCanceled()) {
                    if (djangoClient != null && djangoClient.getConnectionManager() != null) {
                        djangoClient.getConnectionManager().shutdown();
                    }
                }
                checkCanceled();
                int progress = (int) (transferredCount * 100.0f / totalBytes);
                if (lastProgress.get() != progress) {//lastProgress != progress //todo:
                    logger.d("onTransferred progress:  " + progress + ", name: " + name);
                    lastProgress.set(progress);
                    if (callback != null) {
                        callback.onUploadProgress(taskInfo, progress, transferredCount, totalBytes);
                    }
                }
            }
        });
        fileUpReq.setMd5(md5);
        fileUpReq.setExt(DjangoUtils.getExtension(file.getName()));

        checkCanceled();

        FileUpResp fileUpResp = djangoClient.getFileApi().uploadDirect(fileUpReq);
        logger.d("uploadSmallFile fileUpResp: " + fileUpResp);
        if (fileUpResp == null) {
            rsp.setRetCode(APFileRsp.CODE_ERR_RSP_NULL);
        } else if (fileUpResp.isSuccess()) {
            traceId = fileUpResp.getTraceId();
            if (fileUpResp.isRapid() || (fileUpResp.getFileInfo() != null
//                        && fileUpResp.getFileInfo().getSize() == totalBytes
                    && fileUpReq.getMd5().equalsIgnoreCase(fileUpResp.getFileInfo().getMd5()))) {
                rsp.setRetCode(fileUpResp.getCode());
                rsp.setMsg(fileUpResp.getMsg());

                info.setCloudId(fileUpResp.getFileInfo().getId());
                rsp.setFileReq(info);
                String cacheFilePath = addCacheFile(info);
                taskInfo.setDestPath(cacheFilePath);
            } else {
                rsp.setRetCode(APFileRsp.CODE_ERR_FILE_SIZE_WRONG);
                rsp.setMsg("size not match");
            }
        } else {
            traceId = fileUpResp.getTraceId();
            bServerCode = true;
            rsp.setRetCode(fileUpResp.getCode());
            rsp.setMsg(fileUpResp.getMsg());
        }

        if (APFileRsp.CODE_SUCCESS != rsp.getRetCode()) {
            logger.d("uploadSmallFile " + file + ", md5: " + md5 + ", length: " + totalBytes);
        }
    }

    private void uploadBigFile(APFileReq info, APFileUploadRsp rsp, final APFileUploadCallback callback,
            DjangoClient djangoClient, File file, String md5)
            throws InterruptedException {
        final long totalBytes = file.length();
        ChunkUpTxnOpenReq openReq = new ChunkUpTxnOpenReq(totalBytes);
        openReq.setMd5(md5);
        openReq.setExtension(DjangoUtils.getExtension(file.getName()));
        long chunkSize = openReq.getChunkSize();
        //设置了ChunkSize可以不要chunkNum
//        int chunkNum = (int) Math.ceil(totalBytes * 1.0f / chunkSize);
//        openReq.setNumber(chunkNum);
//        openReq.setSize(totalBytes);
        ChunkUpTxnOpenResp openResp = djangoClient.getFileApi().uploadChunkOpen(openReq);
        logger.d("uploadBigFile openResp: " + openResp);
        if (openResp == null) {
            rsp.setRetCode(APFileRsp.CODE_ERR_RSP_NULL);
            rsp.setMsg("openResp null");
        } else if (openResp.isSuccess()) {
            String fileId = openResp.getFileInfo().getId();
            if (TextUtils.isEmpty(fileId)) {
                rsp.setRetCode(APFileRsp.CODE_ERR_FILE_SIZE_ZERO);
                rsp.setMsg("fileId empty");
                return;
            }
            if (openResp.getFileInfo().getStatus() == 20/* && md5.equalsIgnoreCase(openResp.getFileInfo().getMd5())*/) {
                rsp.setRetCode(APFileRsp.CODE_SUCCESS);
                rsp.setMsg(openResp.getMsg());
                info.setCloudId(fileId);
                rsp.setFileReq(info);
                String cacheFilePath = addCacheFile(info);
                taskInfo.setDestPath(cacheFilePath);
                return;
            }
            if (!doChunkUpTxnProgress(rsp, djangoClient, file, chunkSize,
                    openResp.getFileInfo().getChunkNumber(), fileId, callback)) {
                logger.d("uploadBigFile chunk " + file + ", md5: " + md5 + ", length: " + totalBytes);
                return;
            }

            ChunkUpTxnCommitReq commitReq = new ChunkUpTxnCommitReq(fileId);
            ChunkUpTxnCommitResp commitResp = djangoClient.getFileApi().uploadChunkCommit(commitReq);
            logger.d("uploadBigFile commitResp: " + commitResp);
            if (commitResp == null) {
                rsp.setRetCode(APFileRsp.CODE_ERR_RSP_NULL);
                rsp.setMsg("commitResp null");
            } else if (commitResp.isSuccess()) {
                //todo:md5 not match
                //对于10块之内的文件返回的MD5是正真MD5，对于超过10块（40M）的文件拿到的是临时MD5(基本是唯一的，基于文件各个块信息计算出来的)
                if (commitResp.getFileInfo() != null /*&& md5.equalsIgnoreCase(commitResp.getFileInfo().getMd5())*/) {
                    rsp.setRetCode(APFileRsp.CODE_SUCCESS);
                    rsp.setMsg(openResp.getMsg());
                    info.setCloudId(fileId);
                    rsp.setFileReq(info);
                    String cacheFilePath = addCacheFile(info);
                    taskInfo.setDestPath(cacheFilePath);
                } else {
                    rsp.setRetCode(APFileRsp.CODE_ERR_FILE_SIZE_WRONG);
                    rsp.setMsg("commit fileInfo null");
                }
            } else {
                bServerCode = true;
                rsp.setRetCode(commitResp.getCode());
                rsp.setMsg(commitResp.getMsg());
            }
        } else {
            bServerCode = true;
            rsp.setRetCode(openResp.getCode());
            rsp.setMsg(openResp.getMsg());
        }
        if (APFileRsp.CODE_SUCCESS != rsp.getRetCode()) {
            logger.d("uploadBigFile " + file + ", md5: " + md5 + ", length: " + totalBytes);
        }
    }

    private boolean doChunkUpTxnProgress(APFileUploadRsp rsp, final DjangoClient djangoClient, File file,
            long chunkSize, int chunkNum, String fileId, final APFileUploadCallback callback)
            throws InterruptedException {
        final long totalBytes = file.length();
        Map<String, String> chunkUpMd5Map = new ConcurrentHashMap<String, String>();
        for (int seq = 1; seq <= chunkNum; seq++) {
            try {
                String chunkMd5 = MD5Utils.getFileChunkMD5String(file, seq, chunkSize);
                if (!TextUtils.isEmpty(chunkMd5)) {
                    chunkUpMd5Map.put(String.valueOf(seq), chunkMd5);
                } else {
                    break;
                }
            } catch (IOException e) {
                logger.e(e, "");
                break;
            }
        }
        if (chunkUpMd5Map.size() != chunkNum) {
            rsp.setRetCode(APFileRsp.CODE_ERR_FILE_MD5_WRONG);
            rsp.setMsg("TxnPro md5 error");
            return false;
        }


        List<String> failList = new ArrayList<String>();

        Map<String, ChunkUpTxnProcessResp> processRespMap = new ConcurrentHashMap<String, ChunkUpTxnProcessResp>();
        Semaphore latch = new Semaphore(MAX_CHUNK_THREAD_NUM);
        CountDownLatch commitLatch = new CountDownLatch(chunkNum);
        for (int seq = 1; seq <= chunkNum; seq++) {
            ChunkUpTxnProcessReq processReq = new ChunkUpTxnProcessReq(fileId, file, seq);
            processReq.setMd5(chunkUpMd5Map.get(String.valueOf(seq)));
            processReq.setChunkNumber(chunkNum);
            processReq.setChunkSize(chunkSize);
            if (seq == chunkNum) {
                processReq.setRealChunkSize(totalBytes - ((chunkNum - 1) * chunkSize));
            } else {
                processReq.setRealChunkSize(chunkSize);
            }
            processReq.setChunkTransListener(new ChunkTransListener(djangoClient, totalBytes, callback));
            if (MAX_CHUNK_THREAD_NUM > 1) {//并发上传
                latch.acquire();
                new ChunkUpThread(djangoClient, seq, processReq, processRespMap, latch, commitLatch).start();
            } else { //挨个上传
                ChunkUpTxnProcessResp processResp = djangoClient.getFileApi().uploadChunkProcess(processReq);
                processRespMap.put(String.valueOf(seq), processResp);
                if (!processResp.isSuccess()) {
                    break;
                }
            }
        }
        if (MAX_CHUNK_THREAD_NUM > 1) {
            commitLatch.await();
        }

        if (isCanceled()) {
            rsp.setRetCode(APFileRsp.CODE_ERR_TASK_CANCELED);
            rsp.setMsg("task canceled");
            return false;
        }

        int errCode = APFileRsp.CODE_SUCCESS;
        for (String seqString : processRespMap.keySet()) {
            ChunkUpTxnProcessResp processResp = processRespMap.get(seqString);
            logger.d("uploadBigFile seq: " + seqString + ", processResp: " + processResp);
            if (!processResp.isSuccess()) {
                logger.d("seq: " + seqString + ", processResp: " + processResp.getCode()
                        + ", " + processResp.getMsg());
                failList.add(seqString);
                errCode = processResp.getCode();
            } else if (!chunkUpMd5Map.get(seqString).equalsIgnoreCase(processResp.getData().getMd5())) {
                failList.add(seqString);
                errCode = APFileRsp.CODE_ERR_FILE_MD5_WRONG;
            }
        }
        if (!failList.isEmpty()) {
            rsp.setRetCode(errCode);
            rsp.setMsg("some chunk fail, " + failList);
            return false;
        }
        return true;
    }

    @Override
    public APFileUploadRsp call() throws Exception {
        return uploadSync(fileReqList, callback);
    }

    class ChunkUpThread extends Thread {

        private DjangoClient djangoClient;
        private int seq;
        private ChunkUpTxnProcessReq processReq;
        private Map<String, ChunkUpTxnProcessResp> processRespMap;
        private Semaphore latch;
        private CountDownLatch commitLatch;

        public ChunkUpThread(DjangoClient djangoClient, int seq, ChunkUpTxnProcessReq processReq,
                Map<String, ChunkUpTxnProcessResp> processRespMap, Semaphore latch, CountDownLatch commitLatch) {
            this.djangoClient = djangoClient;
            this.seq = seq;
            this.processReq = processReq;
            this.processRespMap = processRespMap;
            this.latch = latch;
            this.commitLatch = commitLatch;
        }

        @Override
        public void run() {
            logger.d("ChunkUpThread run seq: " + seq);
            ChunkUpTxnProcessResp processResp = new ChunkUpTxnProcessResp();
            try {
                if (isCanceled()) {
                    processResp.setCode(APFileRsp.CODE_ERR_TASK_CANCELED);
                    processResp.setMsg("task canceled");
                    return;
                }
                processResp = djangoClient.getFileApi().uploadChunkProcess(processReq);
            } catch (Exception e) {
                logger.e(e, "chuck up exception seq: " + seq);
                processResp.setCode(APFileRsp.CODE_ERR_EXCEPTION);
                processResp.setMsg("ChunkUpThread exception");
            } finally {
                logger.d("ChunkUpThread run finish seq: " + seq);
                latch.release();
                commitLatch.countDown();
                processRespMap.put(String.valueOf(seq), processResp);
            }
        }
    }

    private class ChunkTransListener implements ChunkTransferredListener {
        private DjangoClient djangoClient;
        private long totalBytes;
        private APFileUploadCallback callback;
        private long chunkLastUpBytes;

        public ChunkTransListener(DjangoClient djangoClient, long totalBytes, APFileUploadCallback callback) {
            this.djangoClient = djangoClient;
            this.totalBytes = totalBytes;
            this.chunkLastUpBytes = 0;
            this.callback = callback;
        }

        @Override
        public void onChunkTransferred(int chunkSequence, long transferredCount) {
            if (isCanceled()) {
                if (djangoClient != null && djangoClient.getConnectionManager() != null) {
                    djangoClient.getConnectionManager().shutdown();
                }
            }
            checkCanceled();
            //TODO: 内部做重试时 修正重试块的进度
            long alreadyUpBytes = chunkTotalUploadByte.addAndGet((transferredCount - chunkLastUpBytes));
            int progress = (int) (alreadyUpBytes * 100.0f / totalBytes);
            chunkLastUpBytes = transferredCount;
            if (lastProgress.get() != progress) { //lastProgress != progress
                logger.d("onChunkTransferred progress:  " + progress + ", name: " + name);
                lastProgress.set(progress);
                if (callback != null) {
                    callback.onUploadProgress(taskInfo, progress, chunkTotalUploadByte.get(), totalBytes);
                }
            }
        }
    }
}
