package com.naruto.mobile.framework.rpc.myhttp.transport;

/**
 * 响应
 */
public class Response {

    /**
     * 数据
     */
    protected byte[] mResData;

    /**
     * 数据类型，类似MIME type
     */
    protected String mContentType;

    /**
     * 获取返回数据
     *
     * @return 数据
     */
    public byte[] getResData() {
        return mResData;
    }

    /**
     * 设置返回数据
     */
    public void setResData(byte[] resData) {
        mResData = resData;
    }

    /**
     * 获取数据类型
     *
     * @return 数据类型
     */
    public String getContentType() {
        return mContentType;
    }

    /**
     * 设置数据类型
     */
    public void setContentType(String contentType) {
        mContentType = contentType;
    }


}
