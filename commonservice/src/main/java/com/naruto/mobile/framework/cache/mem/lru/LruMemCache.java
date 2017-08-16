package com.naruto.mobile.framework.cache.mem.lru;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

import com.naruto.mobile.base.log.logging.PerformanceLog;
import com.naruto.mobile.framework.cache.mem.Entity;
import com.naruto.mobile.framework.cache.mem.MemCache;
//import com.alipay.mobile.common.cache.mem.Entity;
//import com.alipay.mobile.common.cache.mem.MemCache;
//import com.alipay.mobile.common.logging.PerformanceLog;

/**
 * LRU算法的内存缓存
 * 
 * @author sanping.li@alipay.com
 *
 */
public class LruMemCache extends MemCache<Object> {
    private static LruMemCache INSTANCE;

    public static synchronized LruMemCache getInstance(){
        if(INSTANCE==null){
            INSTANCE = new LruMemCache();
        }
        return INSTANCE;
    }
    /**
     * @param maxSize 缓存占内存的大小限制
     */
    private LruMemCache() {
        super();
        mMap = new LinkedHashMap<String, Entity<Object>>(10, 0.75f, true) {
            private static final long serialVersionUID = -3776592521668005864L;

            @Override
            protected boolean removeEldestEntry(Entry<String, Entity<Object>> eldest) {
                long freeMemory = Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory();
                
                if (freeMemory < 1024*512) {//如果可用内存小于512K
                    PerformanceLog.getInstance().log(
                        "LruMemCache evict: key=" + eldest.getKey() + "     value="
                                + eldest.getValue());
                    return true;
                } else {
                    return false;
                }
            }
        };

        mGroup = new HashMap<String, Set<Entity<Object>>>();
    }

    /**
     * 缓存对象
     * 
     * @param owner 所用者：g+gid或者u+uid，比如:u10000023213。如果与所有者无关可以传null。
     * @param group 缓存对象所在组,null:只放到缓存中,not null:以group为key将该对象归组
     * @param key 键
     * @param value 值对象
     */
    public synchronized void put(String owner, String group, String key, Object value) {
        super.put(owner, group, key, value);
    }

    /**
     * 获取缓存对象
     * 
     * @param owner 所用者：g+gid|u+uid,或者其一，比如:u10000023213。如果与所有者无关可以传null。
     * @param key 键
     * @return 缓存对象
     */
    public synchronized Object get(String owner, String key) {
        return super.get(owner, key);
    }

    /**移除缓存对象
     * 
     * @param key 键
     * @return 缓存对象
     */
    public synchronized Object remove(String key) {
        return super.remove(key);
    }

    @Override
    protected Entity<Object> makeEntity(String owner, String group, Object value) {
        return new Entity<Object>(owner, group, value);
    }

    @Override
    protected void recordRemove(Entity<Object> entity) {

    }

    @Override
    protected void recordPut(Entity<Object> entity) {

    }

}
