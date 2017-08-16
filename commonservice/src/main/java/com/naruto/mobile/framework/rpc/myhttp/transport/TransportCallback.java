package com.naruto.mobile.framework.rpc.myhttp.transport;

/**
 * 回调
 * 
 */
public interface TransportCallback {
    /**
     * 取消
     * 
     * @param request 请求对象
     */
    public void onCancelled(Request request);
    /**
     * 预处理
     * 
     * @param request 请求对象
     */
    public void onPreExecute(Request request);
    /**
     * 处理完成
     * 
     * @param request 请求对象
     * @param response 响应对象
     */
    public void onPostExecute(Request request, Response response);
	/**
	 * 处理中的进度
	 * 
	 * @param request 请求对象
     * @param percent 进度
	 */
	public void onProgressUpdate(Request request, double percent);
	/**
	 * 处理失败
	 * 
     * @param request 请求对象
	 * @param code 错误码
	 * @param msg 错误消息
	 */
	public void onFailed(Request request, int code, String msg);
}
