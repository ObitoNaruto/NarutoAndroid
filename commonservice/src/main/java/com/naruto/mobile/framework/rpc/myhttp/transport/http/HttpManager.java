package com.naruto.mobile.framework.rpc.myhttp.transport.http;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import android.content.Context;

import com.naruto.mobile.framework.rpc.myhttp.common.info.AppInfo;
import com.naruto.mobile.framework.rpc.myhttp.transport.Request;
import com.naruto.mobile.framework.rpc.myhttp.transport.Response;
import com.naruto.mobile.framework.rpc.myhttp.transport.TransportCallback;
import com.naruto.mobile.framework.rpc.myhttp.utils.SerialExecutor;

/**
 * HTTP网络请求管理器
 */
public class HttpManager {
	/**
	 * @hide
	 */
	public static final String TAG = "HttpManager";
	// -------------------线程池设置-----------------
	private static final int CORE_SIZE = 5;
	private static final int POOL_SIZE = 10;
	private static final int KEEP_ALIVE_TIME = 10;
	private static final int QUEUE_SIZE = 128;

	Context mContext;
	private ThreadPoolExecutor mParallelExecutor;
	private SerialExecutor mSerialExecutor;
	private  AndroidHttpClient mHttpClient;

	// ------------------网络性能-------------------
	/**
	 * 请求的总数据量大小
	 */
	private long mAllDataSize;
	/**
	 * 请求的总连接时间
	 */
	private long mAllConnectTimes;
	/**
	 * 请求的总数据传输时间
	 */
	private long mAllSocketTimes;
	/**
	 * 请求的次数
	 */
	private int mRequestTimes;

	public HttpManager(Context context) {
		mContext = context;
		init();
	}

	/**
	 * 初始化
	 */
	private void init() {
//		if (mHttpClient == null)
			mHttpClient = AndroidHttpClient.newInstance("android");
		setDefaultHostnameVerifier();
		mSerialExecutor = new SerialExecutor("Http");
		mParallelExecutor = new ThreadPoolExecutor(CORE_SIZE, POOL_SIZE,
				KEEP_ALIVE_TIME, TimeUnit.SECONDS,
				new ArrayBlockingQueue<Runnable>(QUEUE_SIZE), THREADFACTORY,
				new ThreadPoolExecutor.CallerRunsPolicy());
	}

	public Future<Response> execute(Request request) {
		if (!(request instanceof HttpUrlRequest)) {
			throw new RuntimeException("request send error.");
		}

		if (AppInfo.getInstance().isDebuggable()) {
//			LogCatLog.i(TAG, dumpPerf());
		}

		final HttpUrlRequest urlHttpRequest = (HttpUrlRequest) request;
//		PerformanceLog.getInstance().log(
//				TAG + " schedule request: " + urlHttpRequest.getUrl());

		HttpWorker httpWorker = generateWorker(urlHttpRequest);
		FutureTask<Response> task = makeTask(httpWorker);
		
		mParallelExecutor.execute(task);
		
//		// TODO 根据网速动态设置超时时间和线程数
//		if (NetworkUtils.getNetType(mContext) == ConnectivityManager.TYPE_WIFI) {
//			mParallelExecutor.execute(task);
//		} else {
//			mSerialExecutor.execute(task);
//		}
		return task;
	}

	private FutureTask<Response> makeTask(final HttpWorker httpWorker) {
		FutureTask<Response> task = new FutureTask<Response>(httpWorker) {
			@Override
			protected void done() {
				Request request = httpWorker.getRequest();
				TransportCallback callback = request.getCallback();
				if (callback == null) {
					super.done();
					return;
				}
				Response response = null;
				try {
					response = get();
					if (isCancelled() || request.isCanceled()) {
						request.cancel();
						if (!this.isCancelled() || !this.isDone()) {
							this.cancel(false);
						}
						callback.onCancelled(request);
					} else if (response != null) {
						callback.onPostExecute(request, response);
					}
				} catch (InterruptedException e) {
					callback.onFailed(request,
							HttpException.NETWORK_SCHEDULE_ERROR, e + "");
				} catch (ExecutionException e) {
					if (null != e && null != e.getCause()
							&& e.getCause() instanceof HttpException) {
						HttpException httpException = (HttpException) e
								.getCause();
						callback.onFailed(request, httpException.getCode(),
								httpException.getMsg());
						return;
					}else
					{
						callback.onFailed(request,
								HttpException.NETWORK_IO_EXCEPTION, e + "");
						return;
					}
//					throw new RuntimeException(
//							"An error occured while executing http request",
//							e.getCause());
				} catch (CancellationException e) {
					request.cancel();
					callback.onCancelled(request);
				} catch (Throwable t) {
					throw new RuntimeException(
							"An error occured while executing http request", t);
				}
			}
		};
		return task;
	}

	/**
	 * 产生HttpWorker
	 * 
	 * @param rpcHttpRequest
	 *            HttpUrlRequest
	 * @return HttpWorker
	 */
	protected HttpWorker generateWorker(HttpUrlRequest rpcHttpRequest) {
		return new HttpWorker(this, rpcHttpRequest);
	}

	private void setDefaultHostnameVerifier() {
		HostnameVerifier hv = new HostnameVerifier() {
			@Override
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		};

		HttpsURLConnection.setDefaultHostnameVerifier(hv);
	}

	/**
	 * 获取AndroidHttpClient
	 * 
	 * @hide
	 * @return 请求AndroidHttpClient
	 */
	public AndroidHttpClient getHttpClient() {
		return mHttpClient;
	}

	private static final ThreadFactory THREADFACTORY = new ThreadFactory() {
		private final AtomicInteger mCount = new AtomicInteger(1);

		public Thread newThread(Runnable r) {
			Thread thread = new Thread(r, "HttpWorker #" + mCount.getAndIncrement());
			thread.setPriority(Thread.NORM_PRIORITY - 1);
			return thread;
		}
	};

	/**
	 * 添加总共数据大小
	 * 
	 * @param size
	 *            数据大小
	 */
	public void addDataSize(long size) {
		mAllDataSize += size;
	}

	/**
	 * 添加连接时间
	 * 
	 * @param time
	 *            时间
	 */
	public void addConnectTime(long time) {
		mAllConnectTimes += time;
		mRequestTimes++;
	}

	/**
	 * 添加数据传输时间
	 * 
	 * @param time
	 *            时间
	 */
	public void addSocketTime(long time) {
		mAllSocketTimes += time;
	}

	/**
	 * 获取当前运行周期的平均网络传输速度:KB/S
	 * 
	 * @return 网络传输速度
	 */
	public long getAverageSpeed() {
		if (mAllSocketTimes == 0)
			return 0;
		long speed = (mAllDataSize * 1000) / mAllSocketTimes;
		return (speed >> 10);
	}

	/**
	 * 获取当前运行周期的平均网络连接时间：毫秒
	 * 
	 * @return 网络连接时间
	 */
	public long getAverageConnectTime() {
		if (mRequestTimes == 0)
			return 0;
		long time = mAllConnectTimes / mRequestTimes;
		return time;
	}

	/**
	 * 输出性能格式化串
	 */
	public String dumpPerf() {
		return String
				.format("HttpManager"+hashCode()+": Active Task = %d, Completed Task = %d, All Task = %d,Avarage Speed = %d KB/S, Connetct Time = %d ms, All data size = %d bytes, All connect time = %d ms, All socket time = %d ms, All request times = %d times",
						mParallelExecutor.getActiveCount(),mParallelExecutor.getCompletedTaskCount(),mParallelExecutor.getTaskCount(),getAverageSpeed(), getAverageConnectTime(),
						mAllDataSize, mAllConnectTimes, mAllSocketTimes,
						mRequestTimes);

	}

	/**
	 * 关闭,将不在接受HTTP请求
	 */
	public void close() {
		if (mSerialExecutor != null) {
			mSerialExecutor.stop();
			mSerialExecutor = null;
		}
		if (mParallelExecutor != null) {
			mParallelExecutor.shutdown();
			mParallelExecutor = null;
		}

		if (mHttpClient != null)
			mHttpClient.close();
		mHttpClient = null;
	}
}
