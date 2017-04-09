package com.naruto.mobile.framework.service.common;

import com.naruto.mobile.base.serviceaop.service.CommonService;

/**
 * 磁盘缓存服务
 */
public abstract class DiskCacheService extends CommonService {

    /**
     * @return 存储目录
     */
    public abstract String getDirectory();

    /**
     * @return 大小
     */
    public abstract long getSize();

    /**
     * @return 最大大小
     */
    public abstract long getMaxsize();

    /**
     * 获取缓存
     *
     * @param owner 所有者
     * @param url 地址
     * @return 数据
     */
//    public abstract byte[] get(String owner, String url) throws CacheException;

    /**
     * 按组删除
     *
     * @param group 组
     */
    public abstract void removeByGroup(String group);

    /**
     * 删除
     *
     * @param url 地址
     */
    public abstract void remove(String url);

    /**
     * 存储缓存
     *
     * @param owner 所有者
     * @param group 组
     * @param url 地址
     * @param data 数据
     * @param createTime 创建时间，单位：毫秒
     * @param period 有效周期，单位：毫秒
     * @param contentType 内容类型
     */
    public abstract void put(String owner, String group, final String url, final byte[] data,
            long createTime, long period, String contentType);

    /**
     * 关闭
     */
    public abstract void close();

    /**
     * 打开
     */
    public abstract void open();

}
