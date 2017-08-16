package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.BitmapCacheLoader;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.assist.DefaultConfigurationFactory;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.task.ImageTask;

/**
 *
 * Created by zhenghui on 15/4/7.
 */
public class ImageLoadEngine {

    private static final String TAG = "ImageLoadEngine";

    public final static int TYPE_NORMAL = 0;
    public final static int TYPE_DJANGO = 1;
    public final static int TYPE_DATA = 2;
    public final static int TYPE_ORIGINAL= 3;
    public final static int TYPE_ASSET = 4;

    private ExecutorService upExecutor;
    private ExecutorService commonExecutor;
    private volatile static ImageLoadEngine instance;
    private BitmapCacheLoader cacheLoader;

    private final AtomicBoolean paused = new AtomicBoolean(false);
    private final Object pauseLock = new Object();

    protected ImageLoadEngine(){

    }

    /** Returns singleton class instance */
    public static ImageLoadEngine getInstance() {
        if (instance == null) {
            synchronized (ImageLoadEngine.class) {
                if (instance == null) {
//                    long start = System.currentTimeMillis();
                    instance = new ImageLoadEngine();
                    instance.init();
//                    Logger.P("MultimediaImageServiceImpl", "ImageLoadEngine getInstance cost: " + (System.currentTimeMillis() - start));
                }
            }
        }
        return instance;
    }

    public boolean hasInitCacheLoader() {
        return cacheLoader != null;
    }

    public BitmapCacheLoader getCacheLoader() {
        if (cacheLoader == null) {
            synchronized (this) {
                if (cacheLoader == null) {
                    cacheLoader = DefaultConfigurationFactory.createCacheLoader();
                }
            }
        }
        return cacheLoader;
    }

    public synchronized void init() {
//        long start = System.currentTimeMillis();
//        cacheLoader = DefaultConfigurationFactory.createCacheLoader();
        upExecutor = DefaultConfigurationFactory.createUpExecutor();//实际就是Executors.newSingleThreadExecutor();
        commonExecutor = DefaultConfigurationFactory.commonExecutor();
//        Logger.P("MultimediaImageServiceImpl", "ImageLoadEngine getInstance init cost: " + (System.currentTimeMillis() - start));
    }

    public Future submit(ImageTask task) {
        return commonExecutor.submit(task);
    }


//    public Future submit(final ImageLoadHandler loadTask){
//        return commonExecutor.submit(loadTask);
//    }

    public Future submit(final ImageUpHandler upTask){
        return upExecutor.submit(upTask);
    }
    public Future submit(Runnable runnable) {
        return commonExecutor.submit(runnable);
    }

    public void resume() {
        paused.set(false);
        synchronized (pauseLock) {
            pauseLock.notifyAll();
        }
    }

    public void pause() {
        paused.set(true);
    }

    public AtomicBoolean getPause() {
        return paused;
    }

    public Object getPauseLock() {
        return pauseLock;
    }

}
