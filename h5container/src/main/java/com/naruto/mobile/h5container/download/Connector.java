
package com.naruto.mobile.h5container.download;

import android.text.TextUtils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;

import com.naruto.mobile.h5container.download.ConnectInfo.HttpMethod;
import com.naruto.mobile.h5container.util.FileUtil;
import com.naruto.mobile.h5container.util.H5Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.CharArrayBuffer;

public class Connector {
    private static final String TAG = "Connector";

    public static final int IO_BUFFER_SIZE = 16 * 1024;
    public static final String USER_AGENT = "User-Agent";

    private ConnectInfo mConnectInfo;
    private HttpResponse mResponse;
    private DefaultHttpClient mClient;
    private HttpRequestBase mRequest;

    private int mStatusCode;
    private String mContent;

    public Connector() {
    };

    public Connector(ConnectInfo info) {
        this();
        mConnectInfo = info;
    }

    public ConnectInfo getConnectInfo() {
        return mConnectInfo;
    }

    public void setConnectInfo(ConnectInfo info) {
        mConnectInfo = info;
    }

    public boolean connect() {
        if (mConnectInfo == null) {
            return false;
        }

        H5Log.d(TAG, mConnectInfo.toString());

        // instantiate request based on method parameter
        String url = mConnectInfo.getURL();
        if (TextUtils.isEmpty(url)) {
            return false;
        }

        HttpMethod method = mConnectInfo.getMethod();
        if (method == HttpMethod.GET) {
            mRequest = new HttpGet(url);
        } else if (method == HttpMethod.POST) {
            mRequest = new HttpPost(url);
        } else if (method == HttpMethod.PUT) {
            mRequest = new HttpPut(url);
        } else if (method == HttpMethod.DELETE) {
            mRequest = new HttpDelete(url);
        } else {
            return false;
        }

        // fill up request headers
        HashMap<String, String> headers = mConnectInfo.getHeaders();

        for (String key : headers.keySet()) {
            String value = headers.get(key);
            mRequest.addHeader(key, value);
        }

        // set entity
        HttpEntity entity = mConnectInfo.getUploadEntity();
        if (entity instanceof UploadEntity) {
            UploadEntity uEntity = (UploadEntity) entity;
            uEntity.setListener(null);
        }

        if (mRequest instanceof HttpEntityEnclosingRequestBase
                && entity != null) {
            ((HttpEntityEnclosingRequestBase) mRequest).setEntity(entity);
        }

        int timeout = mConnectInfo.getTimeout();
        if (timeout <= 0) {
            return false;
        }

        mClient = HttpClient.getHttpClient(timeout);

        // execute request
        try {
            mResponse = mClient.execute(mRequest);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (mResponse == null) {
                return false;
            }
        }

        mStatusCode = mResponse.getStatusLine().getStatusCode();
        H5Log.d(TAG, "mStatusCode " + mStatusCode);

        // get entity input stream
        InputStream ips = null;
        HttpEntity responseEntity = mResponse.getEntity();
        if (responseEntity == null) {
            return false;
        }

        try {
            ips = responseEntity.getContent();
        } catch (Exception e) {
            // e.printStackTrace();
            return false;
        } finally {
            if (ips == null) {
                return false;
            }
        }

        // check if a download file task.
        DownloadEntity dEntity = mConnectInfo.getDownloadEntity();
        if (method == HttpMethod.GET && dEntity != null) {
            String filePath = dEntity.getFilePath();
            long realSize = FileUtil.size(filePath);

            if (mStatusCode == HttpStatus.SC_OK) {
                dEntity.seek(0);
            } else if (mStatusCode == HttpStatus.SC_PARTIAL_CONTENT) {
                dEntity.seek(realSize);
            } else {
                H5Log.w(TAG, "unhandled status " + mStatusCode);
                return false;
            }

            long contentLen = responseEntity.getContentLength();
            if (contentLen > 0) {
                dEntity.setLength(contentLen + realSize);
            }

            return dEntity.input(ips);
        } else {
            // save server response body as string
            int length = (int) responseEntity.getContentLength();
            if (length < 0) {
                length = IO_BUFFER_SIZE;
            }
            try {
                Reader reader = new InputStreamReader(ips, "utf-8");
                CharArrayBuffer buffer = new CharArrayBuffer(length);
                char[] tmp = new char[IO_BUFFER_SIZE];
                int readSize;
                while ((readSize = reader.read(tmp)) != -1) {
                    buffer.append(tmp, 0, readSize);
                }
                mContent = buffer.toString();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (mContent == null) {
                    return false;
                }
            }

            H5Log.d(TAG, "[response] " + mContent);
            return true;
        }
    }

    public boolean disconnect() {
        if (mRequest == null) {
            H5Log.w(TAG, "invalid request");
            return false;
        }

        mRequest.abort();

        if (mClient == null) {
            H5Log.w(TAG, "invalid client");
            return false;
        }

        ClientConnectionManager manager = mClient.getConnectionManager();
        manager.shutdown();
        return true;
    }

    public int getStatusCode() {
        return mStatusCode;
    }

    public String getContent() {
        return mContent;
    }
}
