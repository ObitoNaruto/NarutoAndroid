package com.naruto.mobile.framework.rpc.myhttp.transport.http;

import java.util.ArrayList;

import com.naruto.mobile.framework.rpc.myhttp.transport.Request;

import org.apache.http.Header;
import org.apache.http.message.BasicNameValuePair;

/**
 * HTTP请求
 * 
 */
public class HttpUrlRequest extends Request {
    /**
     * Url地址
     */
    private String mUrl;
    /**
     * 请求数据
     */
    private ArrayList<BasicNameValuePair> mReqData;
    /**
     * 请求HTTP头
     */
    private ArrayList<Header> mHeaders;

    /**
     * @param url 地址
     */
    public HttpUrlRequest(String url) {
        mUrl = url;
        mReqData = new ArrayList<BasicNameValuePair>();
        mHeaders = new ArrayList<Header>();
    }

    /**
     * @param url 地址
     * @param reqData 请求数据
     * @param headers 请求HTTP头
     */
    public HttpUrlRequest(String url, ArrayList<BasicNameValuePair> reqData,
                          ArrayList<Header> headers) {
        mUrl = url;
        mReqData = reqData;
        mHeaders = headers;
    }

    /**
     * 获取请求地址
     * 
     * @return 地址
     */
    public String getUrl() {
        return mUrl;
    }

    /**
     * 获取请求数据
     * 
     * @return 请求数据
     */
    public ArrayList<BasicNameValuePair> getReqData() {
        return mReqData;
    }

    /**
     * 设置请求数据
     * 
     * @param reqData 请求数据
     */
    public void setReqData(ArrayList<BasicNameValuePair> reqData) {
        mReqData = reqData;
    }

    /**
     * 设置请求HTTP头列表
     * 
     * @param headers 请求HTTP头列表
     */
    public void setHeaders(ArrayList<Header> headers) {
        mHeaders = headers;
    }

    /**
     * 添加请求一条HTTP头
     * 
     * @param header 请求HTTP头
     */
    public void addHeader(Header header) {
        mHeaders.add(header);
    }

    /**
     * 获取请求HTTP头列表
     * 
     * @return headers 请求HTTP头列表
     */
    public ArrayList<Header> getHeaders() {
        return mHeaders;
    }
    
    public String getKey(){
    	return getUrl() + getReqData();
    }
    
    @Override
    public String toString() {
        return String.format("Url : %s,ReqData: %s,HttpHeader: %s", getUrl(), getReqData(),
            getHeaders());
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		if(mReqData!=null) {
			for(BasicNameValuePair bnv:mReqData) {
				if( !"id".equals( bnv.getName() ) ) {
					result = prime*result + bnv.hashCode();
				}
			}
		}

		result = prime * result + ((mUrl == null) ? 0 : mUrl.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HttpUrlRequest other = (HttpUrlRequest) obj;
		if (mReqData == null) {
			if (other.mReqData != null)
				return false;
		} else if (!mReqData.equals(other.mReqData))
			return false;
		if (mUrl == null) {
			if (other.mUrl != null)
				return false;
		} else if (!mUrl.equals(other.mUrl))
			return false;
		return true;
	}
    
	
//	public static void main(String[] args) {
//		HttpUrlRequest hur1 = new HttpUrlRequest("http...");
//		HttpUrlRequest hur2 = new HttpUrlRequest("http...");
//		
//		System.out.println("hur1 = "+hur1.hashCode());
//		System.out.println("hur2 = "+hur2.hashCode());
//	}
    
}
