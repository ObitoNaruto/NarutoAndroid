package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by jinmin on 15/4/15.
 */
public class ProgressInputStream extends FilterInputStream {

    private InputProgressListener mListener;
    private int mTotalRead = 0;
    /**
     * Constructs a new {@code FilterInputStream} with the specified input
     * stream as source.
     * <p/>
     * <p><strong>Warning:</strong> passing a null source creates an invalid
     * {@code FilterInputStream}, that fails on every method that is not
     * overridden. Subclasses should check for null in their constructors.
     *
     * @param in the input stream to filter reads on.
     */
    public ProgressInputStream(InputStream in) {
        super(in);
    }

    public ProgressInputStream(InputStream in, InputProgressListener listener) {
        super(in);
        this.mListener = listener;
    }

    @Override
    public int read(byte[] buffer, int byteOffset, int byteCount) throws IOException {
        int read = super.read(buffer, byteOffset, byteCount);
        if (mListener != null) {
            if (read != -1) {
                mTotalRead += read;
            }else{
                mListener.onReadFinish(mTotalRead);
            }
			
			mListener.onReadProgress(read, mTotalRead);
        }
        return read;
    }
}
