package com.naruto.mobile.framework.rpc.myhttp.transport.http;

/**
 * HTTP网络异常
 * 
 * @author sanping.li@alipay.com
 *
 */
public class HttpException extends Exception {
    private static final long serialVersionUID = -6320569206365033676L;

    /**
     * 未知错误
     */
    public static final int NETWORK_UNKNOWN_ERROR = 0;
    /**
     * 没有网络
     */
    public static final int NETWORK_UNAVAILABLE = 1;
    /**
     * 证书异常
     */
    public static final int NETWORK_SSL_EXCEPTION = 2;
    /**
     * 网络连接超时
     */
    public static final int NETWORK_CONNECTION_EXCEPTION = 3;
    /**
     * 网络速度过慢
     */
    public static final int NETWORK_SOCKET_EXCEPTION = 4;
    /**
     * 服务端没返回
     */
    public static final int NETWORK_SERVER_EXCEPTION = 5;
    /**
     * 网络IO异常
     */
    public static final int NETWORK_IO_EXCEPTION = 6;
    /**
     * 客户端网络请求调度错误
     */
    public static final int NETWORK_SCHEDULE_ERROR = 7;

    /**
     * 异常码
     */
    private int mCode;
    /**
     * 异常消息
     */
    private String mMsg;

    /**
     * @param msg 异常消息
     */
    public HttpException(String msg) {
        super(msg);
        mCode = NETWORK_UNKNOWN_ERROR;
        mMsg = msg;
    }

    /**
     * @param code 异常码
     * @param msg 异常消息
     */
    public HttpException(Integer code, String msg) {
        super(format(code, msg));
        mCode = code;
        mMsg = msg;
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

    private static String format(Integer code, String message) {
        StringBuilder str = new StringBuilder();
        str.append("Http Transport error");
        if (code != null) {
            str.append("[").append(code).append("]");
        }
        str.append(" : ");
        if (message != null) {
            str.append(message);
        }
        return str.toString();
    }

}
