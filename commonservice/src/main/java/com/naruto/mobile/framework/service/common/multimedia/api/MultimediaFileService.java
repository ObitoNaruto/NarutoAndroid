package com.naruto.mobile.framework.service.common.multimedia.api;

import android.os.Bundle;

import com.naruto.mobile.base.serviceaop.service.ext.ExternalService;
import com.naruto.mobile.framework.service.common.multimedia.api.data.APMultimediaTaskModel;
import com.naruto.mobile.framework.service.common.multimedia.file.APFileDownCallback;
import com.naruto.mobile.framework.service.common.multimedia.file.APFileUploadCallback;
import com.naruto.mobile.framework.service.common.multimedia.file.data.APFileDownloadRsp;
import com.naruto.mobile.framework.service.common.multimedia.file.data.APFileReq;
import com.naruto.mobile.framework.service.common.multimedia.file.data.APFileUploadRsp;

/**
 * Created by xiangui.fxg on 2015/4/15.
 */
public abstract class MultimediaFileService extends ExternalService {

    @Override
    protected void onCreate(Bundle bundle) {

    }

    @Override
    protected void onDestroy(Bundle bundle) {

    }

    /**
     * 取消下载
     * 如有callback, 会收到onDownloadError, code=APFileRsp.CODE_ERR_TASK_CANCELED
     *
     * @param taskId 任务id
     */
    public abstract void cancelLoad(String taskId);

    /**
     * 取消上传
     * 如有callback, 会收到onUploadError, code=APFileRsp.CODE_ERR_TASK_CANCELED
     *
     * @param taskId 任务id
     */
    public abstract void cancelUp(String taskId);

    /**
     * 根据任务id查询下载任务相关信息
     * 包括任务状态、创建时间等
     *
     * @param taskId 任务id
     * @return
     */
    public abstract APMultimediaTaskModel getLoadTaskStatus(String taskId);

    /**
     * 根据文件id查询下载任务相关信息
     * 包括任务状态、创建时间等。
     * 同一个文件id只会有一个任务
     *
     * @param cloudId 文件id
     * @return
     */
    public abstract APMultimediaTaskModel getLoadTaskStatusByCloudId(String cloudId);

    /**
     * 根据任务id查询上传任务相关信息
     * 包括任务状态、创建时间等
     *
     * @param taskId 任务id
     * @return
     */
    public abstract APMultimediaTaskModel getUpTaskStatus(String taskId);

    /**
     * 根据文件id查询上传任务相关信息
     * 包括任务状态、创建时间等。
     * 同一个文件id只会有一个任务
     *
     * @param cloudId 文件id
     * @return
     */
    public abstract APMultimediaTaskModel getUpTaskStatusByCloudId(String cloudId);

    /**
     * 发起下载, 异步
     *
     * @param url      文件id(暂不支持http开头的url, 建议采用DownloadService)
     * @param callback 下载的回调, 用于业务方获取下载相关信息
     * @return 任务相关信息, 包含任务id, 任务状态等
     * @see #downLoad(APFileReq, APFileDownCallback)
     */
    public abstract APMultimediaTaskModel downLoad(String url, APFileDownCallback callback);

    /**
     * 发起下载, 异步
     *
     * @param url      文件id(暂不支持http开头的url, 建议采用DownloadService)
     * @param callback 下载的回调, 用于业务方获取下载相关信息
     * @param savePath 下载的存储目录
     * @return 任务相关信息, 包含任务id, 任务状态等
     * @see #downLoad(APFileReq, APFileDownCallback)
     */
    public abstract APMultimediaTaskModel downLoad(String url, String savePath, APFileDownCallback callback);

    /**
     * 发起下载, 异步
     * 如未指定存储目录, 默认存在/sdcard/Android/data/com.eg.android.AlipayGphone/files/alipay/multimedia/alipay_files/目录
     * 该存储目录可在callback.onDownloadFinished()的参数通过APFileDownloadRsp.getFileReq().getSavePath()得到
     *
     * @param info     文件请求, 需指定文件的cloudId(暂不支持http开头的url, 建议采用DownloadService)
     * @param callback 下载的回调, 用于业务方获取下载相关信息
     * @return 任务相关信息, 包含任务id, 任务状态等
     */
    public abstract APMultimediaTaskModel downLoad(APFileReq info, APFileDownCallback callback);

    /**
     * 发起上传, 异步
     * 文件将被上传到cdn
     * 上传完成后, 文件id可在APFileUploadCallback.onUploadFinished()的参数通过APFileUploadRsp.getFileReq().getCloudId()得到
     *
     * @param info     文件请求, 需指定上传文件的路径savePath
     * @param callback 上传的回调, 用于业务方获取上传相关信息
     * @return 任务相关信息, 包含任务id, 任务状态等
     */
    public abstract APMultimediaTaskModel upLoad(APFileReq info, APFileUploadCallback callback);

    /**
     * 离线下载
     * 将文件url转成文件id, 同步方法, 不能在主线程调用
     * 完成后, 文件id可在APFileDownloadRsp.getFileReq().getCloudId()得到
     *
     * @param info 文件请求, 需通过setCloudId指定文件的url, isSync 是否等待cdn下载完成再返回APFileDownloadRsp
     */
    public abstract APFileDownloadRsp downloadOffline(APFileReq info);

    /**
     * 发起上传, 异步
     *
     * @see #upLoad(APFileReq, APFileUploadCallback)
     */
    public abstract APMultimediaTaskModel upLoad(String savePath, APFileUploadCallback callback);

    /**
     * 发起下载, 同步
     *
     * @see #downLoad(APFileReq, APFileDownCallback)
     */
    public abstract APFileDownloadRsp downLoadSync(APFileReq info, APFileDownCallback callback);

    /**
     * 发起上传, 同步
     *
     * @see #upLoad(APFileReq, APFileUploadCallback)
     */
    public abstract APFileUploadRsp upLoadSync(APFileReq info, APFileUploadCallback callback);

    /**
     * 监听下载任务
     *
     * @param taskId   任务id
     * @param callBack 新增的回调
     */
    public abstract void registeLoadCallBack(String taskId, APFileDownCallback callBack);

    /**
     * 取消监听下载任务
     *
     * @param taskId   任务id
     * @param callBack 需要取消的回调, 注意如果传null, 会取消该任务所有的callback
     */
    public abstract void unregisteLoadCallBack(String taskId, APFileDownCallback callBack);

    /**
     * 监听上传任务
     *
     * @param taskId   任务id
     * @param callBack 新增的回调
     */
    public abstract void registeUpCallBack(String taskId, APFileUploadCallback callBack);

    /**
     * 取消监听上传任务
     *
     * @param taskId   任务id
     * @param callBack 需要取消的回调, 注意如果传null, 会取消该任务所有的callback
     */
    public abstract void unregisteUpCallBack(String taskId, APFileUploadCallback callBack);

//    public abstract List<APMultimediaTaskModel> batchUpLoad(List requestList,APFileUploadCallback callBack);
//
//    public abstract List<APMultimediaTaskModel> batchDownLoad(List requestList,APFileDownCallback callBack);

    /**
     * 删除缓存
     * @param path      文件的id
     * @return          删除成功or失败, 文件不存在返回成功
     */
    public abstract boolean deleteFileCache(String path);
}
