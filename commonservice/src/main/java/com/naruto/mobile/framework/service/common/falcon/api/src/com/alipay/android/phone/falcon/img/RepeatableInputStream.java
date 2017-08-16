package com.naruto.mobile.framework.service.common.falcon.api.src.com.alipay.android.phone.falcon.img;

import java.io.ByteArrayInputStream;

public class RepeatableInputStream extends ByteArrayInputStream {
    RepeatableInputStream(byte[] data) {
        super(data);
    }

    public void flip() {
        pos = 0;
    }
   
    
} 
