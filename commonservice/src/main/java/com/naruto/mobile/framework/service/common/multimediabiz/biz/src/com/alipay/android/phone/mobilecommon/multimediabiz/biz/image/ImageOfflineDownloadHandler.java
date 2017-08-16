package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image;


import java.util.HashMap;
import java.util.Map;

import com.naruto.mobile.framework.service.common.multimedia.graphics.data.APImageOfflineDownloadReq;
import com.naruto.mobile.framework.service.common.multimedia.graphics.data.APImageOfflineDownloadRsp;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.api.FileApi;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.req.FileOfflineUploadReq;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.req.GetFilesMetaReq;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.resp.DjangoFileInfoResp;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.resp.FileOfflineUploadResp;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.resp.GetFilesMetaResp;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.ConvertUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Logger;

/**
 * 图片离线下载
 * Created by jinmin on 15/6/13.
 */
public class ImageOfflineDownloadHandler extends ImageHandler<APImageOfflineDownloadRsp> {

    private Logger logger = Logger.getLogger("ImageOfflineDownloadHandler");

    private APImageOfflineDownloadReq req;

    public ImageOfflineDownloadHandler(APImageOfflineDownloadReq req) {
        this.req = req;
    }

    @Override
    public APImageOfflineDownloadRsp call() {
        APImageOfflineDownloadRsp rsp = new APImageOfflineDownloadRsp();
        logger.d("ImageOfflineDownloadHandler call req: %s", req);
        rsp.setReq(req);
        FileApi api = getDjangoClient().getFileApi();
        FileOfflineUploadReq offlineUploadReq = new FileOfflineUploadReq();
        offlineUploadReq.downloadUrl = req.getDownloadUrl();
        offlineUploadReq.synchoronous = req.isWaitDownloadFinished();
        FileOfflineUploadResp offlineUploadResp = api.fileOfflineUpload(offlineUploadReq);
        if (offlineUploadResp != null) {
            rsp.setRetCode(offlineUploadResp.getCode());
            if (offlineUploadResp.isSuccess()) {
                DjangoFileInfoResp fileInfo = offlineUploadResp.getFileInfo();
                rsp.setCloudId(fileInfo.getId());
                Map<String, String> exif = new HashMap<String, String>();
                if (req.isWaitDownloadFinished() && !fileInfo.getExt().containsKey("exif")) {
                    //同步等待服务器下载图片结果
                    GetFilesMetaReq filesMetaReq = new GetFilesMetaReq(fileInfo.getId());
                    GetFilesMetaResp getFilesMetaResp = api.getFilesMeta(filesMetaReq);
                    if (getFilesMetaResp != null && getFilesMetaResp.isSuccess()) {
                        if (!getFilesMetaResp.getFilesMeta().isEmpty()) {
                            fileInfo = getFilesMetaResp.getFilesMeta().get(0);
                        }
                    }
                }
                if (fileInfo.getExt().containsKey("exif")) {
                    exif.putAll(fileInfo.getExt().get("exif"));
                }
                rsp.setExif(exif);
                if (exif.containsKey("width")) {
                    rsp.setWidth(ConvertUtils.parseInt(exif.get("width"), -1));
                }
                if (exif.containsKey("height")) {
                    rsp.setHeight(ConvertUtils.parseInt(exif.get("height"), -1));
                }
            }
        }
        logger.d("ImageOfflineDownloadHandler call rsp: %s", rsp);
        return rsp;
    }
}
