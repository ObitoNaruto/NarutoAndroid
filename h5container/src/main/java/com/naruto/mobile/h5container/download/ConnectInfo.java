
package com.naruto.mobile.h5container.download;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

public class ConnectInfo implements Parcelable {
    public static final String TAG = "ConnectInfo";

    public static final String REQUEST = "request";
    public static final String RESPONSE = "response";

    public static final int TIMEOUT_LONG = 60000;
    public static final int TIMEOUT_SHORT = 20000;

    public static enum HttpMethod {
        NONE, GET, PUT, POST, DELETE,
    };

    private String mUserAgent;
    private String mAction;
    private String mTag;
    private String mURL;
    private HttpMethod mMethod;
    private int mTimeout;
    private HttpEntity uEntity;
    private DownloadEntity dEntity;
    private HashMap<String, String> mHeaders;
    private Runnable mRunner;

    public ConnectInfo(String action) {
        mAction = action;
        mTag = action;
        mTimeout = TIMEOUT_SHORT;
        uEntity = null;
        mHeaders = new HashMap<String, String>();
        mMethod = HttpMethod.NONE;
    }

    private ConnectInfo(Parcel parcel) {
        this("");
    }

    public String getURL() {
        return mURL;
    }

    public ConnectInfo setURL(String url) {
        this.mURL = url;
        return this;
    }

    public String getAction() {
        return mAction;
    }

    public ConnectInfo setAction(String action) {
        this.mAction = action;
        return this;
    }

    public String getTag() {
        return mTag;
    }

    public ConnectInfo setTag(String tag) {
        this.mTag = tag;
        return this;
    }

    public Runnable getRunner() {
        return this.mRunner;
    }

    public ConnectInfo setRunner(Runnable runner) {
        this.mRunner = runner;
        return this;
    }

    public HttpMethod getMethod() {
        return this.mMethod;
    }

    public int getTimeout() {
        return this.mTimeout;
    }

    public ConnectInfo setTimeout(int timeout) {
        this.mTimeout = timeout;
        return this;
    }

    public DownloadEntity getDownloadEntity() {
        return this.dEntity;
    }

    public ConnectInfo setDownloadEntity(DownloadEntity entity) {
        this.dEntity = entity;
        return this;
    }

    public ConnectInfo setMethod(HttpMethod method) {
        this.mMethod = method;
        return this;
    }

    public HttpEntity getUploadEntity() {
        return this.uEntity;
    }

    public ConnectInfo setUploadEntity(HttpEntity entity) {
        uEntity = entity;
        return this;
    }

    public String getUserAgent() {
        return mUserAgent;
    }

    public ConnectInfo setUserAgent(String userAgent) {
        mUserAgent = userAgent;
        return this;
    }

    public HashMap<String, String> getHeaders() {
        return this.mHeaders;
    }

    public ConnectInfo addHeader(String name, String value) {
        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(value)) {
            mHeaders.put(name, value);
        }
        return this;
    }

    public ConnectInfo addHeaders(HashMap<String, String> headers) {
        if (headers == null || headers.size() == 0) {
            return this;
        }

        for (String key : headers.keySet()) {
            String value = headers.get(key);
            if (TextUtils.isEmpty(key) || TextUtils.isEmpty(value)) {
                continue;
            }
            mHeaders.put(key, value);
        }
        return this;
    }

    public boolean valid() {
        return !(TextUtils.isEmpty(mTag) || TextUtils.isEmpty(mURL)
                || mTimeout <= 0 || TextUtils.isEmpty(mAction) || mMethod == HttpMethod.NONE);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[action] " + mAction + "\n");
        builder.append("[tag] " + mTag + "\n");
        builder.append("[url] " + mURL + "\n");
        builder.append("[method] " + mMethod + "\n");
        builder.append("[timeout] " + mTimeout + "\n");

        try {
            if (uEntity instanceof StringEntity) {
                builder.append("[entity] " + EntityUtils.toString(uEntity));
            } else if (uEntity != null) {
                builder.append("[entity] " + uEntity.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (String key : mHeaders.keySet()) {
            String value = mHeaders.get(key);
            builder.append("[header] " + key + ":" + value + "\n");
        }
        return builder.toString();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        //
    }

    public static final Creator<ConnectInfo> CREATOR = new Creator<ConnectInfo>() {
        public ConnectInfo createFromParcel(Parcel parcel) {
            return new ConnectInfo(parcel);
        }

        public ConnectInfo[] newArray(int size) {
            return new ConnectInfo[size];
        }
    };
}
