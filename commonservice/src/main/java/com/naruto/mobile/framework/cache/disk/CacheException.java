package com.naruto.mobile.framework.cache.disk;


/**
 * 持久缓存异常
 * 
 * @author sanping.li@alipay.com
 *
 */
public class CacheException extends Exception {
    private static final long serialVersionUID = 4834380159352631260L;

    /**
     * 持久缓存异常错误码
     * 
     * @author sanping.li@alipay.com
     *
     */
    public static  enum ErrorCode{
        /**
         * 写IO错误
         */
        WRITE_IO_ERROR(0),
        /**
         * 读IO错误
         */
        READ_IO_ERROR(1);
        
        private ErrorCode(int value) {
            mCode = value;
        }
        
        private int mCode;

        public int getValue() {
            return mCode;
        }
    } 
    /**
     * 异常码
     */
    private ErrorCode mCode;
    /**
     * 异常消息
     */
    private String mMsg;
    /**
     * @param msg 异常消息
     */
    public CacheException(String msg) {
        super(msg);
        mCode = ErrorCode.WRITE_IO_ERROR;
        mMsg = msg;
    }

    /**
     * @param code 异常码
     * @param msg 异常消息
     */
    public CacheException(ErrorCode code, String msg) {
        super(format(code, msg));
        mCode = code;
        mMsg = msg;
    }

    /**
     * 獲取錯誤碼
     * 
     * @return 錯誤碼
     */
    public ErrorCode getCode() {
        return mCode;
    }

    /**
     * 獲取錯誤消息
     * 
     * @return 錯誤消息
     */
    public String getMsg() {
        return mMsg;
    }

    private static String format(ErrorCode code, String message) {
        StringBuilder str = new StringBuilder();
        str.append("Cache error");
        if (code != null) {
            str.append("[").append(code.getValue()).append("]");
        }
        str.append(" : ");
        if (message != null) {
            str.append(message);
        }
        return str.toString();
    }
}
