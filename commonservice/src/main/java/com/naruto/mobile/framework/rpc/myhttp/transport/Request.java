package com.naruto.mobile.framework.rpc.myhttp.transport;

/**
 * 请求
 */
public abstract class Request {
	/**
	 * 请求取消标识
	 */
	private boolean cancel = false;

	/**
	 * 传输回调
	 */
	protected TransportCallback mCallback;

	/**
	 * 获取传输回调
	 * 
	 * @return 传输回调
	 */
	public TransportCallback getCallback() {
		return mCallback;
	}

	/**
	 * 设置回调
	 * 
	 * @param callback
	 */
	public void setTransportCallback(TransportCallback callback) {
		mCallback = callback;
	}

	/**
	 * 取消当前请求
	 */
	public void cancel() {
		cancel = true;
	}

	/**
	 * 请求是否已取消
	 * @return
	 */
	public boolean isCanceled() {
		return cancel;
	}

}
