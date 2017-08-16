package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.UUID;

import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.io.IOUtils;

/**
 *
 * Created by jinmin on 15/8/10.
 */
public class UuidManager {

    private static final String TAG = "UuidManager";
    private static final String FILE_NAME = "dj.u";
    private static UuidManager sInstance;
    private Context mContext;
    private UUID mUUID;

    private UuidManager() {
        mContext = AppUtils.getApplicationContext();
        createUUID();
    }

    public static UuidManager get() {
        if (sInstance == null) {
            synchronized (UuidManager.class) {
                sInstance = new UuidManager();
            }
        }
        return sInstance;
    }

    public UUID getUUID() {
        Logger.P(TAG, "getUUID " + mUUID);
        return mUUID;
    }

    //
    private synchronized void createUUID() {
        if (mUUID == null) {
            mUUID = loadUUID();
            if (mUUID == null) {
                mUUID = UUID.randomUUID();
                saveUUID();
            }
        }
    }

    private void saveUUID() {
        if (mUUID != null) {
            ByteBuffer buffer = ByteBuffer.allocate(16);
            buffer.putLong(mUUID.getMostSignificantBits());
            buffer.putLong(mUUID.getLeastSignificantBits());
            File dataFile = new File(mContext.getFilesDir(), FILE_NAME);
            try {
                FileUtils.safeCopyToFile(buffer.array(), dataFile);
                FileUtils.copyFile(dataFile, new File(FileUtils.getMediaDir("multimedia"), FILE_NAME));
            } catch (IOException e) {
                Logger.E(TAG, e, "saveUUID error");
            }
        }
    }

    private UUID loadUUID() {
        byte[] data = loadData();
        if (data != null) {
            ByteBuffer buffer = ByteBuffer.wrap(data);
            long mostSigBits = buffer.getLong();
            long leastSigBits = buffer.getLong();
            mUUID = new UUID(mostSigBits, leastSigBits);
        }
        return mUUID;
    }

    private byte[] loadData() {
        File dataFile = new File(mContext.getFilesDir(), FILE_NAME);
        if (!FileUtils.checkFile(dataFile)) {
            File sdDataFile = new File(FileUtils.getMediaDir("multimedia"), FILE_NAME);
            if (FileUtils.checkFile(sdDataFile)) {
                FileUtils.copyFile(sdDataFile, dataFile);
            }
            dataFile = sdDataFile;
        }
        FileInputStream in = null;
        try {
            in = new FileInputStream(dataFile);
            byte[] data = new byte[16];
            int read = in.read(data);
            return read == data.length ? data : null;
        } catch (IOException e) {
            Logger.E(TAG, e, "loadUUID error");
        } finally {
            IOUtils.closeQuietly(in);
        }
        return null;
    }

}
