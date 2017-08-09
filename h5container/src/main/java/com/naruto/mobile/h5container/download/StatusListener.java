
package com.naruto.mobile.h5container.download;

import com.naruto.mobile.h5container.download.Downloader.Status;

public interface StatusListener {
    public void onStatus(String url, Status status);
}
