package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.disc.impl;

import android.graphics.Bitmap;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.disc.naming.FileNameGenerator;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.disc.naming.Md5FileNameGenerator;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.io.IOUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.ImageUtils;

/**
 * 带有一级映射缓存的DiskCache
 * Created by jinmin on 15/5/10.
 */
public class BitmapDiskLruCache extends BaseDiskCache<Bitmap> {

    private final static int MAX_CACHE_SIZE = 100 * 1024 * 1024;

    private final static int APP_VERSION = 1;

    private final static int VALUE_COUNT = 1;

    private DiskLruCache mDiskLruCache;
    private FileNameGenerator mFileNameGenerator;

    public BitmapDiskLruCache(File baseDir) throws IOException {
        super();
        this.mFileNameGenerator = new Md5FileNameGenerator();
        this.mDiskLruCache = DiskLruCache.open(baseDir, APP_VERSION, VALUE_COUNT, MAX_CACHE_SIZE);
    }

    @Override
    public File getDirectory() {
        return mDiskLruCache.getDirectory();
    }

    @Override
    public Bitmap get(String key) {
        Bitmap value = null;
        if (mDiskLruCache != null && checkParam(key)) {
            //先从L1Cache查找映射关系
//            String oldKey = mL1Cache.get(key);
//            if (oldKey != null) key = oldKey;
            //继续本地查找
            File file = getFile(key);
            value = ImageUtils.decodeBitmapByFalcon(file);
        }
        return value;
    }

    @Override
    public boolean save(String key, Bitmap value) throws IOException {
        if (mDiskLruCache != null && checkParam(key, value)) {
            File file = getFile(key, false);
            String cacheKey = getCacheKey(key);
            DiskLruCache.Editor editor = mDiskLruCache.edit(cacheKey);
            OutputStream os = editor.newOutputStream(0);
            boolean ret = ImageUtils.compressBitmap(value, os);
            if (ret) {
                editor.commit();
            } else {
                editor.abort();
            }
            if (ret) file = getFile(key, false);
//            logger.d("saveFile key-val key: " + key + ", file: " + file + ", ret: " + ret);
            if (ret) {
//                file = getFile(key);
                addDbRecord(key, file);
            }
            return ret;
        }
        return false;
    }

    @Override
    public boolean save(String key, InputStream in) throws IOException {
        boolean ret = false;
        if (checkParam(key, in)) {
            File file = getFile(key, false);
            int copyCount = 0;
            OutputStream out = null;
            try {
                DiskLruCache.Editor editor = mDiskLruCache.edit(getCacheKey(key));
                out = editor.newOutputStream(0);
                copyCount = IOUtils.copy(in, out);
                ret = copyCount > 0;
                if (ret) {
                    editor.commit();
                } else {
                    editor.abort();
                }
                if (ret) file = getFile(key, false);
//                logger.d("saveFile key-in key: " + key + ", file: " + file + ", len: " + (file==null?0:file.length()) + ", ret: " + ret);
                if (ret) {
//                    file = getFile(key);
                    addDbRecord(key, file);
                }
            } catch (Exception e) {
//                logger.e(e, "saveData error");
            } finally {
                IOUtils.closeQuietly(in);
                IOUtils.closeQuietly(out);
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
        logger.d("remove key: " + key);
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
        if (mDiskLruCache != null && !mDiskLruCache.isClosed()) {
            try {
                mDiskLruCache.close();
            } catch (Exception e) {
                logger.e(e, "close error");
            }
        }
    }

    @Override
    public void clear() {

    }

    @Override
    public File getFile(String key) {
        return getFile(key, true);
    }

    @Override
    protected File getFile(String key, boolean withRef) {
        File file = null;
        if (mFileNameGenerator != null) {
            //判断是否要检查依赖关系
            if (withRef) {
                String refKey = getRefCacheKey(key);
                if (!TextUtils.isEmpty(refKey)) {
                    key = refKey;
                }
                logger.d("getFile key: " + key + ", refKey: " + refKey);
            }
            //file = new File(mBaseDir, mFileNameGenerator.generate(key));
            DiskLruCache.Snapshot snapshot = null;
            try {
                snapshot = mDiskLruCache.get(getCacheKey(key));
                if (snapshot != null) {
                    file = snapshot.getFile(0);
                }
            } catch (Exception e) {
                logger.e(e, "getFile error");
            } finally {
                IOUtils.closeQuietly(snapshot);
            }
        }
        return file;
    }

    private String getCacheKey(String input) {
        String key = input;
        if (mFileNameGenerator != null) {
            key = mFileNameGenerator.generate(input);
        }
        return key;
    }
}
