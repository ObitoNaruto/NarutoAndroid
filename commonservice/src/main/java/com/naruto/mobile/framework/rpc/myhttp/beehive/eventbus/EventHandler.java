package com.naruto.mobile.framework.rpc.myhttp.beehive.eventbus;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * 事件处理器封装
 * Created by aowen on 15/7/6.
 */
public class EventHandler {

    /** 是否弱引用 */
    private boolean isWeakRef;

    /** 事件监听对象，如果开启弱引用功能 则是目标的WeakRef */
    private final Object subscriber;

    /** Handler method. */
    private final Method method;

    /** handler执行所在线程 */
    private ThreadMode threadMode;

    /** 白名单key */
    private String whiteListKey;

    /** Object hash code. */
    private final int hashCode;

    public EventHandler(Object target, Method method, ThreadMode threadMode, boolean isWeakRef) {
        if (target == null) {
            throw new NullPointerException("EventHandler target cannot be null.");
        }
        if (method == null) {
            throw new NullPointerException("EventHandler method cannot be null.");
        }

        this.isWeakRef = isWeakRef;
        if (isWeakRef) {
            this.subscriber = new WeakReference<Object>(target);
        } else {
            this.subscriber = target;
        }

        this.method = method;
        method.setAccessible(true);

        this.threadMode = threadMode;
        // Compute hash code eagerly since we know it will be used frequently and
        // we cannot estimate the runtime of the target's hashCode call.
        final int prime = 31;
        hashCode = (prime + method.hashCode()) * prime + target.hashCode();
    }

    public ThreadMode getThreadMode() {
        return threadMode;
    }

    /**
     * Invokes the wrapped handler method to handle
     */
    public void handleEvent(Object event) throws InvocationTargetException {
        try {
            invoke(event);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof Error) {
                throw (Error) e.getCause();
            } else {
                throw e;
            }
        }
    }

    private void invoke(Object event) throws InvocationTargetException {
        try {
            Class<?>[] parameterTypes = method.getParameterTypes();
            Object v;
            if (isWeakRef) {
                WeakReference w = (WeakReference) subscriber;
                if (w.get() != null) {
                    v = w.get();
                } else {
//                    LoggerFactory.getTraceLogger().warn("EventBus",
//                            "EventHandler( " + method.getDeclaringClass().getName() + "." +
//                                    method.getName() + ")" + "not exec because it use WeakRef and is garbage collected");
                    return;
                }
            } else {
                v = subscriber;
            }
            // 只能含0个或一个参数
            if (parameterTypes.length == 1) {
                method.invoke(v, event);
            } else if (parameterTypes.length == 0) {
                method.invoke(v);
            }
        } catch (IllegalAccessException e) {
            throw new AssertionError(e);
        }
    }

    @Override
    public String toString() {
        return "[EventHandler " + subscriber + ", " + method + "]";
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    public String getWhiteListKey() {
        return whiteListKey;
    }

    public void setWhiteListKey(String whiteListKey) {
        this.whiteListKey = whiteListKey;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }


        final EventHandler other = (EventHandler) obj;
        if (isWeakRef != other.isWeakRef) {
            return false;
        }

        if (!isWeakRef) {
            return method.equals(other.method) && subscriber == other.subscriber;
        } else {
            WeakReference first = (WeakReference)subscriber;
            WeakReference second = (WeakReference)other.subscriber;
            return method.equals(other.method) && first.get() == second.get();
        }
    }

}
