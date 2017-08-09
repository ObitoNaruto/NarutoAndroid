
package com.naruto.mobile.h5container.download;

import android.content.Context;
import android.text.TextUtils;

import java.io.File;
import java.util.Stack;

import com.naruto.mobile.h5container.util.FileUtil;
import com.naruto.mobile.h5container.util.SecurityUtil;

public class FileCache {
    public static final String TAG = "FileCache";

    public static final long EXPIRE_TIME = 604800000; // 7*24*60*60*1000

    public FileCache(Context context) {
        clearExpired(context);
    }

    public String getCachePath(Context context, String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }

        String mapTo = SecurityUtil.getSHA1(url) + "."
                + FileUtil.getExtension(url);
        String subPath = "/h5/download/cache/" + mapTo;
        String filePath = DiskUtil.getSubDir(context, subPath);
        return filePath;
    }

    public String getTempPath(Context context, String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        String mapTo = SecurityUtil.getSHA1(url);
        String subPath = "/h5/download/temp/" + mapTo;
        String filePath = DiskUtil.getSubDir(context, subPath);
        return filePath;
    }

    public String getDebugPath(Context context, String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        String mapTo = SecurityUtil.getSHA1(url);
        String subPath = "/h5/download/debug/" + mapTo;
        String filePath = DiskUtil.getSubDir(context, subPath);
        return filePath;
    }

    // delete expired cache file
    public void clearExpired(Context context) {
        String tempDir = DiskUtil.getSubDir(context, "/h5/download/temp");
        if (!FileUtil.exists(tempDir)) {
            FileUtil.mkdirs(tempDir);
        } else {
            clearPath(tempDir);
        }

        String cacheDir = DiskUtil.getSubDir(context, "/h5/download/cache");
        if (!FileUtil.exists(cacheDir)) {
            FileUtil.mkdirs(cacheDir);
        } else {
            clearPath(cacheDir);
        }

        String debugDir = DiskUtil.getSubDir(context, "/h5/download/debug");
        if (!FileUtil.exists(debugDir)) {
            FileUtil.mkdirs(debugDir);
        } else {
            clearPath(debugDir);
        }
    }

    private void clearPath(String path) {
        if (!FileUtil.exists(path)) {
            return;
        }

        Stack<String> pathStack = new Stack<String>();
        pathStack.push(path);

        long current = System.currentTimeMillis();
        while (!pathStack.isEmpty()) {
            String curPath = pathStack.pop();

            File curFile = new File(curPath);
            long lastModified = curFile.lastModified();
            if ((current - lastModified) > EXPIRE_TIME) {
                FileUtil.delete(curFile);
                continue;
            }

            if (!curFile.isDirectory()) {
                continue;
            }

            File[] children = curFile.listFiles();
            if (children == null || children.length == 0) {
                continue;
            }

            for (File child : children) {
                pathStack.push(child.getAbsolutePath());
            }
        }
    }
}
