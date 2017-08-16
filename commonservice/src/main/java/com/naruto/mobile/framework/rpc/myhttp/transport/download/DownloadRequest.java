package com.naruto.mobile.framework.rpc.myhttp.transport.download;

import java.util.ArrayList;

import com.naruto.mobile.framework.rpc.myhttp.transport.http.HttpUrlRequest;

import org.apache.http.Header;
import org.apache.http.message.BasicNameValuePair;


/**
 * 下载请求对象
 * 
 * @hide
 * 
 * @author sanping.li@alipay.com
 *
 */
public class DownloadRequest extends HttpUrlRequest {
    /**
     *  下载数据的存储路径
     */
    private String mPath;

    public DownloadRequest(String url) {
        super(url);
    }

    public DownloadRequest(String url, ArrayList<BasicNameValuePair> reqData,
                           ArrayList<Header> headers) {
        super(url, reqData, headers);
    }

    /**
     * @param url 下载地址
     * @param path 存储路径
     * @param reqData 请求数据
     * @param headers 请求HTTP头
     */
    public DownloadRequest(String url, String path, ArrayList<BasicNameValuePair> reqData,
                           ArrayList<Header> headers) {
        super(url, reqData, headers);
        setPath(path);
    }

    /**
     * 获取下载数据的存储路径
     * 
     * @return 下载数据的存储路径
     */
    public String getPath() {
        return mPath;
    }

    /**
     *  设置下载数据的存储路径
     *  
     * @param path 下载数据的存储路径
     */
    public void setPath(String path) {
        if (path == null)
            throw new IllegalArgumentException("Not set valid path.");
        mPath = path;
    }

}
