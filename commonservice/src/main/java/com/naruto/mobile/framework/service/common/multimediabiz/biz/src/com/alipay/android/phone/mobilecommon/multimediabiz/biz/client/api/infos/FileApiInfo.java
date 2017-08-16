/*
 * Copyright 2014 Alibaba.com All right reserved.
 * This software is the confidential and proprietary information of Alibaba.com ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */

package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.api.infos;

import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.config.ServerAddress;

/**
 * 与文件操作相关的API集合
 *
 * @author jinzhaoyu
 */
public class FileApiInfo extends BaseApiInfo{
    /**
     * 直接上传小于10M的文件前，用于秒传的接口<br>
     * http://up.django.t.taobao.com/rest/1.1/file/head<br>
     */
    public static FileApiInfo UPLOAD_DIRECT_RAPID = new FileApiInfo(ServerAddress.ServerType.UPLOAD, "rest/1.1/file/head", HttpMethod.GET);

    /**
     * 直接上传小于10M的文件<br>
     * http://up.django.t.taobao.com/rest/1.0/file<br>
     */
    public static FileApiInfo UPLOAD_DIRECT = new FileApiInfo(ServerAddress.ServerType.UPLOAD, "rest/1.0/file", HttpMethod.POST);

    /**
     * 文件分块上传第一步，创建文件信息。必须要指定文件大小，必须要指定每块的大小或者直接指定分块数,当指定每块大小的情况下，接口会自动计算分块数<br>
     * http://up.django.t.taobao.com/rest/1.0/file/transaction
     */
    public static FileApiInfo UPLOAD_CHUNK_OPEN = new FileApiInfo(ServerAddress.ServerType.UPLOAD, "rest/1.0/file/transaction", HttpMethod.GET);

    /**
     * 文件分块上传第二步前，用于秒传分块接口<br>
     * http://up.django.t.taobao.com/rest/1.1/file/chunk/head
     */
    public static FileApiInfo UPLOAD_CHUNK_PROCESS_RAPID = new FileApiInfo(ServerAddress.ServerType.UPLOAD, "rest/1.1/file/chunk/head", HttpMethod.GET);

    /**
     * 文件分块上传第二步，上传分块的内容。<br>
     * http://up.django.t.taobao.com/rest/1.1/file/chunk
     */
    public static BaseApiInfo UPLOAD_CHUNK_PROCESS = new FileApiInfo(ServerAddress.ServerType.UPLOAD, "rest/1.1/file/chunk", HttpMethod.POST);

    /**
     * 文件分块上传第三步，在所有文件块都成功上传后，提交文件信息。必须传入fileId<br>
     * http://up.django.t.taobao.com/rest/1.0/file/transaction
     */
    public static FileApiInfo UPLOAD_CHUNK_COMMIT = new FileApiInfo(ServerAddress.ServerType.UPLOAD, "rest/1.0/file/transaction", HttpMethod.POST);

    /**
     * 批量获取文件元信息，比如文件分块数等<br>
     * http://api.django.t.taobao.com/rest/1.0/file/meta
     */
    public static FileApiInfo GET_FILES_META = new FileApiInfo(ServerAddress.ServerType.API, "rest/1.0/file/meta", HttpMethod.GET);

    /**
     * 批量文件下载，，多个文件 ID 之间用 ｜ 分割<br>
     * http://dl.django.t.taobao.com/rest/1.0/file
     */
    public static FileApiInfo DOWNLOAD_BATCH = new FileApiInfo(ServerAddress.ServerType.DOWNLOAD, "rest/1.0/file", HttpMethod.GET);

    /**
     * 批量删除文件<br>
     * http://api.django.t.taobao.com/rest/1.0/file
     */
    public static FileApiInfo DELETE_BATCH = new FileApiInfo(ServerAddress.ServerType.API, "rest/1.0/file", HttpMethod.DELETE);

    /**
     * 设置文件的扩展信息，如图片的exif信息等<br>
     * http://api.django.t.taobao.com/rest/1.0/file/ext
     */
    public static FileApiInfo SET_EXT = new FileApiInfo(ServerAddress.ServerType.API, "rest/1.0/file/ext", HttpMethod.POST);

    /**
     * 文件离线上传
     * 通过指定的 download_url 上传文件至 django
     * django 会在第一时间返回 django id，后台异步下载真正的数据流
     * 收到 django id 后，用户可以在第一时间访问该 django 文件
     */
    public static FileApiInfo UPLOAD_OFFLINE = new FileApiInfo(ServerAddress.ServerType.UPLOAD, "rest/1.3/file", HttpMethod.POST);

    /**
     * 批量查询文件元数据
     */
    public static FileApiInfo GET_META_INFO = new FileApiInfo(ServerAddress.ServerType.API, "rest/1.0/file/meta", HttpMethod.GET);

    /**
     * 秒传和断点结果查询
     */
    public static FileApiInfo UPLOAD_CHECK_RAPID_RANGE = new FileApiInfo(ServerAddress.ServerType.UPLOAD, "rest/r2.5/file/head", HttpMethod.GET);
    /**
     * 断点续上传
     * header
        Range: bytes=n-，表示这次上传是从文件的第n个字节开始上传，包括第n个字节，n从0开始计数。如果从文件头开始上传，Range头可以省略。
        举例：

        Range: bytes=0- 表示从文件头开始上传，
        Range: bytes=10- 表示从第11个字节开始上传。
     */
    public static FileApiInfo UPLOAD_FILE_RANGE = new FileApiInfo(ServerAddress.ServerType.UPLOAD, "rest/r2.5/file", HttpMethod.POST);

    private FileApiInfo(ServerAddress.ServerType serverType, String apiPath, HttpMethod httpMethod) {
        super(serverType, apiPath, httpMethod);
    }
}
