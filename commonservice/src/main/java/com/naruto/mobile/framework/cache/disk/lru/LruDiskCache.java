package com.naruto.mobile.framework.cache.disk.lru;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

import com.naruto.mobile.base.log.logging.PerformanceLog;
import com.naruto.mobile.framework.cache.disk.DiskCache;
import com.naruto.mobile.framework.cache.disk.Entity;

/**
 * LRU算法的磁盘缓存
 * 
 */
public abstract class LruDiskCache extends DiskCache {

    @Override
    protected void init() {
        //使用linkedhashmap来做lru缓存
        mEntities = new LinkedHashMap<String, Entity>(10, 0.75f, true) {
            private static final long serialVersionUID = -2365362316950114365L;

            @Override
            protected boolean removeEldestEntry(Entry<String, Entity> eldest) {
                if (mSize >= mMaxsize) {
                    PerformanceLog.getInstance().log("LruDiskCache evict:"+eldest.getValue());
                    mSize -= eldest.getValue().getSize();
                    evict(eldest.getKey());//整理缓存
                    return true;
                } else {
                    return false;
                }
            }
        };
        
        //使用额外的HashMap将entity对象分组
        mGroup = new HashMap<String, Set<Entity>>();
    }

    /**
     * 整理缓存
     */
    private void evict(String url) {
        removeCacheFile(url);
    }

}
