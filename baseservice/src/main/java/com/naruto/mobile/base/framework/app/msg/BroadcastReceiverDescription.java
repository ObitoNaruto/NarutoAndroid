package com.naruto.mobile.base.framework.app.msg;


import com.naruto.mobile.base.serviceaop.MicroDescription;

public class BroadcastReceiverDescription extends MicroDescription {
private String[] msgCode;

public String[] getMsgCode() {
    return msgCode;
}

public void setMsgCode(String[] msgCode) {
    this.msgCode = msgCode;
}

}
