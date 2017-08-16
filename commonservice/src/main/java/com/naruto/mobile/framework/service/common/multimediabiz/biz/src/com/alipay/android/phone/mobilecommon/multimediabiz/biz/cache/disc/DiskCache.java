/*******************************************************************************
 * Copyright 2014 Sergey Tarasevich
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.disc;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Interface for disk cache
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.9.2
 */
public interface DiskCache<E> {

    String PREF_NAME = "pref_disk_cache";

    /**
     * Returns root directory of disk cache
     *
     * @return Root directory of disk cache
     */
    File getDirectory();

    /**
     * Returns value of cached object
     *
     * @param key
     * @return the cached object or <b>null</b> if the object associated with the key wasn't cached
     */
    E get(String key);

    byte[] getRawData(String key) throws IOException;

    void setDiskCacheHandler(DiskCacheHandler<E> handler);

    DiskCacheHandler<E> getDiskCacheHandler();

    /**
     * Saves object in disk cache.
     *
     * @param key
     * @param value
     * @return <b>true</b> - if object was saved successfully; <b>false</b> - if object wasn't saved in disk cache.
     * @throws IOException
     */
    boolean save(String key, E value) throws IOException;

    boolean save(String key, byte[] data) throws IOException;

    boolean save(String key, InputStream in) throws IOException;

    boolean copy(String srcKey, String dstKey) throws IOException;

    /**
     * 获取Cache Key关联的CacheKey
     * @param sourceCacheKey
     * @return      存在则返回关联的CacheKey，否则返回null
     */
    String getRefCacheKey(String sourceCacheKey);

    /**
     * 获取关联路径
     * @param sourcePath
     * @return
     */
    String getRefPath(String sourcePath);

    boolean rename(String oldKey, String newKey) throws IOException;

    /**
     * Removes cache file associated with incoming key
     *
     * @param key cache key
     * @return <b>true</b> - if cache file is deleted successfully; <b>false</b> - if cache file doesn't exist for
     * incoming key or cache file can't be deleted.
     */
    boolean remove(String key);

    /**
     * Closes disk cache, releases resources.
     */
    void close();

    /**
     * Clears disk cache.
     */
    void clear();

    File getFile(String key);
}
