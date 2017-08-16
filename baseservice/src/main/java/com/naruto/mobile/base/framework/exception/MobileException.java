package com.naruto.mobile.base.framework.exception;


/**
 * 异常
 * 
 * @author sanping.li@alipay.com
 *
 */
public abstract class MobileException extends RuntimeException {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
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
    public MobileException(String msg) {
        super(msg);
        mCode = 0;
        mMsg = msg;
    }

    /**
     * @param code 异常码
     * @param msg 异常消息
     */
    public MobileException(Integer code, String msg) {
        super(format(code, msg));
        mCode = code;
        mMsg = msg;
    }
    
    public MobileException(Integer code,Throwable cause) {
    	super(cause);
    	mCode = code;
    	
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
        str.append("MobileException: ");
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
