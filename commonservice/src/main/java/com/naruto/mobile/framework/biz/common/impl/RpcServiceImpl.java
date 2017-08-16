package com.naruto.mobile.framework.biz.common.impl;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import java.lang.annotation.Annotation;
import java.util.concurrent.FutureTask;

import com.naruto.mobile.framework.rpc.myhttp.common.Config;
import com.naruto.mobile.framework.rpc.myhttp.common.RpcFactory;
import com.naruto.mobile.framework.rpc.myhttp.common.RpcInterceptor;
import com.naruto.mobile.framework.biz.common.RpcService;

/**
 * RpcServiceImpl
 *
 * @author sanping.li@alipay.com
 *
 */
public class RpcServiceImpl extends RpcService {
    /**
     * Rpc工厂
     */
    private RpcFactory mRpcFactory;
    
    private Handler mHandler = new Handler(Looper.getMainLooper());

    public RpcServiceImpl() {
        mRpcFactory = new RpcFactory(new DefaultConfig());
        //设置上下文对象,下一期改造成： LoggerFactory.getLogContext().getApplicationContext()
//        mRpcFactory.setContext(LauncherApplicationAgent.getInstance().getApplicationContext());
        //初始化HttpManager
//        CoreHttpManager.getInstance(LauncherApplicationAgent.getInstance().getApplicationContext());
    }

    /**
     * @param config
     */
    @Deprecated
    public RpcServiceImpl(Config config) {
        mRpcFactory = new RpcFactory(config);
        //设置上下文对象
//        mRpcFactory.setContext(LauncherApplicationAgent.getInstance().getApplicationContext());
//        CoreHttpManager.getInstance(LauncherApplicationAgent.getInstance().getApplicationContext());
    }

    /**
     * 推荐扩展时，继承默认实现类，避免每次加方法时，造成预发错误
     * @param config
     */
    public RpcServiceImpl(DefaultConfig config) {
        mRpcFactory = new RpcFactory(config);
        //设置上下文对象
//        mRpcFactory.setContext(LauncherApplicationAgent.getInstance().getApplicationContext());
//        CoreHttpManager.getInstance(LauncherApplicationAgent.getInstance().getApplicationContext());
    }

    /**
     * 推荐扩展时，继承默认实现类，避免每次加方法时，造成预发错误
     * @param config
     */
    public RpcServiceImpl(DefaultConfig config,Context context) {
        mRpcFactory = new RpcFactory(config);
        //设置上下文对象
//        mRpcFactory.setContext(context);
        //实例化
//        CoreHttpManager.getInstance(context);
    }


    @Override
    public <T> T getRpcProxy(Class<T> clazz) {
        return mRpcFactory.getRpcProxy(clazz);
    }

    @Override
    public <T> T getBgRpcProxy(Class<T> clazz) {
        return null;
//        return mRpcFactory.getBgRpcProxy(clazz);
    }

    @Override
    public <T> T getPBRpcProxy(Class<T> clazz) {
        return null;
//        return mRpcFactory.getPBRpcProxy(clazz);
    }

    @Override
    public void batchBegin() {
        mRpcFactory.batchBegin();
    }

    @Override
    public FutureTask<?> batchCommit() {
        return mRpcFactory.batchCommit();
    }

    /**
     * 添加协议参数
     * @param key
     * @param value
     */
    public void addProtocolArgs(String key,String value){
        mRpcFactory.addProtocolArgs(key, value);
    }
    
//    public void setScene(long time,String scene){
//    	mRpcFactory.setScene(scene);
//    	mHandler.postDelayed(new Runnable() {
//
//			@Override
//			public void run() {
//				mRpcFactory.setScene(null);
//			}
//		}, time);
//    }

//    public String getScene(){
//    	return mRpcFactory.getScene();
//    }

    @Override
    public void prepareResetCookie(Object object){
//    	mRpcFactory.prepareResetCookie(object);
    }
    
    @Override
    protected void onCreate(Bundle params) {

    }

    @Override
    protected void onDestroy(Bundle params) {

    }

    @Override
    public void addRpcInterceptor(Class<? extends Annotation> clazz,RpcInterceptor rpcInterceptor) {
        mRpcFactory.addRpcInterceptor(clazz, rpcInterceptor);
    }

//    @Override
//    public RpcInvokeContext getRpcInvokeContext(Object object) {
//        return mRpcFactory.getRpcInvokeContext(object);
//    }

//    @Override
//    public void addRpcHeaderListener(RpcHeaderListener rpcHeaderListener) {
//        mRpcFactory.addRpcHeaderListener(rpcHeaderListener);
//    }
//
//    public void setContext(Context context) {
//        mRpcFactory.setContext(context);
//    }

}
