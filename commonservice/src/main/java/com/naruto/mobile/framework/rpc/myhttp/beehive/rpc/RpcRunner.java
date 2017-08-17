package com.naruto.mobile.framework.rpc.myhttp.beehive.rpc;

import android.os.Looper;
import android.text.TextUtils;

import java.lang.reflect.Modifier;
import java.util.concurrent.ThreadPoolExecutor;

import com.naruto.mobile.framework.rpc.myhttp.beehive.eventbus.EventBusManager;
import com.naruto.mobile.framework.rpc.myhttp.common.RpcException;
import com.naruto.mobile.base.serviceaop.demo.service.TaskScheduleService;
import com.naruto.mobile.framework.utils.DebugUtil;
import com.naruto.mobile.framework.utils.ServiceUtil;

/**
 * 使用事件总线实现的rpc执行器
 */
public class RpcRunner {

    // rpc配置及参数
    private RpcTask<?> rpcTask;

    /**
     * 标记当前rpc请求是否是页面初始化请求
     * 页面初始化请求 和 刷新请求 会影响具体加载过程
     * 刷新请求会忽略缓存加载过程
     * 默认为true
     */
    private boolean isInitRun = true;

    // 是否校验Runnable类的类型
    private boolean isCheckRunnableClass;

    private TaskScheduleService taskScheduleService;

//    private RpcRunner() {
//        // 默认不开启Runnable类校验
//        isCheckRunnableClass = false;
//    }

    public <T> RpcRunner(RpcRunnable<T> r, RpcSubscriber<T> subscriber) {
        init(new RpcRunConfig(), r, subscriber, null);
    }

    public <T> RpcRunner(RpcRunConfig config, RpcRunnable<T> r, RpcSubscriber<T> subscriber) {
        init(config, r, subscriber, null);
    }

    public <T> RpcRunner(RpcRunConfig config, RpcRunnable<T> r, RpcSubscriber<T> subscriber,
            BaseRpcResultProcessor p) {
        init(config, r, subscriber, p);
    }

    private <T> void init(RpcRunConfig config, RpcRunnable<T> r, RpcSubscriber<T> subscriber,
            BaseRpcResultProcessor p) {
        // 默认不开启Runnable类校验
        isCheckRunnableClass = false;
        rpcTask = new RpcTask<T>(config, r, subscriber, p);

        taskScheduleService = ServiceUtil.getServiceByInterface(TaskScheduleService.class);
    }

    /**
     * 静态方法，简化调用接口
     * @param config,        rpc执行配置
     * @param rpcRunnable,   rpc调用接口实现
     * @param rpcSubscriber, rpc回调处理
     * @param params,        rpc调用参数
     */
    public static void run(RpcRunConfig config,
            RpcRunnable<?> rpcRunnable,
            RpcSubscriber rpcSubscriber,
            Object... params) {
        run(config, rpcRunnable, rpcSubscriber, null, params);
    }

    /**
     * 带resultProcessor的执行方法
     */
    public static void run(RpcRunConfig config,
            RpcRunnable<?> rpcRunnable,
            RpcSubscriber rpcSubscriber,
            BaseRpcResultProcessor<?> resultProcessor,
            Object... params) {
        RpcRunner runner = new RpcRunner(config, rpcRunnable, rpcSubscriber, resultProcessor);
        runner.start(params);
    }

    /**
     * 启动RPC任务
     * 注：再次调用start时将不加载缓存
     */
    public void start(Object... params) {
        if (rpcTask != null) {
            rpcTask.setParams(params);
        }
        start(rpcTask);
    }

    /**
     * 外部直接传入task调用
     */
    public void start(final RpcTask<?> task) {
        if (task == null) {
            throw new IllegalArgumentException("task must not be null");
        }
        if (task.getRunnable() == null) {
            throw new IllegalArgumentException("task.runnable must not be null");
        }
        if (task.getSubscriber() == null) {
//            LoggerFactory.getTraceLogger().warn(RpcConstant.TAG, "空subscriber, 是否业务要求不处理任何回调?");
        }
        if (task.getRpcResultProcessor() == null) {
            task.setRpcResultProcessor(new DefaultRpcResultProcessor());
        }
        // 为了防止匿名内部类包含Activity,Fragment,View隐式引用导致内存泄露
        // 在开发环境下检测rpcRunnable是否匿名类, 是则抛出异常!
        if (DebugUtil.isDebug() && isCheckRunnableClass) {
            if (checkIsAnonymousClass(task.getRunnable().getClass())) {
                throw new IllegalArgumentException("for avoid memory leak reason, "
                        + "runnable must not be anonymous Class");
            }
            // 在开发环境下检测rpcRunnable是否非静态内部类, 是则抛出异常!
            if (checkIsNotStaticInnerClass(task.getRunnable().getClass())) {
                throw new IllegalArgumentException("for avoid memory leak reason, "
                        + "runnable must not be none static inner Class");
            }
        }

        // 注册rpc subscriber
        if (task.getSubscriber() != null) {
            EventBusManager.getInstance().registerRaw(task.getSubscriber());
        }

        if (this.rpcTask != task) {
            this.rpcTask = task;
            onTaskChanged();
        }

        Runnable r = new Runnable() {
            @Override
            public void run() {
                // 根据缓存配置 确定加载顺序
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        invokeRpc(rpcTask);
                    }
                };
                if (rpcTask.getRunConfig().cacheMode == CacheMode.CACHE_AND_RPC
                        && !TextUtils.isEmpty(rpcTask.getRunConfig().cacheKey)
                        && isInitRun) {
                    loadRpcCache(rpcTask, r);
                } else {
                    r.run();
                }
            }
        };

        if (Looper.getMainLooper() == Looper.myLooper()) { // 如果当前为ui线程，则使用后台线程执行
            executeInBackgroundThread(r);
        } else { // 支持rpc同步执行: 如果当前线程不是ui线程，则在当前线程直接运行(但所有回调还是在ui线程执行)
            r.run();
        }
    }

    private void executeInBackgroundThread(Runnable r) {
        ThreadPoolExecutor e = taskScheduleService.acquireExecutor(TaskScheduleService.ScheduleType.RPC);
        if (e != null) {
            e.execute(r);
        } else {
//            LoggerFactory.getTraceLogger().warn(RpcConstant.TAG, "执行rpc前获取线程池失败");
        }
    }

    /**
     * 执行RPC请求任务
     */
    private void invokeRpc(RpcTask<?> task) {
        Exception exception = null;
        Object result = null;
        try {
            postRpcEvent(RpcConstant.RPC_START, null, null, task);
            result = task.getRunnable().execute(task.getParams());
        } catch (RpcException ex) {
            exception = ex;
            if (task.getRunConfig().loadingMode == LoadingMode.UNAWARE) {
                // 无感知模式, 不向框架抛异常
//                LoggerFactory.getTraceLogger().warn(RpcConstant.TAG,
//                        "无感知调用方式，不向框架抛出异常");
            } else {
                if (RpcUtil.isNetworkException(ex)) {
                    if (!task.getRunConfig().showNetError) { // 不显示网络FlowTipView时，才抛给框架处理
//                        LoggerFactory.getTraceLogger().warn(RpcConstant.TAG,
//                                "rpc网络异常，并抛给框架处理");
                        throw ex;
                    } else {
//                        LoggerFactory.getTraceLogger().warn(RpcConstant.TAG,
//                                "rpc网络异常，显示无网络界面，不抛给框架处理");
                    }
                } else { // 非网络错误异常 抛给框架
//                    LoggerFactory.getTraceLogger().warn(RpcConstant.TAG,
//                            "rpc非网络异常，并抛给框架处理");
                    throw ex;
                }
            }
        } catch (Exception e) {
            exception = e;
//            LoggerFactory.getTraceLogger().warn(RpcConstant.TAG, exception);
        } finally {
            try {
                onRpcFinish(result, exception, task);
            } catch (Exception ex) {
//                LoggerFactory.getTraceLogger().warn(RpcConstant.TAG, ex);
            }
        }
    }

    private void onRpcFinish(Object result, Exception ex, RpcTask<?> task) {
        // 修改isInitRun,
        // 当同一RpcRunner对象, 再次执行时将以刷新模式执行, 不再加载缓存
        isInitRun = false;

        postRpcEvent(RpcConstant.RPC_FINISH_START, result, ex, task);
        // 这里需要
        if (ex != null) {
            postRpcEvent(RpcConstant.RPC_EXCEPTION, null, ex, task);
        } else {
            if (task.getRpcResultProcessor() == null) {
//                LoggerFactory.getTraceLogger().warn(RpcConstant.TAG, "rpcResultProcessor意外为空，"
//                        + "请检查是否手动设置过!");
            } else {
                if (isRpcSuccess(task, result)) {
                    onRpcSuccess(result, task);
                } else {
                    onRpcFail(result, task);
                }
            }
        }
        postRpcEvent(RpcConstant.RPC_FINISH_END, result, ex, task);
    }

    protected void onRpcSuccess(Object result, RpcTask task) {
        post(buildRpcEvent(RpcConstant.RPC_SUCCESS, result, null, task));
        // 缓存处理
        if (task.getRunConfig().cacheMode == CacheMode.CACHE_AND_RPC
                || task.getRunConfig().cacheMode == CacheMode.RPC_OR_CACHE) {
            saveRpcCache(result, task);
        }
    }

    protected void onRpcFail(Object result, RpcTask task) {
        post(buildRpcEvent(RpcConstant.RPC_FAIL, result, null, task));
    }

    private <T> void loadRpcCache(final RpcTask<T> task, Runnable r) {
        Object result = RpcCache.get(task.getRunConfig().cacheKey,
                task.getRunnable().getClass());

        postRpcEvent(RpcConstant.RPC_CACHE_FINISH_START, result, null, task);

        if (task.getRpcResultProcessor() == null) {
//            LoggerFactory.getTraceLogger().warn(RpcConstant.TAG, "rpcResultProcessor意外为空，"
//                    + "请检查是否手动设置过!");
        } else {
            if (isRpcSuccess(task, result)) {
                if (task.getRunConfig().cacheMode == CacheMode.CACHE_AND_RPC
                        && task.getRunConfig().autoModifyLoadingOnCache) {
                    modifyLoadingModeOnCache(task);
                }
                post(buildRpcEvent(RpcConstant.RPC_CACHE_SUCCESS, result, null, task));
            } else {
                post(buildRpcEvent(RpcConstant.RPC_CACHE_FAIL, result, null, task));
            }
        }
        postRpcEvent(RpcConstant.RPC_CACHE_FINISH_END, result, null, task);
        if (r != null) {
            r.run();
        }
    }

    private boolean isRpcSuccess(RpcTask task, Object result) {
        try {
            return task.getRpcResultProcessor().isSuccess(result);
        } catch(Exception e) {
//            LoggerFactory.getTraceLogger().warn(RpcConstant.TAG, e);
        }
        return false;
    }

    private void modifyLoadingModeOnCache(final RpcTask<?> task) {
        RpcRunConfig config = task.getRunConfig();
        if (config.loadingMode == LoadingMode.CANCELABLE_LOADING
                || config.loadingMode == LoadingMode.BLOCK_LOADING) {
            config.loadingMode = LoadingMode.TITLEBAR_LOADING;
        }
    }

    private void saveRpcCache(Object result, RpcTask task) {
        RpcCache.put(result, task.getRunConfig().cacheKey);
    }

    private void postRpcEvent(String status, Object result, Exception ex, RpcTask task) {
        post(buildRpcEvent(status, result, ex, task));
    }

    private void post(RpcEvent event) {
        if (event != null && rpcTask != null && rpcTask.getSubscriber() != null) {
            EventBusManager.getInstance().post(event);
        }
    }

//    private Class<?>[] getParamTypes(Object... params) {
//        Class<?>[] paramTypes = null;
//        if (params != null) {
//            paramTypes = new Class[params.length];
//            for (int i = 0; i < params.length; i++) {
//                paramTypes[i] = params[i].getClass();
//            }
//        }
//        return paramTypes;
//    }

//    /**
//     * 自动生成token
//     */
//    private String buildRpcToken(WrapRpcTask wp) {
//        String result = wp.facadeClass.getName() + "_" + wp.methodName;
//        if (wp.paramTypes != null) {
//            for (int i = 0; i < wp.paramTypes.length; i++) {
//                result = result + "_" + wp.paramTypes[i].getName();
//            }
//        }
//
//        return result;
//    }

    private RpcEvent buildRpcEvent(String status, Object result, Exception exception,
            RpcTask task) {
        try {
            RpcEvent data = new RpcEvent(this, task, result, exception);
            data.status = status;
            return data;
        } catch (Exception ex) {
//            LoggerFactory.getTraceLogger().warn(RpcConstant.TAG, exception);
        }
        return null;
    }

    private void onTaskChanged() {
        isInitRun = true;
    }

    public RpcTask<?> getRpcTask() {
        return rpcTask;
    }

    public RpcRunConfig getRpcRunConfig() {
        if (rpcTask != null) {
            return rpcTask.getRunConfig();
        }
        return null;
    }

    public RpcSubscriber getRpcSubscriber() {
        if (rpcTask != null) {
            return rpcTask.getSubscriber();
        }
        return null;
    }

    private boolean checkIsNotStaticInnerClass(Class<?> v) {
        return v.getEnclosingClass() != null && !Modifier.isStatic(v.getModifiers());
    }

    private boolean checkIsAnonymousClass(Class<?> v) {
        return v.isAnonymousClass();
    }

}
