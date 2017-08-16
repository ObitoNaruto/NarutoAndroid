package com.naruto.mobile.base.log.transport.http.legacy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.naruto.mobile.base.log.logagent.LogUtil;
import com.naruto.mobile.base.log.logging.LogCatLog;
//import com.alipay.mobile.common.logagent.LogUtil;
//import com.alipay.mobile.common.logging.LogCatLog;

public class HttpClient {
    static final String         TAG                   = HttpClient.class.getSimpleName();

    public int                  errorType             = 0;

    public static int           IO_ERROR              = 0;
    public static int           SSL_ERROR             = 1;
    public static int           SOCKET_ERROR          = 2;
    public static int           CONNECT_ERROR         = 3;
    public static int           SOCKET_TIMEOUT_ERROR  = 4;
    public static int           CONNECT_TIMEOUT_ERROR = 5;
    public static int           UNKNOWN_HOST_ERROR    = 6;

    private final static String CHARSET               = "UTF-8";
    private String              mUrl;

    Context                     mContext;

    public HttpClient(Context context) {
        mUrl = null;
        this.mContext = context;
        setDefaultHostnameVerifier();
    }

    public HttpClient(String url, Context context) {
        this.mUrl = url;
        this.mContext = context;
        setDefaultHostnameVerifier();
    }

    public void setUrl(String url) {
        this.mUrl = url;
    }

    public String getUrl() {
        return mUrl;
    }

    public URL getURL() {
        URL url = null;

        try {
            url = new URL(this.mUrl);
        } catch (Exception e) {
            LogCatLog.printStackTraceAndMore(e);
        }

        return url;
    }

    private void setDefaultHostnameVerifier() {
        //
        HostnameVerifier hv = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        HttpsURLConnection.setDefaultHostnameVerifier(hv);
    }

    public void outputRequestInfo(String strReqData) {
        URL url = getURL();

        LogUtil.logOnlyDebuggable(TAG, "Request" + strReqData);
        LogUtil.logOnlyDebuggable(TAG, "Dest url:  " + url.toString());
    }

    public HttpResponse sendSynchronousRequestAsHttpResponse(String strReqData,
                                                             ArrayList<BasicHeader> headers) {
        ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
        pairs.add(new BasicNameValuePair("requestData", strReqData));
        outputRequestInfo(strReqData);

        headers = prepareCommonHeaders(headers);
        return sendSynchronousRequestAsHttpResponse(pairs, headers);
    }

    public RespData sendSynchronousGetRequest(ArrayList<BasicHeader> headers) {
        RespData respData = null;
        try {
            HttpResponse httpResponse = sendSynchronousRequestAsHttpResponse(
                (ArrayList<BasicNameValuePair>) null, headers);
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = httpResponse.getEntity();
                String strResponse = EntityUtils.toString(entity);
                LogUtil.logOnlyDebuggable(TAG, "Response " + strResponse);

                String contentType = entity.getContentType().getValue();
                String charset = BaseHelper.getCharset(contentType);
                contentType = BaseHelper.getContentType(contentType);
                respData = new RespData(strResponse, contentType, charset);
            }
        } catch (Exception e) {
            LogCatLog.printStackTraceAndMore(e);
        }

        return respData;
    }

    private ArrayList<BasicHeader> prepareCommonHeaders(ArrayList<BasicHeader> headers) {
        if (headers == null) {
            headers = new ArrayList<BasicHeader>();
        }
        headers.add(new BasicHeader("Accept-Encoding", "gzip"));
        return headers;
    }

    public static AndroidHttpClient sAndroidHttpClient = null;

    public HttpResponse sendSynchronousRequestAsHttpResponse(ArrayList<BasicNameValuePair> pairs,
                                                             ArrayList<BasicHeader> headers) {
        HttpResponse httpResponse = null;

        URL url = getURL();
        UrlEncodedFormEntity p_entity = null;
        long start = System.currentTimeMillis();

        HttpRequest httpRequest = null;
        HttpHost target = null;

        Exception catchedException = null;
        try {
            if (sAndroidHttpClient == null)
                sAndroidHttpClient = AndroidHttpClient.newInstance("alipay", mContext);

            // configure the proxy.
            HttpParams httpParams = sAndroidHttpClient.getParams();
            // 设置网络超时参数
            //			HttpConnectionParams.setConnectionTimeout(httpParams, 20 * 1000);
            //			HttpConnectionParams.setSoTimeout(httpParams, 30 * 1000);

            HttpHost proxy = getProxy();
            httpParams.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
            String protocol = url.getProtocol();
            int port = 80;
            if (protocol.equalsIgnoreCase("https"))
                port = 443;

            target = new HttpHost(url.getHost(), port, protocol);

            if (pairs != null) {
                httpRequest = new HttpPost(mUrl);
                p_entity = new UrlEncodedFormEntity(pairs, "utf-8");
                ((HttpPost) httpRequest).setEntity(p_entity);
            } else {
                httpRequest = new HttpGet(mUrl);
            }
            httpRequest
                .addHeader("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
            httpRequest
                .addHeader("Accept",
                    "application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
            httpRequest.addHeader("Connection", "Keep-Alive");

            if (headers != null) {
                for (Header header : headers)
                    httpRequest.addHeader(header);
            }

            // Execute the request.
            httpResponse = sAndroidHttpClient.execute(target, httpRequest);
        } catch (UnsupportedEncodingException e) {
            LogCatLog.printStackTraceAndMore(e);
        } catch (SSLPeerUnverifiedException e) {
            errorType = SSL_ERROR;
            catchedException = e;
        } catch (ConnectException e) {
            errorType = CONNECT_ERROR;
            catchedException = e;
        } catch (SocketException e) {
            errorType = SOCKET_ERROR;
            catchedException = e;
        } catch (SocketTimeoutException e) {
            errorType = SOCKET_TIMEOUT_ERROR;
            catchedException = e;
        } catch (ConnectTimeoutException e) {
            errorType = CONNECT_TIMEOUT_ERROR;
            catchedException = e;
        } catch (UnknownHostException e) {
            errorType = UNKNOWN_HOST_ERROR;
            catchedException = e;
        } catch (Exception e) {
            errorType = IO_ERROR;
            try {
                // Execute the request again.
                if (e instanceof NullPointerException)
                    httpResponse = sAndroidHttpClient.execute(target, httpRequest);
                else
                    throw e;
            } catch (final Exception e1) {
                catchedException = e;
            }
        } finally {
            if (catchedException != null) {
                //TODO:
                //                SystemExceptionHandler.getInstance().saveConnInfoToFile(catchedException,
                //                    Constants.MONITORPOINT_CONNECTERR);
            	LogCatLog.printStackTraceAndMore(catchedException);
            }
            // logout the cost.
            long lend = System.currentTimeMillis();
            LogUtil.logOnlyDebuggable("start at: ", String.valueOf(start) + "(ms)");
            LogUtil.logOnlyDebuggable("finished at: ", String.valueOf(lend) + "(ms)");

            float lEalpse = (float) ((lend - start) / 1000.0);
            String Ealpse = String.valueOf(lEalpse);
            LogUtil.logOnlyDebuggable(" cost : ", Ealpse + "(s)");
        }

        return httpResponse;
    }

    public HttpResponse sendGZipSynchronousRequest(String strReqData) {
        HttpResponse httpResponse = null;

        HttpRequest httpRequest = null;
        HttpHost target = null;

        long start = System.currentTimeMillis();
        Exception catchedException = null;

        try {
            URL url = getURL();
            if (sAndroidHttpClient == null)
                sAndroidHttpClient = AndroidHttpClient.newInstance("alipay",mContext);

            // configure the proxy.
            HttpParams httpParams = sAndroidHttpClient.getParams();
            HttpHost proxy = getProxy();
            httpParams.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
            
            if(url == null || "".equals(url))
            	return null;
            			
            String protocol = url.getProtocol();
            
            if(protocol == null || "".equals(protocol))
            	return null;
            int port = 80;
            if (protocol.equalsIgnoreCase("https"))
                port = 443;

            target = new HttpHost(url.getHost(), port, protocol);

            if (strReqData != null) {
                httpRequest = new HttpPost(mUrl);
                byte[] zipData = GZipData(strReqData);

                ByteArrayEntity zipEntity = new ByteArrayEntity(zipData);
                ((HttpPost) httpRequest).setEntity(zipEntity);
            } else {
                httpRequest = new HttpGet(mUrl);
            }
            httpRequest.addHeader("Content-type", "text/xml");

            // Execute the request.
            httpResponse = sAndroidHttpClient.execute(target, httpRequest);
        } catch (SSLPeerUnverifiedException e) {
            errorType = SSL_ERROR;
            catchedException = e;
        } catch (ConnectException e) {
            errorType = CONNECT_ERROR;
            catchedException = e;
        } catch (SocketException e) {
            errorType = SOCKET_ERROR;
            catchedException = e;
        } catch (SocketTimeoutException e) {
            errorType = SOCKET_TIMEOUT_ERROR;
            catchedException = e;
        } catch (ConnectTimeoutException e) {
            errorType = CONNECT_TIMEOUT_ERROR;
            catchedException = e;
        } catch (UnknownHostException e) {
            errorType = UNKNOWN_HOST_ERROR;
            catchedException = e;
        } catch (Exception e) {
            errorType = IO_ERROR;
            try {
                // Execute the request again.
                if (e instanceof NullPointerException)
                    httpResponse = sAndroidHttpClient.execute(target, httpRequest);
                else
                    throw e;
            } catch (Exception e1) {
            	LogCatLog.printStackTraceAndMore(e1);
            }
        } finally {

            if (catchedException != null) {
                //TODO:
//                SystemExceptionHandler.getInstance().saveConnInfoToFile(catchedException,
//                    Constants.MONITORPOINT_CONNECTERR);
            	LogCatLog.printStackTraceAndMore(catchedException);
            }
            // logout the cost.
            long lend = System.currentTimeMillis();
            Log.i(TAG, "start at: " + String.valueOf(start) + "(ms)");
            Log.i(TAG, "finished at: " + String.valueOf(lend) + "(ms)");

            float lEalpse = (float) ((lend - start) / 1000.0);
            String Ealpse = String.valueOf(lEalpse);
            Log.i(TAG, " cost : " + Ealpse + "(s)");
        }

        return httpResponse;

    }

    @SuppressWarnings("deprecation")
    public HttpHost getProxy() {
        HttpHost proxy = null;
        NetworkInfo ni = this.getActiveNetworkInfo();
        if (ni != null && ni.isAvailable() && ni.getType() == ConnectivityManager.TYPE_MOBILE) {
            String proxyHost = android.net.Proxy.getDefaultHost();
            int port = android.net.Proxy.getDefaultPort();
            if (proxyHost != null)
                proxy = new HttpHost(proxyHost, port);
        }

        return proxy;
    }

    public NetworkInfo getActiveNetworkInfo() {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) this.mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
            return connectivityManager.getActiveNetworkInfo();
        } catch (Exception e) {
            LogCatLog.printStackTraceAndMore(e);
            return null;
        }
    }

   

//    private boolean isModifiedSince(String strUrl, String lastModified, Context context) {
//        ArrayList<BasicHeader> headers = new ArrayList<BasicHeader>();
//        headers.add(new BasicHeader("If-Modified-Since", lastModified));
//        ArrayList<BasicNameValuePair> pairs = null;
//        HttpResponse httpResponse = this.sendSynchronousRequestAsHttpResponse(pairs, headers);
//        if (httpResponse != null)
//            return httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_MODIFIED ? false
//                : true;
//        return true;
//    }

    public InputStream inputStreamFromUrl() {
        InputStream inputStream = null;

        try {
            ArrayList<BasicNameValuePair> pairs = null;
            HttpResponse httpResponse = sendSynchronousRequestAsHttpResponse(pairs, null);
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = httpResponse.getEntity();
                inputStream = entity.getContent();
            }
        } catch (Exception e) {
            LogCatLog.printStackTraceAndMore(e);
        }

        return inputStream;
    }

    /*
     * Gzip压缩
     */
    @SuppressWarnings("finally")
    public static byte[] GZipData(String inData) {
        byte[] result = null;

        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        GZIPOutputStream out = null;
        try {

            out = new GZIPOutputStream(bytesOut);

            byte[] postbyte = inData.getBytes(CHARSET);
            out.write(postbyte, 0, postbyte.length);
            out.finish();
            out.flush();
            result = bytesOut.toByteArray();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            LogCatLog.printStackTraceAndMore(e);
        } finally {
            try {
                if (out != null)
                    out.close();
                bytesOut.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                LogCatLog.printStackTraceAndMore(e);
            }
            return result;
        }
    }

    public String getStrFromResponse(HttpEntity httpEntity) {
        InputStream rawIs = getInputStreamFromRespone(httpEntity);
        String response = null;
        if (rawIs != null)
            response = BaseHelper.convertStreamToString(rawIs);
        return response;
    }

    private InputStream getInputStreamFromRespone(HttpEntity httpEntity) {
        InputStream is = null;
        try {
            is = httpEntity.getContent();
            if (httpEntity.getContentEncoding() != null
                && httpEntity.getContentEncoding().getValue() != null
                && httpEntity.getContentEncoding().getValue().contains("gzip")) {
                is = new GZIPInputStream(is);
                Log.i(TAG + "getInputStreamFromRespone", "isGZIP:" + "true");
            } else {
                Log.i(TAG + "getInputStreamFromRespone", "isGZIP:" + "false");
            }
        } catch (IllegalStateException e) {
            LogCatLog.printStackTraceAndMore(e);
        } catch (IOException e) {
            LogCatLog.printStackTraceAndMore(e);
        }
        return is;
    }
    



}