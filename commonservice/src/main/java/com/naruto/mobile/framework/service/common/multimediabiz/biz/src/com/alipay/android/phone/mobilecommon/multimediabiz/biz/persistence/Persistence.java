package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.persistence;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.persistence.condition.Condition;

/**
 * 持久化接口
 * Created by jinmin on 15/4/11.
 */
public interface Persistence<T> {
    /**
     * 保存一个对象，不存在则创建，存在则更新
     * @param obj
     * @return
     * @throws Exception
     */
    T save(T obj) throws Exception;

    /**
     * 批量保存对象
     * @param objects
     * @return
     * @throws Exception
     */
    List<T> save(List<T> objects) throws Exception;

    /**
     * 批量保存
     * @param objects
     * @return
     * @throws Exception
     */
    T[] save(T... objects) throws Exception;

    /**
     * 增加保存一个对象
     * @param obj
     * @return
     * @throws Exception
     */
    T add(T obj) throws Exception;

    /**
     * 批量增加保存对象
     * @param objects
     * @return
     * @throws Exception
     */
    List<T> add(List<T> objects) throws Exception;

    /**
     * 批量增加保存对象
     * @param objects
     * @return
     * @throws Exception
     */
    T[] add(T... objects) throws Exception;

    /**
     * 更新一个对象
     * @param obj
     * @param fields
     * @return
     * @throws Exception
     */
    T update(T obj, String... fields) throws Exception;

    /**
     * 批量更新
     * @param objects
     * @param fields
     * @return
     * @throws Exception
     */
    List<T> update(List<T> objects, String... fields) throws Exception;

    /**
     * 批量更新
     * @param objects
     * @param conditions
     * @param fields
     * @return
     * @throws Exception
     */
    List<T> update(List<T> objects, List<Condition> conditions, String... fields) throws Exception;

    /**
     * 根据ID删除一个对象
     * @param id
     * @return
     * @throws Exception
     */
    T delete(Class<T> clazz, String id) throws Exception;

    /**
     * 删除一个对象
     * @param obj
     * @return
     * @throws Exception
     */
    T delete(T obj) throws Exception;

    /**
     * 批量删除
     * @param objects
     * @return
     * @throws Exception
     */
    T[] delete(T... objects) throws Exception;

    /**
     * 批量删除
     * @param objects
     * @return
     * @throws Exception
     */
    List<T> delete(List<T> objects) throws Exception;

    /**
     * 根据ID查询
     * @param clazz
     * @param id
     * @return
     * @throws Exception
     */
    T query(Class<T> clazz, String id) throws Exception;

    /**
     * 根据字段查询
     * @param clazz
     * @param fieldName
     * @param value
     * @return
     * @throws Exception
     */
    List<T>  queryForEq(Class<T> clazz, String fieldName, String value) throws Exception;

    /**
     * 根据Condition查询
     * @param clazz
     * @param conditions
     * @return
     * @throws Exception
     */
    List<T>  query(Class<T> clazz, List<Condition> conditions) throws Exception;

    /**
     * 查询全部
     * @param clazz
     * @return
     * @throws Exception
     */
    List<T> queryAll(Class<T> clazz) throws Exception;


    QueryBuilder<T, String> queryBuilder();

    UpdateBuilder<T, String> updateBuilder();

    DeleteBuilder<T, String> deleteBuilder();

    long countOf() throws SQLException;
}
