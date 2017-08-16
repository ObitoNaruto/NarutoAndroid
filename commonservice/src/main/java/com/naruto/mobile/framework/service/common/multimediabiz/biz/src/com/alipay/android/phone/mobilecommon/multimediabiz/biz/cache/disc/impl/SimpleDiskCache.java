package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.disc.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import java.io.*;
import java.lang.ref.WeakReference;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.disc.DiskCacheHandler;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.disc.naming.FileNameGenerator;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.AppUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.FileUtils;

/**
 * 带有一级映射缓存的DiskCache
 * Created by jinmin on 15/5/10.
 */
public class SimpleDiskCache<E> extends BaseDiskCache<E> {

    private File mBaseDir;
    private DiskCacheHandler<E> mDiskCacheHandler;
    private FileNameGenerator mFileNameGenerator;


    public SimpleDiskCache(File baseDir, DiskCacheHandler<E> handler, FileNameGenerator nameGenerator) {
        super();
        this.mBaseDir = baseDir;
        this.mDiskCacheHandler = handler;
        this.mFileNameGenerator = nameGenerator;
        this.mContext = AppUtils.getApplicationContext();

    }

    @Override
    public File getDirectory() {
        return mBaseDir;
    }

    @Override
    public E get(String key) {
        E value = null;
        if (mDiskCacheHandler != null && checkParam(key)) {
            //先从L1Cache查找映射关系
//            String oldKey = mL1Cache.get(key);
//            if (oldKey != null) key = oldKey;
            //继续本地查找
            File file = getFile(key);
            if (checkFileExists(file)) {
                try {
                    value = mDiskCacheHandler.loadDisk(file);
                } catch (Exception e) {
                    logger.e(e, "getCache error");
                }
            }
        }
        return value;
    }

    @Override
    public void setDiskCacheHandler(DiskCacheHandler<E> handler) {
        this.mDiskCacheHandler = handler;
    }

    @Override
    public DiskCacheHandler<E> getDiskCacheHandler() {
        return mDiskCacheHandler;
    }

    @Override
    public boolean save(String key, E value) throws IOException {
        if (mDiskCacheHandler != null && checkParam(key, value)) {
            File file = getFile(key, false);
            if (file != null) {
                boolean ret = mDiskCacheHandler.saveDisk(file, key, value);
                if (ret) {
                    addDbRecord(key, file);
                }
                return ret;
            }
        }
        return false;
    }


    @Override
    public boolean save(String key, InputStream in) throws IOException {
        boolean ret = false;
        if (checkParam(key, in)) {
            File file = getFile(key, false);
            ret = FileUtils.safeCopyToFile(in, file);
            if (ret) {
                addDbRecord(key, file);
            }
        }
        return ret;
    }

    @Override
    public boolean rename(String oldKey, String newKey) throws IOException {
        if (checkParam(oldKey, newKey)) {
            File oldFile = getFile(oldKey);
            File newFile = getFile(newKey);
            if (checkFileExists(oldFile) && (!checkFileExists(newFile) || newFile.delete())) {
                return oldFile.renameTo(newFile);
            }
        }
        return false;
    }

    @Override
    public boolean remove(String key) {
        if (checkParam(key)) {
            removeTaskRecord(key);
            submitDelayDeleteTask(key);
            deleteDbRecord(key);
            return true;
        }
        return false;
    }

    @Override
    public void close() {

    }

    @Override
    public void clear() {

    }

    @Override
    public File getFile(String key) {
        return getFile(key, true);
    }

    protected File getFile(String key, boolean withRef) {
        File file = null;
        if (mFileNameGenerator != null) {
            //判断是否要检查依赖关系
            if (withRef) {
                String refKey = getRefCacheKey(key);
                if (!TextUtils.isEmpty(refKey)) {
                    key = refKey;
                }
                logger.p("getFile key: " + key + ", refKey: " + refKey);
            }
            FileUtils.mkdirs(mBaseDir);
            file = new File(mBaseDir, mFileNameGenerator.generate(key));
        }
        return file;
    }
}
