package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.audio;


import com.naruto.mobile.framework.service.common.multimedia.audio.data.APRequestParam;

/**
 * 音频基础Task
 * Created by jinmin on 15/4/1.
 */
public abstract class AudioTask {
    /**
     * 未知状态
     */
    public static final int STATE_UNKNOWN = -1;
    /**
     * 初始化状态
     */
    public static final int STATE_INIT = 0;
    /**
     * 任务已提交状态
     */
    public static final int STATE_SUBMIT = 1;
    /**
     * 任务正在执行
     */
    public static final int STATE_RUNNING = 2;
    /**
     * 任务取消状态
     */
    public static final int STATE_CANCEL = 3;
    /**
     * 任务已完成状态
     */
    public static final int STATE_FINISH = 4;


    private int mState = STATE_UNKNOWN;

    private APRequestParam mReqParam;

    public int getState() {
        return this.mState;
    }

    public void setState(int state) {
        if (state < STATE_UNKNOWN || state > STATE_FINISH) {
            throw new IllegalArgumentException("state code error, please check your code!");
        }
        this.mState = state;
    }

    public APRequestParam getRequestParam() {
        return mReqParam;
    }

    public void setRequestParam(APRequestParam reqParam) {
        this.mReqParam = reqParam;
    }

    public boolean isRunning() {
        return STATE_RUNNING == mState;
    }

    public boolean isCanceled() {
        return STATE_CANCEL == mState;
    }

    public boolean isFinished() {
        return STATE_FINISH == mState;
    }
}
