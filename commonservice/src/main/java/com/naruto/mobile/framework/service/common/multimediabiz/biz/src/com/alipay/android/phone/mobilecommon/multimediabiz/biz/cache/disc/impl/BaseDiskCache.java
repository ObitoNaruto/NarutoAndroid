package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.disc.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;

import java.io.*;
import java.lang.ref.WeakReference;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.disc.DiskCache;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.disc.DiskCacheHandler;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.disc.db.ImageCacheModel;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.disc.db.ImageCachePersistence;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.io.IOUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.AppUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.CacheUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Logger;

/**
 * DiskCache 基类
 * Created by jinmin on 15/7/31.
 */
public abstract class BaseDiskCache <E> implements DiskCache<E> {

    protected Logger logger = Logger.getLogger("DiskCache");

    protected final static int MSG_PREF_EDITOR_COMMIT = 0;

    private final static int DELAY_TIME = 20;
    private final static int FILE_IO_BUFFER_SIZE = 16 * 1024;

    protected Handler mDelayIoHandler;
    protected Context mContext;
    private SharedPreferences mSp;
    protected SharedPreferences.Editor mEditor;

    protected ImageCachePersistence mCachePersistence;

    //具体CacheKey缓存
    protected Map<String, String> mL1Cache = new ConcurrentHashMap<String, String>();
    //路径SharePreference关联Cache，只记录路径对应缓存关系
    protected Map<String, String> mSpCache = new ConcurrentHashMap<String, String>();
    //考虑大多数情况，是没有，新增黑名单，减少sp检索
    protected Set<String> mSpBlackSet = Collections.synchronizedSet(new HashSet<String>());
    protected Map<String, WeakReference<Runnable>> mDelayTaskMap = new ConcurrentHashMap<String, WeakReference<Runnable>>();

    public BaseDiskCache() {
        mContext = AppUtils.getApplicationContext();
        HandlerThread delayCopyHandlerThread = new HandlerThread("delay_io_handler_thread");
        delayCopyHandlerThread.setPriority(Thread.MIN_PRIORITY);
        delayCopyHandlerThread.setDaemon(false);
        delayCopyHandlerThread.start();
        this.mDelayIoHandler = new Handler(delayCopyHandlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_PREF_EDITOR_COMMIT:
                        getEditor().commit();
                        break;
                }
            }
        };
    }

    protected SharedPreferences getSp() {
        if (mSp == null) {
            mSp = mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        }
        return mSp;
    }

    protected SharedPreferences.Editor getEditor() {
        if (mEditor == null) {
            mEditor = getSp().edit();
        }
        return mEditor;
    }

    public ImageCachePersistence getCachePersistence() {
        if (mCachePersistence == null) {
            try {
                mCachePersistence = new ImageCachePersistence(mContext);
            } catch (SQLException e) {
                logger.e(e, "getCachePersistence error");
            }
        }
        return mCachePersistence;
    }

    protected boolean checkParam(String oldKey, String newKey) {
        return !(TextUtils.isEmpty(oldKey) || TextUtils.isEmpty(newKey) || oldKey.equals(newKey));
    }

    protected boolean checkParam(String key, byte[] data) {
        return key != null && data != null && data.length > 0;
    }

    protected boolean checkParam(String key, InputStream in) {
        return key != null && in != null;
    }

    protected boolean checkParam(String key, E data) {
        return key != null && data != null;
    }

    protected boolean checkParam(String key) {
        return key != null;
    }

    protected boolean checkFileExists(File file) {
        return file != null && file.exists() && file.isFile();
    }

    protected void commitEditor() {
        if (mDelayIoHandler.hasMessages(MSG_PREF_EDITOR_COMMIT)) {
            mDelayIoHandler.removeMessages(MSG_PREF_EDITOR_COMMIT);
        }
        mDelayIoHandler.sendEmptyMessageDelayed(MSG_PREF_EDITOR_COMMIT, DELAY_TIME);
    }

    @Override
    public byte[] getRawData(String key) throws IOException {
        byte[] data = null;
        if (checkParam(key)) {
            //先从L1Cache查找映射关系
            String oldKey = mL1Cache.get(key);
            if (oldKey != null) key = oldKey;
            //继续本地查找
            File file = getFile(key);
            if (checkFileExists(file)) {
                FileInputStream fis = null;
                BufferedInputStream bis = null;
                ByteArrayOutputStream baos = null;
                try {
                    fis = new FileInputStream(file);
                    bis = new BufferedInputStream(fis);
                    baos = new ByteArrayOutputStream((int) file.length());
                    int read;
                    byte[] buf = new byte[FILE_IO_BUFFER_SIZE];
                    while ((read = bis.read(buf)) != -1) {
                        baos.write(buf, 0, read);
                    }
                    data = baos.toByteArray();
                } catch (IOException e) {
                    logger.e(e, "getRawData error");
                } finally {
                    IOUtils.closeQuietly(bis);
                    IOUtils.closeQuietly(fis);
                    IOUtils.closeQuietly(baos);
                }
            }
        }
        return data;
    }

    @Override
    public boolean save(String key, byte[] data) throws IOException {
        boolean ret = false;
        if (checkParam(key, data)) {
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            ret = save(key, bais);
        }
        return ret;
    }

    @Override
    public boolean copy(String srcPath, String dstPath) throws IOException {
        boolean ret = false;
        //参数基本判断，不合法则返回false
        if (checkParam(srcPath, dstPath)) {
            //假如L1 cache有对应的映射关系，则没有必要重新处理
            if (!srcPath.equals(mSpCache.get(dstPath))) {
                //添加映射关系
                mSpCache.put(dstPath, srcPath);
                submitDelayCopyTask(srcPath, dstPath);
            }
            ret = true;
        }
        logger.d("copy srcPath: " + srcPath + ", dstPath: " + dstPath + ", ret: " + ret);
        return ret;
    }

    protected void submitDelayCopyTask(String srcPath, String dstPath) {
//        DelayCopyTask task = new DelayCopyTask(srcPath, dstPath);
//        submitTask(dstPath, task);
        //sp 添加映射关系
//        String baseSrcPath = getBasePath(srcPath);
//        String baseDstPath = getBasePath(dstPath);
        getEditor().putString(dstPath, srcPath);
//        addDbRecord(dstPath, getFile(srcPath));
        commitEditor();
        logger.p("submitDelayCopyTask srcPath: " + srcPath + ", dstPath: " + dstPath);
    }

    protected void submitDelayDeleteTask(String key) {
        DelayDeleteTask task = new DelayDeleteTask(key);
        submitTask(key, task);
    }

    protected void submitTask(String key, Runnable task) {
        Runnable old = addDelayTaskRecord(key, task);
        if (old != null) {
            mDelayIoHandler.removeCallbacks(old);
        }
        mDelayIoHandler.post(task);
    }

    protected Runnable addDelayTaskRecord(String key, Runnable task) {
        WeakReference<Runnable> weakTask = new WeakReference<Runnable>(task);
        WeakReference<Runnable> oldTask = mDelayTaskMap.put(key, weakTask);
        return oldTask == null ? null : oldTask.get();
    }

    protected Runnable getTaskRunnable(String key) {
        Runnable task = null;
        if (key != null) {
            if (mDelayTaskMap.containsKey(key)) {
                task = mDelayTaskMap.get(key).get();
            }
        }
        return task;
    }

    protected void removeTaskRecord(String key) {
        Runnable r = getTaskRunnable(key);
        if (r != null) {
            mDelayIoHandler.removeCallbacks(r);
        }
        mDelayTaskMap.remove(key);
    }


    //延迟删除任务
    protected class DelayDeleteTask implements Runnable {
        private String key;

        public DelayDeleteTask(String key) {
            this.key = key;
        }

        @Override
        public void run() {
//            File file = getFile(key);
//            if (checkFileExists(file)) {
//                file.delete();
//            }
//            mDelayTaskMap.remove(key);
            String basePath = getBasePath(key);
            String refKey = null;
//            if (mL1Cache.containsKey(key)) {
//                refKey = mL1Cache.remove(key);
//            }
            if (getSp().contains(basePath)) {
                refKey = getRefCacheKey(key);
//                refKey = getSp().getString(key, refKey);
                getEditor().remove(key);
                getEditor().commit();
                mL1Cache.remove(key);
                mSpCache.remove(basePath);
                mSpBlackSet.add(basePath);
            }
            File file = getFile(refKey, false);
            //判断是否原始文件，并检查是否存在
            if (checkFileExists(file)) {
                Map<String, ?> snapshot = getSp().getAll();
                if (!snapshot.containsValue(basePath)) {//确保没有别的引用着
                    logger.d("DelayDeleteTask del: " + file);
                    file.delete();
                }
            }
        }
    }

    protected void addDbRecord(String key, File file) {
        logger.p("addDbRecord start key: " + key + ", file: " + file + ", len: " + (file==null?-1:file.length()));
        if (CacheUtils.checkCacheFile(file)) {
            addDbRecord(key, file.getAbsolutePath());
        }
    }

    protected void addDbRecord(String key, String path) {
        String[] params = CacheUtils.splitCacheKey(key);
        //logger.d("addDbRecord key: " + key + ", path: " + path + ", params: " + Arrays.toString(params));
        if (params != null && getCachePersistence() != null) {
            ImageCacheModel model = new ImageCacheModel(params[0], params[1], params[2], params[3], params[4], key);
            model.path = path;
            try {
                getCachePersistence().save(model);
                logger.p("addDbRecord save: " + model);
            } catch (SQLException e) {
                logger.p("addDbRecord exception: " + e);
            }
        }
    }

    protected void deleteDbRecord(String key) {
        if (!TextUtils.isEmpty(key) && getCachePersistence() != null) {
            try {
                getCachePersistence().deleteByCacheKey(key);
            } catch (Exception e) {
                logger.p("deleteDbRecord exception: " + e);
            }
        }
    }

    @Override
    public void setDiskCacheHandler(DiskCacheHandler<E> handler) {

    }

    @Override
    public DiskCacheHandler<E> getDiskCacheHandler() {
        return null;
    }

    protected abstract File getFile(String key, boolean ref);

    protected String getBasePath(String key) {
        return CacheUtils.getBaseCachePath(key);
    }

    @Override
    public String getRefCacheKey(String sourceCacheKey) {
        //基本参数检测
        if (sourceCacheKey == null) return null;
        //要是已缓存这关联的CacheKey，直接返回
        if (mL1Cache.containsKey(sourceCacheKey)) return mL1Cache.get(sourceCacheKey);
        String refCacheKey = null;
        String path = getBasePath(sourceCacheKey);
        //通过黑名单减少sp索引
        if (!TextUtils.isEmpty(path) && !mSpBlackSet.contains(path)) {
            String refPath = getRefPath(path);
            if (!TextUtils.isEmpty(refPath)) {
                refCacheKey = sourceCacheKey.replace(path, refPath);
                mL1Cache.put(sourceCacheKey, refCacheKey);
            }
        }
        return refCacheKey;
    }

    @Override
    public String getRefPath(String path) {
        String refPath = null;
        if (!mSpBlackSet.contains(path)) {
            if (mSpCache.containsKey(path)) {
                refPath = mSpCache.get(path);
            } else {
                refPath = getSp().getString(path, null);
                if (!TextUtils.isEmpty(refPath)) {
                    mSpCache.put(path, refPath);
                } else {
                    mSpBlackSet.add(path);
                }
            }
        }
        return refPath;
    }
}
