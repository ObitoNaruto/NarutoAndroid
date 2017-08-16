package com.naruto.mobile.framework.rpc.myhttp.transport.http;

import java.io.Serializable;

import org.apache.http.Header;
import org.apache.http.message.BufferedHeader;
import org.apache.http.util.CharArrayBuffer;

/**
 * 缓存response结果包装类
 * @author haigang.gong@alipay.com
 *
 */
public class CachedResponseWrapper implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1695597891335658069L;
	
	/*
	 * stag 数值
	 */
	private String etag;
	/*
	 * http body数据
	 */
	private byte[] data;
	
//	private Header typeHeader;
	private String typeHeaderName;
	private String typeHeaderValue;
//	private int length;
	
	public String getEtag() {
		return etag;
	}
	public void setEtag(String etag) {
		this.etag = etag;
	}
    public byte[] getValue() {
        return data;
    }
    public void setValue(byte[] value) {
        this.data = value;
    }
    /**
     * 重新组装Header（BufferedHeader）
     * @return
     */
    public Header getTypeHeader() {
        String temp = this.typeHeaderName+": "+this.typeHeaderValue;//冒号后面有空格
        CharArrayBuffer buffer = new CharArrayBuffer(temp.length());
        buffer.append(temp);
        Header typeHeader = new BufferedHeader(buffer);
        
        return typeHeader;
    }
    /**
     * Header的数据转成string，为了持久化disk（Header 没有实现Serializable 接口）
     * @param typeHeader
     */
    public void setTypeHeader(Header typeHeader) {
        if(typeHeader!=null) {
            this.typeHeaderName = typeHeader.getName();
            this.typeHeaderValue = typeHeader.getValue();
        }
//        this.typeHeader = typeHeader; 
    }
    
	
}
