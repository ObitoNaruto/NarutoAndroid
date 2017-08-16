package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.file;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
//import com.alipay.android.phone.mobilesdk.storage.file.BaseFile;
//import com.alipay.android.phone.mobilesdk.storage.utils.FileUtils;
import org.apache.http.HttpStatus;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.naruto.mobile.framework.service.common.multimedia.api.data.APMultimediaTaskModel;
import com.naruto.mobile.framework.service.common.multimedia.file.APFileDownCallback;
import com.naruto.mobile.framework.service.common.multimedia.file.data.APFileDownloadRsp;
import com.naruto.mobile.framework.service.common.multimedia.file.data.APFileReq;
import com.naruto.mobile.framework.service.common.multimedia.file.data.APFileRsp;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.DjangoClient;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.io.DownloadResponseHelper;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.io.IOUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.io.TransferredListener;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.io.output.ProgressOutputStream;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.req.FilesDownReq;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.resp.FilesDownResp;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.file.valid.FileValidStrategy;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.file.valid.ImageValidStrategy;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.io.InputProgressListener;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.io.ProgressInputStream;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.ExceptionUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.FileUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Logger;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.UCLogUtil;

public class FileDownloadTask extends FileTask {
    private static final String TAG = FileDownloadTask.class.getSimpleName();

    private static final String DOWNLOAD_FILE_TEMP_SUFFIX = ".dltmp";
    private final String savePath;
    private final String tmpSavePath;
    private APFileDownCallback callback;
    private DownloadResponseHelper mDownloadRspHelper;

    private Logger logger = Logger.getLogger(TAG);

    //当前下载第x个
    private int curIndex;
    //已下载xx
    private long hasDownloadSize;
    //tmp file length
    private long rangeSize = 0;

    private int lastProgress = -1;

    private AtomicBoolean bFinish = new AtomicBoolean(false);

    public FileDownloadTask(Context context, List reqList, APMultimediaTaskModel taskInfo,
            APFileDownCallback callback) {
        super(context, reqList, taskInfo);
        this.callback = callback;
        mDownloadRspHelper = new DownloadResponseHelper();
        APFileReq req = (APFileReq) reqList.get(0);
        this.savePath = getSavePath(req);
        this.tmpSavePath = this.savePath + DOWNLOAD_FILE_TEMP_SUFFIX;
        taskInfo.setDestPath(tmpSavePath);
    }

    public APFileDownloadRsp downloadSync(final List reqList, final APFileDownCallback callback) {
        logger.i("downloadSync start req size =  " + reqList.size());
        logger.d("downloadSync start cur thread id: " + Thread.currentThread().getId());
        APFileDownloadRsp rsp = new APFileDownloadRsp();
        try {
            checkCanceled();
            if (callback != null) {
                callback.onDownloadStart(taskInfo);
            }
            final List noCacheList = getNoCacheFile(reqList, callback);
            if (noCacheList == null || noCacheList.isEmpty()) {
                rsp.setRetCode(APFileRsp.CODE_SUCCESS);
                rsp.setMsg("down complete from cache");
                rsp.setFileReq((APFileReq) reqList.get(0));
            } else {
                requestDjangoFile(noCacheList, callback, rsp);
            }
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
        if (rsp.getFileReq() == null) {
            rsp.setFileReq((APFileReq) reqList.get(0));
        }
        if (callback != null) {
            if (APFileRsp.CODE_SUCCESS == rsp.getRetCode()) {
                callback.onDownloadFinished(taskInfo, rsp);
            } else if (APFileRsp.CODE_ERR_TASK_CANCELED == rsp.getRetCode()) {
                //todo: add onCancel
                callback.onDownloadError(taskInfo, rsp);
            } else {
                callback.onDownloadError(taskInfo, rsp);
            }
        }
        return rsp;
    }

    private void requestDjangoFile(final List reqList, final APFileDownCallback callback, APFileDownloadRsp rsp)
            throws Exception {
        //批量下载时 某些参数以第一个req为准
        APFileReq req = (APFileReq) reqList.get(0);
        FilesDownReq filesDownReq = new FilesDownReq(joinCloudIds("|", reqList));
        logger.i("getFileIds:  " + filesDownReq.getFileIds());
        final DjangoClient djangoClient = getDjangoClient(req.getRequestParam());

        InputStream input = null;
        FilesDownResp filesDownResp = null;
        long start = System.currentTimeMillis();
        long size = 0;
        String traceId = null;
        String exp = null;
        boolean bServerCode = false;
        try {
            bFinish.set(false);
            checkCanceled();
            if (reqList.size() <= 1) {
                File cacheFile = new File(tmpSavePath);
                if (cacheFile.isFile() && cacheFile.exists()) {
                    rangeSize = cacheFile.length();
                    logger.i("requestDjangoFile rangeSize = " + rangeSize);
                    if (rangeSize > 0) {
                        filesDownReq.setRange("bytes=" + rangeSize + "-");
                    }
                }
            }
            filesDownResp = djangoClient.getFileApi().downloadBatch(filesDownReq);
            logger.d("downloadBatch " + filesDownResp);
            if (filesDownResp == null) {
                rsp.setRetCode(APFileRsp.CODE_ERR_RSP_NULL);
                rsp.setMsg("filesDownResp null");
            } else if (filesDownResp.isSuccess()) {
                traceId = filesDownResp.getTraceId();
                input = filesDownResp.getResp().getEntity().getContent();
                final long totalLength;
                size = filesDownResp.getResp().getEntity().getContentLength();

                //todo: 初始化savepath时sdcard不可用, 在这又可用的情况
                // 尝试删点东西
                clearOldFileIfNotEnough(size);

                if (reqList.size() <= 1) {
                    if (filesDownResp.getCode() == HttpStatus.SC_OK) { // 不一致，需要重新下载
                        rangeSize = 0;
//                        FileUtils.deleteFileByPath(savePath);
//                        FileUtils.deleteFileByPath(tmpSavePath);
                    }
                    totalLength = size + rangeSize;
                    logger.i("downloadSync totalLength = " + totalLength + ", rangeSize = " + rangeSize);
                    handleDjangoSingleDownloadStream(req, callback,
                            new ProgressInputStream(input, new InputProgressListener() {
                                @Override
                                public void onReadProgress(int read, int totalRead) {
                                    if (isCanceled()) {
                                        try {
                                            djangoClient.getConnectionManager().shutdown();
                                        } catch (Exception e) {
                                            logger.e(e, "");
                                        }
                                    }
                                    checkCanceled();
                                }

                                @Override
                                public void onReadFinish(int totalRead) {
                                    logger.i("downloadSync onReadFinish totalRead = " + totalRead);
                                    if (totalRead > 0 && ((totalRead + rangeSize) == totalLength || totalLength <= 0)) {
                                        bFinish.set(true);
                                    }
                                }
                            }), totalLength);
                } else {
                    //todo: batch download content length is -1
                    totalLength = size;
                    logger.i("downloadSync totalLength = " + totalLength);
                    handleDjangoBatchDownloadStream(reqList, callback,
                            new ProgressInputStream(input, new InputProgressListener() {
                                @Override
                                public void onReadProgress(int read, int totalRead) {
                                    if (isCanceled()) {
                                        try {
                                            djangoClient.getConnectionManager().shutdown();
                                        } catch (Exception e) {
                                            logger.e(e, "");
                                        }
                                    }
                                }

                                @Override
                                public void onReadFinish(int totalRead) {
                                    logger.i("downloadSync onReadFinish totalRead = " + totalRead);
                                }
                            }), totalLength);
                    //todo: batch finish verify
                    bFinish.set(true);
                }
                logger.i("downloadSync bFinish = " + bFinish.get() + ", totalLength = " + totalLength);
                if (bFinish.get()) {
                    rsp.setRetCode(filesDownResp.getCode());
                    rsp.setMsg(filesDownResp.getMsg());
                    rsp.setFileReq(req);
                } else {
                    rsp.setRetCode(APFileRsp.CODE_ERR_FILE_SIZE_WRONG);
                    rsp.setMsg("size not match");
                }
            } else {
                traceId = filesDownResp.getTraceId();
                exp = filesDownResp.getMsg();
                bServerCode = true;
                rsp.setRetCode(filesDownResp.getCode());
                rsp.setMsg(filesDownResp.getMsg());
            }
        } catch (Exception e) {
            ExceptionUtils.checkAndResetDiskCache(e);
            logger.e(e, "");
            exp = e.getMessage();
            rsp.setRetCode(APFileRsp.CODE_ERR_EXCEPTION);
            throw e;
        } finally {
            IOUtils.closeQuietly(input);
            if (djangoClient != null) {
                djangoClient.release(filesDownResp);
                if (djangoClient.getConnectionManager() != null) {
                    djangoClient.getConnectionManager().shutdown();
                }
            }
            String ret = String.valueOf(rsp.getRetCode());
            String fileId = null;
            if (APFileRsp.CODE_SUCCESS != rsp.getRetCode()) {
                fileId = req.getCloudId();
            }
            UCLogUtil.UC_MM_C06(bServerCode ? ("s" + ret) : ret, size, (int) (System.currentTimeMillis() - start),
                    0, exp, traceId, fileId);
        }
    }

    // 处理多个文件下载的流
    private void handleDjangoBatchDownloadStream(final List reqList, final APFileDownCallback callback,
            InputStream input, final long totalLength) throws IOException {
        final HashMap<APFileReq, String> savePaths = new HashMap<APFileReq, String>();
        final HashMap<APFileReq, String> tmpSavePaths = new HashMap<APFileReq, String>();
        // todo:  totalLength = -1
        curIndex = 0;
        hasDownloadSize = 0;
        try {
            mDownloadRspHelper.writeBatchFiles(input,
                    new DownloadResponseHelper.ReadBatchFileRespCallback() {
                        @Override
                        public OutputStream onReadFile(DownloadResponseHelper.FileHeader fileHeader,
                                boolean isEmptyFile) {
                            logger.d("downloadSync onReadFile fileId: " + fileHeader.fileId);
                            APFileReq tmpReq = (APFileReq) reqList.get(curIndex);
                            tmpReq.setCloudId(fileHeader.fileId);

                            String savePath = getSavePath(tmpReq);
                            savePaths.put(tmpReq, savePath);
                            String tmpSavePath = savePath + DOWNLOAD_FILE_TEMP_SUFFIX;
                            tmpSavePaths.put(tmpReq, tmpSavePath);

                            deleteFile(tmpSavePath);
                            deleteFile(savePath);

                            curIndex++;
                            try {
                                return new ProgressOutputStream(
                                        new FileOutputStream(savePath),
                                        new TransferredListener() {

                                            @Override
                                            public void onTransferred(long transferredCount) {
//                                            logger.d("downloadSync batch onTransferred " +
//                                                    "transferredCount: " + transferredCount);
//                                            logger.d("downloadSync batch onTransferred " +
//                                                    "cur thread id: " + Thread.currentThread().getId());
//                                            logger.d("downloadSync batch onTransferred isInterrupted: " +
//                                                    Thread.currentThread().isInterrupted());
                                                checkCanceled();
                                                hasDownloadSize += transferredCount;
                                                if (callback != null) {
                                                    int progress;
                                                    if (totalLength > 0) {
                                                        progress = (int) (hasDownloadSize * 100.0f / totalLength);
                                                    } else {
                                                        progress = 0;
                                                    }
                                                    if (lastProgress != progress) {
                                                        lastProgress = progress;
                                                        callback.onDownloadBatchProgress(taskInfo, progress,
                                                                curIndex, hasDownloadSize, totalLength);
                                                    }
                                                }
                                            }
                                        });
                            } catch (IOException e) {
                                logger.e(e, "");
                            }
                            return null;
                        }
                    });
        } catch (RuntimeException e) {
//            if (e.getMessage().equals(TASK_CANCELED)) {
//                for (Object o : reqList) {
//                    APFileReq req = (APFileReq) o;
//                    deleteFile(tmpSavePaths.get(req));
//                }
//            }
            throw e;
        }
        for (Object o : reqList) {
            APFileReq req = (APFileReq) o;
            if (TextUtils.isEmpty(req.getSavePath())) {
                req.setSavePath(savePaths.get(req));
            }
            //把临时文件拷贝过去再删除
            copyFile(tmpSavePaths.get(req), savePaths.get(req));
            if (req.isNeedCache()) {
                addCacheFile(req);
            }
            deleteFile(tmpSavePaths.get(req));
        }
    }

    // 处理单个文件下载的流
    private void handleDjangoSingleDownloadStream(APFileReq req, final APFileDownCallback callback, InputStream input,
            final long totalLength) throws IOException {
//        deleteFile(savePath);
//        deleteFile(tmpSavePath);

        try {
            if (rangeSize > 0) {
                mDownloadRspHelper.writeSingleFile(input, new File(tmpSavePath), rangeSize, new TransferredListener() {
                    @Override
                    public void onTransferred(long transferredCount) {
                        onDownloadProgress(transferredCount, totalLength);
                    }
                });
            } else {
                FileOutputStream fileOutputStream = new FileOutputStream(tmpSavePath);
                OutputStream out = new ProgressOutputStream(fileOutputStream,
                        new TransferredListener() {

                            @Override
                            public void onTransferred(long transferredCount) {
//                        logger.d("downloadSync onTransferred  transferredCount: " + transferredCount);
//                        logger.d("downloadSync batch onTransferred " +
//                                "cur thread id: " + Thread.currentThread().getId());
//                        logger.d("downloadSync batch onTransferred isInterrupted: " +
//                                Thread.currentThread().isInterrupted());
                                onDownloadProgress(transferredCount, totalLength);
                            }
                        });
                mDownloadRspHelper.writeSingleFile(input, out);
            }
        } catch (RuntimeException e) {
//            if (e.getMessage().equals(TASK_CANCELED)) {
//                deleteFile(tmpSavePath);
//            }
            throw e;
        }

        //校验格式
        FileValidStrategy strategy = getFileValidStrategy(req.getType());
        if (strategy != null && !strategy.checkFileValid(tmpSavePath)) {
//            FileUtils.deleteFileByPath(tmpSavePath);
        }

        File tmpFile = new File(tmpSavePath);
        logger.i("tmpFile.length: " + tmpFile.length());
        if (bFinish.get() && tmpFile.exists() && tmpFile.isFile() && tmpFile.length() == totalLength) {
            String originSavePath = req.getSavePath();
            if (TextUtils.isEmpty(req.getSavePath())) {
                req.setSavePath(savePath);
            }
            //把临时文件拷贝过去再删除
//            FileUtils.moveFile(tmpFile, new BaseFile(savePath));
//            copyFile(tmpSavePath, savePath);
            // todo: maybe not need add cache file
            if (req.isNeedCache() && TextUtils.isEmpty(originSavePath)) {
                addCacheFile(req);
            }
            taskInfo.setDestPath(req.getSavePath());
//            deleteFile(tmpSavePath);
        } else {
            bFinish.set(false);
        }
    }

    private FileValidStrategy getFileValidStrategy(final String type) {
        if (TextUtils.isEmpty(type)) {
            return null;
        }
        if (APFileReq.FILE_TYPE_IMAGE.equalsIgnoreCase(type)) {
            return new ImageValidStrategy();
        }
        return null;
    }

    private void onDownloadProgress(long transferredCount, long totalLength) {
        checkCanceled();
        hasDownloadSize = rangeSize + transferredCount;
        if (callback != null) {
            int progress;
            if (totalLength > 0) {
                progress = (int) (hasDownloadSize * 100.0f / totalLength);
            } else {
                progress = 0;
            }
            if (lastProgress != progress) {
                logger.d("onDownloadProgress progress:  " + progress + ", name: " + name);
                lastProgress = progress;
                callback.onDownloadProgress(taskInfo, progress,
                        hasDownloadSize, totalLength);
            }
        }
    }

    private List getNoCacheFile(List reqList, APFileDownCallback callback) {
        List noCacheFileList = new ArrayList<APFileReq>();
        for (Object o : reqList) {
            APFileReq req = (APFileReq) o;
            String cachePath = checkCacheFile(req);
            if (!TextUtils.isEmpty(cachePath)) {
                if (TextUtils.isEmpty(req.getSavePath())) {
                    req.setSavePath(cachePath);
                } else {
                    copyFile(cachePath, req.getSavePath());
                }
                //todo: update curIndex hasDownloadSize, how to calculate total length
                //todo: callback.onprogress
            } else {
                noCacheFileList.add(req);
            }
        }
        return noCacheFileList;
    }

    private String joinCloudIds(CharSequence delimiter, List reqList) {
        StringBuilder sb = new StringBuilder();
        boolean firstTime = true;
        for (Object obj : reqList) {
            APFileReq req = (APFileReq) obj;
            if (firstTime) {
                firstTime = false;
            } else {
                sb.append(delimiter);
            }
            sb.append(req.getCloudId());
        }
        return sb.toString();
    }

    @Override
    public APFileDownloadRsp call() throws Exception {
        try {
            return downloadSync(fileReqList, callback);
        } catch (Exception e) {
            logger.e(e, "");
        }
        return null;
    }
}
