package com.naruto.mobile.framework.biz.ext.schemeUrl;

import android.net.Uri;

import com.naruto.mobile.base.serviceaop.service.CommonService;

public abstract class SchemeService extends CommonService{
    /**
     * @param uri 自定义scheme的uri
     * @return
     * 0. 处理成功
     * 1. 协议错误
     * 2. id为null
     * 3. 版本不匹配
     * 4. 启动app出错
     * 5. 其它错误
     */
    public abstract int process(Uri uri);
}
