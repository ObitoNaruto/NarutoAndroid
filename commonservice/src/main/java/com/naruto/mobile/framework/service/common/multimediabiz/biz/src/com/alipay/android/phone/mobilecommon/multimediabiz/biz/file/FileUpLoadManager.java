package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.file;

import android.content.Context;
import android.text.TextUtils;
//import com.alipay.android.phone.mobilesdk.storage.utils.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

import com.naruto.mobile.framework.service.common.multimedia.api.data.APMultimediaTaskModel;
import com.naruto.mobile.framework.service.common.multimedia.file.APFileDownCallback;
import com.naruto.mobile.framework.service.common.multimedia.file.APFileUploadCallback;
import com.naruto.mobile.framework.service.common.multimedia.file.data.APFileDownloadRsp;
import com.naruto.mobile.framework.service.common.multimedia.file.data.APFileReq;
import com.naruto.mobile.framework.service.common.multimedia.file.data.APFileRsp;
import com.naruto.mobile.framework.service.common.multimedia.file.data.APFileUploadRsp;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.io.IOUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.util.MD5Utils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.assist.DefaultConfigurationFactory;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.io.RepeatableBufferedInputStream;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.manager.APFileTaskManager;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.FileUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Logger;

public class FileUpLoadManager {
    private static final String TAG = FileUpLoadManager.class.getSimpleName();
    private static final String EMPTY_STRING = "";

    private static final String PREFIX_FILE_DOWNLOAD = "file_dl_";
    private static final String PREFIX_FILE_UPLOAD = "file_up_";

    private static FileUpLoadManager sInstance;

    private Context mContext;

    private APFileTaskManager taskManager;

    private ExecutorService mExecutor = DefaultConfigurationFactory.createLoadExecutor("mfile", true);//Executors.newCachedThreadPool();

    private Logger logger = Logger.getLogger(TAG);

    //保存所有的文件下载 callback, unregister时删除, cancel/完成/失败时删除
    private ConcurrentHashMap<String, Map> downloadCallbackMap = new ConcurrentHashMap<String, Map>();
    //保存所有的文件下载存储路径, unregister时不删除, cancel/完成/失败时删除
    private ConcurrentHashMap<String, Map> downloadInfoMap = new ConcurrentHashMap<String, Map>();
    private ConcurrentHashMap<String, Map> uploadCallbackMap = new ConcurrentHashMap<String, Map>();
    private ConcurrentHashMap<String, FutureTask> runningTask = new ConcurrentHashMap<String, FutureTask>();

    private FileUpLoadManager(Context context) {
        this.mContext = context;
        this.taskManager = APFileTaskManager.getInstance(mContext);
    }

    public synchronized static FileUpLoadManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new FileUpLoadManager(context);
        }
        return sInstance;
    }

    //todo:merge duplicate code
    public APMultimediaTaskModel downLoad(APFileReq info, APFileDownCallback callback) {
        logger.d("downLoad " + info);
        if (checkPreDownload(info, callback)) {
            return null;
        }

        String taskId = getDownloadTaskId(info.getCloudId());
        FutureTask fileTask = runningTask.get(taskId);
        if (fileTask != null) {
            logger.d("add new listener 1 " + fileTask);
            putDownloadReqInfo(taskId, info.getSavePath());
            registeLoadCallBack(taskId, callback);
            return getTaskRecord(taskId);
        }

        List<APFileReq> reqList = new ArrayList<APFileReq>();
        reqList.add(info);
        APMultimediaTaskModel taskInfo = createTaskRecord(PREFIX_FILE_DOWNLOAD, info);
        FileTask newFileTask = new FileDownloadTask(mContext, reqList, taskInfo, new FileDownLoadListener());
        FileFutureTask ft = new FileFutureTask(newFileTask);
        newFileTask.setName(ft.toString());

        FutureTask existFileTask = runningTask.putIfAbsent(taskId, ft);
        if (existFileTask == null) {
            logger.d("submit task " + ft);
            taskInfo.setSourcePath(info.getCloudId());
            taskInfo.setCloudId(info.getCloudId());
            addTaskRecord(taskInfo);
            putDownloadReqInfo(taskId, info.getSavePath());
            registeLoadCallBack(taskId, callback);
            Future f = mExecutor.submit(ft);
            //f.get == ft.get??
            return taskInfo;
        } else {
            logger.d("add new listener 2 " + existFileTask);
            putDownloadReqInfo(taskId, info.getSavePath());
            registeLoadCallBack(taskId, callback);
            return getTaskRecord(taskId);
        }
    }

    public APMultimediaTaskModel upLoad(APFileReq info, APFileUploadCallback callback) {
        return upLoad(info, null, callback);
    }

    public APMultimediaTaskModel upLoad(APFileReq info, Options options, APFileUploadCallback callback) {
        logger.d("upLoad " + info);
        if (checkPreUpload(info, callback)) {
            return null;
        }
        String taskId = getUploadTaskId(info.getSavePath());
        Future fileTask = runningTask.get(taskId);
        if (fileTask != null) {
            logger.d("add new listener 1 " + fileTask);
            registeUpCallBack(taskId, callback);
            return getTaskRecord(taskId);
        }

        APMultimediaTaskModel taskInfo = createTaskRecord(PREFIX_FILE_UPLOAD, info);
        FileTask newFileTask = FileTaskFactory.createUploadTask(mContext, info, options, taskInfo, new FileUploadListener());
        FileFutureTask ft = new FileFutureTask(newFileTask);
        newFileTask.setName(ft.toString());

        FutureTask existFileTask = runningTask.putIfAbsent(taskId, ft);
        if (existFileTask == null) {
            logger.d("submit task " + ft);
            taskInfo.setSourcePath(info.getSavePath());
            addTaskRecord(taskInfo);
            registeUpCallBack(taskInfo.getTaskId(), callback);
            Future f = mExecutor.submit(ft);
            return taskInfo;
        } else {
            logger.d("add new listener 2 " + existFileTask);
            registeUpCallBack(taskInfo.getTaskId(), callback);
            return getTaskRecord(taskId);
        }
    }

    public APFileDownloadRsp downloadOffline(APFileReq info) {
        List<APFileReq> reqList = new ArrayList<APFileReq>();
        reqList.add(info);
        OfflineDownloadTask downloader = new OfflineDownloadTask(mContext, reqList, null);
        APFileDownloadRsp rsp;
        try {
            rsp = downloader.call();
        } catch (Exception e) {
            logger.e(e, "downloadOffline exception");
            rsp = new APFileDownloadRsp();
            rsp.setRetCode(APFileRsp.CODE_ERR_EXCEPTION);
            rsp.setMsg(e.getMessage());
        }
        return rsp;
    }

    /**
     * 同步下载
     */
    public APFileDownloadRsp downLoadSync(APFileReq info, APFileDownCallback callback) {
        logger.d("downLoadSync " + info);
        if (checkPreDownload(info, callback)) {
            return null;
        }
        String taskId = getDownloadTaskId(info.getCloudId());
        Future fileTask = runningTask.get(taskId);
        if (fileTask != null) {
            logger.d("add new listener 1 " + fileTask);
            putDownloadReqInfo(taskId, info.getSavePath());
            registeLoadCallBack(taskId, callback);
        } else {
            info.setSync(true);
            List<APFileReq> reqList = new ArrayList<APFileReq>();
            reqList.add(info);
            APMultimediaTaskModel taskInfo = createTaskRecord(PREFIX_FILE_DOWNLOAD, info);
            FileTask newFileTask = new FileDownloadTask(mContext, reqList, taskInfo, new FileDownLoadListener());
            FileFutureTask ft = new FileFutureTask(newFileTask);
            newFileTask.setName(ft.toString());

            FutureTask existFileTask = runningTask.putIfAbsent(taskId, ft);
            if (existFileTask == null) {
                logger.d("submit task " + ft);
                taskInfo.setSourcePath(info.getCloudId());
                taskInfo.setCloudId(info.getCloudId());
                addTaskRecord(taskInfo);
                putDownloadReqInfo(taskInfo.getTaskId(), info.getSavePath());
                registeLoadCallBack(taskInfo.getTaskId(), callback);
                Future f = mExecutor.submit(ft);
                fileTask = ft;
            } else {
                logger.d("add new listener 2 " + existFileTask);
                putDownloadReqInfo(taskId, info.getSavePath());
                registeLoadCallBack(taskId, callback);
                fileTask = existFileTask;
            }
        }

        APFileDownloadRsp rsp = new APFileDownloadRsp();
        try {
            rsp = (APFileDownloadRsp) fileTask.get();
        } catch (InterruptedException e) {
            rsp.setRetCode(APFileRsp.CODE_ERR_TASK_CANCELED);
            rsp.setMsg(e.getMessage());
        } catch (ExecutionException e) {
            rsp.setRetCode(APFileRsp.CODE_ERR_EXCEPTION);
            rsp.setMsg(e.getMessage());
        }
        return rsp;
    }

    /**
     * 同步上传
     */
    public APFileUploadRsp upLoadSync(APFileReq info, APFileUploadCallback callback) {
        logger.d("upLoadSync " + info);
        if (checkPreUpload(info, callback)) {
            return null;
        }
        String taskId = getUploadTaskId(info.getSavePath());
        Future fileTask = runningTask.get(taskId);
        if (fileTask != null) {
            logger.d("add new listener 1 " + fileTask);
            registeUpCallBack(taskId, callback);
        } else {
            info.setSync(true);
            List<APFileReq> reqList = new ArrayList<APFileReq>();
            reqList.add(info);
            APMultimediaTaskModel taskInfo = createTaskRecord(PREFIX_FILE_UPLOAD, info);
            FileTask newFileTask = new FileUploadTask(mContext, reqList, taskInfo, new FileUploadListener());
            FileFutureTask ft = new FileFutureTask(newFileTask);
            newFileTask.setName(ft.toString());

            FutureTask existFileTask = runningTask.putIfAbsent(taskId, ft);
            if (existFileTask == null) {
                logger.d("submit task " + ft);
                taskInfo.setSourcePath(info.getSavePath());
                addTaskRecord(taskInfo);
                registeUpCallBack(taskInfo.getTaskId(), callback);
                Future f = mExecutor.submit(ft);
                fileTask = ft;
            } else {
                logger.d("add new listener 2 " + existFileTask);
                registeUpCallBack(taskId, callback);
                fileTask = existFileTask;
            }
        }

        APFileUploadRsp rsp = new APFileUploadRsp();
        try {
            rsp = (APFileUploadRsp) fileTask.get();
        } catch (InterruptedException e) {
            rsp.setRetCode(APFileRsp.CODE_ERR_TASK_CANCELED);
            rsp.setMsg(e.getMessage());
        } catch (ExecutionException e) {
            rsp.setRetCode(APFileRsp.CODE_ERR_EXCEPTION);
            rsp.setMsg(e.getMessage());
        }
        return rsp;
    }

    public void cancelLoad(String taskId) {
        logger.d("cancelLoad " + taskId);
        clearDownloadReqInfo(taskId);

        APMultimediaTaskModel taskInfo = new APMultimediaTaskModel();
        taskInfo.setTaskId(taskId);
        taskInfo.setStatus(APMultimediaTaskModel.STATUS_CANCEL);
        APFileDownloadRsp rsp = new APFileDownloadRsp();
        rsp.setRetCode(APFileRsp.CODE_ERR_TASK_CANCELED);
        //防止点击cancel和onError之间, 起个同样的新任务这种情况, 这里提前注销callback
        //unregister之后, 防止其他监听者收不到callback, 在这里先通知下
        notifyDownloadError(taskInfo, rsp);
//        unregisteLoadCallBack(taskId);
        cancelTask(taskId);
    }

    public void cancelUp(String taskId) {
        logger.d("cancelUp " + taskId);

        APMultimediaTaskModel taskInfo = new APMultimediaTaskModel();
        taskInfo.setTaskId(taskId);
        taskInfo.setStatus(APMultimediaTaskModel.STATUS_CANCEL);
        APFileUploadRsp rsp = new APFileUploadRsp();
        rsp.setRetCode(APFileRsp.CODE_ERR_TASK_CANCELED);
        //防止点击cancel和onError之间, 起个同样的新任务这种情况, 这里提前注销callback
        //unregister之后, 防止其他监听者收不到callback, 在这里先通知下
        notifyUploadError(taskInfo, rsp);

//        unregisteUpCallBack(taskId);
        cancelTask(taskId);
    }

    public APMultimediaTaskModel getLoadTaskStatus(String taskId) {
        logger.d("getLoadTaskStatus " + taskId);
        return getTaskRecord(taskId);
    }

    public APMultimediaTaskModel getLoadTaskStatusByCloudId(String cloudId) {
        logger.d("getLoadTaskStatusByCloudId " + cloudId);
//        return getTaskRecordByCloudId(cloudId);
        return getLoadTaskStatus(getDownloadTaskId(cloudId));
    }

    public APMultimediaTaskModel getUpTaskStatus(String taskId) {
        logger.d("getUpTaskStatus " + taskId);
        return getTaskRecord(taskId);
    }

    public APMultimediaTaskModel getUpTaskStatusByCloudId(String savePath) {
        logger.d("getUpTaskStatusByCloudId " + savePath);
//        return getTaskRecordByCloudId(cloudId);
        return getUpTaskStatus(getUploadTaskId(savePath));
    }

    public void registeLoadCallBack(String taskId, APFileDownCallback callBack) {
        logger.d("registeLoadCallBack " + taskId);
        if (taskId == null || callBack == null) {
            return;
        }
        Map callbacks = downloadCallbackMap.get(taskId);
        if (callbacks == null) {
            /**
             * 这里的场景是读多写少, 使用线程安全的CopyOnWriteArraySet,
             * 否则回调遍历callback会报java.util.ConcurrentModificationException.
             * 保险起见，还是用ConcurrentHashMap吧
             */
//            callbacks = new HashSet<APFileDownCallback>();
//            callbacks = Collections.synchronizedSet(new HashSet<APFileDownCallback>());
//            callbacks = new CopyOnWriteArraySet<APFileDownCallback>();
            callbacks = new ConcurrentHashMap<APFileDownCallback, Object>();
        }
        callbacks.put(callBack, EMPTY_STRING);
        downloadCallbackMap.put(taskId, callbacks);
        logger.d("registerLoadCallBack taskId: " + taskId + ", callback num: "
                + downloadCallbackMap.get(taskId).size());
    }

    public void unregisteLoadCallBack(String taskId, APFileDownCallback callBack) {
        logger.d("unregisteLoadCallBack " + taskId);
        if (TextUtils.isEmpty(taskId)) {
            return;
        }
        if (callBack == null) {
            unregisteLoadCallBack(taskId);
            return;
        }
        Map loadCallBasks = downloadCallbackMap.get(taskId);
        if (loadCallBasks != null) {
            loadCallBasks.remove(callBack);
            if (loadCallBasks.isEmpty()) {
                downloadCallbackMap.remove(taskId);
            }
            logger.d("unregisteLoadCallBack taskId: " + taskId + ", callbackSet: "
                    + downloadCallbackMap.get(taskId));
        }
    }

    public void unregisteLoadCallBack(String taskId) {
        logger.d("unregisteLoadCallBack " + taskId);
        if (TextUtils.isEmpty(taskId)) {
            return;
        }
        Map loadCallBaskSet = downloadCallbackMap.remove(taskId);
        if (loadCallBaskSet != null) {
            loadCallBaskSet.clear();
        }
        logger.d("unregisteLoadCallBack taskId: " + taskId + ", remove all callback");
    }

    public void registeUpCallBack(String taskId, APFileUploadCallback callBack) {
        logger.d("registeUpCallBack " + taskId);
        if (TextUtils.isEmpty(taskId) || callBack == null) {
            return;
        }
        Map callbacks = uploadCallbackMap.get(taskId);
        if (callbacks == null) {
//            callBackHashSet = Collections.synchronizedSet(new HashSet<APFileUploadCallback>());
//            callBackHashSet = new CopyOnWriteArraySet<APFileUploadCallback>();
            callbacks = new ConcurrentHashMap<APFileUploadCallback, Object>();
        }
        callbacks.put(callBack, EMPTY_STRING);
        uploadCallbackMap.put(taskId, callbacks);
        logger.d("registerUpCallBack taskId: " + taskId + ", callback num: " + uploadCallbackMap.get(taskId).size());
    }

    public void unregisteUpCallBack(String taskId, APFileUploadCallback callBack) {
        logger.d("unregisteUpCallBack " + taskId);
        if (TextUtils.isEmpty(taskId)) {
            return;
        }
        if (callBack == null) {
            unregisteUpCallBack(taskId);
            return;
        }
        Map loadCallBaskSet = uploadCallbackMap.get(taskId);
        if (loadCallBaskSet != null) {
            loadCallBaskSet.remove(callBack);
            if (loadCallBaskSet.isEmpty()) {
                uploadCallbackMap.remove(taskId);
            }
            logger.d("unregisteUpCallBack taskId: " + taskId + ", callbackSet: " + uploadCallbackMap.get(taskId));
        }
    }

    public void unregisteUpCallBack(String taskId) {
        logger.d("unregisteUpCallBack " + taskId);
        if (TextUtils.isEmpty(taskId)) {
            return;
        }
        Map loadCallBaskSet = uploadCallbackMap.remove(taskId);
        if (loadCallBaskSet != null) {
            loadCallBaskSet.clear();
        }
        logger.d("unregisteUpCallBack taskId: " + taskId + ", remove all callback");
    }

    public List<APMultimediaTaskModel> batchUpLoad(Context context, List requestList, APFileUploadCallback callback) {
        if (requestList == null || requestList.isEmpty()) {
            logger.d("batchUpLoad " + requestList);
            return null;
        }
        logger.d("batchUpLoad " + requestList.size());
        List<APMultimediaTaskModel> taskList = new ArrayList<APMultimediaTaskModel>();
        for (Object object : requestList) {
            APFileReq info = (APFileReq) object;
            info.setSync(false);
            APMultimediaTaskModel taskInfo = upLoad(info, callback);
            taskList.add(taskInfo);
        }
        return taskList;
    }

    public List<APMultimediaTaskModel> batchDownLoad(Context context, List requestList, APFileDownCallback callback) {
        if (requestList == null || requestList.isEmpty()) {
            logger.d("batchDownLoad " + requestList);
            return null;
        }
        logger.d("batchDownLoad " + requestList.size());
        List<APMultimediaTaskModel> taskList = new ArrayList<APMultimediaTaskModel>();
        for (Object object : requestList) {
            APFileReq info = (APFileReq) object;
            info.setSync(false);
            APMultimediaTaskModel taskInfo = downLoad(info, callback);
            taskList.add(taskInfo);
        }

//        APMultimediaTaskModel taskInfo = createTaskRecord(PREFIX_FILE_DOWNLOAD, null);
//        registeLoadCallBack(taskInfo.getTaskId(), callback);
//        taskList.add(taskInfo);
        return taskList;
    }

    private APMultimediaTaskModel createTaskRecord(String taskPrefix, APFileReq req) {
        APMultimediaTaskModel task = new APMultimediaTaskModel();
        if (req == null) {
            task.setTaskId(taskPrefix + System.currentTimeMillis());
        } else if (PREFIX_FILE_DOWNLOAD.equals(taskPrefix)) {
            task.setTaskId(getDownloadTaskId(req.getCloudId()));
        } else if (PREFIX_FILE_UPLOAD.equals(taskPrefix)) {
            task.setTaskId(getUploadTaskId(req.getSavePath()));
        } else {
            task.setTaskId(taskPrefix + System.currentTimeMillis());
        }
        return task;
    }

    private APMultimediaTaskModel addTaskRecord(APMultimediaTaskModel taskInfo) {
        return taskManager.addTaskRecord(taskInfo);
    }

    private APMultimediaTaskModel updateTaskRecord(APMultimediaTaskModel taskInfo) {
//        logger.d("updateTaskRecord " + taskInfo);
        return taskManager.updateTaskRecord(taskInfo);
    }

    private APMultimediaTaskModel getTaskRecord(String taskId) {
        return taskManager.getTaskRecord(taskId);
    }

    private APMultimediaTaskModel getTaskRecordByCloudId(String cloudId) {
        return taskManager.getTaskRecordByCloudId(cloudId);
    }

    private APMultimediaTaskModel removeTaskRecord(String taskId) {
        return taskManager.delTaskRecord(taskId);
    }

    private APMultimediaTaskModel updateTaskModelStatus(APMultimediaTaskModel taskInfo, int status) {
        if (taskInfo != null) {
            taskInfo.setStatus(status);
            return updateTaskRecord(taskInfo);
        }
        return null;
    }

    private APMultimediaTaskModel cancelTask(String taskId) {
        logger.d("cancelTask taskId is " + taskId);
        if (TextUtils.isEmpty(taskId)) {
            return null;
        }
        FutureTask task = runningTask.remove(taskId);
        logger.d("cancelTask " + task);
        if (task != null) {
            task.cancel(true);
        } else {
            return null;
        }

        APMultimediaTaskModel taskInfo = getTaskRecord(taskId);
        return updateTaskModelStatus(taskInfo, APMultimediaTaskModel.STATUS_CANCEL);
    }

    private String getDownloadTaskId(String cloudId) {
        return MD5Utils.getMD5String(PREFIX_FILE_DOWNLOAD + cloudId);
    }

    private String getUploadTaskId(String savePath) {
        return MD5Utils.getMD5String(PREFIX_FILE_UPLOAD + savePath);
    }

    private boolean checkPreDownload(APFileReq info, APFileDownCallback callback) {
        if (info == null || TextUtils.isEmpty(info.getCloudId())) {
            if (callback != null) {
                APFileDownloadRsp rsp = new APFileDownloadRsp();
                rsp.setRetCode(APFileRsp.CODE_ERR_PATH_EMPTY);
                rsp.setMsg("路径为空");
                callback.onDownloadError(null, rsp);
            }
            return true;
        }
        //不需要另存为的情况, 可以直接check cache file exist, 提高响应速度
        if (TextUtils.isEmpty(info.getSavePath())) {
            FileTask checkTask = new FileTask(mContext, null, null);
            String cachePath = checkTask.checkCacheFile(info);
            if (!TextUtils.isEmpty(cachePath) && callback != null) {
                APFileDownloadRsp rsp = new APFileDownloadRsp();
                rsp.setRetCode(APFileRsp.CODE_SUCCESS);
                rsp.setMsg("get from cache file");
                info.setSavePath(cachePath);
                rsp.setFileReq(info);
                callback.onDownloadFinished(null, rsp);
                return true;
            }
        }
        return false;
    }

    private boolean checkPreUpload(APFileReq info, APFileUploadCallback callback) {
        if (info == null || TextUtils.isEmpty(info.getSavePath())) {
            if (callback != null) {
                APFileUploadRsp rsp = new APFileUploadRsp();
                rsp.setRetCode(APFileRsp.CODE_ERR_PATH_EMPTY);
                rsp.setMsg("路径为空");
                callback.onUploadError(null, rsp);
            }
            return true;
        }
        return false;
    }

    private void putDownloadReqInfo(String taskId, String savePath) {
        logger.d("putDownloadReqInfo " + taskId);
        if (TextUtils.isEmpty(taskId) || TextUtils.isEmpty(savePath)) {
            return;
        }
        Map reqs = downloadInfoMap.get(taskId);
        if (reqs == null) {
            reqs = new ConcurrentHashMap<String, Object>();
        }
        reqs.put(savePath, EMPTY_STRING);
        downloadInfoMap.put(taskId, reqs);
        logger.d("putDownloadReqInfo taskId: " + taskId + ", req num: "
                + downloadInfoMap.get(taskId).size());
    }

    public void clearDownloadReqInfo(String taskId) {
        logger.d("clearDownloadReqInfo " + taskId);
        if (TextUtils.isEmpty(taskId)) {
            return;
        }
        Map reqs = downloadInfoMap.remove(taskId);
        if (reqs != null) {
            reqs.clear();
        }
        logger.d("clearDownloadReqInfo taskId: " + taskId + ", remove all req");
    }

    private void copyFileAfterDownload(String taskId, String originalPath) {
        logger.d("copyFileAfterDownload " + taskId + ", originalPath: " + originalPath);
        if (TextUtils.isEmpty(taskId) || TextUtils.isEmpty(originalPath)) {
            return;
        }
        Map<String, Object> reqs = downloadInfoMap.get(taskId);
        if (reqs != null) {
            Set<String> reqSet = new CopyOnWriteArraySet<String>(reqs.keySet());
            RepeatableBufferedInputStream bufferedInputStream = null;

            for (String path : reqSet) {
                logger.d("copyFileAfterDownload, path: " + path);
                if (!TextUtils.isEmpty(path) && !path.equalsIgnoreCase(originalPath)) {
                    logger.d("copyFileAfterDownload, copy file to: " + path);
                    try {
                        bufferedInputStream = new RepeatableBufferedInputStream(
                                new FileInputStream(originalPath));
                        bufferedInputStream.flip();
                        File reqFile = new File(path);
                        File parent = reqFile.getParentFile();
                        if (!parent.exists() && !parent.mkdirs()) {
                            throw new RuntimeException("Couldn't create dir: " + parent);
                        }
//                        FileUtils.copyFile(bufferedInputStream, reqFile);
                    } catch (Throwable e) {
                        logger.e(e, "");
                        //todo: maybe change other exception
                        throw new RuntimeException(e.getMessage());
                    } finally {
                        IOUtils.closeQuietly(bufferedInputStream);
                    }
                }
            }

        }
    }

    private void notifyDownloadStart(APMultimediaTaskModel taskInfo) {
        Map<APFileDownCallback, Object> callbacks = downloadCallbackMap.get(taskInfo.getTaskId());
        if (callbacks != null) {
            logger.p("FileDownLoadListener callbacks " + callbacks.size());
            for (APFileDownCallback callback : callbacks.keySet()) {
                if (callback != null) {
                    callback.onDownloadStart(taskInfo);
                }
            }
        }
    }

    private void notifyDownloadProgress(APMultimediaTaskModel taskInfo, int progress, long hasDownSize,
            long total) {
        Map<APFileDownCallback, Object> callbacks = downloadCallbackMap.get(taskInfo.getTaskId());
        if (callbacks != null) {
            if(progress <= 1 || progress >= 99){
                logger.d("FileDownLoadListener callbacks " + callbacks.size());
            }else{
                logger.p("FileDownLoadListener callbacks " + callbacks.size());
            }

            for (APFileDownCallback callback : callbacks.keySet()) {
                if (callback != null) {
                    callback.onDownloadProgress(taskInfo, progress, hasDownSize, total);
                }
            }
        }
    }

    private void notifyDownloadBatchProgress(APMultimediaTaskModel taskInfo, int progress, int curIndex,
            long hasDownSize, long total) {
        Map<APFileDownCallback, Object> callbacks = downloadCallbackMap.get(taskInfo.getTaskId());
        if (callbacks != null) {
            if(progress <= 1 || progress >= 99){
                logger.d("FileDownLoadListener callbacks " + callbacks.size());
            }else {
                logger.p("FileDownLoadListener callbacks " + callbacks.size());
            }

            for (APFileDownCallback callback : callbacks.keySet()) {
                if (callback != null) {
                    callback.onDownloadBatchProgress(taskInfo, progress, curIndex, hasDownSize, total);
                }
            }
        }
    }

    private void notifyDownFinish(APMultimediaTaskModel taskInfo, APFileDownloadRsp rsp) {
        Map<APFileDownCallback, Object> callbacks = downloadCallbackMap.get(taskInfo.getTaskId());
        if (callbacks != null) {
            Set<APFileDownCallback> cbs = new CopyOnWriteArraySet<APFileDownCallback>(callbacks.keySet());
            //反注册往前提，防止业务在回调里添加同样的任务，回调马上被取消
            unregisteLoadCallBack(taskInfo.getTaskId());
            logger.d("FileDownLoadListener callbacks " + cbs.size());
            for (APFileDownCallback callback : cbs) {
                if (callback != null) {
                    callback.onDownloadFinished(taskInfo, rsp);
                }
            }

        }
    }

    private void notifyDownloadError(APMultimediaTaskModel taskInfo, APFileDownloadRsp rsp) {
        Map<APFileDownCallback, Object> callbacks = downloadCallbackMap.get(taskInfo.getTaskId());
        if (callbacks != null) {
            Set<APFileDownCallback> cbs = new CopyOnWriteArraySet<APFileDownCallback>(callbacks.keySet());
            //反注册往前提，防止业务在回调里添加同样的任务，回调马上被取消
            unregisteLoadCallBack(taskInfo.getTaskId());
            logger.d("FileDownLoadListener callbacks " + cbs.size());
            for (APFileDownCallback callback : cbs) {
                if (callback != null) {
                    callback.onDownloadError(taskInfo, rsp);
                }
            }
//                unregisteLoadCallBack(taskInfo.getTaskId());
        }
    }

    private void notifyUploadStart(APMultimediaTaskModel taskInfo) {
        Map<APFileUploadCallback, Object> callbacks = uploadCallbackMap.get(taskInfo.getTaskId());
        if (callbacks != null) {
            logger.d("FileUploadListener callbacks " + callbacks.size());
            for (APFileUploadCallback callback : callbacks.keySet()) {
                if (callback != null) {
                    callback.onUploadStart(taskInfo);
                }
            }
        }
    }

    private void notifyUploadProgress(APMultimediaTaskModel taskInfo, int progress, long hasUploadSize,
            long total) {
        Map<APFileUploadCallback, Object> callbacks = uploadCallbackMap.get(taskInfo.getTaskId());
        if (callbacks != null) {
            if(progress <= 1 || progress >= 99){
                logger.d("FileUploadListener callbacks " + callbacks.size());
            }else{
                logger.p("FileUploadListener callbacks " + callbacks.size());
            }

            for (APFileUploadCallback callback : callbacks.keySet()) {
                if (callback != null) {
                    callback.onUploadProgress(taskInfo, progress, hasUploadSize, total);
                }
            }
        }
    }

    private void notifyUploadFinish(APMultimediaTaskModel taskInfo, APFileUploadRsp rsp) {
        Map<APFileUploadCallback, Object> callbacks = uploadCallbackMap.get(taskInfo.getTaskId());
        if (callbacks != null) {
            Set<APFileUploadCallback> cbs = new CopyOnWriteArraySet<APFileUploadCallback>(callbacks.keySet());
            unregisteUpCallBack(taskInfo.getTaskId());
            logger.d("FileUploadListener callbacks " + cbs.size());
            for (APFileUploadCallback callback : cbs) {
                if (callback != null) {
                    callback.onUploadFinished(taskInfo, rsp);
                }
            }

        }
    }

    private void notifyUploadError(APMultimediaTaskModel taskInfo, APFileUploadRsp rsp) {
        Map<APFileUploadCallback, Object> callbacks = uploadCallbackMap.get(taskInfo.getTaskId());
        if (callbacks != null) {
            Set<APFileUploadCallback> cbs = new CopyOnWriteArraySet<APFileUploadCallback>(callbacks.keySet());
            unregisteUpCallBack(taskInfo.getTaskId());
            logger.d("FileUploadListener callbacks " + cbs.size());
            for (APFileUploadCallback callback : cbs) {
                if (callback != null) {
                    callback.onUploadError(taskInfo, rsp);
                }
            }
        }
    }

    public boolean deleteFileCache(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }

        try {
            cancelLoad(getDownloadTaskId(path));
            cancelUp(getUploadTaskId(path));

            removeTaskRecord(getDownloadTaskId(path));
            removeTaskRecord(getUploadTaskId(path));

            APFileReq req = new APFileReq();
            req.setCloudId(path);
            FileTask task = new FileTask(mContext, null, null);
            boolean ret = task.removeCacheFile(req);
            return ret;
        } catch (Exception e) {
            logger.e(e, "deleteFileCache exception");
        }
        return false;
    }

    class FileDownLoadListener implements APFileDownCallback {

        public FileDownLoadListener() {
        }

        @Override
        public void onDownloadStart(APMultimediaTaskModel taskInfo) {
            logger.d("FileDownLoadListener onDownloadStart " + taskInfo);
            if (APMultimediaTaskModel.STATUS_CANCEL == taskInfo.getStatus()) {
                logger.d("onDownloadStart cancel return ");
                return;
            }
            updateTaskModelStatus(taskInfo, APMultimediaTaskModel.STATUS_RUNNING);
            notifyDownloadStart(taskInfo);
        }

        @Override
        public void onDownloadProgress(APMultimediaTaskModel taskInfo, int progress, long hasDownSize, long total) {
            if(progress <= 1 || progress >= 99){
                logger.d("FileDownLoadListener onDownloadProgress, " + taskInfo + "progress: " + progress +
                        ", hasDownSize: " + hasDownSize + ", total: " + total);
            }else{
                logger.p("FileDownLoadListener onDownloadProgress, " + taskInfo + "progress: " + progress +
                        ", hasDownSize: " + hasDownSize + ", total: " + total);
            }
            if (APMultimediaTaskModel.STATUS_CANCEL == taskInfo.getStatus()) {
                logger.d("onDownloadProgress cancel return ");
                return;
            }
            taskInfo.setCurrentSize(hasDownSize);
            taskInfo.setTotalSize(total);
//            updateTaskModelStatus(taskInfo, APMultimediaTaskModel.STATUS_RUNNING);

            notifyDownloadProgress(taskInfo, progress, hasDownSize, total);
        }

        @Override
        public void onDownloadBatchProgress(APMultimediaTaskModel taskInfo, int progress, int curIndex,
                long hasDownSize, long total) {
            if(progress <= 1 || progress >= 99){
                logger.d("FileDownLoadListener onDownloadBatchProgress, " + taskInfo + "progress: " + progress
                        + ", curIndex:" + curIndex + ", hasDownSize:" + hasDownSize + ", total: " + total);
            }else{
                logger.p("FileDownLoadListener onDownloadBatchProgress, " + taskInfo + "progress: " + progress
                        + ", curIndex:" + curIndex + ", hasDownSize:" + hasDownSize + ", total: " + total);
            }

            if (APMultimediaTaskModel.STATUS_CANCEL == taskInfo.getStatus()) {
                logger.d("onDownloadBatchProgress cancel return ");
                return;
            }
            taskInfo.setCurrentSize(hasDownSize);
            taskInfo.setTotalSize(total);
//            updateTaskModelStatus(taskInfo, APMultimediaTaskModel.STATUS_RUNNING);

            notifyDownloadBatchProgress(taskInfo, progress, curIndex, hasDownSize, total);
        }

        @Override
        public void onDownloadFinished(APMultimediaTaskModel taskInfo, APFileDownloadRsp rsp) {
            logger.d("FileDownLoadListener onDownloadFinished " + taskInfo + "," + rsp);
            if (APMultimediaTaskModel.STATUS_CANCEL == taskInfo.getStatus()) {
                logger.d("onDownloadFinished cancel return ");
                return;
            }
            if (rsp.getFileReq() == null || rsp.getFileReq().isNeedCache()) {
                updateTaskModelStatus(taskInfo, APMultimediaTaskModel.STATUS_SUCCESS);
            } else {
                removeTaskRecord(taskInfo.getTaskId());
            }
            Future f = runningTask.remove(taskInfo.getTaskId());
            logger.d("onDownloadFinished remove " + f);

            if (rsp != null && rsp.getFileReq() != null) {
                copyFileAfterDownload(taskInfo.getTaskId(), rsp.getFileReq().getSavePath());
            }
            clearDownloadReqInfo(taskInfo.getTaskId());

            notifyDownFinish(taskInfo, rsp);
        }

        @Override
        public void onDownloadError(APMultimediaTaskModel taskInfo, APFileDownloadRsp rsp) {
            logger.d("FileDownLoadListener onDownloadError " + taskInfo + ", " + rsp);
            // click cancel->update cancel->click add task->update running->onDownloadError->update fail
            // 这个时序问题会导致 downLoad()里的判重逻辑失效，起多个任务
            if (APFileRsp.CODE_ERR_TASK_CANCELED == rsp.getRetCode()
                    || APMultimediaTaskModel.STATUS_CANCEL == taskInfo.getStatus()) {
                logger.d("onDownloadError cancel return ");
                return;
            }
            //cancel掉的task, 这下面的逻辑在cancelTask()里面已经走过了, 不能再走了!!!
            if (rsp.getFileReq() == null || rsp.getFileReq().isNeedCache()) {
                updateTaskModelStatus(taskInfo, APMultimediaTaskModel.STATUS_FAIL);
            } else {
                removeTaskRecord(taskInfo.getTaskId());
            }
            Future f = runningTask.remove(taskInfo.getTaskId());
            logger.d("onDownloadError remove " + f);
            clearDownloadReqInfo(taskInfo.getTaskId());

            notifyDownloadError(taskInfo, rsp);
        }
    }

    class FileUploadListener implements APFileUploadCallback {

        public FileUploadListener() {
        }

        @Override
        public void onUploadStart(APMultimediaTaskModel taskInfo) {
            logger.d("FileUploadListener onUploadStart " + taskInfo);
            if (APMultimediaTaskModel.STATUS_CANCEL == taskInfo.getStatus()) {
                logger.d("onUploadStart cancel return ");
                return;
            }
            updateTaskModelStatus(taskInfo, APMultimediaTaskModel.STATUS_RUNNING);

            notifyUploadStart(taskInfo);
        }

        @Override
        public void onUploadProgress(APMultimediaTaskModel taskInfo, int progress, long hasUploadSize, long total) {
            if(progress <= 1 || progress >= 99){
                logger.d("FileUploadListener onUploadProgress " + taskInfo + " progress: " + progress
                        + ", hasUploadSize: " + hasUploadSize + ", total:" + total);
            } else {
                logger.p("FileUploadListener onUploadProgress " + taskInfo + " progress: " + progress
                        + ", hasUploadSize: " + hasUploadSize + ", total:" + total);
            }

            if (APMultimediaTaskModel.STATUS_CANCEL == taskInfo.getStatus()) {
                logger.d("onUploadProgress cancel return ");
                return;
            }
            taskInfo.setCurrentSize(hasUploadSize);
            taskInfo.setTotalSize(total);
//            updateTaskModelStatus(taskInfo, APMultimediaTaskModel.STATUS_RUNNING);

            notifyUploadProgress(taskInfo, progress, hasUploadSize, total);
        }

        @Override
        public void onUploadFinished(APMultimediaTaskModel taskInfo, APFileUploadRsp rsp) {
            logger.d("FileUploadListener onUploadFinished " + taskInfo + ", " + rsp);
            if (APMultimediaTaskModel.STATUS_CANCEL == taskInfo.getStatus()) {
                logger.d("onUploadFinished cancel return ");
                return;
            }
            taskInfo.setCloudId(rsp.getFileReq().getCloudId());
            updateTaskModelStatus(taskInfo, APMultimediaTaskModel.STATUS_SUCCESS);
            Future f = runningTask.remove(taskInfo.getTaskId());
            logger.d("onUploadFinished remove " + f);

            notifyUploadFinish(taskInfo, rsp);

        }

        @Override
        public void onUploadError(APMultimediaTaskModel taskInfo, APFileUploadRsp rsp) {
            logger.d("FileUploadListener onUploadError " + taskInfo + ", " + rsp);
            if (APFileRsp.CODE_ERR_TASK_CANCELED == rsp.getRetCode()
                    || APMultimediaTaskModel.STATUS_CANCEL == taskInfo.getStatus()) {
                logger.d("onUploadError cancel return ");
                return;
            }
            //cancel掉的task, 这下面的逻辑在cancelTask()里面已经走过了, 不能再走了!!!
            updateTaskModelStatus(taskInfo, APMultimediaTaskModel.STATUS_FAIL);
            Future f = runningTask.remove(taskInfo.getTaskId());
            logger.d("onUploadError remove " + f);
            notifyUploadError(taskInfo, rsp);
        }
    }

}
