package com.naruto.mobile.framework.rpc.myhttp.common;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicInteger;

import com.naruto.mobile.framework.rpc.myhttp.annotation.OperationType;
import com.naruto.mobile.framework.rpc.myhttp.common.transport.RpcCaller;
import com.naruto.mobile.framework.rpc.myhttp.protocol.Deserializer;
import com.naruto.mobile.framework.rpc.myhttp.protocol.Serializer;
import com.naruto.mobile.framework.rpc.myhttp.utils.ThreadUtil;

/**
 * RPC方法调用器
 * 
 * @hide
 */
public class RpcInvoker {
    /**
     * 返回值
     */
    private static final ThreadLocal<Object> RETURN_VALUE = new ThreadLocal<Object>();
    /**
     * 协议扩展参数
     */
    private static final ThreadLocal<Map<String, Object>> EXT_PARAM = new ThreadLocal<Map<String,Object>>();
    /**
     * 默认，直接调用
     */
    private static final byte MODE_DEFAULT = 0;
    /**
     * 批量调用
     */
    private static final byte MODE_BATCH = 1;

    /**
     * RPC模式
     */
    private byte mMode = MODE_DEFAULT;
//    /**
//     * 随机对象，用于生成请求序列号
//     */
//    private Random mRandom;
    /**
     * 用递增数列号，来代替原来的“随机的请求序列号”，这样还可以标记 请求的前后顺序
     */
    private AtomicInteger rpcSequence;
    
    /**
     * RPC工厂
     */
    private RpcFactory mRpcFactory;

    public RpcInvoker(RpcFactory rpcFactory) {
        mRpcFactory = rpcFactory;
//        mRandom = new Random();
        rpcSequence = new AtomicInteger();
    }

    /**
     * @param clazz 类
     * @param method 方法
     * @param args 参数
     * @return 数据
     * @throws RpcException
     */
    public Object invoke(RpcInvocationHandler handler, Object proxy, Class<?> clazz, Method method,
                         Object[] args) throws RpcException {
        if(ThreadUtil.checkMainThread())
            throw new IllegalThreadStateException("can't in main thread call rpc .");
        OperationType operationType = method.getAnnotation(OperationType.class);
        Type retType = method.getGenericReturnType();
        Annotation[] annotations = method.getAnnotations();
        RETURN_VALUE.set(null);//初始化返回值
        EXT_PARAM.set(null);

        if (operationType == null) {
            throw new IllegalStateException("OperationType must be set.");
        }
        String operationTypeValue = operationType.value();
//        int id = mRandom.nextInt(Integer.MAX_VALUE);
        int id = rpcSequence.incrementAndGet();

        preHandle(proxy, clazz, method, args, annotations);//前置拦截

//        PerformanceLog.getInstance().log("RPC start: operationTypeValue=" + operationTypeValue);
        try {
            if (mMode == MODE_DEFAULT) {
                String data = singleCall(handler, method, args, operationTypeValue, id);
                Deserializer deserializer = handler.getDeserializer(retType, data);
                Object object = deserializer.parser();
                if (retType != Void.TYPE) {//非void
                    RETURN_VALUE.set(object);
                }
//                DefaultMesssageHandler.getInstance().onChangeEvent(EventObject.OnResponse, operationTypeValue,data);
            } else {
                //TODO 批量调用
            }

        } catch (RpcException exception) {
            exception.setOperationType(operationTypeValue);//异常设置OperationType
            exceptionHandle(proxy, clazz, method, args, annotations, exception);//异常拦截
        }

        postHandle(proxy, clazz, method, args, annotations);//后置拦截
//        PerformanceLog.getInstance().log("RPC finish: operationTypeValue=" + operationTypeValue);
        return RETURN_VALUE.get();
    }

    /**
     * 处理所有拦截
     * 
     * @param annotations
     * @param handle
     * @throws RpcException
     */
    private boolean handleAnnotations(Annotation[] annotations, Handle handle) throws RpcException {
        boolean ret = true;
        for (Annotation annotation : annotations) {
            Class<? extends Annotation> c = annotation.annotationType();
            RpcInterceptor rpcInterceptor = mRpcFactory.findRpcInterceptor(c);
            if (rpcInterceptor == null) {
            	break;
//                throw new RpcException(RpcException.ErrorCode.CLIENT_HANDLE_ERROR,
//                    "can not find Interceptor :" + c);
            }
            ret = handle.handle(rpcInterceptor, annotation);
            if (!ret) {
                break;
            }
        }
        return ret;
    }

    /**
     * 后置处理
     * 
     * @param proxy 调用对象
     * @param clazz
     * @param method
     * @param args
     * @param annotations
     * @throws RpcException
     */
    private void postHandle(final Object proxy, final Class<?> clazz, final Method method,
                            final Object[] args, Annotation[] annotations) throws RpcException {
        handleAnnotations(annotations, new Handle() {
            @Override
            public boolean handle(RpcInterceptor rpcInterceptor, Annotation annotation) throws RpcException {
                if (!rpcInterceptor.postHandle(proxy, RETURN_VALUE, clazz, method, args, annotation)) {
                    throw new RpcException(RpcException.ErrorCode.CLIENT_HANDLE_ERROR,
                        rpcInterceptor + "postHandle stop this call.");
                }
                return true;
            }
        });
    }

    /**
     * 异常处理
     * 
     * @param proxy 调用对象
     * @param clazz
     * @param method
     * @param args
     * @param annotations
     * @param exception
     * @throws RpcException
     */
    private void exceptionHandle(final Object proxy, final Class<?> clazz, final Method method,
                                 final Object[] args, Annotation[] annotations,
                                 final RpcException exception) throws RpcException {
        boolean processed = handleAnnotations(annotations, new Handle() {
            @Override
            public boolean handle(RpcInterceptor rpcInterceptor, Annotation annotation) throws RpcException {
                if (rpcInterceptor.exceptionHandle(proxy, RETURN_VALUE, clazz, method, args, exception, annotation)) {
//                    LogCatLog.e("Rpc", exception + "need process");
                    //                    throw exception;
                    return true;
                } else {
//                    LogCatLog.e("Rpc", exception + "need not process");
                    return false;
                }
            }
        });
        if (processed) {
            throw exception;
        }
    }

    /**
     * 前置处理
     * 
     * @param proxy 调用对象
     * @param clazz
     * @param method
     * @param args
     * @param annotations
     * @throws RpcException
     */
    private void preHandle(final Object proxy, final Class<?> clazz, final Method method,
                           final Object[] args, Annotation[] annotations) throws RpcException {
        handleAnnotations(annotations, new Handle() {
            @Override
            public boolean handle(RpcInterceptor rpcInterceptor, Annotation annotation) throws RpcException {
                if (!rpcInterceptor.preHandle(proxy, RETURN_VALUE, clazz, method, args, annotation,EXT_PARAM)) {
                    throw new RpcException(RpcException.ErrorCode.CLIENT_HANDLE_ERROR,
                        rpcInterceptor + "preHandle stop this call.");
                }
                return true;
            }
        });
    }

    /**
     * 单个调用
     * 
     * @param handler
     * @param method
     * @param args
     * @param operationTypeValue
     * @param id
     * @return
     * @throws RpcException
     */
    private String singleCall(RpcInvocationHandler handler, Method method, Object[] args,
                              String operationTypeValue, int id) throws RpcException {
        String data = null;
        //robin
        if(operationTypeValue.contains("alipay.user.login")) {
//            LogCatLog.e("RpcInvoker", "alipay.user.login");
        }
        
        Serializer serializer = handler.getSerializer(id, operationTypeValue, args);//数据格式协议
        //LogCatLog.v("RpcInvoker", "operationType ["+operationTypeValue + serializer.packet()+"]");
        if(EXT_PARAM.get()!=null){
            serializer.setExtParam(EXT_PARAM.get());
        }
        
        RpcCaller caller = handler.getTransport(method, serializer.packet());//传输
        data = (String) caller.call();//同步
        
        EXT_PARAM.set(null);
        //LogCatLog.v("RpcInvoker", "responseData ["+ data +"]");
        return data;
    }

    /**
     * 批量调用开始
     */
    public void batchBegin() {
        mMode = MODE_BATCH;
    }

    /**
     * 批量调用提交
     */
    public FutureTask<?> batchCommit() {
        mMode = MODE_DEFAULT;
        return null;
    }

    /**
     * 添加协议参数
     * @param key
     * @param value
     */
    public static void addProtocolArgs(String key,Object value){
        if(null==EXT_PARAM.get()){
            EXT_PARAM.set(new HashMap<String, Object>());
        }
        EXT_PARAM.get().put(key, value);
    }

    /**
     * 拦截处理
     * 
     * @author sanping.li@alipay.com
     *
     */
    private interface Handle {
        public boolean handle(RpcInterceptor rpcInterceptor, Annotation annotation) throws RpcException;
    }
}
