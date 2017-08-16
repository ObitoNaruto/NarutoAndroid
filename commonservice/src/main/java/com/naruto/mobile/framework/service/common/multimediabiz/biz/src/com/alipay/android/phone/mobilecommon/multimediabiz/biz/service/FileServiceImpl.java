package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.service;

import android.content.Context;
import android.os.Bundle;

import java.util.List;

import com.naruto.mobile.base.serviceaop.NarutoApplication;
import com.naruto.mobile.framework.service.common.multimedia.api.MultimediaFileService;
import com.naruto.mobile.framework.service.common.multimedia.api.data.APMultimediaTaskModel;
import com.naruto.mobile.framework.service.common.multimedia.file.APFileDownCallback;
import com.naruto.mobile.framework.service.common.multimedia.file.APFileUploadCallback;
import com.naruto.mobile.framework.service.common.multimedia.file.data.APFileDownloadRsp;
import com.naruto.mobile.framework.service.common.multimedia.file.data.APFileReq;
import com.naruto.mobile.framework.service.common.multimedia.file.data.APFileUploadRsp;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.file.FileUpLoadManager;

public class FileServiceImpl extends MultimediaFileService {

    private FileUpLoadManager fileUpLoadManager;
    private Context mContext;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mContext = NarutoApplication.getInstance().getNarutoApplicationContext().getApplicationContext();
        this.fileUpLoadManager = FileUpLoadManager.getInstance(mContext);
    }

    @Override
    public void cancelLoad(String taskId) {
        fileUpLoadManager.cancelLoad(taskId);
    }

    @Override
    public void cancelUp(String taskId) {
        fileUpLoadManager.cancelUp(taskId);
    }

    @Override
    public APMultimediaTaskModel getLoadTaskStatus(String taskId) {
        return fileUpLoadManager.getLoadTaskStatus(taskId);
    }

    @Override
    public APMultimediaTaskModel getLoadTaskStatusByCloudId(String cloudId) {
        return fileUpLoadManager.getLoadTaskStatusByCloudId(cloudId);
    }

    @Override
    public APMultimediaTaskModel getUpTaskStatus(String taskId) {
        return fileUpLoadManager.getUpTaskStatus(taskId);
    }

    @Override
    public APMultimediaTaskModel getUpTaskStatusByCloudId(String cloudId) {
        return fileUpLoadManager.getUpTaskStatusByCloudId(cloudId);
    }

    @Override
    public APMultimediaTaskModel downLoad(String url, APFileDownCallback callback) {
        APFileReq req = new APFileReq();
        req.setCloudId(url);
        return downLoad(req, callback);
    }

    @Override
    public APMultimediaTaskModel downLoad(String url, String savePath, APFileDownCallback callback) {
        APFileReq req = new APFileReq();
        req.setCloudId(url);
        req.setSavePath(savePath);
        return downLoad(req, callback);
    }

    @Override
    public APMultimediaTaskModel downLoad(APFileReq info, APFileDownCallback callback) {
        return fileUpLoadManager.downLoad(info, callback);
    }

    @Override
    public APMultimediaTaskModel upLoad(APFileReq info, APFileUploadCallback callback) {
        return fileUpLoadManager.upLoad(info, callback);
    }

    @Override
    public APFileDownloadRsp downloadOffline(APFileReq info) {
        return fileUpLoadManager.downloadOffline(info);
    }

    @Override
    public APMultimediaTaskModel upLoad(String savePath, APFileUploadCallback callback) {
        APFileReq req = new APFileReq();
        req.setSavePath(savePath);
        return upLoad(req, callback);
    }

    @Override
    public APFileDownloadRsp downLoadSync(APFileReq info, APFileDownCallback callback) {
        return fileUpLoadManager.downLoadSync(info, callback);
    }

    @Override
    public APFileUploadRsp upLoadSync(APFileReq info, APFileUploadCallback callback) {
        return fileUpLoadManager.upLoadSync(info, callback);
    }

    @Override
    public void registeLoadCallBack(String taskId, APFileDownCallback callBack) {
        fileUpLoadManager.registeLoadCallBack(taskId, callBack);
    }

    @Override
    public void unregisteLoadCallBack(String taskId, APFileDownCallback callBack) {
        fileUpLoadManager.unregisteLoadCallBack(taskId, callBack);
    }

    @Override
    public void registeUpCallBack(String taskId, APFileUploadCallback callBack) {
        fileUpLoadManager.registeUpCallBack(taskId, callBack);
    }

    @Override
    public void unregisteUpCallBack(String taskId, APFileUploadCallback callBack) {
        fileUpLoadManager.unregisteUpCallBack(taskId, callBack);
    }

    @Override
    public boolean deleteFileCache(String path) {
        return fileUpLoadManager.deleteFileCache(path);
    }

    //@Override
    public List<APMultimediaTaskModel> batchUpLoad(List requestList, APFileUploadCallback callBack) {
        return fileUpLoadManager.batchUpLoad(mContext, requestList, callBack);
    }

    //@Override
    public List<APMultimediaTaskModel> batchDownLoad(List requestList, APFileDownCallback callBack) {
        return fileUpLoadManager.batchDownLoad(mContext, requestList, callBack);
    }

}
