package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.file;

import android.content.Context;

import java.util.List;

import com.naruto.mobile.framework.service.common.multimedia.api.data.APMultimediaTaskModel;
import com.naruto.mobile.framework.service.common.multimedia.file.data.APFileDownloadRsp;
import com.naruto.mobile.framework.service.common.multimedia.file.data.APFileReq;
import com.naruto.mobile.framework.service.common.multimedia.file.data.APFileRsp;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.DjangoClient;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.req.FileOfflineUploadReq;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.resp.FileOfflineUploadResp;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Logger;

public class OfflineDownloadTask extends FileTask {
    private static final String TAG = OfflineDownloadTask.class.getSimpleName();
    private Logger logger = Logger.getLogger(TAG);

    protected OfflineDownloadTask(Context context, List reqList,
            APMultimediaTaskModel taskInfo) {
        super(context, reqList, taskInfo);
    }

    @Override
    public APFileDownloadRsp call() throws Exception {
        APFileReq req = (APFileReq) fileReqList.get(0);
        APFileDownloadRsp rsp = new APFileDownloadRsp();
        DjangoClient djangoClient = getDjangoClient(req.getRequestParam());

        FileOfflineUploadReq fileOfflineUploadReq = new FileOfflineUploadReq();
        fileOfflineUploadReq.downloadUrl = req.getCloudId();
        fileOfflineUploadReq.synchoronous = req.isSync();

        logger.d("fileOfflineUploadReq req: " + fileOfflineUploadReq);
        FileOfflineUploadResp resp = djangoClient.getFileApi().fileOfflineUpload(fileOfflineUploadReq);
        logger.d("fileOfflineUpload resp: " + resp);

        if (resp != null && resp.isSuccess()) {
            req.setCloudId(resp.getFileInfo().getId());
            rsp.setRetCode(APFileRsp.CODE_SUCCESS);
            rsp.setMsg(resp.getMsg());
        } else if (resp != null) {
            rsp.setRetCode(resp.getCode());
            rsp.setMsg(resp.getMsg());
        } else {
            rsp.setRetCode(APFileRsp.CODE_ERR_RSP_NULL);
            rsp.setMsg("FileOfflineUploadResp is null");
        }
        return rsp;
    }
}
