package com.naruto.mobile.framework.rpc.myhttp.transport.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLPeerUnverifiedException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.content.Context;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

//import com.naruto.mobile.base.cache.disk.CacheException;
import com.naruto.mobile.base.log.logging.LogCatLog;
import com.naruto.mobile.framework.cache.disk.CacheException;
import com.naruto.mobile.framework.rpc.myhttp.transport.Response;
import com.naruto.mobile.framework.rpc.myhttp.transport.TransportCallback;
import com.naruto.mobile.framework.rpc.myhttp.utils.IOUtil;
import com.naruto.mobile.framework.rpc.myhttp.utils.NetworkUtils;


/**
 * HTTP请求处理,能处理200，304，302（5次跳转）
 * 注意：如果从本地缓存中取得etag值，并且通过If-None-Match发送到服务器端，如果服务器端的200响应里没有etag了
 * ，就会去清除本地缓存里的对应缓存值
 * 
 * @author sanping.li@alipay.com
 * 
 */
public class HttpWorker implements Callable<Response> {
	private final static String TAG = "HttpWorker";

	/**
	 * 钱包.net环境cookie domain
	 */
	private static final String COOKIE_DOMAIN_ALIPAY_NET = "alipay.net";

	/**
	 * 钱包.com环境cookie domain
	 */
	private static final String COOKIE_DOMAIN_ALIPAY_COM = "alipay.com";

	/**
	 * 默认etag缓存7天
	 */
	private final static long ETAG_CACHE_EXPIRES = 7 * 24 * 60 * 60 * 1000L;

	/**
	 * 重试Handler
	 */
	private final static HttpRequestRetryHandler sHttpRequestRetryHandler = new ZHttpRequestRetryHandler();

	private Context mContext;

	/**
	 * 网络请求管理器
	 */
	protected HttpManager mHttpManager;
	/**
	 * 请求对象
	 */
	private HttpUrlRequest mRequest;
	/**
	 * 地址
	 */
	String mUrl;
	/**
	 * 302跳转次数
	 */
	int mRedirects;

	private int mRetryTimes = 0;

	private HttpContextExtend mHttpContextExtend = HttpContextExtend
			.getInstance();

	private boolean hasIfNoneMatchInRequest = false;
	private boolean hasEtagInResponse = false;

	private String etagCacheKey = null;

	// private HttpRequestProcessor mHttpRequestProcessor = new
	// HttpRequestProcessorEtag(this);

	/**
	 * @param httpManager
	 *            网络请求管理器
	 * @param request
	 *            请求对象
	 */
	public HttpWorker(HttpManager httpManager, HttpUrlRequest request) {
		mHttpManager = httpManager;
		mContext = mHttpManager.mContext;
		mRequest = request;
	}

	protected URI getUri() throws URISyntaxException {
		String url = mRequest.getUrl();
		if (mUrl != null)// 覆盖地址
			url = mUrl;
		if (url == null)
			throw new RuntimeException("url should not be null");
		return new URI(url);
	}

	protected StringEntity getPostData() throws UnsupportedEncodingException {
		ArrayList<BasicNameValuePair> data = mRequest.getReqData();
		if (data != null && data.size() > 0) {
			return new UrlEncodedFormEntity(data, "utf-8");
		}
		return null;
	}

	protected ArrayList<Header> getHeaders() {
		return mRequest.getHeaders();
	}

	/**
	 * @throws HttpException
	 * @see Callable#call()
	 */
	@Override
	public Response call() throws HttpException {
//		PerformanceLog.getInstance().log(
//				"HttpWorker start request: " + mRequest.getUrl());

		TransportCallback callback = mRequest.getCallback();
		AndroidHttpClient httpClient = mHttpManager.getHttpClient();// haitong
																	// HttpClient
																	// ->AndroidHttpClient
		HttpUriRequest httpRequest = null;

		try {
//			if (!NetworkUtils.isNetworkAvailable(mContext)) {
//				throw new HttpException(HttpException.NETWORK_UNAVAILABLE,
//						"The network is not available");
//			}
			// 开始处理请求
			if (callback != null) {
				callback.onPreExecute(mRequest);
			}

			// configure the proxy.
			HttpParams httpParams = httpClient.getParams();
			HttpHost proxy = NetworkUtils.getProxy(mContext);
			httpParams.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);

			URL targetUrl = new URL(mRequest.getUrl());
			int targetPort = targetUrl.getPort() == -1 ? targetUrl
					.getDefaultPort() : targetUrl.getPort();
			HttpHost targetHttpHost = new HttpHost(targetUrl.getHost(),
					targetPort, targetUrl.getProtocol());

			// 构造请求
			StringEntity entity = getPostData();
			if (entity != null) {
				HttpPost httpPost = new HttpPost(getUri());
				httpPost.setEntity(entity);
				httpRequest = httpPost;
			} else {
				httpRequest = new HttpGet(getUri());
			}

			// 处理请求，主要是etag
			processRequest(httpRequest, mRequest);
			// 设置头
			ArrayList<Header> headers = getHeaders();
			if (headers != null) {
				for (Header header : headers) {
					httpRequest.addHeader(header);// 这里要塞入http头
				}
			}

			// 设置GZIP
			AndroidHttpClient.modifyRequestToAcceptGzipResponse(httpRequest);

			// keep-alive
			AndroidHttpClient.modifyRequestToKeepAlive(httpRequest);

			HttpContext localContext = new BasicHttpContext();
			CookieManager cookieManager = CookieManager.getInstance();
			String str = cookieManager
					.getCookie(httpRequest.getURI().getHost());
			httpRequest.addHeader("cookie", str);
			// httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY,
			// CookiePolicy.RFC_2109);
			CookieStore cookieStore = new BasicCookieStore();
			localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
//			PerformanceLog.getInstance().log(
//					"HttpWorker start execute request: " + mRequest.getUrl());

			// 设置重试Handler
			httpClient.setHttpRequestRetryHandler(sHttpRequestRetryHandler);

			long time = System.currentTimeMillis();

			// HttpResponse httpResponse = httpClient.execute(httpRequest,
			// localContext);
			HttpResponse httpResponse = httpClient.execute(targetHttpHost,
					httpRequest, localContext);

			long responseTime = System.currentTimeMillis();
			mHttpManager.addConnectTime(responseTime - time);

//			PerformanceLog.getInstance().log(
//					"HttpWorker finish execute request: " + mRequest.getUrl());
			List<Cookie> cookies = cookieStore.getCookies();
			if (!cookies.isEmpty()) {
				cookieManager = CookieManager.getInstance();
				// sync all the cookies in the httpclient with the webview by
				// generating cookie string
				for (Cookie cookie : cookies) {
					String domain = cookie.getDomain() == null ? COOKIE_DOMAIN_ALIPAY_COM
							: cookie.getDomain().toLowerCase();
					if (domain.indexOf(COOKIE_DOMAIN_ALIPAY_COM) > 0) {
						domain = COOKIE_DOMAIN_ALIPAY_COM;
					} else if (domain.indexOf(COOKIE_DOMAIN_ALIPAY_NET) > 0) {
						domain = COOKIE_DOMAIN_ALIPAY_NET;
					}
					String cookieString = cookie.getName() + "="
							+ cookie.getValue() + "; domain=" + domain;
					cookieManager.setCookie(httpRequest.getURI().getHost(),
							cookieString);
					CookieSyncManager.getInstance().sync();
				}
			}

			// 处理 response,
			return processResponse(httpResponse, mRequest, callback);

		} catch (HttpException e) {// 网络不可用
			logNet(e);
			if (httpRequest != null) {
				httpRequest.abort();
			}
			if (callback != null) {
				callback.onFailed(mRequest, e.getCode(), e.getMsg());
			}
//			LogCatLog.e(HttpManager.TAG, e + "");
			throw e;
		} catch (URISyntaxException e) {
			throw new RuntimeException("Url parser error!", e.getCause());
		} catch (SSLHandshakeException e) {// 证书不可用
			logNet(e);
			httpRequest.abort();
			if (callback != null) {
				callback.onFailed(mRequest,
						HttpException.NETWORK_SSL_EXCEPTION, e + "");
			}
//			LogCatLog.e(HttpManager.TAG, e + "");
			throw new HttpException(HttpException.NETWORK_SSL_EXCEPTION, e + "");
		} catch (SSLPeerUnverifiedException e) {// 证书不可用
			logNet(e);
			httpRequest.abort();
			if (callback != null) {
				callback.onFailed(mRequest,
						HttpException.NETWORK_SSL_EXCEPTION, e + "");
			}
//			LogCatLog.e(HttpManager.TAG, e + "");
			throw new HttpException(HttpException.NETWORK_SSL_EXCEPTION, e + "");
		} catch (SSLException e) {// SSL异常
			logNet(e);
			httpRequest.abort();
			if (callback != null) {
				callback.onFailed(mRequest, HttpException.NETWORK_IO_EXCEPTION,
						e + "");
			}
//			LogCatLog.e(HttpManager.TAG, e + "");
			throw new HttpException(HttpException.NETWORK_IO_EXCEPTION, e + "");
		} catch (ConnectionPoolTimeoutException e) {// 线程超时
			logNet(e);
			httpRequest.abort();
			if (callback != null) {
				callback.onFailed(mRequest,
						HttpException.NETWORK_CONNECTION_EXCEPTION, e + "");
			}
//			LogCatLog.e(HttpManager.TAG, e + "");
			throw new HttpException(HttpException.NETWORK_CONNECTION_EXCEPTION,
					e + "");
		} catch (ConnectTimeoutException e) {// 连接超时
			httpRequest.abort();
			if (callback != null) {
				callback.onFailed(mRequest,
						HttpException.NETWORK_CONNECTION_EXCEPTION, e + "");
			}
//			LogCatLog.e(HttpManager.TAG, e + "");
			throw new HttpException(HttpException.NETWORK_CONNECTION_EXCEPTION,
					e + "");
		} catch (SocketTimeoutException e) {// 网速过慢
			logNet(e);
			httpRequest.abort();
			if (callback != null) {
				callback.onFailed(mRequest,
						HttpException.NETWORK_SOCKET_EXCEPTION, e + "");
			}
//			LogCatLog.e(HttpManager.TAG, e + "");
			throw new HttpException(HttpException.NETWORK_SOCKET_EXCEPTION, e
					+ "");
		} catch (NoHttpResponseException e) {// 服务端没响应
			logNet(e);
			httpRequest.abort();
			if (callback != null) {
				callback.onFailed(mRequest,
						HttpException.NETWORK_SERVER_EXCEPTION, e + "");
			}
//			LogCatLog.e(HttpManager.TAG, e + "");
			throw new HttpException(HttpException.NETWORK_SERVER_EXCEPTION, e
					+ "");
		} catch (HttpHostConnectException e) {// 网络不可用
			logNet(e);
			httpRequest.abort();
			if (callback != null) {
				callback.onFailed(mRequest, HttpException.NETWORK_UNAVAILABLE,
						e + "");
			}
//			LogCatLog.e(HttpManager.TAG, e);// +""
			throw new HttpException(HttpException.NETWORK_UNAVAILABLE, e + "");
		} catch (UnknownHostException e) {// 网络不可用
			logNet(e);
			httpRequest.abort();
			if (callback != null) {
				callback.onFailed(mRequest, HttpException.NETWORK_UNAVAILABLE,
						e + "");
			}
//			LogCatLog.e(HttpManager.TAG, e + "");
			throw new HttpException(HttpException.NETWORK_UNAVAILABLE, e + "");
		} catch (IOException e) {// IO出错
			logNet(e);
			httpRequest.abort();
			if (callback != null) {
				callback.onFailed(mRequest, HttpException.NETWORK_IO_EXCEPTION,
						e + "");
			}
//			LogCatLog.e(HttpManager.TAG, e + "");
			throw new HttpException(HttpException.NETWORK_IO_EXCEPTION, e + "");

		} catch (NullPointerException e) {
			logNet(e);
			// Execute the request again.
			httpRequest.abort();
			if (mRetryTimes < 1) {
				mRetryTimes++;
				return call();
			} else {
//				LogCatLog.e(HttpManager.TAG, e + "");
				throw new HttpException(HttpException.NETWORK_UNKNOWN_ERROR, e
						+ "");
			}
		} catch (Exception e) {
			logNet(e);
			httpRequest.abort();
			if (callback != null) {
				callback.onFailed(mRequest,
						HttpException.NETWORK_UNKNOWN_ERROR, e + "");
			}
//			LogCatLog.e(HttpManager.TAG, e + "");
			throw new HttpException(HttpException.NETWORK_UNKNOWN_ERROR, e + "");
		} finally {
//			PerformanceLog.getInstance().log(
//					"HttpWorker finish request: " + mRequest.getUrl());
		}
	}

	private void logNet(Exception e) {
//		SystemExceptionHandler.getInstance().saveConnInfoToFile(e,
//				Constants.MONITORPOINT_CONNECTERR);
	}

	protected HashMap<String, String> getContentType(String string) {
		HashMap<String, String> result = new HashMap<String, String>();
		String[] params = string.split(";");
		String[] pairs = null;
		for (String param : params) {
			if (param.indexOf('=') == -1) {
				pairs = new String[2];
				pairs[0] = "Content-Type";
				pairs[1] = param;
			} else
				pairs = param.split("=");
			result.put(pairs[0], pairs[1]);
		}
		return result;
	}

	/**
	 * 处理响应
	 * 
	 * @param httpResponse
	 * @param resCode
	 * @param resMsg
	 * @param handler
	 * @param responseTime
	 * @return
	 * @throws IOException
	 */
	protected Response handleResponse(final HttpResponse httpResponse,
			final int resCode, final String resMsg,
			final TransportCallback callback) throws IOException {
		LogCatLog.d(TAG, "开始handle，handleResponse-1,"
				+ Thread.currentThread().getId());
		HttpEntity responseEntity = httpResponse.getEntity();

		HttpUrlResponse response = null;
		if (responseEntity != null
				&& httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			LogCatLog.d(TAG, "200，开始处理，handleResponse-2,threadid = "
					+ Thread.currentThread().getId());
			ByteArrayOutputStream outputStream = null;
			try {
				outputStream = new ByteArrayOutputStream();

				long time = System.currentTimeMillis();
				// 在304返回时，运行这里会出 IllegalStateException 异常
				writeData(responseEntity, 0, outputStream, callback);// 从0开始读
				byte[] data = outputStream.toByteArray();

				Header[] headers = httpResponse.getHeaders(ETAG);
				// 200响应，是否有etag，有的话需要缓存
				if (headers.length > 0) {
					String etagValue = headers[0].getValue();
					// LogCatLog.d(TAG,
					// "200，有etag,开始缓存,handleResponse－3， etagValue="+etagValue+","+Thread.currentThread().getId());
					// 生成缓存对象,并缓存
					CachedResponseWrapper crw = new CachedResponseWrapper();
					crw.setEtag(etagValue);
					crw.setValue(data);
					crw.setTypeHeader(httpResponse.getEntity().getContentType());// 在304时，Entity是null
					// mHttpContextExtend.getLruMemCache().put(null,
					// ETAG,this.etagCacheKey, crw);//memcache可能缓存失败
					mHttpContextExtend.getDiskCache().putSerializable(null,
							ETAG, this.etagCacheKey, crw,
							(new Date()).getTime(), ETAG_CACHE_EXPIRES,
							"Serializable");// 可能缓存失败
					this.hasEtagInResponse = true;
					// LogCatLog.d(TAG,
					// "200，有etag,缓存完成,handleResponse－4， etagValue="+etagValue+","+Thread.currentThread().getId());
				} else {
					// 不缓存 do nothing
					this.hasEtagInResponse = false;
					// LogCatLog.d(TAG,
					// "200，没有etag,不用缓存,handleResponse－3，"+Thread.currentThread().getId());
				}
				mHttpManager.addSocketTime(System.currentTimeMillis() - time);
				mHttpManager.addDataSize(data.length);
				// 处理head
				response = new HttpUrlResponse(
						handleResponseHeader(httpResponse), resCode, resMsg,
						data);
				fillResponse(response, httpResponse);
				// LogCatLog.d(TAG,
				// "200，获得数据结束，handleResponse-5"+Thread.currentThread().getId());
			} finally {
				// 清理没用的缓存
				removeEtagFromCache(this.etagCacheKey);
				// 关闭流
				if (outputStream != null) {
					try {
						outputStream.close();
					} catch (IOException e) {
						throw new RuntimeException(
								"ArrayOutputStream close error!", e.getCause());
					}
					outputStream = null;
				}
				LogCatLog.d(TAG, "finally,handleResponse");
			}
		} else if (responseEntity == null
				&& httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_MODIFIED) {
			// 如果entity为null 并且304，要从缓存里取数据
			if (resCode == HttpStatus.SC_NOT_MODIFIED) {// 这个判断
				// LogCatLog.d(TAG,
				// "304，从缓存取数据，handleResponse-6，"+Thread.currentThread().getId());
				// 1: 从缓存里取数据
				// CachedResponseWrapper crw = (CachedResponseWrapper)
				// mHttpContextExtend.getLruMemCache().get(null,
				// this.etagCacheKey);
				CachedResponseWrapper crw = (CachedResponseWrapper) this
						.getEtagCacheValue(this.etagCacheKey);

				if (crw != null) {
					response = new HttpUrlResponse(
							handleResponseHeader(httpResponse), resCode,
							resMsg, crw.getValue());
					// fillResponse
					long period = getPeriod(httpResponse);

					Header typeHeader = crw.getTypeHeader();
					String charset = null;
					String contentType = null;
					if (typeHeader != null) {
						HashMap<String, String> contentTypes = getContentType(typeHeader
								.getValue());
						charset = contentTypes.get("charset");
						contentType = contentTypes.get("Content-Type");
					}
					response.setContentType(contentType);
					response.setCharset(charset);
					response.setCreateTime(System.currentTimeMillis());
					response.setPeriod(period);
				}
				// 2: 缓存里没有数据，抛异常 ?
				// LogCatLog.d(TAG,
				// "304从缓存取数据完成，handleResponse-7，"+Thread.currentThread().getId());
			}
		} else {
			// LogCatLog.d(TAG,
			// "非200，304-handleResponse-8，"+Thread.currentThread().getId());
		}
		return response;
	}

	/**
	 * 处理响应头
	 * 
	 * @param httpResponse
	 * @return
	 */
	private HttpUrlHeader handleResponseHeader(final HttpResponse httpResponse) {
		// LogCatLog.d(TAG,
		// "处理header开始-handleResponseHeader-1，"+Thread.currentThread().getId());
		HttpUrlHeader header = new HttpUrlHeader();
		// StringBuffer sb = new StringBuffer();
		for (Header h : httpResponse.getAllHeaders()) {
			header.setHead(h.getName(), h.getValue());
			// sb.append(h.getName()).append("==").append(h.getValue()).append("&");
		}
		// LogCatLog.d(TAG,
		// "处理header完成-handleResponseHeader-2"+Thread.currentThread().getId());
		return header;
	}

	/**
	 * 补全HttpUrlResponse
	 * 
	 * @param response
	 * @param httpResponse
	 */
	protected void fillResponse(HttpUrlResponse response,
			HttpResponse httpResponse) {
		long period = getPeriod(httpResponse);

		Header typeHeader = httpResponse.getEntity().getContentType();
		String charset = null;
		String contentType = null;
		if (typeHeader != null) {
			HashMap<String, String> contentTypes = getContentType(typeHeader
					.getValue());
			charset = contentTypes.get("charset");
			contentType = contentTypes.get("Content-Type");
		}
		response.setContentType(contentType);
		response.setCharset(charset);
		response.setCreateTime(System.currentTimeMillis());
		response.setPeriod(period);
	}

	/**
	 * 如果有Cache-Control，则用Cache-Control，否则用Expires
	 * 
	 * @param httpResponse
	 * @return
	 */
	protected long getPeriod(HttpResponse httpResponse) {
		long expires = 0;
		Header cache = httpResponse.getFirstHeader("Cache-Control");
		if (cache != null) {
			String[] strs = cache.getValue().split("=");
			if (strs.length >= 2) {
				try {
					return parserMaxage(strs);
				} catch (NumberFormatException e) {
					LogCatLog.w(TAG, e);
				}
			}
		}

		Header expire = httpResponse.getFirstHeader("Expires");
		if (expire != null) {
			long expireTime = AndroidHttpClient.parseDate(expire.getValue());
			expires = expireTime - System.currentTimeMillis();
		}
		return expires;
	}

	/**
	 * Max-age
	 * 
	 * @param strs
	 * @return
	 */
	protected long parserMaxage(String[] strs) {
		for (int i = 0; i < strs.length; i++) {
			String str = strs[i];
			if ("max-age".equalsIgnoreCase(str) && strs[i + 1] != null) {
				return Long.parseLong(strs[i + 1]);
			}
		}
		return 0;
	}

	/**
	 * 是否处理该响应码
	 * 
	 * @param resCode
	 *            响应码
	 * @param resMsg
	 *            响应消息
	 * @return
	 */
	protected boolean willHandleOtherCode(int resCode, String resMsg) {
		//
		if (resCode == 304) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * 获取请求对象
	 * 
	 * @return 请求对象
	 */
	public HttpUrlRequest getRequest() {
		return mRequest;
	}

	/**
	 * 写数据
	 * 
	 * @param httpEntity
	 *            HTTPENTITY
	 * @param outstream
	 *            输出流
	 * @throws IOException
	 */
	protected void writeData(HttpEntity httpEntity, long hasReaded,
			final OutputStream outstream, TransportCallback callback)
			throws IOException {
		if (outstream == null) {
			httpEntity.consumeContent();
			throw new IllegalArgumentException("Output stream may not be null");
		}
		// GZIP压缩的数据
		InputStream instream = AndroidHttpClient
				.getUngzippedContent(httpEntity);
		final long length = httpEntity.getContentLength();
		try {
			long read = hasReaded;
			int l;
			byte[] tmp = new byte[2048];
			while ((l = instream.read(tmp)) != -1 && !mRequest.isCanceled()) {
				outstream.write(tmp, 0, l);
				read += l;
				if (callback != null && length > 0) {
					callback.onProgressUpdate(mRequest, (double) read / length);// 完成进度
				}
			}
			outstream.flush();
		} catch (Exception ex) {
			LogCatLog.w(TAG, ex.getCause());
			throw new IOException("HttpWorker Request Error!"
					+ ex.getLocalizedMessage());
		} finally {
			IOUtil.closeStream(instream);
		}
	}

	private static final String IF_NONE_MATCH = "If-None-Match";

	private static final String ETAG = "ETag";

	/**
	 * 
	 * 是否缓存有CachedResponseWrapper： 有，带上 If-None-Match 无 不带
	 */
	private void processRequest(HttpUriRequest httpUriRequest,
			HttpUrlRequest httpUrlRequest) {// HttpUrlRequest
		// 从request的url和参数给etagCacheKey设置值
		this.etagCacheKey = String.valueOf(httpUrlRequest.hashCode());
		// LogCatLog.d(TAG,
		// "开始处理processRequest，etagCacheKey="+etagCacheKey+","+Thread.currentThread().getId());
		// Object cachedValue = mHttpContextExtend.getLruMemCache().get(null,
		// etagCacheKey);
		Object cachedValue = getEtagCacheValue(etagCacheKey);
		// 如果不缓存被清空的话，etag数值和数据一同清除
		if (cachedValue != null && !(cachedValue instanceof CacheException)) {
			// LogCatLog.d(TAG,
			// "processRequest，获得etag缓存,"+Thread.currentThread().getId());
			CachedResponseWrapper crw = (CachedResponseWrapper) cachedValue;
			Header header = new BasicHeader(IF_NONE_MATCH, crw.getEtag());
			httpUriRequest.addHeader(header);
			this.hasIfNoneMatchInRequest = true;// 发的时候带If-None-Match
			// LogCatLog.d(TAG,
			// "processRequest，完成设置"+IF_NONE_MATCH+"="+crw.getEtag()+","+Thread.currentThread().getId());
		} else {
			this.hasIfNoneMatchInRequest = false;
		}
	}

	private Object getEtagCacheValue(String key) {
		Object obj = null;
//		try {
//			obj = mHttpContextExtend.getDiskCache().getSerializable(null,
//					etagCacheKey);
//		} catch (CacheException e) {
//			// can not do anything --haitong
//			e.printStackTrace();
//		}
		return obj;
	}

	/**
	 * 判断是否请求etag本地缓存：
	 * 如果从本地缓存中取得etag值，并且通过If-None-Match发送到服务器端（hasIfNoneMatchInRequest==true），
	 * 并且如果服务器端的200响应里没有etag了（hasEtagInResponse==false），就会去清除本地缓存里的对应缓存值
	 * 
	 * @param etagCacheKey
	 */
	private void removeEtagFromCache(String etagCacheKey) {
		if (this.hasIfNoneMatchInRequest == true
				&& this.hasEtagInResponse == false) {
			// this.mHttpContextExtend.getLruMemCache().remove(etagCacheKey);
			this.mHttpContextExtend.getDiskCache().remove(etagCacheKey);
			LogCatLog.d(TAG, "removeEtagFromCache，完成,"
					+ Thread.currentThread().getId());
		}

	}

	/**
	 * 处理响应
	 * 
	 * @param httpResponse
	 * @param httpUrlRequest
	 * @param callback
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 */
	public Response processResponse(final HttpResponse httpResponse,
			final HttpUrlRequest httpUrlRequest,
			final TransportCallback callback) throws HttpException, IOException {
		// LogCatLog.d(TAG,
		// "开始处理response，processResponse-1:"+Thread.currentThread().getId());
		int resCode = httpResponse.getStatusLine().getStatusCode();
		String resMsg = httpResponse.getStatusLine().getReasonPhrase();

		// LogCatLog.d(TAG,
		// "过了302处理，processResponse-2"+Thread.currentThread().getId());
		// 处理太暴力
		if (resCode != HttpStatus.SC_OK
				&& !willHandleOtherCode(resCode, resMsg)) {// 是否处理响应码为非200
			throw new HttpException(httpResponse.getStatusLine()
					.getStatusCode(), httpResponse.getStatusLine()
					.getReasonPhrase());
		}
		// LogCatLog.d(TAG,
		// "过了200，304外的拒绝处理，processResponse-3"+Thread.currentThread().getId());
		// 从response里处理etag数据
		// etag缓存key

		// LogCatLog.d(TAG,
		// "开始handler前，processResponse-4"+Thread.currentThread().getId());
		return handleResponse(httpResponse, resCode, resMsg, callback);

	}
}
