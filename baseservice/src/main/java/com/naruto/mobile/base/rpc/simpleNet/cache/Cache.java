package com.naruto.mobile.base.rpc.simpleNet.cache;

/**
 * 请求缓存接口
 * 
 * @author mrsimple
 * @param <K> key的类型
 * @param <V> value类型
 */
public interface Cache<K, V> {

    public V get(K key);

    public void put(K key, V value);

    public void remove(K key);

}
