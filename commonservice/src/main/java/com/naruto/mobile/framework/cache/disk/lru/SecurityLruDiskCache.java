package com.naruto.mobile.framework.cache.disk.lru;

import android.os.StatFs;

import com.naruto.mobile.framework.rpc.myhttp.common.info.AppInfo;


/**
 * 安全 LRU算法的磁盘缓存
 * 
 * 存在应用特定的缓存目录(/data/data/xxxxx/cache)
 * 
 */
public class SecurityLruDiskCache extends LruDiskCache {
    private static SecurityLruDiskCache INSTANCE;

    private SecurityLruDiskCache() {
        super();
    }

    public static synchronized SecurityLruDiskCache getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SecurityLruDiskCache();
        }
        return INSTANCE;
    }

    @Override
    protected void init() {
        super.init();
        load();
    }

    private void load() {
        String path = AppInfo.getInstance().getCacheDirPath();
        StatFs statFs = new StatFs(path);
        long size = statFs.getBlockSize() * ((long) statFs.getAvailableBlocks());
        setDirectory(path);

        long canUseSize = size - (512 * 1024);
        setMaxsize(canUseSize > 0 ? canUseSize : 512 * 1024);//留512K的空间，防止达到峰值
    }

}
