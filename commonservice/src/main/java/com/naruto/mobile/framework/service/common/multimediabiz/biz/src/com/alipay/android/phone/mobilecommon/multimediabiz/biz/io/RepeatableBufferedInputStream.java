package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.io;

import java.io.BufferedInputStream;
import java.io.InputStream;

public class RepeatableBufferedInputStream extends BufferedInputStream {
    public RepeatableBufferedInputStream(InputStream in) {
        super(in);
    }

    public void flip() {
        pos = 0;
    }
}
