package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.task;

import android.graphics.Bitmap;
import android.widget.ImageView;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.naruto.mobile.framework.service.common.multimedia.graphics.data.APImageRetMsg;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.ImageLoadReq;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.assist.ViewWrapper;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.exception.CancelException;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Logger;

/**
 * 网络加载任务
 * Created by jinmin on 15/7/13.
 */
public abstract class ImageNetTask extends ImageTask<Bitmap> {

    protected final Set<ImageLoadReq> loadReqSet = Collections.synchronizedSet(new HashSet<ImageLoadReq>());

    public ImageNetTask(ImageLoadReq loadReq, ViewWrapper<ImageView> wrapper) {
        super(loadReq, wrapper);
        addImageLoadReq(loadReq);
    }

    @Override
    public Bitmap call() throws Exception {
        long start = System.currentTimeMillis();
        try {
            //等待队列不空的情况不check
            //TODO:如果要check, 等待队列也需要check reuse?
            if (loadReqSet.size() == 1 && checkTask()) {
                Logger.P("ImageNetTask", "call ImageNetTask loadReqSet.size() == 1 return !");
                return null;
            }
            return executeTask();
        } finally {
            ImageTaskEngine.get().removeTask(getTaskId());
            Logger.TIME("ImageNetTask call " + loadReq.path + ", costTime: "
                    + (System.currentTimeMillis() - start) + ", " + loadReq.path);
        }

    }

    protected abstract Bitmap executeTask() throws Exception;

    public String getTaskId() {
        return loadReq.path;
    }

    public void addImageLoadReq(ImageLoadReq loadReq) {
        loadReqSet.add(loadReq);
    }

    @Override
    protected void notifyCancel() {
        Exception e = new CancelException();
        for (ImageLoadReq req : loadReqSet) {
            if (req.downLoadCallback != null) {
                notifyError(req, APImageRetMsg.RETCODE.CANCEL, "load cancel", e);
            }
        }
        loadReqSet.clear();
    }

    @Override
    protected void notifyReuse() {
        Exception e = new CancelException();
        for (ImageLoadReq req : loadReqSet) {
            if (req.downLoadCallback != null) {
                notifyError(req, APImageRetMsg.RETCODE.REUSE, "load reuse", e);
            }
        }
        loadReqSet.clear();
    }

    protected void notifyError(Exception e) {
        String errMsg = getExceptionInfo(e);
        for (ImageLoadReq req : loadReqSet) {
            if (req.downLoadCallback != null) {
                notifyError(req, APImageRetMsg.RETCODE.DOWNLOAD_FAILED, errMsg, e);
            }
        }
        loadReqSet.clear();
    }

    protected String getExceptionInfo(Exception e) {
        String msg = e != null ? e.getClass().getSimpleName() + ":" + e.getMessage() : "download fail";
        msg += ", loadReq: " + loadReq;
        return msg;
    }
}
