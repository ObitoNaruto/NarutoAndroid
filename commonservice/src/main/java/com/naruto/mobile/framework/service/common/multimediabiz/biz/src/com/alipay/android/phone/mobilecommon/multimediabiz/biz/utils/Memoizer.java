package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils;


import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.req.ComputeCallBack;

/**
 * Created by zhefu.wyq 13-11-5.
 *
 * @param <A>
 * @param <V>
 */
public class Memoizer<A extends HasKey, V> {
    private static final String TAG = "Memoizer";
    private Logger logger = Logger.getLogger(TAG);
    private ConcurrentHashMap<Object, Future<V>> cache = new ConcurrentHashMap<Object, Future<V>>();
    private ConcurrentHashMap<Object, Map> listeners = new ConcurrentHashMap<Object, Map>();

    public Memoizer() {
    }

    public V get(final A arg, final Computable<A, V> comp, ComputeCallBack callBack) throws InterruptedException {
//        while (true) {
        final Object key = arg.getKey();
        Future<V> f = cache.get(key);
        if (f == null) {
            Callable<V> eval = new Callable<V>() {
                @Override
                public V call() throws Exception {
                    return comp.compute(arg, new ComputeCallBack() {

                        @Override
                        public void onProgress(int progress) {
                            notifyComputeProgress(key, progress);
                        }
                    });
                }
            };
            FutureTask<V> ft = new FutureTask<V>(eval);
            f = cache.putIfAbsent(key, ft);
            if (f == null) {
                logger.d(key + " run ... ");
                f = ft;
                ft.run();
            } else {
                logger.d(key + " waiting... ");
                addListener(key, callBack);
            }
        } else {
            logger.d(key + " waiting... ");
            addListener(key, callBack);
        }
        try {
            return f.get();
        } catch (CancellationException e) {
            cache.remove(key);
        } catch (ExecutionException e) {
            throw new RuntimeException(e.getCause());
        } finally {
            cache.remove(key);
            removeListener(key);
        }
//        }
        return null;
    }

    public V remove(A arg) {
        Future<V> f = cache.remove(arg.getKey());
        if (f != null) {
            try {
                return f.get();
            } catch (Exception e) {
            }
        }
        return null;
    }

    public void clear() {
        cache.clear();
    }

    private void addListener(Object key, Object callback) {
        Map listener = listeners.get(key);
        if (listener == null) {
            listener = new ConcurrentHashMap();
            listener.put(callback, "");
        }
        listener = listeners.putIfAbsent(key, listener);
        if (listener != null) {
            listener.put(callback, "");
        }
    }

    private void removeListener(Object key) {
        Map listener = listeners.remove(key);
        if (listener != null) {
            listener.clear();
        }
    }

    private void notifyComputeProgress(Object key, int progress) {
        Map callbacks = listeners.get(key);
        if (callbacks != null) {
            Set<ComputeCallBack> cbs = new CopyOnWriteArraySet<ComputeCallBack>(callbacks.keySet());
            for (ComputeCallBack computeCallBack : cbs) {
                computeCallBack.onProgress(progress);
            }
        }
    }
}
