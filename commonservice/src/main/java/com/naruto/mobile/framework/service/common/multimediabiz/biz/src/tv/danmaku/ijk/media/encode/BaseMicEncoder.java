package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.tv.danmaku.ijk.media.encode;


import java.lang.ref.WeakReference;

import com.naruto.mobile.framework.service.common.multimedia.video.data.APVideoRecordRsp;
import com.naruto.mobile.framework.service.common.multimedia.widget.SightCameraView;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Logger;

/**
 * Created by jinmin on 15/8/22.
 */
public abstract class BaseMicEncoder {
    private Logger logger = Logger.getLogger("MicEncoder");

    private WeakReference<SightCameraView.OnRecordListener> mRecordListener;

    public void setRecordListener(SightCameraView.OnRecordListener listener) {
        this.mRecordListener = new WeakReference<SightCameraView.OnRecordListener>(listener);
    }

    public SightCameraView.OnRecordListener getRecordListener() {
        return mRecordListener != null ? mRecordListener.get() : null;
    }

    protected void notifyError(int code) {
        logger.e("notifyError code: " + code);
        if(mRecordListener != null && mRecordListener.get() != null) {
            APVideoRecordRsp rsp = new APVideoRecordRsp();
            rsp.mRspCode = code;
            mRecordListener.get().onError(rsp);
        }
    }
}
