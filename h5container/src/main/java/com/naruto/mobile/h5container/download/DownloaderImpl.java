
package com.naruto.mobile.h5container.download;

import android.content.Context;
import android.text.TextUtils;

import java.util.List;

import com.naruto.mobile.h5container.env.H5Environment;
import com.naruto.mobile.h5container.util.FileUtil;
import com.naruto.mobile.h5container.util.H5Log;
import com.naruto.mobile.h5container.util.H5Utils;
import com.naruto.mobile.h5container.util.NetworkUtil;
import com.naruto.mobile.h5container.util.NetworkUtil.NetworkListener;
import com.naruto.mobile.h5container.util.NetworkUtil.NetworkType;

public class DownloaderImpl implements Downloader, NetworkListener,
        ProgressListener, StatusListener {
    public static final String TAG = "H5Downloader";

    private Context context;
    private TaskBox<TaskInfo> taskBox;
    private WorkerPool workerPool;
    private InfoCache infoCache;
    private FileCache fileCache;
    private ProgressListener pl;
    private StatusListener sl;
    private NetworkUtil networkUtil;

    private static Downloader downloader;

    public static final Downloader getInstance() {
        synchronized (DownloaderImpl.class) {
            if (downloader == null) {
                downloader = new DownloaderImpl();
            }
        }
        return downloader;
    }

    private DownloaderImpl() {
        taskBox = TaskBoxImpl.getInstance();
        workerPool = new WorkerPool();
        context = H5Environment.getContext();

        infoCache = new InfoCache(context);
        fileCache = new FileCache(context);

        networkUtil = new NetworkUtil(context);
    }

    @Override
    public boolean add(String url, int options) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }

        TaskInfo t = taskBox.getTask(url);
        if (t != null) {
            Status status = t.getStatus();
            if (status == Status.PENDDING || status == Status.DOWNLOADING) {
                H5Log.w(TAG, "download already exists! " + url);
                return true;
            }
        }

        TaskInfo task = new TaskInfo(url, options);
        task.setContext(context);
        task.setProgressListener(this);
        task.setStatusListener(this);
        String tempPath = fileCache.getTempPath(context, url);
        task.setPath(tempPath);

        if (getFile(url) != null) {
            task.setProgress(100);
            task.setStatus(Status.SUCCEED);
            return true;
        }

        if (!FileUtil.exists(tempPath)) {
            infoCache.remove(url);
        }

        if (infoCache.contains(url)) {
            t = infoCache.get(url);
            task.setProgress(t.getProgress());
            long totalSize = t.getTotalSize();
            task.setTotalSize(totalSize);
        }

        NetworkType type = networkUtil.getNetworkType();

        if (type == NetworkType.NONE) {
            H5Log.w(TAG, "no network for download");
            return false;
        }

        taskBox.removeTask(url);
        taskBox.addTask(task);
        int mobile = options & Downloader.OPT_MOBILE_ENABLE;
        if (type != NetworkType.WIFI && mobile == 0) {
            H5Log.d(TAG, "task failed for not enable mobile flag.");
            cancel(url);
            return false;
        } else if (type == NetworkType.WIFI) {
            if (taskBox.size() > 0) {
                networkUtil.register();
                networkUtil.setListener(this);
            }
            task.setStatus(Status.PENDDING);
            workerPool.doWork();
            return true;
        }
        return false;
    }

    @Override
    public boolean has(String url) {
        if (taskBox.hasTask(url)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean cancel(String url) {
        TaskInfo task = taskBox.getTask(url);
        if (task == null) {
            return false;
        }

        boolean downloading = (task.getStatus() == Status.DOWNLOADING);
        task.setStatus(Status.FAILED);

        if (downloading) {
            task.getClient().disconnect();
        }

        taskBox.removeTask(url);
        return true;
    }

    @Override
    public boolean pause(String url) {
        TaskInfo task = taskBox.getTask(url);
        if (task == null) {
            return false;
        }

        boolean downloading = (task.getStatus() == Status.DOWNLOADING);

        taskBox.removeTask(url);
        infoCache.set(url, task);
        task.setStatus(Status.PAUSED);

        if (downloading) {
            task.getClient().disconnect();
        }

        infoCache.save(context);
        return false;
    }

    @Override
    public boolean resume(String url, int options) {
        TaskInfo task = taskBox.getTask(url);
        if (task == null) {
            task = infoCache.get(url);
        }

        if (task == null) {
            return false;
        }

        if (task.getStatus() != Status.PAUSED) {
            return false;
        }

        task.setContext(context);
        task.setProgressListener(this);
        task.setStatusListener(this);
        task.setOptions(options);

        if (infoCache.contains(url)) {
            TaskInfo t = infoCache.get(url);
            task.setProgress(t.getProgress());
        }

        task.setStatus(Status.PENDDING);
        taskBox.addTask(task);
        if (!workerPool.isFull()) {
            workerPool.doWork();
        }
        return true;
    }

    @Override
    public Status getStatus(String url) {
        TaskInfo task = taskBox.getTask(url);

        if (task == null) {
            task = infoCache.get(url);
        }

        if (task == null) {
            return Status.NONE;
        }
        return task.getStatus();
    }

    @Override
    public int getProgress(String url) {
        TaskInfo task = taskBox.getTask(url);
        if (task == null) {
            task = infoCache.get(url);
        }

        if (task != null) {
            return task.getProgress();
        } else if (getFile(url) != null) {
            return 100;
        } else {
            return 0;
        }
    }

    @Override
    public String getFile(String url) {
        String filePath = fileCache.getCachePath(context, url);
        if (FileUtil.exists(filePath)) {
            return filePath;
        } else {
            return null;
        }
    }

    @Override
    public boolean deleteFile(String url) {
        String filePath = getFile(url);
        if (!FileUtil.exists(filePath)) {
            return false;
        }
        return FileUtil.delete(filePath);
    }

    @Override
    public void setProgressListener(ProgressListener l) {
        this.pl = l;
    }

    @Override
    public void setStatusListener(StatusListener l) {
        this.sl = l;
    }

    @Override
    public void onNetworkChanged(NetworkType ot, NetworkType nt) {
        H5Log.d(TAG, "onNetworkChanged " + ot + " --> " + nt);

        if (nt == NetworkType.NONE || nt == NetworkType.WIFI) {
            return;
        }

        List<TaskInfo> tasks = taskBox.tasks();
        for (TaskInfo t : tasks) {
            int options = t.getOptions();
            int mobile = options & Downloader.OPT_MOBILE_ENABLE;
            if (mobile == 0) {
                String url = t.getUrl();
                cancel(url);
                // TODO
                // pause(url);
            }
        }
    }

    @Override
    public void onProgress(final String url, final int progress) {
        H5Log.d(TAG, "onProgress " + url + " " + progress);

        if (pl == null) {
            return;
        }

        H5Utils.runOnMain(new Runnable() {

            @Override
            public void run() {
                pl.onProgress(url, progress);
            }
        });
    }

    @Override
    public void onStatus(final String url, final Status status) {
        H5Log.d(TAG, "onStatus " + url + " " + status);

        if (sl == null) {
            return;
        }

        if (status == Status.SUCCEED) {
            String tempPath = fileCache.getTempPath(context, url);
            String cachePath = fileCache.getCachePath(context, url);
            FileUtil.move(tempPath, cachePath, true);
            if (infoCache.remove(url) != null) {
                infoCache.save(context);
            }
            taskBox.removeTask(url);
        } else if (status == Status.FAILED) {
            TaskInfo task = taskBox.getTask(url);
            infoCache.set(url, task);
            infoCache.save(context);
            taskBox.removeTask(url);
        }

        if (taskBox.size() == 0) {
            networkUtil.unregister();
            networkUtil.setListener(null);
        }

        H5Utils.runOnMain(new Runnable() {

            @Override
            public void run() {
                sl.onStatus(url, status);
            }
        });
    }

    @Override
    public int size() {
        return taskBox.size();
    }
}
