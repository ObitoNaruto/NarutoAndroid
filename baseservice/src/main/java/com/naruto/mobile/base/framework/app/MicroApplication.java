package com.naruto.mobile.base.framework.app;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;

import com.naruto.mobile.base.serviceaop.MicroContent;
import com.naruto.mobile.base.serviceaop.NarutoApplicationContext;

/**
 * App接口
 * 
 * @author sanping.li@alipay.com
 *
 */
public abstract class MicroApplication implements MicroContent {

	/**
     * 上下文
     */
    private NarutoApplicationContext mContext;
    /**
     *  源ID
     */
    private String mSourceId;
    /**
     * 父App类名
     */
    private String mParentAppClassName;
    /**
     * 应用Id
     */
    private String mAppId;

    /**
     * 创建
     * 
     * @param params 参数
     * @throws AppLoadException
     */
    public abstract void create(Bundle params);

    /**
     * 启动
     */
    public abstract void start() throws AppLoadException;

    /**
     * 重新启动
     * 
     * @param params 参数
     */
    public abstract void restart(Bundle params);

    /**
     * 暂停
     */
    public abstract void stop();

    /**
     * 入口
     * 
     * @return 入口Activity或者Service
     */
    public abstract String getEntryClassName();
    

    /**
     * 阻止被回收，当前一activity被finish，并启动另一activity
     */
	public abstract void setIsPrevent(boolean isPrevent);
	
    /**
     * 创建回调
     * 
     * @param params
     */
    protected abstract void onCreate(Bundle params);
    
    /**
     * 启动回调
     */
    protected abstract void onStart();

    /**
     * 重新启动回调
     * 
     * @param params
     */
    protected abstract void onRestart(Bundle params);
    
    /**
     * 暂停回调
     */
    protected abstract void onStop();

    /**
     * 销毁回调
     * 
     * @param params
     */
    protected abstract void onDestroy(Bundle params);

    /**
     * 获取父App类名
     * 
     * @return 父App类名
     */
    public String getParentAppClassName() {
        return mParentAppClassName;
    }

    /**
     * 设置父App类名
     * 
     * @param parentAppClassName 父App类名
     */
    public void setParentAppClassName(String parentAppClassName) {
        mParentAppClassName = parentAppClassName;
    }

    public void setAppId(String appId) {
        this.mAppId = appId;
    }

    public String getAppId() {
        return mAppId;
    }

    public void destroy(Bundle params) {
        onDestroy(params);
    }

    /**
     * 依附MicroApplicationContext上下文
     * 
     * @param applicationContext MicroApplicationContext上下文
     */
    public void attachContext(NarutoApplicationContext applicationContext) {
        mContext = applicationContext;
    }

    /**
     * 获取MicroApplication上下文
     * 
     * @return MicroApplication上下文
     */
    public NarutoApplicationContext getMicroApplicationContext() {
        return mContext;
    }

    /**
     * 获取服务
     * 
     * @param className 服务接口类名
     * @return 服务
     */
    @SuppressWarnings("unchecked")
	public <T> T getServiceByInterface(String className) {
        return (T)mContext.findServiceByInterface(className);
    }

    /**
     * @return 获取源Id
     */
    public String getSourceId() {
        return mSourceId;
    }

    /**
     * 设置源Id
     * 
     * @param sourceId 源Id
     */
    public void setSourceId(String sourceId) {
        mSourceId = sourceId;
    }

    @Override
    public void saveState(Editor editor) {
    }

    @Override
    public void restoreState(SharedPreferences preferences) {
    }	
}
