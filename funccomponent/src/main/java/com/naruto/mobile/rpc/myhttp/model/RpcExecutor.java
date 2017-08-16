package com.naruto.mobile.rpc.myhttp.model;

import android.os.SystemClock;

import com.naruto.mobile.base.framework.app.ui.ActivityResponsable;
import com.naruto.mobile.base.log.logging.LogCatLog;
import com.naruto.mobile.framework.rpc.myhttp.beehive.rpc.BaseRpcResultProcessor;
import com.naruto.mobile.framework.rpc.myhttp.beehive.rpc.LoadingMode;
import com.naruto.mobile.framework.rpc.myhttp.beehive.rpc.RpcRunner;
import com.naruto.mobile.framework.rpc.myhttp.beehive.rpc.RpcSubscriber;
import com.naruto.mobile.framework.rpc.myhttp.beehive.rpc.RpcTask;
import com.naruto.mobile.framework.rpc.myhttp.common.RpcException;

/**
 * rpc执行者
 */
public class RpcExecutor<ResultType> extends RpcSubscriber<ResultType> {

    private BaseRpcModel mRpcModel;

    private OnRpcRunnerListener mListener;

    private RpcRunner mRpcRunner;

    private BaseRpcResultProcessor mBaseRpcResultProcessor;

    private long mTraceTimeStart = 0;

    public RpcExecutor(BaseRpcModel model, ActivityResponsable ar) {
        super(ar);
        this.mRpcModel = model;
    }

    public void setListener(OnRpcRunnerListener listener) {
        this.mListener = listener;
    }

    public void setRpcResultProcessor(BaseRpcResultProcessor processor) {
        this.mBaseRpcResultProcessor = processor;
    }

    /**
     * 移除监听
     */
    public void clearListener() {
        this.mListener = null;
        mRpcRunner = null;
        mRpcModel = null;
//        if (BuildConfig.DEBUG) {
//            ViewHockHelper.get().release();
//        }
    }

    /**
     * 获取结果
     */
    public Object getResponse() {
        return null == mRpcModel ? null : mRpcModel.getResponse();
    }

    /**
     * 执行
     */
    public void run() {
        if (null == mRpcModel) {
            return;
        }
        if (null == mRpcRunner) {
            if (mBaseRpcResultProcessor == null) {
                mRpcRunner = new RpcRunner(mRpcModel.getRpcRunConfig(), mRpcModel, this);
            } else {
                mRpcRunner = new RpcRunner(mRpcModel.getRpcRunConfig(), mRpcModel, this, mBaseRpcResultProcessor);
            }
        }
        mRpcRunner.start();
    }

    protected void onStart() {
        if (null != mRpcModel) {
            mRpcModel.resetResponse();
        }
        //开始rpc之前时间记录-用于埋点
        mTraceTimeStart = SystemClock.elapsedRealtime();
    }

    @Override
    protected void onSuccess(ResultType result) {
        //成功埋点需求
        if (null != mRpcModel) {
            long timeRPC = SystemClock.elapsedRealtime() - mTraceTimeStart;
//            MonitorLogWrap.performance("O2OHOME_RPC", mRpcModel.getServiceClass(), String.valueOf(timeRPC));
        }
        if (null != mListener) {
            mListener.onSuccess(this, result, false);
        }
//        if (BuildConfig.DEBUG) {
//            ViewHockHelper.get().invoke(this, mRpcModel, result);
//        }
    }

    @Override
    protected void onCacheSuccess(ResultType result) {
        super.onCacheSuccess(result);
        if (null != mListener) {
            mListener.onSuccess(this, result, true);
        }
    }

    @Override
    protected void onCacheFail() {
        if (null != mListener) {
            mListener.onFailed(this, ErrorTypeCode.CACHE_READ_ERROR, "");
        }
    }

    @Override
    protected void onFail(ResultType result) {
        if (null != mListener) {
            mListener.onFailed(this, ErrorTypeCode.BUSINESS_ERROR, mRpcModel.getResultDesc());
        }
//        if (BuildConfig.DEBUG) {
//            ViewHockHelper.get().invoke(this, mRpcModel, result);
//        }
    }

    @Override
    protected void onNetworkException(Exception ex, RpcTask task) {
        if (null != mListener) {
            RpcException rpcException = (RpcException) ex;
            mListener.onFailed(this, ErrorTypeCode.NETWORK_ERROR, rpcException.getMsg());
            LogCatLog
                    .d("o2o_RpcExecutor_onNetworkException", rpcException.getMsg() + " code:" + rpcException.getCode());
        }
    }

    @Override
    protected void onNotNetworkException(Exception ex, RpcTask task) {
        boolean showThrow = false;
        if (null != mListener) {
            int code = ErrorTypeCode.OTHER_ERROR;
            String msg = "";//RPC异常，全部采用网关返回的异常进行提示处理
            if (ex instanceof RpcException) {
                RpcException rpcException = (RpcException) ex;
                code = rpcException.getCode();
                showThrow = code == 1002;//限流错误
                msg = rpcException.getMsg();
                LogCatLog.d("o2o_RpcExecutor_onNotNetworkException", msg + " code:" + code);
//                if (7003 == code || RpcException.ErrorCode.CLIENT_NETWORK_SSL_ERROR == code) {
//                    msg = o2oCommonRes.getString(R.string.rpc_error_datetime);
//                } else if (code > 1000) {
//                    msg = rpcException.getMsg();
//                    showThrow = code == 1002;
//                }
            } else {
                LogCatLog.d("o2o_RpcExecutor_onNotRpcException", ex.toString());
//                LogCatLog.getExceptionMsg(ex);
            }
            mListener.onFailed(this, code, msg);
        }
        //如果静默模式，但是1002错误还是扔给框架处理
        if (task.getRunConfig().loadingMode == LoadingMode.UNAWARE && showThrow) {
            throw (RpcException) ex;
        }
    }

    /**
     * 错误类型
     */
    public static class ErrorTypeCode {

        public final static int BUSINESS_ERROR = -1000;

        public final static int CACHE_READ_ERROR = -1005;

        public final static int NETWORK_ERROR = -2000;

        public final static int OTHER_ERROR = -3000;
    }

    /**
     * rpc请求监听，简化为只监听onSuccess onFail
     */
    public interface OnRpcRunnerListener {

        /**
         * 成功回调
         *
         * @param requestType 请求的type，用以区分一个页面多个rpc请求同一个回调类
         * @param result      结果对象
         * @param fromCache   是否从缓存中读取接口
         */
        void onSuccess(RpcExecutor requestType, Object result, boolean fromCache);

        /**
         * 失败回调
         *
         * @param requestType  请求的type
         * @param errorType    错误类型0业务错误，1网络错误，2其他非异常
         * @param exceptionMsg 错误类型非0的情况，返回的错误信息
         */
        void onFailed(RpcExecutor requestType, int errorType, String exceptionMsg);
    }
}
