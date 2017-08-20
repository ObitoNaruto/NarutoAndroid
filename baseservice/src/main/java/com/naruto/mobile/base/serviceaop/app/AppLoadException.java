package com.naruto.mobile.base.serviceaop.app;

/**
 * App加载异常
 * 
 */
public class AppLoadException extends RuntimeException {
    private static final long serialVersionUID = 5169854280037861746L;

    public AppLoadException() {
        super();
    }

    public AppLoadException(String message, Throwable cause) {
        super(message, cause);
    }

    public AppLoadException(String message) {
        super(message);
    }

    public AppLoadException(Throwable cause) {
        super(cause);
    }

}
