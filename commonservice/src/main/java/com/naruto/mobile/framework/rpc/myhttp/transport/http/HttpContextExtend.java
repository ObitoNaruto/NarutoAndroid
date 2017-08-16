package com.naruto.mobile.framework.rpc.myhttp.transport.http;


//import com.naruto.mobile.base.cache.disk.lru.DefaultLruDiskCache;
//import com.naruto.mobile.base.cache.disk.lru.LruDiskCache;

import com.naruto.mobile.framework.cache.disk.lru.DefaultLruDiskCache;
import com.naruto.mobile.framework.cache.disk.lru.LruDiskCache;

/**
 * TODO 要变成appapi的基础上下文
 * 
 */
public class HttpContextExtend {
    /**
     * 最大批量单次请求合并数，为了防止出现的一次请求里发太多数据。
     */
    public static int MAX_HTTP_REQUEST_COUNT_PER_BATCH = 4;
    
    private static HttpContextExtend httpContext = null;
    
//    private MemCache<Object> mLruMemCache = new LruMemCache();
    /**
     * 用来保存etag数据，即：
     * 缓存 etag 和 body数据
     * 请求前从其中取“If-None-Match”,加到header里；
     * 返回304时，从其中取body数据
     * 
     * 注意：不保证，缓存里一定有数据
     */
    private LruDiskCache diskCache;
    
    private HttpContextExtend() {    
        init();
    }
    
    private void init() {
        //初始化
        diskCache = DefaultLruDiskCache.getInstance();//new DefaultLruDiskCache();
        diskCache.open();
    }
    public synchronized static HttpContextExtend getInstance() {
        if(httpContext==null)  {
            httpContext = new HttpContextExtend();
        }
         return httpContext;
    }

//    public MemCache<Object> getLruMemCache() {
//        return mLruMemCache;
//    }
    
    public LruDiskCache getDiskCache() {
        return diskCache;
    }
    
}
