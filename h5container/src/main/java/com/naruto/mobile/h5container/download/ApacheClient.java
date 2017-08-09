
package com.naruto.mobile.h5container.download;

import android.os.Looper;

import com.naruto.mobile.h5container.download.ConnectInfo.HttpMethod;
import com.naruto.mobile.h5container.util.FileUtil;

public class ApacheClient implements Client {
    private Connector connector;
    private DownloadEntity dEntity;
    private TransferListener listener;

    public ApacheClient() {
        connector = new Connector();
        dEntity = null;
    }

    @Override
    public boolean connect(String url, String localPath) {
        ConnectInfo info = new ConnectInfo(null);
        try {
            dEntity = new DownloadEntity(localPath, 0);
            dEntity.setListener(listener);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        info.setURL(url);
        info.setMethod(HttpMethod.GET);
        long size = FileUtil.size(localPath);
        if (size > 0) {
            String range = "bytes=" + size + "-";
            info.addHeader("RANGE", range);
        }

        info.setDownloadEntity(dEntity);
        connector.setConnectInfo(info);
        return connector.connect();
    }

    @Override
    public boolean disconnect() {
        if (dEntity != null) {
            dEntity.setWritable(false);
        }

        if (Looper.getMainLooper() == Looper.myLooper()) {
            new Thread() {
                public void run() {
                    connector.disconnect();
                }
            }.start();
            return true;
        } else {
            return connector.disconnect();
        }
    }

    @Override
    public void setListener(TransferListener listener) {
        this.listener = listener;
    }

}
