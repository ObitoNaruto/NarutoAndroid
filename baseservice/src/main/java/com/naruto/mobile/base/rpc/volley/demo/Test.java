package com.naruto.mobile.base.rpc.volley.demo;

import java.util.concurrent.ExecutionException;

import com.naruto.mobile.base.rpc.volley.IRequestCallback;
import com.naruto.mobile.base.rpc.volley.demo.request.TestGsonRequest;
import com.naruto.mobile.base.rpc.volley.demo.request.TestSyncGsonRequest;
import com.naruto.mobile.base.rpc.volley.demo.response.TestGsonResponse;
import com.naruto.mobile.base.serviceaop.NarutoApplication;

public class Test {

    /**
     * 发起同步请求，场景是在一个工作线程中
     */
    public void excuteSyncRequest() {
        TestSyncGsonRequest testSyncGsonRequest = new TestSyncGsonRequest(
                NarutoApplication.getInstance().getApplicationContext());
        testSyncGsonRequest.setRequestParameter("parameter1", 0);
        testSyncGsonRequest.execute();
        try {
            TestGsonResponse response = testSyncGsonRequest.getResponse();
            // TODO: 17-7-19  下一步的业务逻辑
        } catch (InterruptedException | ExecutionException e) {
            // TODO: 17-7-19
        }


    }

    /**
     * 发起异步请求，保证耗时任务在工作线程，返回数据回调时回到主线程
     */
    public void excuteRequet() {
        TestGsonRequest testGsonRequest = new TestGsonRequest(NarutoApplication.getInstance().getApplicationContext(),
                new IRequestCallback<TestGsonResponse>() {
                    @Override
                    public void onSuccess(TestGsonResponse data) {
                        // TODO: 17-7-19 异步请求成功，下一步业务逻辑
                    }

                    @Override
                    public void onFailure(int errorCode, String errorMsg) {
                        // TODO: 17-7-19 异步请求失败，下一步业务逻辑
                    }
                });
        testGsonRequest.setRequestParameter("parameter1", 0);
        testGsonRequest.execute();
    }

}
