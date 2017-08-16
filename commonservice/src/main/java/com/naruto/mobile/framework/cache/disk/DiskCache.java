package com.naruto.mobile.framework.cache.disk;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import com.naruto.mobile.base.log.logging.LogCatLog;
import com.naruto.mobile.base.log.logging.PerformanceLog;
import com.naruto.mobile.framework.rpc.myhttp.utils.SerialExecutor;

/**
 * 磁盘缓存
 *
 * 异步存储的，所以在put之后马上去get是取不出来的 使用标准http header描述来做缓存： 缓存的标识：url 缓存周期：
 *
 * 权限？按用户？清除 list形的数据的添加 强制刷新？
 *
 * 支持直接存取Serializable 对象
 */
public abstract class DiskCache {

    /**
     * 缓存的目录
     */
    protected String mDirectory;

    /**
     * 缓存的最大空间
     */
    protected long mMaxsize;

    /**
     * 缓存的大小
     */
    protected long mSize;

    /**
     * 存储映射
     */
    protected HashMap<String, Entity> mEntities;

    /**
     * 缓存分组
     */
    protected HashMap<String, Set<Entity>> mGroup;

    /**
     * 元数据
     */
    protected Meta mMeta;

    /**
     * 顺序执行器
     */
    private SerialExecutor mSerialExecutor;

    /**
     * Meta是否处理中
     */
    private AtomicBoolean mMetaProcessing = new AtomicBoolean();

    /**
     * 是否初始化
     */
    private AtomicBoolean mInited = new AtomicBoolean();

    protected DiskCache() {
        mSerialExecutor = new SerialExecutor("DiskCache");
        mMetaProcessing.set(false);
        mInited.set(false);
    }

    /**
     * 打开缓存
     */
    public void open() {
        if (mInited.get()) {// 已经初始化了
            LogCatLog.w("DiskCache", "DiskCache has inited");
            return;
        }
        PerformanceLog.getInstance().log("diskCache start open.");
        init();// 初始化
        mMeta = new Meta(this);
        mMeta.init();
        mInited.set(true);
        PerformanceLog.getInstance().log("diskCache finish open.");
    }

    /**
     * 关闭缓存
     */
    public void close() {
        scheduleMeta();
    }

    /**
     * 把Serializable对象转成byte［］， 然后调用 put(String owner, String group, final String url, final byte[] data,long
     * createTime, long period, String contentType)
     */
    public void putSerializable(String owner, String group, final String url, final Serializable serializable,
            long createTime, long period, String contentType) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(bos);
            oos.writeObject(serializable);
            byte[] objBytes = bos.toByteArray();
            this.put(owner, group, url, objBytes, createTime, period, contentType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                bos.close();
                if (oos != null) {
                    oos.close();
                }
            } catch (IOException e) {
                // can not do anything --haitong
                LogCatLog.e("DiskCache", e + "");
            }
        }
    }

    /**
     * 添加缓存数据
     *
     * @param owner 所用者：g+gid或者u+uid,比如:u10000023213。如果与所有者无关可以传null。不能为-。
     * @param group 缓存对象所在组,null:只放到缓存中,not null:以group为key将该对象归组,不能为-
     * @param url   数据的url
     * @param data  数据内容
     */
    public void put(String owner, String group, final String url, final byte[] data,
            long createTime, long period, String contentType) {
        if (!mInited.get()) {
            throw new RuntimeException("DiskCache must call open() before");
        }
        if (owner != null && owner.equalsIgnoreCase("-")) {
            throw new RuntimeException("owner can't be -");
        }
        if (group != null && group.equalsIgnoreCase("-")) {
            throw new RuntimeException("group can't be -");
        }
        PerformanceLog.getInstance().log("diskCache start put:" + url);
        final String key = obtainKey(url);
        Entity entity = new Entity(owner, group, url, 0, data.length, key, createTime, period,
                contentType);
        addEntity(entity);
        mSerialExecutor.execute(new Runnable() {
            @Override
            public void run() {
                String path = getDirectory() + File.separatorChar + key;
                try {
                    writeFile(path, data);
                    scheduleMeta();
                } catch (CacheException e) {// 添加失败
                    LogCatLog.e("DiskCache", "fail to put cache:" + e);
                } finally {
                    PerformanceLog.getInstance().log("diskCache finish put:" + url);
                }
            }
        });
    }

    /**
     * 添加到组
     *
     * @param entity 实体
     */
    private void addGroup(Entity entity) {
        String group = entity.getGroup();
        if (group != null && !group.equalsIgnoreCase("-")) {
            Set<Entity> entitys = mGroup.get(group);
            if (entitys == null) {
                entitys = new HashSet<Entity>();
                mGroup.put(group, entitys);
            }
            entitys.add(entity);
        }
    }

    /**
     * 移除缓存数据
     *
     * @param url 数据的url
     */
    public void remove(String url) {
        if (!mInited.get()) {
            throw new RuntimeException("DiskCache must call open() before");
        }
        PerformanceLog.getInstance().log("diskCache start remove:" + url);
        removeLocalEntity(url);
    }

    /**
     * 根据指定组删除缓存对象
     *
     * @param group 指定组，不能为-
     */
    public void removeByGroup(String group) {
        if (!mInited.get()) {
            throw new RuntimeException("DiskCache must call open() before");
        }
        // 删除指定group缓存数据
        if (group != null) {
            if (group.equalsIgnoreCase("-")) {
                throw new RuntimeException("group can't be -");
            }
            Set<Entity> entities = mGroup.get(group);
            if (entities != null) {
                Set<String> urls = new HashSet<String>();
                //FIXME 待优化
                for (Entity entity : entities) {
                    urls.add(entity.getUrl());
                }
                for (String url : urls) {
                    removeLocalEntity(url);
                    PerformanceLog.getInstance().log(
                            "diskCache start remove group:" + group + " url :[" + url + "]");
                }
            }
        }
    }

    /**
     * 删除本地缓存数据
     *
     * @param url 数据url
     */
    private void removeLocalEntity(String url) {
        if (url != null) {
            removeEntity(url);
            removeCacheFile(url);
        }
    }

    /**
     * 移除组中特定缓存对象
     *
     * @param entity 实体
     * @param group  组
     */
    private void removeGroup(Entity entity) {
        String group = entity.getGroup();
        if (group != null && !group.equalsIgnoreCase("-")) {
            Set<Entity> entitys = mGroup.get(group);
            if (entitys != null) {
                entitys.remove(entity);
            }
        }
    }

    protected void removeCacheFile(final String url) {
        final String key = obtainKey(url);
        mSerialExecutor.execute(new Runnable() {

            @Override
            public void run() {
                String path = getDirectory() + File.separatorChar + key;
                File file = new File(path);
                if (!file.exists()) {
                    return;
                }
                boolean ret = file.delete();
                if (!ret) {// 删除失败
                    LogCatLog.e("DiskCache", "fail to remove cache file");
                }
                scheduleMeta();
                PerformanceLog.getInstance().log("diskCache finish remove:" + url);
            }
        });
    }

    /**
     * 如果得到的byte[]为null，会返回 null 对象
     */
    public Serializable getSerializable(String owner, String url) throws CacheException {
        byte[] objBytes = get(owner, url);
        if (objBytes == null) {
            return null;
        }
        InputStream bis = new ByteArrayInputStream(objBytes);
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(bis);
            return (Serializable) ois.readObject();
        } catch (StreamCorruptedException e) {
            throw new CacheException(e.getMessage());
        } catch (IOException e) {
            throw new CacheException(e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new CacheException(e.getMessage());
        } finally {
            try {
                bis.close();
                if (ois != null) {
                    ois.close();
                }
            } catch (IOException e) {
                // can not do anything --haitong
                LogCatLog.e("DiskCache", e + "");
            }
        }

    }

    /**
     * 获取缓存数据
     *
     * @param owner 所用者：g+gid|u+uid,或者其一，比如:u10000023213。如果与所有者无关可以传null。不能为-。
     * @param url   数据的url
     */
    public byte[] get(String owner, String url) throws CacheException {
        if (!mInited.get()) {
            throw new RuntimeException("DiskCache must call open() before");
        }
        if (owner != null && owner.equalsIgnoreCase("-")) {
            throw new RuntimeException("owner can't be -");
        }
        PerformanceLog.getInstance().log("diskCache start get:" + url);
        // 不在缓存中
        if (!containEntity(url)) {
            return null;
        }
        Entity entity = getEntity(url);
        // 过期
        if (entity.expire()) {
            remove(url);
            return null;
        }
        // 不属于该用户
        if (!entity.authenticate(owner)) {
            return null;
        }
        entity.use();// 增加使用次数
        String key = obtainKey(entity.getUrl());
        String path = getDirectory() + File.separatorChar + key;
        byte[] data = readFile(path);
        PerformanceLog.getInstance().log("diskCache finish get:" + url);
        return data;
    }

    /**
     * @return 缓存大小限制
     */
    public long getMaxsize() {
        return mMaxsize;
    }

    /**
     * @return 当前已占用的大小
     */
    public long getSize() {
        synchronized (mEntities) {
            return mSize;
        }
    }

    /**
     * 获取缓存对象个数
     *
     * @hide
     */
    public int getCacheCount() {
        return mEntities.size();
    }

    /**
     * @param directory 缓存目录
     */
    protected final void setDirectory(String directory) {
        mDirectory = directory;
        if (mDirectory == null) {
            throw new IllegalArgumentException("Not set valid cache directory.");
        }
        File file = new File(mDirectory);
        if (!file.exists() && !file.mkdir()) {
            throw new IllegalArgumentException("An Error occured while  cache directory.");
        } else if (!file.isDirectory()) {
            throw new IllegalArgumentException("Not set valid cache directory.");
        }
    }

    /**
     * @param maxsize 缓存新的大小
     */
    protected final void setMaxsize(long maxsize) {
        mMaxsize = maxsize;
        if (mMaxsize <= 0) {
            throw new IllegalArgumentException("Not set valid cache size.");
        }
    }

    /**
     * @return 缓存目录
     */
    public String getDirectory() {
        return mDirectory;
    }

    /**
     * 添加缓存实体
     *
     * @param entity 缓存实体
     */
    void addEntity(Entity entity) {
        synchronized (mEntities) {
            mEntities.put(entity.getUrl(), entity);
            addGroup(entity);
            mSize += entity.getSize();
        }
    }

    /**
     * 移除实体
     *
     * @param url 实体的url
     */
    void removeEntity(String url) {
        synchronized (mEntities) {
            Entity entity = mEntities.get(url);
            if (entity != null) {
                mEntities.remove(url);
                removeGroup(entity);
                mSize -= entity.getSize();
            }
        }
    }

    /**
     * 是否存在实体
     *
     * @param url 实体的url
     */
    boolean containEntity(String url) {
        synchronized (mEntities) {
            return mEntities.containsKey(url);
        }
    }

    /**
     * 获取实体
     *
     * @param url 实体的url
     */
    Entity getEntity(String url) {
        synchronized (mEntities) {
            return mEntities.get(url);
        }
    }

    /**
     * 清除缓存
     */
    void clear() {
        mSerialExecutor.execute(new Runnable() {

            @Override
            public void run() {
                File file = new File(getDirectory());
                if (file.exists() && file.isDirectory()) {
                    File[] files = file.listFiles();
                    if (files == null) {
                        return;
                    }
                    for (File f : files) {
                        f.delete();
                    }
                }
            }
        });
    }

    protected String obtainKey(String url) {
        return Integer.toHexString(url.hashCode());
    }

    private byte[] readFile(String path) throws CacheException {
        File file = new File(path);
        FileInputStream inputStream = null;
        if (!file.exists()) {
            throw new CacheException(CacheException.ErrorCode.READ_IO_ERROR,
                    "cache file not found.");
        }
        try {
            inputStream = new FileInputStream(file);
            byte[] data = new byte[inputStream.available()];
            inputStream.read(data);
            return data;
        } catch (IOException e) {
            throw new CacheException(CacheException.ErrorCode.READ_IO_ERROR, e == null ? ""
                    : e.getMessage());
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    LogCatLog.e("DiskCache", e + "");
                }
            }
        }
    }

    private void writeFile(String path, byte[] data) throws CacheException {
        File file = new File(path);
        FileOutputStream outputStream = null;
        try {
            if (!file.exists() && !file.createNewFile()) {// not found
                throw new CacheException(CacheException.ErrorCode.WRITE_IO_ERROR,
                        "cache file create error.");
            }
            outputStream = new FileOutputStream(file);
            outputStream.write(data);
            outputStream.flush();
        } catch (FileNotFoundException e) {
            throw new CacheException(CacheException.ErrorCode.WRITE_IO_ERROR, e == null ? ""
                    : e.getMessage());
        } catch (IOException e) {
            throw new CacheException(CacheException.ErrorCode.WRITE_IO_ERROR, e == null ? ""
                    : e.getMessage());
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    LogCatLog.e("DiskCache", e + "");
                }
            }
        }
    }

    private void scheduleMeta() {
        if (mMetaProcessing.get()) {
            return;
        }
        PerformanceLog.getInstance().log("diskCache start write meta.");
        mMetaProcessing.set(true);// 调度存储
        mSerialExecutor.execute(new Runnable() {

            @Override
            public void run() {
                // 存储meta
                synchronized (mEntities) {
                    mMeta.writeMeta(mEntities);
                }
                mMetaProcessing.set(false);// 存储调度执行完毕
                PerformanceLog.getInstance().log("diskCache finish write meta.");
            }
        });
    }

    /**
     * 初始化
     */
    protected abstract void init();
}
