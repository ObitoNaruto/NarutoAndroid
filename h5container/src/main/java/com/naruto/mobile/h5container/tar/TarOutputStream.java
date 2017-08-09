
package com.naruto.mobile.h5container.tar;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

public class TarOutputStream extends OutputStream {
    private final OutputStream out;
    private long bytesWritten;
    private long currentFileSize;
    private TarEntry currentEntry;

    public TarOutputStream(OutputStream out) {
        this.out = out;
        bytesWritten = 0;
        currentFileSize = 0;
    }

    public TarOutputStream(final File fout) throws FileNotFoundException {
        this.out = new BufferedOutputStream(new FileOutputStream(fout));
        bytesWritten = 0;
        currentFileSize = 0;
    }

    public TarOutputStream(final File fout, final boolean append)
            throws IOException {
        @SuppressWarnings("resource")
        RandomAccessFile raf = new RandomAccessFile(fout, "rw");
        final long fileSize = fout.length();
        if (append && fileSize > TarConstants.EOF_BLOCK) {
            raf.seek(fileSize - TarConstants.EOF_BLOCK);
        }
        out = new BufferedOutputStream(new FileOutputStream(raf.getFD()));
    }

    @Override
    public void close() throws IOException {
        closeCurrentEntry();
        write(new byte[TarConstants.EOF_BLOCK]);
        out.close();
    }

    @Override
    public void write(int b) throws IOException {
        out.write(b);
        bytesWritten += 1;

        if (currentEntry != null) {
            currentFileSize += 1;
        }
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        if (currentEntry != null && !currentEntry.isDirectory()) {
            if (currentEntry.getSize() < currentFileSize + len) {
                throw new IOException("The current entry["
                        + currentEntry.getName() + "] size["
                        + currentEntry.getSize()
                        + "] is smaller than the bytes["
                        + (currentFileSize + len) + "] being written.");
            }
        }

        out.write(b, off, len);

        bytesWritten += len;

        if (currentEntry != null) {
            currentFileSize += len;
        }
    }

    public void putNextEntry(TarEntry entry) throws IOException {
        closeCurrentEntry();

        byte[] header = new byte[TarConstants.HEADER_BLOCK];
        entry.writeEntryHeader(header);

        write(header);

        currentEntry = entry;
    }

    protected void closeCurrentEntry() throws IOException {
        if (currentEntry != null) {
            if (currentEntry.getSize() > currentFileSize) {
                throw new IOException("The current entry["
                        + currentEntry.getName() + "] of size["
                        + currentEntry.getSize()
                        + "] has not been fully written.");
            }

            currentEntry = null;
            currentFileSize = 0;

            pad();
        }
    }

    protected void pad() throws IOException {
        if (bytesWritten > 0) {
            int extra = (int) (bytesWritten % TarConstants.DATA_BLOCK);

            if (extra > 0) {
                write(new byte[TarConstants.DATA_BLOCK - extra]);
            }
        }
    }
}
