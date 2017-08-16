package com.naruto.mobile.framework.cache.mem;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import com.naruto.mobile.base.log.logging.PerformanceLog;

/**
 * 内存缓存
 * 
 * @param <T> 类型
 *
 */
public abstract class MemCache<T> {
    /**
     * 缓存映射
     */
    protected HashMap<String, Entity<T>> mMap;
    /**
     * 一组对象的缓存映射
     */
    protected HashMap<String, Set<Entity<T>>> mGroup;

    /**
     * 缓存对象
     * 
     * @param owner 所用者：g+gid或者u+uid，比如:u10000023213。如果与所有者无关可以传null。
     * @param group 缓存对象所在组,null:只放到缓存中,not null:以group为key将该对象归组
     * @param key 键
     * @param value 值对象
     */
    public synchronized void put(String owner, String group, String key, T value) {
        PerformanceLog.getInstance().log("MemCache start put:" + key);
        Entity<T> entity = makeEntity(owner, group, value);

        addGroup(entity);
        mMap.put(key, entity);
        recordPut(entity);
        PerformanceLog.getInstance().log("MemCache finish put:" + key);
    }

    /**
     * 添加到组
     * 
     * @param entity 实体
     * @param group 组
     */
    private void addGroup(Entity<T> entity) {
        String group = entity.getGroup();
        if (group != null) {
            Set<Entity<T>> entitys = mGroup.get(group);
            if (entitys == null) {
                entitys = new HashSet<Entity<T>>();
                mGroup.put(group, entitys);
            }
            entitys.add(entity);
        }
    }

    /**
     * 获取缓存对象
     * 
     * @param owner 所用者：g+gid|u+uid,或者其一，比如:u10000023213。如果与所有者无关可以传null。
     * @param key 键
     * @return 缓存对象
     */
    public synchronized T get(String owner, String key) {
        PerformanceLog.getInstance().log("MemCache start get:" + key);
        if (!mMap.containsKey(key))
            return null;
        Entity<T> entity = mMap.get(key);
        //不属于该用户
        if (!entity.authenticate(owner)) {
            remove(key);
            return null;
        }
        PerformanceLog.getInstance().log("MemCache finish get:" + key);
        return entity.getValue();
    }

    /**
     * 移除缓存对象
     * 
     * @param key 键
     * @return 缓存对象
     */
    public synchronized T remove(String key) {
        PerformanceLog.getInstance().log("MemCache start remove:" + key);
        if (!mMap.containsKey(key))
            return null;
        Entity<T> entity = mMap.get(key);
        mMap.remove(key);
        removeGroup(entity);
        recordRemove(entity);
        PerformanceLog.getInstance().log("MemCache finish remove:" + key);
        return entity.getValue();
    }

    /**
     * 移除组中特定缓存对象
     * 
     * @param entity 实体
     * @param group 组
     */
    private void removeGroup(Entity<T> entity) {
        String group = entity.getGroup();
        if (group != null) {
            CopyOnWriteArraySet<Entity<T>> entitys = new CopyOnWriteArraySet<Entity<T>>(mGroup.get(group));
            if (entitys != null) {
                entitys.remove(entity);
            }
        }
    }

    /**
     * 移除指定组中的对象
     * 
     * @param group 对象所在组
     */
    public synchronized void removeByGroup(String group) {
        PerformanceLog.getInstance().log("MemCache start remove group:" + group);
        //删除指定组对象
        if (group != null) {
        	CopyOnWriteArraySet<Entity<T>> entitys = new CopyOnWriteArraySet<Entity<T>>(mGroup.get(group));
            if (entitys != null) {
                Collection<Entity<T>> collections;
                for (Entity<T> entity : entitys) {
                    collections = mMap.values();
                    //根据删除的对象更新mSize
                    if (collections.remove(entity)) {
                        recordRemove(entity);
                    }
                }
            }
        }
        PerformanceLog.getInstance().log("MemCache finish remove group:" + group);
    }

    /**
     * 构建实体
     * 
     * @param owner 所有者
     * @param group 组
     * @param value 值
     * @return
     */
    protected abstract Entity<T> makeEntity(String owner, String group, T value);

    /**
     * 记录删除
     * 
     * @param entity 实体
     */
    protected abstract void recordRemove(Entity<T> entity);

    /**
     * 记录添加
     * 
     * @param entity 实体
     */
    protected abstract void recordPut(Entity<T> entity);

    /**
     * 获取缓存对象个数
     * @hide
     * @return
     */
    public int getCacheCount() {
        return mMap.size();
    }

    @Override
    public String toString() {
        return mMap.toString();
    }

}
