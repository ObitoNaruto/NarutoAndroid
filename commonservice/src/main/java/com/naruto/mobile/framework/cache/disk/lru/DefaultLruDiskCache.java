package com.naruto.mobile.framework.cache.disk.lru;

import android.os.StatFs;

import com.naruto.mobile.framework.rpc.myhttp.common.info.AppInfo;
import com.naruto.mobile.framework.rpc.myhttp.common.info.DeviceInfo;

/**
 * Default LRU算法的磁盘缓存
 * 
 * 有外部存储(SD卡)，存外部存储，没有则存在应用特定的缓存目录(/data/data/xxxxx/cache)
 * 
 */
public class DefaultLruDiskCache extends LruDiskCache {
    private static DefaultLruDiskCache INSTANCE;

    private DefaultLruDiskCache() {
        super();
    }

    public static synchronized DefaultLruDiskCache getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DefaultLruDiskCache();
        }
        return INSTANCE;
    }

    @Override
    protected void init() {
        super.init();
        load();
    }

    private void load() {
        String path = DeviceInfo.getInstance().getExternalStoragePath("cache");
        if (path == null) {
            path = AppInfo.getInstance().getCacheDirPath();
        }
        StatFs statFs = new StatFs(path);
        long size = statFs.getBlockSize() * ((long) statFs.getAvailableBlocks());
        setDirectory(path);
        long canUseSize = size - (512 * 1024);
        setMaxsize(canUseSize > 0 ? canUseSize : 512 * 1024);//留512K的空间，防止达到峰值
    }

}
