/**
 * 
 */
package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.io.output;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.io.TransferredListener;


/**
 * @author zhenghui
 *
 */
public class ProgressOutputStream extends FilterOutputStream {

	private final TransferredListener listener;
    private long transferred;
    private long offset;

    public ProgressOutputStream(final OutputStream out,
            final TransferredListener listener) {
        this(out, 0, listener);
    }

    public ProgressOutputStream(final OutputStream out,
                                long offset,
                                final TransferredListener listener) {
        super(out);
        this.listener = listener;
        this.transferred = 0;
        this.offset = offset;
    }

    @Override
    public void write(final byte[] b, final int off, final int len)
            throws IOException {
        // NO, double-counting, as super.write(byte[], int, int)
        // delegates to write(int).
        // super.write(b, off, len);
        out.write(b, off, len);
        this.transferred += len;
        if (this.listener != null) {
            this.listener.onTransferred(this.transferred + offset);
        }
    }

    @Override
    public void write(final int b) throws IOException {
        out.write(b);
        this.transferred++;
        this.listener.onTransferred(this.transferred + offset);
    }

}
