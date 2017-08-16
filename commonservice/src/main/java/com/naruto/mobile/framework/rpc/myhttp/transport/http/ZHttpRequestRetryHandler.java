package com.naruto.mobile.framework.rpc.myhttp.transport.http;

import java.io.IOException;
import java.net.SocketException;

import javax.net.ssl.SSLException;

import com.naruto.mobile.base.log.logging.LogCatLog;

import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.protocol.HttpContext;


/**
 * 重试Handler
 * @author shiqun.shi@alipay.com
 *
 */
public class ZHttpRequestRetryHandler implements HttpRequestRetryHandler { 
	final static String TAG = ZHttpRequestRetryHandler.class.getSimpleName();

	@Override
	public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
		// retry a max of 3 times
		if (executionCount >= 3) {
			return false;
		}
        
		if (exception instanceof NoHttpResponseException) {
			LogCatLog.v(TAG, "exception instanceof NoHttpResponseException");
			return true;
		}
        if ((exception instanceof SocketException||exception instanceof SSLException)&&exception.getMessage()!=null&&exception.getMessage().contains("Broken pipe")) { 
            LogCatLog.v(TAG, "exception instanceof SocketException:Broken pipe"); 
            return true;  
        }
		return false;
	}
}
