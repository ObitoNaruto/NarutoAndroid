
package com.naruto.mobile.h5container.download;

import android.webkit.MimeTypeMap;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.naruto.mobile.h5container.util.FileUtil;

import org.apache.http.entity.AbstractHttpEntity;

public class UploadEntity extends AbstractHttpEntity implements Cloneable {
    public final static String TAG = "UploadEntity";

    public static final int PROGRESS_DELTA = 250; // 250ms
    public static final int IO_BUFFER_SIZE = 16 * 1024;

    protected final File mFile;
    protected TransferListener mListener;
    protected long mFileSize;
    protected long mOffset;
    protected InputStream mInputStream;

    public UploadEntity(String absPath, long offset)
            throws FileNotFoundException {
        super();
        File file = new File(absPath);
        if (!FileUtil.exists(absPath)) {
            throw new FileNotFoundException();
        }

        String mimeType = getMimeType(absPath);
        setContentType(mimeType);
        this.mFile = file;
        this.mFileSize = mFile.length();
        this.mOffset = offset;
        try {
            mInputStream = new FileInputStream(mFile);
            mInputStream.skip(mOffset);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public void writeTo(OutputStream outstream) throws IOException {
        if (outstream == null) {
            throw new IllegalArgumentException("Output stream may not be null");
        }
        UploadData ud = new UploadData(outstream);
        try {
            byte[] tmp = new byte[IO_BUFFER_SIZE];
            int length = 0;
            while ((length = mInputStream.read(tmp)) != -1) {
                ud.write(tmp, 0, length);
            }
            ud.flush();
        } finally {
            mInputStream.close();
            ud.close();
        }
    }

    public InputStream getContent() {
        return mInputStream;
    }

    public long getContentLength() {
        return mFile.length() - mOffset;
    }

    public boolean isRepeatable() {
        return true;
    }

    public boolean isStreaming() {
        return false;
    }

    public void setListener(TransferListener listener) {
        this.mListener = listener;
    }

    public TransferListener getListener() {
        return this.mListener;
    }

    class UploadData extends DataOutputStream {
        public static final String TAG = "UploadData";

        private long mLastTime;
        private long mTransferred;
        private int mProgress;

        public UploadData(OutputStream out) {
            super(out);
            mTransferred = mOffset;
            mProgress = 0;
            mLastTime = 0;
        }

        public void write(byte[] buffer, int offset, int count)
                throws IOException {
            super.write(buffer, offset, count);

            mTransferred += count;

            if (mFileSize == 0 || mListener == null) {
                return;
            }

            int curPro = (int) (mTransferred * 100 / mFileSize);
            long curTime = System.currentTimeMillis();
            long deltaTime = curTime - mLastTime;

            if (curPro <= mProgress || deltaTime < PROGRESS_DELTA) {
                return;
            }

            mProgress = curPro;
            mLastTime = curTime;

            mListener.onProgress(mProgress);
        }
    }
}
