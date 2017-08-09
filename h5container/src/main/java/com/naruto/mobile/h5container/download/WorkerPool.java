
package com.naruto.mobile.h5container.download;

import java.util.LinkedList;

class WorkerPool {
    public static final int DEFAULT_WORKERS = 3;

    private LinkedList<Worker> workers;

    private int maxWorkers;

    public WorkerPool() {
        workers = new LinkedList<Worker>();
        maxWorkers = DEFAULT_WORKERS;
    }

    public void setMaxWorkers(int max) {
        maxWorkers = max;
    }

    public boolean doWork() {
        synchronized (workers) {
            if (isFull()) {
                return false;
            }

            // remove finished thread from pool
            for (Worker worker : workers) {
                if (!worker.running) {
                    try {
                        worker.join();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    workers.remove(worker);
                }
            }

            // add new thread worker
            int num = workers.size();
            if (num < maxWorkers) {
                Worker worker = new Worker();
                workers.add(worker);
                worker.setName("h5downloader_" + num);
                worker.start();
                return true;
            }

            // no thread available
            return false;
        }
    }

    public boolean isFull() {
        synchronized (workers) {
            if (workers.size() < maxWorkers) {
                return false;
            }

            // remove extra worker thread
            int num = workers.size();
            int index = num - 1;
            while (num > maxWorkers && index >= 0) {
                Worker worker = workers.get(index);
                if (!worker.running) {
                    workers.remove(index);
                }
                --index;
                num = workers.size();
            }

            for (Worker worker : workers) {
                if (!worker.running) {
                    return false;
                }
            }
            return true;
        }
    }

    public boolean hasTask(TaskInfo t) {
        if (t == null) {
            return false;
        }
        String tUrl = t.getUrl();
        synchronized (workers) {
            for (Worker worker : workers) {
                if (worker.task == null) {
                    continue;
                }

                String wUrl = worker.task.getUrl();
                if (tUrl != null && tUrl.equals(wUrl)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean clearTask() {
        synchronized (workers) {
            for (Worker worker : workers) {
                worker.disconnect();
            }
        }
        return true;
    }
}
