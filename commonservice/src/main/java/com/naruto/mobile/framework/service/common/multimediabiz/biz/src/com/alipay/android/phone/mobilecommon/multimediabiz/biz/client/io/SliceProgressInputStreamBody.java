package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.io;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.io.output.ProgressOutputStream;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.http.apache.entity.ContentType;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.http.apache.entity.mine.content.InputStreamBody;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.http.util.Args;

/**
 * 分片input stream body
 * Created by jinmin on 15/8/7.
 */
public class SliceProgressInputStreamBody extends InputStreamBody {

    private static final String TAG = "SliceProgressInputStreamBody";
    private int startPos;
    private int endPos = -1;
    private int length = -1;
    private TransferredListener listener;

    public SliceProgressInputStreamBody(InputStream in, String mimeType, String filename) {
        super(in, mimeType, filename);
    }

    public SliceProgressInputStreamBody(InputStream in, String filename) {
        super(in, filename);
    }

    public SliceProgressInputStreamBody(InputStream in, ContentType contentType, String filename) {
        super(in, contentType, filename);
    }

    public SliceProgressInputStreamBody(InputStream in, ContentType contentType) {
        super(in, contentType);
    }

    public SliceProgressInputStreamBody(InputStream in, String filename, int startPos, int endPos, TransferredListener listener) {
        super(in, filename);
        this.startPos = startPos;
        this.endPos = endPos;
        this.listener = listener;

        this.length = endPos <= 0 ? -1 : endPos - startPos + 1;
    }

    private InputStream getOffsetStream(long offset) throws IOException {
        InputStream in = getInputStream();
        if (offset > 0) {
            long totalSkip = 0;
            while ((totalSkip += in.skip(offset)) < offset);
        }
        return in;
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {
        Args.notNull(out, "check out");
        out = new ProgressOutputStream(out, listener);
        InputStream in = getOffsetStream(startPos);
        try {
            final byte[] tmp = new byte[4096];
            int l;
            int bl = (length < 0 || length > tmp.length) ? tmp.length : length;
            int r = length == -1 ? -1 : length;
            while ((l = in.read(tmp, 0, bl)) != -1 && (r > 0 || length == -1)) {
                out.write(tmp, 0, l);
                r -= l;
                bl = r < 0 || r > tmp.length ? tmp.length : r;
            }
            out.flush();
        } finally {
            in.close();
        }

    }

    @Override
    public long getContentLength() {
        return length;
    }
}
