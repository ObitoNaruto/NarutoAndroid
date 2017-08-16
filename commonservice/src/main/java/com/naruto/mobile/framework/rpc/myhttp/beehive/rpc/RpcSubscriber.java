package com.naruto.mobile.framework.rpc.myhttp.beehive.rpc;


import android.app.Activity;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import java.lang.reflect.Field;

import com.naruto.mobile.base.framework.app.ui.ActivityResponsable;
import com.naruto.mobile.framework.rpc.myhttp.beehive.eventbus.EventBusManager;
import com.naruto.mobile.framework.rpc.myhttp.beehive.eventbus.Subscribe;
import com.naruto.mobile.framework.rpc.myhttp.common.RpcException;

/**
 * 默认的rpc event处理, 这里必须使用event queue来处理cancel事件!
 */
public abstract class RpcSubscriber<ResultType> {

    private volatile boolean isCancelPending;

    private RpcUiProcessor rpcUiProcessor;

    private RpcEvent<ResultType> event;

    /**
     * rpc缓存是否加载成功
     */
    private boolean isGetCacheSuccess;

    /**
     * 任务执行状态标记
     * 表示之前执行过程中是否成功过
     * 用来标记界面内容已成功渲染!
     */
    private boolean isEverSuccess;

    public RpcSubscriber() {
        // 非ui的回调
        init(null, null);
    }

    public RpcSubscriber(ActivityResponsable ar) {
        init(ar, null);
    }

    /**
     * 增加fragment调用接口
     */
    public RpcSubscriber(Fragment f) {
        Activity a = f.getActivity();
        if (a != null && a instanceof ActivityResponsable) {
            init((ActivityResponsable)a, f);
        } else {
//            LoggerFactory.getTraceLogger().warn(RpcConstant.TAG,
//                    "fragment activity is not ActivityResponsible!");
        }
    }

    public void setRpcUiProcessor(RpcUiProcessor p) {
        this.rpcUiProcessor = p;
    }

    private void init(ActivityResponsable ar, Fragment f) {
        if (f != null) {
            rpcUiProcessor = new RpcUiProcessor(f);
        } else if (ar != null) {
            rpcUiProcessor = new RpcUiProcessor(ar);
        }
        isCancelPending = false;
        isGetCacheSuccess = false;
        isEverSuccess = false;
    }

    /**
     * @param event, event必有效
     */
    @Subscribe(threadMode = "ui")
    public void onEvent(final RpcEvent<ResultType> event) {
        if (event == null || event.rpcTask == null || event.rpcTask.getSubscriber() == null) {
//            LoggerFactory.getTraceLogger().warn(RpcConstant.TAG,
//                    "onEvent call: event || event.rpcTask || event.rpcTask.subscriber is null");
            return;
        }

        final RpcTask task = event.rpcTask;
        if (task.getSubscriber() != this) { // 接受到其他rpc事件，忽略
            return;
        }

        this.event = event;
        // 自动调整loadingMode
        LoadingMode loadingMode = autoChangeLoadingMode(task.getRunConfig());
//        LoggerFactory.getTraceLogger().info(RpcConstant.TAG,
//                String.format("RpcSubscriber onEvent status=%s", event.status));

        if (TextUtils.equals(event.status, RpcConstant.RPC_CANCEL)) {
            isCancelPending = true;
            if (loadingMode == LoadingMode.CANCELABLE_EXIT_LOADING) {
                if (rpcUiProcessor != null && rpcUiProcessor.getActivityResponsible() instanceof Activity) {
                    ((Activity) rpcUiProcessor.getActivityResponsible()).finish();
                }
            }
            onCancel();
            unregisterFromEventBus();
        } else if (TextUtils.equals(event.status, RpcConstant.RPC_START)) {
            // (只在开始时)设置holder id
            rpcUiProcessor.setFlowTipHolderId(task.getRunConfig().flowTipHolderViewId);

            // 开始请求时 打配置参数日志
//            LoggerFactory.getTraceLogger().info(RpcConstant.TAG,
//                    String.format("runConfig=%s", task.getRunConfig().toString()));

            isCancelPending = false;
            if (rpcUiProcessor != null) {
                if (loadingMode == LoadingMode.BLOCK_LOADING) {
                    rpcUiProcessor.showProgressDialog(false, null);
                } else if (loadingMode == LoadingMode.CANCELABLE_LOADING
                        || loadingMode == LoadingMode.CANCELABLE_EXIT_LOADING) {
                    rpcUiProcessor.showProgressDialog(true, new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            postRpcEvent(RpcConstant.RPC_CANCEL, event);
                        }
                    });
                } else if (loadingMode == LoadingMode.TITLEBAR_LOADING) {
                    rpcUiProcessor.showTitleBarLoading();
                }
                // 设置rpcUiProcessor的retry
                rpcUiProcessor.setRetryRunnable(new Runnable() {
                    @Override
                    public void run() {
                        // 再次启动rpc
                        event.rpcRunner.start(task.getParams());
                    }
                });
            } else {
//                LoggerFactory.getTraceLogger().info(RpcConstant.TAG,
//                        "非ui的subscriber, rpcUiProcessor=null");
            }
        } else if (TextUtils.equals(event.status, RpcConstant.RPC_FINISH_START)) {
            if (rpcUiProcessor != null && !(loadingMode == LoadingMode.SILENT)) {
                rpcUiProcessor.dismissProgressDialog();
                if (loadingMode == LoadingMode.TITLEBAR_LOADING) {
                    rpcUiProcessor.dismissTitleBarLoading();
                }
            }
            onFinishStart();
        } else if (TextUtils.equals(event.status, RpcConstant.RPC_FINISH_END)) {
            isCancelPending = false;
            onFinishEnd();
            unregisterFromEventBus();
        } else if (TextUtils.equals(event.status, RpcConstant.RPC_SUCCESS)) {
            if (!isCancelPending) {
                isEverSuccess = true;
                hideFlowTipView();
                onSuccess(event.result);
            }
        } else if (TextUtils.equals(event.status, RpcConstant.RPC_FAIL)) {
            if (!isCancelPending) {
                hideFlowTipView();
                onFail(event.result);
            }
        } else if (TextUtils.equals(event.status, RpcConstant.RPC_EXCEPTION)) {
            if (!isCancelPending) {
                onException(event.exception, event.rpcTask);
            }
        }
        handleCacheEvent(event);
    }

    protected void onFinishStart() {
        // ignore
    }

    protected void onFinishEnd() {
        // ignore
    }

    protected void onCancel() {
        // ignore
    }

    /**
     * 业务成功回调
     * onRpcFinish中调用到，在onRpcException后执行
     * 如果不需要组件的处理功能，请覆盖onSuccess
     * @param result, 保证result及result.result不为NULL
     */
    protected void onSuccess(ResultType result) {
        RpcTask task = getRpcEvent().getRpcTask();
        if (task.getRpcResultProcessor().isEmpty(result)) {
            // 获取自定义文案
            String tip = task.getRpcResultProcessor().convertResultText(result);
            rpcUiProcessor.showEmptyView(tip);
        } else {
            // 在rpc成功后，处理服务端配置的follow action (不处理showType)
            processFollowAction(result);
        }
    }

    /**
     * 业务失败
     * onRpcFinish中调用到，在onRpcException后执行，
     * 如果不需要组件的错误处理功能，请覆盖onFail
     * @param result, 保证不为NULL
     */
    protected void onFail(ResultType result) {
        boolean flag = processFollowAction(result);
        if (!flag) { // 不包含followAction
            // 处理经典的showType和resultView格式
            boolean v = processShowTypeAction(result);
            if (!v) { // 不包含resultView或showType,
                // 按兼容模式执行，需要接入方实现BaseRpcResultProcessor的convertResultText方法
                RpcTask task = getRpcEvent().getRpcTask();
                if (task.getRunConfig().showWarn && shouldShowFlowTip(task)) {
                    // 获取自定义文案
                    String tip = task.getRpcResultProcessor().convertResultText(result);
                    rpcUiProcessor.showWarn("", tip);
                }
            }
        }
    }

    private boolean processFollowAction(ResultType result) {
        if (result != null) {
            Field resultAction = RpcUtil.getFieldByReflect(result, RpcConstant.RPC_RESULT_FOLLOW_ACTION);
            if (resultAction != null) {
                try {
                    Object action = resultAction.get(result);
                    if (action != null && rpcUiProcessor != null) {
                        // 如果result中含非空的followAction，使用followAction处理，不按简单方式处理
                        if (action instanceof String && !TextUtils.isEmpty((String) action)) {
                            ResultActionProcessor.processAction(rpcUiProcessor, (String) action);
                            return true;
                        }
                    }
                } catch (Exception ex) {
//                    LoggerFactory.getTraceLogger().warn(RpcConstant.TAG, ex);
                }
            }
        }
        return false;
    }

    private boolean processShowTypeAction(ResultType result) {
        if (result != null) {
            if (RpcSettings.supportShowType && rpcUiProcessor != null) {
                return ResultActionProcessor.processShowType(rpcUiProcessor, result);
            }
        }
        return false;
    }

    /**
     * rpc本地异常回调
     * 如果是网络异常，且设置相关配置，则显示网络错误页面
     * 如果是其他非网络的异常，且设置了相关配置，则显示提示错误页面
     */
    protected void onException(Exception ex, RpcTask task) {
        if (ex instanceof RpcException && RpcUtil.isNetworkException(ex)) {
            if (task != null && task.getRunConfig().showNetError && shouldShowFlowTip(task)) {
                // 这里区分 无网络异常还是超时异常
                String text = "";
                if (RpcUtil.isNetworkSlow(ex)) {
                    text = rpcUiProcessor.getNetErrorSlowText();
                }
                rpcUiProcessor.showNetworkError(text, "");
            }
            onNetworkException(ex, task);
        } else {
            if (ex instanceof RpcException && task != null && task.getRunConfig().showWarn
                    && shouldShowFlowTip(task)) {
                rpcUiProcessor.showWarn();
            }
            onNotNetworkException(ex, task);
        }
    }

    private LoadingMode autoChangeLoadingMode(RpcRunConfig config) {
        if (isEverSuccess && config.loadingMode == LoadingMode.CANCELABLE_LOADING) {
            return LoadingMode.TITLEBAR_LOADING;
        }
        return config.loadingMode;
    }

    /**
     * 根据当前状态 判断是否要显示异常界面
     */
    private boolean shouldShowFlowTip(RpcTask task) {
        return rpcUiProcessor != null && !isGetCacheSuccess && !isEverSuccess
                && task.getRunConfig().loadingMode != LoadingMode.UNAWARE;
    }

    private void hideFlowTipView() {
        if (rpcUiProcessor != null) {
            rpcUiProcessor.hideFlowTipViewIfShow();
//            LoggerFactory.getTraceLogger().info(RpcConstant.TAG, "如果显示异常界面，则隐藏错误提示");
        }
    }

    /**
     * rpc网络异常回调，默认不处理
     */
    protected void onNetworkException(Exception ex, RpcTask task) {
        // ignore
    }

    /**
     * 非网络异常回调，默认不处理
     */
    protected void onNotNetworkException(Exception ex, RpcTask task) {
        // ignore
    }

    /**
     * 处理缓存回调事件
     */
    private void handleCacheEvent(final RpcEvent<ResultType> event) {
        if (TextUtils.equals(event.status, RpcConstant.RPC_CACHE_START)) {
            isGetCacheSuccess = false;
        } else if (TextUtils.equals(event.status, RpcConstant.RPC_CACHE_FINISH_START)) {
            onCacheFinishStart();
        } else if (TextUtils.equals(event.status, RpcConstant.RPC_CACHE_FINISH_END)) {
            onCacheFinishEnd();
        } else if (TextUtils.equals(event.status, RpcConstant.RPC_CACHE_SUCCESS)) {
            isGetCacheSuccess = true;
            onCacheSuccess(event.result);
        } else if (TextUtils.equals(event.status, RpcConstant.RPC_CACHE_FAIL)) {
            onCacheFail();
        }
    }

    /**
     * 缓存加载完成后 开始时调用
     */
    protected void onCacheFinishStart() {
        // ignore
    }

    /**
     * 缓存加载完成后 结束时调用
     */
    protected void onCacheFinishEnd() {
        // ignore
    }

    /**
     * 缓存加载完成后 有成功数据时调用
     */
    protected void onCacheSuccess(ResultType result) {
        // ignore
    }

    /**
     * 缓存加载完成后无数据时调用
     * 注意：如果缓存有数据但success=false, 即业务失败数据，当作无数据处理
     */
    protected void onCacheFail() {
        // ignore
    }

    /**
     * 获取当前rpc事件
     * 有状态接口，注意调用时机
     */
    public RpcEvent getRpcEvent() {
        return event;
    }

    /**
     * 获取当前UiProcessor, 自定义修改ui行为时需要
     */
    public RpcUiProcessor getRpcUiProcessor() {
        return rpcUiProcessor;
    }

    /**
     * 外部调用 获取当前rpc结果
     * 有状态的接口，注意调用时机
     */
    public ResultType getRpcResult() {
        if (event == null || event.result == null) {
            return null;
        } else {
            return event.result;
        }
    }

    /**
     * 显示空盒子记录接口
     * 空盒子控制
     * 需要知晓rpc响应的数据结构，目前需业务方手动调用
     */
    public void showEmptyView(String tip) {
        if (rpcUiProcessor != null) {
            rpcUiProcessor.showEmptyView(tip);
        }
    }

    private void unregisterFromEventBus() {
        EventBusManager.getInstance().unregisterRaw(this);
    }

    /**
     * 手动调用 取消当前执行中rpc
     * 如果当前并没有rpc执行或者rpc已经执行完成，则不执行任何实际行为
     */
    public void cancelRpc() {
        if (getRpcEvent() == null) {
            return;
        }
        postRpcEvent(RpcConstant.RPC_CANCEL, getRpcEvent());
    }

    private void postRpcEvent(String statusStr, RpcEvent<ResultType> data) {
        RpcEvent<ResultType> rData = new RpcEvent<ResultType>(data.rpcRunner, data.rpcTask, data.result, null);
        rData.status = statusStr;
        EventBusManager.getInstance().post(rData);
    }

}
