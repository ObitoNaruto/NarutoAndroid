package com.naruto.mobile.framework.cache.disk;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import com.naruto.mobile.base.log.logging.LogCatLog;

/**
 * @hide
 * 
 * 磁盘缓存元数据
 * 
 * 字段的扩展性？
 * 缓存元数据文件：
 * magic
 * version
 * maxsize
 * 
 * owner group  url   used time   size   relative path   create time   period   content Type
 * 
 * @author sanping.li@alipay.com
 *
 */
public class Meta {
    private static final String META_MAGIC = "alipay.diskcache";
    private static final int META_VERSION = 1;

    private static final byte LF = '\n';
    private static final String SEPARATE = "   ";//3个空格，防止和url中的字符冲突

    private DiskCache mDiskCache;

    private String mPath;

    public Meta(DiskCache diskCache) {
        mDiskCache = diskCache;
        mPath = mDiskCache.getDirectory() + File.separator + "_meta";
    }

    void init() {
        File file = new File(mPath);
        if (!file.exists()) {
            return;
        }

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String magic = reader.readLine();
            int version = Integer.parseInt(reader.readLine());
            long maxsize = Long.parseLong(reader.readLine());
            String blank = reader.readLine();

            if (!magic.equals(META_MAGIC) || blank.length() == 0) {
                throw new IOException("Unexpected cache meta file: [" + magic + ", " + version
                                      + ", " + maxsize + ", " + blank + "]");
            } else if (version > META_VERSION) {
                throw new IOException("Unexpected meta file version:" + version);
            }

            String line = null;
            while ((line = reader.readLine()) != null) {
                readMetaLine(line);
            }
        } catch (Exception exception) {
            file.delete();
            mDiskCache.clear();
            LogCatLog.e("DiskCache", exception == null ? "" : exception.getMessage());
//            throw new RuntimeException(exception);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                    reader = null;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void readMetaLine(String line) throws IOException {
        String[] parts = line.split(SEPARATE);
        if (parts.length < 9) {
            throw new IOException("unexpected meta line: " + line);
        }

        String owner = parts[0];
        String group = parts[1];
        String url = parts[2];
        int usedTime = Integer.parseInt(parts[3]);
        long size = Long.parseLong(parts[4]);
        String path = parts[5];
        long createTime = Long.parseLong(parts[6]);
        long period = Long.parseLong(parts[7]);
        String contentType = parts[8];

        Entity entity = new Entity(owner, group, url, usedTime, size, path, createTime, period,
            contentType);

        mDiskCache.addEntity(entity);
    }

    /**
     * 持久化到Meta文件
     * @param metaMap
     */
    void writeMeta(HashMap<String, Entity> metaMap) {
        File file = new File(mPath);
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(file));
            bufferedWriter.write(META_MAGIC);
            bufferedWriter.write(LF);
            bufferedWriter.write(String.valueOf(META_VERSION));
            bufferedWriter.write(LF);
            bufferedWriter.write(String.valueOf(mDiskCache.getMaxsize()));
            bufferedWriter.write(LF);
            bufferedWriter.write(' ');
            bufferedWriter.write(LF);

            Iterator<Entity> iterator = metaMap.values().iterator();
            Entity entity = null;
            while (iterator.hasNext()) {
                entity = iterator.next();
                bufferedWriter.write(entity.getOwner());
                bufferedWriter.write(SEPARATE);
                bufferedWriter.write(entity.getGroup());
                bufferedWriter.write(SEPARATE);
                bufferedWriter.write(entity.getUrl());
                bufferedWriter.write(SEPARATE);
                bufferedWriter.write(String.valueOf(entity.getUsedTime()));
                bufferedWriter.write(SEPARATE);
                bufferedWriter.write(String.valueOf(entity.getSize()));
                bufferedWriter.write(SEPARATE);
                bufferedWriter.write(entity.getPath());
                bufferedWriter.write(SEPARATE);
                bufferedWriter.write(String.valueOf(entity.getCreateTime()));
                bufferedWriter.write(SEPARATE);
                bufferedWriter.write(String.valueOf(entity.getPeriod()));
                bufferedWriter.write(SEPARATE);
                bufferedWriter.write(entity.getContentType());
                bufferedWriter.write(LF);
            }
            bufferedWriter.flush();
        } catch (IOException exception) {
            LogCatLog.e("Meta",
                "fail to write meta file:" + exception == null ? "" : exception.getMessage());
            mDiskCache.clear();
        } finally {
            try {
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                    bufferedWriter = null;
                }
            } catch (IOException e) {
                LogCatLog.e("Meta", e+"");
            }
        }

    }
}
