package com.naruto.mobile.rpc.myhttp.demo;


import android.content.DialogInterface;
import android.os.Bundle;

import com.naruto.mobile.base.framework.app.ui.BaseActivity;
import com.naruto.mobile.rpc.myhttp.model.RpcExecutor;
import com.naruto.mobile.rpc.myhttp.request.MyRequest;
import com.naruto.mobile.rpc.myhttp.response.MyResponse;

public class DemoTest extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyRequest myRequest = new MyRequest();
        final MyModel myModel = new MyModel(myRequest);
        RpcExecutor mRpcExecutor = new RpcExecutor(myModel, this);
        mRpcExecutor.setListener(new RpcExecutor.OnRpcRunnerListener() {
            @Override
            public void onSuccess(RpcExecutor requestType, Object result, boolean fromCache) {
                // TODO: 17-7-28
                MyResponse reponse = myModel.getResponse();
            }

            @Override
            public void onFailed(RpcExecutor requestType, int errorType, String exceptionMsg) {
                // TODO: 17-7-28
            }
        });
        myModel.setRequestParameter("123456");
        mRpcExecutor.run();
    }

    @Override
    public void alert(String title, String msg, String positive,
            DialogInterface.OnClickListener positiveListener, String negative,
            DialogInterface.OnClickListener negativeListener) {
    }

    @Override
    public void alert(String title, String msg, String positive,
            DialogInterface.OnClickListener positiveListener, String negative,
            DialogInterface.OnClickListener negativeListener, Boolean isCanceledOnTouchOutside) {
    }

    @Override
    public void toast(String msg, int period) {
    }

    @Override
    public void showProgressDialog(String msg) {
    }

    @Override
    public void showProgressDialog(String msg, boolean cancelable,
            DialogInterface.OnCancelListener cancelListener) {
    }

    @Override
    public void dismissProgressDialog() {
    }
}
