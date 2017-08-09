
package com.naruto.mobile.h5container.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import com.naruto.mobile.h5container.tar.TarEntry;
import com.naruto.mobile.h5container.tar.TarInputStream;

public class TarUtil {

    public static final String TAG = "TarUtil";

    public static boolean untar(String tarPath, String untarFolder) {
        if (!FileUtil.exists(tarPath)) {
            H5Log.e(TAG, "tar path not exists!");
            return false;
        }

        if (!FileUtil.mkdirs(untarFolder, true)) {
            H5Log.e(TAG, "failed to create untar folder.");
            return false;
        }

        try {
            FileInputStream fis = new FileInputStream(tarPath);
            BufferedInputStream bis = new BufferedInputStream(fis);
            TarInputStream tis = new TarInputStream(bis);
            TarEntry te;

            while ((te = tis.getNextEntry()) != null) {
                String entryName = te.getName();
                H5Log.d(TAG, "untar entry " + entryName);

                String entryPath = untarFolder + "/" + entryName;
                if (te.isDirectory()) {
                    FileUtil.mkdirs(entryPath);
                } else {
                    if (!FileUtil.create(entryPath, true)) {
                        H5Log.e(TAG, "failed to create file " + entryPath);
                        continue;
                    }

                    byte buffer[] = new byte[2048];
                    int count;
                    FileOutputStream fos = new FileOutputStream(entryPath);
                    BufferedOutputStream bos = new BufferedOutputStream(fos);
                    while ((count = tis.read(buffer)) != -1) {
                        bos.write(buffer, 0, count);
                    }
                    bos.flush();
                    bos.close();
                }
            }
            tis.close();
        } catch (Exception e) {
            H5Log.e("untar exception", e);
            return false;
        }
        return true;
    }
}
