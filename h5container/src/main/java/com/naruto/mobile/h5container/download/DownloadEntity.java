
package com.naruto.mobile.h5container.download;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import com.naruto.mobile.h5container.util.FileUtil;

public class DownloadEntity {
    public static final String TAG = "DownloadEntity";

    public static final int PROGRESS_DELTA = 250;
    public static final int IO_BUFFER_SIZE = 16384;

    private RandomAccessFile mFile;
    private String mFilePath;
    private int mProgress;
    private long mLength;
    private long mReceived;
    private TransferListener mListener;
    private long mLastTime;
    private long mStartTime;
    private long mCurTime;
    private boolean mWritable;

    public DownloadEntity(String filePath, long fileSize) throws Exception {
        FileUtil.create(filePath);
        mFile = new RandomAccessFile(filePath, "rw");
        mStartTime = -1;
        mProgress = -1;
        mFilePath = filePath;
        mLength = fileSize;
        mWritable = true;
        mReceived = mFile.length();
    }

    public void setLength(long fileSize) {
        this.mLength = fileSize;
        if (mListener != null) {
            mListener.onTotalSize(fileSize);
        }
    }

    public void setWritable(boolean writable) {
        synchronized (this) {
            mWritable = writable;
        }
    }

    public boolean getWritable() {
        return mWritable;
    }

    public String getFilePath() {
        return mFilePath;
    }

    public void setListener(TransferListener listener) {
        this.mListener = listener;
    }

    public TransferListener getListener() {
        return this.mListener;
    }

    public void seek(long offset) {
        try {
            mFile.seek(offset);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(byte[] buffer, int offset, int count) throws IOException {
        if (!mWritable) {
            return;
        }
        synchronized (this) {
            mFile.write(buffer, offset, count);
        }

        if (mStartTime == -1) {
            mStartTime = System.currentTimeMillis();
        }

        mReceived += count;

        if (mLength <= 0 || mListener == null) {
            return;
        }

        int curPro = (int) (mReceived * 100 / mLength);
        mCurTime = System.currentTimeMillis();
        long deltaTime = mCurTime - mLastTime;

        if (curPro <= mProgress || deltaTime < PROGRESS_DELTA) {
            return;
        }

        mProgress = curPro;
        mLastTime = mCurTime;
        mListener.onProgress(mProgress);
    }

    public boolean input(InputStream ips) {
        if (ips == null) {
            return false;
        }

        try {
            int count = 0;
            byte[] buffer = new byte[IO_BUFFER_SIZE];
            while ((count = ips.read(buffer)) != -1 && getWritable()) {
                write(buffer, 0, count);
            }
            ips.close();
            close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return (mReceived == mLength);
    }

    public void close() {
        try {
            mFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
