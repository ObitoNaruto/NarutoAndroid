package com.naruto.mobile.base.rpc.volley;

public interface IRequestCallback<T> {
    /**
     * 成功时调用
     *
     * @param data 返回的数据
     */
    void onSuccess(T data);

    /**
     * 失败时调用
     *
     * @param errorCode 错误码
     * @param errorMsg    错误信息
     */
    void onFailure(int errorCode, String errorMsg);
}
