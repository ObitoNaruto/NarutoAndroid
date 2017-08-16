package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.api.infos;


import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.config.EnvSwitcher;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.config.ServerAddress;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.util.DjangoConstant;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.http.util.TextUtils;

/**
 * Created by xiangui.fxg on 2015/7/13.
 */
public abstract class BaseApiInfo {
    private static ServerAddress add = null;
    protected ServerAddress.ServerType serverType;
    protected String apiPath;
    protected HttpMethod httpMethod;
    protected String api;
    protected String ip;
    protected String urlApi;
    protected String host;
    protected int port = 80;

    static {
        //设置网络环境
        add = EnvSwitcher.getCurrentEnv().getServerAddress();
    }

    public BaseApiInfo(ServerAddress.ServerType serverType, String apiPath, HttpMethod httpMethod){
        this.serverType = serverType;
        this.apiPath = apiPath;
        this.httpMethod = httpMethod;

        initServerAddress(add);
    }

    /**
     * 设置服务器地址，API根据此处设置的值决定请求的服务器
     * @param serverAddress
     */
    public void initServerAddress(ServerAddress serverAddress) {
        switch (serverType) {
            case UPLOAD:
                port = serverAddress.getUploadServerPort();
                this.host = serverAddress.getUploadServerHost();
                break;
            case DOWNLOAD:
                port = serverAddress.getDownloandServerPort();
                this.host = serverAddress.getDownloadServerHost();
                break;
            case API:
                port = serverAddress.getApiServerPort();
                this.host = serverAddress.getApiServerHost();
                break;
        }

        //this.ip = ApiInfoUtil.getIpInfoByHost(this.host);
        //serverAddr = ApiInfoUtil.getServerAddress(this.ip,port);

        //Log.d("elvis", "setServerAddress host=" + token.host + ";ip=" + ip + ";serverAddr=" + serverAddr);
        //this.api = String.format(DjangoConstant.API_URL_FORMAT_HTTP, serverAddr, this.apiPath);
        //this.urlApi = String.format(DjangoConstant.API_URL_FORMAT_HTTP, ApiInfoUtil.getServerAddress(this.host,port), this.apiPath);
    }

    /**
     * 获取API的URL，如: http://api.django.t.taobao.com/rest/1.0/token<br>
     *
     * @return
     */
    public String getApi() {
        api = String.format(DjangoConstant.API_URL_FORMAT_HTTP, ApiInfoUtil.getServerAddress(getIp(),port),apiPath);
        return api;
    }

    /**
     * 获取API的host，如: http://api.django.t.taobao.com/rest/1.0/token<br>
     *
     * @return
     */
    public String getHost() {
        return host;
    }

    /**
     * 获取API带域名的URL，如: http://api.django.t.taobao.com/rest/1.0/token<br>
     * @return
     */
    public String getUrlApi(){
        if(TextUtils.isEmpty(urlApi)){
            urlApi = String.format(DjangoConstant.API_URL_FORMAT_HTTP, ApiInfoUtil.getServerAddress(host,port), apiPath);
        }

        return urlApi;
    }

    /**
     * 获取域名对应的ip地址
     * @return
     */
    public String getIp(){
        ip = ApiInfoUtil.getIpInfoByHost(host);
        return ip;
    }

    /**
     * 获取API的{@link HttpMethod}
     *
     * @return
     */
    public HttpMethod getHttpMethod() {
        return httpMethod;
    }
}
