
package com.naruto.mobile.h5container.download;

import java.util.LinkedList;
import java.util.List;

import com.naruto.mobile.h5container.download.Downloader.Status;

public class TaskBoxImpl implements TaskBox<TaskInfo> {
    public static final String TAG = "TaskBoxImpl";

    private static volatile TaskBox<TaskInfo> taskBox;

    public static final TaskBox<TaskInfo> getInstance() {
        synchronized (TaskBoxImpl.class) {
            if (taskBox == null) {
                taskBox = new TaskBoxImpl();
            }
        }
        return taskBox;
    }

    private LinkedList<TaskInfo> tasks;

    public TaskBoxImpl() {
        tasks = new LinkedList<TaskInfo>();
    }

    @Override
    public boolean addTask(TaskInfo info) {
        if (info == null) {
            return false;
        }

        synchronized (tasks) {
            String url = info.getUrl();
            if (hasTask(url)) {
                return false;
            }

            tasks.addLast(info);
            return true;
        }
    }

    @Override
    public boolean hasTask(String url) {
        if (url == null) {
            return false;
        }

        synchronized (tasks) {
            for (TaskInfo t : tasks) {
                if (t.getUrl().equals(url)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public TaskInfo removeTask(String url) {
        if (url == null) {
            return null;
        }

        synchronized (tasks) {
            int size = tasks.size();
            int index = 0;
            for (; index < size; ++index) {
                TaskInfo t = tasks.get(index);
                if (url.equals(t.getUrl())) {
                    tasks.remove(index);
                    return t;
                }
            }
        }
        return null;
    }

    @Override
    public TaskInfo getTask(String url) {
        if (url == null) {
            return null;
        }

        synchronized (tasks) {
            for (TaskInfo t : tasks) {
                if (t.getUrl().equals(url)) {
                    return t;
                }
            }
        }
        return null;
    }

    @Override
    public TaskInfo doTask() {
        synchronized (tasks) {
            int size = tasks.size();
            int index = 0;
            for (; index < size; ++index) {
                TaskInfo t = tasks.get(index);
                if (t.getStatus() == Status.PENDDING) {
                    t.setStatus(Status.DOWNLOADING);
                    return t;
                }
            }
            return null;
        }
    }

    @Override
    public boolean clearTask() {
        synchronized (tasks) {
            tasks.clear();
        }
        return true;
    }

    @Override
    public List<TaskInfo> tasks() {
        return tasks;
    }

    @Override
    public int size() {
        return tasks.size();
    }
}
