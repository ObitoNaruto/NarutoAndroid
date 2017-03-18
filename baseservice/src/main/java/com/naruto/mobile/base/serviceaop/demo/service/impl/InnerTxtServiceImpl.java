package com.naruto.mobile.base.serviceaop.demo.service.impl;

import com.naruto.mobile.base.serviceaop.demo.service.InnerTxtService;

public class InnerTxtServiceImpl extends InnerTxtService{

    @Override
    public int subtract(int a, int b) {
        return a - b;
    }
}
