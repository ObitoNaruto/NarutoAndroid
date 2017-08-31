package com.naruto.mobile.base.serviceaop;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.naruto.mobile.base.serviceaop.app.AppLoadException;
import com.naruto.mobile.base.serviceaop.app.ApplicationDescription;
import com.naruto.mobile.base.serviceaop.app.MicroApplication;
import com.naruto.mobile.base.serviceaop.service.ext.ExternalService;
import com.naruto.mobile.base.threadpool.PipeLine;

import java.lang.ref.WeakReference;

/**
 * Created by xinming.xxm on 2016/5/13.
 */
public interface NarutoApplicationContext {

    /**
     * 返回栈顶的Activity
     *
     * @return 栈顶Activity
     */
    WeakReference<Activity> getTopActivity();

    /**
     * 更新当前Activity
     * 备注：所有的activity继承了BaseActivity，其onResume中进行了调用
     * @param activity Activity
     */
    void updateActivity(Activity activity);

    /**
     * 依附android上下文
     *
     * @param application
     */
    void attachContext(NarutoApplication application);

    /**
     * 获取android上下文
     *
     * @return
     */
    NarutoApplication getApplicationContext();

    /**
     * 获得当前运行的顶部app
     *
     * @return
     */
    MicroApplication findTopRunningApp();

    /**
     * 注册服务
     *
     * @param className 服务接口类名
     * @param service   服务
     * @param <T>
     * @return
     */
    <T> boolean registerService(String className, T service);

    /**
     * 反注册服务
     * @param interfaceName 服务接口类名
     * @param <T>
     * @return
     */
    <T> T unregisterService(String interfaceName);

    /**
     * 查找服务
     *
     * @param className 服务接口类名
     * @param <T>
     * @return
     */
    <T> T findServiceByInterface(String className);

    /**
     * 通过服务接口获取外部服务
     *
     * @param className
     * @param <T>
     * @return
     */
    <T extends ExternalService> T getExtServiceByInterface(String className);

    /**
     * 根据管道名称获取管道名
     *
     * @param pipeLineName
     * @param pipeLineTimeout
     * @param <T>
     * @return
     */
    <T extends PipeLine> T getPipelineByName(String pipeLineName, long pipeLineTimeout);


    /**
     * 根据管道名称获取管道名
     *
     * @param pipeLineName
     * @param <T>
     * @return
     */
    <T extends PipeLine> T getPipelineByName(String pipeLineName);

    /**
     * 启动App
     *
     * @param sourceAppId 源App唯一Id
     * @param targetAppId 目标App唯一Id
     * @param params      参数
     * @throws AppLoadException
     */
    void startApp(String sourceAppId, String targetAppId, Bundle params)
            throws AppLoadException;

    /**
     * 关闭App
     *
     * @param sourceAppId 源App唯一Id
     * @param targetAppId 目标App唯一Id
     * @param params      参数
     */
    void finishApp(String sourceAppId, String targetAppId, Bundle params);


    /**
     * 查找App
     *
     * @param appId App唯一Id
     * @return MicroApplication
     */
    MicroApplication findAppById(String appId);

    /**
     * 根据app id获取ApplicationDescription
     * @param appId
     * @return
     */
    ApplicationDescription findDescriptionById(String appId);

    /**
     * 启动Activity
     *
     * @param microApplication
     * @param className        类名
     */
    void startActivity(MicroApplication microApplication, String className);

    /**
     * 启动Activity
     *
     * @param microApplication
     * @param intent           Intent
     */
    void startActivity(MicroApplication microApplication, Intent intent);

    /**
     * 启动Activity
     *
     * @param microApplication
     * @param className        类名
     * @param requestCode      请求码
     */
    void startActivityForResult(MicroApplication microApplication, String className,
                                int requestCode);

    /**
     * 启动Activity
     *
     * @param microApplication
     * @param intent           Intent
     * @param requestCode      请求码
     */
    void startActivityForResult(MicroApplication microApplication, Intent intent,
                                int requestCode);

    /**
     * 启动外部Activity
     *
     * @param microApplication
     * @param intent
     * @param requestCode
     */
    void startExtActivityForResult(MicroApplication microApplication, Intent intent,
                                   int requestCode);

    /**
     * 启动外部Activity
     *
     * @param microApplication
     * @param intent
     */
    void startExtActivity(MicroApplication microApplication, Intent intent);

    /**
     * microApplication获得窗口焦点
     *
     * @param application
     */
    void onWindowFocus(MicroApplication application);


    /**
     * 退出
     */
    void exit();

    /**
     * @return 网关地址
     */
    String getGwUrl();

    /**
     * 设置网关地址
     *
     * @param url 网关地址
     */
    void setGwUrl(String url);

    /**
     * TOAST
     *
     * @param msg    消息
     * @param period 时长
     */
    void Toast(String msg, int period);

    /**
     * 弹对话框
     *
     * @param title            标题
     * @param msg              消息
     * @param positive         确定
     * @param positiveListener 确定回调
     * @param negative         否定
     * @param negativeListener 否定回调
     */
    void Alert(String title, String msg, String positive,
               DialogInterface.OnClickListener positiveListener, String negative,
               DialogInterface.OnClickListener negativeListener);

    /**
     * 显示进度对话框
     *
     * @param msg 消息
     */
    void showProgressDialog(String msg);

    /**
     * 显示可取消的进度对话框
     *
     * @param msg 消息
     */
    void showProgressDialog(final String msg, final boolean cancelable,
                            final DialogInterface.OnCancelListener cancelListener);

    /**
     * 隐藏进度对话框
     */
    void dismissProgressDialog();


    /**
     * 销毁
     *
     * @param microContent
     */
    void onDestroyContent(MicroContent microContent);

    /**
     * 保存状态
     */
    void saveState();

    /**
     * 恢复状态
     */
    void restoreState();

    /**
     * 清楚状态
     */
    void clearState();

    /**
     * 是否已经初始化
     *
     * @return 是否已经初始化
     */
    boolean hasInited();
}
