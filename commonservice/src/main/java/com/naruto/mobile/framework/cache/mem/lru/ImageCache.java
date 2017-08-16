package com.naruto.mobile.framework.cache.mem.lru;

import android.graphics.Bitmap;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

import com.naruto.mobile.base.log.logging.PerformanceLog;
import com.naruto.mobile.framework.cache.mem.Entity;
import com.naruto.mobile.framework.cache.mem.MemCache;

/**
 * 图片缓存
 */
public class ImageCache extends MemCache<Bitmap> {

    private static ImageCache INSTANCE;

    /**
     * 当前缓存占内存的大小
     */
    protected long mSize;

    public static synchronized ImageCache getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ImageCache();
        }
        return INSTANCE;
    }

    private ImageCache() {
        super();
        mSize = 0;
        mMap = new LinkedHashMap<String, Entity<Bitmap>>(10, 0.75f, true) {
            private static final long serialVersionUID = -4725089762367259243L;

            @Override
            protected boolean removeEldestEntry(Entry<String, Entity<Bitmap>> eldest) {
                if (mSize >= getSkiaSize()) {
                    PerformanceLog.getInstance().log("ImageCache evict: key=" + eldest.getKey());
                    eldest.getValue().getValue().recycle();//回收图片
                    ImageEntity entity = (ImageEntity) eldest.getValue();
                    mSize -= entity.getSize();
                    return true;
                } else {
                    return false;
                }
            }
        };
        mGroup = new HashMap<String, Set<Entity<Bitmap>>>();
    }

    private long getSkiaSize() {
        long maxMemory = Runtime.getRuntime().maxMemory();
        // Use 1/8th of the available memory for this memory cache.
        long cacheSize = maxMemory / 8;
        return cacheSize;
    }

    /**
     * 缓存图片
     *
     * @param owner 所用者：g+gid或者u+uid，比如:u10000023213。如果与所有者无关可以传null。
     * @param group 缓存对象所在组,null-只放到缓存中,not null将所有该group的该对象归组
     * @param key   键
     * @param value 图片
     */
    @Override
    public synchronized void put(String owner, String group, String key, Bitmap value) {
        super.put(owner, group, key, value);
    }

    /**
     * 获取缓存图片
     *
     * @param owner 所用者：g+gid|u+uid,或者其一，比如:u10000023213。如果与所有者无关可以传null。
     * @param key   键
     * @return 缓存图片
     */
    @Override
    public synchronized Bitmap get(String owner, String key) {
        return super.get(owner, key);
    }

    /**
     * 移除缓存图片
     *
     * @param key 键
     * @return 缓存图片
     */
    @Override
    public synchronized Bitmap remove(String key) {
        return super.remove(key);
    }

    /**
     * 获取缓存的内存大小限制
     *
     * @return 缓存的内存大小限制
     */
    public long getMaxsize() {
        return getSkiaSize();
    }

    /**
     * 获取当前缓存所占内存的大小
     *
     * @return 当前缓存所占内存的大小
     */
    public synchronized long getSize() {
        return mSize;
    }

    @Override
    protected Entity<Bitmap> makeEntity(String owner, String group, Bitmap value) {
        return new ImageEntity(owner, group, value);
    }

    /**
     * 记录删除
     *
     * @param entity 实体
     */
    @Override
    protected void recordRemove(Entity<Bitmap> entity) {
        ImageEntity imageEntity = (ImageEntity) entity;
        mSize -= imageEntity.getSize();
    }

    /**
     * 记录添加
     *
     * @param entity 实体
     */
    @Override
    protected void recordPut(Entity<Bitmap> entity) {
        ImageEntity imageEntity = (ImageEntity) entity;
        mSize += imageEntity.getSize();
    }
}
