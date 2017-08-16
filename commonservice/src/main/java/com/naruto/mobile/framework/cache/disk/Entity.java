package com.naruto.mobile.framework.cache.disk;

import java.text.SimpleDateFormat;

import com.naruto.mobile.base.log.logging.LogCatLog;

/**
 * 缓存实体
 * 
 * @hide
 */
public class Entity {
    /**
     * 所用者："-"所有人所有;"g+gid"组所有，比如"g001";"u+uid"个人所有，比如"u001"
     */
    private String mOwner;
    /**
     * 组
     */
    protected String mGroup;
    /**
     * Url
     */
    private String mUrl;
    /**
     * 使用次数
     */
    private int mUsedTime;
    /**
     * 大小
     */
    private long mSize;
    /**
     * 存储的相对路径
     */
    private String mPath;
    /**
     * 更新时间
     */
    private long mCreateTime;
    /**
     * 有效周期
     */
    private long mPeriod;
    /**
     * 内容类型
     */
    private String mContentType;

    public Entity(String owner, String group, String url, int usedTime, long size, String path,
                  long createTime, long period, String contentType) {
        if (owner == null) {
            mOwner = "-";
        } else {
            mOwner = owner;
        }
        if (group == null) {
            mGroup = "-";
        } else {
            mGroup = group;
        }
        if (url == null){
        	mUrl = ""; 
        }else{
        	mUrl = url;        	
        }
        mUsedTime = usedTime;
        mSize = size;
        
        if (path == null){
        	mPath = "";
        }else{
        	mPath = path;
        }
        
        mCreateTime = createTime;
        mPeriod = period;
        
        if ( contentType == null){
        	mContentType = "";
        }else{
        	mContentType = contentType;
        }
    }

    /**
     * 所用者："-"所有人所有;"g+gid"组所有，比如"g001";"u+uid"个人所有，比如"u001"
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
     * @return Url
     */
    public String getUrl() {
        return mUrl;
    }

    /**
     * @return 使用次数
     */
    public int getUsedTime() {
        return mUsedTime;
    }

    /**
     * 使用
     */
    public void use() {
        mUsedTime++;
    }

    /**
     * @return 大小
     */
    public long getSize() {
        return mSize;
    }

    /**
     * @return 存储的相对路径
     */
    public String getPath() {
        return mPath;
    }

    /**
     * @return 更新时间
     */
    public long getCreateTime() {
        return mCreateTime;
    }

    /**
     * @return 有效周期
     */
    public long getPeriod() {
        return mPeriod;
    }

    /**
     * @return 内容类型
     */
    public String getContentType() {
        return mContentType;
    }

    /**
     * @return 数据是否过期
     */
    public boolean expire() {
        return mCreateTime + mPeriod *1000< System.currentTimeMillis();
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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return String
            .format(
                "url: %s usedTime: %d size: %d path: %s createTime:%s period: %d content-type: %s owner: %s",
                mUrl, mUsedTime, mSize, mPath, sdf.format(mCreateTime), mPeriod, mContentType,
                mOwner);
    }

}
