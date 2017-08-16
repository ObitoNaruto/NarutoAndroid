/*
 * Copyright 2014 Alibaba.com All right reserved.
 * This software is the confidential and proprietary information of Alibaba.com ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */

package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.api;


import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.BaseDownResp;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.req.StreamApiReq;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.resp.StreamCommitTaskResp;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.resp.StreamQueryTaskResp;

/**
 * 通用文件流处理操作接口
 *
 * @author jinzhaoyu
 */
public interface StreamApi {

    /**
     * 通用文件流处理任务提交接口 <br/>
     * http://api.django.t.taobao.com/rest/1.2/stream/task
     *
     * @param streamApiReq
     * @return
     */
    StreamCommitTaskResp commitTask(StreamApiReq streamApiReq);

    /**
     * 通用文件流处理任务状态查询接口 <br/>
     * http://api.django.t.taobao.com/rest/1.2/stream/task
     *
     * @param streamApiReq
     * @return
     */
    StreamQueryTaskResp queryTask(StreamApiReq streamApiReq);

    /**
     * 通用文件流处理展示接口 <br/>
     * http://api.django.t.taobao.com/rest/1.2/stream/content
     *
     * @param streamApiReq
     * @return
     */
    BaseDownResp getContent(StreamApiReq streamApiReq);

}
