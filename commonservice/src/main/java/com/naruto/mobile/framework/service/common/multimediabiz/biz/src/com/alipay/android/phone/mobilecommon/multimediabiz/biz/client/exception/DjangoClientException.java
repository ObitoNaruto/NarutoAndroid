/*
 * Copyright 2014 Alibaba.com All right reserved.
 * This software is the confidential and proprietary information of Alibaba.com ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.exception;

public class DjangoClientException extends Exception {
    private static final long serialVersionUID = 3509658503585778347L;

    public DjangoClientException() {
    }

    public DjangoClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public DjangoClientException(String message) {
        super(message);
    }

    public DjangoClientException(Throwable cause) {
        super(cause);
    }
}
