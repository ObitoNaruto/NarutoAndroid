package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.io;

import android.text.TextUtils;

import java.io.*;
import java.util.Arrays;

import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.io.output.ProgressOutputStream;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.http.apache.entity.ContentType;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.http.apache.entity.mine.content.FileBody;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.http.util.Args;

/**
 * 分片file body
 * Created by jinmin on 15/8/7.
 */
public class SliceProgressFileBody extends FileBody {
    private int startPos;
    private int endPos = -1;
    private int length = -1;
    private TransferredListener listener;
    private String fileName;

    public SliceProgressFileBody(File file, String filename, String mimeType, String charset) {
        super(file, filename, mimeType, charset);
    }

    public SliceProgressFileBody(File file, String mimeType, String charset) {
        super(file, mimeType, charset);
    }
    public SliceProgressFileBody(File file, String mimeType) {
        super(file, mimeType);
    }
    public SliceProgressFileBody(File file) {
        super(file);
    }
    public SliceProgressFileBody(File file, ContentType contentType, String filename) {
        super(file, contentType, filename);
    }

    public SliceProgressFileBody(File file, ContentType contentType) {
        super(file, contentType);
    }

    public SliceProgressFileBody(File file, String fileName, int startPos, int endPos, TransferredListener listener) {
        super(file);
        this.startPos = startPos;
        this.endPos = endPos;
        this.listener = listener;
        if (endPos != -1) {
            length = endPos - startPos + 1;
        }

        if (!TextUtils.isEmpty(fileName)) {
            this.fileName = fileName;
        }
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {
        Args.notNull(out, "Output stream");
        final RandomAccessFile in = new RandomAccessFile(getFile(), "r");
        in.seek(startPos);
        out = new ProgressOutputStream(out, listener);
        try {
            final byte[] tmp = new byte[4096];
            int l;
            int bl = (length < 0 || length > tmp.length) ? tmp.length : length;//buffer length
            int r = length == -1 ? -1 : length;//remain length
            while ((l = in.read(tmp, 0, bl)) != -1 && (r > 0 || length==-1)) {
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
    public String getFilename() {
        return TextUtils.isEmpty(fileName) ? super.getFilename() : fileName;
    }

    @Override
    public long getContentLength() {
        return length == -1 ? getFile().length()-startPos : length;
    }
}
