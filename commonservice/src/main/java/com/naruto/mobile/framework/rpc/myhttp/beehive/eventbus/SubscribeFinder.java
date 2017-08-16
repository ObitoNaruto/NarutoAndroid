package com.naruto.mobile.framework.rpc.myhttp.beehive.eventbus;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;

import android.text.TextUtils;

/**
 * 查找使用@Subscribe注解的 注册的事件接收者
 * Created by aowen on 15/7/6.
 */
public class SubscribeFinder {

    /** Cache event bus subscriber methods for each class. */
    private static final ConcurrentMap<Class<?>, Map<Object, Set<Method>>> SUBSCRIBERS_CACHE =
            new ConcurrentHashMap<Class<?>, Map<Object, Set<Method>>>();

    private void loadAnnotatedSubscriberMethods(Class<?> listenerClass,
            Map<Object, Set<Method>> subscriberMethods) {
        loadAnnotatedMethods(listenerClass, subscriberMethods);
    }

    private void loadAnnotatedMethods(Class<?> listenerClass,
            Map<Object, Set<Method>> subscriberMethods) {
        for (Method method : listenerClass.getMethods()) {
            // The compiler sometimes creates synthetic bridge methods as part of the
            // type erasure process. As of JDK8 these methods now include the same
            // annotations as the original declarations. They should be ignored for
            // subscribe/produce.
            if (method.isBridge()) {
                continue;
            }
            if (method.isAnnotationPresent(Subscribe.class)) {
                Class<?>[] parameterTypes = method.getParameterTypes();
                // 只能含0个或一个参数
                if (parameterTypes.length > 1) {
                    throw new IllegalArgumentException("method:"+ method + "@Subscribe parameter Not more than 2");
                }

                Object key;
                Subscribe subscribe = method.getAnnotation(Subscribe.class);
                if (TextUtils.isEmpty(subscribe.name())) {
                    if (parameterTypes.length <= 0) {
                        throw new IllegalArgumentException("method:"+ method +
                                "@Subscribe Necessary parameters='name'");
                    } else {
                        // 参数必须是具体类
                        Class<?> eventType = parameterTypes[0];
                        if (eventType.isInterface()) {
                            throw new IllegalArgumentException("method:" + method + " @Subscribe Object must be a implementation");
                        }

                        if ((method.getModifiers() & Modifier.PUBLIC) == 0) {
                            throw new IllegalArgumentException("method:" + method + " @Subscribe method must be 'public'");
                        }
                        key = eventType;
                    }
                } else {
                    key = subscribe.name();
                }

                Set<Method> methods = subscriberMethods.get(key);
                if (methods == null) {
                    methods = new CopyOnWriteArraySet<Method>();
                    subscriberMethods.put(key, methods);
                }
                methods.add(method);
                //Log.d(EventBusManager.TAG, "loadAnnotatedMethods result: " + methods.toString());
            }
        }
        SUBSCRIBERS_CACHE.put(listenerClass, subscriberMethods);
    }

    /** This implementation finds all methods marked with a {@link com.naruto.mobile.beehive.eventbus.Subscribe} annotation. */
    public Map<Object, Set<EventHandler>> findAllSubscribers(Object listener, boolean isWeakRef) {
        Map<Object, Set<EventHandler>> subscriberMap = new HashMap<Object, Set<EventHandler>>();

        Class<?> listenerClass = listener.getClass();
        Map<Object, Set<Method>> methods = SUBSCRIBERS_CACHE.get(listenerClass);
        if (null == methods) {
            methods = new HashMap<Object, Set<Method>>();
            loadAnnotatedSubscriberMethods(listenerClass, methods);
        }
        if (!methods.isEmpty()) {
            for (Map.Entry<Object, Set<Method>> e : methods.entrySet()) {
                Set<EventHandler> handlers = new CopyOnWriteArraySet<EventHandler>();
                for (Method m : e.getValue()) {
                    Subscribe s = m.getAnnotation(Subscribe.class);
                    String tm = s.threadMode();
                    if (TextUtils.isEmpty(tm)) {
                        tm = ThreadMode.CURRENT.name();
                    }
                    EventHandler eh = new EventHandler(listener, m, ThreadMode.fromString(tm), isWeakRef);
                    eh.setWhiteListKey(s.whiteListKey());
                    handlers.add(eh);
                }
                subscriberMap.put(e.getKey(), handlers);
            }
        }
        return subscriberMap;
    }

    private SubscribeFinder() {
        // No instances
    }

    public void dispose() {
        SUBSCRIBERS_CACHE.clear();
    }

    public static SubscribeFinder
            ANNOTATED = new SubscribeFinder();

}