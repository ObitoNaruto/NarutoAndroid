/*
 * Copyright 2014 Alibaba.com All right reserved.
 * This software is the confidential and proprietary information of Alibaba.com ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */

package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.api.infos;

import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.config.ServerAddress;

/**
 * 与通用文件流处理相关的API集合
 *
 * @author jinzhaoyu
 */
public class StreamApiInfo extends BaseApiInfo{
    /**
     * 通用文件流处理任务提交接口 <br/>
     * http://api.django.t.taobao.com/rest/1.2/stream/task
     */
    public static StreamApiInfo COMMIT_TASK = new StreamApiInfo(ServerAddress.ServerType.API, "rest/1.2/stream/task", HttpMethod.POST);

    /**
     * 通用文件流处理任务状态查询接口 <br/>
     * http://api.django.t.taobao.com/rest/1.2/stream/task
     */
    public static StreamApiInfo QUERY_TASK = new StreamApiInfo(ServerAddress.ServerType.API, "rest/1.2/stream/task", HttpMethod.GET);

    /**
     * 通用文件流处理展示接口 <br/>
     * http://api.django.t.taobao.com/rest/1.2/stream/content
     */
    public static StreamApiInfo GET_CONTENT = new StreamApiInfo(ServerAddress.ServerType.API, "rest/1.2/stream/content", HttpMethod.GET);

    private StreamApiInfo(ServerAddress.ServerType serverType, String apiPath, HttpMethod httpMethod) {
        super(serverType, apiPath, httpMethod);
    }
}
