package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.io;

import java.io.ByteArrayInputStream;

public class RepeatableInputStream extends ByteArrayInputStream {
    public RepeatableInputStream(byte[] data) {
        super(data);
    }

    public void flip() {
        pos = 0;
    }
}