package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.api.infos;


import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.config.ServerAddress;

/**
 * Created by xiangui.fxg on 2015/7/13.
 */
public class ChunkApiInfo extends BaseApiInfo{
    /**
     * 查询文件所有块元数据,必须指定文件ID<br>
     * http://api.django.t.taobao.com/rest/1.0/chunkinfo
     */
    public static ChunkApiInfo GET_FILE_CHUNKS_INFO = new ChunkApiInfo(ServerAddress.ServerType.API, "rest/1.0/chunkinfo", HttpMethod.GET);

    /**
     * 获取文件块的元信息，比如分块的MD5、size等. 必须指定文件块ID<br>
     * http://api.django.t.taobao.com/rest/1.0/chunk/meta
     */
    public static ChunkApiInfo GET_CHUNKS_META = new ChunkApiInfo(ServerAddress.ServerType.API, "rest/1.0/chunk/meta", HttpMethod.GET);

    /**
     * 批量下载文件块，必须指定文件块ID，格式: fileId#chunkSequence , 注意，这里的chunkSequence 是从1开始计数的<br>
     * http://dl.django.t.taobao.com/rest/1.0/chunk
     */
    public static ChunkApiInfo DOWNLOAD_CHUNKS = new ChunkApiInfo(ServerAddress.ServerType.DOWNLOAD, "rest/1.0/chunk", HttpMethod.GET);

    private ChunkApiInfo(ServerAddress.ServerType serverType, String apiPath, HttpMethod httpMethod) {
        super(serverType, apiPath, httpMethod);
    }
}
