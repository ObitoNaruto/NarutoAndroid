package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.service.graphics;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import java.util.Map;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.naruto.mobile.framework.service.common.multimedia.api.data.APMultimediaTaskModel;
import com.naruto.mobile.framework.service.common.multimedia.graphics.APImageDownLoadCallback;
import com.naruto.mobile.framework.service.common.multimedia.graphics.APImageUploadCallback;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.req.ThumbnailsDownReq;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.resp.ThumbnailsDownResp;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.manager.APMultimediaTaskManager;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Logger;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Memoizer;

/**
 * 图片管理：图片上传、图片下载
 *
 * @author xiaofeng.dxf
 */
public class APImageManager {
    public static final String IMAGE_TAG = APImageManager.class.getSimpleName();

    private static APImageManager sInstance;
    private APMultimediaTaskManager mTaskManager;
	private static final String EMPTY_STRING = "";

    private Memoizer<ThumbnailsDownReq, ThumbnailsDownResp> thumbnailsDownMemoizer = new Memoizer<ThumbnailsDownReq, ThumbnailsDownResp>();

    /**
     * 用于上传状态的重新注册监听
     */
    private ConcurrentHashMap<String, Map> uploadHashMap = new ConcurrentHashMap<String, Map>();

    private ConcurrentHashMap<String, Map> downLoadHashMap = new ConcurrentHashMap<String, Map>();

    private ConcurrentHashMap<String, String> loadMap = new ConcurrentHashMap<String, String>();

    public APMultimediaTaskManager getTaskManager() {
        return mTaskManager;
    }

    public void setTaskManager(APMultimediaTaskManager taskManager) {
        this.mTaskManager = taskManager;
    }
    
    /**
     * 注册上传回调
     *
     * @param taskId
     * @param callBack
     */
    public void registUploadCallback(String taskId, APImageUploadCallback callBack) {
        if (taskId == null || callBack == null) {
            return;
        }
        Map callBacks = uploadHashMap.get(taskId);
        if (callBacks == null) {
            callBacks = new ConcurrentHashMap<APImageUploadCallback,Object>();
        }
        callBacks.put(callBack, EMPTY_STRING);
        uploadHashMap.put(taskId, callBacks);
    }

    /**
     * 注册下载回调
     *
     * @param taskId
     * @param callBack
     */
    public void registLoadCallback(String taskId, APImageDownLoadCallback callBack) {
        if (taskId == null || callBack == null) {
            return;
        }

        Map callBacks = downLoadHashMap.get(taskId);
        if (callBacks == null) {
            callBacks = new ConcurrentHashMap<APImageUploadCallback,Object>();
        }
        callBacks.put(callBack, EMPTY_STRING);
        downLoadHashMap.put(taskId, callBacks);
    }

    /**
     * 注销上传回调
     * 注：如果callback不为空则注销单个callback,否则注销此taskid下所有callback
     *
     * @param taskId
     * @param callBack
     */
    public void unregistUploadCallback(String taskId, APImageUploadCallback callBack) {
        Logger.I(IMAGE_TAG, "unregistUploadCallback " + taskId);

        if (TextUtils.isEmpty(taskId)) {
            return;
        }

        if(callBack == null){
            unregistUploadCallback(taskId);
            return;
        }

        Map callBacks = uploadHashMap.get(taskId);
        if (callBacks != null) {
            callBacks.remove(callBack);
            if (callBacks.isEmpty()) {
                uploadHashMap.remove(taskId);
            }
            Logger.I(IMAGE_TAG, "unregistUploadCallback taskId: " + taskId + ", callbackSet: " + uploadHashMap.get(taskId));
        }
    }

    /**
     * 注销taskid下所有上传回调
     *
     * @param taskId
     */
    public void unregistUploadCallback(String taskId) {
        if (!TextUtils.isEmpty(taskId) && uploadHashMap != null) {
            Map callBacks = uploadHashMap.get(taskId);
            if (callBacks != null) {
                callBacks.clear();
            }

            uploadHashMap.remove(taskId);
        }
    }

    /**
     * 注销下载回调
     * 注：如果callback不为空则注销单个callback,否则注销此taskid下所有callback
     *
     * @param taskId
     * @param callBack
     */
    public void unregistLoadCallback(String taskId, APImageDownLoadCallback callBack) {
        Logger.I(IMAGE_TAG, "unregistLoadCallback " + taskId);

        if (TextUtils.isEmpty(taskId)) {
            return;
        }

        if(callBack == null){
            unregistLoadCallback(taskId);
            return;
        }

        Map callBacks = downLoadHashMap.get(taskId);
        if (callBacks != null) {
            callBacks.remove(callBack);
            if (callBacks.isEmpty()) {
                downLoadHashMap.remove(taskId);
            }
            Logger.I(IMAGE_TAG,
                    "unregistLoadCallback taskId: " + taskId + ", callbackSet: " + downLoadHashMap.get(taskId));
        }
    }

    /**
     * 注销taskid下所有下载回调
     *
     * @param taskId
     */
    public void unregistLoadCallback(String taskId) {
        if (!TextUtils.isEmpty(taskId) && downLoadHashMap != null) {
            Map callBacks = downLoadHashMap.get(taskId);         
            if (callBacks != null) {
                callBacks.clear();
            }

            downLoadHashMap.remove(taskId);
        }
    }
	
    /**
     * 获取上传的回调
     *
     * @param taskId
     * @return
     */
    public Map<APImageUploadCallback,Object> getUpTaskCallback(String taskId) {
        if (!TextUtils.isEmpty(taskId)) {
            return uploadHashMap.get(taskId);
        }

        return null;
    }

    /**
     * 获取下载的回调
     *
     * @param taskId
     * @return
     */
    public Map<APImageDownLoadCallback,Object> getLoadTaskCallback(String taskId) {
        if (taskId != null) {
            return downLoadHashMap.get(taskId);
        }
        return null;
    }

    /**
     * 获取上传下载的状态
     *
     * @param taskId
     * @return
     */
    public APMultimediaTaskModel getTaskStatusRecord(String taskId) {
        return mTaskManager.getTaskRecord(taskId);
    }

    private APImageManager(Context context) {
        setTaskManager(APMultimediaTaskManager.getInstance(context));
    }

    public static synchronized APImageManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new APImageManager(context);
        }
        return sInstance;
    }

    //将网络任务根据key值加入到map中
    public void putLoadlingTaskTag(String key,String value){
        if(TextUtils.isEmpty(key) || TextUtils.isEmpty(value)){
            return;
        }

        loadMap.put(key,value);
    }

    public String getLoadlingTaskTag(String key){
        if(!TextUtils.isEmpty(key)){
            return loadMap.get(key);
        }
        return null;
    }

    public void removeLoadingTaskTag(String key){
        if(!TextUtils.isEmpty(key)){
            loadMap.remove(key);
        }
    }

    public boolean isUrlInNetTask(String key){
        boolean ret = false;
        if(!TextUtils.isEmpty(key)){
            ret = loadMap.containsKey(key);
        }

        return ret;
    }

    public Memoizer<ThumbnailsDownReq, ThumbnailsDownResp> getThumbnailsDownMemoizer() {
        return thumbnailsDownMemoizer;
    }
}
