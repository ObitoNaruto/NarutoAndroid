package com.naruto.mobile.rpc.myhttp.demo;

import com.naruto.mobile.framework.rpc.myhttp.beehive.rpc.RpcRunConfig;
import com.naruto.mobile.rpc.myhttp.model.BaseRpcModel;
import com.naruto.mobile.rpc.myhttp.request.MyRequest;
import com.naruto.mobile.rpc.myhttp.response.MyResponse;
import com.naruto.mobile.rpc.myhttp.service.CommentService;

public class MyModel extends BaseRpcModel<CommentService, MyResponse> {

    private MyRequest mRequest;

    public MyModel(MyRequest request) {
        super(CommentService.class);
        mRequest = request;
    }

    public void setRequestParameter(String commentId) {
        if (null == mRequest) {
            mRequest = new MyRequest();
        }
        mRequest.id = commentId;
    }

    @Override
    public RpcRunConfig getRpcRunConfig() {
        RpcRunConfig config = super.getRpcRunConfig();
        config.showNetError = true;
        return config;
    }

    @Override
    protected MyResponse requestData(CommentService commentService) {
        if (null != mRequest) {
            return commentService.getCommentById(mRequest);
        }
        return null;
    }
}
