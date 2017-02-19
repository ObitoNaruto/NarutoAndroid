package com.naruto.mobile.CountDownTimer;

import android.os.CountDownTimer;

public class CountDownTimerManager extends CountDownTimer{
    public static final long TOTAL_TIME = 60 * 1000;

    public static final long INTERVAL = 1000;

    public interface OnTimeCountListener {

        void onTick(long secondUntilFinished);

        void onFinish();
    }

    private OnTimeCountListener mOnTimeCountListener;

    public void setOnTimeCountListener(OnTimeCountListener onTimeCountListener) {
        mOnTimeCountListener = onTimeCountListener;
    }

    public CountDownTimerManager(long millisInFuture, long countDownInterval, OnTimeCountListener onTimeCountListener) {
        super(millisInFuture, countDownInterval);
        this.mOnTimeCountListener = onTimeCountListener;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        long secondUntilFinished = millisUntilFinished / 1000;
        if (mOnTimeCountListener != null) {
            mOnTimeCountListener.onTick(secondUntilFinished);
        }
    }

    @Override
    public void onFinish() {
        if (mOnTimeCountListener != null) {
            mOnTimeCountListener.onFinish();
        }
    }
}
