package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.file;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import org.apache.http.client.HttpClient;

import java.io.*;
import java.util.List;
import java.util.concurrent.Callable;

import com.naruto.mobile.framework.service.common.multimedia.api.data.APMultimediaTaskModel;
import com.naruto.mobile.framework.service.common.multimedia.audio.data.APRequestParam;
import com.naruto.mobile.framework.service.common.multimedia.file.data.APFileReq;
import com.naruto.mobile.framework.service.common.multimedia.file.data.APFileRsp;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.DjangoClient;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.config.ConnectionManager;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.exception.DjangoClientException;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.impl.HttpConnectionManager;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.impl.HttpDjangoClient;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.io.IOUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.util.DjangoConstant;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.util.DjangoUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.FileUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Logger;

public class FileTask implements Callable<APFileRsp> {
    private static final String TAG = FileTask.class.getSimpleName();
    private Context mContext;
    protected List fileReqList;
    protected APMultimediaTaskModel taskInfo;

    private ClearFileStrategy clearFileStrategy;

    protected static final String TASK_CANCELED = "multimedia_file_task_canceled";
    private String mBaseDir;
    private final APRequestParam defaultRequestParam = new APRequestParam("ACL", "UID");
    private DjangoClient djangoClient;
    private volatile boolean mNeedStopFlag = false;

    private Logger logger = Logger.getLogger(TAG);
    protected String name;

    protected FileTask(Context context, List reqList, APMultimediaTaskModel taskInfo) {
        this.mContext = context;
        this.fileReqList = reqList;
        this.taskInfo = taskInfo;
        this.clearFileStrategy = new SimpleClearFileStrategy(context);
    }

    // 需要 needSize 大小的空间, 不够就删点
    protected void clearOldFileIfNotEnough(long needSize) {
        clearFileStrategy.clear(needSize);
    }

    protected void deleteFile(String savePath) {
        try {
//            FileUtils.deleteFileByPath(savePath);
        } catch (Exception e) {
            // ignore
        }
    }

    protected boolean copyFile(String srcPath, String destPath) {
        logger.d("copyFile from " + srcPath + " to " + destPath);
        if (TextUtils.isEmpty(srcPath) || TextUtils.isEmpty(destPath) || srcPath.equals(destPath)) {
            return false;
        }

        File src = new File(srcPath);
        File dst = new File(destPath);
        if ((src.exists() && src.isFile()) && (!dst.exists() || !dst.isFile())) {
            InputStream input = null;
            OutputStream out = null;
            try {
                dst.getParentFile().mkdirs();
                input = new FileInputStream(src);
                out = new FileOutputStream(dst);
                IOUtils.copy(input, out);
                return true;
            } catch (IOException e) {
                logger.e(e, "");
            } finally {
                IOUtils.closeQuietly(input);
                IOUtils.closeQuietly(out);
            }
        }
        return false;
    }

    protected boolean removeCacheFile(APFileReq req) {
        logger.d("removeCacheFile req: " + req);
        if (req != null && !TextUtils.isEmpty(req.getCloudId())) {
            String path = getCachePathByCloudId(req);
            if (!TextUtils.isEmpty(path)) {
                deleteFile(path);
                return true;
            }
        }
        return false;
    }

    protected String addCacheFile(APFileReq req) {
        logger.d("addCacheFile req: " + req);
        if (req != null && !TextUtils.isEmpty(req.getCloudId()) && !TextUtils.isEmpty(req.getSavePath())) {
            File originalFile = new File(req.getSavePath());
            if (originalFile.exists()) {
                clearOldFileIfNotEnough(originalFile.length());
                String cachePath = getCachePathByCloudId(req);
                if (copyFile(req.getSavePath(), cachePath)) {
                    return cachePath;
                }
            }
        }
        return "";
    }

    private String getCachePathByCloudId(APFileReq req) {
        if (req == null || TextUtils.isEmpty(req.getCloudId())) {
            return null;
        }
        return getBaseDir() + File.separator + req.getCloudId();
    }

    protected String checkCacheFile(APFileReq req) {
        logger.d("checkCacheFile req: " + req);
        String cachePath = getCachePathByCloudId(req);
        if (TextUtils.isEmpty(cachePath)) {
            return null;
        }
        File dst = new File(cachePath);
        if ((dst.exists() && dst.isFile())) {
            logger.i("checkCacheFile return true ");
            return cachePath;
        }
        return null;
    }

    private String getBaseDir() {
        if (TextUtils.isEmpty(mBaseDir)) {
            try {
                mBaseDir = DjangoUtils.getMediaDir(mContext, DjangoConstant.FILE_PATH);
            } catch (DjangoClientException e) {
                mBaseDir = mContext.getFilesDir().getAbsolutePath() + File.separator + DjangoConstant.FILE_PATH;
            }
        }
        File baseDir = new File(mBaseDir);
        if (!baseDir.exists() || !baseDir.isDirectory()) {
            baseDir.mkdirs();
        }
        logger.i("getBaseDir mBaseDir: " + mBaseDir);
        return mBaseDir;
    }

    protected String getSavePath(APFileReq req) {
        logger.d("generateSavePath info: " + req);
        String path;
        if (req != null && !TextUtils.isEmpty(req.getSavePath())) {
            path = req.getSavePath();
        } else if (req != null && !TextUtils.isEmpty(req.getCloudId())) {
            path = getCachePathByCloudId(req);
        } else {
            path = getBaseDir() + File.separator + System.currentTimeMillis();
        }
        File pathFile = new File(path);
        if (!pathFile.getParentFile().exists()) {
            boolean ret = pathFile.getParentFile().mkdirs();
            logger.i("generateSavePath mkdirs return : " + ret);
        }
        logger.d("generateSavePath path: " + path);
        return path;
    }

    protected synchronized DjangoClient getDjangoClient(APRequestParam param) {
        if (djangoClient == null) {
            if (param == null) {
                param = defaultRequestParam;
            }
            ConnectionManager<HttpClient> conMgr = new HttpConnectionManager();
            if (param != null) {
                conMgr.setAcl(param.getACL());
                conMgr.setUid(param.getUID());
            }
            djangoClient = new HttpDjangoClient(mContext, conMgr);
            //todo: add APP_KEY APP_SECRET?
//            conMgr.setAppKey(APP_KEY);
//            conMgr.setAppSecret(APP_SECRET);
        }
        return djangoClient;
    }

    protected boolean isCanceled() {
        boolean ret = mNeedStopFlag || Thread.currentThread().isInterrupted();
        if (ret) {
            //确保任务状态变成cancel
            cancel();
        }
        return ret;
    }

    protected void cancel() {
        mNeedStopFlag = true;
        if (taskInfo != null) {
            taskInfo.setStatus(APMultimediaTaskModel.STATUS_CANCEL);
        }
    }

    protected void checkCanceled() {
        if (isCanceled()) {
            throw new RuntimeException(TASK_CANCELED);
        }
    }

    @Override
    public APFileRsp call() throws Exception {
        return null;
    }

    public void setName(String name) {
        this.name = name;
    }
}

