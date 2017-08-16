package com.naruto.mobile.framework.rpc.myhttp.transport.http;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * HttpTransportSeviceImpl
 * 
 * @author haigang.gong@alipay.com
 *
 */
public class HttpUrlHeader implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6098125857367743614L;
	
	private Map<String,String> headers = new HashMap<String,String>();
	

	public Map<String,String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String,String> headers) {
		this.headers = headers;
	}
	
	public String getHead(String key) {
		return this.headers.get(key);
	}
	public void setHead(String key,String value) {
		this.headers.put(key, value);
	}
}
