
package com.naruto.mobile.h5container.download;

public interface Downloader {

    /* enable transfer under WiFi network */
    public static final int OPT_WIFI_ENABLE = 1;

    /* enable transfer under mobile network */
    public static final int OPT_MOBILE_ENABLE = 2;

    public static enum Status {
        /* initial status */
        NONE,

        /* task is pending */
        PENDDING,

        /* task is downloading */
        DOWNLOADING,

        /* task paused */
        PAUSED,

        /* task failed */
        FAILED,

        /* task succeed */
        SUCCEED,
    };

    public boolean add(String url, int options);

    public boolean has(String url);

    public boolean cancel(String url);

    public boolean pause(String url);

    public boolean resume(String url, int options);

    public int size();

    public Status getStatus(String url);

    public int getProgress(String url);

    public String getFile(String url);

    public boolean deleteFile(String url);

    public void setProgressListener(ProgressListener l);

    public void setStatusListener(StatusListener l);
}
