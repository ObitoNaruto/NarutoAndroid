
package com.naruto.mobile.h5container.util;

import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;

public class FileUtil {
    public static final String TAG = "FileUtil";
    private static final int IO_BUFFER_SIZE = 16384;

    public static boolean create(String absPath) {
        return create(absPath, false);
    }

    public static boolean create(String absPath, boolean force) {
        if (TextUtils.isEmpty(absPath)) {
            return false;
        }

        if (exists(absPath)) {
            return true;
        }

        String parentPath = getParent(absPath);
        mkdirs(parentPath, force);

        try {
            File file = new File(absPath);
            return file.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean mkdirs(String absPath) {
        return mkdirs(absPath, false);
    }

    public static boolean mkdirs(String absPath, boolean force) {
        File file = new File(absPath);
        if (exists(absPath) && !isFolder(absPath)) {
            if (!force) {
                return false;
            }

            delete(file);
        }
        try {
            file.mkdirs();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return exists(file);
    }

    public static boolean move(String srcPath, String dstPath) {
        return move(srcPath, dstPath, false);
    }

    public static boolean move(String srcPath, String dstPath, boolean force) {
        if (TextUtils.isEmpty(srcPath) || TextUtils.isEmpty(dstPath)) {
            return false;
        }

        if (!exists(srcPath)) {
            return false;
        }

        if (exists(dstPath)) {
            if (!force) {
                return false;
            } else {
                delete(dstPath);
            }
        }

        try {
            File srcFile = new File(srcPath);
            File dstFile = new File(dstPath);
            return srcFile.renameTo(dstFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean delete(String absPath) {
        if (TextUtils.isEmpty(absPath)) {
            return false;
        }

        File file = new File(absPath);
        return delete(file);
    }

    public static boolean delete(File file) {
        if (!exists(file)) {
            return true;
        }

        if (file.isFile()) {
            return file.delete();
        }

        boolean result = true;
        File files[] = file.listFiles();
        for (int index = 0; index < files.length; index++) {
            result |= delete(files[index]);
        }
        result |= file.delete();

        return result;
    }

    public static boolean exists(String absPath) {
        if (TextUtils.isEmpty(absPath)) {
            return false;
        }
        File file = new File(absPath);
        return exists(file);
    }

    public static boolean exists(File file) {
        if (file == null) {
            return false;
        }
        return file.exists();
    }

    public final static String getParent(String absPath) {
        if (TextUtils.isEmpty(absPath)) {
            return null;
        }
        absPath = cleanPath(absPath);
        File file = new File(absPath);
        return getParent(file);
    }

    public final static String getParent(File file) {
        if (file == null) {
            return null;
        } else {
            return file.getParent();
        }
    }

    public static boolean childOf(String childPath, String parentPath) {
        if (TextUtils.isEmpty(childPath) || TextUtils.isEmpty(parentPath)) {
            return false;
        }
        childPath = cleanPath(childPath);
        parentPath = cleanPath(parentPath);
        if (childPath.startsWith(parentPath + File.separator)) {
            return true;
        }
        return false;
    }

    public static int childCount(String absPath) {
        if (!exists(absPath)) {
            return 0;
        }
        File file = new File(absPath);
        File[] children = file.listFiles();
        if (children == null || children.length == 0) {
            return 0;
        }
        return children.length;
    }

    public static String cleanPath(String absPath) {
        if (TextUtils.isEmpty(absPath)) {
            return absPath;
        }
        try {
            File file = new File(absPath);
            absPath = file.getCanonicalPath();
        } catch (Exception e) {

        }
        return absPath;
    }

    public static long size(String absPath) {
        if (absPath == null) {
            return 0;
        }
        File file = new File(absPath);
        return size(file);
    }

    public static long size(File file) {
        if (!exists(file)) {
            return 0;
        }

        long length = 0;
        if (isFile(file)) {
            length = file.length();
            return length;
        }

        File files[] = file.listFiles();
        if (files == null || files.length == 0) {
            return length;
        }

        int size = files.length;
        for (int index = 0; index < size; index++) {
            File child = files[index];
            length += size(child);
        }
        return length;
    }

    public static boolean copy(String srcPath, String dstPath) {
        return copy(srcPath, dstPath, false);
    }

    public static boolean copy(String srcPath, String dstPath, boolean force) {
        if (TextUtils.isEmpty(srcPath) || TextUtils.isEmpty(dstPath)) {
            return false;
        }

        // check if copy source equals destination
        if (srcPath.equals(dstPath)) {
            return true;
        }

        // check if source file exists or is a directory
        if (!exists(srcPath) || !isFile(srcPath)) {
            return false;
        }

        // delete old content
        if (exists(dstPath)) {
            if (!force) {
                return false;
            } else {
                delete(dstPath);
            }
        }
        if (!create(dstPath)) {
            return false;
        }

        FileInputStream in = null;
        FileOutputStream out = null;

        // get streams
        try {
            in = new FileInputStream(srcPath);
            out = new FileOutputStream(dstPath);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        try {
            byte[] buffer = new byte[IO_BUFFER_SIZE];

            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                in.close();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public static String fileName(String absPath) {
        if (TextUtils.isEmpty(absPath)) {
            return null;
        }

        absPath = cleanPath(absPath);
        File file = new File(absPath);
        return file.getName();
    }

    public static String getExtension(File file) {
        if (file == null) {
            return null;
        }
        return getExtension(file.getName());
    }

    public static String getExtension(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return "";
        }

        int index = fileName.lastIndexOf('.');
        if (index < 0 || index >= (fileName.length() - 1)) {
            return "";
        }
        return fileName.substring(index + 1);
    }

    public static String getMimeType(File file) {
        if (file == null) {
            return "*/*";
        }
        String fileName = file.getName();
        return getMimeType(fileName);
    }

    public static String getMimeType(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return "*/*";
        }
        String extension = getExtension(fileName);
        MimeTypeMap map = MimeTypeMap.getSingleton();
        String type = map.getMimeTypeFromExtension(extension);
        if (TextUtils.isEmpty(type)) {
            return "*/*";
        } else {
            return type;
        }
    }

    public final static boolean isFile(String absPath) {
        boolean exists = exists(absPath);
        if (!exists) {
            return false;
        }

        File file = new File(absPath);
        return isFile(file);
    }

    public final static boolean isFile(File file) {
        if (file == null) {
            return false;
        }

        return file.isFile();
    }

    public final static boolean isFolder(String absPath) {
        boolean exists = exists(absPath);
        if (!exists) {
            return false;
        }

        File file = new File(absPath);
        return file.isDirectory();
    }

    public final static String getFileName(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }
        int index = filePath.lastIndexOf("/");
        if (index > 0 && index < (filePath.length() - 1)) {
            filePath = filePath.substring(index + 1, filePath.length());
        }
        return filePath;
    }

    public final static String getFileStem(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return null;
        }

        int index = fileName.lastIndexOf(".");
        if (index > 0) {
            return fileName.substring(0, index);
        } else {
            return "";
        }
    }

    public static String getFileSHA1(String absPath) {
        if (TextUtils.isEmpty(absPath)) {
            return null;
        }
        File file = new File(absPath);

        if (!file.exists() || file.isDirectory()) {
            return null;
        }
        String fileHash = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
        if (fis == null) {
            return null;
        }

        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("SHA-1");
            byte[] buffer = new byte[IO_BUFFER_SIZE];
            int length = 0;
            while ((length = fis.read(buffer)) > 0) {
                messageDigest.update(buffer, 0, length);
            }
            fis.close();
            fileHash = SecurityUtil.bytes2Hex(messageDigest.digest());
        } catch (Exception e) {
            e.printStackTrace();
            try {
                fis.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        if (!TextUtils.isEmpty(fileHash)) {
            fileHash = fileHash.trim();
        }
        return fileHash;
    }

    public static String getFileMD5(String absPath) {
        if (TextUtils.isEmpty(absPath)) {
            return null;
        }
        File file = new File(absPath);
        if (!file.exists() || file.isDirectory()) {
            return null;
        }
        String fileHash = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }

        if (fis == null) {
            return null;
        }

        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[IO_BUFFER_SIZE];
            int length = 0;
            while ((length = fis.read(buffer)) > 0) {
                messageDigest.update(buffer, 0, length);
            }
            fis.close();
            fileHash = SecurityUtil.bytes2Hex(messageDigest.digest());
        } catch (Exception e) {
            e.printStackTrace();
            try {
                fis.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        if (!TextUtils.isEmpty(fileHash)) {
            fileHash = fileHash.trim();
        }
        return fileHash;
    }

    public static String getText(String absPath) {
        if (!exists(absPath)) {
            return null;
        }

        File file = new File(absPath);
        int length = (int) file.length();
        if (length > 20480) {
            return null;
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream(length);
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(file));
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int len = 0;
            while (-1 != (len = in.read(buffer, 0, bufferSize))) {
                bos.write(buffer, 0, len);
            }
            in.close();
            bos.close();
            return bos.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
