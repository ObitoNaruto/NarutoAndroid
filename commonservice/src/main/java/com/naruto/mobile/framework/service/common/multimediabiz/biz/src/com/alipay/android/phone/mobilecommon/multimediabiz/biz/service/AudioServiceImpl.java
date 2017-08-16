package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.service;

import android.content.Context;
import android.os.Bundle;

import com.naruto.mobile.base.serviceaop.NarutoApplication;
import com.naruto.mobile.framework.service.common.multimedia.api.MultimediaAudioService;
import com.naruto.mobile.framework.service.common.multimedia.api.data.APMultimediaTaskModel;
import com.naruto.mobile.framework.service.common.multimedia.audio.APAudioDownloadCallback;
import com.naruto.mobile.framework.service.common.multimedia.audio.APAudioPlayCallback;
import com.naruto.mobile.framework.service.common.multimedia.audio.APAudioPlayOutputModeChangeListener;
import com.naruto.mobile.framework.service.common.multimedia.audio.APAudioRecordCallback;
import com.naruto.mobile.framework.service.common.multimedia.audio.APAudioRecordUploadCallback;
import com.naruto.mobile.framework.service.common.multimedia.audio.APAudioUploadCallback;
import com.naruto.mobile.framework.service.common.multimedia.audio.data.APAudioConfiguration;
import com.naruto.mobile.framework.service.common.multimedia.audio.data.APAudioDownloadRsp;
import com.naruto.mobile.framework.service.common.multimedia.audio.data.APAudioInfo;
import com.naruto.mobile.framework.service.common.multimedia.audio.data.APAudioUploadRsp;
import com.naruto.mobile.framework.service.common.multimedia.audio.data.APRequestParam;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.audio.AudioTaskManager;

/**
 * 多媒体音频Service实现
 * Created by jinmin on 15/4/18.
 */
public class AudioServiceImpl extends MultimediaAudioService {

    private AudioTaskManager mAudioTaskManager;
    private final APRequestParam defaultRequestParam = new APRequestParam("ACL", "UID");

    protected void onCreate(Bundle params) {
        Context context = NarutoApplication.getInstance().getNarutoApplicationContext().getApplicationContext();
        mAudioTaskManager = AudioTaskManager.getInstance(context);
    }

    protected void onDestroy(Bundle params) {

    }

    /**
     * 开始录音
     * @param callback              录音与上传状态回调
     */
    public void startRecord(APAudioRecordUploadCallback callback) {
        startRecord(new APAudioInfo(), callback);
    }

    /**
     * 开始录音
     * @param info                  录音配置：配置最长录音时长，最短时长，文件保存路径
     * @param callback              录音相关回调
     */
    public void startRecord(APAudioInfo info, APAudioRecordUploadCallback callback) {
        startRecord(info, defaultRequestParam, callback);
    }

    /**
     * 开始录音
     * @param info                  录音相关配置
     * @param param                 请求相关参数：ACL、UID
     * @param callback              录音上传相关回调
     */
    public void startRecord(APAudioInfo info, APRequestParam param, APAudioRecordUploadCallback callback) {
        mAudioTaskManager.startRecord(info, param, callback);
    }
    /**
     * 开始录音，只录音不上传
     * @param cb              录音相关回调
     */
    public void startRecord(APAudioRecordCallback cb) {
        startRecord(new APAudioInfo(), cb);
    }
    /**
     * 开始录音，只录音不上传
     * @param info                  录音相关配置
     * @param cb              录音上传相关回调
     */
    public void startRecord(APAudioInfo info, APAudioRecordCallback cb) {
        mAudioTaskManager.startRecord(info, cb);
    }

    /**
     * 取消当前录音
     */
    public void cancelRecord() {
        mAudioTaskManager.cancelRecord();
    }

    /**
     * 取消录音，录音文件会删除
     * @param info                  任务ID
     */
//    private void cancelRecord(APAudioInfo info) {
//        mAudioTaskManager.cancelRecord();
//    }

    /**
     * 停止当前录音
     */
    public void stopRecord() {
        mAudioTaskManager.stopRecord();
    }

    /**
     * 停止录音，录音文件会自动上传
     * @param info               任务ID
     */
//    public void stopRecord(APAudioInfo info) {
//        mAudioTaskManager.stopRecord(info);
//    }

    /**
     * 上传录音
     * @param info              录音信息，需要指定 LocalId 或 SavePath
     * @param cb                异步上传信息回调
     */
    public void uploadRecord(APAudioInfo info, APAudioUploadCallback cb) {
        uploadRecord(info, defaultRequestParam, cb);
    }

    /**
     * 上传录音，一般是录音自动上传失败，重新上传
     * @param info               任务ID
     * @param param              请求相关参数：ACL、UID
     * @param cb                 音频文件上传
     */
    public void uploadRecord(APAudioInfo info, APRequestParam param, APAudioUploadCallback cb) {
        mAudioTaskManager.uploadAudio(info, param, cb);
    }

    /**
     * 同步上传录音文件
     * @param info              录音信息，需要指定 LocalId 或 SavePath
     * @return                  上传结果
     */
    public APAudioUploadRsp uploadRecordSync(APAudioInfo info) {
        return uploadRecordSync(info, defaultRequestParam);
    }

    /**
     * 同步上传录音文件
     * @param info              录音信息，需要指定 LocalId 或 SavePath
     * @param param             Django相关参数
     * @return                  上传结果
     */
    public APAudioUploadRsp uploadRecordSync(APAudioInfo info, APRequestParam param) {
        return mAudioTaskManager.uploadAudioSync(info, param);
    }

    /**
     * 播放音频
     * @param info                  音频信息：需要指定 LocalId 或 SavePath 或 CloudId
     * @param cb                    播放相关信息回调
     */
    public void startPlay(APAudioInfo info, APAudioPlayCallback cb) {
        startPlay(info, defaultRequestParam, cb);
    }


    /**
     * 开始播放
     * @param info                  音频文件ID
     * @param param                 请求相关参数：ACL、UID
     * @param callback              播放相关回调
     */
    public void startPlay(APAudioInfo info, APRequestParam param, APAudioPlayCallback callback) {
        mAudioTaskManager.playAudio(info, param, callback);
    }

    @Override
    public long getPlayCurrentPosition() {
        return mAudioTaskManager.getPlayCurrentPosition();
    }

    /**
     * 停止播放
     */
    public void stopPlay() {
        mAudioTaskManager.stopPlayAudio();
    }

    @Override
    public void pausePlay() {
        mAudioTaskManager.pausePlayAudio();
    }

    @Override
    public void resumePlay() {
        mAudioTaskManager.resumePlayAudio();
    }

    /**
     * 判断当前是否正在播放
     * @return
     */
    public boolean isPlaying() {
        return mAudioTaskManager.isPlaying();
    }

    /**
     * 获取正在播放的音频信息
     * @return      null：没有正在播放的音频
     */
    public APAudioInfo getPlayingAudioInfo() {
        return mAudioTaskManager.getPlayingAudioInfo();
    }

    /**
     * 提交一个音频下载的任务
     * @param cloudId               音频对应的CloudId
     * @param cb                    下载的回调
     */
    public APMultimediaTaskModel submitAudioDownloadTask(String cloudId, APAudioDownloadCallback cb) {
        return submitAudioDownloadTask(new APAudioInfo(null, cloudId, null), cb);
    }

    /**
     * 提交一个音频下载的任务
     * @param info                  音频信息：需要包含cloudId
     * @param cb                    下载的回调
     */
    public APMultimediaTaskModel submitAudioDownloadTask(APAudioInfo info, APAudioDownloadCallback cb) {
        return submitAudioDownloadTask(info, defaultRequestParam, cb);
    }

    /**
     * 提交一个音频下载的任务
     * @param info                  音频信息：需要包含cloudId
     * @param param                 Django相关参数
     * @param cb                    下载的回调
     */
    private APMultimediaTaskModel submitAudioDownloadTask(APAudioInfo info, APRequestParam param, APAudioDownloadCallback cb) {
        return mAudioTaskManager.submitAudioDownloadTask(info, param, cb);
    }

    /**
     * 同步下载一个音频文件
     * @param cloudId
     * @return
     */
    @Override
    public APAudioDownloadRsp downloadAudio(String cloudId) {
        return downloadAudio(APAudioInfo.fromCloudId(cloudId));
    }

    /**
     * 同步下载一个音频文件
     * @param info
     * @return
     */
    @Override
    public APAudioDownloadRsp downloadAudio(APAudioInfo info) {
        return mAudioTaskManager.downloadAudio(info, defaultRequestParam);
    }

    /**
     * 判断音频文件是否已经下载好
     * @param info          音频文件描述, 需要填入LocalId 或 CloudId 或 Path
     * @return              true：音频已存在， false：音频不存在
     */
    public boolean checkAudioReady(APAudioInfo info) {
        return mAudioTaskManager.checkAudioOk(info);
    }

    /**
     * 注册音频播放时，播放输出方式改变的回调
     * @param listener              变化的回调
     */
    public void registerAudioPlayOutputModeChangeListenr(APAudioPlayOutputModeChangeListener listener) {
        mAudioTaskManager.registerAudioPlayOutputModeChangeListener(listener);
    }

    /**
     * 反注册音频播放时，播放输出方式改变的回调
     * @param listener              之前注册的变化回调
     */
    public void unregisterAudioPlayOutputModeChangeListenr(APAudioPlayOutputModeChangeListener listener) {
        mAudioTaskManager.unregisterAudioPlayOutputModeChangeListener(listener);
    }

    /**
     * 配置
     */
    public void setAudioConfiguration(APAudioConfiguration configuration) {
        mAudioTaskManager.setAudioConfiguration(configuration);
    }

    public APAudioConfiguration getAudioConfiguration() {
        return mAudioTaskManager.getAudioConfiguration();
    }

    @Override
    public int deleteCache(String path) {
        return mAudioTaskManager.deleteCache(path);
    }
}