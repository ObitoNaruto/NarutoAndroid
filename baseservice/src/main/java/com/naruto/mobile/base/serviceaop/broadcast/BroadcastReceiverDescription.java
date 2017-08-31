package com.naruto.mobile.base.serviceaop.broadcast;

import com.naruto.mobile.base.serviceaop.MicroDescription;

public class BroadcastReceiverDescription extends MicroDescription {

    //广播的action集合
    private String[] msgCode;

    public String[] getMsgCode() {
        return msgCode;
    }

    public BroadcastReceiverDescription setMsgCode(String[] msgCode) {
        this.msgCode = msgCode;
        return this;
    }
}
