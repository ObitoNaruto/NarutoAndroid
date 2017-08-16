package com.naruto.mobile.framework.service.common.multimedia.api;

import android.os.Bundle;

import com.naruto.mobile.base.serviceaop.service.ext.ExternalService;
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

public abstract class MultimediaAudioService extends ExternalService {

    @Override
    protected void onCreate(Bundle bundle) {
    }

    /**
     * 开始录音
     * @param callback  录音与上传状态回调
     */
    public abstract void startRecord(APAudioRecordUploadCallback callback);

    /**
     * 开始录音
     * @param info  录音配置：配置最长录音时长，最短时长，文件保存路径
     * @param callback  录音相关回调
     */
    public abstract void startRecord(APAudioInfo info, APAudioRecordUploadCallback callback);

    /**
     * 开始录音
     * @param info  录音相关配置
     * @param param 请求相关参数：ACL、UID
     * @param callback  录音上传相关回调
     */
    public abstract void startRecord(APAudioInfo info, APRequestParam param, APAudioRecordUploadCallback callback);
    /**
     * 开始录音，只录音不上传
     * @param cb   录音相关回调
     */
    public abstract void startRecord(APAudioRecordCallback cb);
    /**
     * 开始录音，只录音不上传
     * @param info  录音相关配置
     * @param cb    录音上传相关回调
     */
    public abstract void startRecord(APAudioInfo info, APAudioRecordCallback cb);

    /**
     * 取消当前录音
     */
    public abstract void cancelRecord();
    /**
     * 取消录音，录音文件会删除
     * @param info  任务ID
     */
//    private void cancelRecord(APAudioInfo info) {
//        mAudioTaskManager.cancelRecord();
//    }

    /**
     * 停止当前录音
     */
    public abstract void stopRecord();

    /**
     * 停止录音，录音文件会自动上传
     * @param info  任务ID
     */
//    public abstract void stopRecord(APAudioInfo info) {
//        mAudioTaskManager.stopRecord(info);
//    }

    /**
     * 上传录音
     * @param info  录音信息，需要指定 LocalId 或 SavePath
     * @param cb    异步上传信息回调
     */
    public abstract void uploadRecord(APAudioInfo info, APAudioUploadCallback cb);

    /**
     * 上传录音，一般是录音自动上传失败，重新上传
     * @param info  任务ID
     * @param param 请求相关参数：ACL、UID
     * @param cb    音频文件上传
     */
    public abstract void uploadRecord(APAudioInfo info, APRequestParam param, APAudioUploadCallback cb);

    /**
     * 同步上传录音文件
     * @param info  录音信息，需要指定 LocalId 或 SavePath
     * @return  上传结果
     */
    public abstract APAudioUploadRsp uploadRecordSync(APAudioInfo info);

    /**
     * 同步上传录音文件
     * @param info  录音信息，需要指定 LocalId 或 SavePath
     * @param param Django相关参数
     * @return  上传结果
     */
    public abstract APAudioUploadRsp uploadRecordSync(APAudioInfo info, APRequestParam param);

    /**
     * 播放音频
     * @param info  音频信息：需要指定 LocalId 或 SavePath 或 CloudId
     * @param cb    播放相关信息回调
     */
    public abstract void startPlay(APAudioInfo info, APAudioPlayCallback cb);

    /**
     * 开始播放
     * @param info  音频文件ID
     * @param param 请求相关参数：ACL、UID
     * @param callback  播放相关回调
     *                  【APAudioDownloadPlayCallback】: 带下载状态和播放状态回调
     *                  【APAudioPlayCallback】： 自动下载，不回调下载状态，只回调播放状态
     */
    public abstract void startPlay(APAudioInfo info, APRequestParam param, APAudioPlayCallback callback);

    /**
     * 获取当前播放音频，已播放时长
     * @return -1：没有音频播放，否则正常已播放播放时长，单位 ms
     */
    public abstract long getPlayCurrentPosition();

    /**
     * 暂停播放
     */
    public abstract void pausePlay();
    /**
     * 继续播放
     */
    public abstract void resumePlay();
    /**
     * 停止播放
     */
    public abstract void stopPlay();
    /**
     * 判断当前是否正在播放
     * @return
     */
    public abstract boolean isPlaying();
    /**
     * 获取正在播放的音频信息
     * @return  null：没有正在播放的音频
     */
    public abstract APAudioInfo getPlayingAudioInfo();

    /**
     * 提交一个音频下载的任务
     * @param cloudId   音频对应的CloudId
     * @param cb    下载的回调
     */
    public abstract APMultimediaTaskModel submitAudioDownloadTask(String cloudId, APAudioDownloadCallback cb);
    /**
     * 提交一个音频下载的任务
     * @param info  音频信息：需要包含cloudId
     * @param cb    下载的回调
     */
    public abstract APMultimediaTaskModel submitAudioDownloadTask(APAudioInfo info, APAudioDownloadCallback cb);

    /**
     * 同步下载一个音频文件
     * @param cloudId
     * @return
     */
    public abstract APAudioDownloadRsp downloadAudio(String cloudId);

    /**
     * 同步下载一个音频文件
     * @param info
     * @return
     */
    public abstract APAudioDownloadRsp downloadAudio(APAudioInfo info);


    /**
     * 判断音频文件是否已经下载好
     * @param info  音频文件描述, 需要填入LocalId 或 CloudId 或 Path
     * @return  true：音频已存在， false：音频不存在
     */
    public abstract boolean checkAudioReady(APAudioInfo info);

    /**
     * 注册音频播放时，播放输出方式改变的回调
     * @param listener  变化的回调
     */
    public abstract void registerAudioPlayOutputModeChangeListenr(APAudioPlayOutputModeChangeListener listener);

    /**
     * 反注册音频播放时，播放输出方式改变的回调
     * @param listener  之前注册的变化回调
     */
    public abstract void unregisterAudioPlayOutputModeChangeListenr(APAudioPlayOutputModeChangeListener listener);
    /**
     * 配置
     */
    public abstract void setAudioConfiguration(APAudioConfiguration configuration) ;

    /**
     * 获取配置
     * @return
     */
    public abstract APAudioConfiguration getAudioConfiguration();

    /**
     * 删除缓存
     * @param path      音频的id path localId
     * @return
     */
    public abstract int deleteCache(String path);

}
