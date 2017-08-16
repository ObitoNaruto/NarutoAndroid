package com.naruto.mobile.framework.rpc.myhttp.common;


/**
 * RPC异常
 */
public class RpcException extends RuntimeException {
    private static final long serialVersionUID = -2875437994101380406L;

    private String mOperationType;
    /**
     * 异常码
     */
    private int mCode;
    /**
     * 异常消息
     */
    private String mMsg;

    public RpcException(Integer code, String msg) {
        super(format(code, msg));
        mCode = code;
        mMsg = msg;
    }
    
    public RpcException(Integer code, Throwable cause) {
        super(cause);
        mCode = code;
    }

    public RpcException(String msg) {
        super(msg);
        mCode = 0;
        mMsg = msg;
    }

    public String getOperationType() {
        return mOperationType;
    }

    public void setOperationType(String string) {
        mOperationType = string;
    }

    /**
     * 获取异常码
     * 
     * @return 异常码
     */
    public int getCode() {
        return mCode;
    }

    /**
     * 获取异常消息
     * 
     * @return 异常消息
     */
    public String getMsg() {
        return mMsg;
    }

    protected static String format(Integer code, String message) {
        StringBuilder str = new StringBuilder();
        str.append("RPCException: ");
        if (code != null) {
            str.append("[").append(code).append("]");
        }
        str.append(" : ");
        if (message != null) {
            str.append(message);
        }
        return str.toString();
    }
    /**
     * 错误码
     * 
     * 取值为0-1000：
     * <li>0-100：表示客户端本地出错</li>
     * <li>100-600：表示标准http响应码</li>
     * <li>1000-9999：表示服务端出错</li>
     * 
     * @author sanping.li@alipay.com
     *
     */
    public interface ErrorCode {
        /**
         * 成功
         */
        public static final int OK = 1000;

        //**************************************客户端的错误**********************************
        /**
         * 未知错误
         */
        public static final int CLIENT_UNKNOWN_ERROR = 0;
        /**
         * 客户端找不到通讯对象
         */
        public static final int CLIENT_TRANSPORT_UNAVAILABAL_ERROR = 1;
        /**
         * 客户端没有网络
         */
        public static final int CLIENT_NETWORK_UNAVAILABLE_ERROR = 2;
        /**
         * 客户端证书错误
         */
        public static final int CLIENT_NETWORK_SSL_ERROR = 3;
        /**
         * 客户端网络连接超时
         */
        public static final int CLIENT_NETWORK_CONNECTION_ERROR = 4;
        /**
         * 客户端网络速度过慢
         */
        public static final int CLIENT_NETWORK_SOCKET_ERROR = 5;
        /**
         * 客户端请求服务端没返回
         */
        public static final int CLIENT_NETWORK_SERVER_ERROR = 6;
        /**
         * 客户端网络IO错误
         */
        public static final int CLIENT_NETWORK_ERROR = 7;
        /**
         * 客户端网络请求调度错误
         */
        public static final int CLIENT_NETWORK_SCHEDULE_ERROR = 8;
        /**
         * 客户端处理错误
         */
        public static final int CLIENT_HANDLE_ERROR = 9;
        /**
         * 客户端数据反序列化错误,服务端数据格式有误
         */
        public static final int CLIENT_DESERIALIZER_ERROR = 10;
        /**
         * 客户端登录失败
         */
        public static final int CLIENT_LOGIN_FAIL_ERROR = 11;
        /**
         * 客户端登录账号切换
         */
        public static final int CLIENT_USER_CHANGE_ERROR = 12;
        
        /**
         * 请求中断错误，例如线程中断时网络请求会被中断。
         */
        public static final int CLIENT_INTERUPTED_ERROR = 13;
        
        /**
         * 客户端网络缓存错误
         */        
        public static final int CLIENT_NETWORK_CACHE_ERROR = 14;

        //********************************************服务端的错误************************************

        // ======================1001-1999 权限错误==================
        /**
         * 拒绝访问。
         */
        public static final int SERVER_PERMISSIONDENY = 1001;

        /**
         * 调用次数超过限制:系统繁忙，请稍后再试。
         */
        public static final int SERVER_INVOKEEXCEEDLIMIT = 1002;

        //=====================2000-2999 通用业务错误==================
        /**
         * 登录超时，请重新登录:登录超时，请重新登录。
         */
        public static final int SERVER_SESSIONSTATUS = 2000;
        /**
         * 缺少操作类型或者此操作类型不支持
         */
        public static final int SERVER_OPERATIONTYPEMISSED = 3000;
        /**
         *请求数据为空:系统繁忙，请稍后再试。
         */
        public static final int SERVER_REQUESTDATAMISSED = 3001;
        /**
         * 数据格式有误。
         */
        public static final int SERVER_VALUEINVALID = 3002;

        //===================4001-4999 远程调用异常=====================
        /**
         * 服务请求超时，请稍后再试:。
         */
        public static final int SERVER_REQUESTTIMEOUT = 4001;
        /**
         * 远程调用业务系统异常:网络繁忙，请稍后再试。
         */
        public static final int SERVER_REMOTEACCESSEXCEPTION = 4002;
        /**
         * 创建远程调用代理失败:网络繁忙，请稍后再试。
         */
        public static final int SERVER_CREATEPROXYERROR = 4003;
        /**
         * 未知错误:抱歉，暂时无法操作，请稍后再试。
         */
        public static final int SERVER_UNKNOWERROR = 5000;
        
        //=====================6000-6999为spi包的RPC异常===============
        /**
         * RPC-服务找不到。
         */
        public static final int SERVER_SERVICENOTFOUND = 6000;
        /**
         * RPC-目标方法找不到。
         */
        public static final int SERVER_METHODNOTFOUND = 6001;
        /**
         * RPC-参数数目不正确
         */
        public static final int SERVER_PARAMMISSING = 6002;
        /**
         * RPC-目标方法不可访问。
         */
        public static final int SERVER_ILLEGALACCESS = 6003;
        /**
         * PRC-JSON解析异常。
         */
        public static final int SERVER_JSONPARSEREXCEPTION = 6004;
        /**
         * PRC-调用目标方法时参数不合法
         */
        public static final int SERVER_ILLEGALARGUMENT = 6005;
        /**
         * RPC-业务异常。
         */
        public static final int SERVER_BIZEXCEPTION = 6666;

    }

}
