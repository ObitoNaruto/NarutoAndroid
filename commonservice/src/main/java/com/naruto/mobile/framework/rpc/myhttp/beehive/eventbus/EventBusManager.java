package com.naruto.mobile.framework.rpc.myhttp.beehive.eventbus;


import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArraySet;

import com.naruto.mobile.framework.utils.DebugUtil;

/**
 * EventBusManager
 * Created by zhanqu.awb on 15/1/30.
 */
public class EventBusManager {

    public static final String TAG = "EventBus";

    private String id;

    private static EventBusManager instance;

    private ConcurrentHashMap<Object, Set<EventHandler>> subscriberMap;

    private ConcurrentHashMap<Object, List<String>> whiteListMap;

    private Handler uiHandler;

    private SubscribeFinder subscriberFinder;

    /** Queues of events for the current thread to dispatch. */
    private final ThreadLocal<ConcurrentLinkedQueue<EventHandlerWrapper>> eventsToDispatch =
            new ThreadLocal<ConcurrentLinkedQueue<EventHandlerWrapper>>() {
                @Override
                protected ConcurrentLinkedQueue<EventHandlerWrapper> initialValue() {
                    return new ConcurrentLinkedQueue<EventHandlerWrapper>();
                }
            };

    /** 线程级flag变量 */
//    private final ThreadLocal<Boolean> isDispatching = new ThreadLocal<Boolean>() {
//        @Override
//        protected Boolean initialValue() {
//            return false;
//        }
//    };

    private EventBusManager() {
        this.id = "DEFAULT";
        init();
    }

    public EventBusManager(String id) {
        if ("DEFAULT".equals(id)) {
            throw new IllegalArgumentException("cannot create 'DEFAULT' event bus, because 'DEFAULT' is global event bus");
        }
        this.id = id;
        init();
    }

    private void init() {
        subscriberMap = new ConcurrentHashMap<Object, Set<EventHandler>>();
        whiteListMap = new ConcurrentHashMap<Object, List<String>>();
        subscriberFinder = SubscribeFinder.ANNOTATED;
        uiHandler = new Handler(Looper.getMainLooper());
    }

    public static synchronized EventBusManager getInstance() {
        if (instance == null) {
            instance = new EventBusManager();
        }
        return instance;
    }

    /**
     * 全局清理
     */
    public synchronized void dispose() {
        subscriberMap.clear();
        subscriberFinder.dispose();
    }

    @Override
    public String toString() {
        return "[Bus \"" + id + "\"]";
    }

    /**
     * 注册事件接收者，默认使用弱引用!
     */
    public boolean register(Object target) {
        return innerRegister(target, true);
    }

    public boolean registerRaw(Object target) {
        return innerRegister(target, false);
    }

    public void unregister(Object target) {
        innerUnregister(target, true);
    }

    public void unregisterRaw(Object target) {
        innerUnregister(target, false);
    }

    /**
     * 增加白名单规则
     * 如果当前已经存在事件对应的白名单，则覆盖老的数据
     * 注意：目前实现缺陷，必须在注册监听前调用才生效!
     * @param eventKey, 字符串或者 事件对象的Class对象
     */
    public void addWhiteList(Object eventKey, List<String> values) {
        if (eventKey == null || values == null || values.isEmpty()) {
//            LoggerFactory.getTraceLogger().warn(TAG, "values to add white list must not be null");
            return;
        }
        if (whiteListMap.contains(eventKey)) {
            whiteListMap.remove(eventKey);
//            LoggerFactory.getTraceLogger().warn(TAG,
//                    toString() + " remove duplicated " + eventKey.toString() + " white list");
        }
        whiteListMap.put(eventKey, values);
    }

    /**
     * 去除白名单规则
     * 注意：绝大部分情况不用调用, 另外目前的实现有缺陷，必须重新注册才能生效!
     */
    public void removeWhiteList(Object eventKey) {
        if (eventKey == null) {
//            LoggerFactory.getTraceLogger().warn(TAG, "event to remove from white list must not be null");
            return;
        }
        if (!whiteListMap.contains(eventKey)) {
//            LoggerFactory.getTraceLogger().warn(TAG, toString() +
//                    " white list " + eventKey.toString() + " do not added before");
        }
        whiteListMap.remove(eventKey);
    }

    private boolean innerRegister(Object target, boolean isWeakRef) {
        if (target == null) {
            throw new NullPointerException("target to register must not be null.");
        }

        //LoggerFactory.getTraceLogger().debug(TAG, toString() + " register start>>>");
        boolean flag = true;
        Map<Object, Set<EventHandler>> foundSubscribers = subscriberFinder.findAllSubscribers(target, isWeakRef);
        for (Object key : foundSubscribers.keySet()) {
            Set<EventHandler> handlers = subscriberMap.get(key);
            if (handlers == null) {
                //concurrent put if absent (使用CopyOnWriteArraySet防止并发问题)
                Set<EventHandler> handlersCreation = new CopyOnWriteArraySet<EventHandler>();
                handlers = subscriberMap.putIfAbsent(key, handlersCreation);
                if (handlers == null) {
                    handlers = handlersCreation;
                }
            }
            final Set<EventHandler> foundHandlers = filterWhiteList(key, foundSubscribers.get(key));
            try {
                if (foundHandlers != null && !foundHandlers.isEmpty()) {
                    // 先做一次重复判断
                    Set<EventHandler> toAdd = getNotSameSubSet(handlers, foundHandlers);
                    if (toAdd != null && !toAdd.isEmpty()) {
                        flag = handlers.addAll(toAdd);
                    } else {
                        flag = false;
//                        LoggerFactory.getTraceLogger()
//                                .info(TAG, "add subscriber : (" + key + ")=> " + foundHandlers.toString() +
//                                        " fail, maybe registered before");
                    }
                }
            } catch (Exception ex) {
                flag = false;
//                LoggerFactory.getTraceLogger().warn(TAG, ex.getMessage());
            }
        }
        //LoggerFactory.getTraceLogger().debug(TAG, toString() + " register end<<<");
        return flag;
    }

    protected void innerUnregister(Object target, boolean isWeakRef) {
        if (target == null) {
            throw new NullPointerException("target to unregister must not be null.");
        }

        //LoggerFactory.getTraceLogger().debug(TAG, toString() + " unregister start>>>");

        Map<Object, Set<EventHandler>> foundSubscribers = subscriberFinder.findAllSubscribers(target, isWeakRef);
        for (Object key : foundSubscribers.keySet()) {
            Set<EventHandler> handlers = subscriberMap.get(key);
            if (handlers == null) {
                continue;
            }
            final Set<EventHandler> foundHandlers = foundSubscribers.get(key);
            try {
                if (foundHandlers != null && !foundHandlers.isEmpty() &&
                        !removeSubscriber(handlers, foundHandlers)) {
//                    LoggerFactory.getTraceLogger()
//                            .info(TAG, "remove subscriber : (" + key + ")=> " + foundHandlers.toString()
//                                    + " fail, maybe not registered");
                }
            } catch (Exception ex) {
//                LoggerFactory.getTraceLogger().warn(TAG, ex);
            }
        }
        //LoggerFactory.getTraceLogger().debug(TAG, toString() + " unregister end<<<");
    }

    private boolean removeSubscriber(Set<EventHandler> from, Set<EventHandler> target) {
        if (target == null || target.isEmpty()) {
            return false;
        }
        // 注意CopyOnWriteArraySet不支持iterator删除
        Set<EventHandler> toDelete = getSameSubSet(from, target);
        if (toDelete != null && !toDelete.isEmpty()) {
            return from.removeAll(toDelete);
        }
        return false;
    }

    private Set<EventHandler> getSameSubSet(Set<EventHandler> from, Set<EventHandler> target) {
        Set<EventHandler> result = null;
        for (EventHandler ev : from) {
            for (EventHandler t: target) {
                if (ev.equals(t)) {
                    if (result == null) {
                        result = new CopyOnWriteArraySet<EventHandler>();
                    }
                    result.add(t);
                    break;
                }
            }
        }
        return result;
    }

    private Set<EventHandler> getNotSameSubSet(Set<EventHandler> from, Set<EventHandler> target) {
        if (from == null || from.isEmpty()) { // 原始列表为空，返回target即可
            return target;
        }
        Set<EventHandler> result = null;
        for (EventHandler t: target) {
            boolean flag = true;
            for (EventHandler ev : from) {
                if (ev.equals(t)) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                if (result == null) {
                    result = new CopyOnWriteArraySet<EventHandler>();
                }
                result.add(t);
            }
        }
        return result;
    }

    private Set<EventHandler> filterWhiteList(Object key, Set<EventHandler> handlers) {
        List<String> whiteList = whiteListMap.get(key);
        Set<EventHandler> result;
        if (whiteList != null && !whiteList.isEmpty()) {
            result = new CopyOnWriteArraySet<EventHandler>();
            for (EventHandler handler : handlers) {
                boolean flag = false;
                for (String s : whiteList) {
                    if (TextUtils.equals(handler.getWhiteListKey(), s)) {
                        flag = true;
                        break;
                    }
                }
                if (flag) {
                    result.add(handler);
                }
            }
        } else {
            result = handlers;
        }
        return result;
    }

    /**
     * 发送事件到bus
     * 适用于只需要事件名，不需要数据的场景
     */
    public void postByName(String eventName) {
        post(null, eventName);
    }


    /**
     * 发送事件到bus
     */
    public void post(Object eventData) {
        post(eventData, "");
    }

    /**
     * 发送事件到bus
     * 可使用非空的eventName区分事件，也可使用eventData的类型区分事件
     * @param eventName 事件名称
     * @param eventData 事件数据
     */
    public void post(Object eventData, String eventName) {
        if (TextUtils.isEmpty(eventName) && eventData == null) {
            String msg = "name should not be empty or object should not be null.";
            if (DebugUtil.isDebug()) {
                throw new IllegalArgumentException(msg);
            } else {
//                LoggerFactory.getTraceLogger().warn(TAG, msg);
                return;
            }
        }

        Set<EventHandler> handlers = getHandlersForEvent(eventName, eventData);
        if (handlers == null || handlers.isEmpty()) {
            String s = (eventData == null) ? "" : eventData.toString();
//            LoggerFactory.getTraceLogger().info(TAG,
//                    toString() + " post event[" + eventName + "," + s + "] do not have handler");
        } else {
            for (EventHandler handler : handlers) {
                if (handler != null) {
                    enqueueEvent(eventData, handler);
                }
            }
            dispatchQueuedEvents();
        }
    }

    protected void enqueueEvent(Object event, EventHandler handler) {
        eventsToDispatch.get().offer(new EventHandlerWrapper(event, handler));
    }

    /**
     * Drain the queue of events to be dispatched. As the queue is being drained, new events may be posted to the end
     * of the queue.
     */
    protected void dispatchQueuedEvents() {
        // bug?
        // 当正在分发处理事件时，暂时屏蔽分发过程
//        if (isDispatching.get()) {
//            return;
//        }
        //isDispatching.set(true);
        try {
            while (true) {
                EventHandlerWrapper eventWithHandler = eventsToDispatch.get().poll();
                if (eventWithHandler == null) {
                    break;
                }
                dispatchEvent(eventWithHandler.event, eventWithHandler.handler);
            }
        } finally {
            //isDispatching.set(false);
        }
    }

    /**
     * 使用不同线程分发事件
     */
    protected void dispatchEvent(Object event, EventHandler wrapper) {
        if (wrapper.getThreadMode() == ThreadMode.CURRENT) {
            dispatchEventOnPostThread(event, wrapper);
        } else if (wrapper.getThreadMode() == ThreadMode.BACKGROUND) {
            dispatchEventOnBackground(event, wrapper);
        } else if (wrapper.getThreadMode() == ThreadMode.UI) {
            dispatchEventOnUi(event, wrapper);
        }
    }

    /**
     * 在后台线程投送事件
     */
    private void dispatchEventOnPostThread(Object event, EventHandler handler) {
        dispatchEventToHandler(event, handler);
    }

    /**
     * 在ui线程投送事件
     */
    private void dispatchEventOnUi(final Object event, final EventHandler handler) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            dispatchEventToHandler(event, handler);
        } else {
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    dispatchEventToHandler(event, handler);
                }
            });
        }
    }

    /**
     * 在后台线程投送事件
     */
    private void dispatchEventOnBackground(final Object event, final EventHandler handler) {
//        BackgroundExecutor.execute(new Runnable() {
//            @Override
//            public void run() {
//                dispatchEventToHandler(event, handler);
//            }
//        });
    }

    private void dispatchEventToHandler(Object event, EventHandler wrapper) {
        try {
            wrapper.handleEvent(event);
        } catch (InvocationTargetException e) {
            //LoggerFactory.getTraceLogger().warn(TAG,
            //        "Could not dispatch event: " + event.getClass() + " to handler " + wrapper);
//            LoggerFactory.getTraceLogger().warn(TAG, e);
        }
    }

    private Set<EventHandler> getHandlersForEvent(String eventName, Object eventData) {
        Set<EventHandler> handlers;
        // 通过obj查找到对应subscriber
        if (TextUtils.isEmpty(eventName)) {
            handlers = subscriberMap.get(eventData.getClass());
        } else {
            handlers = subscriberMap.get(eventName);
        }
        return handlers;
    }

    /** Simple struct representing an event and its handler. */
    protected static class EventHandlerWrapper {

        final Object event;

        final EventHandler handler;

        public EventHandlerWrapper(Object event, EventHandler handler) {
            this.event = event;
            this.handler = handler;
        }
    }

}
