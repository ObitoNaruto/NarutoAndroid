package com.naruto.mobile.base.Router.andRouter.router;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.util.LinkedHashSet;
import java.util.Set;

import com.naruto.mobile.base.Router.andRouter.route.BrowserRoute;
import com.naruto.mobile.base.Router.andRouter.route.IRoute;

import static com.naruto.mobile.base.Router.andRouter.utils.UrlUtils.getScheme;

/**
 * Created by kris on 16/3/17.
 */
public class BrowserRouter extends BaseRouter {
    //支持的scheme的类型集合
    private static final Set<String> SCHEMES_CAN_OPEN = new LinkedHashSet<>();

    static BrowserRouter mBrowserRouter = new BrowserRouter();  //浏览器


    static {
        SCHEMES_CAN_OPEN.add("https");
        SCHEMES_CAN_OPEN.add("http");
    }

    public static BrowserRouter getInstance(){
        return mBrowserRouter;
    }

    protected boolean open(Context context, IRoute route){
        //当权url被拦截器拦截
        if(doOnInterceptor(context, route.getUrl())){
            return true;
        }
        //否则调取系统的h5容器
        Uri uri = Uri.parse(route.getUrl());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        return true;
    }

    @Override
    public boolean open(IRoute route) {
        return open(mBaseContext, route);
    }

    @Override
    public boolean open(String url) {
        open(getRoute(url));
        return true;
    }

    @Override
    public boolean open(Context context, String url) {
        return open(context, getRoute(url));
    }

    @Override
    public BrowserRoute getRoute(String url) {
        return new BrowserRoute.Builder(this)
                .setUrl(url)
                .build();
    }

    /**
     * 判断该当前route能否打开
     * @param route
     * @return
     */
    @Override
    public boolean canOpenTheRoute(IRoute route) {
        return getCanOpenRoute().equals(route.getClass());
    }

    /**
     * 当前url能否打开
     * @param url
     * @return
     */
    @Override
    public boolean canOpenTheUrl(String url) {
        return SCHEMES_CAN_OPEN.contains(getScheme(url));
    }

    @Override
    public Class<? extends IRoute> getCanOpenRoute() {
        return BrowserRoute.class;
    }


    /**
     * 对url做拦截器判断处理
     * @param context
     * @param url
     * @return
     */
    private boolean doOnInterceptor(Context context, String url){
        if(interceptor != null){
            return interceptor.intercept(context, url);
        }
        return false;
    }
}
