/*******************************************************************************
 * Copyright 2011-2014 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.assist;

import android.text.TextUtils;
import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.BitmapCacheLoader;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Logger;

/**
 *
 */
public class DefaultConfigurationFactory {
    // -------------------线程池设置-----------------
    private static final int CORE_SIZE = 4;//Runtime.getRuntime().availableProcessors();
    private static final int POOL_SIZE = 50;
    private static final int COMMON_POOL_SIZE = 10;

    private static ExecutorService sCommonExecutor = createLoadExecutor("img_cm", false, COMMON_POOL_SIZE); //Executors.newCachedThreadPool();


    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static ExecutorService createLoadExecutor(String name, boolean lifo, int maximumPoolSize) {
        // cpu个数-》运行线程数量
        int coreSize = getCoreSize(CORE_SIZE);

        BlockingDeque<Runnable> taskQueue = lifo ? new LIFOLinkedBlockingDeque<Runnable>() :
                                                    new LinkedBlockingDeque<Runnable>();
        return new ThreadPoolExecutor(coreSize, maximumPoolSize, 60L, TimeUnit.SECONDS, taskQueue,
                TextUtils.isEmpty(name) ? new DefaultThreadFactory() : new DefaultThreadFactory(name));
    }

    public static ExecutorService createLoadExecutor(String name, boolean lifo) {
        return createLoadExecutor(name, lifo, POOL_SIZE);
    }

    public static BitmapCacheLoader createCacheLoader(){
        return new BitmapCacheLoader();
    }

    public static ExecutorService createUpExecutor() {
        return Executors.newSingleThreadExecutor();
    }

    public static ExecutorService commonExecutor() {
        return sCommonExecutor;
    }

    public static ExecutorService createPreLoadExecutor() {
        // cpu个数-》运行线程数量
        int coreSize = getCoreSize(2);

        Logger.D("DefaultCfgFactory","coreSize="+coreSize);
        BlockingDeque<Runnable> taskQueue = new LIFOLinkedBlockingDeque<Runnable>();

        return new ThreadPoolExecutor(coreSize, coreSize, 60L, TimeUnit.SECONDS, taskQueue,
                new DefaultThreadFactory("image-pre-"));

    }

    private static int getCoreSize(int max) {
        int coreSize = Runtime.getRuntime().availableProcessors() - 1;

        if(coreSize > max){
            coreSize = max;
        }else if(coreSize <= 0){
            coreSize = 1;
        }
        return coreSize;
    }

    private static class DefaultThreadFactory implements ThreadFactory {

        private static final AtomicInteger poolNumber = new AtomicInteger(1);

//        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;
        private final int threadPriority;

        DefaultThreadFactory() {
            this(Thread.NORM_PRIORITY-2, "image-");
        }

        DefaultThreadFactory(String threadName) {
            this(Thread.NORM_PRIORITY-2, threadName);
        }

        DefaultThreadFactory(int threadPriority, String threadNamePrefix) {
            this.threadPriority = threadPriority;
            namePrefix = threadNamePrefix + "-" + poolNumber.getAndIncrement() + "-t-";
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, namePrefix + threadNumber.getAndIncrement());
            if (t.isDaemon()) t.setDaemon(false);
            t.setPriority(threadPriority);
            return t;
        }
    }


    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    static class LIFOLinkedBlockingDeque<T> extends LinkedBlockingDeque<T> {

        private static final long serialVersionUID = -4114786347960826192L;
        @Override
        public boolean offer(T e) {
            return super.offerFirst(e);
        }

        @Override
        public T remove() {
            return super.removeFirst();
        }
    }

}
