package com.naruto.mobile.base.serviceaop.task;

import com.naruto.mobile.base.serviceaop.MicroDescription;

public class ValueDescription extends MicroDescription{

    //线程名称
    private String mThreadName;
    //管道名称,一般是自定义的
    private String mPipeLineName;
    //权重
    private int mWeight;

    public String getThreadName() {
        return mThreadName;
    }

    public ValueDescription setThreadName(String threadName) {
        mThreadName = threadName;
        return this;
    }

    public String getPipeLineName() {
        return mPipeLineName;
    }

    public ValueDescription setPipeLineName(String pipeLineName) {
        mPipeLineName = pipeLineName;
        return this;
    }

    public int getWeight() {
        return mWeight;
    }

    public ValueDescription setWeight(int weight) {
        mWeight = weight;
        return this;
    }
}
