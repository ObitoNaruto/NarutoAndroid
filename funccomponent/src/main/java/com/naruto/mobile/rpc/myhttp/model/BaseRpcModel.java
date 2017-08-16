package com.naruto.mobile.rpc.myhttp.model;

import java.lang.reflect.Field;

import com.naruto.mobile.base.log.logging.LogCatLog;
import com.naruto.mobile.framework.rpc.myhttp.beehive.rpc.CacheMode;
import com.naruto.mobile.framework.rpc.myhttp.beehive.rpc.LoadingMode;
import com.naruto.mobile.framework.rpc.myhttp.beehive.rpc.RpcRunConfig;
import com.naruto.mobile.framework.rpc.myhttp.beehive.rpc.RpcRunnable;
import com.naruto.mobile.base.serviceaop.NarutoApplication;
import com.naruto.mobile.framework.biz.common.RpcService;

/**
 * RPC基础模型
 */
public abstract class BaseRpcModel<Service, ResultType> implements RpcRunnable<ResultType> {

    protected Class<Service> mClazz;

    private int mFlowTipViewId;

    private ResultType mResponse;

    public BaseRpcModel(Class<Service> class1) {
        this.mClazz = class1;
    }

    public String getServiceClass() {
        return null != mClazz ? mClazz.getName() : "";
    }

    /**
     * 设置错误布局id
     */
    public BaseRpcModel setFlowTipViewId(int flowTipId) {
        mFlowTipViewId = flowTipId;
        return this;
    }

    /**
     * config的一些设置，各个业务可以自定义
     */
    public RpcRunConfig getRpcRunConfig() {
        RpcRunConfig config = new RpcRunConfig();
        config.showWarn = false;//需要设置flowTipHolderViewId，同步使用
        config.showNetError = false;
        config.loadingMode = LoadingMode.UNAWARE;
        config.flowTipHolderViewId = mFlowTipViewId;
        config.cacheMode = CacheMode.NONE;
        return config;
    }

    /**
     * 是否允许重试
     */
    public boolean allowRetry() {
        return true;
    }

    /**
     * 处理请求
     */
    public ResultType execute(Object... objects) {
        RpcService rpcService = NarutoApplication.getInstance().getNarutoApplicationContext()
                .findServiceByInterface(RpcService.class.getName());
        Service service = rpcService.getRpcProxy(this.mClazz);
        mResponse = requestData(service);
        return mResponse;
    }

    /**
     * 获取返回结果
     */
    public ResultType getResponse() {
        return mResponse;
    }

    /**
     * 重置结果集，保证重试之后的结果是干净的
     */
    public void resetResponse() {
        mResponse = null;
    }

    /**
     * 执行请求
     */
    protected abstract ResultType requestData(Service service);

    /**
     * 获取Error——code
     */
    protected String getResultCode() {
        if (null == mResponse) {
            return "";
        }
        String resultCode = "";
        try {
            Field field = getFieldByReflect("resultCode");
            if (null != field) {
                field.setAccessible(true);
                resultCode = (String) field.get(mResponse);
            }
        } catch (Exception e) {
            LogCatLog.d("o2o-BaseRpcModel",
                    "get field exception" + mResponse.getClass() + "->resultCode: " + e.getMessage());
        } finally {
            return resultCode;
        }
    }

    /**
     * 获取Error-描述信息
     */
    public String getResultDesc() {
        if (null == mResponse) {
            return "";
        }
        String resultDesc = "";
        try {
            //PB格式的错误resultDesc
            Field ex = getFieldByReflect("resultDesc");
            if (null != ex) {
                ex.setAccessible(true);
                resultDesc = (String) ex.get(mResponse);
            } else {
                //csa的错误字段
                ex = getFieldByReflect("desc");
                if (ex != null) {
                    ex.setAccessible(true);
                    resultDesc = (String) ex.get(mResponse);
                }
            }
        } catch (Exception var2) {
        } finally {
            return resultDesc;
        }
    }

    /**
     * 反射获取字段
     */
    private Field getFieldByReflect(String fieldName) {
        Class responseClass = mResponse.getClass();
        Field field = null;
        try {
            field = responseClass.getField(fieldName);
        } catch (Exception e) {
        } finally {
            return field;
        }
    }
}
