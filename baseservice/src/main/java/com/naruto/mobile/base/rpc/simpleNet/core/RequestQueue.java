package com.naruto.mobile.base.rpc.simpleNet.core;

import android.util.Log;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import com.naruto.mobile.base.rpc.simpleNet.base.Request;
import com.naruto.mobile.base.rpc.simpleNet.httpstacks.HttpStack;
import com.naruto.mobile.base.rpc.simpleNet.httpstacks.HttpStackFactory;

/**
 * 请求队列, 使用优先队列,使得请求可以按照优先级进行处理. [ Thread Safe ]
 * 
 * @author mrsimple
 */
public final class RequestQueue {
    /**
     * 请求队列 [ Thread-safe ]
     */
    private BlockingQueue<Request<?>> mRequestQueue = new PriorityBlockingQueue<Request<?>>();
    /**
     * 请求的序列化生成器
     */
    private AtomicInteger mSerialNumGenerator = new AtomicInteger(0);

    /**
     * 默认的核心数
     */
    public static int DEFAULT_CORE_NUMS = Runtime.getRuntime().availableProcessors() + 1;
    /**
     * CPU核心数 + 1个分发线程数
     */
    private int mDispatcherNums = DEFAULT_CORE_NUMS;
    /**
     * NetworkExecutor,执行网络请求的线程
     */
    private NetworkExecutor[] mDispatchers = null;
    /**
     * Http请求的真正执行者
     */
    private HttpStack mHttpStack;

    /**
     * @param coreNums 线程核心数
     * @param httpStack http执行器
     */
    protected RequestQueue(int coreNums, HttpStack httpStack) {
        mDispatcherNums = coreNums;
        mHttpStack = httpStack != null ? httpStack : HttpStackFactory.createHttpStack();
    }

    /**
     * 启动NetworkExecutor
     */
    private final void startNetworkExecutors() {
        mDispatchers = new NetworkExecutor[mDispatcherNums];
        for (int i = 0; i < mDispatcherNums; i++) {
            mDispatchers[i] = new NetworkExecutor(mRequestQueue, mHttpStack);
            mDispatchers[i].start();
        }
    }

    public void start() {
        stop();
        startNetworkExecutors();
    }

    /**
     * 停止NetworkExecutor
     */
    public void stop() {
        if (mDispatchers != null && mDispatchers.length > 0) {
            for (int i = 0; i < mDispatchers.length; i++) {
                mDispatchers[i].quit();
            }
        }
    }

    /**
     * 不能重复添加请求
     * 
     * @param request
     */
    public void addRequest(Request<?> request) {
        if (!mRequestQueue.contains(request)) {
            request.setSerialNumber(this.generateSerialNumber());
            mRequestQueue.add(request);
        } else {
            Log.d("", "### 请求队列中已经含有");
        }
    }

    public void clear() {
        mRequestQueue.clear();
    }

    public BlockingQueue<Request<?>> getAllRequests() {
        return mRequestQueue;
    }

    /**
     * 为每个请求生成一个系列号
     * 
     * @return 序列号
     */
    private int generateSerialNumber() {
        return mSerialNumGenerator.incrementAndGet();
    }
}
