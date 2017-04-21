package com.naruto.mobile.base.Router.andRouter;

import android.content.Context;

import java.util.Locale;
import java.util.Queue;

import com.naruto.mobile.base.Router.andRouter.interceptor.Interceptor;
import com.naruto.mobile.base.Router.andRouter.route.IRoute;
import com.naruto.mobile.base.Router.andRouter.router.HistoryItem;
import com.naruto.mobile.base.Router.andRouter.router.IActivityRouteTableInitializer;
import com.naruto.mobile.base.Router.andRouter.router.IRouter;

import timber.log.Timber;

/**
 * Created by kris on 16/3/17.
 * shell to the user
 */
public class Router {


    public static synchronized void addRouter(IRouter router){
        RouterManager.getSingleton().addRouter(router);
    }

    public static synchronized void initBrowserRouter(Context context){
       RouterManager.getSingleton().initBrowserRouter(context);
    }


    public static synchronized void initActivityRouter(Context context){
        RouterManager.getSingleton().initActivityRouter(context);
    }

    /**
     * @See
     * @param context
     * @param scheme
     * @param initializer
     */
    @Deprecated
    public static synchronized void initActivityRouter(Context context, String scheme, IActivityRouteTableInitializer initializer){
        RouterManager.getSingleton().initActivityRouter(context, initializer, scheme);
    }


    public static synchronized void initActivityRouter(Context context, IActivityRouteTableInitializer initializer, String ... scheme){
        RouterManager.getSingleton().initActivityRouter(context, initializer, scheme);
    }

    public static synchronized void initActivityRouter(Context context, String ... scheme){
        RouterManager.getSingleton().initActivityRouter(context, scheme);
    }

    public static boolean open(String url, Object ... params){
        String temp = String.format(Locale.ENGLISH, url, params);
        return RouterManager.getSingleton().open(temp);
    }

    /**
     *
     * @param context
     * @param url activity://second/%s
     * @param params 汤二狗
     * @return
     */
    public static boolean open(Context context, String url, Object ... params){
        String temp = String.format(Locale.ENGLISH, url, params);//activity://second/汤二狗
        return RouterManager.getSingleton().open(context, temp);
    }

    /**
     * AndRouter uses Timber to output logs. Timber needs init, so if you don't use Timber and you want to view logs of AndRouter, you may need to
     * use this method, and set the debug as true
     */
    public static void setDebugMode(boolean debug){
        if(debug) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    /**
     * the route of the url, if there is not router to process the url, return null
     * @param url
     * @return
     */
    public static IRoute getRoute(String url, Object ... params){
        String temp = String.format(Locale.ENGLISH, url, params);
        return RouterManager.getSingleton().getRoute(temp);
    }


    public static boolean openRoute(IRoute route){
        return RouterManager.getSingleton().openRoute(route);
    }

    public static Queue<HistoryItem> getActivityChangedHistories(){
        return RouterManager.getSingleton().getActivityChangedHistories();
    }

    public static void setInterceptor(Interceptor interceptor){
        RouterManager.getSingleton().setInterceptor(interceptor);
    }

}
