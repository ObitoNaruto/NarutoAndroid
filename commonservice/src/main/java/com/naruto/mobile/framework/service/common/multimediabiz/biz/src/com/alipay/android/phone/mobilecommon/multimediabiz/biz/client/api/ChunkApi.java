/*
 * Copyright 2014 Alibaba.com All right reserved.
 * This software is the confidential and proprietary information of Alibaba.com ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */

package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.api;

import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.req.ChunksDownReq;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.req.FileChunksInfoReq;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.req.GetChunksMetaReq;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.resp.ChunksDownResp;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.resp.FileChunksInfoResp;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.resp.GetChunksMetaResp;

/**
 * 所有文件分块相关操作API
 *
 * @author jinzhaoyu
 */
public interface ChunkApi {

    /**
     * 查询文件所有块元数据,必须指定文件ID<br>
     *
     * @param fileChunksInfoReq 必须指定文件ID
     * @return
     * @throws com.naruto.android.phone.mobilecommon.multimediabiz.biz.client.exception.DjangoClientException
     */
    FileChunksInfoResp getFileChunksInfo(FileChunksInfoReq fileChunksInfoReq);

    /**
     * 获取文件块的元信息，比如分块的MD5、size等. 必须指定文件块ID<br>
     *
     * @param getChunksMetaReq 多个文件块 ID 之间用 ｜ 分割, chunkIds 格式：fileId#sequence ，注意，这里的sequence计数是从1开始的
     * @return
     * @throws com.naruto.android.phone.mobilecommon.multimediabiz.biz.client.exception.DjangoClientException
     */
    GetChunksMetaResp getChunksMeta(GetChunksMetaReq getChunksMetaReq);

    /**
     * 批量下载文件块，必须指定文件块ID，格式: fileId#chunkSequence , 注意，这里的chunkSequence 是从1开始计数的<br>
     * 解析响应流可以使用或参考 {@link com.naruto.android.phone.mobilecommon.multimediabiz.biz.client.io.DownloadResponseHelper}
     *
     * @param chunksDownReq 必须指定文件块ID，格式: fileId#chunkSequence , 注意，这里的chunkSequence 是从1开始计数的.多个文件块 ID 之间用 ｜ 分割
     * @return
     * @throws com.naruto.android.phone.mobilecommon.multimediabiz.biz.client.exception.DjangoClientException
     */
    ChunksDownResp downloadChunks(ChunksDownReq chunksDownReq);
}
