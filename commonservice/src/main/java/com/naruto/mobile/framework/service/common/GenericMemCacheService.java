package com.naruto.mobile.framework.service.common;

import com.naruto.mobile.base.serviceaop.service.CommonService;

/**
 * 内存缓存服务
 */
public abstract class GenericMemCacheService extends CommonService {
    /**
     * @param group 删除组
     */
    public abstract void removeByGroup(String group);

    /**
     * 删除
     *
     * @param key 键
     * @return
     */
    public abstract Object remove(String key);

    /**
     * 获取
     *
     * @param owner 所有者
     * @param key 键
     * @return
     */
    public abstract Object get(String owner, String key);

    /**
     * 存储
     *
     * @param owner 所有者
     * @param group 组
     * @param key 键
     * @param value 值
     */
    public abstract void put(String owner, String group, String key, Object value);

}
