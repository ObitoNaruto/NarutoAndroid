package com.naruto.mobile.base.serviceaop.task;

import com.naruto.mobile.base.serviceaop.MicroDescription;

public class ValueDescription extends MicroDescription{

    private String mThreadName;
    private String mPipelingName;
    private int mWeight;

    public String getThreadName() {
        return mThreadName;
    }

    public void setThreadName(String threadName) {
        mThreadName = threadName;
    }

    public String getPipelingName() {
        return mPipelingName;
    }

    public void setPipelingName(String pipelingName) {
        mPipelingName = pipelingName;
    }

    public int getWeight() {
        return mWeight;
    }

    public void setWeight(int weight) {
        mWeight = weight;
    }
}
