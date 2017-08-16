package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.content.Context;
import android.text.TextUtils;
import android.text.format.Formatter;
//import com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.ImageCacheContext;
//import com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.io.IOUtils;
//import com.alipay.android.phone.mobilesdk.storage.file.ZExternalFile;
//import com.alipay.android.phone.mobilesdk.storage.file.ZFile;

import java.io.*;

import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.ImageCacheContext;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.io.IOUtils;

/**
 * 文件工具类
 * Created by jinmin on 15/6/13.
 */
public class FileUtils {

    private static final Logger logger = Logger.getLogger("FileUtils");
    private static final String GROUP_ID = "multimedia";
    private static final String ALIPAY_SDCARD_PATH = File.separator + "alipay";// + File.separator + "files";

    public static boolean safeCopyToFile(byte[] data, File dstFile) throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        try {
            return safeCopyToFile(in, dstFile);
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    public static boolean safeCopyToFile(InputStream in, File dstFile) throws IOException {
        if (dstFile == null) {
            return false;
        }
        deleteFile(dstFile);
        File tmpFile = createTmpFile(dstFile);
        boolean copied = copyToFile(in, tmpFile);
        if (copied) {
            deleteFile(dstFile);
            copied = tmpFile.renameTo(dstFile);
            logger.d("safeCopyToFile tmpFile: " + tmpFile + ", dstFile: " + dstFile);
        }
        return copied;
    }

    private static void deleteFile(File dstFile) throws IOException {
        if (dstFile.exists() && dstFile.isFile()) {
            boolean deleted = dstFile.delete();
            logger.d("deleteFile file: " + dstFile + ", ret? " + deleted);
            if (!deleted) throw new IOException("delete dstFile failed!!");
        }
    }

    private static File createTmpFile(File dstFile) throws IOException {
        File tmpDstFile = new File(dstFile.getAbsolutePath() + ".tmp");
        if (tmpDstFile.exists()) {
            boolean deleted = tmpDstFile.delete();
            logger.d("createTmpFile del exists file: " + tmpDstFile + ", ret: " + deleted);
            if (!deleted) throw new IOException("delete tmp file error!!!");
        } else {
            tmpDstFile.getParentFile().mkdirs();
        }
        tmpDstFile.createNewFile();
        return tmpDstFile;
    }

    /**
     * Perform an fsync on the given FileOutputStream.  The stream at this
     * point must be flushed but not yet closed.
     */
    public static boolean sync(FileOutputStream stream) {
        try {
            if (stream != null) {
                stream.getFD().sync();
            }
            return true;
        } catch (IOException e) {
        }
        return false;
    }

    // copy a file from srcFile to destFile, return true if succeed, return
    // false if fail
    public static boolean copyFile(File srcFile, File destFile) {
        boolean result = false;
        try {
            InputStream in = new FileInputStream(srcFile);
            try {
                result = copyToFile(in, destFile);
            } finally  {
                in.close();
            }
        } catch (IOException e) {
            result = false;
        }
        return result;
    }

    /**
     * Copy data from a source stream to destFile.
     * Return true if succeed, return false if failed.
     */
    public static boolean copyToFile(InputStream inputStream, File destFile) {
        try {
            deleteFile(destFile);
            FileOutputStream out = new FileOutputStream(destFile);
            try {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) >= 0) {
                    out.write(buffer, 0, bytesRead);
                }
            } finally {
                out.flush();
                try {
                    out.getFD().sync();
                } catch (IOException e) {
                }
                out.close();
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean checkFile(String path) {
        if (!TextUtils.isEmpty(path)) {
            return checkFile(new File(path));
        }
        return false;
    }

    public static boolean checkFile(File file) {
        return file != null && file.exists() && file.isFile() && file.length() > 0;
    }

    public static String getMediaDir(String subPath) {
        return getMediaDir(subPath, true);
    }

//    private static final String ALIPAY_SDCARD_PATH = File.separator + "ExtDataTunnel" + File.separator + "files";
    public static String getMediaDir(String subPath, boolean needReset) {
        String baseDir = null;
        Context context = AppUtils.getApplicationContext();
        File file = null;
        int tryTimes = 2;
        while (--tryTimes >= 0) {
            String sdPath = ""/*FileUtils.getSDPath()*/;
            if (TextUtils.isEmpty(sdPath)) {
//                file = new ZFile(context, GROUP_ID, subPath);
            } else {
                String dir = sdPath + ALIPAY_SDCARD_PATH + File.separator + GROUP_ID;
                file = new File(dir, subPath);
//                file = new ZExternalFile(context, GROUP_ID, subPath);
            }
            boolean created = file.exists() && file.isDirectory();
            if (!created) {
                created = mkdirs(file);
            }
            if (created) {
                baseDir = file.getAbsolutePath();
				break;
            } else {
                //要是创建目录失败，很大概率是DiskCache存在fd未release
                if (ImageCacheContext.getWithoutInit() != null) {
                    ImageCacheContext.get().resetDiskCache(needReset);
                } else {
                    logger.d("getMediaDir subPath: " + subPath + ", needReset: " + needReset + ", imageCache not init");
                }
            }
        }
        //还是创建失败，使用data下目录
        if (TextUtils.isEmpty(baseDir)) {
//            file = new ZFile(context, GROUP_ID, subPath);
            baseDir = file.getAbsolutePath();
            mkdirs(file);
        }
        return baseDir;
    }

    /**
     * 创建目录，rename同名文件导致创建目录失败
     * @param dir
     * @return
     */
    public static boolean mkdirs(File dir) {
        if (dir == null) return false;
        if (dir.exists() && dir.isDirectory()) return true;
        boolean ret = false;
        File cur = dir;
        while (cur != null && !(ret = dir.mkdirs())) {
            if (cur.exists() && cur.isFile()) {
                boolean rename = cur.renameTo(new File(cur.getParent(), cur.getName() + "_" + System.currentTimeMillis()));
                if (!rename) break;
            } else {
                cur = cur.getParentFile();
            }
        }
        return ret;
    }

    /**
     * 获取文件后缀名
     * @param file
     * @return
     */
    public static String getSuffix(String file) {
        String suffix = null;
        if (!TextUtils.isEmpty(file)) {
            int index = file.lastIndexOf('.');
            if (index > 0) {
                suffix = file.substring(index, file.length());
            }
        }
        return suffix;
    }

    public static String getSdTotalSize(){
        StatFs sf = getStatFs(true);
        if (sf == null) return "0";
        long blockSize = sf.getBlockSize();
        long totalBlocks = sf.getBlockCount();
        return formatFileSize(blockSize * totalBlocks);
    }

    public static String getSdAvailableSize(){
        StatFs sf = getStatFs(true);
        if (sf == null) return "0";
        long blockSize = sf.getBlockSize();
        long availableBlocks = sf.getAvailableBlocks();
        return formatFileSize(blockSize * availableBlocks);
    }

    public static String getPhoneTotalSize(){
        StatFs sf = getStatFs(false);
        if (sf == null) return "0";
        long blockSize = sf.getBlockSize();
        long totalBlocks = sf.getBlockCount();
        return formatFileSize(blockSize * totalBlocks);
    }

    public static String getPhoneAvailableSize(){
        StatFs sf = getStatFs(false);
        if (sf == null) return "0";
        long blockSize = sf.getBlockSize();
        long availableBlocks = sf.getAvailableBlocks();
        return formatFileSize(blockSize * availableBlocks);
    }

    private static StatFs getStatFs(boolean sdcard) {
        Context context = AppUtils.getApplicationContext();
        if (context == null) return null;
        String root = ""/*sdcard ?
                FileUtils.getSDPath() :
                context.getCacheDir().getAbsolutePath()*/;
        if (TextUtils.isEmpty(root)) return null;
        return new StatFs(root);
    }

    private static String formatFileSize(long size) {
        return Formatter.formatFileSize(AppUtils.getApplicationContext(), size);
    }

    public static boolean delete(String path) {
        if (!TextUtils.isEmpty(path)) {
            return delete(new File(path));
        }
        return false;
    }

    public static boolean delete(File file) {
        if (file != null) {
            if (file.isFile() && file.exists()) {
                return file.delete();
            } else if (file.isDirectory() && file.exists()) {
                boolean delete = true;
                File[] files = file.listFiles();
                for (File f : files) {
                    delete &= delete(f);
                }
                return delete & file.delete();
            }
        }
        return false;
    }
}
