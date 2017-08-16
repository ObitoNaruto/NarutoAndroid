package com.naruto.mobile.framework.rpc.myhttp.transport.http;

import com.naruto.mobile.framework.rpc.myhttp.transport.Response;

/**
 * HTTP响应 
 * 
 * @author sanping.li@alipay.com
 *
 */
public class HttpUrlResponse extends Response {
    /**
     * http响应码
     */
    private int mCode;
    /**
     * http响应消息
     */
    private String mMsg;
    /**
     * 数据请求回来的时间
     */
    private long mCreateTime;
    /**
     * 数据的有效周期
     */
    private long mPeriod;
    /**
     * 内容字符集
     */
    private String mCharset;
    
    private HttpUrlHeader mHeader;

    public HttpUrlResponse(HttpUrlHeader header,int code, String msg, byte[] resData) {
    	mHeader = header;
        mCode = code;
        mMsg = msg;
        mResData = resData;
    }

    /**
     * 获取响应码
     * 
     * @return 响应码
     */
    public int getCode() {
        return mCode;
    }

    /**
     * 获取响应消息
     * 
     * @return 响应消息
     */
    public String getMsg() {
        return mMsg;
    }

    /**
     * 获取响应数据的编码
     * 
     * @return 编码
     */
    public String getCharset() {
        return mCharset;
    }

    /**
     * 设置响应数据的编码
     * 
     * @param charset 编码
     */
    public void setCharset(String charset) {
        mCharset = charset;
    }

    /**
     * 获取数据请求回来的时间
     * 
     * @return 数据请求回来的时间
     */
    public long getCreateTime() {
        return mCreateTime;
    }

    /**
     * 设置数据请求回来的时间
     * 
     * @param createTime 数据请求回来的时间
     */
    public void setCreateTime(long createTime) {
        mCreateTime = createTime;
    }

    /**
     * 获取数据的有效周期
     * 
     * @return 数据的有效周期
     */
    public long getPeriod() {
        return mPeriod;
    }

    /**
     * 设置数据的有效周期
     * 
     * @param period 数据的有效周期
     */
    public void setPeriod(long period) {
        mPeriod = period;
    }

	public HttpUrlHeader getHeader() {
		return mHeader;
	}

	public void setHeader(HttpUrlHeader header) {
		this.mHeader = header;
	}

}
