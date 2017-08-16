package com.naruto.mobile.framework.rpc.myhttp.transport.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.TimeZone;

import com.naruto.mobile.framework.rpc.myhttp.transport.Response;
import com.naruto.mobile.framework.rpc.myhttp.transport.TransportCallback;
import com.naruto.mobile.framework.rpc.myhttp.transport.http.AndroidHttpClient;
import com.naruto.mobile.framework.rpc.myhttp.transport.http.HttpManager;
import com.naruto.mobile.framework.rpc.myhttp.transport.http.HttpUrlHeader;
import com.naruto.mobile.framework.rpc.myhttp.transport.http.HttpUrlRequest;
import com.naruto.mobile.framework.rpc.myhttp.transport.http.HttpUrlResponse;
import com.naruto.mobile.framework.rpc.myhttp.transport.http.HttpWorker;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.message.BasicHeader;

//import com.alipay.mobile.common.transport.Response;
//import com.alipay.mobile.common.transport.TransportCallback;
//import com.alipay.mobile.common.transport.http.AndroidHttpClient;
//import com.alipay.mobile.common.transport.http.HttpManager;
//import com.alipay.mobile.common.transport.http.HttpUrlHeader;
//import com.alipay.mobile.common.transport.http.HttpUrlRequest;
//import com.alipay.mobile.common.transport.http.HttpUrlResponse;
//import com.alipay.mobile.common.transport.http.HttpWorker;

/**
 * 下载处理
 * 
 * @hide
 * 
 * @author sanping.li@alipay.com
 * 
 */
public class DownloadWorker extends HttpWorker {
	private String mPath;

	public DownloadWorker(HttpManager httpManager, HttpUrlRequest request) {
		super(httpManager, request);
		DownloadRequest req = (DownloadRequest) request;
		mPath = req.getPath();
	}

	@Override
	protected ArrayList<Header> getHeaders() {
		ArrayList<Header> headers = super.getHeaders();
		File file = new File(mPath);
		long length = file.length();
		long lastModify = file.lastModified();
		if (length > 0 && lastModify > 0
				&& android.os.Build.VERSION.SDK_INT < 14) {
			headers.add(new BasicHeader("Range", "bytes=" + length + "-"));

			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
			dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
			String lastModified = dateFormat.format(lastModify);
			headers.add(new BasicHeader("If-Range", lastModified));
		}
		return headers;
	}

	@Override
	protected Response handleResponse(final HttpResponse httpResponse,
			final int resCode, final String resMsg,
			final TransportCallback callback) throws IOException {
		HttpEntity responseEntity = httpResponse.getEntity();

		if (responseEntity != null) {
			File file = new File(mPath);
			if (resCode == HttpStatus.SC_REQUESTED_RANGE_NOT_SATISFIABLE) {// 不一致，需要重新下载
				return checkFileIsDownloadSucess(httpResponse, resCode, resMsg,
						file);
			}
			if ((resCode == HttpStatus.SC_OK) && file.exists()
					&& !file.delete()) {// 不一致，需要重新下载
				throw new IOException("file delete fail or create fail");
			}
			if (resCode != HttpStatus.SC_OK
					&& resCode != HttpStatus.SC_PARTIAL_CONTENT) {
				file.deleteOnExit();
				throw new IOException("download failed! code=" + resCode);
			}
			final long hasReaded = file.length();
			FileOutputStream outputStream = null;

			try {
				outputStream = new FileOutputStream(file, true);
				long time = System.currentTimeMillis();
				writeData(responseEntity, hasReaded, outputStream, callback);
				mHttpManager.addSocketTime(System.currentTimeMillis() - time);
				mHttpManager.addDataSize(file.length());

				HttpUrlResponse response = new HttpUrlResponse(
						new HttpUrlHeader(), resCode, resMsg, null);
				fillResponse(response, httpResponse);
				return response;
			} catch (Exception ex) {
				throw new IOException("download failed!"
						+ ex.getLocalizedMessage());
			} finally {
				writeLastModifiedTime(httpResponse, file);

				if (outputStream != null) {
					try {
						outputStream.close();
					} catch (IOException e) {
						throw new RuntimeException(
								"ArrayOutputStream close error!", e.getCause());
					}
					outputStream = null;
				}
			}
		} else {
			return null;
		}
	}

	/**
	 * 检查文件是否下载完成，如果下载完成直接返回
	 * 
	 * @param httpResponse
	 * @param resCode
	 * @param resMsg
	 * @param file
	 * @return
	 * @throws IOException
	 */
	private Response checkFileIsDownloadSucess(final HttpResponse httpResponse,
			final int resCode, final String resMsg, File file)
			throws IOException {
		HttpURLConnection openConnection = null;
		try {
			URL url = getUri().toURL();
			openConnection = (HttpURLConnection) url.openConnection();
			int contentLength = openConnection.getContentLength();
			if (!(file.length() == contentLength && openConnection
					.getLastModified() == file.lastModified())) {
				throw new IOException("download failed! code=" + resCode);
			}
			HttpUrlResponse response = new HttpUrlResponse(null, resCode,
					resMsg, null);
			fillResponse(response, httpResponse);
			return response;
		} catch (Exception e) {
			if (file.exists()) {
				file.delete();
			}
			throw new IOException("download failed! code=" + resCode);
		} finally {
			if (openConnection != null) {
				openConnection.disconnect();
			}
		}
	}

	/**
	 * 设置文件最终修改时间
	 * 
	 * @param httpResponse
	 * @param file
	 */
	private void writeLastModifiedTime(final HttpResponse httpResponse,
			File file) {
		try {
			Header header = httpResponse.getFirstHeader("Last-Modified");
			if (header != null) {
				long lastModified = AndroidHttpClient.parseDate(header
						.getValue());
				file.setLastModified(lastModified);
			}
		} catch (Exception e1) {
		}
	}

	@Override
	protected boolean willHandleOtherCode(int resCode, String resMsg) {
		if (resCode == HttpStatus.SC_PARTIAL_CONTENT
				|| resCode == HttpStatus.SC_REQUESTED_RANGE_NOT_SATISFIABLE) {// 续传||长度不对
			return true;
		}
		return false;
	}

}
