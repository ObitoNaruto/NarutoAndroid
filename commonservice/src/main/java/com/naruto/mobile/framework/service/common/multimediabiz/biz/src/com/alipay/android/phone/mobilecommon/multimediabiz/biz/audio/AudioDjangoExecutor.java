package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.audio;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
//import com.alipay.android.phone.mobilesdk.storage.utils.FileUtils;
import org.apache.http.client.HttpClient;

import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import com.naruto.mobile.framework.service.common.multimedia.api.MultimediaFileService;
import com.naruto.mobile.framework.service.common.multimedia.api.data.APMultimediaTaskModel;
import com.naruto.mobile.framework.service.common.multimedia.audio.APAudioDownloadCallback;
import com.naruto.mobile.framework.service.common.multimedia.audio.APAudioUploadCallback;
import com.naruto.mobile.framework.service.common.multimedia.audio.data.APAudioDownloadRsp;
import com.naruto.mobile.framework.service.common.multimedia.audio.data.APAudioInfo;
import com.naruto.mobile.framework.service.common.multimedia.audio.data.APAudioRsp;
import com.naruto.mobile.framework.service.common.multimedia.audio.data.APAudioUploadRsp;
import com.naruto.mobile.framework.service.common.multimedia.audio.data.APAudioUploadState;
import com.naruto.mobile.framework.service.common.multimedia.audio.data.APRequestParam;
import com.naruto.mobile.framework.service.common.multimedia.file.APFileDownCallback;
import com.naruto.mobile.framework.service.common.multimedia.file.APFileUploadCallback;
import com.naruto.mobile.framework.service.common.multimedia.file.data.APFileDownloadRsp;
import com.naruto.mobile.framework.service.common.multimedia.file.data.APFileReq;
import com.naruto.mobile.framework.service.common.multimedia.file.data.APFileUploadRsp;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.audio.utils.AudioUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.DjangoClient;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.api.FileApi;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.config.ConnectionManager;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.impl.HttpConnectionManager;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.impl.HttpDjangoClient;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.io.DownloadResponseHelper;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.io.IOUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.io.TransferredListener;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.req.FilesDownReq;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.req.InputStreamUpReq;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.resp.FileUpResp;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.resp.FilesDownResp;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.file.FileUpLoadManager;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.file.Options;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.AppUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.CacheUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.CommonUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.FileUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Logger;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.UCLogUtil;

/**
 * 对接Django服务器API
 * Created by jinmin on 15/4/1.
 */
public class AudioDjangoExecutor {
    private static final String TAG = AudioDjangoExecutor.class.getSimpleName();

    private static final int BUFFER_SIZE = 4096;

    private static AudioDjangoExecutor sInstance;

    private Logger logger = Logger.getLogger("AudioDjangoExecutor");

    private Context mContext;
    //    private DjangoClient mDjangoClient;
    private DownloadResponseHelper mDownloadRspHelper;
    //    private FileApi mFileApi;
    private ExecutorService mExecutor = Executors.newCachedThreadPool();
    private Set<String> mDownloadingIds = new HashSet<String>();

    private MultimediaFileService mFileService;

    private AudioDjangoExecutor(Context context) {
        this.mContext = context;
        mFileService = AppUtils.getFileService();
        initDjango();
    }

    private void initDjango() {
        mDownloadRspHelper = new DownloadResponseHelper();
    }

    public synchronized static AudioDjangoExecutor getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new AudioDjangoExecutor(context);
        }
        return sInstance;
    }

    public void uploadDirect(APAudioInfo info, APRequestParam param, APAudioUploadCallback cb) {
        mExecutor.execute(new UploadTask(cb, info, param));
    }

    public UploadIntervalTask uploadAudioInterval(APAudioInfo info, APRequestParam param, UploadIntervalListener listener) {
        UploadIntervalTask task = new UploadIntervalTask(info, param, listener);
        mExecutor.execute(task);
        return task;
    }

    public APMultimediaTaskModel download(APAudioInfo info, APRequestParam param, APAudioDownloadCallback listener) {
        APMultimediaTaskModel task = null;
        if (fromCache(info)) {
            if (listener != null) {
                info.getExtra().putBoolean("notifyDownloadFinished", false);
                listener.onDownloadFinished(info);
            }
            return task;
        }
        String savePath = AudioFileManager.getInstance(mContext).makeSaveFilePath(info);
        APAudioFileDownloadCallback callback = new APAudioFileDownloadCallback(info, listener);
        APFileReq req = new APFileReq();
        req.setCloudId(info.getCloudId());
        req.setIsNeedCache(false);
        if (!TextUtils.isEmpty(savePath)) {
            req.setSavePath(savePath);
        }
        task = mFileService.downLoad(req, callback);
        /*if (task == null) {
            task = new APMultimediaTaskModel();
            task.setCloudId(info.getCloudId());
            task.setTaskId(info.getCloudId());
            task.setDestPath(info.getSavePath());
        }*/
        return task;
    }

    public APAudioDownloadRsp download(APAudioInfo info, APRequestParam param) {
        final APAudioDownloadRsp rsp = new APAudioDownloadRsp();
        APAudioDownloadCallback listener = new APAudioDownloadCallback() {
            @Override
            public void onDownloadStart(APAudioInfo info) {

            }

            @Override
            public void onDownloadFinished(APAudioInfo info) {
                rsp.setRetCode(APAudioRsp.CODE_SUCCESS);
                rsp.setAudioInfo(info);
            }

            @Override
            public void onDownloadError(APAudioInfo info, APAudioDownloadRsp rsp) {
                rsp.setAudioInfo(info);
                rsp.setRetCode(rsp.getRetCode());
                rsp.setMsg(rsp.getMsg());
            }
        };
        new DownloadTask(info, param, listener).run();
        return rsp;
    }

    public void cancelDownload(APAudioInfo info) {
        APMultimediaTaskModel task = mFileService.getUpTaskStatusByCloudId(info.getCloudId());
        if (task != null) {
            mFileService.cancelLoad(task.getTaskId());
        }
    }

    //================================

    private FileApi getFileApi(APRequestParam param) {
        ConnectionManager<HttpClient> conMgr = new HttpConnectionManager();
        if (param != null) {
            conMgr.setAcl(param.getACL());
            conMgr.setUid(param.getUID());
        }
        DjangoClient client = new HttpDjangoClient(mContext, conMgr);
        return client.getFileApi();
    }

    private void copyToCache(APAudioInfo info) {
        AudioFileManager audioFileManager = AudioFileManager.getInstance(mContext);
        File src = new File(info.getSavePath());
        if (!TextUtils.isEmpty(info.getCloudId())) {
            File dst = new File(audioFileManager.getBaseDir() + File.separator + info.getCloudId());
            if ((src.exists() && src.isFile()) && (!dst.exists() || !dst.isFile())) {
                copyAudio(src, dst);
                info.setSavePath(dst.getAbsolutePath());
            }
        }
        //清理LocalCache
        if (TextUtils.isEmpty(info.getLocalId())) {
            File local = new File(audioFileManager.getBaseDir() + File.separator + info.getLocalId());
            if (local.exists() && local.isFile()) {
                local.delete();
            }
        }
    }

    private void copyToLocalCache(APAudioInfo info) {
        AudioFileManager audioFileManager = AudioFileManager.getInstance(mContext);
        File src = null;
        if (!TextUtils.isEmpty(info.getSavePath())) {
            src = new File(info.getSavePath());
        }
        if (!TextUtils.isEmpty(info.getLocalId())) {
            File dst = new File(audioFileManager.getBaseDir() + File.separator + info.getLocalId());
            if ((src != null && src.exists() && src.isFile()) && (!dst.exists() || !dst.isFile())) {
                copyAudio(src, dst);
            }
            info.setSavePath(dst.getAbsolutePath());
        }
    }

    private void copyAudio(File src, File dst) {
        src.renameTo(dst);
    }

    public APAudioUploadRsp uploadDirectSync(APAudioInfo info, APRequestParam param) {
        return uploadSync(info, param);
    }

    private class UploadTask implements Runnable {

        private APAudioInfo mAudioInfo;
        private APRequestParam mReqParam;
        private APAudioUploadCallback mCallback;

        public UploadTask(APAudioUploadCallback callback, APAudioInfo info, APRequestParam param) {
            this.mCallback = callback;
            this.mAudioInfo = info;
            this.mReqParam = param;
        }

        public void run() {
            if (mCallback != null) {
                mCallback.onUploadStart(mAudioInfo);
            }
            APAudioUploadRsp rsp = uploadSync(mAudioInfo, mReqParam);
            if (rsp.getRetCode() == 0) {
                mCallback.onUploadFinished(rsp);
            } else {
                mCallback.onUploadError(rsp);
            }
        }
    }

    public class UploadIntervalTask implements Runnable {
        private static final boolean USE_DELAY_MIN_RECORD_TIME = true;
        private final AtomicBoolean cancelled = new AtomicBoolean(false);
        private APAudioInfo mAudioInfo;
        private APRequestParam mReqParam;
        private UploadIntervalListener mListener;
        private PipedInputStream pipedInputStream;
        private OutputStream outputStream;
        private AtomicBoolean hasError = new AtomicBoolean(false);
        private AtomicBoolean hasClosed = new AtomicBoolean(false);

        public UploadIntervalTask(APAudioInfo mAudioInfo, APRequestParam mReqParam, UploadIntervalListener mListener) {
            this.mAudioInfo = mAudioInfo;
            this.mReqParam = mReqParam;
            this.mListener = mListener;

            preparedUpStream();
        }

        public void notifyStop() {
            if (!hasClosed.get()) {
                try {
                    if (!hasError.get()) {
                        outputStream.flush();
                    }
                } catch (IOException e) {
//                    logger.e(e, "notifyStop");
                    hasError.set(true);
                } finally {
                    if (!hasError.get()) {
                        IOUtils.closeQuietly(outputStream);
                    }
                }
            }
        }

        public void cancel() {
            if (USE_DELAY_MIN_RECORD_TIME) {
                logger.p("cancel");
                cancelled.set(true);
                synchronized (cancelled) {
                    cancelled.notifyAll();
                }
            }
        }

        public OutputStream getTaskOutput() {
            return outputStream;
        }

        private void preparedUpStream() {
            try {
                pipedInputStream = new PipedInputStream();
                PipedOutputStream pipedOutputStream = new PipedOutputStream();
                pipedOutputStream.connect(pipedInputStream);
                outputStream = new BufferedOutputStream(pipedOutputStream, 4192);
            } catch (Exception e) {
                hasError.set(true);
            }
        }

        /**
         * 当明确上传成功时，调用此方法，把本地文件转换为cloud 文件
         */
        public void copyToCacheWhileSuccess() {
            copyToCache(mAudioInfo);
        }

        @Override
        public void run() {
            if (pipedInputStream != null) {
                try {
                    InputStreamUpReq req = new InputStreamUpReq(pipedInputStream, mAudioInfo.getLocalId()+".amr", new TransferredListener() {
                        @Override
                        public void onTransferred(long transferredCount) {
//                            logger.p("onTransferred transferredCount: " + transferredCount);
                            if (mListener != null) {
                                mListener.onUploadProgress(mAudioInfo, transferredCount);
                            }
                        }
                    }, -1);
                    req.setExt(".amr");
                    req.setSkipRapid(true);
                    FileApi api = getFileApi(mReqParam);
                    if (USE_DELAY_MIN_RECORD_TIME) {
                        try {
                            synchronized (cancelled) {
                                cancelled.wait(mAudioInfo.getRecordMinTime() + 200);
                            }
                        } catch (Exception e) {/*ignore*/}
                    }
                    hasError.set(!CommonUtils.isActiveNetwork(mContext));
                    FileUpResp rsp = !hasError.get() && !cancelled.get() ? api.uploadDirect(req) : null;
                    logger.d("UploadIntervalTask rsp: " + rsp);
                    if (mListener != null) {
                        if (rsp != null && rsp.isSuccess() && !hasError.get()) {
                            mAudioInfo.setCloudId(rsp.getFileInfo().getId());
                            mAudioInfo.setFileMD5(rsp.getFileInfo().getMd5());
                            mListener.onUploadFinished(mAudioInfo);
                        } else {
                            if (rsp == null) {
                                rsp = new FileUpResp();
                                rsp.setCode(APAudioUploadRsp.CODE_SYNC_UPLOAD_ERROR);
                                if (cancelled.get()) {
                                    rsp.setMsg("less than min record time, cancel upload!!");
                                } else {
                                    rsp.setMsg("network is not active!!");
                                }
                            }
                            logger.e("uploadError rsp: " + rsp);
                            mListener.onUploadError(mAudioInfo, rsp);
                        }
                    }
                } catch (Exception e) {
                    logger.e(e, "UploadIntervalTask");
                    FileUpResp rsp = new FileUpResp();
                    rsp.setCode(APAudioUploadRsp.CODE_SYNC_UPLOAD_ERROR);
                    rsp.setMsg(e.getClass().getSimpleName() + ":" + e.getMessage());
                    if (mListener != null) {
                        mListener.onUploadError(mAudioInfo, rsp);
                    }
                } finally {
                    IOUtils.closeQuietly(pipedInputStream);
                    hasClosed.set(true);
                }
            }
        }
    }


    private APAudioUploadRsp uploadSync(APAudioInfo audioInfo, APRequestParam param) {
        logger.d("uploadSync info: " + audioInfo + ", param: " + param);
        long start = System.currentTimeMillis();
        copyToLocalCache(audioInfo);
        long size = new File(audioInfo.getSavePath()).length();
        FileUpLoadManager mgr = FileUpLoadManager.getInstance(mContext);
        APFileReq req = new APFileReq();
        req.setSavePath(audioInfo.getSavePath());
        req.setIsNeedCache(false);
        Options options = new Options();
        options.uploadType = Options.UPLOAD_TYPE_SLICE;
        final APFileUploadRsp uploadRsp = new APFileUploadRsp();
        final CountDownLatch latch = new CountDownLatch(1);
        mgr.upLoad(req, options, new APFileUploadCallback() {
            @Override
            public void onUploadStart(APMultimediaTaskModel taskInfo) {

            }

            @Override
            public void onUploadProgress(APMultimediaTaskModel taskInfo, int progress, long hasUploadSize, long total) {

            }

            @Override
            public void onUploadFinished(APMultimediaTaskModel taskInfo, APFileUploadRsp rsp) {
                fillUploadRsp(rsp);
                latch.countDown();
            }

            private void fillUploadRsp(APFileUploadRsp rsp) {
                uploadRsp.setFileReq(rsp.getFileReq());
                uploadRsp.setRetCode(rsp.getRetCode());
                uploadRsp.setMsg(rsp.getMsg());
            }

            @Override
            public void onUploadError(APMultimediaTaskModel taskInfo, APFileUploadRsp rsp) {
                fillUploadRsp(rsp);
                latch.countDown();
            }
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            logger.e(e, "uploadSync");
            APAudioUploadRsp cbRsp = new APAudioUploadRsp();
            cbRsp.setAudioInfo(audioInfo);
            cbRsp.setRetCode(APAudioUploadRsp.CODE_FILE_UPLOAD_ERROR);
            cbRsp.setMsg(e.getMessage());
            return cbRsp;
        }


        APAudioUploadRsp cbRsp = null;
        String traceId = null;
        try {
            traceId = uploadRsp.getTraceId();

            cbRsp = new APAudioUploadRsp();
            cbRsp.setAudioInfo(audioInfo);
            cbRsp.setRetCode(uploadRsp.getRetCode());
            cbRsp.setMsg(uploadRsp.getMsg());

            if (uploadRsp.getRetCode() == APAudioUploadRsp.CODE_SUCCESS) {
                audioInfo.setCloudId(uploadRsp.getFileReq().getCloudId());
                copyToCache(audioInfo);
                audioInfo.setUploadState(new APAudioUploadState(APAudioUploadState.STATE_SUCCESS));
            } else {
                cbRsp.extCode = cbRsp.getRetCode();
                cbRsp.extMsg = cbRsp.getMsg();
                audioInfo.setUploadState(new APAudioUploadState(APAudioUploadState.STATE_ERROR));
                cbRsp.setRetCode(APAudioUploadRsp.CODE_SYNC_UPLOAD_ERROR);
            }
        } finally {
            int code = uploadRsp.getRetCode();
            String exp = uploadRsp.getMsg();
            UCLogUtil.UC_MM_C02(code, size, (int) (System.currentTimeMillis() - start), audioInfo.getDuration(), traceId, exp);
        }
        cbRsp.recordState = APAudioUploadRsp.STATE_RECORD_FINISHED;
        logger.d("uploadSync APAudioUploadRsp: " + cbRsp);
        return cbRsp;
    }

    //===================== Download ======================

    private class DownloadTask implements Runnable {
        private APAudioInfo mAudioInfo;
        private APRequestParam mReqParam;
        private APAudioDownloadCallback mListener;

        public DownloadTask(APAudioInfo info, APRequestParam param, APAudioDownloadCallback listener) {
            this.mAudioInfo = info;
            this.mReqParam = param;
            this.mListener = listener;
        }

        public void run() {
            if (fromCache(mAudioInfo)) {
                if (mListener != null) {
                    mAudioInfo.getExtra().putBoolean("notifyDownloadFinished", false);
                    mListener.onDownloadFinished(mAudioInfo);
                }
                mDownloadingIds.remove(mAudioInfo.getCloudId());
                return;
            }
            logger.d("DownloadTask audioInfo: " + mAudioInfo);
            FilesDownReq req = new FilesDownReq(mAudioInfo.getCloudId());
            logger.d("DownloadTask req: " + req);
            if (mListener != null) {
                mListener.onDownloadStart(mAudioInfo);
            }
            FilesDownResp rsp = getFileApi(mReqParam).downloadBatch(req);
            APAudioDownloadRsp audioDownloadRsp = new APAudioDownloadRsp();
            logger.d("DownloadTask rsp: " + rsp);
            if (rsp != null) {
                if (rsp.isSuccess()) {//获取下载成功
                    String output = AudioFileManager.getInstance(mContext).getBaseDir() +
                            File.separator + mAudioInfo.getCloudId();
                    logger.d("DownloadTask cache cloud file path: " + output);
                    try {
                        InputStream input = rsp.getResp().getEntity().getContent();
                        FileOutputStream fos = new FileOutputStream(output);
                        mDownloadRspHelper.writeSingleFile(input, fos);
                        mAudioInfo.setSavePath(output);
                        if (mListener != null) {
                            mListener.onDownloadFinished(mAudioInfo);
                        }
                    } catch (Exception e) {
                        logger.e(e, "DownloadTask");
                        audioDownloadRsp.setMsg(e.getMessage());
                        if (mListener != null) {
                            mListener.onDownloadError(mAudioInfo, audioDownloadRsp);
                        }
                    }
                } else {
                    audioDownloadRsp.setRetCode(rsp.getCode());
                    audioDownloadRsp.setMsg(rsp.getMsg());
                    if (mListener != null) {
                        mListener.onDownloadError(mAudioInfo, audioDownloadRsp);
                    }
                }
            } else if (mListener != null) {
                audioDownloadRsp.setRetCode(APAudioRsp.CODE_ERROR);
                audioDownloadRsp.setMsg("No FilesDownResp");
                mListener.onDownloadError(mAudioInfo, audioDownloadRsp);
            }
            mDownloadingIds.remove(mAudioInfo.getCloudId());
        }

    }

    private class APAudioFileDownloadCallback implements APFileDownCallback {

        private APAudioInfo mAudioInfo;
        private APAudioDownloadCallback mCallback;

        public APAudioFileDownloadCallback(APAudioInfo audioInfo, APAudioDownloadCallback callback) {
            this.mAudioInfo = audioInfo;
            this.mCallback = callback;
        }

        @Override
        public void onDownloadStart(APMultimediaTaskModel taskInfo) {
            if (mCallback != null) {
                mCallback.onDownloadStart(mAudioInfo);
            }
        }

        @Override
        public void onDownloadProgress(APMultimediaTaskModel taskInfo, int progress, long hasDownSize, long total) {

        }

        @Override
        public void onDownloadBatchProgress(APMultimediaTaskModel taskInfo, int progress, int curIndex, long hasDownSize, long total) {

        }

        @Override
        public void onDownloadFinished(APMultimediaTaskModel taskInfo, APFileDownloadRsp rsp) {
            if (mCallback != null) {
                mAudioInfo.setSavePath(rsp.getFileReq().getSavePath());
                if (AudioUtils.checkSilkAudioFile(mAudioInfo.getSavePath())) {
                    mCallback.onDownloadFinished(mAudioInfo);
                } else {
                    logger.e("onDownloadFinished , but the file is error silk file!!");
//                    FileUtils.deleteFileByPath(mAudioInfo.getSavePath());
                    mAudioInfo.setSavePath("");
                    rsp.setRetCode(-1);
                    rsp.setMsg("Download finished, but the file is error silk file!!");
                    onDownloadError(taskInfo, rsp);
                }
            }
        }

        @Override
        public void onDownloadError(APMultimediaTaskModel taskInfo, APFileDownloadRsp rsp) {
            if (mCallback != null) {
                APAudioDownloadRsp downloadRsp = new APAudioDownloadRsp();
                downloadRsp.setMsg(rsp.getMsg());
                downloadRsp.setRetCode(rsp.getRetCode());
                mCallback.onDownloadError(mAudioInfo, downloadRsp);
            }
        }
    }

    public boolean fromCache(APAudioInfo info) {
        AudioFileManager audioFileManager = AudioFileManager.getInstance(mContext);
        boolean fromCache = false;
        File localCache =  null;
        if (!TextUtils.isEmpty(info.getLocalId())) {
            localCache = new File(audioFileManager.getBaseDir(), info.getLocalId());
        }
        File savePath = null;
        if (!TextUtils.isEmpty(info.getSavePath())) {
            savePath = new File(info.getSavePath());
        }
        File cloudCache = null;
        if (!TextUtils.isEmpty(info.getCloudId())) {
            cloudCache = new File(audioFileManager.getBaseDir(), info.getCloudId());
        }
        //Cache读取优先级： Cloud -> LocalPath -> LocalId-tmp
        if (isCacheFile(cloudCache)) {
            info.setSavePath(cloudCache.getAbsolutePath());
            fromCache = true;
        } else if (isCacheFile(savePath)) {
            copyToCache(info);
            fromCache = true;
        } else if (isCacheFile(localCache)) {
            info.setSavePath(localCache.getAbsolutePath());
            fromCache = true;
        }
        logger.d("fromCache: " + fromCache + ", filePath: " + info.getSavePath());
        return fromCache;
    }

    private boolean isCacheFile(File file) {
        return CacheUtils.checkCacheFile(file);
    }

    public interface UploadIntervalListener {
        void onUploadProgress(APAudioInfo info, long send);
        boolean onUploadFinished(APAudioInfo info);
        void onUploadError(APAudioInfo info, FileUpResp rsp);
    }
}
