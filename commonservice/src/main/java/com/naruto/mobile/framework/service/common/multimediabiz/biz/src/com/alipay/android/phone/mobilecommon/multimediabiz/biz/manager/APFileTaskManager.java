package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.manager;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
//import com.alipay.android.phone.mobilesdk.storage.utils.FileUtils;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.naruto.mobile.framework.service.common.multimedia.api.data.APMultimediaTaskModel;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.util.DjangoConstant;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.util.DjangoUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.util.MD5Utils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.persistence.Persistence;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.FileUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class APFileTaskManager {

    private static final String TAG = APFileTaskManager.class.getSimpleName();
    private static final long MAX_LOCAL_TASK_RECORDS = 12000;

    private Logger logger = Logger.getLogger(TAG);

    private static APFileTaskManager instance;
    private Persistence<APMultimediaTaskModel> mPersistence;
    private Context mContext;
    private ExecutorService mExecutor = Executors.newSingleThreadExecutor();//Executors.newCachedThreadPool();

    private Map<String, APMultimediaTaskModel> taskCache = new ConcurrentHashMap<String, APMultimediaTaskModel>();

    private APFileTaskManager(Context context) {
        this.mContext = context;
        try {
            mPersistence = new MultiMediaTaskPersistence(mContext);
            checkDbData();
        } catch (Throwable e) {
            logger.e(e, "APFileTaskManager init exception");
        }
    }

    private void checkDbData() throws Exception {
        mExecutor.submit(new Runnable() {
            @Override
            public void run() {
                // 需要保存写文件的时候清理吧
//                deleteOutDateFile(FILE_KEEP_TIME);
//                int count = deleteOutDateTask(FILE_KEEP_TIME);
//                //如果删了还不够再删点
//                if (!FileUtils.isSDcardAvailableSpace(MIN_FREE_STORAGE)) {
//                    deleteOutDateFile(FILE_KEEP_TIME / 3);
//                    int count2 = deleteOutDateTask(FILE_KEEP_TIME / 3);
//                }
                // 处理上次失败导致保持running状态的问题
                resetTaskStatus();

                TimerTask task = new TimerTask(){
                    public void run(){
                        // 清理过期的数据
                        clearOutDateData();
                    }
                };
                Timer timer = new Timer();
                timer.schedule(task, 15000);
            }
        });
    }

    public void deleteOutDateFile(long interval) {
        try {
            String savePath = DjangoUtils.getMediaDir(mContext, DjangoConstant.FILE_PATH);
            String cachePath = mContext.getCacheDir().getAbsolutePath();

            QueryBuilder<APMultimediaTaskModel, String> queryBuilder = mPersistence.queryBuilder();
            List<APMultimediaTaskModel> list = queryBuilder.where().le(APMultimediaTaskModel.F_CREATE_TIME,
                    System.currentTimeMillis() - interval).query();
            for (APMultimediaTaskModel task : list) {
                String filePath = task.getDestPath();
                if (!TextUtils.isEmpty(filePath) && (filePath.startsWith(savePath) || filePath.startsWith(cachePath))) {
                    logger.d("delete out date file : " + filePath);
//                    FileUtils.deleteFileByPath(filePath);
                }
            }
        } catch (Throwable e) {
            logger.e(e, "deleteOutDateFile exception");
        }

    }

    public int deleteOutDateTask(long interval) {
        try {
            DeleteBuilder<APMultimediaTaskModel, String> deleteBuilder = mPersistence.deleteBuilder();
            deleteBuilder.where().le(APMultimediaTaskModel.F_CREATE_TIME,
                    System.currentTimeMillis() - interval);
            int removedCount = deleteBuilder.delete();
            logger.d("delete out date task removedCount " + removedCount);
            return removedCount;
        } catch (Throwable e) {
            logger.e(e, "deleteOutDateTask exception");
        }
        return 0;
    }

    private void resetTaskStatus() {
        try {
            final List<APMultimediaTaskModel> failList = mPersistence.queryForEq(APMultimediaTaskModel.class,
                    APMultimediaTaskModel.F_TASK_STATUS, String.valueOf(APMultimediaTaskModel.STATUS_RUNNING));
            if (failList == null || failList.isEmpty()) {
                logger.i("failList empty");
            } else {
                logger.i("failList SIZE  =  " + failList.size());
                for (APMultimediaTaskModel taskInfo : failList) {
                    taskInfo.setStatus(APMultimediaTaskModel.STATUS_FAIL);
                }
                mPersistence.save(failList);
            }
        } catch (Throwable e) {
            logger.e(e, "resetTaskStatus exception");
        }
    }

    /**
     * Returns singleton class instance
     */
    public static APFileTaskManager getInstance(Context context) {
        if (instance == null) {
            synchronized (APFileTaskManager.class) {
                if (instance == null) {
                    instance = new APFileTaskManager(context);
                }
            }
        }
        return instance;
    }

    public APMultimediaTaskModel getTaskRecord(String recordId) {
        logger.d("getTaskRecord recordId: " + recordId);
        APMultimediaTaskModel task = null;
        try {
            task = taskCache.get(recordId);
            if (task == null) {
                task = mPersistence.query(APMultimediaTaskModel.class, recordId);
            }
        } catch (Exception e) {
            //ignore
        }
        return task;
    }

    public APMultimediaTaskModel getTaskRecordByCloudId(String cloudId) {
        logger.d("getTaskRecordByCloudId cloudId: " + cloudId);
        if (cloudId == null) {
            return null;
        }
        APMultimediaTaskModel task = null;
        try {
            for (String taskId : taskCache.keySet()) {
                task = taskCache.get(taskId);
                if (task != null && cloudId.equals(task.getCloudId())) {
                    return task;
                }
            }
            if (task == null) {
                List<APMultimediaTaskModel> results = mPersistence.queryForEq(
                        APMultimediaTaskModel.class, APMultimediaTaskModel.F_CLOUD_ID, cloudId);
                if (results != null && !results.isEmpty()) {
                    task = results.get(0);
                    return task;
                }
            }
        } catch (Exception e) {
            //ignore
        }
        return task;
    }

    public APMultimediaTaskModel updateTaskRecord(final APMultimediaTaskModel taskRecord) {
        try {
            if (taskRecord != null && !TextUtils.isEmpty(taskRecord.getTaskId())) {
                logger.d("updateTaskRecord " + taskRecord);
                taskRecord.setUpdateTime(System.currentTimeMillis());
                taskCache.put(taskRecord.getTaskId(), taskRecord);
                mExecutor.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mPersistence.save(taskRecord);
                        } catch (Exception e) {
                            //ignore
                        }
                    }
                });
                return taskRecord;
            }
        } catch (Exception e) {
            //ignore
        }
        return taskRecord;
    }

    public APMultimediaTaskModel delTaskRecord(String recordId) {
        logger.d("delTaskRecord recordId: " + recordId);
        try {
            if (taskCache.containsKey(recordId)) {
                taskCache.remove(recordId);
            }
            APMultimediaTaskModel model = mPersistence.delete(APMultimediaTaskModel.class, recordId);
            return model;
        } catch (Exception e) {
            //ignore
        }
        return null;
    }

    public synchronized APMultimediaTaskModel addTaskRecord(APMultimediaTaskModel taskRecord) {
        logger.d("addTaskRecord " + taskRecord);
        if (taskRecord == null) {
            return null;
        }
        if (TextUtils.isEmpty(taskRecord.getTaskId())) {
            String taskId = genTaskId(taskRecord);
            taskRecord.setTaskId(taskId);
        }
        long cur = System.currentTimeMillis();
        taskRecord.setCreatTime(cur);
        taskRecord.setUpdateTime(cur);
        updateCache(taskRecord);
        return taskRecord;
    }

    private String genTaskId(APMultimediaTaskModel task) {
        if (task != null) {
            long currentTime = System.currentTimeMillis();
            String keyOrigin = currentTime + "@" + task.hashCode();
            return MD5Utils.getMD5String(keyOrigin);
        }
        return null;
    }

    private void checkAndSaveCache() {
        mExecutor.submit(new Runnable() {
            @Override
            public void run() {
                logger.d("checkAndSaveCache reach limit ，need save ");
                saveCache();
            }
        });
    }

    private void clearOutDateData() {
        try {
            long count = mPersistence.countOf();
            if (count >= MAX_LOCAL_TASK_RECORDS) {
                List<APMultimediaTaskModel> taskModels = mPersistence.queryBuilder()
                        .orderBy(APMultimediaTaskModel.F_CREATE_TIME, true)
                        .limit(count - MAX_LOCAL_TASK_RECORDS + 100)
                        .query();
                if (taskModels != null) {
                    String savePath = DjangoUtils.getMediaDir(mContext, DjangoConstant.FILE_PATH);
                    String cachePath = mContext.getCacheDir().getAbsolutePath();
                    logger.d("found out date count: " + taskModels.size());
                    for (APMultimediaTaskModel task : taskModels) {
                        String filePath = task.getDestPath();
                        if (!TextUtils.isEmpty(filePath) &&
                                (filePath.startsWith(savePath) || filePath.startsWith(cachePath))) {
                            logger.d("delete out date file : " + filePath);
//                            FileUtils.deleteFileByPath(filePath);
                        }
                    }
                    mPersistence.delete(taskModels);
                }
            }
        } catch (Exception e) {
            logger.e(e, "clearOutDateData exception");
        }
    }

    private void saveCache() {
        if (mPersistence != null && taskCache != null && !taskCache.isEmpty()) {
            List<APMultimediaTaskModel> list = new ArrayList<APMultimediaTaskModel>(taskCache.values());
            try {
                mPersistence.save(list);
                taskCache.clear();
            } catch (Exception e) {
                logger.e(e, "saveCache error");
            }
        }
    }

    //更新缓存，检测是否该存盘
    private void updateCache(APMultimediaTaskModel task) {
        taskCache.put(task.getTaskId(), task);
        checkAndSaveCache();
    }
}
