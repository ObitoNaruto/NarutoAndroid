
package com.naruto.mobile.h5container.download;

public interface TransferListener {
    void onProgress(int progress);

    void onTotalSize(long size);
}
