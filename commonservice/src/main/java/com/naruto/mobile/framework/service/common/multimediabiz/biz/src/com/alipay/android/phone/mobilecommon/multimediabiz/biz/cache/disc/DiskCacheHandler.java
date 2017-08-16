package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.disc;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * 考虑不同对象存储，分离存储实现
 * Created by jinmin on 15/4/8.
 */
public interface DiskCacheHandler<E> {
    boolean saveDisk(File dst, String key, E value) throws IOException;
    E loadDisk(File input) throws IOException;
}
