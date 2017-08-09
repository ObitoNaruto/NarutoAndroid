
package com.naruto.mobile.h5container.download;

public interface Client {
    public boolean connect(String url, String localPath);

    public boolean disconnect();

    public void setListener(TransferListener listener);
}
