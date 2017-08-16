package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.manager;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import com.naruto.mobile.framework.service.common.multimedia.api.data.APMultimediaTaskModel;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.util.MD5Utils;

/**
 * Created by zhenghui on 15/4/10.
 */
public class APMultimediaTaskManager {

    private static final String TAG = "APMultimediaTaskManager";

    private static final int MAX_CACHE_COUNT = 5;
    private static final int MAX_DELAY_TIME = 3 * 1000;
    private static final String EMPTY_STRING = "";

    private static final int MSG_SAVE_CACHE = 0;

    private static APMultimediaTaskManager instance;
//    private Persistence<APMultimediaTaskModel> mPersistence;
    private Context context;
//    private HandlerThread mCacheHandlerThread = new HandlerThread("cache_handler");
//    private Handler mCacheHandler;

    private Map<String, APMultimediaTaskModel> taskCache = new ConcurrentHashMap<String, APMultimediaTaskModel>();
    //用于存储future
    private Map<String, Map> mFutureListMap = new ConcurrentHashMap<String, Map>();

    private APMultimediaTaskManager(Context context) {
        this.context = context;
//        try {
//            mCacheHandlerThread.start();
//            mPersistence = new MultiMediaTaskPersistence(context);
//        } catch (Exception e) {
//        }
//
//        mCacheHandler = new Handler(mCacheHandlerThread.getLooper()) {
//            @Override
//            public void handleMessage(Message msg) {
//                switch (msg.what) {
//                    case MSG_SAVE_CACHE:
//                        log(TAG, "MSG_SAVE_CACHE 保存缓存");
//                        saveCache();
//                        break;
//                }
//            }
//        };
    }

    /**
     * Returns singleton class instance
     */
    public static APMultimediaTaskManager getInstance(Context context) {
        if (instance == null) {
            synchronized (APMultimediaTaskManager.class) {
                if (instance == null) {
                    instance = new APMultimediaTaskManager(context);
                }
            }
        }
        return instance;
    }

    public APMultimediaTaskModel getTaskRecord(String recordId) {
        log(TAG, "getTaskRecord recordId: " + recordId);
        APMultimediaTaskModel task = null;
        try {
            task = taskCache.get(recordId);
            if (task == null) {
//                task = mPersistence.query(APMultimediaTaskModel.class, recordId);
            }
        } catch (Exception e) {
            //ignore
        }
        return task;
    }

    public void removeTaskRecord(String recordId) {
        log(TAG, "removeTaskRecord recordId: " + recordId);
        try {
            if(!TextUtils.isEmpty(recordId)){
                taskCache.remove(recordId);
            }
        } catch (Exception e) {
            //ignore
        }
    }

    public APMultimediaTaskModel getTaskRecordByCloudId(String cloudId) {
        log(TAG, "getTaskRecordByCloudId cloudId: " + cloudId);
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
                // todo: find in mPersistence
                // task = mPersistence.query(APMultimediaTaskModel.class, recordId);
            }
        } catch (Exception e) {
            //ignore
        }
        return task;
    }

    public APMultimediaTaskModel updateTaskRecord(APMultimediaTaskModel taskRecord) {
        try {
            if (taskRecord != null && !TextUtils.isEmpty(taskRecord.getTaskId())) {
                log(TAG, "updateTaskRecord id=" + taskRecord.getTaskId());
                taskCache.put(taskRecord.getTaskId(), taskRecord);
            }

//            taskRecord = mPersistence.save(taskRecord);
        } catch (Exception e) {
            //ignore
        }
        return taskRecord;
    }

    public APMultimediaTaskModel delTaskRecord(String recordId) {
        log(TAG, "delTaskRecord recordId: " + recordId);
        try {
            if (taskCache.containsKey(recordId)) {
                taskCache.remove(recordId);
            }
//            APMultimediaTaskModel model = mPersistence.delete(APMultimediaTaskModel.class, recordId);
//            return model;
        } catch (Exception e) {
            //ignore
        }
        return null;
    }

    public synchronized APMultimediaTaskModel addTaskRecord(APMultimediaTaskModel taskRecord) {
        log(TAG, "addTaskRecord");
        if (taskRecord == null) {
            return null;
        }
        if (TextUtils.isEmpty(taskRecord.getTaskId())) {
            String taskId = genTaskId(taskRecord);
            taskRecord.setTaskId(taskId);
        }
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
        //缓存上限了，要保存
//        if (taskCache.size() >= MAX_CACHE_COUNT) {
//            log(TAG, "checkAndSaveCache 缓存上限了，要保存");
//            saveCache();
//        } else {
//            mCacheHandler.sendEmptyMessageDelayed(MSG_SAVE_CACHE, MAX_DELAY_TIME);
//        }
    }

    private void saveCache() {
//        if (mPersistence != null) {
//            List<APMultimediaTaskModel> list = new ArrayList<APMultimediaTaskModel>(taskCache.values());
//            try {
//                mPersistence.save(list);
//                mCacheHandler.removeCallbacksAndMessages(null);
//                taskCache.clear();
//            } catch (Exception e) {
//            }
//        }
    }

    //更新缓存，检测是否该存盘
    private void updateCache(APMultimediaTaskModel task) {
//        mCacheHandler.removeCallbacksAndMessages(null);
        taskCache.put(task.getTaskId(), task);
        checkAndSaveCache();
    }

    public Map<Future,Object> getTaskFutureList(String taskId) {
        if (TextUtils.isEmpty(taskId)) {
            return null;
        }

        return mFutureListMap.get(taskId);
    }

    public void addTaskFuture(String taskId, Future future) {
        if (!TextUtils.isEmpty(taskId)) {
            Map<Future, Object> futures = getTaskFutureList(taskId);
            if (futures == null) {
                futures = new ConcurrentHashMap<Future, Object>();
                futures.put(future,EMPTY_STRING);
                mFutureListMap.put(taskId,futures);
            } else {
                futures.put(future, EMPTY_STRING);
            }
        }
    }

    public void removeTaskFuture(String taskId) {
        if (!TextUtils.isEmpty(taskId)) {
            Map<Future, Object> futures = getTaskFutureList(taskId);
            if(futures != null){
                futures.clear();
            }
            mFutureListMap.remove(taskId);
        }
    }

    private void log(String tag, String msg) {
        //ImageHandler.log(tag,msg);
    }
}
