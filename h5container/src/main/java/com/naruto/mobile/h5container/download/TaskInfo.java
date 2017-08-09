
package com.naruto.mobile.h5container.download;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.naruto.mobile.h5container.download.Downloader.Status;
import com.naruto.mobile.h5container.util.H5Log;
import com.naruto.mobile.h5container.util.H5Utils;

public class TaskInfo implements TransferListener {
    public static final String TAG = "TaskImpl";

    private String url;
    private int options;
    private Status status;
    private long totalSize;
    private int progress;
    private Context context;
    private Client client;
    private ProgressListener pl;
    private StatusListener sl;
    private long time;
    private String path;

    public TaskInfo() {
        status = Status.NONE;
        progress = 0;
        options = Downloader.OPT_WIFI_ENABLE;
    }

    public TaskInfo(String url, int options) {
        this();
        this.url = url;
    }

    public TaskInfo(String text) {
        this();
        try {
            JSONObject jo = H5Utils.parseObject(text);
            if (jo == null || jo.isEmpty()) {
                return;
            }
            url = jo.getString("url");
            progress = jo.getIntValue("progress");
            time = jo.getLongValue("time");
            status = Status.valueOf(jo.getString("status"));
            totalSize = jo.getLongValue("total");
            path = jo.getString("path");
            options = jo.getIntValue("options");
        } catch (Exception e) {

        }
    }

    public int getProgress() {
        return progress;
    }

    protected void setStatus(Status status) {
        if (this.status == status) {
            return;
        }

        H5Log.d(TAG, "setStatus " + status);
        this.status = status;

        if (sl != null) {
            sl.onStatus(url, status);
        }
    }

    public Status getStatus() {
        return this.status;
    }

    public String getUrl() {
        return url;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getOptions() {
        return options;
    }

    protected void setOptions(int options) {
        this.options = options;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((url == null) ? 0 : url.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TaskInfo other = (TaskInfo) obj;
        if (url == null) {
            if (other.url != null)
                return false;
        } else if (!url.equals(other.url))
            return false;
        return true;
    }

    @Override
    public String toString() {
        // for save progress
        JSONObject joTask = new JSONObject();

        joTask.put("url", url);
        joTask.put("status", status);
        joTask.put("progress", progress);
        joTask.put("total", getTotalSize());
        joTask.put("time", System.currentTimeMillis());
        joTask.put("path", path);
        joTask.put("options", options);

        return joTask.toJSONString();
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void setProgressListener(ProgressListener l) {
        this.pl = l;
    }

    public void setStatusListener(StatusListener l) {
        this.sl = l;
    }

    @Override
    public void onProgress(int progress) {
        if (this.progress == progress) {
            return;
        }
        this.progress = progress;

        if (pl != null) {
            pl.onProgress(url, progress);
        }
    }

    @Override
    public void onTotalSize(long size) {
        this.setTotalSize(size);
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }
}
