package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.file;

import android.content.Context;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.naruto.mobile.framework.service.common.multimedia.api.data.APMultimediaTaskModel;
import com.naruto.mobile.framework.service.common.multimedia.file.APFileUploadCallback;
import com.naruto.mobile.framework.service.common.multimedia.file.data.APFileReq;

/**
 * 文件任务工厂
 * Created by jinmin on 15/8/6.
 */
public class FileTaskFactory {

    public static FileTask createUploadTask(Context context, APFileReq req,
                                            Options options, APMultimediaTaskModel taskModel,
                                            APFileUploadCallback cb) {
        FileTask task;
        List<APFileReq> list = new ArrayList<APFileReq>();
        list.add(req);
        if ((options != null && (Options.UPLOAD_TYPE_SLICE == options.uploadType))
                || (new File(req.getSavePath()).length() < FileUploadTask.BIG_FILE_SIZE_THRESHOLD)) {
            task = new FileSliceUploadTask(context, list, options, taskModel, cb);
        } else {
            task = new FileUploadTask(context, list, taskModel, cb);
        }
        return task;
    }
}
