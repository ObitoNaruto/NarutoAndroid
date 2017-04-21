package com.naruto.mobile;


import android.app.Activity;
import android.content.Context;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.naruto.mobile.LogReport.CrashHandler.CrashHandler;
import com.naruto.mobile.LogReport.PowerLogReport.libray.LogReport;
import com.naruto.mobile.LogReport.PowerLogReport.libray.save.imp.CrashWriter;
import com.naruto.mobile.LogReport.PowerLogReport.libray.upload.email.EmailReporter;
import com.naruto.mobile.LogReport.PowerLogReport.libray.upload.http.HttpReporter;
import com.naruto.mobile.Router.AndRouter.AndRouterSecondActivity;
import com.naruto.mobile.base.Router.andRouter.Router;
import com.naruto.mobile.base.Router.andRouter.interceptor.Interceptor;
import com.naruto.mobile.base.Router.andRouter.router.IActivityRouteTableInitializer;
import com.naruto.mobile.base.serviceaop.NarutoApplication;
import com.naruto.mobile.log.KLog.KLog;

public class FuncComponentApplication extends NarutoApplication{
    @Override
    public void onCreate() {
        super.onCreate();
        //--------------------------KLog---------------------------
        //在Application初始化的时候就对KLog进行初始化，因为KLog的初始化过程非常的简单，因此不会影响App的启动速度
//        KLog.init(BuildConfig.LOG_DEBUG);
        KLog.init(BuildConfig.LOG_DEBUG, "Kai");//传入参数，是否打印日志标记和全局TAG

        //对于全局打印开关，推荐使用Gradle变量进行控制，这样当发布release版本的时候，就会自动的关闭Log的输出，防止信息的泄露和影响效率。

        //------------------------CrashHandler---------------------------
        //在这里为应用设置异常处理程序，然后我们的程序才能捕获未处理的异常
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);

        //--------------------------LogReport test-------------------------------------
        initCrashReport();

        //-----------------------init andRouter------------------
        initRouter();
    }

    private void initCrashReport() {
        LogReport.getInstance()
                .setCacheSize(30 * 1024/* * 1024*/)//支持设置缓存大小，超出后清空
                .setLogDir(getApplicationContext(), "sdcard/" + this.getString(this.getApplicationInfo().labelRes) + "/")//定义路径为：sdcard/[app name]/
                .setWifiOnly(true)//设置只在Wifi状态下上传，设置为false为Wifi和移动网络都上传
                .setLogSaver(new CrashWriter(getApplicationContext()))//支持自定义保存崩溃信息的样式
//                .setEncryption(new AESEncode()) //支持日志到AES加密或者DES加密，默认不开启
                .init(getApplicationContext());
        initEmailReporter();
//        initHttpReporter();
    }

    /**
     * 使用EMAIL发送日志
     */
    private void initEmailReporter() {
        EmailReporter email = new EmailReporter(this);
        email.setReceiver("15833227282@163.com");//收件人
        email.setSender("15833227282@163.com");//发送人邮箱
        email.setSendPassword("xuxm123456");//授权码，请使用授权码登录第三方邮件客户端
        email.setSMTPHost("smtp.163.com");//SMTP地址
        email.setPort("465");//SMTP 端口
        LogReport.getInstance().setUploadType(email);
    }

    /**
     * 使用HTTP发送日志
     */
    private void initHttpReporter() {
        HttpReporter http = new HttpReporter(this);
        http.setUrl("http://crashreport.jd-app.com/your_receiver");//发送请求的地址
        http.setFileParam("fileName");//文件的参数名
        http.setToParam("to");//收件人参数名
        http.setTo("你的接收邮箱");//收件人
        http.setTitleParam("subject");//标题
        http.setBodyParam("message");//内容
        LogReport.getInstance().setUploadType(http);
    }

    private static final HashMap<String, String> INTERCEPTOR_BLACK_LIST_SET = new LinkedHashMap<>();

    static {
        INTERCEPTOR_BLACK_LIST_SET.put("http://www.souhu.com", "activity://error");
        INTERCEPTOR_BLACK_LIST_SET.put("activity://intercepted", "activity://error");
    }

    private void initRouter(){
        Router.initActivityRouter(getApplicationContext(), new IActivityRouteTableInitializer() {
            //router就是ActivityRouter中的mRouteTable
            @Override
            public void initRouterTable(Map<String, Class<? extends Activity>> router) {
                router.put("activity://second/:{name}", AndRouterSecondActivity.class);
            }
        }, "activity", "activity2");
        // Router.initActivityRouter(this);
        Router.initBrowserRouter(getApplicationContext());
        // to output logs of AndRouter
        Router.setDebugMode(true);
        //设置拦截器
        Router.setInterceptor(new Interceptor() {
            @Override
            public boolean intercept(Context context, String url) {
                if(INTERCEPTOR_BLACK_LIST_SET.keySet().contains(url)){
                    Router.open(context, INTERCEPTOR_BLACK_LIST_SET.get(url));
                    return true;
                }
                return false;
            }
        });
    }
}
