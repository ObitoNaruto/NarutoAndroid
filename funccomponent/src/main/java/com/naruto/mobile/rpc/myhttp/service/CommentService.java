package com.naruto.mobile.rpc.myhttp.service;

import com.naruto.mobile.framework.rpc.myhttp.annotation.OperationType;
import com.naruto.mobile.framework.rpc.myhttp.annotation.ext.annotation.CheckLogin;
import com.naruto.mobile.rpc.myhttp.request.MyRequest;
import com.naruto.mobile.rpc.myhttp.response.MyResponse;

public interface CommentService {

    /**
     * @param request
     * @return
     */

    @OperationType("alipay.mobilecsa.getCommentById")
    @CheckLogin
//    @SignCheck
    MyResponse getCommentById(MyRequest request);

}
