
package com.naruto.mobile.h5container.download;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;

import com.naruto.mobile.h5container.util.FileUtil;

public class DiskUtil {
    public static final String TAG = "DiskUtil";

    public static String getAppDir(Context context) {
        String appDir = null;
        boolean enableMedia = false;
        if (mediaMounted() && enableMedia) {
            File fileDir = context.getExternalFilesDir("");
            if (FileUtil.exists(fileDir)) {
                appDir = fileDir.getParent();
            }
        }

        if (appDir == null) {
            File fileDir = context.getFilesDir();
            if (FileUtil.exists(fileDir)) {
                appDir = fileDir.getParent();
            }
        }
        return appDir;
    }

    public static String getSubDir(Context context, String subDir) {
        String appDir = getAppDir(context);
        if (TextUtils.isEmpty(appDir)) {
            return null;
        } else {
            return appDir + "/" + subDir;
        }
    }

    public static boolean mediaMounted() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        } else {
            return false;
        }
    }

}
