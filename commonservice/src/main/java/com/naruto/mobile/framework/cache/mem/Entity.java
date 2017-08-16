package com.naruto.mobile.framework.cache.mem;

import com.naruto.mobile.base.log.logging.LogCatLog;

/**
 * @hide
 * 缓存实体
 * @param <T> 类型
 *
 */
public class Entity<T> {
    /**
     * 所用者："-"所有人所有;"g+gid"组所有，比如"g001";"u+uid"个人所有，比如"u001"
     */
    protected String mOwner;
    /**
     * 组
     */
    protected String mGroup;
    /**
     * 缓存的值对象
     */
    protected T mValue;

    public Entity(String owner, String group, T value) {
        if (owner == null) {
            mOwner = "-";
        } else {
            mOwner = owner;
        }
        mGroup = group;
        mValue = value;
    }

    /**
     * @return 所有者
     */
    public String getOwner() {
        return mOwner;
    }

    /**
     * @return 组
     */
    public String getGroup() {
        return mGroup;
    }

    /**
     * @return 缓存的值对象
     */
    public T getValue() {
        return mValue;
    }

    /**
     * 鉴权
     * 
     * @param owner 鉴权对象 ，组标识|用户标识：g001|u001，或者其一
     * @return 是否合法
     */
    public boolean authenticate(String owner) {
        if (mOwner.equalsIgnoreCase("-")) {
            return true;
        }

        if (owner == null) {
            LogCatLog.w("Entity", "authenticate: owner is null");
            return false;
        } else if (owner.contains(mOwner)) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("value: %s", mValue.toString());
    }
}
