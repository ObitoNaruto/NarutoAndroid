/*
 * Copyright 2014 Alibaba.com All right reserved.
 * This software is the confidential and proprietary information of Alibaba.com ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */

package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.api;


import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.exception.DjangoClientException;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.req.ChunkUpTxnCommitReq;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.req.ChunkUpTxnOpenReq;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.req.ChunkUpTxnProcessReq;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.req.FileOfflineUploadReq;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.req.FileRapidUpReq;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.req.FileUpReq;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.req.FilesDelReq;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.req.FilesDownReq;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.req.GetFilesMetaReq;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.req.InputStreamUpReq;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.req.SetExtReq;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.resp.ChunkUpTxnCommitResp;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.resp.ChunkUpTxnOpenResp;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.resp.ChunkUpTxnProcessResp;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.resp.FileOfflineUploadResp;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.resp.FileUpResp;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.resp.FilesDelResp;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.resp.FilesDownResp;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.resp.GetFilesMetaResp;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.resp.SetExtResp;

/**
 * 所有文件相关操作API
 *
 * @author jinzhaoyu
 */
public interface FileApi {

    /**
     * 文件分块，默认每块的大小: 4M
     */
    public static final long DEFAULT_TRUNK_SIZE = 4 * 1024 * 1024;

    /**
     * 直接上传小于10M的文件前，进行秒传检查的接口
     * @param fileRapidUpReq 必须传文件的md5或gcid属性
     * @return
     */
    FileUpResp uploadDirectRapid(FileRapidUpReq fileRapidUpReq);

    /**
     * 直接上传小于10M的文件<br>
     * 在文件上传之前SDK会检查 fileUpReq 参数是否设置了md5或者gcid属性，
     * 如果设置了这两个属性中的一个就去利用{@link com.naruto.android.phone.mobilecommon.multimediabiz.biz.client.api.infos.FileApiInfo#UPLOAD_DIRECT_RAPID}接口做秒传检查，
     * 只有秒传失败时才会继续上传文件.
     *
     * @param fileUpReq 如果关心已传输的字节数，可以设置其 transferedListener属性
     * @return
     * @throws com.naruto.android.phone.mobilecommon.multimediabiz.biz.client.exception.DjangoClientException
     */
    FileUpResp uploadDirect(FileUpReq fileUpReq);

//    /**
//     * 上传一个文件
//     * @param fileUpReq         文件上传请求
//     * @param skipRapid         是否跳过秒传检测
//     * @return
//     */
//    FileUpResp uploadDirect(FileUpReq fileUpReq, boolean skipRapid);

    /**
     * 直接上传小于10M文件的InputStream<br>
     * 在文件上传之前SDK会检查 fileUpReq 参数是否设置了md5或者gcid属性，
     * 如果设置了这两个属性中的一个就去利用{@link com.naruto.android.phone.mobilecommon.multimediabiz.biz.client.api.infos.FileApiInfo#UPLOAD_DIRECT_RAPID}接口做秒传检查，
     * 只有秒传失败时才会继续上传文件.
     *
     * @param inputStreamUpReq 如果关心已传输的字节数，可以设置其 transferedListener属性
     * @return
     * @throws com.naruto.android.phone.mobilecommon.multimediabiz.biz.client.exception.DjangoClientException
     */
    FileUpResp uploadDirect(InputStreamUpReq inputStreamUpReq);

    /**
     * 文件分块上传第一步，创建文件信息。<br/>
     * <B>>必须要指定文件大小，文件将被以每块4M的大小切分</B>
     *
     * @param openReq 必须要指定文件大小，文件将被以每块4M的大小切分
     * @return
     * @throws com.naruto.android.phone.mobilecommon.multimediabiz.biz.client.exception.DjangoClientException
     * @see #DEFAULT_TRUNK_SIZE
     */
    ChunkUpTxnOpenResp uploadChunkOpen(ChunkUpTxnOpenReq openReq);

    /**
     *  文件分块上传第二步，上传分块的内容前，进行秒传检查的接口
     * @param processReq 必须传文件的md5或gcid属性
     * @return
     */
    ChunkUpTxnProcessResp uploadChunkProcessRapid(ChunkUpTxnProcessReq processReq);

    /**
     * 文件分块上传第二步，上传分块的内容。每块的文件大小必须为 4M <br>
     * 必须要指定{@link #uploadChunkOpen(ChunkUpTxnOpenReq)}返回的fileId、文件、sequence(从1开始)<br>
     * 在分块上传之前SDK会检查 processReq 参数是否设置了md5或者gcid属性，
     * 如果设置了这两个属性中的一个就去利用{@link com.naruto.android.phone.mobilecommon.multimediabiz.biz.client.api.infos.FileApiInfo#UPLOAD_CHUNK_PROCESS_RAPID}接口做秒传检查，
     * 只有秒传失败时才会继续上传分块.
     *
     * @param processReq 如果关心上传进度，则可以设置其chunkTransListener属性
     * @return
     * @throws com.naruto.android.phone.mobilecommon.multimediabiz.biz.client.exception.DjangoClientException
     */
    ChunkUpTxnProcessResp uploadChunkProcess(ChunkUpTxnProcessReq processReq);

    /**
     * 文件分块上传第三步，在所有文件块都成功上传后，提交文件信息。必须传入fileId<br>
     * http://up.django.t.taobao.com/rest/1.0/file/transaction
     *
     * @param commitReq 必须传入fileId
     * @return
     * @throws com.naruto.android.phone.mobilecommon.multimediabiz.biz.client.exception.DjangoClientException
     */
    ChunkUpTxnCommitResp uploadChunkCommit(ChunkUpTxnCommitReq commitReq);

    /**
     * 批量获取文件元信息，比如文件分块数等<br>
     * http://api.django.t.taobao.com/rest/1.0/file/meta
     *
     * @param req 必须传入fileIds，多个文件 ID 之间用 ｜ 分割
     * @return
     * @throws com.naruto.android.phone.mobilecommon.multimediabiz.biz.client.exception.DjangoClientException
     */
    GetFilesMetaResp getFilesMeta(GetFilesMetaReq req);

    /**
     * 批量文件下载，，多个文件 ID 之间用 ｜ 分割<br>
     * http://up.django.t.taobao.com/rest/1.0/file <br/>
     * 解析响应流可以使用或参考 {@link com.naruto.android.phone.mobilecommon.multimediabiz.biz.client.io.DownloadResponseHelper}
     *
     * @param req 必须传入fileIds，多个文件 ID 之间用 ｜ 分割
     * @return
     * @throws com.naruto.android.phone.mobilecommon.multimediabiz.biz.client.exception.DjangoClientException
     */
    FilesDownResp downloadBatch(FilesDownReq req);

    /**
     * 批量删除文件<br>
     * http://api.django.t.taobao.com/rest/1.0/file
     *
     * @param req
     * @return
     * @throws com.naruto.android.phone.mobilecommon.multimediabiz.biz.client.exception.DjangoClientException
     */
    FilesDelResp deleteBatch(FilesDelReq req);

    /**
     * 设置文件的扩展信息，如图片的exif信息等<br>
     * http://api.django.t.taobao.com/rest/1.0/file/ext
     *
     * @param req
     * @return
     * @throws com.naruto.android.phone.mobilecommon.multimediabiz.biz.client.exception.DjangoClientException
     */
    SetExtResp setExt(SetExtReq req);

    /**
     * 文件离线下载
     * @param req
     * @return
     */
    FileOfflineUploadResp fileOfflineUpload(FileOfflineUploadReq req);

    /**
     * 秒传和断点续上传检查的接口
     * @param fileRapidUpReq 必须传文件的md5或gcid属性
     * @return
     */
    FileUpResp uploadRapidRange(FileRapidUpReq fileRapidUpReq);

    /**
     * 分片上传接口
     * @param fileUpReq 必须传文件的md5或gcid属性
     * @return
     */
    FileUpResp uploadRange(FileUpReq fileUpReq) throws DjangoClientException;
    /**
     * 分片上传接口
     * @param upReq 必须传文件的md5或gcid属性
     * @return
     */
    FileUpResp uploadRange(InputStreamUpReq upReq) throws DjangoClientException;
}
