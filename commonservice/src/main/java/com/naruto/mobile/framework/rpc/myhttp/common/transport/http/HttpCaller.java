package com.naruto.mobile.framework.rpc.myhttp.common.transport.http;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.naruto.mobile.framework.rpc.myhttp.common.Config;
import com.naruto.mobile.framework.rpc.myhttp.common.RpcException;
import com.naruto.mobile.framework.rpc.myhttp.common.info.DeviceInfo;
import com.naruto.mobile.framework.rpc.myhttp.common.transport.AbstractRpcCaller;
import com.naruto.mobile.framework.rpc.myhttp.transport.Response;
import com.naruto.mobile.framework.rpc.myhttp.transport.Transport;
import com.naruto.mobile.framework.rpc.myhttp.transport.http.HttpException;
import com.naruto.mobile.framework.rpc.myhttp.transport.http.HttpUrlRequest;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;

/**
 * HTTP传输调用器，管理HTTP请求
 * 
 * @hide
 * @author sanping.li@alipay.com
 * @author haigang.gong@alipay.com
 *
 */
public class HttpCaller extends AbstractRpcCaller {
    /**
     * 配置
     */
    private Config mConfig;
    /**
     * @param method 方法
     * @param reqData 请求数据
     */
    public HttpCaller(Config config,Method method, Object reqData) {
        super(method, reqData);
        mConfig = config;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object call() throws RpcException {
        HttpUrlRequest request = new HttpUrlRequest(mConfig.getUrl());
        request.setReqData((ArrayList<BasicNameValuePair>) mReqData);

        addHeader(request);
//        LogCatLog.i("HttpCaller", "threadid = " + Thread.currentThread().getId()+ "; " + request.toString());

//        //advanced http begin--haitong
//        Response response = this.getMyTransport().execute(request);
//        String value = new String(response.getResData());
//        LogCatLog.v("HttpCaller", "rpc response:  " + value);
//        return value;
//        //advanced http end
        try {
            Future<Response> future = getTransport().execute(request);
            Response response = future.get();
            
            if (response == null)
            	throw new RpcException(RpcException.ErrorCode.CLIENT_HANDLE_ERROR, "response is null");
            
            String value = new String(response.getResData());
//            LogCatLog.v("HttpCaller", "threadid = " + Thread.currentThread().getId()+ "; rpc response:  " + value);
            return value;
        } catch (InterruptedException e) {
            throw new RpcException(RpcException.ErrorCode.CLIENT_INTERUPTED_ERROR, e + "");
        } catch (ExecutionException e) {
            Throwable throwable =e.getCause(); 
            if (throwable!=null&&throwable instanceof HttpException) {//是RPC异常
                HttpException httpException = (HttpException) throwable;
                throw new RpcException(transferCode(httpException.getCode()),
                    httpException.getMsg());
            }
            throw new RpcException(RpcException.ErrorCode.CLIENT_HANDLE_ERROR, e + "");
            //是否带cause异常会更好一点？
//            throw new RpcException(RpcException.ErrorCode.CLIENT_HANDLE_ERROR, e);
        }
    }

    /**
     * 异常码转换
     * 
    * @param code 异常
    * @return
    */
    private int transferCode(int code) {
        int ret = code;
        switch (code) {
            case HttpException.NETWORK_CONNECTION_EXCEPTION:
                ret = RpcException.ErrorCode.CLIENT_NETWORK_CONNECTION_ERROR;
                break;
            case HttpException.NETWORK_IO_EXCEPTION:
                ret = RpcException.ErrorCode.CLIENT_NETWORK_ERROR;
                break;
            case HttpException.NETWORK_SCHEDULE_ERROR:
                ret = RpcException.ErrorCode.CLIENT_NETWORK_SCHEDULE_ERROR;
                break;
            case HttpException.NETWORK_SERVER_EXCEPTION:
                ret = RpcException.ErrorCode.CLIENT_NETWORK_SERVER_ERROR;
                break;
            case HttpException.NETWORK_SOCKET_EXCEPTION:
                ret = RpcException.ErrorCode.CLIENT_NETWORK_SOCKET_ERROR;
                break;
            case HttpException.NETWORK_SSL_EXCEPTION:
                ret = RpcException.ErrorCode.CLIENT_NETWORK_SSL_ERROR;
                break;
            case HttpException.NETWORK_UNAVAILABLE:
                ret = RpcException.ErrorCode.CLIENT_NETWORK_UNAVAILABLE_ERROR;
                break;
            default:
                break;
        }
        return ret;
    }

    /**
     * 添加HTTP头
     * 
     * @param request HttpUrlRequest
     */
    private void addHeader(HttpUrlRequest request) {
        Header didHeader = new BasicHeader("did", DeviceInfo.getInstance().getmDid());
        request.addHeader(didHeader);
        Header clientIdHeader = new BasicHeader("clientId", DeviceInfo.getInstance().getClientId());
        request.addHeader(clientIdHeader);
    }

    /**
     * 获取传输对象
     * @return 传输对象
     * @throws RpcException
     */
    private Transport getTransport() throws RpcException {
        if (mConfig.getTransport() == null)
            throw new RpcException(RpcException.ErrorCode.CLIENT_TRANSPORT_UNAVAILABAL_ERROR,
                "Not find this type Transport");
        return mConfig.getTransport();
    }

   /* private CommonTransport getMyTransport() throws RpcException {
      if (mConfig.getMyTransport() == null)
          throw new RpcException(ErrorCode.CLIENT_TRANSPORT_UNAVAILABAL_ERROR,
              "Not find this type MyTransport");
      return mConfig.getMyTransport();
  }*/

}
