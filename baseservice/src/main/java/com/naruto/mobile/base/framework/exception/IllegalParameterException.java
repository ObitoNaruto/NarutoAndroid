package com.naruto.mobile.base.framework.exception;
/**
 * 
 * 非法参数异常
 * @author haigang.gong@alipay.com
 *
 */
public class IllegalParameterException extends MobileException {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = -4505876205013783700L;
	
	
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
    public IllegalParameterException(String msg) {
        super(msg);
        this.mCode = 0;
        this.mMsg = msg;
    }

    /**
     * @param code 异常码
     * @param msg 异常消息
     */
    public IllegalParameterException(Integer code, String msg) {
        super(format(code, msg));
        this.mCode = code;
        this.mMsg = msg;
    }

	@Override
	public String toString() {
		return "IllegalParameterException [mCode=" + mCode + ", mMsg=" + mMsg
				+ "]";
	}


}
