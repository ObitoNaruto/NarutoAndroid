package com.naruto.mobile.applauncher.utils;

import android.content.Intent;
import android.os.Bundle;

import com.naruto.mobile.base.serviceaop.NarutoApplication;
import com.naruto.mobile.base.serviceaop.NarutoApplicationContext;
import com.naruto.mobile.base.serviceaop.app.MicroApplication;
import com.naruto.mobile.base.serviceaop.service.CommonService;
import com.naruto.mobile.base.serviceaop.service.ext.ExternalService;

/**
 */

public class NarutoUtils {

    //不同app间跳转
    public static void startApp(String srcAppId, String objAppId, Bundle bundle) {
        NarutoApplicationContext context = NarutoApplication.getInstance().getNarutoApplicationContext();
        context.startApp(srcAppId, objAppId, bundle);
    }

    public static void startActivity(Intent intent) {
        NarutoApplicationContext context = NarutoApplication.getInstance().getNarutoApplicationContext();
        MicroApplication mApp = context.findTopRunningApp();
        context.startActivity(mApp, intent);
    }

    public static <T extends ExternalService> T getExtServiceByInterface(Class<?> clazz) {
        return NarutoApplication.getInstance().getNarutoApplicationContext()
                .getExtServiceByInterface(clazz.getName());
    }

    public static <T extends CommonService> T findServiceByInterface(Class<?> clazz) {
        return NarutoApplication.getInstance().getNarutoApplicationContext()
                .findServiceByInterface(clazz.getName());
    }

    //同一个app之间利用scheme跳转
//    public static void goScheme(String schemeStr) {
//        if (TextUtils.isEmpty(schemeStr)){
//            return;
//        }
//        MicroApplicationContext context = AlipayApplication.getInstance().getMicroApplicationContext();
//        SchemeService schemeService = context.findServiceByInterface(SchemeService.class.getName());
//        Uri uri = Uri.parse(schemeStr);
//        schemeService.process(uri);
//    }
//
//    public static void goUrl(String url){
//        MicroApplicationContext context = AlipayApplication.getInstance().getMicroApplicationContext();
//        SchemeService schemeService = context.findServiceByInterface(SchemeService.class.getName());
//        String schemeHead = "alipays://platformapi/startapp?"
//                + "appId=20000067&showTitleBar=YES&showToolBar=NO&url=";
//        Uri uri = Uri.parse(schemeHead + Uri.encode(url));
//        schemeService.process(uri);
//    }
//
//    public static int getScreenWidth(){
//        MicroApplicationContext context = AlipayApplication.getInstance().getMicroApplicationContext();
//        WindowManager wm = (WindowManager)context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
//        return wm.getDefaultDisplay().getWidth();
//    }
}
