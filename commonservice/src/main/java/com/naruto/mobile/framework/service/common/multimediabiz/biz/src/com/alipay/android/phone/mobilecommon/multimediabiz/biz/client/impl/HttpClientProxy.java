package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.impl;

import android.text.TextUtils;
import org.apache.http.*;
import org.apache.http.Header;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;

import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.config.EnvSwitcher;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.util.DjangoConstant;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.util.DjangoUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.util.HttpConstants;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.http.apache.entity.mine.MultipartFormEntity;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.CommonUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Logger;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.ReflectUtils;

/**
 * Created by xiangui.fxg on 2015/7/3.
 */
public class HttpClientProxy implements HttpClient {
    private static final String TAG = "HttpClientProxy";
    private HttpClient mHttpClient;
    private boolean hasShutdown;
    private int mProxyPort = 80;
    private HttpHost mProxy = null;

    public HttpClientProxy(boolean bSpdy){
        mHttpClient = createHttpClient(bSpdy);
    }

    private HttpClient createHttpClient(boolean bSpdy) {
        try {
            if(!HttpConstants.SPDY_CONFIG_SWITCH){
                bSpdy = HttpConstants.SPDY_CONFIG_SWITCH;
            }

            bSpdy = EnvSwitcher.enableSpdyDebug() && bSpdy;

            if(bSpdy){
//                mHttpClient = new OkClientProxy();
//				hasShutdown = false;
            }else{
                mHttpClient = creatHttpClient();
            }
        } catch (Exception e) {
            Logger.E(TAG,e,"create http client factory failed, system exit");
        }

        return mHttpClient;
    }

    @Override
    public HttpParams getParams() {
        return mHttpClient.getParams();
    }

    @Override
    public ClientConnectionManager getConnectionManager() {
        return mHttpClient.getConnectionManager();
    }

    @Override
    public HttpResponse execute(HttpUriRequest httpUriRequest) throws IOException, ClientProtocolException {
        HttpResponse rsp = null;
        String uriHost = "";
        try {
            boolean bSpdy = isOkApacheClient();
            HttpUriRequest req = convIpToHostReq(httpUriRequest,false,bSpdy);
            uriHost = httpUriRequest.getURI().getHost();
            rsp = mHttpClient.execute(req);

            if(rsp != null && CommonUtils.isNeedRetry(rsp.getStatusLine().getStatusCode())){
                //这里返回403错误时，大部分是由于host丢失或变成ip导致；另外502可能是ip和host不对应，因此都做降级处理
                /*如果是spdy则降级为普通的http*/
                if(bSpdy){
                    dgradeToHttp();
                }

                HttpUriRequest req1 = convIpToHostReq(httpUriRequest,true,false);
                if(isRepeatableReq(httpUriRequest)){
                    uriHost = req1.getURI().getHost();
                    Logger.D(TAG, "execute retry uri=" + req1.getURI());
                    rsp = mHttpClient.execute(req1);
                }
            }

        }catch (IOException e){
            Logger.D(TAG,"execute exp " + httpUriRequest.getURI());
            Logger.E(TAG,e,"execute exp " + uriHost);
            boolean bSpdy = isOkApacheClient();
            //只有ip直连和spdy的情况下出现time out时走重试逻辑，且spdy的重试需要降级为域名的http的方式
            if(e instanceof ConnectTimeoutException && (DjangoUtils.isValidIp(uriHost) || bSpdy)){
                //如果是域名的情况就没必要再次重试，直接抛此异常
                if(bSpdy){
                    dgradeToHttp();
                }

                HttpUriRequest req = convIpToHostReq(httpUriRequest,true,false);
                rsp = mHttpClient.execute(req);
            }else{
                appendExceptionMessage(e, uriHost);
                throw e;
            }
        }

        if(rsp != null && rsp.getStatusLine().getStatusCode() != HttpStatus.SC_OK){
            Logger.D(TAG,"execute rsp code="+rsp.getStatusLine().getStatusCode()+";uri="+httpUriRequest.getURI());
        }

        return rsp;
    }
	
    @Override
    public HttpResponse execute(HttpUriRequest httpUriRequest, HttpContext httpContext) throws IOException, ClientProtocolException {
        return mHttpClient.execute(httpUriRequest,httpContext);
    }

    @Override
    public HttpResponse execute(HttpHost httpHost, HttpRequest httpRequest) throws IOException, ClientProtocolException {
        return mHttpClient.execute(httpHost,httpRequest);
    }

    @Override
    public HttpResponse execute(HttpHost httpHost, HttpRequest httpRequest, HttpContext httpContext) throws IOException, ClientProtocolException {
        return mHttpClient.execute(httpHost,httpRequest,httpContext);
    }

    @Override
    public <T> T execute(HttpUriRequest httpUriRequest, ResponseHandler<? extends T> responseHandler) throws IOException, ClientProtocolException {
        return mHttpClient.execute(httpUriRequest,responseHandler);
    }

    @Override
    public <T> T execute(HttpUriRequest httpUriRequest, ResponseHandler<? extends T> responseHandler, HttpContext httpContext) throws IOException, ClientProtocolException {
        return mHttpClient.execute(httpUriRequest,responseHandler,httpContext);
    }

    @Override
    public <T> T execute(HttpHost httpHost, HttpRequest httpRequest, ResponseHandler<? extends T> responseHandler) throws IOException, ClientProtocolException {
        return mHttpClient.execute(httpHost,httpRequest,responseHandler);
    }

    @Override
    public <T> T execute(HttpHost httpHost, HttpRequest httpRequest, ResponseHandler<? extends T> responseHandler, HttpContext httpContext) throws IOException, ClientProtocolException {
        return mHttpClient.execute(httpHost,httpRequest,responseHandler,httpContext);
    }

    /**
     * 设置代理，主要用于wap环境下走http的时候用
     * @param ip
     * @param host
     */
    public void setProxy(String ip,String host,boolean bSpdy){
        if (mHttpClient == null) {
            createHttpClient(bSpdy);
        }

        //走spdy的时候代理由spdy自己内部设置
        if(TextUtils.isEmpty(ip) || TextUtils.isEmpty(host) || ip.equalsIgnoreCase(host)||isOkApacheClient()){
            //Logger.D(TAG, "setProxy mProxy = null");
            mProxy = null;//CommonUtils.getProxy();
        }else{
            mProxy = new HttpHost(ip, mProxyPort);//CommonUtils.getProxy();
        }

        if(mHttpClient != null && mHttpClient.getParams() != null){
            mHttpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, mProxy);
        }
    }

    /**
     * 关闭网络流
     */
    public void shutdown() {
        hasShutdown = true;
        if (mHttpClient != null) {
            if(isOkApacheClient()){
//                ((OkClientProxy)mHttpClient).shutdown();
            }else if(mHttpClient.getConnectionManager() != null){
                mHttpClient.getConnectionManager().shutdown();
                mHttpClient = null;
            }
        }
    }

    /**
     * 当前网络流是否已关闭
     * @return
     */
    public boolean isShutdown() {
        return hasShutdown;
    }

    /**
     * 当前网络环境是否是spdy
     * @return
     */
    private boolean isOkApacheClient(){
        //return EnvSwitcher.enableSpdyDebug() && (mHttpClient instanceof OkClientProxy);
    	return false;
    }

    /**
     * spdy降级为http
     */
    private void dgradeToHttp(){
//        if(EnvSwitcher.enableSpdyDebug() && (mHttpClient instanceof OkClientProxy)){
//            mHttpClient = creatHttpClient();
//        }
    }

    /**
     * 拼接host信息到exp里面
     * @param e
     * @param uriHost
     */
    private void appendExceptionMessage(Throwable e, String uriHost) {
        if (e != null) {
            Field field = ReflectUtils.getField(Throwable.class, "detailMessage");
            ReflectUtils.setField(e, field, e.getMessage() + ",h:" + uriHost);
        }
    }

    /**
     * 从请求头里获取host
     * @param req
     * @return
     */
    private String getHostName(HttpUriRequest req){
        Header[] hds = req.getHeaders("Host");
        Header ct = null;
        String host = null;
        if(hds != null && hds.length > 0){
            for (int i = 0; i < hds.length; i++) {
                if (hds[i].getName().equalsIgnoreCase("Host")) {
                    ct = hds[i];
                    break;
                }
            }

            if(ct != null){
                host = ct.getValue();
            }
        }
        //Log.d(TAG,"getHostName host="+host);
        return host;
    }

    /**
     * 将请求的ip直连地址转换成域名地址
     * @param req  请求
     * @param bRetry  是否是重试请求
     * @param bSpdy  是否走的spdy，目前android都spdy都是基于域名的方式
     * @return
     */
    private HttpUriRequest convIpToHostReq(HttpUriRequest req,boolean bRetry,boolean bSpdy){
        URI uri = req.getURI();
        String ipOrHost = uri.getHost();
        String host = getHostName(req);
        //Log.d(TAG,"convIpToHostReq ipOrHost="+ipOrHost+";host="+host+";bRetry="+bRetry);
        //ip直连这种情况下需要重试
        if(!TextUtils.isEmpty(ipOrHost) && !TextUtils.isEmpty(host) && !ipOrHost.equalsIgnoreCase(host)){
            if(!HttpDnsManager.getInstance().isIpTimeOut(ipOrHost) || bRetry || bSpdy){
                String url =  uri.toString();
                //Log.d(TAG,"getHostName original url="+url);
                if(TextUtils.isEmpty(url)){
                    return req;
                }

                url = url.replace(ipOrHost,host);
                if(req instanceof HttpRequestBase){
                    ((HttpRequestBase)req).setURI(URI.create(url));
                    if(bRetry){
                        HttpDnsManager.getInstance().putValue(ipOrHost,System.currentTimeMillis());
                    }
                    //Log.d(TAG,"getHostName replaced url="+url);
                }
            }
        }
        return req;
    }

    /**
     * 创建HttpCilent
     * @return
     */
    private HttpClient creatHttpClient(){
	    hasShutdown = false;
        HttpParams params = new BasicHttpParams();
        // 设置一些基本参数
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params,
                DjangoConstant.DEFAULT_CHARSET_NAME);
        HttpProtocolParams.setUseExpectContinue(params, true);
        // 超时设置
        /* 从连接池中取连接的超时时间 */
        ConnManagerParams.setTimeout(params, HttpConstants.POOL_TIME_OUT);
        /* 连接超时 */
        HttpConnectionParams.setConnectionTimeout(params, HttpConstants.CONNECTION_TIME_OUT);
        /* 请求超时 */
        HttpConnectionParams.setSoTimeout(params, HttpConstants.SOCKET_TIME_OUT);
        HttpConnectionParams.setSocketBufferSize(params, HttpConstants.SOCKET_BUFF_SIZE);
        return new DefaultHttpClient(params);
    }

    /**
     * 请求是否可重用
     * @param request
     * @return
     */
    private boolean isRepeatableReq(HttpUriRequest request){
        if (request instanceof HttpEntityEnclosingRequest) {
            HttpEntityEnclosingRequest entityRequest = (HttpEntityEnclosingRequest) request;
            HttpEntity entity = entityRequest.getEntity();
            if (entity != null && entity instanceof MultipartFormEntity) {
                MultipartFormEntity mfEntity =  ((MultipartFormEntity) entity);
                return mfEntity.isRepeatableEntity();
            }
        }
        return true;
    }
}
